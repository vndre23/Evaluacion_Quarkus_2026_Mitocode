package org.mitocode.application.response;

import io.quarkus.runtime.annotations.RegisterForReflection;
import org.mitocode.domain.model.Profesional;
import org.mitocode.domain.model.Reserva;

import java.util.List;

@RegisterForReflection
public record ProfesionalReservaResponseDto(
        Profesional profesional,
        List<ReservaResponseDto> reservas
        //Map<LocalDate, List<Reserva>> reservasPorFecha
) {
}
