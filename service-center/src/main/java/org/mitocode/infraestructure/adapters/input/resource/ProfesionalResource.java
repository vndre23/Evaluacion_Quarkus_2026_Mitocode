package org.mitocode.infraestructure.adapters.input.resource;

import io.smallrye.mutiny.Uni;
import jakarta.validation.Valid;
import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.ws.rs.*;
import jakarta.ws.rs.core.Response;
import lombok.extern.slf4j.Slf4j;
import org.eclipse.microprofile.openapi.annotations.Operation;
import org.eclipse.microprofile.openapi.annotations.tags.Tag;
import org.mitocode.application.command.CreateUpdateClientCommand;
import org.mitocode.application.command.CreateUpdateProfesionalCommand;
import org.mitocode.application.port.in.CrudClienteUseCase;
import org.mitocode.application.port.in.CrudProfesionalUseCase;
import org.mitocode.application.port.in.ListAllProfesionalByFechaUseCase;
import org.mitocode.application.port.in.ListAllProfesionalGroupByFechaUseCase;
import org.mitocode.infraestructure.adapters.input.resource.dto.ClienteRequestDto;
import org.mitocode.infraestructure.adapters.input.resource.dto.ProfesionalRequestDto;

@Tag( name = "Profesionales", description = "Operaciones para la gestión de profesionales del centro de servicios" )
@Slf4j
@Path("/mitocode/api/v1/service-center/profesional")
public class ProfesionalResource {
    private final CrudProfesionalUseCase crudProfesionalUseCase;

    private final ListAllProfesionalByFechaUseCase listAllProfesionalByFechaUseCase;

    private final ListAllProfesionalGroupByFechaUseCase listAllProfesionalGroupByFechaUseCase;

    public ProfesionalResource(CrudProfesionalUseCase crudProfesionalUseCase, ListAllProfesionalByFechaUseCase listAllProfesionalByFechaUseCase, ListAllProfesionalGroupByFechaUseCase listAllProfesionalGroupByFechaUseCase) {
        this.crudProfesionalUseCase = crudProfesionalUseCase;
        this.listAllProfesionalByFechaUseCase = listAllProfesionalByFechaUseCase;
        this.listAllProfesionalGroupByFechaUseCase = listAllProfesionalGroupByFechaUseCase;
    }

    @Operation( summary = "Crear profesional", description = "Registra un nuevo profesional en el centro de servicios." )
    @POST
    public Uni<Response> create(@Valid ProfesionalRequestDto request) {
        return crudProfesionalUseCase.create(toDomain(request))
                .map(response -> Response.status(201).entity(response).build());
    }

    @Operation( summary = "Listar profesionales", description = "Obtiene una lista paginada de profesionales." )
    @GET
    public Uni<Response> listAll(@QueryParam("page") @DefaultValue("1")
                                     @Min(value = 1, message = "La página mínima es 1")
                                     int page,
                                 @QueryParam("limit") @DefaultValue("10")
                                 @Min(5)
                                 @Max(value = 20, message = "No puedes pedir más de 20 registros")
                                 int limit) {
        return crudProfesionalUseCase.listAll(page, limit)
                .map(response -> Response.ok(response).build());
    }

    @Operation( summary = "Obtener profesional por ID", description = "Obtiene la información de un profesional utilizando su identificador." )
    @GET
    @Path("/{id}")
    public Uni<Response> findById(@PathParam("id") String id) {
        return this.crudProfesionalUseCase.findById(id)
                .map(response -> Response.ok(response).build());
    }

    @Operation( summary = "Actualizar profesional", description = "Actualiza los datos de un profesional existente." )
    @PUT
    @Path("/{id}")
    public Uni<Response> updateById(@Valid ProfesionalRequestDto request, @PathParam("id") String id) {
        return this.crudProfesionalUseCase.update(toDomain(request), id)
                .map(response -> Response.ok(response).build());
    }

    @Operation( summary = "Listar profesionales con reservas", description = "Obtiene una lista paginada de profesionales asociados a reservas." )
    @GET
    @Path("/reservas")
    public Uni<Response> listAllByReserva(@QueryParam("page") @DefaultValue("1")
                                    @Min(value = 1, message = "La página mínima es 1")
                                    int page,
                                    @QueryParam("limit") @DefaultValue("10")
                                    @Min(5)
                                    @Max(value = 20, message = "No puedes pedir más de 20 registros")
                                    int limit) {
        return this.listAllProfesionalByFechaUseCase.listAllByFecha(page, limit)
                .map(response -> Response.ok(response).build());
    }

    @Operation( summary = "Listar profesionales agrupados por fecha", description = "Obtiene una lista paginada de profesionales agrupados por fecha de reserva." )
    @GET
    @Path("/reservas-by-fecha")
    public Uni<Response> listAllByFecha(@QueryParam("page") @DefaultValue("1")
                                        @Min(value = 1, message = "La página mínima es 1")
                                        int page,
                                        @QueryParam("limit") @DefaultValue("10")
                                        @Min(5)
                                        @Max(value = 20, message = "No puedes pedir más de 20 registros")
                                        int limit) {
        return this.listAllProfesionalGroupByFechaUseCase.listAllGroupByFecha(page, limit)
                .map(response -> Response.ok(response).build());
    }



    private CreateUpdateProfesionalCommand toDomain(ProfesionalRequestDto requestDto) {
        return new CreateUpdateProfesionalCommand(requestDto.nombres(), requestDto.apellidos(), requestDto.especialidad());
    }
}
