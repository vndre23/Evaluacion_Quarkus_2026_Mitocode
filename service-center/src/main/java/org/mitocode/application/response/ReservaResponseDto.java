package org.mitocode.application.response;

import org.mitocode.domain.enums.ReservaEstado;
import org.mitocode.domain.model.Cliente;
import org.mitocode.domain.model.Profesional;

import java.time.LocalDate;
import java.time.LocalTime;
import java.util.UUID;

public record ReservaResponseDto(
        UUID id,
        LocalDate fecha,
        LocalTime horaInicio,
        LocalTime horaFin,
        Cliente cliente,
        ReservaEstado estado
) {
}
