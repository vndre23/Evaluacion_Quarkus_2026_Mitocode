package org.mitocode.infraestructure.adapters.input.resource.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import org.eclipse.microprofile.openapi.annotations.media.Schema;

import java.time.LocalDate;
import java.time.LocalTime;

@Schema(
        name = "AsignarHorarioRequestDto",
        description = "Información requerida para asignar un horario disponible a un profesional"
)
public record AsignarHorarioRequestDto(

        @Schema(
                description = "ID del profesional al que se asignará el horario",
                example = "79965c97-d74a-44e1-83e8-076ba781ccdf",
                required = true
        )
        @NotBlank(message = "El profesional es obligatorio")
        String profesional,

        @Schema(
                description = "Fecha del horario disponible",
                example = "2026-09-29",
                required = true
        )
        @NotNull(message = "La fecha es obligatoria")
        LocalDate fecha,

        @Schema(
                description = "Hora de inicio del horario",
                example = "09:00:00",
                required = true
        )
        @NotNull(message = "La hora de inicio es obligatoria")
        LocalTime horaInicio,

        @Schema(
                description = "Hora de fin del horario",
                example = "10:00:00",
                required = true
        )
        @NotNull(message = "La hora de fin es obligatoria")
        LocalTime horaFin
) {
}