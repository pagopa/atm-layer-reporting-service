package it.gov.pagopa.atmlayerreportingservice.service.model.resource;

import io.quarkus.test.junit.QuarkusTest;
import io.smallrye.mutiny.Uni;
import io.smallrye.mutiny.helpers.test.UniAssertSubscriber;
import it.gov.pagopa.atmlayerreportingservice.service.model.dto.PagopaTransactionsDto;
import it.gov.pagopa.atmlayerreportingservice.service.model.entity.PagopaTransactions;
import it.gov.pagopa.atmlayerreportingservice.service.model.mapper.PagopaTransactionsMapper;
import it.gov.pagopa.atmlayerreportingservice.service.model.service.PagopaTransactionsService;
import java.util.List;

import jakarta.ws.rs.core.Response;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;

import static org.junit.jupiter.api.Assertions.assertEquals;

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
        String senderBank = "12345";
        List<PagopaTransactions> entities = List.of(new PagopaTransactions());
        List<PagopaTransactionsDto> dtos = List.of(new PagopaTransactionsDto());
        Mockito.when(service.findAllBySenderBank(senderBank)).thenReturn(Uni.createFrom().item(entities));
        Mockito.when(mapper.toDtoList(entities)).thenReturn(dtos);

        UniAssertSubscriber<Response> subscriber = resource.listPagopaTransactions(senderBank)
                .subscribe().withSubscriber(UniAssertSubscriber.create());

        subscriber.assertCompleted();
        Response response = subscriber.getItem();
        assertEquals(response.getStatus(),Response.Status.OK.getStatusCode());
        assertEquals(response.getEntity(), dtos);
        Mockito.verify(service).findAllBySenderBank(senderBank);
        Mockito.verify(mapper).toDtoList(entities);
    }

    @Test
    void listPagopaTransactions_shouldReturnEmptyList_whenServiceReturnsNoEntities() {
        String senderBank = "12345";
        List<PagopaTransactions> entities = List.of();
        List<PagopaTransactionsDto> dtos = List.of();
        Mockito.when(service.findAllBySenderBank(senderBank)).thenReturn(Uni.createFrom().item(entities));
        Mockito.when(mapper.toDtoList(entities)).thenReturn(dtos);

        UniAssertSubscriber<Response> subscriber = resource.listPagopaTransactions(senderBank)
                .subscribe().withSubscriber(UniAssertSubscriber.create());

        subscriber.assertCompleted();
        Response response = subscriber.getItem();
        assertEquals(response.getStatus(),Response.Status.OK.getStatusCode());
        assertEquals(response.getEntity(), dtos);
        Mockito.verify(service).findAllBySenderBank(senderBank);
        Mockito.verify(mapper).toDtoList(entities);
    }

    @Test
    void listPagopaTransactions_shouldReturnEmptyList_whenSenderBankHasNoRecords() {
        String senderBank = "NON_EXISTENT_BANK";
        List<PagopaTransactions> entities = List.of();
        List<PagopaTransactionsDto> dtos = List.of();
        Mockito.when(service.findAllBySenderBank(senderBank)).thenReturn(Uni.createFrom().item(entities));
        Mockito.when(mapper.toDtoList(entities)).thenReturn(dtos);
        UniAssertSubscriber<Response> subscriber = resource.listPagopaTransactions(senderBank)
                .subscribe().withSubscriber(UniAssertSubscriber.create());
        subscriber.assertCompleted();
        Response response = subscriber.getItem();
        assertEquals(response.getStatus(),Response.Status.OK.getStatusCode());
        assertEquals(response.getEntity(), dtos);
        Mockito.verify(service).findAllBySenderBank(senderBank);
        Mockito.verify(mapper).toDtoList(entities);
    }

    @Test
    void listPagopaTransactions_shouldPropagateFailure_whenServiceFails() {
        String senderBank = "12345";
        RuntimeException failure = new RuntimeException("find failed");
        Mockito.when(service.findAllBySenderBank(senderBank)).thenReturn(Uni.createFrom().failure(failure));

        UniAssertSubscriber<Response> subscriber = resource.listPagopaTransactions(senderBank)
                .subscribe().withSubscriber(UniAssertSubscriber.create());

        subscriber.assertCompleted();
        Mockito.verify(service).findAllBySenderBank(senderBank);
        Mockito.verifyNoInteractions(mapper);
    }

    @Test
    void listPagopaTransactions_shouldReturnBadRequest_whenSenderBankIsNull() {
        UniAssertSubscriber<Response> subscriber = resource.listPagopaTransactions(null)
                .subscribe().withSubscriber(UniAssertSubscriber.create());

        subscriber.assertCompleted();
        Response response = subscriber.getItem();
        assertEquals(Response.Status.BAD_REQUEST.getStatusCode(), response.getStatus());
        Mockito.verifyNoInteractions(service);
        Mockito.verifyNoInteractions(mapper);
    }

    @Test
    void listPagopaTransactions_shouldReturnBadRequest_whenSenderBankIsEmpty() {
        UniAssertSubscriber<Response> subscriber = resource.listPagopaTransactions("")
                .subscribe().withSubscriber(UniAssertSubscriber.create());

        subscriber.assertCompleted();
        Response response = subscriber.getItem();
        assertEquals(Response.Status.BAD_REQUEST.getStatusCode(), response.getStatus());
        Mockito.verifyNoInteractions(service);
        Mockito.verifyNoInteractions(mapper);
    }

    @Test
    void listPagopaTransactions_shouldReturnBadRequest_whenSenderBankIsBlank() {
        UniAssertSubscriber<Response> subscriber = resource.listPagopaTransactions("   ")
                .subscribe().withSubscriber(UniAssertSubscriber.create());

        subscriber.assertCompleted();
        Response response = subscriber.getItem();
        assertEquals(Response.Status.BAD_REQUEST.getStatusCode(), response.getStatus());
        Mockito.verifyNoInteractions(service);
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
