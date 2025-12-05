package it.gov.pagopa.atmlayerreportingservice.service.model.resource;

import it.gov.pagopa.atmlayerreportingservice.service.model.dto.PagopaTransactionsDto;
import it.gov.pagopa.atmlayerreportingservice.service.model.entity.PagopaTransactions;
import it.gov.pagopa.atmlayerreportingservice.service.model.mapper.PagopaTransactionsMapper;
import it.gov.pagopa.atmlayerreportingservice.service.model.service.PagopaTransactionsService;
import io.smallrye.mutiny.Uni;
import io.smallrye.mutiny.helpers.test.UniAssertSubscriber;
import java.util.List;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;

public class PagopaTransactionsResourceTest {

    private PagopaTransactionsResource resource;
    private PagopaTransactionsService service;
    private PagopaTransactionsMapper mapper;

    @BeforeEach
    void setup() {
        resource = new PagopaTransactionsResource();
        service = Mockito.mock(PagopaTransactionsService.class);
        mapper = Mockito.mock(PagopaTransactionsMapper.class);
        resource.service = service;
        resource.mapper = mapper;
    }

    @Test
    void list_shouldReturnDtos() {
        PagopaTransactions entity = new PagopaTransactions();
        PagopaTransactionsDto dto = new PagopaTransactionsDto();
        Mockito.when(service.findAll()).thenReturn(Uni.createFrom().item(List.of(entity)));
        Mockito.when(mapper.toDtoList(List.of(entity))).thenReturn(List.of(dto));

        UniAssertSubscriber<List<PagopaTransactionsDto>> subscriber = resource.listPagopaTransactions().subscribe().withSubscriber(UniAssertSubscriber.create());

        subscriber.assertCompleted().assertItem(List.of(dto));
        Mockito.verify(service).findAll();
    }

    @Test
    void list_shouldPropagateFailure() {
        RuntimeException error = new RuntimeException("error");
        Mockito.when(service.findAll()).thenReturn(Uni.createFrom().failure(error));

        UniAssertSubscriber<List<PagopaTransactionsDto>> subscriber = resource.listPagopaTransactions().subscribe().withSubscriber(UniAssertSubscriber.create());

        subscriber.assertFailedWith(RuntimeException.class, "error");
    }

    @Test
    void create_shouldPersistAndReturnDto() {
        PagopaTransactionsDto dto = new PagopaTransactionsDto();
        PagopaTransactions entity = new PagopaTransactions();
        Mockito.when(mapper.toEntity(dto)).thenReturn(entity);
        Mockito.when(service.persist(entity)).thenReturn(Uni.createFrom().item(entity));
        Mockito.when(mapper.toDto(entity)).thenReturn(dto);

        UniAssertSubscriber<PagopaTransactionsDto> subscriber = resource.createPagopaTransaction(dto).subscribe().withSubscriber(UniAssertSubscriber.create());

        subscriber.assertCompleted().assertItem(dto);
        Mockito.verify(service).persist(entity);
    }

    @Test
    void create_shouldPropagateFailure() {
        PagopaTransactionsDto dto = new PagopaTransactionsDto();
        PagopaTransactions entity = new PagopaTransactions();
        RuntimeException error = new RuntimeException("persist");
        Mockito.when(mapper.toEntity(dto)).thenReturn(entity);
        Mockito.when(service.persist(entity)).thenReturn(Uni.createFrom().failure(error));

        UniAssertSubscriber<PagopaTransactionsDto> subscriber = resource.createPagopaTransaction(dto).subscribe().withSubscriber(UniAssertSubscriber.create());

        subscriber.assertFailedWith(RuntimeException.class, "persist");
    }
}
