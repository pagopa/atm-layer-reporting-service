package it.gov.pagopa.atmlayerreportingservice.service.model.service;

import it.gov.pagopa.atmlayerreportingservice.service.model.entity.PagopaTransactions;
import io.smallrye.mutiny.Uni;
import java.util.List;

public interface PagopaTransactionsService {
    Uni<List<PagopaTransactions>> findAll(String senderBank);

    Uni<List<PagopaTransactions>> findBySenderBank(String senderBank);

    Uni<List<PagopaTransactions>> findAllBySenderBank(String senderBank);

    Uni<List<PagopaTransactions>> findAll();

    Uni<PagopaTransactions> persist(PagopaTransactions entity);
}
