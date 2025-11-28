package it.gov.pagopa.atmlayerreportingservice.service.model.mapper;

import it.gov.pagopa.atmlayerreportingservice.service.model.dto.CbillAbiFederazioneDto;
import it.gov.pagopa.atmlayerreportingservice.service.model.entity.CbillAbiFederazione;
import java.util.List;
import org.mapstruct.Mapper;

@Mapper(componentModel = "jakarta")
public interface CbillAbiFederazioneMapper {
    CbillAbiFederazione toEntity(CbillAbiFederazioneDto dto);

    CbillAbiFederazioneDto toDto(CbillAbiFederazione entity);

    List<CbillAbiFederazioneDto> toDtoList(List<CbillAbiFederazione> entities);

    List<CbillAbiFederazione> toEntityList(List<CbillAbiFederazioneDto> dtos);
}
