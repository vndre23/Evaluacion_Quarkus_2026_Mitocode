package org.mitocode.infraestructure.adapters.input.resource;

import io.smallrye.mutiny.Uni;
import jakarta.validation.Valid;
import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.ws.rs.*;
import jakarta.ws.rs.core.Response;
import lombok.extern.slf4j.Slf4j;
import org.eclipse.microprofile.openapi.annotations.Operation;
import org.eclipse.microprofile.openapi.annotations.media.Content;
import org.eclipse.microprofile.openapi.annotations.media.Schema;
import org.eclipse.microprofile.openapi.annotations.parameters.Parameter;
import org.eclipse.microprofile.openapi.annotations.parameters.RequestBody;
import org.eclipse.microprofile.openapi.annotations.responses.APIResponse;
import org.eclipse.microprofile.openapi.annotations.responses.APIResponses;
import org.eclipse.microprofile.openapi.annotations.tags.Tag;
import org.mitocode.application.command.CreateUpdateClientCommand;
import org.mitocode.application.port.in.CrudClienteUseCase;
import org.mitocode.application.response.ApiResponse;
import org.mitocode.application.response.PageResponseDto;
import org.mitocode.infraestructure.adapters.input.resource.dto.ClienteRequestDto;

@Slf4j
@Path("/mitocode/api/v1/service-center/client")
@Tag(
        name = "Clientes",
        description = "Operaciones para la gestión de clientes"
)
public class ClienteResource {

    private final CrudClienteUseCase crudClienteUseCase;

    public ClienteResource(CrudClienteUseCase crudClienteUseCase) {
        this.crudClienteUseCase = crudClienteUseCase;
    }

    @POST
    @Operation(
            summary = "Registrar cliente",
            description = "Registra un nuevo cliente en el sistema."
    )
    @APIResponses({
            @APIResponse(
                    responseCode = "201",
                    description = "Cliente registrado correctamente",
                    content = @Content(
                            schema = @Schema(implementation = ApiResponse.class)
                    )
            ),
            @APIResponse(
                    responseCode = "409",
                    description = "El email del cliente ya se encuentra registrado"
            ),
            @APIResponse(
                    responseCode = "400",
                    description = "Los datos enviados no son válidos"
            )
    })
    public Uni<Response> create(
            @RequestBody(
                    required = true,
                    description = "Datos del cliente a registrar"
            )
            @Valid ClienteRequestDto request) {

        return crudClienteUseCase.create(toDomain(request))
                .map(response ->
                        Response.status(201)
                                .entity(response)
                                .build()
                );
    }

    @GET
    @Operation(
            summary = "Listar clientes",
            description = "Obtiene una lista paginada de clientes."
    )
    @APIResponses({
            @APIResponse(
                    responseCode = "200",
                    description = "Clientes obtenidos correctamente",
                    content = @Content(
                            schema = @Schema(implementation = PageResponseDto.class)
                    )
            ),
            @APIResponse(
                    responseCode = "400",
                    description = "Parámetros de paginación inválidos"
            )
    })
    public Uni<Response> listAll(
            @Parameter(
                    description = "Número de página. La numeración comienza en 1.",
                    example = "1"
            )
            @QueryParam("page")
            @DefaultValue("1")
            @Min(value = 1, message = "La página mínima es 1")
            int page,

            @Parameter(
                    description = "Cantidad de registros por página. Mínimo 5 y máximo 20.",
                    example = "10"
            )
            @QueryParam("limit")
            @DefaultValue("10")
            @Min(5)
            @Max(value = 20, message = "No puedes pedir más de 20 registros")
            int limit) {

        return crudClienteUseCase.listAll(page, limit)
                .map(response ->
                        Response.ok(response).build()
                );
    }

    @GET
    @Path("/{id}")
    @Operation(
            summary = "Buscar cliente por ID",
            description = "Obtiene la información de un cliente mediante su identificador."
    )
    @APIResponses({
            @APIResponse(
                    responseCode = "200",
                    description = "Cliente encontrado",
                    content = @Content(
                            schema = @Schema(implementation = ApiResponse.class)
                    )
            ),
            @APIResponse(
                    responseCode = "404",
                    description = "Cliente no encontrado"
            )
    })
    public Uni<Response> findById(
            @Parameter(
                    description = "Identificador único del cliente",
                    required = true,
                    example = "11111111-1111-1111-1111-111111111111"
            )
            @PathParam("id") String id) {

        return this.crudClienteUseCase.findById(id)
                .map(response ->
                        Response.ok(response).build()
                );
    }

    @PUT
    @Path("/{id}")
    @Operation(
            summary = "Actualizar cliente",
            description = "Actualiza los datos de un cliente existente."
    )
    @APIResponses({
            @APIResponse(
                    responseCode = "200",
                    description = "Cliente actualizado correctamente",
                    content = @Content(
                            schema = @Schema(implementation = ApiResponse.class)
                    )
            ),
            @APIResponse(
                    responseCode = "404",
                    description = "Cliente no encontrado"
            ),
            @APIResponse(
                    responseCode = "409",
                    description = "El email del cliente ya se encuentra registrado"
            ),
            @APIResponse(
                    responseCode = "400",
                    description = "Los datos enviados no son válidos"
            )
    })
    public Uni<Response> updateById(
            @Valid ClienteRequestDto request,

            @Parameter(
                    description = "Identificador único del cliente",
                    required = true,
                    example = "11111111-1111-1111-1111-111111111111"
            )
            @PathParam("id") String id) {

        return crudClienteUseCase.update(toDomain(request), id)
                .map(response ->
                        Response.ok(response).build()
                );
    }

    @DELETE
    @Path("/{id}")
    @Operation(
            summary = "Eliminar cliente",
            description = "Cambia el estado del cliente a false."
    )
    public Uni<Response> deleteById(
            @Parameter(
                    description = "Identificador único del cliente",
                    required = true,
                    example = "11111111-1111-1111-1111-111111111111"
            )
            @PathParam("id") String id) {

        return crudClienteUseCase.deleteById(id)
                .replaceWith(Response.noContent().build());
    }

    private CreateUpdateClientCommand toDomain(
            ClienteRequestDto requestDto) {

        return new CreateUpdateClientCommand(
                requestDto.nombres(),
                requestDto.apellidos(),
                requestDto.email(),
                requestDto.telefono()
        );
    }
}