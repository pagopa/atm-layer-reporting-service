package it.gov.pagopa.atmlayerreportingservice.service.model.service.impl;

import io.smallrye.mutiny.Uni;
import io.smallrye.mutiny.helpers.test.UniAssertSubscriber;
import it.gov.pagopa.atmlayerreportingservice.service.model.entity.PagopaTransactions;
import it.gov.pagopa.atmlayerreportingservice.service.model.repository.PagopaTransactionsRepository;
import java.util.List;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;

public class PagopaTransactionsServiceImplAdditionalTest {

    @Test
    void findAllWithSenderBank_shouldValidateHeader() {
        PagopaTransactionsRepository repository = Mockito.mock(PagopaTransactionsRepository.class);
        PagopaTransactionsServiceImpl service = new PagopaTransactionsServiceImpl(repository);

        UniAssertSubscriber<List<PagopaTransactions>> subscriber = service.findAll(null).subscribe().withSubscriber(UniAssertSubscriber.create());

        subscriber.assertFailedWith(IllegalArgumentException.class, "SenderBank header is required");
    }

    @Test
    void findAllWithSenderBank_shouldDelegate() {
        PagopaTransactionsRepository repository = Mockito.mock(PagopaTransactionsRepository.class);
        PagopaTransactionsServiceImpl service = new PagopaTransactionsServiceImpl(repository);
        PagopaTransactions entity = new PagopaTransactions();
        Mockito.when(repository.findBySenderBank("BANK")).thenReturn(Uni.createFrom().item(List.of(entity)));

        UniAssertSubscriber<List<PagopaTransactions>> subscriber = service.findAll("BANK").subscribe().withSubscriber(UniAssertSubscriber.create());

        subscriber.assertCompleted().assertItem(List.of(entity));
        Mockito.verify(repository).findBySenderBank("BANK");
    }
}
