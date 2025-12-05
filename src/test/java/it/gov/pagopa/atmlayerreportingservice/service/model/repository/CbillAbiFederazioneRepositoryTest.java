package it.gov.pagopa.atmlayerreportingservice.service.model.repository;

import io.quarkus.hibernate.reactive.panache.PanacheQuery;
import io.smallrye.mutiny.Uni;
import io.smallrye.mutiny.helpers.test.UniAssertSubscriber;
import it.gov.pagopa.atmlayerreportingservice.service.model.entity.CbillAbiFederazione;
import java.util.List;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;

public class CbillAbiFederazioneRepositoryTest {

    @Test
    void findByAbi_shouldReturnResult() {
        CbillAbiFederazioneRepository repository = Mockito.spy(new CbillAbiFederazioneRepository());
        PanacheQuery<CbillAbiFederazione> query = Mockito.mock(PanacheQuery.class);
        CbillAbiFederazione entity = new CbillAbiFederazione();

        Mockito.doReturn(query).when(repository).find(Mockito.eq("abi"), Mockito.eq("12345"));
        Mockito.when(query.firstResult()).thenReturn(Uni.createFrom().item(entity));

        UniAssertSubscriber<CbillAbiFederazione> subscriber = repository.findByAbi("12345").subscribe().withSubscriber(UniAssertSubscriber.create());

        subscriber.assertCompleted().assertItem(entity);
        Mockito.verify(repository).find("abi", "12345");
        Mockito.verify(query).firstResult();
    }

    @Test
    void listAll_shouldDelegate() {
        CbillAbiFederazioneRepository repository = Mockito.spy(new CbillAbiFederazioneRepository());
        Mockito.doReturn(Uni.createFrom().item(List.of())).when(repository).listAll();

        UniAssertSubscriber<List<CbillAbiFederazione>> subscriber = repository.listAll().subscribe().withSubscriber(UniAssertSubscriber.create());

        subscriber.assertCompleted();
        Mockito.verify(repository).listAll();
    }
}
