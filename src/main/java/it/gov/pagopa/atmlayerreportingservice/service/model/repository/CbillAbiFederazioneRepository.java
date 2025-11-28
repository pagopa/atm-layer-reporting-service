package it.gov.pagopa.atmlayerreportingservice.service.model.repository;

import io.quarkus.hibernate.reactive.panache.PanacheRepositoryBase;
import it.gov.pagopa.atmlayerreportingservice.service.model.entity.CbillAbiFederazione;
import jakarta.enterprise.context.ApplicationScoped;

@ApplicationScoped
public class CbillAbiFederazioneRepository implements PanacheRepositoryBase<CbillAbiFederazione, String> {
}
