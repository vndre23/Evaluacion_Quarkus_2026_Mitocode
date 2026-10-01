package org.mitocode.application.port.in;

import io.smallrye.mutiny.Uni;
import org.mitocode.application.command.RegistrarReservaCommand;
import org.mitocode.application.response.ApiResponse;

import java.util.UUID;

public interface RegistrarReservaUseCase {

    Uni<ApiResponse<UUID>> execute(RegistrarReservaCommand command);

}
