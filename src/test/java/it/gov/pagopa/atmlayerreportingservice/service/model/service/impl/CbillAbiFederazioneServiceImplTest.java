package it.gov.pagopa.atmlayerreportingservice.service.model.service.impl;

import io.quarkus.test.junit.QuarkusTest;
import io.smallrye.mutiny.Uni;
import io.smallrye.mutiny.helpers.test.UniAssertSubscriber;
import it.gov.pagopa.atmlayerreportingservice.service.model.entity.CbillAbiFederazione;
import it.gov.pagopa.atmlayerreportingservice.service.model.repository.CbillAbiFederazioneRepository;
import java.util.List;
import jakarta.persistence.PersistenceException;
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
        Mockito.when(repository.findByAbi("12345")).thenReturn(Uni.createFrom().item(entity));

        UniAssertSubscriber<CbillAbiFederazione> subscriber = service.findByAbi("12345").subscribe().withSubscriber(UniAssertSubscriber.create());

        subscriber.assertCompleted().assertItem(entity);
        Mockito.verify(repository).findByAbi("12345");
    }

    @Test
    void findByAbi_shouldFail_whenRepositoryEmitsFailure() {
        CbillAbiFederazioneRepository repository = Mockito.mock(CbillAbiFederazioneRepository.class);
        CbillAbiFederazioneServiceImpl service = new CbillAbiFederazioneServiceImpl(repository);
        RuntimeException failure = new RuntimeException("find error");
        Mockito.when(repository.findByAbi("12345")).thenReturn(Uni.createFrom().failure(failure));

        UniAssertSubscriber<CbillAbiFederazione> subscriber = service.findByAbi("12345").subscribe().withSubscriber(UniAssertSubscriber.create());

        subscriber.assertFailedWith(failure.getClass(), "find error");
        Mockito.verify(repository).findByAbi("12345");
    }

    @Test
    void findByAbi_shouldFail_whenAbiIsNull() {
        CbillAbiFederazioneRepository repository = Mockito.mock(CbillAbiFederazioneRepository.class);
        CbillAbiFederazioneServiceImpl service = new CbillAbiFederazioneServiceImpl(repository);

        UniAssertSubscriber<CbillAbiFederazione> subscriber = service.findByAbi(null).subscribe().withSubscriber(UniAssertSubscriber.create());

        subscriber.assertFailedWith(IllegalArgumentException.class, "ABI is required");
        Mockito.verifyNoInteractions(repository);
    }

    @Test
    void findByAbi_shouldFail_whenAbiExceedsMaximumLength() {
        CbillAbiFederazioneRepository repository = Mockito.mock(CbillAbiFederazioneRepository.class);
        CbillAbiFederazioneServiceImpl service = new CbillAbiFederazioneServiceImpl(repository);

        UniAssertSubscriber<CbillAbiFederazione> subscriber = service.findByAbi("123456").subscribe().withSubscriber(UniAssertSubscriber.create());

        subscriber.assertFailedWith(IllegalArgumentException.class, "ABI exceeds 5 characters");
        Mockito.verifyNoInteractions(repository);
    }

    @Test
    void findByAbi_shouldFail_whenAbiIsInvalid() {
        CbillAbiFederazioneServiceImpl service = new CbillAbiFederazioneServiceImpl(Mockito.mock(CbillAbiFederazioneRepository.class));

        UniAssertSubscriber<CbillAbiFederazione> subscriber = service.findByAbi(" ").subscribe().withSubscriber(UniAssertSubscriber.create());

        subscriber.assertFailedWith(IllegalArgumentException.class, "ABI is required");
    }

    @Test
    void findByAbi_shouldFail_whenNotFound() {
        CbillAbiFederazioneRepository repository = Mockito.mock(CbillAbiFederazioneRepository.class);
        CbillAbiFederazioneServiceImpl service = new CbillAbiFederazioneServiceImpl(repository);
        Mockito.when(repository.findByAbi("12345")).thenReturn(Uni.createFrom().nullItem());

        UniAssertSubscriber<CbillAbiFederazione> subscriber = service.findByAbi("12345").subscribe().withSubscriber(UniAssertSubscriber.create());

        subscriber.assertFailedWith(IllegalArgumentException.class, "ABI not found");
        Mockito.verify(repository).findByAbi("12345");
    }

    @Test
    void findByAbi_shouldFail_whenRepositoryEmitsPersistenceException() {
        CbillAbiFederazioneRepository repository = Mockito.mock(CbillAbiFederazioneRepository.class);
        CbillAbiFederazioneServiceImpl service = new CbillAbiFederazioneServiceImpl(repository);
        Mockito.when(repository.findByAbi("12345")).thenReturn(Uni.createFrom().failure(new PersistenceException("db error")));

        UniAssertSubscriber<CbillAbiFederazione> subscriber = service.findByAbi("12345").subscribe().withSubscriber(UniAssertSubscriber.create());

        subscriber.assertFailedWith(IllegalArgumentException.class, "Unable to fetch ABI");
        Mockito.verify(repository).findByAbi("12345");
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
    void getPspConfiguration_shouldFail_whenNotFound() {
        CbillAbiFederazioneRepository repository = Mockito.mock(CbillAbiFederazioneRepository.class);
        CbillAbiFederazioneServiceImpl service = new CbillAbiFederazioneServiceImpl(repository);
        Mockito.when(repository.findByAbi("12345")).thenReturn(Uni.createFrom().nullItem());

        UniAssertSubscriber<CbillAbiFederazione> subscriber = service.getPspConfiguration("12345").subscribe().withSubscriber(UniAssertSubscriber.create());

        subscriber.assertFailedWith(IllegalArgumentException.class, "PSP configuration not found for ABI 12345");
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

    @Test
    void getPspConfiguration_shouldWrapPersistenceException_whenRepositoryFails() {
        CbillAbiFederazioneRepository repository = Mockito.mock(CbillAbiFederazioneRepository.class);
        CbillAbiFederazioneServiceImpl service = new CbillAbiFederazioneServiceImpl(repository);
        PersistenceException persistenceException = new PersistenceException("db error");
        Mockito.when(repository.findByAbi("12345")).thenReturn(Uni.createFrom().failure(persistenceException));

        UniAssertSubscriber<CbillAbiFederazione> subscriber = service.getPspConfiguration("12345").subscribe().withSubscriber(UniAssertSubscriber.create());

        subscriber.assertFailedWith(IllegalArgumentException.class, "Unable to fetch PSP configuration");
        Mockito.verify(repository).findByAbi("12345");
    }

    @Test
    void create_shouldReturnEntity_whenRepositorySucceeds() {
        CbillAbiFederazioneRepository repository = Mockito.mock(CbillAbiFederazioneRepository.class);
        CbillAbiFederazioneServiceImpl service = new CbillAbiFederazioneServiceImpl(repository);
        CbillAbiFederazione entity = buildEntity();
        Mockito.when(repository.persist(entity)).thenReturn(Uni.createFrom().item(entity));

        UniAssertSubscriber<CbillAbiFederazione> subscriber = service.create(entity).subscribe().withSubscriber(UniAssertSubscriber.create());

        subscriber.assertCompleted().assertItem(entity);
        Mockito.verify(repository).persist(entity);
    }

    @Test
    void create_shouldFail_whenValidationFails() {
        CbillAbiFederazioneServiceImpl service = new CbillAbiFederazioneServiceImpl(Mockito.mock(CbillAbiFederazioneRepository.class));

        UniAssertSubscriber<CbillAbiFederazione> subscriber = service.create(null).subscribe().withSubscriber(UniAssertSubscriber.create());

        subscriber.assertFailedWith(IllegalArgumentException.class, "Request body is required");
    }

    @Test
    void create_shouldFail_whenRepositoryEmitsPersistenceException() {
        CbillAbiFederazioneRepository repository = Mockito.mock(CbillAbiFederazioneRepository.class);
        CbillAbiFederazioneServiceImpl service = new CbillAbiFederazioneServiceImpl(repository);
        CbillAbiFederazione entity = buildEntity();
        Mockito.when(repository.persist(entity)).thenReturn(Uni.createFrom().failure(new PersistenceException("db error")));

        UniAssertSubscriber<CbillAbiFederazione> subscriber = service.create(entity).subscribe().withSubscriber(UniAssertSubscriber.create());

        subscriber.assertFailedWith(IllegalArgumentException.class, "Invalid CbillAbiFederazione data");
        Mockito.verify(repository).persist(entity);
    }

    private CbillAbiFederazione buildEntity() {
        CbillAbiFederazione entity = new CbillAbiFederazione();
        entity.abi = "12345";
        entity.pagopaId = "pagopa-id";
        entity.pspFiscalCode = "12345678901";
        entity.pspChannel = "12345";
        entity.password = "password";
        entity.pagopaDirect = Boolean.TRUE;
        return entity;
    }
}
