package it.gov.pagopa.atmlayerreportingservice.service.model.mapper;

import it.gov.pagopa.atmlayerreportingservice.service.model.dto.PagopaTransferListDto;
import it.gov.pagopa.atmlayerreportingservice.service.model.entity.PagopaTransferList;
import java.util.List;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(componentModel = "jakarta")
public interface PagopaTransferListMapper {
    @Mapping(target = "pagopaTransaction", ignore = true)
    PagopaTransferList toEntity(PagopaTransferListDto dto);

    PagopaTransferListDto toDto(PagopaTransferList entity);

    List<PagopaTransferListDto> toDtoList(List<PagopaTransferList> entities);

    List<PagopaTransferList> toEntityList(List<PagopaTransferListDto> dtos);
}
