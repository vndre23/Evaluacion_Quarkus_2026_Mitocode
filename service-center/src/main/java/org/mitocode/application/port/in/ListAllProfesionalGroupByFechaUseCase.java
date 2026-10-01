package org.mitocode.application.port.in;

import io.smallrye.mutiny.Uni;
import org.mitocode.application.response.PageResponseDto;
import org.mitocode.application.response.ProfesionalReservaGroupByFechaResponseDto;

public interface ListAllProfesionalGroupByFechaUseCase {

    Uni<PageResponseDto<ProfesionalReservaGroupByFechaResponseDto>> listAllGroupByFecha(int page, int limit);
}
