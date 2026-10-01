package org.mitocode.application.response;

import org.mitocode.domain.model.Reserva;

import java.time.LocalDate;
import java.util.List;

public record ProfesionalReservaGroupByFechaResponseDto(
        LocalDate fecha,
        List<Reserva> reservas
) {

}
