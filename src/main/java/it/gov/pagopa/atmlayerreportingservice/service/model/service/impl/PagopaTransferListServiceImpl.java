package it.gov.pagopa.atmlayerreportingservice.service.model.service.impl;

import java.util.List;
import java.math.BigDecimal;
import java.time.LocalDate;
import org.jboss.logging.Logger;
import io.smallrye.mutiny.Uni;
import io.quarkus.hibernate.reactive.panache.common.WithSession;
import io.quarkus.hibernate.reactive.panache.common.WithTransaction;
import it.gov.pagopa.atmlayerreportingservice.service.model.entity.PagopaTransferList;
import it.gov.pagopa.atmlayerreportingservice.service.model.repository.PagopaTransferListRepository;
import it.gov.pagopa.atmlayerreportingservice.service.model.service.PagopaTransferListService;
import jakarta.enterprise.context.ApplicationScoped;

@ApplicationScoped
public class PagopaTransferListServiceImpl implements PagopaTransferListService {
    private static final Logger LOG = Logger.getLogger(PagopaTransferListServiceImpl.class);
    private final PagopaTransferListRepository repository;

    public PagopaTransferListServiceImpl(PagopaTransferListRepository repository) {
        this.repository = repository;
    }

    @Override
    @WithSession
    public Uni<List<PagopaTransferList>> findBySenderBank(String senderBank) {
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
        return repository.find("transactionId", transactionId).list();
    }

    @Override
    @WithTransaction
    public Uni<PagopaTransferList> persist(PagopaTransferList entity) {
        return repository.persist(entity);
    }

    @Override
    @WithTransaction
    public Uni<PagopaTransferList> updateTransferList(Long transactionId, Integer transferId, BigDecimal transferAmount, String transferCro, String flowId, LocalDate transferExecutionDt, String paFiscalCode) {
        return repository.findByTransactionIdAndTransferId(transactionId, transferId)
                .onItem().ifNotNull().invoke(entity -> {
                    entity.transferAmount = transferAmount;
                    entity.transferCro = transferCro;
                    entity.flowId = flowId;
                    entity.transferExecutionDt = transferExecutionDt;
                    entity.paFiscalCode = paFiscalCode;
                })
                .call(entity -> repository.persist(entity));
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
