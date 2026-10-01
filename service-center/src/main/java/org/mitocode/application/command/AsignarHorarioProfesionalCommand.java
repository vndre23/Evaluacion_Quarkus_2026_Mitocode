package org.mitocode.application.command;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.UUID;

public record AsignarHorarioProfesionalCommand(
        UUID profesionalId,
        LocalDate fecha,
        LocalTime horaInicio,
        LocalTime horaFin
) {
}
