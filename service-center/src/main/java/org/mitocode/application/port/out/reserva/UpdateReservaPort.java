package org.mitocode.application.port.out.reserva;

import io.smallrye.mutiny.Uni;
import org.mitocode.application.command.RegistrarReservaCommand;
import org.mitocode.domain.model.Reserva;

public interface UpdateReservaPort {
    Uni<Reserva> update(Reserva reserva);
}
