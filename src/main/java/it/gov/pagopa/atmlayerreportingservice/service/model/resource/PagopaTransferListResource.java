package it.gov.pagopa.atmlayerreportingservice.service.model.resource;

import it.gov.pagopa.atmlayerreportingservice.service.model.dto.ErrorResponseDto;
import it.gov.pagopa.atmlayerreportingservice.service.model.dto.PagopaTransferListDto;
import it.gov.pagopa.atmlayerreportingservice.service.model.mapper.PagopaTransferListMapper;
import it.gov.pagopa.atmlayerreportingservice.service.model.service.PagopaTransferListService;
import io.smallrye.mutiny.Uni;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import jakarta.validation.Valid;
import jakarta.ws.rs.Consumes;
import jakarta.ws.rs.GET;
import jakarta.ws.rs.POST;
import jakarta.ws.rs.Path;
import jakarta.ws.rs.PathParam;
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
@Path("/")
@Produces(MediaType.APPLICATION_JSON)
@Consumes(MediaType.APPLICATION_JSON)
@Tag(name = "PagopaTransferList", description = "Operazioni sugli elementi della lista di trasferimento PagoPA")
public class PagopaTransferListResource {
    private static final Logger LOG = Logger.getLogger(PagopaTransferListResource.class);
    @Inject
    PagopaTransferListService service;

    @Inject
    PagopaTransferListMapper mapper;

    @GET
    @Path("/pagopa-transfer-lists")
    @Operation(operationId = "listPagopaTransferLists", summary = "Lista degli elementi di trasferimento")
    @APIResponse(responseCode = "200", description = "Operazione eseguita con successo", content = @Content(schema = @Schema(implementation = PagopaTransferListDto[].class)))
    @APIResponse(responseCode = "400", description = "Richiesta non valida", content = @Content(schema = @Schema(implementation = ErrorResponseDto.class)))
    public Uni<List<PagopaTransferListDto>> listPagopaTransferLists() {
        LOG.info("Received request to list all PagopaTransferLists.");
        return service.findAll().map(mapper::toDtoList);
    }

    @POST
    @Path("/pagopa-transfer-lists")
    @Operation(operationId = "createPagopaTransferList", summary = "Creazione di un elemento di trasferimento")
    @APIResponse(responseCode = "200", description = "Elemento di trasferimento persistito", content = @Content(schema = @Schema(implementation = PagopaTransferListDto.class)))
    @APIResponse(responseCode = "400", description = "Richiesta non valida", content = @Content(schema = @Schema(implementation = ErrorResponseDto.class)))
    public Uni<PagopaTransferListDto> createPagopaTransferList(@Valid PagopaTransferListDto dto) {
        LOG.info("Received request to create a new PagopaTransferList");
        return service.persist(mapper.toEntity(dto)).map(mapper::toDto);
    }

    @GET
    @Path("/pagopa-transactions/{transactionId}/transfer-lists")
    @Operation(operationId = "listPagopaTransferListsByTransactionId", summary = "Lista di trasferimenti per transazione")
    @APIResponse(responseCode = "200", description = "Operazione eseguita con successo", content = @Content(schema = @Schema(implementation = PagopaTransferListDto[].class)))
    @APIResponse(responseCode = "400", description = "Richiesta non valida", content = @Content(schema = @Schema(implementation = ErrorResponseDto.class)))
    @APIResponse(responseCode = "404", description = "Risorsa non trovata", content = @Content(schema = @Schema(implementation = ErrorResponseDto.class)))
    public Uni<List<PagopaTransferListDto>> listPagopaTransferListsByTransactionId(@PathParam("transactionId") Long transactionId) {
        LOG.info("Received request to list PagopaTransferLists for transactionId: " + transactionId);
        return service.findByTransactionId(transactionId).map(mapper::toDtoList);
    }
}
