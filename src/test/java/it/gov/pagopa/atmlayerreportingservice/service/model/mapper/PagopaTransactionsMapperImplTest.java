package it.gov.pagopa.atmlayerreportingservice.service.model.mapper;

import it.gov.pagopa.atmlayerreportingservice.service.model.dto.PagopaTransactionsDto;
import it.gov.pagopa.atmlayerreportingservice.service.model.entity.PagopaTransactions;
import java.math.BigDecimal;
import java.time.Instant;
import java.util.List;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

public class PagopaTransactionsMapperImplTest {

    private final PagopaTransactionsMapper mapper = new PagopaTransactionsMapperImpl();

    @Test
    void toEntity_shouldMapAllFields() {
        PagopaTransactionsDto dto = new PagopaTransactionsDto();
        dto.id = 1L;
        dto.transactionId = "TID";
        dto.status = "S";
        dto.billAccountId = "BA";
        dto.billAmount = BigDecimal.TEN;
        dto.senderBank = "BANK";
        dto.payDate = Instant.now();
        dto.reported = Boolean.TRUE;
        dto.billerIban = "IBAN";
        dto.billId = "BID";
        dto.billerCommission = BigDecimal.ONE;
        dto.bankCommission = BigDecimal.ONE;
        dto.idempotencyKey = "IDK";
        dto.billerFiscalCode = "FISCAL";
        dto.noticeNumber = "NOTICE";
        dto.retCode = "RET";
        dto.outcomeCode = "O";
        dto.payDescription = "DESC";
        dto.billerName = "NAME";
        dto.billerOffice = "OFFICE";
        dto.payOptAmount = BigDecimal.valueOf(2);
        dto.payOptType = "OPT";
        dto.payOptDuedate = Instant.now();
        dto.payOptNote = "NOTE";
        dto.payToken = "TOKEN";
        dto.tokenExpDt = Instant.now();
        dto.crdReferenceId = "CRD";
        dto.atmCode = "ATM";

        PagopaTransactions entity = mapper.toEntity(dto);

        Assertions.assertNotNull(entity);
        Assertions.assertEquals(dto.id, entity.id);
        Assertions.assertEquals(dto.transactionId, entity.transactionId);
        Assertions.assertEquals(dto.atmCode, entity.atmCode);
    }

    @Test
    void toDto_shouldMapAllFields() {
        PagopaTransactions entity = new PagopaTransactions();
        entity.id = 2L;
        entity.transactionId = "T2";
        entity.status = "A";
        entity.billAccountId = "BB";
        entity.billAmount = BigDecimal.ONE;
        entity.senderBank = "SB";
        entity.payDate = Instant.now();
        entity.reported = Boolean.FALSE;
        entity.billerIban = "IB";
        entity.billId = "ID";
        entity.billerCommission = BigDecimal.ONE;
        entity.bankCommission = BigDecimal.ONE;
        entity.idempotencyKey = "IK";
        entity.billerFiscalCode = "CF";
        entity.noticeNumber = "NN";
        entity.retCode = "RC";
        entity.outcomeCode = "OC";
        entity.payDescription = "PD";
        entity.billerName = "BN";
        entity.billerOffice = "BO";
        entity.payOptAmount = BigDecimal.valueOf(3);
        entity.payOptType = "PT";
        entity.payOptDuedate = Instant.now();
        entity.payOptNote = "PN";
        entity.payToken = "PTK";
        entity.tokenExpDt = Instant.now();
        entity.crdReferenceId = "CR";
        entity.atmCode = "AC";

        PagopaTransactionsDto dto = mapper.toDto(entity);

        Assertions.assertNotNull(dto);
        Assertions.assertEquals(entity.transactionId, dto.transactionId);
        Assertions.assertEquals(entity.atmCode, dto.atmCode);
    }

    @Test
    void listMappings_shouldMapBothDirections() {
        PagopaTransactions entity = new PagopaTransactions();
        entity.id = 3L;
        PagopaTransactionsDto dto = mapper.toDtoList(List.of(entity)).getFirst();
        Assertions.assertEquals(entity.id, dto.id);

        PagopaTransactionsDto source = new PagopaTransactionsDto();
        source.id = 4L;
        PagopaTransactions mapped = mapper.toEntityList(List.of(source)).getFirst();
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
