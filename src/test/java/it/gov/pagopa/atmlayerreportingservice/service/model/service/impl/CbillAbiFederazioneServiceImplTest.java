package it.gov.pagopa.atmlayerreportingservice.service.model.service.impl;

import it.gov.pagopa.atmlayerreportingservice.service.model.entity.CbillAbiFederazione;
import it.gov.pagopa.atmlayerreportingservice.service.model.repository.CbillAbiFederazioneRepository;
import io.smallrye.mutiny.Uni;
import io.smallrye.mutiny.helpers.test.UniAssertSubscriber;
import java.util.List;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;

public class CbillAbiFederazioneServiceImplTest {
    @Test
    void findAll_shouldReturnFederazioni_whenRepositoryReturnsData() {
        CbillAbiFederazioneRepository repository = Mockito.mock(CbillAbiFederazioneRepository.class);
        CbillAbiFederazioneServiceImpl service = new CbillAbiFederazioneServiceImpl(repository);
        CbillAbiFederazione entity = new CbillAbiFederazione();
        Mockito.when(repository.listAll()).thenReturn(Uni.createFrom().item(List.of(entity)));

        UniAssertSubscriber<List<CbillAbiFederazione>> subscriber = service.findAll().subscribe().withSubscriber(UniAssertSubscriber.create());

        subscriber.assertCompleted().assertItem(List.of(entity));
        Mockito.verify(repository).listAll();
    }

    @Test
    void findAll_shouldFail_whenRepositoryFails() {
        CbillAbiFederazioneRepository repository = Mockito.mock(CbillAbiFederazioneRepository.class);
        CbillAbiFederazioneServiceImpl service = new CbillAbiFederazioneServiceImpl(repository);
        RuntimeException failure = new RuntimeException("error");
        Mockito.when(repository.listAll()).thenReturn(Uni.createFrom().failure(failure));

        UniAssertSubscriber<List<CbillAbiFederazione>> subscriber = service.findAll().subscribe().withSubscriber(UniAssertSubscriber.create());

        subscriber.assertFailedWith(failure.getClass(), "error");
        Mockito.verify(repository).listAll();
    }

    @Test
    void findByAbi_shouldReturnEntity_whenRepositoryReturnsData() {
        CbillAbiFederazioneRepository repository = Mockito.mock(CbillAbiFederazioneRepository.class);
        CbillAbiFederazioneServiceImpl service = new CbillAbiFederazioneServiceImpl(repository);
        CbillAbiFederazione entity = new CbillAbiFederazione();
        Mockito.when(repository.findById("ABI")).thenReturn(Uni.createFrom().item(entity));

        UniAssertSubscriber<CbillAbiFederazione> subscriber = service.findByAbi("ABI").subscribe().withSubscriber(UniAssertSubscriber.create());

        subscriber.assertCompleted().assertItem(entity);
        Mockito.verify(repository).findById("ABI");
    }

    @Test
    void findByAbi_shouldFail_whenRepositoryFails() {
        CbillAbiFederazioneRepository repository = Mockito.mock(CbillAbiFederazioneRepository.class);
        CbillAbiFederazioneServiceImpl service = new CbillAbiFederazioneServiceImpl(repository);
        RuntimeException failure = new RuntimeException("error");
        Mockito.when(repository.findById("ABI")).thenReturn(Uni.createFrom().failure(failure));

        UniAssertSubscriber<CbillAbiFederazione> subscriber = service.findByAbi("ABI").subscribe().withSubscriber(UniAssertSubscriber.create());

        subscriber.assertFailedWith(failure.getClass(), "error");
        Mockito.verify(repository).findById("ABI");
    }
}
