package it.gov.pagopa.atmlayerreportingservice.service.model.mapper;

import io.quarkus.test.junit.QuarkusTest;
import it.gov.pagopa.atmlayerreportingservice.service.model.dto.CbillAbiFederazioneDto;
import it.gov.pagopa.atmlayerreportingservice.service.model.entity.CbillAbiFederazione;
import java.util.List;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

@QuarkusTest
class CbillAbiFederazioneMapperImplTest {

    private final CbillAbiFederazioneMapper mapper = new CbillAbiFederazioneMapperImpl();

    @Test
    void toEntity_shouldMapAllFields_whenDtoProvided() {
        CbillAbiFederazioneDto dto = new CbillAbiFederazioneDto();
        dto.abi = "12345";
        dto.pagopaId = "PID";
        dto.pspFiscalCode = "FISCAL";
        dto.pspChannel = "CH";
        dto.pagopaDirect = Boolean.TRUE;

        CbillAbiFederazione entity = mapper.toEntity(dto);

        Assertions.assertNotNull(entity);
        Assertions.assertEquals(dto.abi, entity.abi);
        Assertions.assertEquals(dto.pagopaId, entity.pagopaId);
        Assertions.assertEquals(dto.pspFiscalCode, entity.pspFiscalCode);
        Assertions.assertEquals(dto.pspChannel, entity.pspChannel);
        Assertions.assertEquals(dto.pagopaDirect, entity.pagopaDirect);
    }

    @Test
    void toEntity_shouldReturnNull_whenDtoNull() {
        Assertions.assertNull(mapper.toEntity(null));
    }

    @Test
    void toDto_shouldMapAllFields_whenEntityProvided() {
        CbillAbiFederazione entity = new CbillAbiFederazione();
        entity.abi = "54321";
        entity.pagopaId = "P2";
        entity.pspFiscalCode = "F2";
        entity.pspChannel = "C2";
        entity.pagopaDirect = Boolean.FALSE;

        CbillAbiFederazioneDto dto = mapper.toDto(entity);

        Assertions.assertNotNull(dto);
        Assertions.assertEquals(entity.abi, dto.abi);
        Assertions.assertEquals(entity.pagopaId, dto.pagopaId);
        Assertions.assertEquals(entity.pspFiscalCode, dto.pspFiscalCode);
        Assertions.assertEquals(entity.pspChannel, dto.pspChannel);
        Assertions.assertEquals(entity.pagopaDirect, dto.pagopaDirect);
    }

    @Test
    void toDto_shouldReturnNull_whenEntityNull() {
        Assertions.assertNull(mapper.toDto(null));
    }

    @Test
    void toDtoList_shouldMapItems_whenEntitiesProvided() {
        CbillAbiFederazione entity = new CbillAbiFederazione();
        entity.abi = "1";

        List<CbillAbiFederazioneDto> dtos = mapper.toDtoList(List.of(entity));

        Assertions.assertEquals(1, dtos.size());
        Assertions.assertEquals(entity.abi, dtos.getFirst().abi);
    }

    @Test
    void toDtoList_shouldReturnNull_whenEntitiesNull() {
        Assertions.assertNull(mapper.toDtoList(null));
    }

    @Test
    void toEntityList_shouldMapItems_whenDtosProvided() {
        CbillAbiFederazioneDto dto = new CbillAbiFederazioneDto();
        dto.abi = "2";

        List<CbillAbiFederazione> entities = mapper.toEntityList(List.of(dto));

        Assertions.assertEquals(1, entities.size());
        Assertions.assertEquals(dto.abi, entities.getFirst().abi);
    }

    @Test
    void toEntityList_shouldReturnNull_whenDtosNull() {
        Assertions.assertNull(mapper.toEntityList(null));
    }
}
