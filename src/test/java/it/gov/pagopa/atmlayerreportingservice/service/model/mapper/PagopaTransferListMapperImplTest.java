package it.gov.pagopa.atmlayerreportingservice.service.model.mapper;

import io.quarkus.test.junit.QuarkusTest;
import it.gov.pagopa.atmlayerreportingservice.service.model.dto.PagopaTransferListDto;
import it.gov.pagopa.atmlayerreportingservice.service.model.entity.PagopaTransactions;
import it.gov.pagopa.atmlayerreportingservice.service.model.entity.PagopaTransferList;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

@QuarkusTest
class PagopaTransferListMapperImplTest {

    private final PagopaTransferListMapper mapper = new PagopaTransferListMapperImpl();

    @Test
    void toEntity_shouldMapAndCreateNestedTransaction() {
        PagopaTransferListDto dto = new PagopaTransferListDto();
        dto.id = 10L;
        dto.transactionId = 20L;
        dto.transferId = 5;
        dto.transferAmount = BigDecimal.valueOf(12);
        dto.transferCro = "CRO";
        dto.flowId = "FLOW";
        dto.pagopaReported = Boolean.FALSE;
        dto.transferExecutionDt = LocalDate.now();
        dto.paFiscalCode = "FISCAL";
        dto.paName = "NAME";
        dto.paIban = "IBAN";
        dto.rmtInfo = "INFO";

        PagopaTransferList entity = mapper.toEntity(dto);

        Assertions.assertNotNull(entity.pagopaTransaction);
        Assertions.assertEquals(dto.transactionId, entity.pagopaTransaction.id);
        Assertions.assertEquals(dto.transferId, entity.transferId);
        Assertions.assertEquals(dto.rmtInfo, entity.rmtInfo);
        Assertions.assertEquals(dto.paName, entity.paName);
    }

    @Test
    void toDto_shouldMapTransactionIdFromNestedEntity() {
        PagopaTransferList entity = new PagopaTransferList();
        PagopaTransactions transaction = new PagopaTransactions();
        transaction.id = 30L;
        entity.pagopaTransaction = transaction;
        entity.id = 40L;
        entity.transferId = 6;
        entity.transferAmount = BigDecimal.ONE;
        entity.transferCro = "CR";
        entity.flowId = "FL";
        entity.pagopaReported = Boolean.TRUE;
        entity.transferExecutionDt = LocalDate.now();
        entity.paFiscalCode = "PF";
        entity.paName = "PN";
        entity.paIban = "PI";
        entity.rmtInfo = "RI";

        PagopaTransferListDto dto = mapper.toDto(entity);

        Assertions.assertEquals(transaction.id, dto.transactionId);
        Assertions.assertEquals(entity.transferId, dto.transferId);
        Assertions.assertEquals(entity.paIban, dto.paIban);
        Assertions.assertEquals(entity.paName, dto.paName);
    }

    @Test
    void toDto_shouldSetTransactionIdNull_whenNestedTransactionMissing() {
        PagopaTransferList entity = new PagopaTransferList();
        entity.id = 50L;
        entity.transferId = 7;
        entity.transferAmount = BigDecimal.ZERO;

        PagopaTransferListDto dto = mapper.toDto(entity);

        Assertions.assertNull(dto.transactionId);
        Assertions.assertEquals(entity.transferId, dto.transferId);
    }

    @Test
    void listMappings_shouldMapBothDirections() {
        PagopaTransferList entity = new PagopaTransferList();
        entity.id = 1L;
        PagopaTransferListDto dto = mapper.toDtoList(List.of(entity)).getFirst();
        Assertions.assertEquals(entity.id, dto.id);

        PagopaTransferListDto source = new PagopaTransferListDto();
        source.id = 2L;
        PagopaTransferList mapped = mapper.toEntityList(List.of(source)).getFirst();
        Assertions.assertEquals(source.id, mapped.id);
    }

    @Test
    void nullInputs_shouldReturnNull() {
        Assertions.assertNull(mapper.toDto(null));
        Assertions.assertNull(mapper.toEntity(null));
        Assertions.assertNull(mapper.toDtoList(null));
        Assertions.assertNull(mapper.toEntityList(null));
    }
}
