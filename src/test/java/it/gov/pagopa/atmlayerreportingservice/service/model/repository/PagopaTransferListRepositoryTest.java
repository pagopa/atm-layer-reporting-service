package it.gov.pagopa.atmlayerreportingservice.service.model.repository;

import io.quarkus.hibernate.reactive.panache.PanacheQuery;
import io.smallrye.mutiny.Uni;
import io.smallrye.mutiny.helpers.test.UniAssertSubscriber;
import it.gov.pagopa.atmlayerreportingservice.service.model.entity.PagopaTransferList;
import java.time.LocalDate;
import java.util.List;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;

public class PagopaTransferListRepositoryTest {

    @Test
    void findBySenderBank_shouldReturnList() {
        PagopaTransferListRepository repository = Mockito.spy(new PagopaTransferListRepository());
        PanacheQuery<PagopaTransferList> query = Mockito.mock(PanacheQuery.class);
        PagopaTransferList entity = new PagopaTransferList();
        Mockito.doReturn(query).when(repository).find(Mockito.eq("pagopaTransaction.senderBank"), Mockito.eq("BANK"));
        Mockito.when(query.list()).thenReturn(Uni.createFrom().item(List.of(entity)));

        UniAssertSubscriber<List<PagopaTransferList>> subscriber = repository.findBySenderBank("BANK").subscribe().withSubscriber(UniAssertSubscriber.create());

        subscriber.assertCompleted().assertItem(List.of(entity));
        Mockito.verify(repository).find("pagopaTransaction.senderBank", "BANK");
        Mockito.verify(query).list();
    }

    @Test
    void findUnreportedBySenderBank_shouldReturnList() {
        PagopaTransferListRepository repository = Mockito.spy(new PagopaTransferListRepository());
        PanacheQuery<PagopaTransferList> query = Mockito.mock(PanacheQuery.class);
        Mockito.doReturn(query).when(repository).find(Mockito.eq("pagopaReported = false and pagopaTransaction.senderBank = ?1"), Mockito.eq("BANK"));
        Mockito.when(query.list()).thenReturn(Uni.createFrom().item(List.of()));

        UniAssertSubscriber<List<PagopaTransferList>> subscriber = repository.findUnreportedBySenderBank("BANK").subscribe().withSubscriber(UniAssertSubscriber.create());

        subscriber.assertCompleted().assertItem(List.of());
        Mockito.verify(repository).find("pagopaReported = false and pagopaTransaction.senderBank = ?1", "BANK");
        Mockito.verify(query).list();
    }

    @Test
    void findByTransactionIdAndTransferId_shouldReturnEntity() {
        PagopaTransferListRepository repository = Mockito.spy(new PagopaTransferListRepository());
        PanacheQuery<PagopaTransferList> query = Mockito.mock(PanacheQuery.class);
        PagopaTransferList entity = new PagopaTransferList();
        Mockito.doReturn(query).when(repository).find(Mockito.eq("pagopaTransaction.transactionId = ?1 and transferId = ?2"), Mockito.eq(1L), Mockito.eq(2));
        Mockito.when(query.firstResult()).thenReturn(Uni.createFrom().item(entity));

        UniAssertSubscriber<PagopaTransferList> subscriber = repository.findByTransactionIdAndTransferId(1L, 2).subscribe().withSubscriber(UniAssertSubscriber.create());

        subscriber.assertCompleted().assertItem(entity);
        Mockito.verify(repository).find("pagopaTransaction.transactionId = ?1 and transferId = ?2", 1L, 2);
        Mockito.verify(query).firstResult();
    }

    @Test
    void findPayedNotReported_shouldReturnList() {
        PagopaTransferListRepository repository = Mockito.spy(new PagopaTransferListRepository());
        PanacheQuery<PagopaTransferList> query = Mockito.mock(PanacheQuery.class);
        LocalDate date = LocalDate.now();
        Mockito.doReturn(query).when(repository).find(Mockito.eq("pagopaReported = false and transferCro is not null and pagopaTransaction.senderBank = ?1 and pagopaTransaction.payDate < ?2"), Mockito.eq("BANK"), Mockito.eq(date));
        Mockito.when(query.list()).thenReturn(Uni.createFrom().item(List.of()));

        UniAssertSubscriber<List<PagopaTransferList>> subscriber = repository.findPayedNotReportedToPagoPAForBank(date, "BANK").subscribe().withSubscriber(UniAssertSubscriber.create());

        subscriber.assertCompleted().assertItem(List.of());
        Mockito.verify(repository).find("pagopaReported = false and transferCro is not null and pagopaTransaction.senderBank = ?1 and pagopaTransaction.payDate < ?2", "BANK", date);
        Mockito.verify(query).list();
    }
}
