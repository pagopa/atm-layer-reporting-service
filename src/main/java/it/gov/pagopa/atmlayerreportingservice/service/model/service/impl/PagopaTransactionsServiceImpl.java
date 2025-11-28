package it.gov.pagopa.atmlayerreportingservice.service.model.service.impl;

import it.gov.pagopa.atmlayerreportingservice.service.model.entity.PagopaTransactions;
import it.gov.pagopa.atmlayerreportingservice.service.model.repository.PagopaTransactionsRepository;
import it.gov.pagopa.atmlayerreportingservice.service.model.service.PagopaTransactionsService;
import java.util.List;
import org.jboss.logging.Logger;
import io.smallrye.mutiny.Uni;
import jakarta.enterprise.context.ApplicationScoped;

@ApplicationScoped
public class PagopaTransactionsServiceImpl implements PagopaTransactionsService {
    private static final Logger LOG = Logger.getLogger(PagopaTransactionsServiceImpl.class);
    private final PagopaTransactionsRepository repository;

    public PagopaTransactionsServiceImpl(PagopaTransactionsRepository repository) {
        this.repository = repository;
    }

    @Override
    public Uni<List<PagopaTransactions>> findAll() {
        LOG.info("Calling findAll method to fetch all PagopaTransactions records.");
        return repository.listAll()
                .onItem().invoke(result -> LOG.info("Retrieved " + result.size() + " PagopaTransactions records."))
                .onFailure().invoke(ex -> LOG.error("Error occurred while fetching PagopaTransactions records: " + ex.getMessage()));
    }

    @Override
    public Uni<PagopaTransactions> persist(PagopaTransactions entity) {
        LOG.info("Persisting PagopaTransactions entity: " + entity);
        return repository.persist(entity)
                .onItem().invoke(result -> LOG.info("Persisted PagopaTransactions entity with ID: " + result.id))
                .onFailure().invoke(ex -> LOG.error("Error occurred while persisting PagopaTransactions entity: " + ex.getMessage()));
    }
}
