package it.gov.pagopa.atmlayerreportingservice.service.model.resource;

import it.gov.pagopa.atmlayerreportingservice.service.model.dto.PagopaTransferListDto;
import it.gov.pagopa.atmlayerreportingservice.service.model.entity.PagopaTransferList;
import it.gov.pagopa.atmlayerreportingservice.service.model.mapper.PagopaTransferListMapper;
import it.gov.pagopa.atmlayerreportingservice.service.model.service.PagopaTransferListService;
import io.smallrye.mutiny.Uni;
import io.smallrye.mutiny.helpers.test.UniAssertSubscriber;
import java.util.List;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;

public class PagopaTransferListResourceTest {

    private PagopaTransferListResource resource;
    private PagopaTransferListService service;
    private PagopaTransferListMapper mapper;

    @BeforeEach
    void setup() {
        resource = new PagopaTransferListResource();
        service = Mockito.mock(PagopaTransferListService.class);
        mapper = Mockito.mock(PagopaTransferListMapper.class);
        resource.service = service;
        resource.mapper = mapper;
    }

    @Test
    void list_shouldReturnDtos() {
        PagopaTransferList entity = new PagopaTransferList();
        PagopaTransferListDto dto = new PagopaTransferListDto();
        Mockito.when(service.findAll()).thenReturn(Uni.createFrom().item(List.of(entity)));
        Mockito.when(mapper.toDtoList(List.of(entity))).thenReturn(List.of(dto));

        UniAssertSubscriber<List<PagopaTransferListDto>> subscriber = resource.listPagopaTransferLists().subscribe().withSubscriber(UniAssertSubscriber.create());

        subscriber.assertCompleted().assertItem(List.of(dto));
        Mockito.verify(service).findAll();
    }

    @Test
    void list_shouldPropagateFailure() {
        RuntimeException error = new RuntimeException("fail");
        Mockito.when(service.findAll()).thenReturn(Uni.createFrom().failure(error));

        UniAssertSubscriber<List<PagopaTransferListDto>> subscriber = resource.listPagopaTransferLists().subscribe().withSubscriber(UniAssertSubscriber.create());

        subscriber.assertFailedWith(RuntimeException.class, "fail");
    }

    @Test
    void create_shouldPersistAndReturnDto() {
        PagopaTransferListDto dto = new PagopaTransferListDto();
        PagopaTransferList entity = new PagopaTransferList();
        Mockito.when(mapper.toEntity(dto)).thenReturn(entity);
        Mockito.when(service.persist(entity)).thenReturn(Uni.createFrom().item(entity));
        Mockito.when(mapper.toDto(entity)).thenReturn(dto);

        UniAssertSubscriber<PagopaTransferListDto> subscriber = resource.createPagopaTransferList(dto).subscribe().withSubscriber(UniAssertSubscriber.create());

        subscriber.assertCompleted().assertItem(dto);
        Mockito.verify(service).persist(entity);
    }

    @Test
    void create_shouldPropagateFailure() {
        PagopaTransferListDto dto = new PagopaTransferListDto();
        PagopaTransferList entity = new PagopaTransferList();
        RuntimeException error = new RuntimeException("persist");
        Mockito.when(mapper.toEntity(dto)).thenReturn(entity);
        Mockito.when(service.persist(entity)).thenReturn(Uni.createFrom().failure(error));

        UniAssertSubscriber<PagopaTransferListDto> subscriber = resource.createPagopaTransferList(dto).subscribe().withSubscriber(UniAssertSubscriber.create());

        subscriber.assertFailedWith(RuntimeException.class, "persist");
    }

    @Test
    void listByTransaction_shouldReturnDtos() {
        PagopaTransferList entity = new PagopaTransferList();
        PagopaTransferListDto dto = new PagopaTransferListDto();
        Mockito.when(service.findByTransactionId(1L)).thenReturn(Uni.createFrom().item(List.of(entity)));
        Mockito.when(mapper.toDtoList(List.of(entity))).thenReturn(List.of(dto));

        UniAssertSubscriber<List<PagopaTransferListDto>> subscriber = resource.listPagopaTransferListsByTransactionId(1L).subscribe().withSubscriber(UniAssertSubscriber.create());

        subscriber.assertCompleted().assertItem(List.of(dto));
        Mockito.verify(service).findByTransactionId(1L);
    }

    @Test
    void listByTransaction_shouldPropagateFailure() {
        RuntimeException error = new RuntimeException("tx");
        Mockito.when(service.findByTransactionId(2L)).thenReturn(Uni.createFrom().failure(error));

        UniAssertSubscriber<List<PagopaTransferListDto>> subscriber = resource.listPagopaTransferListsByTransactionId(2L).subscribe().withSubscriber(UniAssertSubscriber.create());

        subscriber.assertFailedWith(RuntimeException.class, "tx");
    }
}
