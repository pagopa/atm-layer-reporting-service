package it.gov.pagopa.atmlayerreportingservice.service.model.service;

import it.gov.pagopa.atmlayerreportingservice.service.model.entity.CbillAbiFederazione;
import io.smallrye.mutiny.Uni;
import java.util.List;

public interface CbillAbiFederazioneService {
    Uni<List<CbillAbiFederazione>> findAll();

    Uni<CbillAbiFederazione> findByAbi(String abi);

    Uni<CbillAbiFederazione> getPspConfiguration(String abi);
}
