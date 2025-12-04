package it.gov.pagopa.atmlayerreportingservice.service.model.service.impl;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;
import io.quarkus.hibernate.reactive.panache.common.WithSession;
import io.quarkus.hibernate.reactive.panache.common.WithTransaction;
import io.smallrye.mutiny.Uni;
import it.gov.pagopa.atmlayerreportingservice.service.model.dto.PagopaTransferListDto;
import it.gov.pagopa.atmlayerreportingservice.service.model.entity.PagopaTransferList;
import it.gov.pagopa.atmlayerreportingservice.service.model.repository.PagopaTransferListRepository;
import it.gov.pagopa.atmlayerreportingservice.service.model.service.PagopaTransferListService;
import jakarta.enterprise.context.ApplicationScoped;

@ApplicationScoped
public class PagopaTransferListServiceImpl implements PagopaTransferListService {
    private final PagopaTransferListRepository repository;

    public PagopaTransferListServiceImpl(PagopaTransferListRepository repository) {
        this.repository = repository;
    }

    @Override
    @WithSession
    public Uni<List<PagopaTransferList>> findAll(String senderBank) {
        if (senderBank == null || senderBank.isBlank()) {
            return Uni.createFrom().failure(new IllegalArgumentException("SenderBank header is required"));
        }
        return repository.findBySenderBank(senderBank);
    }

    @Override
    @WithSession
    public Uni<List<PagopaTransferList>> findBySenderBank(String senderBank) {
        return repository.findBySenderBank(senderBank);
    }

    @Override
    @WithSession
    public Uni<List<PagopaTransferList>> findAllBySenderBank(String senderBank) {
        return repository.findBySenderBank(senderBank);
    }

    @Override
    @WithSession
    public Uni<List<PagopaTransferList>> findAll() {
        return repository.listAll();
    }

    @Override
    @WithSession
    public Uni<List<PagopaTransferList>> findByTransactionId(Long transactionId) {
        return repository.find("pagopaTransaction.id", transactionId).list();
    }

    @Override
    @WithTransaction
    public Uni<PagopaTransferList> persist(PagopaTransferList entity) {
        return repository.persist(entity);
    }

    @Override
    @WithTransaction
    public Uni<PagopaTransferList> updateTransferList(String senderBank, PagopaTransferListDto request) {
        if (request == null) {
            return Uni.createFrom().failure(new IllegalArgumentException("Request body is required"));
        }
        return updateTransferList(senderBank, request.transactionId, request.transferId, request.transferAmount, request.transferCro, request.flowId, request.transferExecutionDt, request.paFiscalCode);
    }

    @Override
    @WithTransaction
    public Uni<PagopaTransferList> updateTransferList(String senderBank, Long transactionId, Integer transferId, BigDecimal transferAmount, String transferCro, String flowId, LocalDate transferExecutionDt, String paFiscalCode) {
        return repository.findByTransactionIdAndTransferId(transactionId, transferId)
                .flatMap(entity -> {
                    if (entity == null) {
                        return Uni.createFrom().failure(new IllegalArgumentException("TransferList not found"));
                    }
                    if (entity.pagopaTransaction == null || senderBank == null || !senderBank.equals(entity.pagopaTransaction.senderBank)) {
                        return Uni.createFrom().failure(new IllegalArgumentException("Sender bank mismatch"));
                    }
                    entity.transferAmount = transferAmount;
                    entity.transferCro = transferCro;
                    entity.flowId = flowId;
                    entity.transferExecutionDt = transferExecutionDt;
                    entity.paFiscalCode = paFiscalCode;
                    return repository.persist(entity);
                });
    }

    @Override
    @WithSession
    public Uni<List<PagopaTransferList>> findPayedNotReportedToPagoPAForBank(LocalDate toDate, String senderBank) {
        return repository.findPayedNotReportedToPagoPAForBank(toDate, senderBank);
    }

    @Override
    @WithTransaction
    public Uni<Void> markAsReported(Long id) {
        return repository.findById(id)
                .onItem().ifNotNull().invoke(entity -> entity.pagopaReported = Boolean.TRUE)
                .call(entity -> repository.persist(entity))
                .replaceWithVoid();
    }
}
