package it.gov.pagopa.atmlayerreportingservice.service.model.service.impl;

import io.smallrye.mutiny.Uni;
import io.smallrye.mutiny.helpers.test.UniAssertSubscriber;
import it.gov.pagopa.atmlayerreportingservice.service.model.dto.PagopaTransferListDto;
import it.gov.pagopa.atmlayerreportingservice.service.model.entity.PagopaTransactions;
import it.gov.pagopa.atmlayerreportingservice.service.model.entity.PagopaTransferList;
import it.gov.pagopa.atmlayerreportingservice.service.model.repository.PagopaTransferListRepository;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;

public class PagopaTransferListServiceImplAdditionalTest {

    @Test
    void findAllWithSenderBank_shouldValidateHeader() {
        PagopaTransferListRepository repository = Mockito.mock(PagopaTransferListRepository.class);
        PagopaTransferListServiceImpl service = new PagopaTransferListServiceImpl(repository);

        UniAssertSubscriber<List<PagopaTransferList>> subscriber = service.findAll(null).subscribe().withSubscriber(UniAssertSubscriber.create());

        subscriber.assertFailedWith(IllegalArgumentException.class, "SenderBank header is required");
    }

    @Test
    void updateTransferList_requestNull_shouldFail() {
        PagopaTransferListRepository repository = Mockito.mock(PagopaTransferListRepository.class);
        PagopaTransferListServiceImpl service = new PagopaTransferListServiceImpl(repository);

        UniAssertSubscriber<PagopaTransferList> subscriber = service.updateTransferList("BANK", (PagopaTransferListDto) null).subscribe().withSubscriber(UniAssertSubscriber.create());

        subscriber.assertFailedWith(IllegalArgumentException.class, "Request body is required");
    }

    @Test
    void updateTransferList_notFound_shouldFail() {
        PagopaTransferListRepository repository = Mockito.mock(PagopaTransferListRepository.class);
        PagopaTransferListServiceImpl service = new PagopaTransferListServiceImpl(repository);
        Mockito.when(repository.findByTransactionIdAndTransferId(1L, 2)).thenReturn(Uni.createFrom().item((PagopaTransferList) null));

        UniAssertSubscriber<PagopaTransferList> subscriber = service.updateTransferList("BANK", 1L, 2, BigDecimal.ONE, "CRO", "FLOW", LocalDate.now(), "PF").subscribe().withSubscriber(UniAssertSubscriber.create());

        subscriber.assertFailedWith(IllegalArgumentException.class, "TransferList not found");
    }

    @Test
    void updateTransferList_senderMismatch_shouldFail() {
        PagopaTransferListRepository repository = Mockito.mock(PagopaTransferListRepository.class);
        PagopaTransferListServiceImpl service = new PagopaTransferListServiceImpl(repository);
        PagopaTransferList entity = new PagopaTransferList();
        entity.pagopaTransaction = new PagopaTransactions();
        entity.pagopaTransaction.senderBank = "OTHER";
        Mockito.when(repository.findByTransactionIdAndTransferId(1L, 2)).thenReturn(Uni.createFrom().item(entity));

        UniAssertSubscriber<PagopaTransferList> subscriber = service.updateTransferList("BANK", 1L, 2, BigDecimal.ONE, "CRO", "FLOW", LocalDate.now(), "PF").subscribe().withSubscriber(UniAssertSubscriber.create());

        subscriber.assertFailedWith(IllegalArgumentException.class, "Sender bank mismatch");
    }

    @Test
    void updateTransferList_success_shouldPersistUpdates() {
        PagopaTransferListRepository repository = Mockito.mock(PagopaTransferListRepository.class);
        PagopaTransferListServiceImpl service = new PagopaTransferListServiceImpl(repository);
        PagopaTransferList entity = new PagopaTransferList();
        entity.pagopaTransaction = new PagopaTransactions();
        entity.pagopaTransaction.senderBank = "BANK";
        Mockito.when(repository.findByTransactionIdAndTransferId(1L, 2)).thenReturn(Uni.createFrom().item(entity));
        Mockito.when(repository.persist(entity)).thenReturn(Uni.createFrom().item(entity));

        UniAssertSubscriber<PagopaTransferList> subscriber = service.updateTransferList("BANK", 1L, 2, BigDecimal.TEN, "CRO", "FLOW", LocalDate.of(2024, 1, 1), "PF").subscribe().withSubscriber(UniAssertSubscriber.create());

        subscriber.assertCompleted().assertItem(entity);
        Mockito.verify(repository).persist(entity);
        org.junit.jupiter.api.Assertions.assertEquals(BigDecimal.TEN, entity.transferAmount);
        org.junit.jupiter.api.Assertions.assertEquals("CRO", entity.transferCro);
        org.junit.jupiter.api.Assertions.assertEquals("FLOW", entity.flowId);
        org.junit.jupiter.api.Assertions.assertEquals(LocalDate.of(2024, 1, 1), entity.transferExecutionDt);
        org.junit.jupiter.api.Assertions.assertEquals("PF", entity.paFiscalCode);
    }

    @Test
    void markAsReported_shouldFlagAndPersist() {
        PagopaTransferListRepository repository = Mockito.mock(PagopaTransferListRepository.class);
        PagopaTransferListServiceImpl service = new PagopaTransferListServiceImpl(repository);
        PagopaTransferList entity = new PagopaTransferList();
        entity.id = 5L;
        Mockito.when(repository.findById(5L)).thenReturn(Uni.createFrom().item(entity));
        Mockito.when(repository.persist(entity)).thenReturn(Uni.createFrom().item(entity));

        UniAssertSubscriber<Void> subscriber = service.markAsReported(5L).subscribe().withSubscriber(UniAssertSubscriber.create());

        subscriber.assertCompleted();
        org.junit.jupiter.api.Assertions.assertTrue(entity.pagopaReported);
        Mockito.verify(repository).persist(entity);
    }
}
