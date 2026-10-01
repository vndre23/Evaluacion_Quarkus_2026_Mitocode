package org.mitocode.application.port.out.profesional;

import io.smallrye.mutiny.Uni;
import org.mitocode.domain.model.Profesional;

public interface FindByIdProfesionalPort {

    Uni<Profesional> findById(String id);
}
