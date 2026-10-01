package org.mitocode.application.port.out.profesional;

import io.smallrye.mutiny.Uni;
import org.mitocode.domain.model.Cliente;
import org.mitocode.domain.model.Profesional;

public interface UpdateProfesionalByIdPort {
    Uni<Profesional> update(Profesional profesional);
}
