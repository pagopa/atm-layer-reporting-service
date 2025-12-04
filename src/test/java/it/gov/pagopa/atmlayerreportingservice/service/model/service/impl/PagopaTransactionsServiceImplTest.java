package it.gov.pagopa.atmlayerreportingservice.service.model.service.impl;

import io.quarkus.test.junit.QuarkusTest;
import it.gov.pagopa.atmlayerreportingservice.service.model.entity.PagopaTransactions;
import it.gov.pagopa.atmlayerreportingservice.service.model.repository.PagopaTransactionsRepository;
import io.smallrye.mutiny.Uni;
import io.smallrye.mutiny.helpers.test.UniAssertSubscriber;
import java.math.BigDecimal;
import java.time.Instant;
import java.util.List;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;

@QuarkusTest
public class PagopaTransactionsServiceImplTest {
    @Test
    void findAll_shouldReturnTransactions_whenRepositoryReturnsData() {
        PagopaTransactionsRepository repository = Mockito.mock(PagopaTransactionsRepository.class);
        PagopaTransactionsServiceImpl service = new PagopaTransactionsServiceImpl(repository);
        PagopaTransactions tx = new PagopaTransactions();
        tx.id = 1L;
        tx.transactionId = "T1";
        tx.status = "S";
        tx.billAccountId = "B1";
        tx.billAmount = BigDecimal.ONE;
        tx.senderBank = "BANK";
        tx.payDate = Instant.now();
        tx.reported = Boolean.FALSE;
        tx.billerIban = "IT00";
        tx.billId = "BID";
        tx.billerCommission = BigDecimal.ONE;
        tx.bankCommission = BigDecimal.ONE;
        tx.idempotencyKey = "IDK";
        tx.billerFiscalCode = "FISCAL";
        tx.noticeNumber = "NOTICE";
        tx.retCode = "RET";
        tx.outcomeCode = "O";
        tx.payDescription = "DESC";
        tx.billerName = "NAME";
        tx.billerOffice = "OFFICE";
        tx.payToken = "TOKEN";
        tx.tokenExpDt = Instant.now();
        tx.crdReferenceId = "CRD";
        tx.atmCode = "ATM";
        Mockito.when(repository.listAll()).thenReturn(Uni.createFrom().item(List.of(tx)));

        UniAssertSubscriber<List<PagopaTransactions>> subscriber = service.findAll().subscribe().withSubscriber(UniAssertSubscriber.create());

        subscriber.assertCompleted().assertItem(List.of(tx));
        Mockito.verify(repository).listAll();
    }

    @Test
    void findAll_shouldFail_whenRepositoryFails() {
        PagopaTransactionsRepository repository = Mockito.mock(PagopaTransactionsRepository.class);
        PagopaTransactionsServiceImpl service = new PagopaTransactionsServiceImpl(repository);
        RuntimeException failure = new RuntimeException("error");
        Mockito.when(repository.listAll()).thenReturn(Uni.createFrom().failure(failure));

        UniAssertSubscriber<List<PagopaTransactions>> subscriber = service.findAll().subscribe().withSubscriber(UniAssertSubscriber.create());

        subscriber.assertFailedWith(failure.getClass(), "error");
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

    @Test
    void persist_shouldFail_whenRepositoryFails() {
        PagopaTransactionsRepository repository = Mockito.mock(PagopaTransactionsRepository.class);
        PagopaTransactionsServiceImpl service = new PagopaTransactionsServiceImpl(repository);
        PagopaTransactions tx = new PagopaTransactions();
        RuntimeException failure = new RuntimeException("persist error");
        Mockito.when(repository.persist(tx)).thenReturn(Uni.createFrom().failure(failure));

        UniAssertSubscriber<PagopaTransactions> subscriber = service.persist(tx).subscribe().withSubscriber(UniAssertSubscriber.create());

        subscriber.assertFailedWith(failure.getClass(), "persist error");
        Mockito.verify(repository).persist(tx);
    }
}
