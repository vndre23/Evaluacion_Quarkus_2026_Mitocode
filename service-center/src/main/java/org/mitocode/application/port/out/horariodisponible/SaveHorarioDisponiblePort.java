package org.mitocode.application.port.out.horariodisponible;

import io.smallrye.mutiny.Uni;
import org.mitocode.domain.model.HorarioDisponible;

public interface SaveHorarioDisponiblePort {

    Uni<HorarioDisponible> save(HorarioDisponible horario);
}
