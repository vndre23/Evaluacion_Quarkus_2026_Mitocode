package org.mitocode.application.port.out.horariodisponible;

import io.smallrye.mutiny.Uni;

import java.time.LocalDate;
import java.time.LocalTime;
import java.util.UUID;

public interface ExistsHorarioDisponibleReservaPort {

    Uni<Boolean> existsHorarioDisponible(
            UUID profesionalId,
            LocalDate fecha,
            LocalTime horaInicio,
            LocalTime horaFin
    );
}
