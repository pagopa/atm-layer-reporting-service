package it.gov.pagopa.atmlayerreportingservice.service.model.mapper;

import it.gov.pagopa.atmlayerreportingservice.service.model.dto.PagopaTransactionsDto;
import it.gov.pagopa.atmlayerreportingservice.service.model.entity.PagopaTransactions;
import java.util.List;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(componentModel = "jakarta")
public interface PagopaTransactionsMapper {
    @Mapping(target = "transferLists", ignore = true)
    PagopaTransactions toEntity(PagopaTransactionsDto dto);

    PagopaTransactionsDto toDto(PagopaTransactions entity);

    List<PagopaTransactionsDto> toDtoList(List<PagopaTransactions> entities);

    List<PagopaTransactions> toEntityList(List<PagopaTransactionsDto> dtos);
}
