package org.mitocode.application.port.out.reserva;

import io.smallrye.mutiny.Uni;
import org.mitocode.domain.model.Reserva;

import java.util.UUID;

public interface CancelReservaPort {

    Uni<Boolean> cancelar(UUID reservaId);
}
