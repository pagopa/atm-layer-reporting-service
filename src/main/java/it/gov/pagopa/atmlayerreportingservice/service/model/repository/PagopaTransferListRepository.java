package it.gov.pagopa.atmlayerreportingservice.service.model.repository;

import io.quarkus.hibernate.reactive.panache.PanacheRepositoryBase;
import it.gov.pagopa.atmlayerreportingservice.service.model.entity.PagopaTransferList;
import jakarta.enterprise.context.ApplicationScoped;
import io.smallrye.mutiny.Uni;
import java.util.List;

@ApplicationScoped
public class PagopaTransferListRepository implements PanacheRepositoryBase<PagopaTransferList, Long> {
    public Uni<List<PagopaTransferList>> findBySenderBank(String senderBank) {
        return find("pagopaTransaction.senderBank", senderBank).list();
    }

    public Uni<List<PagopaTransferList>> findUnreportedBySenderBank(String senderBank) {
        return find("pagopaReported = false and pagopaTransaction.senderBank = ?1", senderBank).list();
    }

    public Uni<PagopaTransferList> findByTransactionIdAndTransferId(Long transactionId, Integer transferId) {
        return find("transactionId = ?1 and transferId = ?2", transactionId, transferId).firstResult();
    }
}
