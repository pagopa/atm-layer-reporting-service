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
    void findAll_shouldReturnEntities() {
        CbillAbiFederazioneRepository repository = Mockito.mock(CbillAbiFederazioneRepository.class);
        CbillAbiFederazioneServiceImpl service = new CbillAbiFederazioneServiceImpl(repository);
        Mockito.when(repository.listAll()).thenReturn(Uni.createFrom().item(List.of(new CbillAbiFederazione())));

        UniAssertSubscriber<List<CbillAbiFederazione>> subscriber = service.findAll().subscribe().withSubscriber(UniAssertSubscriber.create());

        subscriber.assertCompleted();
        Mockito.verify(repository).listAll();
    }

    @Test
    void findByAbi_shouldReturnEntity() {
        CbillAbiFederazioneRepository repository = Mockito.mock(CbillAbiFederazioneRepository.class);
        CbillAbiFederazioneServiceImpl service = new CbillAbiFederazioneServiceImpl(repository);
        CbillAbiFederazione entity = new CbillAbiFederazione();
        Mockito.when(repository.findById("12345")).thenReturn(Uni.createFrom().item(entity));

        UniAssertSubscriber<CbillAbiFederazione> subscriber = service.findByAbi("12345").subscribe().withSubscriber(UniAssertSubscriber.create());

        subscriber.assertCompleted().assertItem(entity);
        Mockito.verify(repository).findById("12345");
    }

    @Test
    void getPspConfiguration_shouldValidateAbi() {
        CbillAbiFederazioneRepository repository = Mockito.mock(CbillAbiFederazioneRepository.class);
        CbillAbiFederazioneServiceImpl service = new CbillAbiFederazioneServiceImpl(repository);

        UniAssertSubscriber<CbillAbiFederazione> subscriber = service.getPspConfiguration(null).subscribe().withSubscriber(UniAssertSubscriber.create());

        subscriber.assertFailedWith(IllegalArgumentException.class, "ABI is required");
    }

    @Test
    void getPspConfiguration_shouldReturnEntity() {
        CbillAbiFederazioneRepository repository = Mockito.mock(CbillAbiFederazioneRepository.class);
        CbillAbiFederazioneServiceImpl service = new CbillAbiFederazioneServiceImpl(repository);
        CbillAbiFederazione entity = new CbillAbiFederazione();
        Mockito.when(repository.findByAbi("54321")).thenReturn(Uni.createFrom().item(entity));

        UniAssertSubscriber<CbillAbiFederazione> subscriber = service.getPspConfiguration("54321").subscribe().withSubscriber(UniAssertSubscriber.create());

        subscriber.assertCompleted().assertItem(entity);
        Mockito.verify(repository).findByAbi("54321");
    }
}
