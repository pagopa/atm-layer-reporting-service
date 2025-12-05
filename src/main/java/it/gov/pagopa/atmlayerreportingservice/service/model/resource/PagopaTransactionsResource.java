package it.gov.pagopa.atmlayerreportingservice.service.model.resource;

import it.gov.pagopa.atmlayerreportingservice.service.model.dto.ErrorResponseDto;
import it.gov.pagopa.atmlayerreportingservice.service.model.dto.PagopaTransactionsDto;
import it.gov.pagopa.atmlayerreportingservice.service.model.mapper.PagopaTransactionsMapper;
import it.gov.pagopa.atmlayerreportingservice.service.model.service.PagopaTransactionsService;
import io.smallrye.mutiny.Uni;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import jakarta.validation.Valid;
import jakarta.ws.rs.Consumes;
import jakarta.ws.rs.GET;
import jakarta.ws.rs.POST;
import jakarta.ws.rs.Path;
import jakarta.ws.rs.Produces;
import jakarta.ws.rs.core.MediaType;
import java.util.List;
import org.eclipse.microprofile.openapi.annotations.Operation;
import org.eclipse.microprofile.openapi.annotations.media.Content;
import org.eclipse.microprofile.openapi.annotations.media.Schema;
import org.eclipse.microprofile.openapi.annotations.responses.APIResponse;
import org.eclipse.microprofile.openapi.annotations.tags.Tag;
import org.jboss.logging.Logger;

@ApplicationScoped
@Path("/transactions")
@Produces(MediaType.APPLICATION_JSON)
@Consumes(MediaType.APPLICATION_JSON)
@Tag(name = "PagopaTransactions", description = "Operazioni sulle transazioni PagoPA")
public class PagopaTransactionsResource {
    private static final Logger LOG = Logger.getLogger(PagopaTransactionsResource.class);
    @Inject
    PagopaTransactionsService service;

    @Inject
    PagopaTransactionsMapper mapper;

    @GET
    @Operation(operationId = "listPagopaTransactions", summary = "Lista delle transazioni PagoPA")
    @APIResponse(responseCode = "200", description = "Operazione eseguita con successo", content = @Content(schema = @Schema(implementation = PagopaTransactionsDto[].class)))
    @APIResponse(responseCode = "400", description = "Richiesta non valida", content = @Content(schema = @Schema(implementation = ErrorResponseDto.class)))
    public Uni<List<PagopaTransactionsDto>> listPagopaTransactions() {
        LOG.info("Received request to list all PagopaTransactions.");
        return service.findAll().map(mapper::toDtoList);
    }

    @POST
    @Operation(operationId = "createPagopaTransaction", summary = "Creazione di una transazione PagoPA")
    @APIResponse(responseCode = "200", description = "Transazione persistita", content = @Content(schema = @Schema(implementation = PagopaTransactionsDto.class)))
    @APIResponse(responseCode = "400", description = "Richiesta non valida", content = @Content(schema = @Schema(implementation = ErrorResponseDto.class)))
    public Uni<PagopaTransactionsDto> createPagopaTransaction(@Valid PagopaTransactionsDto dto) {
        LOG.info("Received request to create a new PagopaTransaction");
        return service.persist(mapper.toEntity(dto)).map(mapper::toDto);
    }
}
