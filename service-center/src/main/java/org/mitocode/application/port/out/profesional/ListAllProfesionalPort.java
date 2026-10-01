package org.mitocode.application.port.out.profesional;

import io.smallrye.mutiny.Uni;
import org.mitocode.application.response.PageResponseDto;
import org.mitocode.domain.model.Profesional;

public interface ListAllProfesionalPort {
    Uni<PageResponseDto<Profesional>> listAll(int page, int limit);
}
