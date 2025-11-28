package it.gov.pagopa.atmlayerreportingservice.service.model.service.impl;

import java.util.List;

import org.jboss.logging.Logger;

import io.smallrye.mutiny.Uni;
import it.gov.pagopa.atmlayerreportingservice.service.model.entity.CbillAbiFederazione;
import it.gov.pagopa.atmlayerreportingservice.service.model.repository.CbillAbiFederazioneRepository;
import it.gov.pagopa.atmlayerreportingservice.service.model.service.CbillAbiFederazioneService;
import jakarta.enterprise.context.ApplicationScoped;

@ApplicationScoped
public class CbillAbiFederazioneServiceImpl implements CbillAbiFederazioneService {
    private static final Logger LOG = Logger.getLogger(CbillAbiFederazioneServiceImpl.class);
    private final CbillAbiFederazioneRepository repository;

    public CbillAbiFederazioneServiceImpl(CbillAbiFederazioneRepository repository) {
        this.repository = repository;
    }

    @Override
    public Uni<List<CbillAbiFederazione>> findAll() {
        LOG.info("Calling findAll method to fetch all CbillAbiFederazione records.");
        return repository.listAll()
                .onItem().invoke(result -> LOG.info("Retrieved " + result.size() + " CbillAbiFederazione records."))
                .onFailure().invoke(ex -> LOG.error("Error occurred while fetching CbillAbiFederazione records: " + ex.getMessage()));
    }

    @Override
    public Uni<CbillAbiFederazione> findByAbi(String abi) {
        LOG.info("Calling findByAbi method with ABI: " + abi);
        return repository.findById(abi)
                .onItem().invoke(result -> {
                    if (result != null) {
                        LOG.info("CbillAbiFederazione record found for ABI: " + abi);
                    } else {
                        LOG.warn("No CbillAbiFederazione record found for ABI: " + abi);
                    }
                })
                .onFailure().invoke(ex -> LOG.error("Error occurred while fetching CbillAbiFederazione record for ABI: " + abi + ": " + ex.getMessage()));
    }
}
