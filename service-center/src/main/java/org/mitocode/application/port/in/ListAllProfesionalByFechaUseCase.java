package org.mitocode.application.port.in;

import io.smallrye.mutiny.Uni;
import org.mitocode.application.response.PageResponseDto;
import org.mitocode.application.response.ProfesionalReservaResponseDto;

import java.util.List;

public interface ListAllProfesionalByFechaUseCase {

    Uni<PageResponseDto<ProfesionalReservaResponseDto>> listAllByFecha(int page, int limit);
}
