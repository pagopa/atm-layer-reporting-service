package it.gov.pagopa.atmlayerreportingservice.service.model.repository;

import io.quarkus.hibernate.reactive.panache.PanacheQuery;
import io.quarkus.test.junit.QuarkusTest;
import io.smallrye.mutiny.Uni;
import io.smallrye.mutiny.helpers.test.UniAssertSubscriber;
import it.gov.pagopa.atmlayerreportingservice.service.model.entity.PagopaTransferList;
import java.time.LocalDate;
import java.util.List;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;

@QuarkusTest
class PagopaTransferListRepositoryTest {

    PagopaTransferListRepository repository;

    @BeforeEach
    void setUp() {
        repository = Mockito.spy(new PagopaTransferListRepository());
    }

    @Test
    void findBySenderBank_shouldReturnList_whenQuerySucceeds() {
        String senderBank = "BANK";
        List<PagopaTransferList> entities = List.of(new PagopaTransferList());
        PanacheQuery<PagopaTransferList> query = Mockito.mock(PanacheQuery.class);
        Mockito.doReturn(query).when(repository).find("pagopaTransaction.senderBank", senderBank);
        Mockito.when(query.list()).thenReturn(Uni.createFrom().item(entities));

        UniAssertSubscriber<List<PagopaTransferList>> subscriber = repository.findBySenderBank(senderBank)
                .subscribe().withSubscriber(UniAssertSubscriber.create());

        subscriber.assertCompleted().assertItem(entities);
        Mockito.verify(repository).find("pagopaTransaction.senderBank", senderBank);
        Mockito.verify(query).list();
    }

    @Test
    void findBySenderBank_shouldPropagateFailure_whenQueryFails() {
        String senderBank = "FAIL";
        RuntimeException failure = new RuntimeException("query failed");
        PanacheQuery<PagopaTransferList> query = Mockito.mock(PanacheQuery.class);
        Mockito.doReturn(query).when(repository).find("pagopaTransaction.senderBank", senderBank);
        Mockito.when(query.list()).thenReturn(Uni.createFrom().failure(failure));

        UniAssertSubscriber<List<PagopaTransferList>> subscriber = repository.findBySenderBank(senderBank)
                .subscribe().withSubscriber(UniAssertSubscriber.create());

        subscriber.assertFailedWith(RuntimeException.class, "query failed");
        Mockito.verify(repository).find("pagopaTransaction.senderBank", senderBank);
        Mockito.verify(query).list();
    }

    @Test
    void findUnreportedBySenderBank_shouldReturnList_whenQuerySucceeds() {
        String senderBank = "BANK";
        List<PagopaTransferList> entities = List.of(new PagopaTransferList());
        PanacheQuery<PagopaTransferList> query = Mockito.mock(PanacheQuery.class);
        Mockito.doReturn(query).when(repository).find("pagopaReported = false and pagopaTransaction.senderBank = ?1", senderBank);
        Mockito.when(query.list()).thenReturn(Uni.createFrom().item(entities));

        UniAssertSubscriber<List<PagopaTransferList>> subscriber = repository.findUnreportedBySenderBank(senderBank)
                .subscribe().withSubscriber(UniAssertSubscriber.create());

        subscriber.assertCompleted().assertItem(entities);
        Mockito.verify(repository).find("pagopaReported = false and pagopaTransaction.senderBank = ?1", senderBank);
        Mockito.verify(query).list();
    }

    @Test
    void findUnreportedBySenderBank_shouldPropagateFailure_whenQueryFails() {
        String senderBank = "FAIL";
        RuntimeException failure = new RuntimeException("unreported failed");
        PanacheQuery<PagopaTransferList> query = Mockito.mock(PanacheQuery.class);
        Mockito.doReturn(query).when(repository).find("pagopaReported = false and pagopaTransaction.senderBank = ?1", senderBank);
        Mockito.when(query.list()).thenReturn(Uni.createFrom().failure(failure));

        UniAssertSubscriber<List<PagopaTransferList>> subscriber = repository.findUnreportedBySenderBank(senderBank)
                .subscribe().withSubscriber(UniAssertSubscriber.create());

        subscriber.assertFailedWith(RuntimeException.class, "unreported failed");
        Mockito.verify(repository).find("pagopaReported = false and pagopaTransaction.senderBank = ?1", senderBank);
        Mockito.verify(query).list();
    }

    @Test
    void findByTransactionIdAndTransferId_shouldReturnEntity_whenQuerySucceeds() {
        Long transactionId = 10L;
        Integer transferId = 5;
        PagopaTransferList entity = new PagopaTransferList();
        PanacheQuery<PagopaTransferList> query = Mockito.mock(PanacheQuery.class);
        Mockito.doReturn(query).when(repository).find("pagopaTransaction.transactionId = ?1 and transferId = ?2", transactionId, transferId);
        Mockito.when(query.firstResult()).thenReturn(Uni.createFrom().item(entity));

        UniAssertSubscriber<PagopaTransferList> subscriber = repository.findByTransactionIdAndTransferId(transactionId, transferId)
                .subscribe().withSubscriber(UniAssertSubscriber.create());

        subscriber.assertCompleted().assertItem(entity);
        Mockito.verify(repository).find("pagopaTransaction.transactionId = ?1 and transferId = ?2", transactionId, transferId);
        Mockito.verify(query).firstResult();
    }

    @Test
    void findByTransactionIdAndTransferId_shouldPropagateFailure_whenQueryFails() {
        Long transactionId = 20L;
        Integer transferId = 7;
        RuntimeException failure = new RuntimeException("firstResult failed");
        PanacheQuery<PagopaTransferList> query = Mockito.mock(PanacheQuery.class);
        Mockito.doReturn(query).when(repository).find("pagopaTransaction.transactionId = ?1 and transferId = ?2", transactionId, transferId);
        Mockito.when(query.firstResult()).thenReturn(Uni.createFrom().failure(failure));

        UniAssertSubscriber<PagopaTransferList> subscriber = repository.findByTransactionIdAndTransferId(transactionId, transferId)
                .subscribe().withSubscriber(UniAssertSubscriber.create());

        subscriber.assertFailedWith(RuntimeException.class, "firstResult failed");
        Mockito.verify(repository).find("pagopaTransaction.transactionId = ?1 and transferId = ?2", transactionId, transferId);
        Mockito.verify(query).firstResult();
    }

    @Test
    void findPayedNotReportedToPagoPAForBank_shouldReturnList_whenQuerySucceeds() {
        LocalDate toDate = LocalDate.now();
        String senderBank = "BANK";
        List<PagopaTransferList> entities = List.of(new PagopaTransferList());
        PanacheQuery<PagopaTransferList> query = Mockito.mock(PanacheQuery.class);
        Mockito.doReturn(query).when(repository).find("pagopaReported = false and transferCro is not null and pagopaTransaction.senderBank = ?1 and pagopaTransaction.payDate < ?2", senderBank, toDate);
        Mockito.when(query.list()).thenReturn(Uni.createFrom().item(entities));

        UniAssertSubscriber<List<PagopaTransferList>> subscriber = repository.findPayedNotReportedToPagoPAForBank(toDate, senderBank)
                .subscribe().withSubscriber(UniAssertSubscriber.create());

        subscriber.assertCompleted().assertItem(entities);
        Mockito.verify(repository).find("pagopaReported = false and transferCro is not null and pagopaTransaction.senderBank = ?1 and pagopaTransaction.payDate < ?2", senderBank, toDate);
        Mockito.verify(query).list();
    }

    @Test
    void findPayedNotReportedToPagoPAForBank_shouldPropagateFailure_whenQueryFails() {
        LocalDate toDate = LocalDate.now().minusDays(1);
        String senderBank = "FAIL";
        RuntimeException failure = new RuntimeException("payed not reported failed");
        PanacheQuery<PagopaTransferList> query = Mockito.mock(PanacheQuery.class);
        Mockito.doReturn(query).when(repository).find("pagopaReported = false and transferCro is not null and pagopaTransaction.senderBank = ?1 and pagopaTransaction.payDate < ?2", senderBank, toDate);
        Mockito.when(query.list()).thenReturn(Uni.createFrom().failure(failure));

        UniAssertSubscriber<List<PagopaTransferList>> subscriber = repository.findPayedNotReportedToPagoPAForBank(toDate, senderBank)
                .subscribe().withSubscriber(UniAssertSubscriber.create());

        subscriber.assertFailedWith(RuntimeException.class, "payed not reported failed");
        Mockito.verify(repository).find("pagopaReported = false and transferCro is not null and pagopaTransaction.senderBank = ?1 and pagopaTransaction.payDate < ?2", senderBank, toDate);
        Mockito.verify(query).list();
    }
}
