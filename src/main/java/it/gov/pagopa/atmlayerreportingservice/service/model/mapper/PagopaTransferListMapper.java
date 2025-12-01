package it.gov.pagopa.atmlayerreportingservice.service.model.mapper;

import java.util.List;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.ObjectFactory;

import it.gov.pagopa.atmlayerreportingservice.service.model.dto.PagopaTransferListDto;
import it.gov.pagopa.atmlayerreportingservice.service.model.entity.PagopaTransactions;
import it.gov.pagopa.atmlayerreportingservice.service.model.entity.PagopaTransferList;

@Mapper(componentModel = "jakarta")
public interface PagopaTransferListMapper {

    @Mapping(target = "pagopaTransaction.id", source = "transactionId")
    PagopaTransferList toEntity(PagopaTransferListDto dto);

    @Mapping(target = "transactionId", source = "pagopaTransaction.id")
    PagopaTransferListDto toDto(PagopaTransferList entity);

    List<PagopaTransferListDto> toDtoList(List<PagopaTransferList> entities);
    List<PagopaTransferList> toEntityList(List<PagopaTransferListDto> dtos);

    @ObjectFactory
    default PagopaTransferList createEntity() {
        PagopaTransferList entity = new PagopaTransferList();
        entity.pagopaTransaction = new PagopaTransactions(); // necessario per popolazione nested
        return entity;
    }
}
