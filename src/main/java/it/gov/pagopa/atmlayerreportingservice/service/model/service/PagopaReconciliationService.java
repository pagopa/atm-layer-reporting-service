package it.gov.pagopa.atmlayerreportingservice.service.model.service;

import io.smallrye.mutiny.Uni;

public interface PagopaReconciliationService {
    Uni<Void> schedulePagoPaReconciliation();
}
