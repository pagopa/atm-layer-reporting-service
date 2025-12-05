package it.gov.pagopa.atmlayerreportingservice.service.model.mapper;

import io.quarkus.test.junit.QuarkusTest;
import it.gov.pagopa.atmlayerreportingservice.service.model.dto.PagopaTransactionsDto;
import it.gov.pagopa.atmlayerreportingservice.service.model.entity.PagopaTransactions;
import java.math.BigDecimal;
import java.time.Instant;
import java.util.List;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

@QuarkusTest
class PagopaTransactionsMapperImplTest {

    private final PagopaTransactionsMapper mapper = new PagopaTransactionsMapperImpl();

    @Test
    void toEntity_shouldMapAllFields_whenDtoProvided() {
        PagopaTransactionsDto dto = new PagopaTransactionsDto();
        dto.id = 1L;
        dto.transactionId = "TID";
        dto.status = "S";
        dto.billAccountId = "BA";
        dto.billAmount = BigDecimal.TEN;
        dto.senderBank = "BANK";
        dto.payDate = Instant.parse("2024-01-01T10:00:00Z");
        dto.reported = Boolean.TRUE;
        dto.billerIban = "IBAN";
        dto.billId = "BID";
        dto.billerCommission = BigDecimal.ONE;
        dto.bankCommission = BigDecimal.valueOf(2);
        dto.idempotencyKey = "IDK";
        dto.billerFiscalCode = "FISCAL";
        dto.noticeNumber = "NOTICE";
        dto.retCode = "RET";
        dto.outcomeCode = "O";
        dto.payDescription = "DESC";
        dto.billerName = "NAME";
        dto.billerOffice = "OFFICE";
        dto.payOptAmount = BigDecimal.valueOf(3);
        dto.payOptType = "OPT";
        dto.payOptDuedate = Instant.parse("2024-01-02T10:00:00Z");
        dto.payOptNote = "NOTE";
        dto.payToken = "TOKEN";
        dto.tokenExpDt = Instant.parse("2024-01-03T10:00:00Z");
        dto.crdReferenceId = "CRD";
        dto.atmCode = "ATM";

        PagopaTransactions entity = mapper.toEntity(dto);

        Assertions.assertNotNull(entity);
        Assertions.assertEquals(dto.id, entity.id);
        Assertions.assertEquals(dto.transactionId, entity.transactionId);
        Assertions.assertEquals(dto.status, entity.status);
        Assertions.assertEquals(dto.billAccountId, entity.billAccountId);
        Assertions.assertEquals(dto.billAmount, entity.billAmount);
        Assertions.assertEquals(dto.senderBank, entity.senderBank);
        Assertions.assertEquals(dto.payDate, entity.payDate);
        Assertions.assertEquals(dto.reported, entity.reported);
        Assertions.assertEquals(dto.billerIban, entity.billerIban);
        Assertions.assertEquals(dto.billId, entity.billId);
        Assertions.assertEquals(dto.billerCommission, entity.billerCommission);
        Assertions.assertEquals(dto.bankCommission, entity.bankCommission);
        Assertions.assertEquals(dto.idempotencyKey, entity.idempotencyKey);
        Assertions.assertEquals(dto.billerFiscalCode, entity.billerFiscalCode);
        Assertions.assertEquals(dto.noticeNumber, entity.noticeNumber);
        Assertions.assertEquals(dto.retCode, entity.retCode);
        Assertions.assertEquals(dto.outcomeCode, entity.outcomeCode);
        Assertions.assertEquals(dto.payDescription, entity.payDescription);
        Assertions.assertEquals(dto.billerName, entity.billerName);
        Assertions.assertEquals(dto.billerOffice, entity.billerOffice);
        Assertions.assertEquals(dto.payOptAmount, entity.payOptAmount);
        Assertions.assertEquals(dto.payOptType, entity.payOptType);
        Assertions.assertEquals(dto.payOptDuedate, entity.payOptDuedate);
        Assertions.assertEquals(dto.payOptNote, entity.payOptNote);
        Assertions.assertEquals(dto.payToken, entity.payToken);
        Assertions.assertEquals(dto.tokenExpDt, entity.tokenExpDt);
        Assertions.assertEquals(dto.crdReferenceId, entity.crdReferenceId);
        Assertions.assertEquals(dto.atmCode, entity.atmCode);
    }

    @Test
    void toEntity_shouldReturnNull_whenDtoNull() {
        Assertions.assertNull(mapper.toEntity(null));
    }

    @Test
    void toDto_shouldMapAllFields_whenEntityProvided() {
        PagopaTransactions entity = new PagopaTransactions();
        entity.id = 2L;
        entity.transactionId = "T2";
        entity.status = "A";
        entity.billAccountId = "BB";
        entity.billAmount = BigDecimal.ONE;
        entity.senderBank = "SB";
        entity.payDate = Instant.parse("2024-02-01T11:00:00Z");
        entity.reported = Boolean.FALSE;
        entity.billerIban = "IB";
        entity.billId = "ID";
        entity.billerCommission = BigDecimal.valueOf(5);
        entity.bankCommission = BigDecimal.valueOf(6);
        entity.idempotencyKey = "IK";
        entity.billerFiscalCode = "CF";
        entity.noticeNumber = "NN";
        entity.retCode = "RC";
        entity.outcomeCode = "OC";
        entity.payDescription = "PD";
        entity.billerName = "BN";
        entity.billerOffice = "BO";
        entity.payOptAmount = BigDecimal.valueOf(7);
        entity.payOptType = "PT";
        entity.payOptDuedate = Instant.parse("2024-02-02T11:00:00Z");
        entity.payOptNote = "PN";
        entity.payToken = "PTK";
        entity.tokenExpDt = Instant.parse("2024-02-03T11:00:00Z");
        entity.crdReferenceId = "CR";
        entity.atmCode = "AC";

        PagopaTransactionsDto dto = mapper.toDto(entity);

        Assertions.assertNotNull(dto);
        Assertions.assertEquals(entity.id, dto.id);
        Assertions.assertEquals(entity.transactionId, dto.transactionId);
        Assertions.assertEquals(entity.status, dto.status);
        Assertions.assertEquals(entity.billAccountId, dto.billAccountId);
        Assertions.assertEquals(entity.billAmount, dto.billAmount);
        Assertions.assertEquals(entity.senderBank, dto.senderBank);
        Assertions.assertEquals(entity.payDate, dto.payDate);
        Assertions.assertEquals(entity.reported, dto.reported);
        Assertions.assertEquals(entity.billerIban, dto.billerIban);
        Assertions.assertEquals(entity.billId, dto.billId);
        Assertions.assertEquals(entity.billerCommission, dto.billerCommission);
        Assertions.assertEquals(entity.bankCommission, dto.bankCommission);
        Assertions.assertEquals(entity.idempotencyKey, dto.idempotencyKey);
        Assertions.assertEquals(entity.billerFiscalCode, dto.billerFiscalCode);
        Assertions.assertEquals(entity.noticeNumber, dto.noticeNumber);
        Assertions.assertEquals(entity.retCode, dto.retCode);
        Assertions.assertEquals(entity.outcomeCode, dto.outcomeCode);
        Assertions.assertEquals(entity.payDescription, dto.payDescription);
        Assertions.assertEquals(entity.billerName, dto.billerName);
        Assertions.assertEquals(entity.billerOffice, dto.billerOffice);
        Assertions.assertEquals(entity.payOptAmount, dto.payOptAmount);
        Assertions.assertEquals(entity.payOptType, dto.payOptType);
        Assertions.assertEquals(entity.payOptDuedate, dto.payOptDuedate);
        Assertions.assertEquals(entity.payOptNote, dto.payOptNote);
        Assertions.assertEquals(entity.payToken, dto.payToken);
        Assertions.assertEquals(entity.tokenExpDt, dto.tokenExpDt);
        Assertions.assertEquals(entity.crdReferenceId, dto.crdReferenceId);
        Assertions.assertEquals(entity.atmCode, dto.atmCode);
    }

    @Test
    void toDto_shouldReturnNull_whenEntityNull() {
        Assertions.assertNull(mapper.toDto(null));
    }

    @Test
    void toDtoList_shouldMapItems_whenEntitiesProvided() {
        PagopaTransactions entity = new PagopaTransactions();
        entity.id = 3L;

        List<PagopaTransactionsDto> dtos = mapper.toDtoList(List.of(entity));

        Assertions.assertEquals(1, dtos.size());
        Assertions.assertEquals(entity.id, dtos.getFirst().id);
    }

    @Test
    void toDtoList_shouldReturnNull_whenEntitiesNull() {
        Assertions.assertNull(mapper.toDtoList(null));
    }

    @Test
    void toEntityList_shouldMapItems_whenDtosProvided() {
        PagopaTransactionsDto dto = new PagopaTransactionsDto();
        dto.id = 4L;

        List<PagopaTransactions> entities = mapper.toEntityList(List.of(dto));

        Assertions.assertEquals(1, entities.size());
        Assertions.assertEquals(dto.id, entities.getFirst().id);
    }

    @Test
    void toEntityList_shouldReturnNull_whenDtosNull() {
        Assertions.assertNull(mapper.toEntityList(null));
    }
}
