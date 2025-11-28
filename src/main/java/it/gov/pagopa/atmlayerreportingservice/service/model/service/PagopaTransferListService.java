package it.gov.pagopa.atmlayerreportingservice.service.model.service;

import it.gov.pagopa.atmlayerreportingservice.service.model.entity.PagopaTransferList;
import io.smallrye.mutiny.Uni;
import java.util.List;

public interface PagopaTransferListService {
    Uni<List<PagopaTransferList>> findAll();

    Uni<List<PagopaTransferList>> findByTransactionId(Long transactionId);

    Uni<PagopaTransferList> persist(PagopaTransferList entity);
}
