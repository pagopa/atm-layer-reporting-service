package it.gov.pagopa.atmlayerreportingservice.service.model.resource;

import it.gov.pagopa.atmlayerreportingservice.service.model.dto.CbillAbiFederazioneDto;
import it.gov.pagopa.atmlayerreportingservice.service.model.entity.CbillAbiFederazione;
import it.gov.pagopa.atmlayerreportingservice.service.model.mapper.CbillAbiFederazioneMapper;
import it.gov.pagopa.atmlayerreportingservice.service.model.service.CbillAbiFederazioneService;
import io.smallrye.mutiny.Uni;
import io.smallrye.mutiny.helpers.test.UniAssertSubscriber;
import java.util.List;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;

public class CbillAbiFederazioneResourceTest {

    private CbillAbiFederazioneResource resource;
    private CbillAbiFederazioneService service;
    private CbillAbiFederazioneMapper mapper;

    @BeforeEach
    void setup() {
        resource = new CbillAbiFederazioneResource();
        service = Mockito.mock(CbillAbiFederazioneService.class);
        mapper = Mockito.mock(CbillAbiFederazioneMapper.class);
        resource.service = service;
        resource.mapper = mapper;
    }

    @Test
    void list_shouldReturnDtos() {
        CbillAbiFederazione entity = new CbillAbiFederazione();
        CbillAbiFederazioneDto dto = new CbillAbiFederazioneDto();
        Mockito.when(service.findAll()).thenReturn(Uni.createFrom().item(List.of(entity)));
        Mockito.when(mapper.toDtoList(List.of(entity))).thenReturn(List.of(dto));

        UniAssertSubscriber<List<CbillAbiFederazioneDto>> subscriber = resource.listCbillAbiFederazione().subscribe().withSubscriber(UniAssertSubscriber.create());

        subscriber.assertCompleted().assertItem(List.of(dto));
        Mockito.verify(service).findAll();
    }

    @Test
    void list_shouldPropagateFailure() {
        RuntimeException error = new RuntimeException("err");
        Mockito.when(service.findAll()).thenReturn(Uni.createFrom().failure(error));

        UniAssertSubscriber<List<CbillAbiFederazioneDto>> subscriber = resource.listCbillAbiFederazione().subscribe().withSubscriber(UniAssertSubscriber.create());

        subscriber.assertFailedWith(RuntimeException.class, "err");
    }

    @Test
    void getByAbi_shouldReturnDto() {
        CbillAbiFederazione entity = new CbillAbiFederazione();
        CbillAbiFederazioneDto dto = new CbillAbiFederazioneDto();
        Mockito.when(service.findByAbi("12345")).thenReturn(Uni.createFrom().item(entity));
        Mockito.when(mapper.toDto(entity)).thenReturn(dto);

        UniAssertSubscriber<CbillAbiFederazioneDto> subscriber = resource.getCbillAbiFederazioneByAbi("12345").subscribe().withSubscriber(UniAssertSubscriber.create());

        subscriber.assertCompleted().assertItem(dto);
        Mockito.verify(service).findByAbi("12345");
    }

    @Test
    void getByAbi_shouldPropagateFailure() {
        RuntimeException error = new RuntimeException("fail");
        Mockito.when(service.findByAbi("999")).thenReturn(Uni.createFrom().failure(error));

        UniAssertSubscriber<CbillAbiFederazioneDto> subscriber = resource.getCbillAbiFederazioneByAbi("999").subscribe().withSubscriber(UniAssertSubscriber.create());

        subscriber.assertFailedWith(RuntimeException.class, "fail");
    }
}
