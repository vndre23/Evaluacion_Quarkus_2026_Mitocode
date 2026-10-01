package org.mitocode.application.port.out.reserva;

import io.smallrye.mutiny.Uni;

import java.time.LocalDate;
import java.time.LocalTime;
import java.util.UUID;

public interface ExistsReservaSolapadaPort {

    Uni<Boolean> existsReservaSolapada(
            UUID profesionalId,
            LocalDate fecha,
            LocalTime horaInicio,
            LocalTime horaFin
    );
}
