package org.mitocode.application.port.in;

import io.smallrye.mutiny.Uni;
import org.mitocode.application.command.CreateUpdateClientCommand;
import org.mitocode.application.command.CreateUpdateProfesionalCommand;
import org.mitocode.application.response.ApiResponse;
import org.mitocode.application.response.PageResponseDto;
import org.mitocode.domain.model.Cliente;
import org.mitocode.domain.model.Profesional;

public interface CrudProfesionalUseCase {

    Uni<ApiResponse<Profesional>> create(CreateUpdateProfesionalCommand command);
    Uni<ApiResponse<Profesional>> update(CreateUpdateProfesionalCommand command, String id);
    Uni<PageResponseDto<Profesional>> listAll(int page, int limit);
    Uni<ApiResponse<Profesional>> findById(String id);
    Uni<Void> deleteById(String id);
}
