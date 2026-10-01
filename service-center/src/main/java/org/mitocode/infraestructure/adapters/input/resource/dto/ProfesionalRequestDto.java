package org.mitocode.infraestructure.adapters.input.resource.dto;

import jakarta.validation.constraints.NotBlank;
import org.eclipse.microprofile.openapi.annotations.media.Schema;

@Schema(
        name = "ProfesionalRequestDto",
        description = "Información requerida para crear un profesional"
)
public record ProfesionalRequestDto(
        @Schema(
                description = "Nombres del profesional",
                example = "Carlos Alberto",
                required = true
        )
        @NotBlank(message = "Los nombres son obligatorios")
        String nombres,

        @Schema(
                description = "Apellidos del profesional",
                example = "García López",
                required = true
        )
        @NotBlank(message = "Los apellidos son obligatorios")
        String apellidos,

        @Schema(
                description = "Especialidad del profesional",
                example = "psicología",
                required = true
        )
        @NotBlank(message = "La especialidad es obligatoria")
        String especialidad
) {
}
