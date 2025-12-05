package it.gov.pagopa.atmlayerreportingservice.service.model.mapper;

import it.gov.pagopa.atmlayerreportingservice.service.model.dto.CbillAbiFederazioneDto;
import it.gov.pagopa.atmlayerreportingservice.service.model.entity.CbillAbiFederazione;
import java.util.List;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

public class CbillAbiFederazioneMapperImplTest {

    private final CbillAbiFederazioneMapper mapper = new CbillAbiFederazioneMapperImpl();

    @Test
    void toDto_shouldMapAllFields() {
        CbillAbiFederazione entity = new CbillAbiFederazione();
        entity.abi = "12345";
        entity.pagopaId = "PAGO";
        entity.pspFiscalCode = "FISCALCODE";
        entity.pspChannel = "CH01";
        entity.pagopaDirect = Boolean.TRUE;

        CbillAbiFederazioneDto dto = mapper.toDto(entity);

        Assertions.assertNotNull(dto);
        Assertions.assertEquals(entity.abi, dto.abi);
        Assertions.assertEquals(entity.pagopaId, dto.pagopaId);
        Assertions.assertEquals(entity.pspFiscalCode, dto.pspFiscalCode);
        Assertions.assertEquals(entity.pspChannel, dto.pspChannel);
        Assertions.assertEquals(entity.pagopaDirect, dto.pagopaDirect);
    }

    @Test
    void toEntity_shouldMapAllFields() {
        CbillAbiFederazioneDto dto = new CbillAbiFederazioneDto();
        dto.abi = "54321";
        dto.pagopaId = "ID2";
        dto.pspFiscalCode = "PSC";
        dto.pspChannel = "CH02";
        dto.pagopaDirect = Boolean.FALSE;

        CbillAbiFederazione entity = mapper.toEntity(dto);

        Assertions.assertNotNull(entity);
        Assertions.assertEquals(dto.abi, entity.abi);
        Assertions.assertEquals(dto.pagopaId, entity.pagopaId);
        Assertions.assertEquals(dto.pspFiscalCode, entity.pspFiscalCode);
        Assertions.assertEquals(dto.pspChannel, entity.pspChannel);
        Assertions.assertEquals(dto.pagopaDirect, entity.pagopaDirect);
    }

    @Test
    void listMappings_shouldMapBothDirections() {
        CbillAbiFederazione entity = new CbillAbiFederazione();
        entity.abi = "11111";
        CbillAbiFederazioneDto dto = mapper.toDtoList(List.of(entity)).getFirst();
        Assertions.assertEquals(entity.abi, dto.abi);

        CbillAbiFederazioneDto source = new CbillAbiFederazioneDto();
        source.abi = "22222";
        CbillAbiFederazione mappedEntity = mapper.toEntityList(List.of(source)).getFirst();
        Assertions.assertEquals(source.abi, mappedEntity.abi);
    }

    @Test
    void nullInputs_shouldReturnNull() {
        Assertions.assertNull(mapper.toDto(null));
        Assertions.assertNull(mapper.toEntity(null));
        Assertions.assertNull(mapper.toDtoList(null));
        Assertions.assertNull(mapper.toEntityList(null));
    }
}
