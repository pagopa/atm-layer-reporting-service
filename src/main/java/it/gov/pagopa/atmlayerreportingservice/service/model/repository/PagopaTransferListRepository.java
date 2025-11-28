package it.gov.pagopa.atmlayerreportingservice.service.model.repository;

import io.quarkus.hibernate.reactive.panache.PanacheRepositoryBase;
import it.gov.pagopa.atmlayerreportingservice.service.model.entity.PagopaTransferList;
import jakarta.enterprise.context.ApplicationScoped;

@ApplicationScoped
public class PagopaTransferListRepository implements PanacheRepositoryBase<PagopaTransferList, Long> {
}
