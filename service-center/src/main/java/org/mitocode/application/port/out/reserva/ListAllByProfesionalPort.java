package org.mitocode.application.port.out.reserva;

import io.smallrye.mutiny.Uni;
import org.mitocode.domain.model.Reserva;

import java.util.List;
import java.util.UUID;

public interface ListAllByProfesionalPort {

    Uni<List<Reserva>> listAllByProfesional(List<UUID> profesionalIds);
}
