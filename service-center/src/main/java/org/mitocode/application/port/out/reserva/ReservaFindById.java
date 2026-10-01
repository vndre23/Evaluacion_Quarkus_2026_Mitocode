package org.mitocode.application.port.out.reserva;

import io.smallrye.mutiny.Uni;
import org.mitocode.domain.model.Reserva;

public interface ReservaFindById {

    Uni<Reserva> findById(String id);
}
