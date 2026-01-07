package it.gov.pagopa.atmlayerreportingservice.service.model.service.impl;

import io.quarkus.hibernate.reactive.panache.common.WithSession;
import io.quarkus.hibernate.reactive.panache.common.WithTransaction;
import io.smallrye.mutiny.Uni;
import it.gov.pagopa.atmlayerreportingservice.service.model.entity.CbillAbiFederazione;
import it.gov.pagopa.atmlayerreportingservice.service.model.repository.CbillAbiFederazioneRepository;
import it.gov.pagopa.atmlayerreportingservice.service.model.service.CbillAbiFederazioneService;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.persistence.PersistenceException;
import java.util.List;

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
        String normalizedAbi;
        try {
            normalizedAbi = validateAbi(abi);
        } catch (IllegalArgumentException ex) {
            return Uni.createFrom().failure(ex);
        }
        return repository.findByAbi(normalizedAbi)
                .onItem().ifNull().failWith(() -> new IllegalArgumentException("ABI not found"))
                .onFailure(PersistenceException.class).transform(ex -> new IllegalArgumentException("Unable to fetch ABI", ex));
    }

    @Override
    @WithSession
    public Uni<CbillAbiFederazione> getPspConfiguration(String abi) {
        String normalizedAbi;
        try {
            normalizedAbi = validateAbi(abi);
        } catch (IllegalArgumentException ex) {
            return Uni.createFrom().failure(ex);
        }
        return repository.findByAbi(normalizedAbi)
                .onItem().ifNull().failWith(() -> new IllegalArgumentException("PSP configuration not found for ABI " + normalizedAbi))
                .onFailure(PersistenceException.class).transform(ex -> new IllegalArgumentException("Unable to fetch PSP configuration", ex));
    }

    @Override
    @WithTransaction
    public Uni<CbillAbiFederazione> create(CbillAbiFederazione entity) {
        try {
            validateEntity(entity);
        } catch (IllegalArgumentException ex) {
            return Uni.createFrom().failure(ex);
        }
        return repository.persist(entity)
                .onFailure(PersistenceException.class).transform(ex -> new IllegalArgumentException("Invalid CbillAbiFederazione data", ex));
    }

    private void validateEntity(CbillAbiFederazione entity) {
        if (entity == null) {
            throw new IllegalArgumentException("Request body is required");
        }
        entity.abi = validateLength(entity.abi, 5, "ABI");
        entity.pagopaId = validateLength(entity.pagopaId, 35, "pagopaId");
        entity.pspFiscalCode = validateLength(entity.pspFiscalCode, 11, "pspFiscalCode");
        entity.pspChannel = validateLength(entity.pspChannel, 5, "pspChannel");
        entity.password = validateLength(entity.password, 255, "password");
    }

    private String validateAbi(String abi) {
        return validateLength(abi, 5, "ABI");
    }

    private String validateLength(String value, int maxLength, String fieldName) {
        if (value == null) {
            throw new IllegalArgumentException(fieldName + " is required");
        }
        String trimmed = value.trim();
        if (trimmed.isEmpty()) {
            throw new IllegalArgumentException(fieldName + " is required");
        }
        if (trimmed.length() > maxLength) {
            throw new IllegalArgumentException(fieldName + " exceeds " + maxLength + " characters");
        }
        return trimmed;
    }
}
