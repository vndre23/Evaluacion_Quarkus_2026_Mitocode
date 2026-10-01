package org.mitocode.application.port.out.cliente;

import io.smallrye.mutiny.Uni;
import org.mitocode.domain.model.Cliente;

public interface FindByIdClientePort {

    Uni<Cliente> findById(String id);
}
