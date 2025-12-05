package it.gov.pagopa.atmlayerreportingservice.service.model.repository;

import io.quarkus.hibernate.reactive.panache.PanacheQuery;
import io.smallrye.mutiny.Uni;
import io.smallrye.mutiny.helpers.test.UniAssertSubscriber;
import it.gov.pagopa.atmlayerreportingservice.service.model.entity.PagopaTransactions;
import java.util.List;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;

public class PagopaTransactionsRepositoryTest {

    @Test
    void findBySenderBank_shouldReturnList() {
        PagopaTransactionsRepository repository = Mockito.spy(new PagopaTransactionsRepository());
        PanacheQuery<PagopaTransactions> query = Mockito.mock(PanacheQuery.class);
        PagopaTransactions entity = new PagopaTransactions();
        Mockito.doReturn(query).when(repository).find(Mockito.eq("senderBank"), Mockito.eq("BANK"));
        Mockito.when(query.list()).thenReturn(Uni.createFrom().item(List.of(entity)));

        UniAssertSubscriber<List<PagopaTransactions>> subscriber = repository.findBySenderBank("BANK").subscribe().withSubscriber(UniAssertSubscriber.create());

        subscriber.assertCompleted().assertItem(List.of(entity));
        Mockito.verify(repository).find("senderBank", "BANK");
        Mockito.verify(query).list();
    }

    @Test
    void listAll_shouldDelegate() {
        PagopaTransactionsRepository repository = Mockito.spy(new PagopaTransactionsRepository());
        Mockito.doReturn(Uni.createFrom().item(List.of())).when(repository).listAll();

        UniAssertSubscriber<List<PagopaTransactions>> subscriber = repository.listAll().subscribe().withSubscriber(UniAssertSubscriber.create());

        subscriber.assertCompleted();
        Mockito.verify(repository).listAll();
    }
}
