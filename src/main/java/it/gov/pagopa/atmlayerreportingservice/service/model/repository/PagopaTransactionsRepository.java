package it.gov.pagopa.atmlayerreportingservice.service.model.repository;

import io.quarkus.hibernate.reactive.panache.PanacheRepositoryBase;
import it.gov.pagopa.atmlayerreportingservice.service.model.entity.PagopaTransactions;
import jakarta.enterprise.context.ApplicationScoped;
import io.smallrye.mutiny.Uni;
import java.util.List;

@ApplicationScoped
public class PagopaTransactionsRepository implements PanacheRepositoryBase<PagopaTransactions, Long> {
    public Uni<List<PagopaTransactions>> findBySenderBank(String senderBank) {
        return find("senderBank", senderBank).list();
    }
}
