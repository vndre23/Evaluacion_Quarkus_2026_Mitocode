package org.mitocode.application.port.out.cliente;

import io.smallrye.mutiny.Uni;

public interface DeleteByIdClientePort {

    Uni<Void> deleteById(String id);
}
