package org.mitocode.application.port.out.profesional;

import io.smallrye.mutiny.Uni;
import org.mitocode.domain.model.Cliente;
import org.mitocode.domain.model.Profesional;

public interface SaveProfesionalPort {
    Uni<Profesional> save(Profesional profesional);
}
