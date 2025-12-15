package it.gov.pagopa.atmlayerreportingservice.service.model.resource;

import io.quarkus.test.junit.QuarkusTest;
import io.smallrye.mutiny.Uni;
import io.smallrye.mutiny.helpers.test.UniAssertSubscriber;
import it.gov.pagopa.atmlayerreportingservice.service.model.dto.CbillAbiFederazioneDto;
import it.gov.pagopa.atmlayerreportingservice.service.model.dto.ErrorResponseDto;
import it.gov.pagopa.atmlayerreportingservice.service.model.entity.CbillAbiFederazione;
import it.gov.pagopa.atmlayerreportingservice.service.model.mapper.CbillAbiFederazioneMapper;
import it.gov.pagopa.atmlayerreportingservice.service.model.service.CbillAbiFederazioneService;
import java.util.List;
import jakarta.ws.rs.core.Response;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;

@QuarkusTest
class CbillAbiFederazioneResourceTest {

    CbillAbiFederazioneService service;
    CbillAbiFederazioneMapper mapper;
    CbillAbiFederazioneResource resource;

    @BeforeEach
    void setUp() {
        service = Mockito.mock(CbillAbiFederazioneService.class);
        mapper = Mockito.mock(CbillAbiFederazioneMapper.class);
        resource = new CbillAbiFederazioneResource();
        resource.service = service;
        resource.mapper = mapper;
    }

    @Test
    void listCbillAbiFederazione_shouldReturnMappedDtos_whenServiceReturnsEntities() {
        List<CbillAbiFederazione> entities = List.of(new CbillAbiFederazione());
        List<CbillAbiFederazioneDto> dtos = List.of(new CbillAbiFederazioneDto());
        Mockito.when(service.findAll()).thenReturn(Uni.createFrom().item(entities));
        Mockito.when(mapper.toDtoList(entities)).thenReturn(dtos);

        UniAssertSubscriber<List<CbillAbiFederazioneDto>> subscriber = resource.listCbillAbiFederazione()
                .subscribe().withSubscriber(UniAssertSubscriber.create());

        subscriber.assertCompleted().assertItem(dtos);
        Mockito.verify(service).findAll();
        Mockito.verify(mapper).toDtoList(entities);
    }

    @Test
    void listCbillAbiFederazione_shouldPropagateFailure_whenServiceFails() {
        RuntimeException failure = new RuntimeException("find all failed");
        Mockito.when(service.findAll()).thenReturn(Uni.createFrom().failure(failure));

        UniAssertSubscriber<List<CbillAbiFederazioneDto>> subscriber = resource.listCbillAbiFederazione()
                .subscribe().withSubscriber(UniAssertSubscriber.create());

        subscriber.assertFailedWith(RuntimeException.class, "find all failed");
        Mockito.verify(service).findAll();
        Mockito.verifyNoInteractions(mapper);
    }

    @Test
    void getCbillAbiFederazioneByAbi_shouldReturnDto_whenEntityFound() {
        String abi = "12345";
        CbillAbiFederazione entity = new CbillAbiFederazione();
        CbillAbiFederazioneDto dto = new CbillAbiFederazioneDto();
        Mockito.when(service.findByAbi(abi)).thenReturn(Uni.createFrom().item(entity));
        Mockito.when(mapper.toDto(entity)).thenReturn(dto);

        UniAssertSubscriber<Response> subscriber = resource.getCbillAbiFederazioneByAbi(abi)
                .subscribe().withSubscriber(UniAssertSubscriber.create());

        Response response = subscriber.assertCompleted().getItem();
        Assertions.assertEquals(Response.Status.OK.getStatusCode(), response.getStatus());
        Assertions.assertEquals(dto, response.getEntity());
        Mockito.verify(service).findByAbi(abi);
        Mockito.verify(mapper).toDto(entity);
    }

    @Test
    void getCbillAbiFederazioneByAbi_shouldReturnBadRequest_whenServiceValidationFails() {
        String abi = " ";
        IllegalArgumentException failure = new IllegalArgumentException("ABI is required");
        Mockito.when(service.findByAbi(abi)).thenReturn(Uni.createFrom().failure(failure));

        UniAssertSubscriber<Response> subscriber = resource.getCbillAbiFederazioneByAbi(abi)
                .subscribe().withSubscriber(UniAssertSubscriber.create());

        Response response = subscriber.assertCompleted().getItem();
        Assertions.assertEquals(Response.Status.BAD_REQUEST.getStatusCode(), response.getStatus());
        ErrorResponseDto error = (ErrorResponseDto) response.getEntity();
        Assertions.assertEquals("ABI is required", error.message);
        Mockito.verify(service).findByAbi(abi);
        Mockito.verifyNoInteractions(mapper);
    }

    @Test
    void getCbillAbiFederazioneByAbi_shouldReturnNotFound_whenServiceSignalsMissingEntity() {
        String abi = "99999";
        IllegalArgumentException failure = new IllegalArgumentException("ABI not found");
        Mockito.when(service.findByAbi(abi)).thenReturn(Uni.createFrom().failure(failure));

        UniAssertSubscriber<Response> subscriber = resource.getCbillAbiFederazioneByAbi(abi)
                .subscribe().withSubscriber(UniAssertSubscriber.create());

        Response response = subscriber.assertCompleted().getItem();
        Assertions.assertEquals(Response.Status.NOT_FOUND.getStatusCode(), response.getStatus());
        ErrorResponseDto error = (ErrorResponseDto) response.getEntity();
        Assertions.assertEquals("ABI not found", error.message);
        Mockito.verify(service).findByAbi(abi);
        Mockito.verifyNoInteractions(mapper);
    }

    @Test
    void getCbillAbiFederazioneByAbi_shouldReturnServerError_whenServiceFailsUnexpectedly() {
        String abi = "12345";
        RuntimeException failure = new RuntimeException("unexpected");
        Mockito.when(service.findByAbi(abi)).thenReturn(Uni.createFrom().failure(failure));

        UniAssertSubscriber<Response> subscriber = resource.getCbillAbiFederazioneByAbi(abi)
                .subscribe().withSubscriber(UniAssertSubscriber.create());

        Response response = subscriber.assertCompleted().getItem();
        Assertions.assertEquals(Response.Status.INTERNAL_SERVER_ERROR.getStatusCode(), response.getStatus());
        ErrorResponseDto error = (ErrorResponseDto) response.getEntity();
        Assertions.assertEquals("Internal server error", error.message);
        Mockito.verify(service).findByAbi(abi);
        Mockito.verifyNoInteractions(mapper);
    }

    @Test
    void createCbillAbiFederazione_shouldReturnDto_whenServiceSucceeds() {
        CbillAbiFederazioneDto dto = new CbillAbiFederazioneDto();
        CbillAbiFederazione entity = new CbillAbiFederazione();
        Mockito.when(service.create(entity)).thenReturn(Uni.createFrom().item(entity));
        Mockito.when(mapper.toEntity(dto)).thenReturn(entity);
        Mockito.when(mapper.toDto(entity)).thenReturn(dto);

        UniAssertSubscriber<Response> subscriber = resource.createCbillAbiFederazione(dto)
                .subscribe().withSubscriber(UniAssertSubscriber.create());

        Response response = subscriber.assertCompleted().getItem();
        Assertions.assertEquals(Response.Status.OK.getStatusCode(), response.getStatus());
        Assertions.assertEquals(dto, response.getEntity());
        Mockito.verify(service).create(entity);
        Mockito.verify(mapper).toEntity(dto);
        Mockito.verify(mapper).toDto(entity);
    }

    @Test
    void createCbillAbiFederazione_shouldReturnBadRequest_whenServiceFailsValidation() {
        CbillAbiFederazioneDto dto = new CbillAbiFederazioneDto();
        CbillAbiFederazione entity = new CbillAbiFederazione();
        IllegalArgumentException failure = new IllegalArgumentException("invalid data");
        Mockito.when(mapper.toEntity(dto)).thenReturn(entity);
        Mockito.when(service.create(entity)).thenReturn(Uni.createFrom().failure(failure));

        UniAssertSubscriber<Response> subscriber = resource.createCbillAbiFederazione(dto)
                .subscribe().withSubscriber(UniAssertSubscriber.create());

        Response response = subscriber.assertCompleted().getItem();
        Assertions.assertEquals(Response.Status.BAD_REQUEST.getStatusCode(), response.getStatus());
        ErrorResponseDto error = (ErrorResponseDto) response.getEntity();
        Assertions.assertEquals("invalid data", error.message);
        Mockito.verify(service).create(entity);
        Mockito.verify(mapper).toEntity(dto);
        Mockito.verify(mapper, Mockito.never()).toDto(Mockito.any());
    }

    @Test
    void createCbillAbiFederazione_shouldReturnServerError_whenServiceFailsUnexpectedly() {
        CbillAbiFederazioneDto dto = new CbillAbiFederazioneDto();
        CbillAbiFederazione entity = new CbillAbiFederazione();
        RuntimeException failure = new RuntimeException("unexpected");
        Mockito.when(mapper.toEntity(dto)).thenReturn(entity);
        Mockito.when(service.create(entity)).thenReturn(Uni.createFrom().failure(failure));

        UniAssertSubscriber<Response> subscriber = resource.createCbillAbiFederazione(dto)
                .subscribe().withSubscriber(UniAssertSubscriber.create());

        Response response = subscriber.assertCompleted().getItem();
        Assertions.assertEquals(Response.Status.INTERNAL_SERVER_ERROR.getStatusCode(), response.getStatus());
        ErrorResponseDto error = (ErrorResponseDto) response.getEntity();
        Assertions.assertEquals("Internal server error", error.message);
        Mockito.verify(service).create(entity);
        Mockito.verify(mapper).toEntity(dto);
        Mockito.verify(mapper, Mockito.never()).toDto(Mockito.any());
    }
}
