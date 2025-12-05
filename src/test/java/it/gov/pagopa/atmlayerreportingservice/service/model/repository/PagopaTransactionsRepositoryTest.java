package it.gov.pagopa.atmlayerreportingservice.service.model.repository;

import io.quarkus.hibernate.reactive.panache.PanacheQuery;
import io.quarkus.test.junit.QuarkusTest;
import io.smallrye.mutiny.Uni;
import io.smallrye.mutiny.helpers.test.UniAssertSubscriber;
import it.gov.pagopa.atmlayerreportingservice.service.model.entity.PagopaTransactions;
import java.util.List;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;

@QuarkusTest
class PagopaTransactionsRepositoryTest {

    PagopaTransactionsRepository repository;

    @BeforeEach
    void setUp() {
        repository = Mockito.spy(new PagopaTransactionsRepository());
    }

    @Test
    void findBySenderBank_shouldReturnList_whenQuerySucceeds() {
        String senderBank = "BANK";
        PagopaTransactions entity = new PagopaTransactions();
        PanacheQuery<PagopaTransactions> query = Mockito.mock(PanacheQuery.class);
        Mockito.doReturn(query).when(repository).find("senderBank", senderBank);
        Mockito.when(query.list()).thenReturn(Uni.createFrom().item(List.of(entity)));

        UniAssertSubscriber<List<PagopaTransactions>> subscriber = repository.findBySenderBank(senderBank)
                .subscribe().withSubscriber(UniAssertSubscriber.create());

        subscriber.assertCompleted().assertItem(List.of(entity));
        Mockito.verify(repository).find("senderBank", senderBank);
        Mockito.verify(query).list();
    }

    @Test
    void findBySenderBank_shouldPropagateFailure_whenQueryFails() {
        String senderBank = "FAIL";
        RuntimeException failure = new RuntimeException("query failed");
        PanacheQuery<PagopaTransactions> query = Mockito.mock(PanacheQuery.class);
        Mockito.doReturn(query).when(repository).find("senderBank", senderBank);
        Mockito.when(query.list()).thenReturn(Uni.createFrom().failure(failure));

        UniAssertSubscriber<List<PagopaTransactions>> subscriber = repository.findBySenderBank(senderBank)
                .subscribe().withSubscriber(UniAssertSubscriber.create());

        subscriber.assertFailedWith(RuntimeException.class, "query failed");
        Mockito.verify(repository).find("senderBank", senderBank);
        Mockito.verify(query).list();
    }
}
