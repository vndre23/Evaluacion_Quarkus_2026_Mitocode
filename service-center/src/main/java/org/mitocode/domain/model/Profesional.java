package org.mitocode.domain.model;

import com.fasterxml.jackson.annotation.JsonInclude;
import io.quarkus.runtime.annotations.RegisterForReflection;
import lombok.*;

import java.util.List;
import java.util.UUID;

@Getter
@AllArgsConstructor
@NoArgsConstructor
@Builder
@Setter
@JsonInclude(JsonInclude.Include.NON_NULL)
@RegisterForReflection
public class Profesional {

    private UUID id;
    private String nombres;
    private String apellidos;
    private String especialidad;
    private boolean estadoActivo;
    private List<Reserva> reservas;

    public Profesional(UUID id, String nombres, String apellidos, String especialidad, boolean estadoActivo) {
        this.id = id;
        this.nombres = nombres;
        this.apellidos = apellidos;
        this.especialidad = especialidad;
        this.estadoActivo = estadoActivo;
    }

    public Profesional(String nombres, String apellidos, String especialidad) {
        this.nombres = nombres;
        this.apellidos = apellidos;
        this.especialidad = especialidad;
        this.estadoActivo = true;
    }

    public Profesional(UUID id) {
        this.id = id;
        this.estadoActivo = true;
    }
}
