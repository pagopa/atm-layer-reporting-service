package it.gov.pagopa.atmlayerreportingservice.service.model.repository;

import io.quarkus.hibernate.reactive.panache.PanacheRepositoryBase;
import it.gov.pagopa.atmlayerreportingservice.service.model.entity.PagopaTransactions;
import jakarta.enterprise.context.ApplicationScoped;

@ApplicationScoped
public class PagopaTransactionsRepository implements PanacheRepositoryBase<PagopaTransactions, Long> {
}
