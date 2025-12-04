package it.gov.pagopa.atmlayerreportingservice.service.model.service.impl;

import it.gov.digitpa.schemas._2011.pagamenti.CtDatiSingoliPagamenti;
import it.gov.digitpa.schemas._2011.pagamenti.CtFlussoRiversamento;
import it.gov.digitpa.schemas._2011.pagamenti.CtIdentificativoUnivoco;
import it.gov.digitpa.schemas._2011.pagamenti.CtIdentificativoUnivocoPersonaG;
import it.gov.digitpa.schemas._2011.pagamenti.CtIstitutoMittente;
import it.gov.digitpa.schemas._2011.pagamenti.CtIstitutoRicevente;
import it.gov.digitpa.schemas._2011.pagamenti.StTipoIdentificativoUnivoco;
import it.gov.digitpa.schemas._2011.pagamenti.StTipoIdentificativoUnivocoPersG;
import it.gov.pagopa.atmlayerreportingservice.service.model.entity.CbillAbiFederazione;
import it.gov.pagopa.atmlayerreportingservice.service.model.entity.PagopaTransferList;
import it.gov.pagopa.atmlayerreportingservice.service.model.service.CbillAbiFederazioneService;
import it.gov.pagopa.atmlayerreportingservice.service.model.service.PagopaReconciliationService;
import it.gov.pagopa.atmlayerreportingservice.service.model.service.PagopaTransactionsService;
import it.gov.pagopa.atmlayerreportingservice.service.model.service.PagopaTransferListService;
import io.quarkus.scheduler.Scheduled;
import io.smallrye.mutiny.Uni;
import io.smallrye.mutiny.infrastructure.Infrastructure;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.xml.bind.JAXBContext;
import jakarta.xml.bind.JAXBElement;
import jakarta.xml.bind.JAXBException;
import jakarta.xml.bind.Marshaller;
import java.io.StringWriter;
import java.math.BigDecimal;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.nio.charset.StandardCharsets;
import java.time.Duration;
import java.time.Instant;
import java.time.LocalDate;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Base64;
import java.util.Comparator;
import java.util.GregorianCalendar;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Set;
import java.util.stream.Collectors;
import javax.xml.datatype.DatatypeConfigurationException;
import javax.xml.datatype.DatatypeFactory;
import javax.xml.datatype.XMLGregorianCalendar;
import javax.xml.namespace.QName;
import org.eclipse.microprofile.config.inject.ConfigProperty;

@ApplicationScoped
public class PagopaReconciliationServiceImpl implements PagopaReconciliationService {
    private static final DateTimeFormatter FLOW_DATE_FORMAT = DateTimeFormatter.ofPattern("yyyy-MM-dd");
    private static final DateTimeFormatter KEY_DATE_FORMAT = DateTimeFormatter.BASIC_ISO_DATE;

    private final PagopaTransferListService transferListService;
    private final PagopaTransactionsService transactionsService;
    private final CbillAbiFederazioneService cbillAbiFederazioneService;
    private final DatatypeFactory datatypeFactory;

    @ConfigProperty(name = "pagopa.rendicontazione.url", defaultValue = "")
    String pagoPaUrl;

    @ConfigProperty(name = "pagopa.rendicontazione.password", defaultValue = "")
    String pagoPaPassword;

    @ConfigProperty(name = "pagopa.rendicontazione.subscription-key", defaultValue = "")
    String pagoPaSubscriptionKey;

    @ConfigProperty(name = "pagopa.rendicontazione.connection-timeout", defaultValue = "1800000")
    long pagoPaConnectionTimeout;

    @ConfigProperty(name = "pagopa.rendicontazione.read-timeout", defaultValue = "1800000")
    long pagoPaReadTimeout;

    public PagopaReconciliationServiceImpl(PagopaTransferListService transferListService, PagopaTransactionsService transactionsService, CbillAbiFederazioneService cbillAbiFederazioneService) {
        this.transferListService = transferListService;
        this.transactionsService = transactionsService;
        this.cbillAbiFederazioneService = cbillAbiFederazioneService;
        this.datatypeFactory = createDatatypeFactory();
    }

    private DatatypeFactory createDatatypeFactory() {
        try {
            return DatatypeFactory.newInstance();
        } catch (DatatypeConfigurationException ex) {
            throw new IllegalStateException(ex);
        }
    }

    @Override
    @Scheduled(cron = "{pagopa.rendicontazione.cron}")
    public Uni<Void> schedulePagoPaReconciliation() {
        LocalDate toDate = LocalDate.now();
        return transactionsService.findAll()
                .onItem().transform(transactions -> {
                    if (transactions == null) {
                        return Set.<String>of();
                    }
                    return transactions.stream().map(tx -> tx.senderBank).filter(Objects::nonNull).collect(Collectors.toSet());
                })
                .flatMap(banks -> {
                    if (banks.isEmpty()) {
                        return Uni.createFrom().voidItem();
                    }
                    List<Uni<Void>> tasks = banks.stream().map(bank -> processBank(bank, toDate)).toList();
                    return Uni.combine().all().unis(tasks).combinedWith(x -> null).replaceWithVoid();
                });
    }

    private Uni<Void> processBank(String senderBank, LocalDate toDate) {
        return transferListService.findPayedNotReportedToPagoPAForBank(toDate, senderBank)
                .flatMap(transfers -> {
                    if (transfers == null || transfers.isEmpty()) {
                        return Uni.createFrom().voidItem();
                    }
                    transfers.sort(Comparator.comparing((PagopaTransferList t) -> t.paIban)
                            .thenComparing(t -> t.transferCro, Comparator.nullsFirst(String::compareTo))
                            .thenComparing(t -> t.flowId, Comparator.nullsFirst(String::compareTo)));
                    return cbillAbiFederazioneService.getPspConfiguration(senderBank)
                            .flatMap(config -> {
                                if (config == null) {
                                    return Uni.createFrom().failure(new IllegalArgumentException("PSP configuration not found for ABI " + senderBank));
                                }
                                Map<String, FlowAggregation> aggregated = aggregateFlows(config, transfers);
                                if (aggregated.isEmpty()) {
                                    return Uni.createFrom().voidItem();
                                }
                                List<Uni<Void>> flowTasks = aggregated.values().stream().map(agg -> sendAndMark(config, agg)).toList();
                                return Uni.combine().all().unis(flowTasks).combinedWith(x -> null).replaceWithVoid();
                            });
                });
    }

    private Map<String, FlowAggregation> aggregateFlows(CbillAbiFederazione config, List<PagopaTransferList> transfers) {
        Map<String, FlowAggregation> flows = new HashMap<>();
        for (PagopaTransferList transfer : transfers) {
            if (transfer == null) {
                continue;
            }
            String key = buildAggregationKey(transfer);
            FlowAggregation aggregation = flows.get(key);
            if (aggregation == null) {
                CtFlussoRiversamento flow = createFlow(config, transfer);
                List<PagopaTransferList> entries = new ArrayList<>();
                entries.add(transfer);
                flows.put(key, new FlowAggregation(flow, entries));
            } else {
                updateFlow(aggregation.flow, transfer);
                aggregation.transfers.add(transfer);
            }
        }
        return flows;
    }

    private String buildAggregationKey(PagopaTransferList transfer) {
        LocalDate executionDate = executionDate(transfer);
        String flowId = safeString(transfer.flowId);
        String cro = safeString(transfer.transferCro);
        String iban = safeString(transfer.paIban);
        return iban + cro + flowId + KEY_DATE_FORMAT.format(executionDate);
    }

    private CtFlussoRiversamento createFlow(CbillAbiFederazione config, PagopaTransferList transfer) {
        if (transfer.pagopaTransaction == null) {
            throw new IllegalArgumentException("Missing transaction for transfer " + transfer.id);
        }
        CtFlussoRiversamento flow = new CtFlussoRiversamento();
        LocalDate execDate = executionDate(transfer);
        flow.setVersioneOggetto("1.1");
        String flowIdentifier = FLOW_DATE_FORMAT.format(execDate) + safeString(config.pagopaId) + "-" + safeString(transfer.flowId);
        flow.setIdentificativoFlusso(flowIdentifier);
        flow.setDataOraFlusso(toXmlDateTime(Instant.now()));
        flow.setIdentificativoUnivocoRegolamento(transfer.transferCro);
        flow.setDataRegolamento(toXmlDate(execDate));
        flow.setIstitutoMittente(buildMittente(config.pagopaId));
        flow.setIstitutoRicevente(buildRicevente(transfer.paFiscalCode));
        flow.setNumeroTotalePagamenti(BigDecimal.ONE);
        flow.setImportoTotalePagamenti(safeAmount(transfer.transferAmount));
        flow.getDatiSingoliPagamenti().add(buildPayment(transfer));
        return flow;
    }

    private void updateFlow(CtFlussoRiversamento flow, PagopaTransferList transfer) {
        BigDecimal totalPayments = flow.getNumeroTotalePagamenti() == null ? BigDecimal.ZERO : flow.getNumeroTotalePagamenti();
        flow.setNumeroTotalePagamenti(totalPayments.add(BigDecimal.ONE));
        BigDecimal totalAmount = flow.getImportoTotalePagamenti() == null ? BigDecimal.ZERO : flow.getImportoTotalePagamenti();
        flow.setImportoTotalePagamenti(totalAmount.add(safeAmount(transfer.transferAmount)));
        flow.getDatiSingoliPagamenti().add(buildPayment(transfer));
    }

    private CtIstitutoMittente buildMittente(String pagopaId) {
        CtIstitutoMittente mittente = new CtIstitutoMittente();
        CtIdentificativoUnivoco identificativo = new CtIdentificativoUnivoco();
        identificativo.setCodiceIdentificativoUnivoco(pagopaId);
        identificativo.setTipoIdentificativoUnivoco(StTipoIdentificativoUnivoco.B);
        mittente.setIdentificativoUnivocoMittente(identificativo);
        return mittente;
    }

    private CtIstitutoRicevente buildRicevente(String paFiscalCode) {
        CtIstitutoRicevente ricevente = new CtIstitutoRicevente();
        CtIdentificativoUnivocoPersonaG identificativo = new CtIdentificativoUnivocoPersonaG();
        identificativo.setCodiceIdentificativoUnivoco(paFiscalCode);
        identificativo.setTipoIdentificativoUnivoco(StTipoIdentificativoUnivocoPersG.G);
        ricevente.setIdentificativoUnivocoRicevente(identificativo);
        return ricevente;
    }

    private CtDatiSingoliPagamenti buildPayment(PagopaTransferList transfer) {
        if (transfer.pagopaTransaction == null) {
            throw new IllegalArgumentException("Missing transaction for transfer " + transfer.id);
        }
        CtDatiSingoliPagamenti payment = new CtDatiSingoliPagamenti();
        payment.setIdentificativoUnivocoVersamento(transfer.pagopaTransaction.noticeNumber);
        payment.setIdentificativoUnivocoRiscossione(transfer.pagopaTransaction.payToken);
        payment.setIndiceDatiSingoloPagamento(transfer.transferId);
        payment.setSingoloImportoPagato(safeAmount(transfer.transferAmount));
        payment.setCodiceEsitoSingoloPagamento(transfer.pagopaTransaction.outcomeCode);
        Instant payDateInstant = transfer.pagopaTransaction.payDate;
        LocalDate payDate = payDateInstant == null ? executionDate(transfer) : LocalDate.ofInstant(payDateInstant, ZoneId.systemDefault());
        payment.setDataEsitoSingoloPagamento(toXmlDate(payDate));
        return payment;
    }

    private Uni<Void> sendAndMark(CbillAbiFederazione config, FlowAggregation aggregation) {
        return sendFlussoRiconciliazione(config, aggregation.flow)
                .flatMap(success -> {
                    if (!Boolean.TRUE.equals(success)) {
                        return Uni.createFrom().voidItem();
                    }
                    List<Uni<Void>> updates = aggregation.transfers.stream()
                            .map(transfer -> transferListService.markAsReported(transfer.id))
                            .toList();
                    if (updates.isEmpty()) {
                        return Uni.createFrom().voidItem();
                    }
                    return Uni.combine().all().unis(updates).combinedWith(x -> null).replaceWithVoid();
                });
    }

    private Uni<Boolean> sendFlussoRiconciliazione(CbillAbiFederazione config, CtFlussoRiversamento flow) {
        return Uni.createFrom().item(() -> {
            if (pagoPaUrl == null || pagoPaUrl.isBlank()) {
                return Boolean.FALSE;
            }
            String xml = marshalFlow(flow);
            String encodedXml = Base64.getEncoder().encodeToString(xml.getBytes(StandardCharsets.UTF_8));
            HttpRequest request = HttpRequest.newBuilder()
                    .uri(URI.create(pagoPaUrl))
                    .timeout(Duration.ofMillis(pagoPaReadTimeout))
                    .header("Content-Type", "text/xml; charset=UTF-8")
                    .header("Ocp-Apim-Subscription-Key", pagoPaSubscriptionKey == null ? "" : pagoPaSubscriptionKey)
                    .POST(HttpRequest.BodyPublishers.ofString(buildSoapEnvelope(config, flow, encodedXml)))
                    .build();
            try {
                HttpResponse<String> response = httpClient().send(request, HttpResponse.BodyHandlers.ofString());
                if (response.statusCode() < 200 || response.statusCode() >= 300) {
                    return Boolean.FALSE;
                }
                String body = response.body();
                if (body == null) {
                    return Boolean.FALSE;
                }
                return body.contains("OK");
            } catch (Exception ex) {
                return Boolean.FALSE;
            }
        }).runSubscriptionOn(Infrastructure.getDefaultWorkerPool());
    }

    private String marshalFlow(CtFlussoRiversamento flow) {
        try {
            JAXBContext context = JAXBContext.newInstance(CtFlussoRiversamento.class);
            Marshaller marshaller = context.createMarshaller();
            marshaller.setProperty(Marshaller.JAXB_FORMATTED_OUTPUT, Boolean.FALSE);
            JAXBElement<CtFlussoRiversamento> element = new JAXBElement<>(new QName("http://www.digitpa.gov.it/schemas/2011/Pagamenti/", "FlussoRiversamento"), CtFlussoRiversamento.class, flow);
            StringWriter writer = new StringWriter();
            marshaller.marshal(element, writer);
            return writer.toString();
        } catch (JAXBException ex) {
            throw new IllegalStateException(ex);
        }
    }

    private String buildSoapEnvelope(CbillAbiFederazione config, CtFlussoRiversamento flow, String encodedXml) {
        String dataOraFlusso = flow.getDataOraFlusso() == null ? "" : flow.getDataOraFlusso().toXMLFormat();
        String identificativoDominio = flow.getIstitutoRicevente() != null && flow.getIstitutoRicevente().getIdentificativoUnivocoRicevente() != null ? safeString(flow.getIstitutoRicevente().getIdentificativoUnivocoRicevente().getCodiceIdentificativoUnivoco()) : "";
        String identificativoPSP = safeString(config.pagopaId);
        String identificativoIntermediarioPSP = safeString(config.pspFiscalCode);
        String identificativoCanale = safeString(config.pspFiscalCode) + safeString(config.pspChannel);
        return "<soapenv:Envelope xmlns:soapenv=\"http://schemas.xmlsoap.org/soap/envelope/\" xmlns:ws=\"http://ws.pagamenti.telematici.gov/\">" +
                "<soapenv:Header/>" +
                "<soapenv:Body>" +
                "<ws:nodoInviaFlussoRendicontazione>" +
                "<identificativoPSP>" + identificativoPSP + "</identificativoPSP>" +
                "<identificativoIntermediarioPSP>" + identificativoIntermediarioPSP + "</identificativoIntermediarioPSP>" +
                "<identificativoCanale>" + identificativoCanale + "</identificativoCanale>" +
                "<password>" + safeString(pagoPaPassword) + "</password>" +
                "<identificativoDominio>" + identificativoDominio + "</identificativoDominio>" +
                "<identificativoFlusso>" + safeString(flow.getIdentificativoFlusso()) + "</identificativoFlusso>" +
                "<dataOraFlusso>" + dataOraFlusso + "</dataOraFlusso>" +
                "<xmlRendicontazione>" + encodedXml + "</xmlRendicontazione>" +
                "</ws:nodoInviaFlussoRendicontazione>" +
                "</soapenv:Body>" +
                "</soapenv:Envelope>";
    }

    private HttpClient httpClient() {
        return HttpClient.newBuilder().connectTimeout(Duration.ofMillis(pagoPaConnectionTimeout)).build();
    }

    private XMLGregorianCalendar toXmlDate(LocalDate date) {
        GregorianCalendar calendar = GregorianCalendar.from(date.atStartOfDay(ZoneId.systemDefault()));
        return datatypeFactory.newXMLGregorianCalendar(calendar);
    }

    private XMLGregorianCalendar toXmlDateTime(Instant instant) {
        GregorianCalendar calendar = GregorianCalendar.from(instant.atZone(ZoneId.systemDefault()));
        return datatypeFactory.newXMLGregorianCalendar(calendar);
    }

    private LocalDate executionDate(PagopaTransferList transfer) {
        return transfer.transferExecutionDt == null ? LocalDate.now() : transfer.transferExecutionDt;
    }

    private String safeString(String value) {
        return value == null ? "" : value;
    }

    private BigDecimal safeAmount(BigDecimal amount) {
        return amount == null ? BigDecimal.ZERO : amount;
    }

    private static final class FlowAggregation {
        final CtFlussoRiversamento flow;
        final List<PagopaTransferList> transfers;

        FlowAggregation(CtFlussoRiversamento flow, List<PagopaTransferList> transfers) {
            this.flow = flow;
            this.transfers = transfers;
        }
    }
}
