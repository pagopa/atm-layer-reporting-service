package it.gov.pagopa.atmlayerreportingservice.service.model.service;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;
import it.gov.pagopa.atmlayerreportingservice.service.model.dto.PagopaTransferListDto;
import it.gov.pagopa.atmlayerreportingservice.service.model.entity.PagopaTransferList;
import io.smallrye.mutiny.Uni;

public interface PagopaTransferListService {
    Uni<List<PagopaTransferList>> findAll(String senderBank);

    Uni<List<PagopaTransferList>> findBySenderBank(String senderBank);

    Uni<List<PagopaTransferList>> findAllBySenderBank(String senderBank);

    Uni<List<PagopaTransferList>> findAll();

    Uni<List<PagopaTransferList>> findByTransactionId(Long transactionId);

    Uni<PagopaTransferList> persist(PagopaTransferList entity);

    Uni<PagopaTransferList> updateTransferList(String senderBank, PagopaTransferListDto request);

    Uni<PagopaTransferList> updateTransferList(String senderBank, Long transactionId, Integer transferId, BigDecimal transferAmount, String transferCro, String flowId, LocalDate transferExecutionDt, String paFiscalCode);

    Uni<List<PagopaTransferList>> findPayedNotReportedToPagoPAForBank(LocalDate toDate, String senderBank);

    Uni<Void> markAsReported(Long id);
}
