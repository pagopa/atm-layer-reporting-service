package it.gov.pagopa.atmlayerreportingservice.service.model.repository;

import java.time.LocalDate;
import java.util.List;

import io.quarkus.hibernate.reactive.panache.PanacheRepositoryBase;
import io.smallrye.mutiny.Uni;
import it.gov.pagopa.atmlayerreportingservice.service.model.entity.PagopaTransferList;
import jakarta.enterprise.context.ApplicationScoped;

@ApplicationScoped
public class PagopaTransferListRepository implements PanacheRepositoryBase<PagopaTransferList, Long> {
    public Uni<List<PagopaTransferList>> findBySenderBank(String senderBank) {
        return find("pagopaTransaction.senderBank", senderBank).list();
    }

    public Uni<List<PagopaTransferList>> findUnreportedBySenderBank(String senderBank) {
        return find("pagopaReported = false and pagopaTransaction.senderBank = ?1", senderBank).list();
    }

    public Uni<PagopaTransferList> findByTransactionIdAndTransferId(Long transactionId, Integer transferId) {
        return find("pagopaTransaction.transactionId = ?1 and transferId = ?2", transactionId, transferId).firstResult();
    }

    public Uni<List<PagopaTransferList>> findPayedNotReportedToPagoPAForBank(LocalDate toDate, String senderBank) {
        return find("pagopaReported = false and transferCro is not null and pagopaTransaction.senderBank = ?1 and pagopaTransaction.payDate < ?2", senderBank, toDate).list();
    }
}
