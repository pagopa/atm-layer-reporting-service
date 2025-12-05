package it.gov.pagopa.atmlayerreportingservice.service.model.resource;

import io.quarkus.test.junit.QuarkusTest;
import io.smallrye.mutiny.Uni;
import io.smallrye.mutiny.helpers.test.UniAssertSubscriber;
import it.gov.pagopa.atmlayerreportingservice.service.model.dto.PagopaTransactionsDto;
import it.gov.pagopa.atmlayerreportingservice.service.model.entity.PagopaTransactions;
import it.gov.pagopa.atmlayerreportingservice.service.model.mapper.PagopaTransactionsMapper;
import it.gov.pagopa.atmlayerreportingservice.service.model.service.PagopaTransactionsService;
import java.util.List;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;

@QuarkusTest
class PagopaTransactionsResourceTest {

    PagopaTransactionsService service;
    PagopaTransactionsMapper mapper;
    PagopaTransactionsResource resource;

    @BeforeEach
    void setUp() {
        service = Mockito.mock(PagopaTransactionsService.class);
        mapper = Mockito.mock(PagopaTransactionsMapper.class);
        resource = new PagopaTransactionsResource();
        resource.service = service;
        resource.mapper = mapper;
    }

    @Test
    void listPagopaTransactions_shouldReturnMappedDtos_whenServiceReturnsEntities() {
        List<PagopaTransactions> entities = List.of(new PagopaTransactions());
        List<PagopaTransactionsDto> dtos = List.of(new PagopaTransactionsDto());
        Mockito.when(service.findAll()).thenReturn(Uni.createFrom().item(entities));
        Mockito.when(mapper.toDtoList(entities)).thenReturn(dtos);

        UniAssertSubscriber<List<PagopaTransactionsDto>> subscriber = resource.listPagopaTransactions()
                .subscribe().withSubscriber(UniAssertSubscriber.create());

        subscriber.assertCompleted().assertItem(dtos);
        Mockito.verify(service).findAll();
        Mockito.verify(mapper).toDtoList(entities);
    }

    @Test
    void listPagopaTransactions_shouldPropagateFailure_whenServiceFails() {
        RuntimeException failure = new RuntimeException("find failed");
        Mockito.when(service.findAll()).thenReturn(Uni.createFrom().failure(failure));

        UniAssertSubscriber<List<PagopaTransactionsDto>> subscriber = resource.listPagopaTransactions()
                .subscribe().withSubscriber(UniAssertSubscriber.create());

        subscriber.assertFailedWith(RuntimeException.class, "find failed");
        Mockito.verify(service).findAll();
        Mockito.verifyNoInteractions(mapper);
    }

    @Test
    void createPagopaTransaction_shouldReturnDto_whenPersistSucceeds() {
        PagopaTransactionsDto input = new PagopaTransactionsDto();
        PagopaTransactions entity = new PagopaTransactions();
        PagopaTransactionsDto output = new PagopaTransactionsDto();
        Mockito.when(mapper.toEntity(input)).thenReturn(entity);
        Mockito.when(service.persist(entity)).thenReturn(Uni.createFrom().item(entity));
        Mockito.when(mapper.toDto(entity)).thenReturn(output);

        UniAssertSubscriber<PagopaTransactionsDto> subscriber = resource.createPagopaTransaction(input)
                .subscribe().withSubscriber(UniAssertSubscriber.create());

        subscriber.assertCompleted().assertItem(output);
        Mockito.verify(mapper).toEntity(input);
        Mockito.verify(service).persist(entity);
        Mockito.verify(mapper).toDto(entity);
    }

    @Test
    void createPagopaTransaction_shouldPropagateFailure_whenPersistFails() {
        PagopaTransactionsDto input = new PagopaTransactionsDto();
        PagopaTransactions entity = new PagopaTransactions();
        RuntimeException failure = new RuntimeException("persist failed");
        Mockito.when(mapper.toEntity(input)).thenReturn(entity);
        Mockito.when(service.persist(entity)).thenReturn(Uni.createFrom().failure(failure));

        UniAssertSubscriber<PagopaTransactionsDto> subscriber = resource.createPagopaTransaction(input)
                .subscribe().withSubscriber(UniAssertSubscriber.create());

        subscriber.assertFailedWith(RuntimeException.class, "persist failed");
        Mockito.verify(mapper).toEntity(input);
        Mockito.verify(service).persist(entity);
        Mockito.verify(mapper, Mockito.never()).toDto(Mockito.any());
    }
}
