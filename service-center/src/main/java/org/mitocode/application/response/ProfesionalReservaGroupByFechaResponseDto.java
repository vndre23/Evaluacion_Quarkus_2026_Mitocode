package org.mitocode.application.response;

import io.quarkus.runtime.annotations.RegisterForReflection;
import org.mitocode.domain.model.Reserva;

import java.time.LocalDate;
import java.util.List;

@RegisterForReflection
public record ProfesionalReservaGroupByFechaResponseDto(
        LocalDate fecha,
        List<Reserva> reservas
) {

}
