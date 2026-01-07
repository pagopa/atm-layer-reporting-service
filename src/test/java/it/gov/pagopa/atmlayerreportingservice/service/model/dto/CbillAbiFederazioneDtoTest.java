package it.gov.pagopa.atmlayerreportingservice.service.model.dto;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

public class CbillAbiFederazioneDtoTest {

    @Test
    void shouldRetainAssignedValues() {
        CbillAbiFederazioneDto dto = new CbillAbiFederazioneDto();
        dto.abi = "12345";
        dto.pagopaId = "PID";
        dto.pspFiscalCode = "FISCAL";
        dto.pspChannel = "CH";
        dto.pagopaDirect = Boolean.TRUE;

        Assertions.assertEquals("12345", dto.abi);
        Assertions.assertEquals("PID", dto.pagopaId);
        Assertions.assertEquals("FISCAL", dto.pspFiscalCode);
        Assertions.assertEquals("CH", dto.pspChannel);
        Assertions.assertTrue(dto.pagopaDirect);
    }
}
