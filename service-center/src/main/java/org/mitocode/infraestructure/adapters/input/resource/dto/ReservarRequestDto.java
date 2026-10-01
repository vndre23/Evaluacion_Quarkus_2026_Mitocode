package org.mitocode.infraestructure.adapters.input.resource.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import org.eclipse.microprofile.openapi.annotations.media.Schema;

import java.time.LocalDate;
import java.time.LocalTime;

@Schema(
        name = "ReservarRequestDto",
        description = "Información requerida para registrar una reserva"
)
public record ReservarRequestDto(

        @Schema(
                description = "ID del cliente que realizará la reserva",
                example = "79965c97-d74a-44e1-83e8-076ba781ccdf",
                required = true
        )
        @NotBlank(message = "El cliente es obligatorio")
        String cliente,

        @Schema(
                description = "ID del profesional con quien se realizará la reserva",
                example = "79965c97-d74a-44e1-83e8-076ba781ccdf",
                required = true
        )
        @NotBlank(message = "El profesional es obligatorio")
        String profesional,

        @Schema(
                description = "Fecha de la reserva",
                example = "2026-09-29",
                required = true
        )
        @NotNull(message = "La fecha es obligatoria")
        LocalDate fecha,

        @Schema(
                description = "Hora de inicio de la reserva",
                example = "09:00:00",
                required = true
        )
        @NotNull(message = "La hora de inicio es obligatoria")
        LocalTime horaInicio,

        @Schema(
                description = "Hora de fin de la reserva",
                example = "10:00:00",
                required = true
        )
        @NotNull(message = "La hora de fin es obligatoria")
        LocalTime horaFin
) {
}