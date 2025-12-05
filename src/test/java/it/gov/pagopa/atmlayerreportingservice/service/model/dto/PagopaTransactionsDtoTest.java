package it.gov.pagopa.atmlayerreportingservice.service.model.dto;

import java.math.BigDecimal;
import java.time.Instant;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

public class PagopaTransactionsDtoTest {

    @Test
    void shouldRetainAssignedValues() {
        PagopaTransactionsDto dto = new PagopaTransactionsDto();
        dto.id = 1L;
        dto.transactionId = "TID";
        dto.status = "S";
        dto.billAccountId = "BA";
        dto.billAmount = BigDecimal.ONE;
        dto.senderBank = "BANK";
        dto.payDate = Instant.now();
        dto.reported = Boolean.FALSE;
        dto.billerIban = "IBAN";
        dto.billId = "BID";
        dto.billerCommission = BigDecimal.TEN;
        dto.bankCommission = BigDecimal.ONE;
        dto.idempotencyKey = "IDK";
        dto.billerFiscalCode = "CF";
        dto.noticeNumber = "NN";
        dto.retCode = "RC";
        dto.outcomeCode = "OC";
        dto.payDescription = "DESC";
        dto.billerName = "NAME";
        dto.billerOffice = "OFFICE";
        dto.payOptAmount = BigDecimal.valueOf(2);
        dto.payOptType = "OPT";
        dto.payOptDuedate = Instant.now();
        dto.payOptNote = "NOTE";
        dto.payToken = "PTK";
        dto.tokenExpDt = Instant.now();
        dto.crdReferenceId = "CRD";
        dto.atmCode = "ATM";

        Assertions.assertEquals("TID", dto.transactionId);
        Assertions.assertEquals("ATM", dto.atmCode);
        Assertions.assertEquals(BigDecimal.ONE, dto.billAmount);
    }
}
