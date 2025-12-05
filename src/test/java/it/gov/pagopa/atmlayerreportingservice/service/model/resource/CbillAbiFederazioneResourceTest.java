package it.gov.pagopa.atmlayerreportingservice.service.model.resource;

import io.quarkus.test.junit.QuarkusTest;
import io.smallrye.mutiny.Uni;
import io.smallrye.mutiny.helpers.test.UniAssertSubscriber;
import it.gov.pagopa.atmlayerreportingservice.service.model.dto.CbillAbiFederazioneDto;
import it.gov.pagopa.atmlayerreportingservice.service.model.entity.CbillAbiFederazione;
import it.gov.pagopa.atmlayerreportingservice.service.model.mapper.CbillAbiFederazioneMapper;
import it.gov.pagopa.atmlayerreportingservice.service.model.service.CbillAbiFederazioneService;
import java.util.List;
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

        UniAssertSubscriber<CbillAbiFederazioneDto> subscriber = resource.getCbillAbiFederazioneByAbi(abi)
                .subscribe().withSubscriber(UniAssertSubscriber.create());

        subscriber.assertCompleted().assertItem(dto);
        Mockito.verify(service).findByAbi(abi);
        Mockito.verify(mapper).toDto(entity);
    }

    @Test
    void getCbillAbiFederazioneByAbi_shouldReturnNull_whenMapperReturnsNull() {
        String abi = "67890";
        CbillAbiFederazione entity = new CbillAbiFederazione();
        Mockito.when(service.findByAbi(abi)).thenReturn(Uni.createFrom().item(entity));
        Mockito.when(mapper.toDto(entity)).thenReturn(null);

        UniAssertSubscriber<CbillAbiFederazioneDto> subscriber = resource.getCbillAbiFederazioneByAbi(abi)
                .subscribe().withSubscriber(UniAssertSubscriber.create());

        subscriber.assertCompleted().assertItem(null);
        Mockito.verify(service).findByAbi(abi);
        Mockito.verify(mapper).toDto(entity);
    }

    @Test
    void getCbillAbiFederazioneByAbi_shouldPropagateFailure_whenServiceFails() {
        String abi = "99999";
        RuntimeException failure = new RuntimeException("find by abi failed");
        Mockito.when(service.findByAbi(abi)).thenReturn(Uni.createFrom().failure(failure));

        UniAssertSubscriber<CbillAbiFederazioneDto> subscriber = resource.getCbillAbiFederazioneByAbi(abi)
                .subscribe().withSubscriber(UniAssertSubscriber.create());

        subscriber.assertFailedWith(RuntimeException.class, "find by abi failed");
        Mockito.verify(service).findByAbi(abi);
        Mockito.verifyNoInteractions(mapper);
    }
}
