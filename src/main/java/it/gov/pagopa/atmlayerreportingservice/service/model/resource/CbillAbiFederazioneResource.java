package it.gov.pagopa.atmlayerreportingservice.service.model.resource;

import it.gov.pagopa.atmlayerreportingservice.service.model.dto.CbillAbiFederazioneDto;
import it.gov.pagopa.atmlayerreportingservice.service.model.dto.ErrorResponseDto;
import it.gov.pagopa.atmlayerreportingservice.service.model.mapper.CbillAbiFederazioneMapper;
import it.gov.pagopa.atmlayerreportingservice.service.model.service.CbillAbiFederazioneService;
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
import jakarta.ws.rs.core.Response;
import java.util.List;
import org.eclipse.microprofile.openapi.annotations.Operation;
import org.eclipse.microprofile.openapi.annotations.media.Content;
import org.eclipse.microprofile.openapi.annotations.media.Schema;
import org.eclipse.microprofile.openapi.annotations.responses.APIResponse;
import org.eclipse.microprofile.openapi.annotations.tags.Tag;
import org.jboss.logging.Logger;

@ApplicationScoped
@Path("/cbill-abi-federazione")
@Produces(MediaType.APPLICATION_JSON)
@Consumes(MediaType.APPLICATION_JSON)
@Tag(name = "CbillAbiFederazione", description = "Operazioni sulla federazione ABI CBILL")
public class CbillAbiFederazioneResource {

    private static final Logger LOG = Logger.getLogger(CbillAbiFederazioneResource.class);

    @Inject
    CbillAbiFederazioneService service;

    @Inject
    CbillAbiFederazioneMapper mapper;

    @GET
    @Operation(operationId = "listCbillAbiFederazione", summary = "Lista delle federazioni ABI CBILL")
    @APIResponse(responseCode = "200", description = "Operazione eseguita con successo", content = @Content(schema = @Schema(implementation = CbillAbiFederazioneDto[].class)))
    @APIResponse(responseCode = "400", description = "Richiesta non valida", content = @Content(schema = @Schema(implementation = ErrorResponseDto.class)))
    public Uni<List<CbillAbiFederazioneDto>> listCbillAbiFederazione() {
        LOG.info("Received request to list all CbillAbiFederazione.");
        return service.findAll()
                .map(mapper::toDtoList)
                .onItem().invoke(result -> LOG.info("Successfully retrieved " + result.size() + " CbillAbiFederazione records."))
                .onFailure().invoke(ex -> LOG.error("Error occurred while fetching CbillAbiFederazione records: " + ex.getMessage()));
    }

    @GET
    @Path("/{abi}")
    @Operation(operationId = "getCbillAbiFederazioneByAbi", summary = "Dettaglio federazione ABI CBILL")
    @APIResponse(responseCode = "200", description = "Operazione eseguita con successo", content = @Content(schema = @Schema(implementation = CbillAbiFederazioneDto.class)))
    @APIResponse(responseCode = "400", description = "Richiesta non valida", content = @Content(schema = @Schema(implementation = ErrorResponseDto.class)))
    @APIResponse(responseCode = "404", description = "Risorsa non trovata", content = @Content(schema = @Schema(implementation = ErrorResponseDto.class)))
    @APIResponse(responseCode = "500", description = "Errore interno", content = @Content(schema = @Schema(implementation = ErrorResponseDto.class)))
    public Uni<Response> getCbillAbiFederazioneByAbi(@PathParam("abi") String abi) {
        LOG.info("Received request to get CbillAbiFederazione by ABI: " + abi);
        return service.findByAbi(abi)
                .map(mapper::toDto)
                .map(dto -> Response.ok(dto).build())
                .onFailure(IllegalArgumentException.class).recoverWithItem(ex -> {
                    ErrorResponseDto error = new ErrorResponseDto();
                    error.message = ex.getMessage();
                    String message = ex.getMessage() == null ? "" : ex.getMessage().toLowerCase();
                    if (message.contains("not found")) {
                        return Response.status(Response.Status.NOT_FOUND).entity(error).build();
                    }
                    return Response.status(Response.Status.BAD_REQUEST).entity(error).build();
                })
                .onFailure().recoverWithItem(ex -> {
                    ErrorResponseDto error = new ErrorResponseDto();
                    error.message = "Internal server error";
                    LOG.error("Error occurred while fetching CbillAbiFederazione for ABI: " + abi + ": " + ex.getMessage());
                    return Response.status(Response.Status.INTERNAL_SERVER_ERROR).entity(error).build();
                });
    }

    @POST
    @Operation(operationId = "createCbillAbiFederazione", summary = "Creazione di una federazione ABI CBILL")
    @APIResponse(responseCode = "200", description = "Federazione ABI CBILL persistita", content = @Content(schema = @Schema(implementation = CbillAbiFederazioneDto.class)))
    @APIResponse(responseCode = "400", description = "Richiesta non valida", content = @Content(schema = @Schema(implementation = ErrorResponseDto.class)))
    @APIResponse(responseCode = "500", description = "Errore interno", content = @Content(schema = @Schema(implementation = ErrorResponseDto.class)))
    public Uni<Response> createCbillAbiFederazione(@Valid CbillAbiFederazioneDto dto) {
        LOG.info("Received request to create a new CbillAbiFederazione");
        return service.create(mapper.toEntity(dto))
                .map(mapper::toDto)
                .map(created -> Response.ok(created).build())
                .onFailure(IllegalArgumentException.class).recoverWithItem(ex -> {
                    ErrorResponseDto error = new ErrorResponseDto();
                    error.message = ex.getMessage();
                    return Response.status(Response.Status.BAD_REQUEST).entity(error).build();
                })
                .onFailure().recoverWithItem(ex -> {
                    ErrorResponseDto error = new ErrorResponseDto();
                    error.message = "Internal server error";
                    LOG.error("Error occurred while creating CbillAbiFederazione: " + ex.getMessage());
                    return Response.status(Response.Status.INTERNAL_SERVER_ERROR).entity(error).build();
                });
    }
}
