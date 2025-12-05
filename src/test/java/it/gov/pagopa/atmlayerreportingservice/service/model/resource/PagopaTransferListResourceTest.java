package it.gov.pagopa.atmlayerreportingservice.service.model.resource;

import io.quarkus.test.junit.QuarkusTest;
import io.smallrye.mutiny.Uni;
import io.smallrye.mutiny.helpers.test.UniAssertSubscriber;
import it.gov.pagopa.atmlayerreportingservice.service.model.dto.PagopaTransferListDto;
import it.gov.pagopa.atmlayerreportingservice.service.model.entity.PagopaTransferList;
import it.gov.pagopa.atmlayerreportingservice.service.model.mapper.PagopaTransferListMapper;
import it.gov.pagopa.atmlayerreportingservice.service.model.service.PagopaTransferListService;
import java.util.List;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;

@QuarkusTest
class PagopaTransferListResourceTest {

    PagopaTransferListService service;
    PagopaTransferListMapper mapper;
    PagopaTransferListResource resource;

    @BeforeEach
    void setUp() {
        service = Mockito.mock(PagopaTransferListService.class);
        mapper = Mockito.mock(PagopaTransferListMapper.class);
        resource = new PagopaTransferListResource();
        resource.service = service;
        resource.mapper = mapper;
    }

    @Test
    void listPagopaTransferLists_shouldReturnMappedDtos_whenServiceReturnsEntities() {
        List<PagopaTransferList> entities = List.of(new PagopaTransferList());
        List<PagopaTransferListDto> dtos = List.of(new PagopaTransferListDto());
        Mockito.when(service.findAll()).thenReturn(Uni.createFrom().item(entities));
        Mockito.when(mapper.toDtoList(entities)).thenReturn(dtos);

        UniAssertSubscriber<List<PagopaTransferListDto>> subscriber = resource.listPagopaTransferLists()
                .subscribe().withSubscriber(UniAssertSubscriber.create());

        subscriber.assertCompleted().assertItem(dtos);
        Mockito.verify(service).findAll();
        Mockito.verify(mapper).toDtoList(entities);
    }

    @Test
    void listPagopaTransferLists_shouldPropagateFailure_whenServiceFails() {
        RuntimeException failure = new RuntimeException("failure");
        Mockito.when(service.findAll()).thenReturn(Uni.createFrom().failure(failure));

        UniAssertSubscriber<List<PagopaTransferListDto>> subscriber = resource.listPagopaTransferLists()
                .subscribe().withSubscriber(UniAssertSubscriber.create());

        subscriber.assertFailedWith(RuntimeException.class, "failure");
        Mockito.verify(service).findAll();
        Mockito.verifyNoInteractions(mapper);
    }

    @Test
    void createPagopaTransferList_shouldReturnDto_whenPersistSucceeds() {
        PagopaTransferListDto input = new PagopaTransferListDto();
        PagopaTransferList entity = new PagopaTransferList();
        PagopaTransferListDto output = new PagopaTransferListDto();
        Mockito.when(mapper.toEntity(input)).thenReturn(entity);
        Mockito.when(service.persist(entity)).thenReturn(Uni.createFrom().item(entity));
        Mockito.when(mapper.toDto(entity)).thenReturn(output);

        UniAssertSubscriber<PagopaTransferListDto> subscriber = resource.createPagopaTransferList(input)
                .subscribe().withSubscriber(UniAssertSubscriber.create());

        subscriber.assertCompleted().assertItem(output);
        Mockito.verify(mapper).toEntity(input);
        Mockito.verify(service).persist(entity);
        Mockito.verify(mapper).toDto(entity);
    }

    @Test
    void createPagopaTransferList_shouldPropagateFailure_whenPersistFails() {
        PagopaTransferListDto input = new PagopaTransferListDto();
        PagopaTransferList entity = new PagopaTransferList();
        RuntimeException failure = new RuntimeException("persist failed");
        Mockito.when(mapper.toEntity(input)).thenReturn(entity);
        Mockito.when(service.persist(entity)).thenReturn(Uni.createFrom().failure(failure));

        UniAssertSubscriber<PagopaTransferListDto> subscriber = resource.createPagopaTransferList(input)
                .subscribe().withSubscriber(UniAssertSubscriber.create());

        subscriber.assertFailedWith(RuntimeException.class, "persist failed");
        Mockito.verify(mapper).toEntity(input);
        Mockito.verify(service).persist(entity);
        Mockito.verify(mapper, Mockito.never()).toDto(Mockito.any());
    }

    @Test
    void listPagopaTransferListsByTransactionId_shouldReturnMappedDtos_whenServiceReturnsEntities() {
        Long transactionId = 123L;
        List<PagopaTransferList> entities = List.of(new PagopaTransferList());
        List<PagopaTransferListDto> dtos = List.of(new PagopaTransferListDto());
        Mockito.when(service.findByTransactionId(transactionId)).thenReturn(Uni.createFrom().item(entities));
        Mockito.when(mapper.toDtoList(entities)).thenReturn(dtos);

        UniAssertSubscriber<List<PagopaTransferListDto>> subscriber = resource.listPagopaTransferListsByTransactionId(transactionId)
                .subscribe().withSubscriber(UniAssertSubscriber.create());

        subscriber.assertCompleted().assertItem(dtos);
        Mockito.verify(service).findByTransactionId(transactionId);
        Mockito.verify(mapper).toDtoList(entities);
    }

    @Test
    void listPagopaTransferListsByTransactionId_shouldPropagateFailure_whenServiceFails() {
        Long transactionId = 456L;
        RuntimeException failure = new RuntimeException("find failed");
        Mockito.when(service.findByTransactionId(transactionId)).thenReturn(Uni.createFrom().failure(failure));

        UniAssertSubscriber<List<PagopaTransferListDto>> subscriber = resource.listPagopaTransferListsByTransactionId(transactionId)
                .subscribe().withSubscriber(UniAssertSubscriber.create());

        subscriber.assertFailedWith(RuntimeException.class, "find failed");
        Mockito.verify(service).findByTransactionId(transactionId);
        Mockito.verifyNoInteractions(mapper);
    }
}
