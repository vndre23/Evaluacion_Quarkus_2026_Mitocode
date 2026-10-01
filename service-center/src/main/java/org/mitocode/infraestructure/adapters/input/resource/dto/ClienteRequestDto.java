package org.mitocode.infraestructure.adapters.input.resource.dto;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import org.eclipse.microprofile.openapi.annotations.media.Schema;

@Schema( name = "ClientRequestDto", description = "Información requerida para crear un cliente" )
public record ClienteRequestDto(
        @Schema( description = "Nombres del cliente",
                example = "Juan Carlos", required = true )
        @NotBlank(message = "Los nombres son obligatorios")
        String nombres,

        @Schema( description = "Apellidos del cliente",
                example = "Pérez García", required = true )
        @NotBlank(message = "Los apellidos son obligatorios")
        String apellidos,

        @Schema( description = "Correo electrónico del cliente",
                example = "juan.perez@gmail.com", required = true )
        @NotBlank(message = "El email es obligatorio")
        @Email(message = "El email no tiene un formato válido")
        String email,

        @Schema( description = "Número telefónico del cliente",
        example = "+51 999000111", required = true )
        @NotBlank(message = "El teléfono es obligatorio")
        String telefono
) {
}
