package org.mitocode.application.port.in;

import io.smallrye.mutiny.Uni;
import org.mitocode.application.response.ApiResponse;
import org.mitocode.domain.model.Reserva;

import java.util.UUID;

public interface CancelReservaUseCase {

    Uni<ApiResponse<Reserva>> cancelar(UUID reservaId);
}
