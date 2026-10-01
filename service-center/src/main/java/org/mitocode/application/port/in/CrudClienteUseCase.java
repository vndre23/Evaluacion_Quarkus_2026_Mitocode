package org.mitocode.application.port.in;

import io.smallrye.mutiny.Uni;
import org.mitocode.application.command.CreateUpdateClientCommand;
import org.mitocode.application.response.ApiResponse;
import org.mitocode.application.response.PageResponseDto;
import org.mitocode.domain.model.Cliente;

public interface CrudClienteUseCase {

    Uni<ApiResponse<Cliente>> create(CreateUpdateClientCommand command);
    Uni<ApiResponse<Cliente>> update(CreateUpdateClientCommand command, String id);
    Uni<PageResponseDto<Cliente>> listAll(int page, int limit);
    Uni<ApiResponse<Cliente>> findById(String id);
    Uni<Void> deleteById(String id);
}
