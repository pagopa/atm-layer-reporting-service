package it.gov.pagopa.atmlayerreportingservice.service.model.dto;

import io.quarkus.test.junit.QuarkusTest;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

@QuarkusTest
class ErrorResponseDtoTest {

    @Test
    void constructor_shouldCreateDtoWithNullMessage_whenInvoked() {
        ErrorResponseDto dto = new ErrorResponseDto();

        Assertions.assertNull(dto.message);
    }

    @Test
    void message_shouldRetainValue_whenAssignedDirectly() {
        ErrorResponseDto dto = new ErrorResponseDto();

        dto.message = "error";

        Assertions.assertEquals("error", dto.message);
    }
}
