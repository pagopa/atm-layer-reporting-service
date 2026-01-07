package it.gov.pagopa.atmlayerreportingservice.service.model.service.impl;

import com.sun.net.httpserver.HttpServer;
import io.quarkus.test.junit.QuarkusTest;
import io.smallrye.mutiny.Uni;
import io.smallrye.mutiny.helpers.test.UniAssertSubscriber;
import it.gov.pagopa.atmlayerreportingservice.service.model.entity.CbillAbiFederazione;
import it.gov.pagopa.atmlayerreportingservice.service.model.entity.PagopaTransactions;
import it.gov.pagopa.atmlayerreportingservice.service.model.entity.PagopaTransferList;
import it.gov.pagopa.atmlayerreportingservice.service.model.service.CbillAbiFederazioneService;
import it.gov.pagopa.atmlayerreportingservice.service.model.service.PagopaTransactionsService;
import it.gov.pagopa.atmlayerreportingservice.service.model.service.PagopaTransferListService;
import java.net.InetSocketAddress;
import java.nio.charset.StandardCharsets;
import java.time.Instant;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.math.BigDecimal;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;

@QuarkusTest
class PagopaReconciliationServiceImplTest {

    @Test
    void schedulePagoPaReconciliation_shouldComplete_whenTransactionsNull() {
        PagopaTransferListService transferListService = Mockito.mock(PagopaTransferListService.class);
        PagopaTransactionsService transactionsService = Mockito.mock(PagopaTransactionsService.class);
        CbillAbiFederazioneService cbillAbiFederazioneService = Mockito.mock(CbillAbiFederazioneService.class);
        PagopaReconciliationServiceImpl service = new PagopaReconciliationServiceImpl(transferListService, transactionsService, cbillAbiFederazioneService);
        Mockito.when(transactionsService.findAll()).thenReturn(Uni.createFrom().item((List<PagopaTransactions>) null));

        UniAssertSubscriber<Void> subscriber = service.schedulePagoPaReconciliation().subscribe().withSubscriber(UniAssertSubscriber.create());

        subscriber.awaitItem().assertCompleted().assertItem(null);
        Mockito.verify(transactionsService).findAll();
        Mockito.verifyNoInteractions(transferListService, cbillAbiFederazioneService);
    }

    @Test
    void schedulePagoPaReconciliation_shouldComplete_whenNoTransfersForBank() {
        PagopaTransferListService transferListService = Mockito.mock(PagopaTransferListService.class);
        PagopaTransactionsService transactionsService = Mockito.mock(PagopaTransactionsService.class);
        CbillAbiFederazioneService cbillAbiFederazioneService = Mockito.mock(CbillAbiFederazioneService.class);
        PagopaReconciliationServiceImpl service = new PagopaReconciliationServiceImpl(transferListService, transactionsService, cbillAbiFederazioneService);
        PagopaTransactions tx = new PagopaTransactions();
        tx.senderBank = "BANK";
        Mockito.when(transactionsService.findAll()).thenReturn(Uni.createFrom().item(List.of(tx)));
        Mockito.when(transferListService.findPayedNotReportedToPagoPAForBank(Mockito.any(LocalDate.class), Mockito.eq("BANK"))).thenReturn(Uni.createFrom().item(List.of()));

        UniAssertSubscriber<Void> subscriber = service.schedulePagoPaReconciliation().subscribe().withSubscriber(UniAssertSubscriber.create());

        subscriber.awaitItem().assertCompleted().assertItem(null);
        Mockito.verify(transferListService).findPayedNotReportedToPagoPAForBank(Mockito.any(LocalDate.class), Mockito.eq("BANK"));
        Mockito.verifyNoInteractions(cbillAbiFederazioneService);
    }

    @Test
    void schedulePagoPaReconciliation_shouldComplete_whenTransferListNull() {
        PagopaTransferListService transferListService = Mockito.mock(PagopaTransferListService.class);
        PagopaTransactionsService transactionsService = Mockito.mock(PagopaTransactionsService.class);
        CbillAbiFederazioneService cbillAbiFederazioneService = Mockito.mock(CbillAbiFederazioneService.class);
        PagopaReconciliationServiceImpl service = new PagopaReconciliationServiceImpl(transferListService, transactionsService, cbillAbiFederazioneService);
        PagopaTransactions tx = new PagopaTransactions();
        tx.senderBank = "BANK";
        Mockito.when(transactionsService.findAll()).thenReturn(Uni.createFrom().item(List.of(tx)));
        Mockito.when(transferListService.findPayedNotReportedToPagoPAForBank(Mockito.any(LocalDate.class), Mockito.eq("BANK"))).thenReturn(Uni.createFrom().item((List<PagopaTransferList>) null));

        UniAssertSubscriber<Void> subscriber = service.schedulePagoPaReconciliation().subscribe().withSubscriber(UniAssertSubscriber.create());

        subscriber.awaitItem().assertCompleted().assertItem(null);
        Mockito.verify(transferListService).findPayedNotReportedToPagoPAForBank(Mockito.any(LocalDate.class), Mockito.eq("BANK"));
        Mockito.verifyNoInteractions(cbillAbiFederazioneService);
    }

    @Test
    void schedulePagoPaReconciliation_shouldFail_whenPspConfigurationMissing() {
        PagopaTransferListService transferListService = Mockito.mock(PagopaTransferListService.class);
        PagopaTransactionsService transactionsService = Mockito.mock(PagopaTransactionsService.class);
        CbillAbiFederazioneService cbillAbiFederazioneService = Mockito.mock(CbillAbiFederazioneService.class);
        PagopaReconciliationServiceImpl service = new PagopaReconciliationServiceImpl(transferListService, transactionsService, cbillAbiFederazioneService);
        PagopaTransactions tx = new PagopaTransactions();
        tx.senderBank = "BANK";
        Mockito.when(transactionsService.findAll()).thenReturn(Uni.createFrom().item(List.of(tx)));
        PagopaTransferList transfer = new PagopaTransferList();
        transfer.id = 1L;
        transfer.transferId = 10;
        transfer.transferAmount = BigDecimal.ONE;
        transfer.transferExecutionDt = LocalDate.now();
        transfer.transferCro = "CRO";
        transfer.flowId = "FLOW";
        transfer.paIban = "IBAN";
        transfer.paFiscalCode = "FISCAL";
        PagopaTransactions linkedTx = new PagopaTransactions();
        linkedTx.senderBank = "BANK";
        linkedTx.noticeNumber = "NOTICE";
        linkedTx.payToken = "TOKEN";
        linkedTx.outcomeCode = "0";
        transfer.pagopaTransaction = linkedTx;
        Mockito.when(transferListService.findPayedNotReportedToPagoPAForBank(Mockito.any(LocalDate.class), Mockito.eq("BANK")))
                .thenReturn(Uni.createFrom().item(new ArrayList<>(List.of(transfer))));
        Mockito.when(cbillAbiFederazioneService.getPspConfiguration("BANK")).thenReturn(Uni.createFrom().item((CbillAbiFederazione) null));

        UniAssertSubscriber<Void> subscriber = service.schedulePagoPaReconciliation().subscribe().withSubscriber(UniAssertSubscriber.create());

        subscriber.assertFailedWith(IllegalArgumentException.class, "PSP configuration not found for ABI BANK");
    }

    @Test
    void schedulePagoPaReconciliation_shouldNotMarkTransfers_whenSendFlussoFails() {
        PagopaTransferListService transferListService = Mockito.mock(PagopaTransferListService.class);
        PagopaTransactionsService transactionsService = Mockito.mock(PagopaTransactionsService.class);
        CbillAbiFederazioneService cbillAbiFederazioneService = Mockito.mock(CbillAbiFederazioneService.class);
        PagopaReconciliationServiceImpl service = new PagopaReconciliationServiceImpl(transferListService, transactionsService, cbillAbiFederazioneService);
        service.pagoPaUrl = "";
        PagopaTransactions tx = new PagopaTransactions();
        tx.senderBank = "BANK";
        Mockito.when(transactionsService.findAll()).thenReturn(Uni.createFrom().item(List.of(tx)));
        PagopaTransferList transfer = new PagopaTransferList();
        transfer.id = 3L;
        transfer.transferId = 1;
        transfer.transferAmount = BigDecimal.TEN;
        transfer.transferExecutionDt = LocalDate.now();
        transfer.transferCro = "CRO1";
        transfer.flowId = "FLOW1";
        transfer.paIban = "IBAN1";
        transfer.paFiscalCode = "FISCAL1";
        PagopaTransactions linkedTx = new PagopaTransactions();
        linkedTx.senderBank = "BANK";
        linkedTx.noticeNumber = "NOTICE";
        linkedTx.payToken = "TOKEN";
        linkedTx.outcomeCode = "0";
        transfer.pagopaTransaction = linkedTx;
        Mockito.when(transferListService.findPayedNotReportedToPagoPAForBank(Mockito.any(LocalDate.class), Mockito.eq("BANK")))
                .thenReturn(Uni.createFrom().item(new ArrayList<>(List.of(transfer))));
        CbillAbiFederazione config = new CbillAbiFederazione();
        config.pagopaId = "PAGOPA";
        config.pspFiscalCode = "PSPFC";
        config.pspChannel = "CHAN";
        config.password = "pwd";
        Mockito.when(cbillAbiFederazioneService.getPspConfiguration("BANK")).thenReturn(Uni.createFrom().item(config));
        Mockito.when(transferListService.markAsReported(Mockito.anyLong())).thenReturn(Uni.createFrom().voidItem());

        UniAssertSubscriber<Void> subscriber = service.schedulePagoPaReconciliation().subscribe().withSubscriber(UniAssertSubscriber.create());

        subscriber.awaitItem().assertCompleted().assertItem(null);
        Mockito.verify(transferListService, Mockito.never()).markAsReported(Mockito.anyLong());
    }

    @Test
    void schedulePagoPaReconciliation_shouldMarkTransfers_whenRendicontazioneSucceeds() throws Exception {
        PagopaTransferListService transferListService = Mockito.mock(PagopaTransferListService.class);
        PagopaTransactionsService transactionsService = Mockito.mock(PagopaTransactionsService.class);
        CbillAbiFederazioneService cbillAbiFederazioneService = Mockito.mock(CbillAbiFederazioneService.class);
        PagopaReconciliationServiceImpl service = new PagopaReconciliationServiceImpl(transferListService, transactionsService, cbillAbiFederazioneService);
        service.pagoPaSubscriptionKey = "sub";
        service.pagoPaConnectionTimeout = 1000L;
        service.pagoPaReadTimeout = 1000L;
        HttpServer server = HttpServer.create(new InetSocketAddress(0), 0);
        server.createContext("/rend", exchange -> {
            byte[] body = "OK".getBytes(StandardCharsets.UTF_8);
            exchange.sendResponseHeaders(200, body.length);
            exchange.getResponseBody().write(body);
            exchange.close();
        });
        server.start();
        try {
            service.pagoPaUrl = "http://localhost:" + server.getAddress().getPort() + "/rend";
            PagopaTransactions tx = new PagopaTransactions();
            tx.senderBank = "BANK";
            Mockito.when(transactionsService.findAll()).thenReturn(Uni.createFrom().item(List.of(tx)));
            LocalDate execution = LocalDate.now();
            PagopaTransferList t1 = new PagopaTransferList();
            t1.id = 1L;
            t1.transferId = 1;
            t1.transferAmount = null;
            t1.transferCro = null;
            t1.flowId = null;
            t1.paIban = "IBAN";
            t1.paFiscalCode = "FISCAL";
            t1.transferExecutionDt = execution;
            PagopaTransactions transferTx1 = new PagopaTransactions();
            transferTx1.senderBank = "BANK";
            transferTx1.noticeNumber = "NOTICE1";
            transferTx1.payToken = "TOKEN1";
            transferTx1.outcomeCode = "0";
            transferTx1.payDate = null;
            t1.pagopaTransaction = transferTx1;
            PagopaTransferList t2 = new PagopaTransferList();
            t2.id = 2L;
            t2.transferId = 2;
            t2.transferAmount = BigDecimal.TEN;
            t2.transferCro = null;
            t2.flowId = null;
            t2.paIban = "IBAN";
            t2.paFiscalCode = "FISCAL";
            t2.transferExecutionDt = execution;
            PagopaTransactions transferTx2 = new PagopaTransactions();
            transferTx2.senderBank = "BANK";
            transferTx2.noticeNumber = "NOTICE2";
            transferTx2.payToken = "TOKEN2";
            transferTx2.outcomeCode = "1";
            transferTx2.payDate = Instant.now();
            t2.pagopaTransaction = transferTx2;
            List<PagopaTransferList> transfers = new ArrayList<>();
            transfers.add(t1);
            transfers.add(t2);
            Mockito.when(transferListService.findPayedNotReportedToPagoPAForBank(Mockito.any(LocalDate.class), Mockito.eq("BANK")))
                    .thenReturn(Uni.createFrom().item(transfers));
            CbillAbiFederazione config = new CbillAbiFederazione();
            config.pagopaId = "PAGOPAID";
            config.pspFiscalCode = "PSPFC";
            config.pspChannel = "CH";
            config.password = "pwd";
            Mockito.when(cbillAbiFederazioneService.getPspConfiguration("BANK")).thenReturn(Uni.createFrom().item(config));
            Mockito.when(transferListService.markAsReported(Mockito.anyLong())).thenReturn(Uni.createFrom().voidItem());

            UniAssertSubscriber<Void> subscriber = service.schedulePagoPaReconciliation().subscribe().withSubscriber(UniAssertSubscriber.create());

            subscriber.awaitItem().assertCompleted().assertItem(null);
            Mockito.verify(transferListService).markAsReported(1L);
            Mockito.verify(transferListService).markAsReported(2L);
        } finally {
            server.stop(0);
        }
    }

    @Test
    void schedulePagoPaReconciliation_shouldFail_whenTransferTransactionMissing() {
        PagopaTransferListService transferListService = Mockito.mock(PagopaTransferListService.class);
        PagopaTransactionsService transactionsService = Mockito.mock(PagopaTransactionsService.class);
        CbillAbiFederazioneService cbillAbiFederazioneService = Mockito.mock(CbillAbiFederazioneService.class);
        PagopaReconciliationServiceImpl service = new PagopaReconciliationServiceImpl(transferListService, transactionsService, cbillAbiFederazioneService);
        PagopaTransactions tx = new PagopaTransactions();
        tx.senderBank = "BANK";
        Mockito.when(transactionsService.findAll()).thenReturn(Uni.createFrom().item(List.of(tx)));
        PagopaTransferList transfer = new PagopaTransferList();
        transfer.id = 99L;
        transfer.transferId = 1;
        transfer.transferAmount = BigDecimal.ONE;
        transfer.transferExecutionDt = LocalDate.now();
        transfer.transferCro = "CRO";
        transfer.flowId = "FLOW";
        transfer.paIban = "IBAN";
        transfer.paFiscalCode = "FISCAL";
        Mockito.when(transferListService.findPayedNotReportedToPagoPAForBank(Mockito.any(LocalDate.class), Mockito.eq("BANK")))
                .thenReturn(Uni.createFrom().item(new ArrayList<>(List.of(transfer))));
        CbillAbiFederazione config = new CbillAbiFederazione();
        config.pagopaId = "PAGOPA";
        config.pspFiscalCode = "PSPFC";
        config.pspChannel = "CH";
        config.password = "pwd";
        Mockito.when(cbillAbiFederazioneService.getPspConfiguration("BANK")).thenReturn(Uni.createFrom().item(config));

        UniAssertSubscriber<Void> subscriber = service.schedulePagoPaReconciliation().subscribe().withSubscriber(UniAssertSubscriber.create());

        subscriber.assertFailedWith(IllegalArgumentException.class, "Missing transaction for transfer 99");
    }
}
