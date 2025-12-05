package it.gov.pagopa.atmlayerreportingservice.service.model.service.impl;

import io.quarkus.test.junit.QuarkusTest;
import io.smallrye.mutiny.Uni;
import io.smallrye.mutiny.helpers.test.UniAssertSubscriber;
import it.gov.pagopa.atmlayerreportingservice.service.model.entity.PagopaTransactions;
import it.gov.pagopa.atmlayerreportingservice.service.model.repository.PagopaTransactionsRepository;
import java.util.List;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;

@QuarkusTest
class PagopaTransactionsServiceImplTest {

    @Test
    void findAll_shouldFail_whenSenderBankMissing() {
        PagopaTransactionsRepository repository = Mockito.mock(PagopaTransactionsRepository.class);
        PagopaTransactionsServiceImpl service = new PagopaTransactionsServiceImpl(repository);

        UniAssertSubscriber<List<PagopaTransactions>> subscriber = service.findAll(" ").subscribe().withSubscriber(UniAssertSubscriber.create());

        subscriber.assertFailedWith(IllegalArgumentException.class, "SenderBank header is required");
        Mockito.verifyNoInteractions(repository);
    }

    @Test
    void findAll_shouldDelegate_whenSenderBankProvided() {
        PagopaTransactionsRepository repository = Mockito.mock(PagopaTransactionsRepository.class);
        PagopaTransactionsServiceImpl service = new PagopaTransactionsServiceImpl(repository);
        PagopaTransactions tx = new PagopaTransactions();
        Mockito.when(repository.findBySenderBank("BANK")).thenReturn(Uni.createFrom().item(List.of(tx)));

        UniAssertSubscriber<List<PagopaTransactions>> subscriber = service.findAll("BANK").subscribe().withSubscriber(UniAssertSubscriber.create());

        subscriber.assertCompleted().assertItem(List.of(tx));
        Mockito.verify(repository).findBySenderBank("BANK");
    }

    @Test
    void findBySenderBank_shouldReturnList_whenRepositorySucceeds() {
        PagopaTransactionsRepository repository = Mockito.mock(PagopaTransactionsRepository.class);
        PagopaTransactionsServiceImpl service = new PagopaTransactionsServiceImpl(repository);
        PagopaTransactions tx = new PagopaTransactions();
        Mockito.when(repository.findBySenderBank("BANK")).thenReturn(Uni.createFrom().item(List.of(tx)));

        UniAssertSubscriber<List<PagopaTransactions>> subscriber = service.findBySenderBank("BANK").subscribe().withSubscriber(UniAssertSubscriber.create());

        subscriber.assertCompleted().assertItem(List.of(tx));
        Mockito.verify(repository).findBySenderBank("BANK");
    }

    @Test
    void findAllBySenderBank_shouldReturnList_whenRepositorySucceeds() {
        PagopaTransactionsRepository repository = Mockito.mock(PagopaTransactionsRepository.class);
        PagopaTransactionsServiceImpl service = new PagopaTransactionsServiceImpl(repository);
        PagopaTransactions tx = new PagopaTransactions();
        Mockito.when(repository.findBySenderBank("BANK")).thenReturn(Uni.createFrom().item(List.of(tx)));

        UniAssertSubscriber<List<PagopaTransactions>> subscriber = service.findAllBySenderBank("BANK").subscribe().withSubscriber(UniAssertSubscriber.create());

        subscriber.assertCompleted().assertItem(List.of(tx));
        Mockito.verify(repository).findBySenderBank("BANK");
    }

    @Test
    void findAll_shouldReturnList_whenRepositorySucceeds() {
        PagopaTransactionsRepository repository = Mockito.mock(PagopaTransactionsRepository.class);
        PagopaTransactionsServiceImpl service = new PagopaTransactionsServiceImpl(repository);
        PagopaTransactions tx = new PagopaTransactions();
        Mockito.when(repository.listAll()).thenReturn(Uni.createFrom().item(List.of(tx)));

        UniAssertSubscriber<List<PagopaTransactions>> subscriber = service.findAll().subscribe().withSubscriber(UniAssertSubscriber.create());

        subscriber.assertCompleted().assertItem(List.of(tx));
        Mockito.verify(repository).listAll();
    }

    @Test
    void persist_shouldReturnEntity_whenRepositoryPersists() {
        PagopaTransactionsRepository repository = Mockito.mock(PagopaTransactionsRepository.class);
        PagopaTransactionsServiceImpl service = new PagopaTransactionsServiceImpl(repository);
        PagopaTransactions tx = new PagopaTransactions();
        Mockito.when(repository.persist(tx)).thenReturn(Uni.createFrom().item(tx));

        UniAssertSubscriber<PagopaTransactions> subscriber = service.persist(tx).subscribe().withSubscriber(UniAssertSubscriber.create());

        subscriber.assertCompleted().assertItem(tx);
        Mockito.verify(repository).persist(tx);
    }
}
