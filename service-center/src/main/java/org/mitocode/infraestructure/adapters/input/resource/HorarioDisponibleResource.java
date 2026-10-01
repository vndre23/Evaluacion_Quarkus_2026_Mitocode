package org.mitocode.infraestructure.adapters.input.resource;

import io.smallrye.mutiny.Uni;
import jakarta.ws.rs.POST;
import jakarta.ws.rs.Path;
import jakarta.ws.rs.PathParam;
import jakarta.ws.rs.core.Response;
import lombok.extern.slf4j.Slf4j;
import org.eclipse.microprofile.openapi.annotations.Operation;
import org.eclipse.microprofile.openapi.annotations.media.Content;
import org.eclipse.microprofile.openapi.annotations.media.Schema;
import org.eclipse.microprofile.openapi.annotations.parameters.RequestBody;
import org.eclipse.microprofile.openapi.annotations.responses.APIResponse;
import org.eclipse.microprofile.openapi.annotations.responses.APIResponses;
import org.eclipse.microprofile.openapi.annotations.tags.Tag;
import org.mitocode.application.command.AsignarHorarioProfesionalCommand;
import org.mitocode.application.port.in.AsignarHorarioProfesionalUseCase;
import org.mitocode.application.service.AsignarHorarioService;
import org.mitocode.infraestructure.adapters.input.resource.dto.AsignarHorarioRequestDto;

import java.util.UUID;

@Slf4j
@Path("/mitocode/api/v1/service-center/horario-disponible")
@Tag(
        name = "Horarios disponibles",
        description = "Operaciones para gestionar los horarios disponibles de los profesionales"
)
public class HorarioDisponibleResource {

    private final AsignarHorarioProfesionalUseCase asignarHorarioProfesionalUseCase;

    public HorarioDisponibleResource(
            AsignarHorarioProfesionalUseCase asignarHorarioProfesionalUseCase) {
        this.asignarHorarioProfesionalUseCase = asignarHorarioProfesionalUseCase;
    }

    @POST
    @Operation(
            summary = "Asignar horario disponible",
            description = "Registra un horario disponible para un profesional."
    )
    @APIResponses({
            @APIResponse(
                    responseCode = "201",
                    description = "Horario disponible registrado correctamente",
                    content = @Content(
                            schema = @Schema(implementation = AsignarHorarioRequestDto.class)
                    )
            ),
            @APIResponse(
                    responseCode = "404",
                    description = "El profesional no existe"
            ),
            @APIResponse(
                    responseCode = "409",
                    description = "El horario se solapa con otro horario existente"
            )
    })
    public Uni<Response> asignar(
            @RequestBody(
                    required = true,
                    description = "Datos del horario que se desea asignar"
            )
            AsignarHorarioRequestDto request) {

        return this.asignarHorarioProfesionalUseCase
                .asignarHorarioDisponible(toCommand(request))
                .map(response ->
                        Response.status(201)
                                .entity(response)
                                .build()
                );
    }

    private AsignarHorarioProfesionalCommand toCommand(
            AsignarHorarioRequestDto requestDto) {

        return new AsignarHorarioProfesionalCommand(
                UUID.fromString(requestDto.profesional()),
                requestDto.fecha(),
                requestDto.horaInicio(),
                requestDto.horaFin()
        );
    }
}