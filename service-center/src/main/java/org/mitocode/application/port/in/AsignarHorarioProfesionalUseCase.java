package org.mitocode.application.port.in;

import io.smallrye.mutiny.Uni;
import org.mitocode.application.command.AsignarHorarioProfesionalCommand;
import org.mitocode.application.response.ApiResponse;

import java.util.UUID;

public interface AsignarHorarioProfesionalUseCase {

    Uni<ApiResponse<UUID>> asignarHorarioDisponible(AsignarHorarioProfesionalCommand command);
}
