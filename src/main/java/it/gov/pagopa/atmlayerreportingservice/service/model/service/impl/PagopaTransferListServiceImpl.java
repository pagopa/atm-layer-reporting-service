package it.gov.pagopa.atmlayerreportingservice.service.model.service.impl;

import java.util.List;

import org.jboss.logging.Logger;

import io.smallrye.mutiny.Uni;
import it.gov.pagopa.atmlayerreportingservice.service.model.entity.PagopaTransferList;
import it.gov.pagopa.atmlayerreportingservice.service.model.repository.PagopaTransferListRepository;
import it.gov.pagopa.atmlayerreportingservice.service.model.service.PagopaTransferListService;
import jakarta.enterprise.context.ApplicationScoped;

@ApplicationScoped
public class PagopaTransferListServiceImpl implements PagopaTransferListService {
    private static final Logger LOG = Logger.getLogger(PagopaTransferListServiceImpl.class);
    private final PagopaTransferListRepository repository;

    public PagopaTransferListServiceImpl(PagopaTransferListRepository repository) {
        this.repository = repository;
    }

    @Override
    public Uni<List<PagopaTransferList>> findAll() {
        LOG.info("Finding all PagopaTransferList entities");
        return repository.listAll();
    }

    @Override
    public Uni<List<PagopaTransferList>> findByTransactionId(Long transactionId) {
        LOG.info("Finding PagopaTransferList by transactionId: " + transactionId);
        return repository.find("pagopaTransaction.id", transactionId).list();
    }

    @Override
    public Uni<PagopaTransferList> persist(PagopaTransferList entity) {
        LOG.info("Persisting PagopaTransferList entity: " + entity);
        return repository.persist(entity);
    }
}
