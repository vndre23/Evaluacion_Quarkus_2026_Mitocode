package org.mitocode.application.response;

import org.mitocode.domain.model.Profesional;
import org.mitocode.domain.model.Reserva;

import java.util.List;

public record ProfesionalReservaResponseDto(
        Profesional profesional,
        List<ReservaResponseDto> reservas
        //Map<LocalDate, List<Reserva>> reservasPorFecha
) {
}
