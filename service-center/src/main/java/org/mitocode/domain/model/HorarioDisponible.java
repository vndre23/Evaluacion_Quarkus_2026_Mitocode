package org.mitocode.domain.model;

import io.quarkus.runtime.annotations.RegisterForReflection;
import lombok.*;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.UUID;

@Getter
@AllArgsConstructor
@NoArgsConstructor
@Builder
@Setter
@RegisterForReflection
public class HorarioDisponible {

    private UUID id;
    private Profesional profesional;
    private LocalDate fecha;
    private LocalTime horaInicio;
    private LocalTime horaFin;

}
