package org.mitocode.domain.model;

import com.fasterxml.jackson.annotation.JsonInclude;
import io.quarkus.runtime.annotations.RegisterForReflection;
import lombok.*;
import org.mitocode.domain.enums.ReservaEstado;

import java.time.LocalDate;
import java.time.LocalTime;
import java.util.UUID;

@Getter
@AllArgsConstructor
@NoArgsConstructor
@Builder
@Setter
@JsonInclude(JsonInclude.Include.NON_NULL)
@RegisterForReflection
public class Reserva {

    private UUID id;
    private LocalDate fecha;
    private LocalTime horaInicio;
    private LocalTime horaFin;
    private Cliente cliente;
    private Profesional profesional;
    private ReservaEstado estado;

    public Reserva(UUID id, ReservaEstado estado) {
        this.id = id;
        this.estado = estado;
    }
}
