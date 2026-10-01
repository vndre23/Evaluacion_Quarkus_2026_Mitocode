package org.mitocode.application.service;

import io.smallrye.mutiny.Uni;
import jakarta.enterprise.context.ApplicationScoped;
import lombok.extern.slf4j.Slf4j;
import org.mitocode.application.command.RegistrarReservaCommand;
import org.mitocode.application.exceptions.BusinessException;
import org.mitocode.application.port.in.CancelReservaUseCase;
import org.mitocode.application.port.in.RegistrarReservaUseCase;
import org.mitocode.application.port.out.cliente.FindByIdClientePort;
import org.mitocode.application.port.out.profesional.FindByIdProfesionalPort;
import org.mitocode.application.port.out.horariodisponible.ExistsHorarioDisponibleReservaPort;
import org.mitocode.application.port.out.reserva.*;
import org.mitocode.application.response.ApiResponse;
import org.mitocode.domain.enums.ErrorType;
import org.mitocode.domain.enums.ReservaEstado;
import org.mitocode.domain.model.Cliente;
import org.mitocode.domain.model.Profesional;
import org.mitocode.domain.model.Reserva;

import java.util.UUID;

@ApplicationScoped
@Slf4j
public class ReservaService implements RegistrarReservaUseCase, CancelReservaUseCase {

    private final FindByIdClientePort findByIdClientePort;
    private final FindByIdProfesionalPort findByIdProfesionalPort;
    private final ExistsHorarioDisponibleReservaPort existsHorarioDisponiblePort;
    private final ExistsReservaSolapadaPort existsReservaSolapadaPort;
    private final SaveReservaPort saveReservaPort;

    private final ReservaFindById reservaFindById;
    private final CancelReservaPort cancelReservaPort;

    public ReservaService(FindByIdClientePort findByIdClientePort,
                          FindByIdProfesionalPort findByIdProfesionalPort,
                          ExistsHorarioDisponibleReservaPort existsHorarioDisponiblePort,
                          ExistsReservaSolapadaPort existsReservaSolapadaPort,
                          SaveReservaPort saveReservaPort,
                          ReservaFindById reservaFindById,
                          CancelReservaPort cancelReservaPort) {

        this.findByIdClientePort = findByIdClientePort;
        this.findByIdProfesionalPort = findByIdProfesionalPort;
        this.existsHorarioDisponiblePort = existsHorarioDisponiblePort;
        this.existsReservaSolapadaPort = existsReservaSolapadaPort;
        this.saveReservaPort = saveReservaPort;
        this.reservaFindById = reservaFindById;
        this.cancelReservaPort = cancelReservaPort;
    }

    @Override
    public Uni<ApiResponse<UUID>> execute(RegistrarReservaCommand command) {

        if (!command.horaInicio().isBefore(command.horaFin())) {
            return Uni.createFrom().failure(
                    new BusinessException(
                            ErrorType.VALIDATION_ERROR_HORA,
                            ErrorType.VALIDATION_ERROR_HORA.getDescription())
            );
        }

        return findByIdClientePort.findById(command.clienteId().toString())
                .onItem().ifNull().failWith(() -> new BusinessException(
                        ErrorType.BUSINESS_NOT_FOUND_ERROR,
                        ErrorType.BUSINESS_NOT_FOUND_ERROR.getDescription()))
                .chain(cliente -> {
                    if (!cliente.isEstadoActivo()) {
                        return Uni.createFrom().failure(
                                new BusinessException(
                                        ErrorType.BUSINESS_NOT_ACTIVATED_CLIENTE,
                                        ErrorType.BUSINESS_NOT_ACTIVATED_CLIENTE.getDescription())
                                );
                    }

                    return Uni.createFrom().item(cliente);
                })
                .chain(cliente -> findByIdProfesionalPort.findById(command.profesionalId().toString())
                        .onItem().ifNull().failWith(() -> new BusinessException(
                                ErrorType.BUSINESS_NOT_FOUND_ERROR,
                                ErrorType.BUSINESS_NOT_FOUND_ERROR.getDescription()))
                        .chain(profesional -> {
                            if (!profesional.isEstadoActivo()) {
                                return Uni.createFrom()
                                        .failure(new BusinessException(
                                                ErrorType.BUSINESS_NOT_ACTIVATED_PROFESIONAL,
                                                ErrorType.BUSINESS_NOT_ACTIVATED_PROFESIONAL.getDescription()));
                            }

                            return Uni.combine().all()
                                    .unis(
                                            Uni.createFrom().item(cliente),
                                            Uni.createFrom().item(profesional)
                                    ).asTuple();
                        })

                ).chain(tuple-> {
                    Cliente cliente = tuple.getItem1();
                    Profesional profesional = tuple.getItem2();

                    return existsHorarioDisponiblePort.existsHorarioDisponible(
                            profesional.getId(),
                            command.fecha(),
                            command.horaInicio(),
                            command.horaFin()
                    ).chain(existe -> {
                        if (!existe) {
                            return Uni.createFrom()
                                    .failure(new BusinessException(
                                            ErrorType.BUSINESS_NOT_HORARIO_DISPONIBLE,
                                            ErrorType.BUSINESS_NOT_HORARIO_DISPONIBLE.getDescription()));
                        }

                        return existsReservaSolapadaPort.existsReservaSolapada(
                                profesional.getId(),
                                command.fecha(),
                                command.horaInicio(),
                                command.horaFin()
                        ).chain(solapada -> {
                            if (solapada) {
                                return Uni.createFrom()
                                        .failure(new BusinessException(
                                                ErrorType.BUSINESS_CON_RESERVA,
                                                ErrorType.BUSINESS_CON_RESERVA.getDescription()));
                            }

                            Reserva reserva = Reserva.builder()
                                    .fecha(command.fecha())
                                    .horaInicio(command.horaInicio())
                                    .horaFin(command.horaFin())
                                    .cliente(cliente)
                                    .profesional(profesional)
                                    .estado(ReservaEstado.CREADA)
                                    .build();

                            return saveReservaPort.save(reserva);
                        });
                    });
                }).map(reserva -> {
                    ApiResponse<UUID> response = new ApiResponse<>();
                    response.setData(reserva.getId());
                    return response;
                });

    }

    @Override
    public Uni<ApiResponse<Reserva>> cancelar(UUID reservaId) {
        return reservaFindById.findById(reservaId.toString())
                .onItem().ifNull().failWith(() -> new BusinessException(
                        ErrorType.BUSINESS_NOT_FOUND_ERROR,
                        ErrorType.BUSINESS_NOT_FOUND_ERROR.getDescription()))
                .chain(reserva -> {
                    if (reserva.getEstado() != ReservaEstado.CREADA) {
                        return Uni.createFrom().failure(
                                new BusinessException(
                                        ErrorType.VALIDATION_ERROR_CANCELAR,
                                        ErrorType.VALIDATION_ERROR_CANCELAR.getDescription())
                        );
                    }
                    reserva.setEstado(ReservaEstado.CANCELADA);

                    return cancelReservaPort.cancelar(reservaId);
                }).map(reserva -> {
                    ApiResponse<Reserva> response = new ApiResponse<>();
                    response.setData(new Reserva(reservaId, ReservaEstado.CANCELADA));
                    return response;
                });

    }
}
