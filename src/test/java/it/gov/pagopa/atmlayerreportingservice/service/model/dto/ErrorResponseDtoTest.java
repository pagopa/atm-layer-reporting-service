package it.gov.pagopa.atmlayerreportingservice.service.model.dto;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

public class ErrorResponseDtoTest {

    @Test
    void shouldAllowSettingMessage() {
        ErrorResponseDto dto = new ErrorResponseDto();
        dto.message = "error";
        Assertions.assertEquals("error", dto.message);
    }
}
