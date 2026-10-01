package org.mitocode.application.port.out.profesional;

import io.smallrye.mutiny.Uni;

public interface DeleteByIdProfesionalPort {

    Uni<Void> deleteById(String id);
}
