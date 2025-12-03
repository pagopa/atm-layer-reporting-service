package it.gov.pagopa.atmlayerreportingservice.service.model.service.impl;

import it.gov.pagopa.atmlayerreportingservice.service.model.entity.PagopaTransferList;
import it.gov.pagopa.atmlayerreportingservice.service.model.repository.PagopaTransferListRepository;
import io.smallrye.mutiny.Uni;
import io.smallrye.mutiny.helpers.test.UniAssertSubscriber;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;

public class PagopaTransferListServiceImplTest {
    @Test
    void findAll_shouldReturnTransferLists_whenRepositoryReturnsData() {
        PagopaTransferListRepository repository = Mockito.mock(PagopaTransferListRepository.class);
        PagopaTransferListServiceImpl service = new PagopaTransferListServiceImpl(repository);
        PagopaTransferList transfer = new PagopaTransferList();
        transfer.id = 1L;
        transfer.transferId = 10;
        transfer.transferAmount = BigDecimal.ONE;
        transfer.transferExecutionDt = LocalDate.now();
        transfer.pagopaReported = Boolean.FALSE;
        transfer.paFiscalCode = "CF";
        transfer.paIban = "IBAN";
        transfer.rmtInfo = "INFO";
        Mockito.when(repository.listAll()).thenReturn(Uni.createFrom().item(List.of(transfer)));

        UniAssertSubscriber<List<PagopaTransferList>> subscriber = service.findAll().subscribe().withSubscriber(UniAssertSubscriber.create());

        subscriber.assertCompleted().assertItem(List.of(transfer));
        Mockito.verify(repository).listAll();
    }

    @Test
    void findAll_shouldFail_whenRepositoryFails() {
        PagopaTransferListRepository repository = Mockito.mock(PagopaTransferListRepository.class);
        PagopaTransferListServiceImpl service = new PagopaTransferListServiceImpl(repository);
        RuntimeException failure = new RuntimeException("error");
        Mockito.when(repository.listAll()).thenReturn(Uni.createFrom().failure(failure));

        UniAssertSubscriber<List<PagopaTransferList>> subscriber = service.findAll().subscribe().withSubscriber(UniAssertSubscriber.create());

        subscriber.assertFailedWith(failure.getClass(), "error");
        Mockito.verify(repository).listAll();
    }

    @Test
    void findByTransactionId_shouldReturnTransferLists_whenRepositoryReturnsData() {
        PagopaTransferListRepository repository = Mockito.mock(PagopaTransferListRepository.class);
        PagopaTransferListServiceImpl service = new PagopaTransferListServiceImpl(repository);
        PagopaTransferList transfer = new PagopaTransferList();
        transfer.id = 1L;
        io.quarkus.hibernate.reactive.panache.PanacheQuery<PagopaTransferList> query = Mockito.mock(io.quarkus.hibernate.reactive.panache.PanacheQuery.class);
        Mockito.when(repository.find("pagopaTransaction.id", 1L)).thenReturn(query);
        Mockito.when(query.list()).thenReturn(Uni.createFrom().item(List.of(transfer)));

        UniAssertSubscriber<List<PagopaTransferList>> subscriber = service.findByTransactionId(1L).subscribe().withSubscriber(UniAssertSubscriber.create());

        subscriber.assertCompleted().assertItem(List.of(transfer));
        Mockito.verify(repository).find("pagopaTransaction.id", 1L);
        Mockito.verify(query).list();
    }

    @Test
    void findByTransactionId_shouldFail_whenRepositoryFails() {
        PagopaTransferListRepository repository = Mockito.mock(PagopaTransferListRepository.class);
        PagopaTransferListServiceImpl service = new PagopaTransferListServiceImpl(repository);
        RuntimeException failure = new RuntimeException("error");
        io.quarkus.hibernate.reactive.panache.PanacheQuery<PagopaTransferList> query = Mockito.mock(io.quarkus.hibernate.reactive.panache.PanacheQuery.class);
        Mockito.when(repository.find("pagopaTransaction.id", 1L)).thenReturn(query);
        Mockito.when(query.list()).thenReturn(Uni.createFrom().failure(failure));

        UniAssertSubscriber<List<PagopaTransferList>> subscriber = service.findByTransactionId(1L).subscribe().withSubscriber(UniAssertSubscriber.create());

        subscriber.assertFailedWith(failure.getClass(), "error");
        Mockito.verify(repository).find("pagopaTransaction.id", 1L);
        Mockito.verify(query).list();
    }

    @Test
    void persist_shouldReturnEntity_whenRepositoryPersists() {
        PagopaTransferListRepository repository = Mockito.mock(PagopaTransferListRepository.class);
        PagopaTransferListServiceImpl service = new PagopaTransferListServiceImpl(repository);
        PagopaTransferList transfer = new PagopaTransferList();
        Mockito.when(repository.persist(transfer)).thenReturn(Uni.createFrom().item(transfer));

        UniAssertSubscriber<PagopaTransferList> subscriber = service.persist(transfer).subscribe().withSubscriber(UniAssertSubscriber.create());

        subscriber.assertCompleted().assertItem(transfer);
        Mockito.verify(repository).persist(transfer);
    }

    @Test
    void persist_shouldFail_whenRepositoryFails() {
        PagopaTransferListRepository repository = Mockito.mock(PagopaTransferListRepository.class);
        PagopaTransferListServiceImpl service = new PagopaTransferListServiceImpl(repository);
        PagopaTransferList transfer = new PagopaTransferList();
        RuntimeException failure = new RuntimeException("persist error");
        Mockito.when(repository.persist(transfer)).thenReturn(Uni.createFrom().failure(failure));

        UniAssertSubscriber<PagopaTransferList> subscriber = service.persist(transfer).subscribe().withSubscriber(UniAssertSubscriber.create());

        subscriber.assertFailedWith(failure.getClass(), "persist error");
        Mockito.verify(repository).persist(transfer);
    }
}
