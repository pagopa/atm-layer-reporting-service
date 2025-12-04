package it.gov.pagopa.atmlayerreportingservice.service.model.service.impl;

import java.util.List;

import io.quarkus.hibernate.reactive.panache.common.WithSession;
import io.smallrye.mutiny.Uni;
import it.gov.pagopa.atmlayerreportingservice.service.model.entity.CbillAbiFederazione;
import it.gov.pagopa.atmlayerreportingservice.service.model.repository.CbillAbiFederazioneRepository;
import it.gov.pagopa.atmlayerreportingservice.service.model.service.CbillAbiFederazioneService;
import jakarta.enterprise.context.ApplicationScoped;

@ApplicationScoped
public class CbillAbiFederazioneServiceImpl implements CbillAbiFederazioneService {
    private final CbillAbiFederazioneRepository repository;

    public CbillAbiFederazioneServiceImpl(CbillAbiFederazioneRepository repository) {
        this.repository = repository;
    }

    @Override
    @WithSession
    public Uni<List<CbillAbiFederazione>> findAll() {
        return repository.listAll();
    }

    @Override
    @WithSession
    public Uni<CbillAbiFederazione> findByAbi(String abi) {
        return repository.findById(abi);
    }

    @Override
    @WithSession
    public Uni<CbillAbiFederazione> getPspConfiguration(String abi) {
        if (abi == null || abi.isBlank()) {
            return Uni.createFrom().failure(new IllegalArgumentException("ABI is required"));
        }
        return repository.findByAbi(abi);
    }
}
