package org.mitocode.infraestructure.adapters.input.resource;

import io.smallrye.mutiny.Uni;
import jakarta.ws.rs.PATCH;
import jakarta.ws.rs.POST;
import jakarta.ws.rs.Path;
import jakarta.ws.rs.PathParam;
import jakarta.ws.rs.core.Response;
import lombok.extern.slf4j.Slf4j;
import org.eclipse.microprofile.openapi.annotations.Operation;
import org.eclipse.microprofile.openapi.annotations.tags.Tag;
import org.mitocode.application.command.AsignarHorarioProfesionalCommand;
import org.mitocode.application.command.RegistrarReservaCommand;
import org.mitocode.application.port.in.CancelReservaUseCase;
import org.mitocode.application.port.in.RegistrarReservaUseCase;
import org.mitocode.infraestructure.adapters.input.resource.dto.AsignarHorarioRequestDto;
import org.mitocode.infraestructure.adapters.input.resource.dto.ReservarRequestDto;

import java.util.UUID;

@Tag( name = "Reservas", description = "Operaciones para la gestión de reservas del centro de servicios" )
@Slf4j
@Path("/mitocode/api/v1/service-center/reserva")
public class ReservaResource {

    private final RegistrarReservaUseCase registrarReservaUseCase;
    private final CancelReservaUseCase cancelReservaUseCase;

    public ReservaResource(RegistrarReservaUseCase registrarReservaUseCase, CancelReservaUseCase cancelReservaUseCase) {
        this.registrarReservaUseCase = registrarReservaUseCase;
        this.cancelReservaUseCase = cancelReservaUseCase;
    }

    @Operation( summary = "Registrar reserva", description = "Registra una nueva reserva para un cliente y un profesional." )
    @POST
    public Uni<Response> reservar(ReservarRequestDto request) {
        return this.registrarReservaUseCase.execute(toCommand(request))
                .map(response -> Response.status(201).entity(response).build());
    }

    @Operation( summary = "Cancelar reserva", description = "Cancela una reserva existente utilizando su identificador." )
    @PATCH
    @Path("/cancelar/{id}")
    public Uni<Response> cancelar(@PathParam("id") String id) {
        return this.cancelReservaUseCase.cancelar(UUID.fromString(id))
                .map(response -> Response.ok(response).build());
    }

    private RegistrarReservaCommand toCommand(ReservarRequestDto requestDto) {
        return new RegistrarReservaCommand(UUID.fromString(requestDto.cliente()), UUID.fromString(requestDto.profesional()) , requestDto.fecha(), requestDto.horaInicio(), requestDto.horaFin());
    }
}
