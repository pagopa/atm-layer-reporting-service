package it.gov.pagopa.atmlayerreportingservice.service.model.service.impl;

import io.quarkus.test.junit.QuarkusTest;
import io.smallrye.mutiny.Uni;
import io.smallrye.mutiny.helpers.test.UniAssertSubscriber;
import it.gov.pagopa.atmlayerreportingservice.service.model.entity.CbillAbiFederazione;
import it.gov.pagopa.atmlayerreportingservice.service.model.repository.CbillAbiFederazioneRepository;
import java.util.List;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;

@QuarkusTest
class CbillAbiFederazioneServiceImplTest {

    @Test
    void findAll_shouldReturnEntities_whenRepositorySucceeds() {
        CbillAbiFederazioneRepository repository = Mockito.mock(CbillAbiFederazioneRepository.class);
        CbillAbiFederazioneServiceImpl service = new CbillAbiFederazioneServiceImpl(repository);
        CbillAbiFederazione entity = new CbillAbiFederazione();
        Mockito.when(repository.listAll()).thenReturn(Uni.createFrom().item(List.of(entity)));

        UniAssertSubscriber<List<CbillAbiFederazione>> subscriber = service.findAll().subscribe().withSubscriber(UniAssertSubscriber.create());

        subscriber.assertCompleted().assertItem(List.of(entity));
        Mockito.verify(repository).listAll();
    }

    @Test
    void findAll_shouldFail_whenRepositoryEmitsFailure() {
        CbillAbiFederazioneRepository repository = Mockito.mock(CbillAbiFederazioneRepository.class);
        CbillAbiFederazioneServiceImpl service = new CbillAbiFederazioneServiceImpl(repository);
        RuntimeException failure = new RuntimeException("err");
        Mockito.when(repository.listAll()).thenReturn(Uni.createFrom().failure(failure));

        UniAssertSubscriber<List<CbillAbiFederazione>> subscriber = service.findAll().subscribe().withSubscriber(UniAssertSubscriber.create());

        subscriber.assertFailedWith(failure.getClass(), "err");
        Mockito.verify(repository).listAll();
    }

    @Test
    void findByAbi_shouldReturnEntity_whenRepositorySucceeds() {
        CbillAbiFederazioneRepository repository = Mockito.mock(CbillAbiFederazioneRepository.class);
        CbillAbiFederazioneServiceImpl service = new CbillAbiFederazioneServiceImpl(repository);
        CbillAbiFederazione entity = new CbillAbiFederazione();
        Mockito.when(repository.findById("12345")).thenReturn(Uni.createFrom().item(entity));

        UniAssertSubscriber<CbillAbiFederazione> subscriber = service.findByAbi("12345").subscribe().withSubscriber(UniAssertSubscriber.create());

        subscriber.assertCompleted().assertItem(entity);
        Mockito.verify(repository).findById("12345");
    }

    @Test
    void findByAbi_shouldFail_whenRepositoryEmitsFailure() {
        CbillAbiFederazioneRepository repository = Mockito.mock(CbillAbiFederazioneRepository.class);
        CbillAbiFederazioneServiceImpl service = new CbillAbiFederazioneServiceImpl(repository);
        RuntimeException failure = new RuntimeException("find error");
        Mockito.when(repository.findById("12345")).thenReturn(Uni.createFrom().failure(failure));

        UniAssertSubscriber<CbillAbiFederazione> subscriber = service.findByAbi("12345").subscribe().withSubscriber(UniAssertSubscriber.create());

        subscriber.assertFailedWith(failure.getClass(), "find error");
        Mockito.verify(repository).findById("12345");
    }

    @Test
    void getPspConfiguration_shouldFail_whenAbiBlank() {
        CbillAbiFederazioneRepository repository = Mockito.mock(CbillAbiFederazioneRepository.class);
        CbillAbiFederazioneServiceImpl service = new CbillAbiFederazioneServiceImpl(repository);

        UniAssertSubscriber<CbillAbiFederazione> subscriber = service.getPspConfiguration(" ").subscribe().withSubscriber(UniAssertSubscriber.create());

        subscriber.assertFailedWith(IllegalArgumentException.class, "ABI is required");
        Mockito.verifyNoInteractions(repository);
    }

    @Test
    void getPspConfiguration_shouldReturnEntity_whenRepositorySucceeds() {
        CbillAbiFederazioneRepository repository = Mockito.mock(CbillAbiFederazioneRepository.class);
        CbillAbiFederazioneServiceImpl service = new CbillAbiFederazioneServiceImpl(repository);
        CbillAbiFederazione entity = new CbillAbiFederazione();
        Mockito.when(repository.findByAbi("12345")).thenReturn(Uni.createFrom().item(entity));

        UniAssertSubscriber<CbillAbiFederazione> subscriber = service.getPspConfiguration("12345").subscribe().withSubscriber(UniAssertSubscriber.create());

        subscriber.assertCompleted().assertItem(entity);
        Mockito.verify(repository).findByAbi("12345");
    }

    @Test
    void getPspConfiguration_shouldFail_whenRepositoryEmitsFailure() {
        CbillAbiFederazioneRepository repository = Mockito.mock(CbillAbiFederazioneRepository.class);
        CbillAbiFederazioneServiceImpl service = new CbillAbiFederazioneServiceImpl(repository);
        RuntimeException failure = new RuntimeException("repo fail");
        Mockito.when(repository.findByAbi("12345")).thenReturn(Uni.createFrom().failure(failure));

        UniAssertSubscriber<CbillAbiFederazione> subscriber = service.getPspConfiguration("12345").subscribe().withSubscriber(UniAssertSubscriber.create());

        subscriber.assertFailedWith(failure.getClass(), "repo fail");
        Mockito.verify(repository).findByAbi("12345");
    }
}
