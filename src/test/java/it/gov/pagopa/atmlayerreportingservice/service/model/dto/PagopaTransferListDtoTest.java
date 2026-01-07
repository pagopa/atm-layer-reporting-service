package it.gov.pagopa.atmlayerreportingservice.service.model.dto;

import java.math.BigDecimal;
import java.time.LocalDate;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

public class PagopaTransferListDtoTest {

    @Test
    void shouldRetainAssignedValues() {
        PagopaTransferListDto dto = new PagopaTransferListDto();
        dto.id = 1L;
        dto.transactionId = 2L;
        dto.transferId = 3;
        dto.transferAmount = BigDecimal.TEN;
        dto.transferCro = "CRO";
        dto.flowId = "FLOW";
        dto.pagopaReported = Boolean.TRUE;
        dto.transferExecutionDt = LocalDate.now();
        dto.paFiscalCode = "PF";
        dto.paName = "PN";
        dto.paIban = "IBAN";
        dto.rmtInfo = "INFO";

        Assertions.assertEquals(1L, dto.id);
        Assertions.assertEquals("CRO", dto.transferCro);
        Assertions.assertTrue(dto.pagopaReported);
    }
}
