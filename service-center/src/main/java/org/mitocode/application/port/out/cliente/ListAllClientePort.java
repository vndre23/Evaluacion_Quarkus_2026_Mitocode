package org.mitocode.application.port.out.cliente;

import io.smallrye.mutiny.Uni;
import org.mitocode.application.response.PageResponseDto;
import org.mitocode.domain.model.Cliente;

public interface ListAllClientePort {
    Uni<PageResponseDto<Cliente>> listAll(int page, int limit);
}
