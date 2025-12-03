package it.gov.pagopa.atmlayerreportingservice.service.model.service;

import it.gov.pagopa.atmlayerreportingservice.service.model.entity.PagopaTransferList;
import io.smallrye.mutiny.Uni;
import java.util.List;
import java.math.BigDecimal;
import java.time.LocalDate;

public interface PagopaTransferListService {
    Uni<List<PagopaTransferList>> findBySenderBank(String senderBank);

    Uni<List<PagopaTransferList>> findAll();

    Uni<List<PagopaTransferList>> findByTransactionId(Long transactionId);

    Uni<PagopaTransferList> persist(PagopaTransferList entity);

    Uni<PagopaTransferList> updateTransferList(Long transactionId, Integer transferId, BigDecimal transferAmount, String transferCro, String flowId, LocalDate transferExecutionDt, String paFiscalCode);

    Uni<List<PagopaTransferList>> findPayedNotReportedToPagoPAForBank(LocalDate toDate, String senderBank);

    Uni<Void> markAsReported(Long id);
}
