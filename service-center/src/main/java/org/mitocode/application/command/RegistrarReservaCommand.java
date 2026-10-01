package org.mitocode.application.command;

import java.time.LocalDate;
import java.time.LocalTime;
import java.util.UUID;

public record RegistrarReservaCommand(
        UUID clienteId,
        UUID profesionalId,
        LocalDate fecha,
        LocalTime horaInicio,
        LocalTime horaFin
) {
}