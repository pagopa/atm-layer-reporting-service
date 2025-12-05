package it.gov.pagopa.atmlayerreportingservice.service.model.repository;

import io.quarkus.hibernate.reactive.panache.PanacheQuery;
import io.quarkus.test.junit.QuarkusTest;
import io.smallrye.mutiny.Uni;
import io.smallrye.mutiny.helpers.test.UniAssertSubscriber;
import it.gov.pagopa.atmlayerreportingservice.service.model.entity.CbillAbiFederazione;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;

@QuarkusTest
class CbillAbiFederazioneRepositoryTest {

    CbillAbiFederazioneRepository repository;

    @BeforeEach
    void setUp() {
        repository = Mockito.spy(new CbillAbiFederazioneRepository());
    }

    @Test
    void findByAbi_shouldReturnEntity_whenQuerySucceeds() {
        String abi = "12345";
        CbillAbiFederazione entity = new CbillAbiFederazione();
        PanacheQuery<CbillAbiFederazione> query = Mockito.mock(PanacheQuery.class);
        Mockito.doReturn(query).when(repository).find("abi", abi);
        Mockito.when(query.firstResult()).thenReturn(Uni.createFrom().item(entity));

        UniAssertSubscriber<CbillAbiFederazione> subscriber = repository.findByAbi(abi)
                .subscribe().withSubscriber(UniAssertSubscriber.create());

        subscriber.assertCompleted().assertItem(entity);
        Mockito.verify(repository).find("abi", abi);
        Mockito.verify(query).firstResult();
    }

    @Test
    void findByAbi_shouldPropagateFailure_whenQueryFails() {
        String abi = "fail";
        RuntimeException failure = new RuntimeException("query failed");
        PanacheQuery<CbillAbiFederazione> query = Mockito.mock(PanacheQuery.class);
        Mockito.doReturn(query).when(repository).find("abi", abi);
        Mockito.when(query.firstResult()).thenReturn(Uni.createFrom().failure(failure));

        UniAssertSubscriber<CbillAbiFederazione> subscriber = repository.findByAbi(abi)
                .subscribe().withSubscriber(UniAssertSubscriber.create());

        subscriber.assertFailedWith(RuntimeException.class, "query failed");
        Mockito.verify(repository).find("abi", abi);
        Mockito.verify(query).firstResult();
    }
}
