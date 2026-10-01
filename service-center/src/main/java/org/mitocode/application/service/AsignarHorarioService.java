package org.mitocode.application.service;

import io.smallrye.mutiny.Uni;
import jakarta.enterprise.context.ApplicationScoped;
import lombok.extern.slf4j.Slf4j;
import org.mitocode.application.command.AsignarHorarioProfesionalCommand;
import org.mitocode.application.exceptions.BusinessException;
import org.mitocode.application.port.in.AsignarHorarioProfesionalUseCase;
import org.mitocode.application.port.out.horariodisponible.ExistsHorarioSolapadoPort;
import org.mitocode.application.port.out.horariodisponible.SaveHorarioDisponiblePort;
import org.mitocode.application.port.out.profesional.FindByIdProfesionalPort;
import org.mitocode.application.response.ApiResponse;
import org.mitocode.domain.enums.ErrorType;
import org.mitocode.domain.model.HorarioDisponible;
import org.mitocode.domain.model.Profesional;

import java.util.UUID;

@ApplicationScoped
@Slf4j
public class AsignarHorarioService implements AsignarHorarioProfesionalUseCase {

    private final ExistsHorarioSolapadoPort existsHorarioSolapadoPort;
    private final SaveHorarioDisponiblePort saveHorarioDisponiblePort;
    private final FindByIdProfesionalPort findByIdProfesionalPort;

    public AsignarHorarioService(ExistsHorarioSolapadoPort existsHorarioSolapadoPort, SaveHorarioDisponiblePort saveHorarioDisponiblePort, FindByIdProfesionalPort findByIdProfesionalPort) {
        this.existsHorarioSolapadoPort = existsHorarioSolapadoPort;
        this.saveHorarioDisponiblePort = saveHorarioDisponiblePort;
        this.findByIdProfesionalPort = findByIdProfesionalPort;
    }

    @Override
    public Uni<ApiResponse<UUID>> asignarHorarioDisponible(AsignarHorarioProfesionalCommand command) {

        if (!command.horaInicio().isBefore(command.horaFin())) {
            return Uni.createFrom().failure(
                    new BusinessException(
                            ErrorType.VALIDATION_ERROR_HORA,
                            ErrorType.VALIDATION_ERROR_HORA.getDescription())
            );
        }

        return findByIdProfesionalPort.findById(command.profesionalId().toString())
                .onItem().ifNull().failWith(() ->
                        new BusinessException(
                                ErrorType.BUSINESS_NOT_FOUND_ERROR,
                                ErrorType.BUSINESS_NOT_FOUND_ERROR.getDescription())
                )
                .chain(profe ->
                        existsHorarioSolapadoPort.existsSolapamiento(
                                        profe.getId(),
                                        command.fecha(),
                                        command.horaInicio(),
                                        command.horaFin()
                                )
                                .chain(existe -> {

                                    if (existe) {
                                        return Uni.createFrom().<HorarioDisponible>failure(
                                                new BusinessException(
                                                        ErrorType.BUSINESS_HORARIO_SOLAPADO,
                                                        ErrorType.BUSINESS_HORARIO_SOLAPADO.getDescription())
                                        );
                                    }

                                    HorarioDisponible horario = HorarioDisponible.builder()
                                            .profesional(profe)
                                            .fecha(command.fecha())
                                            .horaInicio(command.horaInicio())
                                            .horaFin(command.horaFin())
                                            .build();

                                    return saveHorarioDisponiblePort.save(horario);
                                })
                )
                .map(horario -> {
                    ApiResponse<UUID> response = new ApiResponse<>();
                    response.setData(horario.getId());
                    return response;
                });
    }
}
