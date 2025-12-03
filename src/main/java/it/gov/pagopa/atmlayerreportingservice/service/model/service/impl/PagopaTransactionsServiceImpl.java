package it.gov.pagopa.atmlayerreportingservice.service.model.service.impl;

import java.util.List;
import org.jboss.logging.Logger;
import io.quarkus.hibernate.reactive.panache.common.WithSession;
import io.quarkus.hibernate.reactive.panache.common.WithTransaction;
import io.smallrye.mutiny.Uni;
import it.gov.pagopa.atmlayerreportingservice.service.model.entity.PagopaTransactions;
import it.gov.pagopa.atmlayerreportingservice.service.model.repository.PagopaTransactionsRepository;
import it.gov.pagopa.atmlayerreportingservice.service.model.service.PagopaTransactionsService;
import jakarta.enterprise.context.ApplicationScoped;

@ApplicationScoped
public class PagopaTransactionsServiceImpl implements PagopaTransactionsService {
    private static final Logger LOG = Logger.getLogger(PagopaTransactionsServiceImpl.class);
    private final PagopaTransactionsRepository repository;

    public PagopaTransactionsServiceImpl(PagopaTransactionsRepository repository) {
        this.repository = repository;
    }

    @Override
    @WithSession
    public Uni<List<PagopaTransactions>> findBySenderBank(String senderBank) {
        return repository.findBySenderBank(senderBank);
    }

    @Override
    @WithSession
    public Uni<List<PagopaTransactions>> findAll() {
        return repository.listAll();
    }

    @Override
    @WithTransaction
    public Uni<PagopaTransactions> persist(PagopaTransactions entity) {
        return repository.persist(entity);
    }
}
