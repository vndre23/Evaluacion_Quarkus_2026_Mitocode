package org.mitocode.domain.model;

import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.*;

import java.util.UUID;

@Getter
@AllArgsConstructor
@NoArgsConstructor
@Builder
@Setter
@JsonInclude(JsonInclude.Include.NON_NULL)
public class Cliente {

    private UUID id;
    private String nombres;
    private String apellidos;
    private String email;
    private String telefono;
    private boolean estadoActivo;

    public Cliente(String nombres, String apellidos, String email, String telefono) {
        this.nombres = nombres;
        this.apellidos = apellidos;
        this.email = email;
        this.telefono = telefono;
        this.estadoActivo = true;
    }

    public Cliente(UUID id) {
        this.id = id;
        this.estadoActivo = true;
    }
}
