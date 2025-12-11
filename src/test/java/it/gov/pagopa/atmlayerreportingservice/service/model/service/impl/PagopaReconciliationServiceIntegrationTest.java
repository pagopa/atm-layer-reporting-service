package it.gov.pagopa.atmlayerreportingservice.service.model.service.impl;

import com.sun.net.httpserver.HttpServer;
import io.smallrye.mutiny.Uni;
import io.smallrye.mutiny.helpers.test.UniAssertSubscriber;
import it.gov.digitpa.schemas._2011.pagamenti.CtDatiSingoliPagamenti;
import it.gov.digitpa.schemas._2011.pagamenti.CtFlussoRiversamento;
import it.gov.digitpa.schemas._2011.pagamenti.CtIdentificativoUnivoco;
import it.gov.digitpa.schemas._2011.pagamenti.CtIdentificativoUnivocoPersonaG;
import it.gov.digitpa.schemas._2011.pagamenti.CtIstitutoMittente;
import it.gov.digitpa.schemas._2011.pagamenti.CtIstitutoRicevente;
import it.gov.digitpa.schemas._2011.pagamenti.StTipoIdentificativoUnivoco;
import it.gov.digitpa.schemas._2011.pagamenti.StTipoIdentificativoUnivocoPersG;
import it.gov.pagopa.atmlayerreportingservice.service.model.entity.CbillAbiFederazione;
import it.gov.pagopa.atmlayerreportingservice.service.model.service.CbillAbiFederazioneService;
import it.gov.pagopa.atmlayerreportingservice.service.model.service.PagopaTransactionsService;
import it.gov.pagopa.atmlayerreportingservice.service.model.service.PagopaTransferListService;
import java.io.ByteArrayOutputStream;
import java.io.InputStream;
import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.math.BigDecimal;
import java.net.InetSocketAddress;
import java.nio.charset.StandardCharsets;
import java.time.Instant;
import java.time.LocalDate;
import java.time.ZoneId;
import java.util.GregorianCalendar;
import javax.xml.datatype.DatatypeFactory;
import javax.xml.datatype.XMLGregorianCalendar;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.condition.EnabledIfEnvironmentVariable;
import org.mockito.Mockito;

/**
 * Test di integrazione per sendFlussoRiconciliazione.
 * Verifica:
 * 1. Comportamento con mock server locale (OK/KO)
 * 2. Comportamento con endpoint reale UAT (se PAGOPA_TEST_ENABLED=true)
 *
 * Specifiche SOAP: vedere docs/specifiche-soap.md
 * Endpoint UAT: https://api.uat.platform.pagopa.it/nodo-auth/node-for-psp/v1
 */
class PagopaReconciliationServiceIntegrationTest {

    private HttpServer server;

    @AfterEach
    void tearDown() {
        if (server != null) {
            server.stop(0);
            server = null;
        }
    }

    /**
     * Test con mock server locale.
     * Verifica che il servizio riconosca una risposta SOAP contenente "OK".
     */
    @Test
    void sendFlussoRiconciliazione_withMockServer_shouldReturnTrue_whenResponseContainsOK() throws Exception {
        // Avvia mock server locale che simula nodoInviaFlussoRendicontazione
        server = HttpServer.create(new InetSocketAddress(0), 0);
        final String expectedSubscription = "test-subscription-key";

        server.createContext("/rend", exchange -> {
            try (InputStream is = exchange.getRequestBody();
                 ByteArrayOutputStream baos = new ByteArrayOutputStream()) {

                byte[] buffer = new byte[4096];
                int r;
                while ((r = is.read(buffer)) != -1) {
                    baos.write(buffer, 0, r);
                }
                String requestBody = baos.toString(StandardCharsets.UTF_8);

                // Validazioni della richiesta
                Assertions.assertTrue(
                    requestBody.contains("nodoInviaFlussoRendicontazione"),
                    "Richiesta SOAP non contiene nodoInviaFlussoRendicontazione"
                );
                Assertions.assertTrue(
                    requestBody.contains("xmlRendicontazione"),
                    "Richiesta SOAP non contiene xmlRendicontazione (base64)"
                );

                String sub = exchange.getRequestHeaders().getFirst("Ocp-Apim-Subscription-Key");
                if (sub == null || !sub.equals(expectedSubscription)) {
                    byte[] resp = "SUBSCRIPTION KEY MISMATCH".getBytes(StandardCharsets.UTF_8);
                    exchange.sendResponseHeaders(400, resp.length);
                    exchange.getResponseBody().write(resp);
                    return;
                }

                // Risposta SOAP OK (conforme specifiche)
                String response = "<?xml version=\"1.0\" encoding=\"UTF-8\"?>" +
                    "<soapenv:Envelope xmlns:soapenv=\"http://schemas.xmlsoap.org/soap/envelope/\" xmlns:ppt=\"http://ws.pagamenti.telematici.gov/\">" +
                    "<soapenv:Body>" +
                    "<ppt:nodoInviaFlussoRendicontazioneRisposta>" +
                    "<esito>OK</esito>" +
                    "</ppt:nodoInviaFlussoRendicontazioneRisposta>" +
                    "</soapenv:Body>" +
                    "</soapenv:Envelope>";

                byte[] resp = response.getBytes(StandardCharsets.UTF_8);
                exchange.getResponseHeaders().add("Content-Type", "text/xml; charset=UTF-8");
                exchange.sendResponseHeaders(200, resp.length);
                exchange.getResponseBody().write(resp);
            }
        });
        server.start();

        PagopaTransferListService transferListService = Mockito.mock(PagopaTransferListService.class);
        PagopaTransactionsService transactionsService = Mockito.mock(PagopaTransactionsService.class);
        CbillAbiFederazioneService cbillAbiFederazioneService = Mockito.mock(CbillAbiFederazioneService.class);
        PagopaReconciliationServiceImpl service = new PagopaReconciliationServiceImpl(
            transferListService, transactionsService, cbillAbiFederazioneService
        );

        setField(service, "pagoPaSubscriptionKey", expectedSubscription);
        setField(service, "pagoPaConnectionTimeout", 5000L);
        setField(service, "pagoPaReadTimeout", 5000L);

        String url = "http://localhost:" + server.getAddress().getPort() + "/rend";
        setField(service, "pagoPaUrl", url);

        // Costruire un flusso di rendicontazione minimo valido
        CtFlussoRiversamento flow = buildMinimalValidFlow();

        CbillAbiFederazione config = new CbillAbiFederazione();
        config.pagopaId = "TESTPSP001";
        config.pspFiscalCode = "12345678901";
        config.pspChannel = "01";

        Method method = PagopaReconciliationServiceImpl.class.getDeclaredMethod(
            "sendFlussoRiconciliazione", CbillAbiFederazione.class, CtFlussoRiversamento.class
        );
        method.setAccessible(true);
        @SuppressWarnings("unchecked")
        Uni<Boolean> uni = (Uni<Boolean>) method.invoke(service, config, flow);

        UniAssertSubscriber<Boolean> subscriber = uni.subscribe().withSubscriber(UniAssertSubscriber.create());
        subscriber.awaitItem().assertCompleted().assertItem(Boolean.TRUE);
    }

    /**
     * Test con mock server locale che ritorna KO.
     * Verifica che il servizio riconosca una risposta SOAP contenente "KO".
     */
    @Test
    void sendFlussoRiconciliazione_withMockServer_shouldReturnFalse_whenResponseContainsKO() throws Exception {
        server = HttpServer.create(new InetSocketAddress(0), 0);

        server.createContext("/rend", exchange -> {
            try (InputStream is = exchange.getRequestBody();
                 ByteArrayOutputStream baos = new ByteArrayOutputStream()) {

                byte[] buffer = new byte[4096];
                int r;
                while ((r = is.read(buffer)) != -1) {
                    baos.write(buffer, 0, r);
                }

                String response = "<?xml version=\"1.0\" encoding=\"UTF-8\"?>" +
                    "<soapenv:Envelope xmlns:soapenv=\"http://schemas.xmlsoap.org/soap/envelope/\" xmlns:ppt=\"http://ws.pagamenti.telematici.gov/\">" +
                    "<soapenv:Body>" +
                    "<ppt:nodoInviaFlussoRendicontazioneRisposta>" +
                    "<fault>" +
                    "<faultCode>PPT_SEMANTICA</faultCode>" +
                    "<faultString>Errore semantico.</faultString>" +
                    "</fault>" +
                    "<esito>KO</esito>" +
                    "</ppt:nodoInviaFlussoRendicontazioneRisposta>" +
                    "</soapenv:Body>" +
                    "</soapenv:Envelope>";

                byte[] resp = response.getBytes(StandardCharsets.UTF_8);
                exchange.getResponseHeaders().add("Content-Type", "text/xml; charset=UTF-8");
                exchange.sendResponseHeaders(200, resp.length);
                exchange.getResponseBody().write(resp);
            }
        });
        server.start();

        PagopaTransferListService transferListService = Mockito.mock(PagopaTransferListService.class);
        PagopaTransactionsService transactionsService = Mockito.mock(PagopaTransactionsService.class);
        CbillAbiFederazioneService cbillAbiFederazioneService = Mockito.mock(CbillAbiFederazioneService.class);
        PagopaReconciliationServiceImpl service = new PagopaReconciliationServiceImpl(
            transferListService, transactionsService, cbillAbiFederazioneService
        );

        setField(service, "pagoPaSubscriptionKey", "test-sub");
        setField(service, "pagoPaConnectionTimeout", 5000L);
        setField(service, "pagoPaReadTimeout", 5000L);

        String url = "http://localhost:" + server.getAddress().getPort() + "/rend";
        setField(service, "pagoPaUrl", url);

        CtFlussoRiversamento flow = buildMinimalValidFlow();

        CbillAbiFederazione config = new CbillAbiFederazione();
        config.pagopaId = "TESTPSP001";
        config.pspFiscalCode = "12345678901";
        config.pspChannel = "01";

        Method method = PagopaReconciliationServiceImpl.class.getDeclaredMethod(
            "sendFlussoRiconciliazione", CbillAbiFederazione.class, CtFlussoRiversamento.class
        );
        method.setAccessible(true);
        @SuppressWarnings("unchecked")
        Uni<Boolean> uni = (Uni<Boolean>) method.invoke(service, config, flow);

        UniAssertSubscriber<Boolean> subscriber = uni.subscribe().withSubscriber(UniAssertSubscriber.create());
        subscriber.awaitItem().assertCompleted().assertItem(Boolean.FALSE);
    }

    /**
     * Test di integrazione REALE con endpoint UAT di PagoPA.
     *
     * Abilitato solo se PAGOPA_TEST_ENABLED=true e le variabili di ambiente contengono:
     * - PAGOPA_API_SUBSCRIPTION_KEY: chiave di subscription (da .env)
     * - PAGOPA_API_PASSWORD: password del canale (da fornire)
     *
     * Questo test effettua una chiamata REALE al Nodo PagoPA in UAT.
     * Utilizzerà credenziali di test fornite dall'ambiente.
     */
    @Test
    @EnabledIfEnvironmentVariable(named = "PAGOPA_TEST_ENABLED", matches = "true")
    void sendFlussoRiconciliazione_withRealUATEndpoint_shouldSucceedOrFailGracefully() throws Exception {
        String subscriptionKey = System.getenv("PAGOPA_API_SUBSCRIPTION_KEY");
        String password = System.getenv("PAGOPA_API_PASSWORD");
        String uatEndpoint = System.getenv("PAGOPA_API_URL");

        if (uatEndpoint == null) {
            uatEndpoint = "https://api.uat.platform.pagopa.it/nodo-auth/node-for-psp/v1";
        }

        Assertions.assertNotNull(subscriptionKey,
            "PAGOPA_API_SUBSCRIPTION_KEY non impostata");
        Assertions.assertNotNull(password,
            "PAGOPA_API_PASSWORD non impostata (necessaria per test UAT)");

        PagopaTransferListService transferListService = Mockito.mock(PagopaTransferListService.class);
        PagopaTransactionsService transactionsService = Mockito.mock(PagopaTransactionsService.class);
        CbillAbiFederazioneService cbillAbiFederazioneService = Mockito.mock(CbillAbiFederazioneService.class);
        PagopaReconciliationServiceImpl service = new PagopaReconciliationServiceImpl(
            transferListService, transactionsService, cbillAbiFederazioneService
        );

        setField(service, "pagoPaSubscriptionKey", subscriptionKey);
        setField(service, "pagoPaConnectionTimeout", 30000L);
        setField(service, "pagoPaReadTimeout", 30000L);
        setField(service, "pagoPaUrl", uatEndpoint);

        CtFlussoRiversamento flow = buildTestFlowForUAT();

        CbillAbiFederazione config = new CbillAbiFederazione();
        config.pagopaId = "TESTPSP001";
        config.pspFiscalCode = "12345678901";
        config.pspChannel = "01";

        Method sendFlussoMethod = PagopaReconciliationServiceImpl.class.getDeclaredMethod(
            "sendFlussoRiconciliazione", CbillAbiFederazione.class, CtFlussoRiversamento.class
        );
        sendFlussoMethod.setAccessible(true);

        Method buildSoapMethod = PagopaReconciliationServiceImpl.class.getDeclaredMethod(
            "buildSoapEnvelope", CbillAbiFederazione.class, CtFlussoRiversamento.class, String.class
        );
        buildSoapMethod.setAccessible(true);

        Method marshalFlowMethod = PagopaReconciliationServiceImpl.class.getDeclaredMethod(
            "marshalFlow", CtFlussoRiversamento.class
        );
        marshalFlowMethod.setAccessible(true);

        String xmlFlow = (String) marshalFlowMethod.invoke(service, flow);
        String encodedXml = java.util.Base64.getEncoder().encodeToString(xmlFlow.getBytes(StandardCharsets.UTF_8));
        String soapRequest = (String) buildSoapMethod.invoke(service, config, flow, encodedXml);

        System.out.println("\n========== RICHIESTA SOAP ==========");
        System.out.println(soapRequest);
        System.out.println("===================================\n");

        java.net.http.HttpClient httpClient = java.net.http.HttpClient.newBuilder()
            .connectTimeout(java.time.Duration.ofMillis(30000L))
            .build();
        java.net.http.HttpRequest httpRequest = java.net.http.HttpRequest.newBuilder()
            .uri(java.net.URI.create(uatEndpoint))
            .timeout(java.time.Duration.ofMillis(30000L))
            .header("Content-Type", "text/xml; charset=UTF-8")
            .header("Ocp-Apim-Subscription-Key", subscriptionKey)
            .POST(java.net.http.HttpRequest.BodyPublishers.ofString(soapRequest))
            .build();

        java.net.http.HttpResponse<String> soapResponse = httpClient.send(httpRequest, java.net.http.HttpResponse.BodyHandlers.ofString());

        System.out.println("\n========== RISPOSTA SOAP ==========");
        System.out.println("Status: " + soapResponse.statusCode());
        System.out.println(soapResponse.body());
        System.out.println("===================================\n");

        @SuppressWarnings("unchecked")
        Uni<Boolean> uni = (Uni<Boolean>) sendFlussoMethod.invoke(service, config, flow);

        UniAssertSubscriber<Boolean> subscriber = uni.subscribe().withSubscriber(UniAssertSubscriber.create());
        subscriber.awaitItem(java.time.Duration.ofSeconds(35)).assertCompleted();
        Boolean result = subscriber.getItem();

        System.out.println("✓ Test UAT completato. Esito: " + result);
    }

    // ========== Helper methods ==========

    /**
     * Costruisce un flusso di rendicontazione minimo valido per test mock.
     */
    private static CtFlussoRiversamento buildMinimalValidFlow() throws Exception {
        CtFlussoRiversamento flow = new CtFlussoRiversamento();
        flow.setVersioneOggetto("1.1");
        flow.setIdentificativoFlusso("2025-12-10TESTPSP001-001");
        flow.setDataOraFlusso(toXmlDateTime(Instant.now()));
        flow.setIdentificativoUnivocoRegolamento("TRN001");
        flow.setDataRegolamento(toXmlDate(LocalDate.now()));

        // Mittente (PSP)
        CtIstitutoMittente mittente = new CtIstitutoMittente();
        CtIdentificativoUnivoco idMittente = new CtIdentificativoUnivoco();
        idMittente.setTipoIdentificativoUnivoco(StTipoIdentificativoUnivoco.B);
        idMittente.setCodiceIdentificativoUnivoco("TESTPSP001");
        mittente.setIdentificativoUnivocoMittente(idMittente);
        flow.setIstitutoMittente(mittente);

        // Ricevente (PA)
        CtIstitutoRicevente ricevente = new CtIstitutoRicevente();
        CtIdentificativoUnivocoPersonaG idRicevente = new CtIdentificativoUnivocoPersonaG();
        idRicevente.setTipoIdentificativoUnivoco(StTipoIdentificativoUnivocoPersG.G);
        idRicevente.setCodiceIdentificativoUnivoco("12345678901");
        ricevente.setIdentificativoUnivocoRicevente(idRicevente);
        flow.setIstitutoRicevente(ricevente);

        // Dati pagamenti
        flow.setNumeroTotalePagamenti(BigDecimal.ONE);
        flow.setImportoTotalePagamenti(new BigDecimal("100.00"));

        CtDatiSingoliPagamenti pagamento = new CtDatiSingoliPagamenti();
        pagamento.setIdentificativoUnivocoVersamento("123000000000000001");
        pagamento.setIdentificativoUnivocoRiscossione("RIC001");
        pagamento.setIndiceDatiSingoloPagamento(1);
        pagamento.setSingoloImportoPagato(new BigDecimal("100.00"));
        pagamento.setCodiceEsitoSingoloPagamento("0");
        pagamento.setDataEsitoSingoloPagamento(toXmlDate(LocalDate.now()));

        flow.getDatiSingoliPagamenti().add(pagamento);

        return flow;
    }

    /**
     * Costruisce un flusso di test più completo per UAT, conforme specifiche.
     */
    private static CtFlussoRiversamento buildTestFlowForUAT() throws Exception {
        CtFlussoRiversamento flow = new CtFlussoRiversamento();
        flow.setVersioneOggetto("1.0");
        LocalDate today = LocalDate.now();
        String flowId = String.format("%tY-%tm-%td", today, today, today) + "TESTPSP001-UAT001";
        flow.setIdentificativoFlusso(flowId);
        flow.setDataOraFlusso(toXmlDateTime(Instant.now()));
        flow.setIdentificativoUnivocoRegolamento("SCT-UAT-" + System.currentTimeMillis());
        flow.setDataRegolamento(toXmlDate(today));

        // Mittente
        CtIstitutoMittente mittente = new CtIstitutoMittente();
        CtIdentificativoUnivoco idMittente = new CtIdentificativoUnivoco();
        idMittente.setTipoIdentificativoUnivoco(StTipoIdentificativoUnivoco.B);
        idMittente.setCodiceIdentificativoUnivoco("TESTPSP001");
        mittente.setIdentificativoUnivocoMittente(idMittente);
        mittente.setDenominazioneMittente("TEST PSP BANK");
        flow.setIstitutoMittente(mittente);

        // Ricevente
        CtIstitutoRicevente ricevente = new CtIstitutoRicevente();
        CtIdentificativoUnivocoPersonaG idRicevente = new CtIdentificativoUnivocoPersonaG();
        idRicevente.setTipoIdentificativoUnivoco(StTipoIdentificativoUnivocoPersG.G);
        idRicevente.setCodiceIdentificativoUnivoco("12345678901");
        ricevente.setIdentificativoUnivocoRicevente(idRicevente);
        ricevente.setDenominazioneRicevente("PA TEST");
        flow.setIstitutoRicevente(ricevente);

        flow.setNumeroTotalePagamenti(BigDecimal.ONE);
        flow.setImportoTotalePagamenti(new BigDecimal("250.75"));

        CtDatiSingoliPagamenti pagamento = new CtDatiSingoliPagamenti();
        pagamento.setIdentificativoUnivocoVersamento("12210209926737900");
        pagamento.setIdentificativoUnivocoRiscossione("2130101502302932577");
        pagamento.setIndiceDatiSingoloPagamento(1);
        pagamento.setSingoloImportoPagato(new BigDecimal("250.75"));
        pagamento.setCodiceEsitoSingoloPagamento("0");
        pagamento.setDataEsitoSingoloPagamento(toXmlDate(today));

        flow.getDatiSingoliPagamenti().add(pagamento);

        return flow;
    }

    private static XMLGregorianCalendar toXmlDate(LocalDate date) throws Exception {
        GregorianCalendar cal = GregorianCalendar.from(date.atStartOfDay(ZoneId.systemDefault()));
        return DatatypeFactory.newInstance().newXMLGregorianCalendar(cal);
    }

    private static XMLGregorianCalendar toXmlDateTime(Instant instant) throws Exception {
        GregorianCalendar cal = GregorianCalendar.from(instant.atZone(ZoneId.systemDefault()));
        return DatatypeFactory.newInstance().newXMLGregorianCalendar(cal);
    }

    private static void setField(Object target, String name, Object value) throws Exception {
        Field field = PagopaReconciliationServiceImpl.class.getDeclaredField(name);
        field.setAccessible(true);
        field.set(target, value);
    }
}

