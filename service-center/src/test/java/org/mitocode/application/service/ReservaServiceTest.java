package org.mitocode.application.service;

import io.smallrye.mutiny.Uni;
import io.smallrye.mutiny.helpers.test.UniAssertSubscriber;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mitocode.application.command.RegistrarReservaCommand;
import org.mitocode.application.exceptions.BusinessException;
import org.mitocode.application.port.out.cliente.FindByIdClientePort;
import org.mitocode.application.port.out.horariodisponible.ExistsHorarioDisponibleReservaPort;
import org.mitocode.application.port.out.profesional.FindByIdProfesionalPort;
import org.mitocode.application.port.out.reserva.CancelReservaPort;
import org.mitocode.application.port.out.reserva.ExistsReservaSolapadaPort;
import org.mitocode.application.port.out.reserva.ReservaFindById;
import org.mitocode.application.port.out.reserva.SaveReservaPort;
import org.mitocode.application.response.ApiResponse;
import org.mitocode.domain.enums.ErrorType;
import org.mitocode.domain.enums.ReservaEstado;
import org.mitocode.domain.model.Cliente;
import org.mitocode.domain.model.Profesional;
import org.mitocode.domain.model.Reserva;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDate;
import java.time.LocalTime;
import java.util.UUID;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.when;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoInteractions;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

@ExtendWith(MockitoExtension.class)
class ReservaServiceTest {

    @Mock
    private FindByIdClientePort findByIdClientePort;
    @Mock
    private FindByIdProfesionalPort findByIdProfesionalPort;
    @Mock
    private ExistsHorarioDisponibleReservaPort existsHorarioDisponiblePort;
    @Mock
    private ExistsReservaSolapadaPort existsReservaSolapadaPort;
    @Mock
    private SaveReservaPort saveReservaPort;
    @Mock
    private ReservaFindById reservaFindById;
    @Mock
    private CancelReservaPort cancelReservaPort;

    private ReservaService reservaService;

    @BeforeEach
    void setUp() {
        reservaService = new ReservaService(findByIdClientePort,
                findByIdProfesionalPort, existsHorarioDisponiblePort, existsReservaSolapadaPort,
                saveReservaPort, reservaFindById, cancelReservaPort);
    }


    @Test
    void registrarReserva_whenDataIsValid() {

        UUID clienteId = UUID.randomUUID();
        UUID profesionalId = UUID.randomUUID();
        UUID reservaId = UUID.randomUUID();

        LocalDate fecha = LocalDate.of(2026, 10, 1);
        LocalTime horaInicio = LocalTime.of(10, 0);
        LocalTime horaFin = LocalTime.of(11, 0);

        RegistrarReservaCommand command =
                new RegistrarReservaCommand(
                        clienteId,
                        profesionalId,
                        fecha,
                        horaInicio,
                        horaFin
                );

        Cliente cliente = Cliente.builder()
                .id(clienteId)
                .estadoActivo(true)
                .build();

        Profesional profesional = Profesional.builder()
                .id(profesionalId)
                .estadoActivo(true)
                .build();

        Reserva reserva = Reserva.builder()
                .id(reservaId)
                .fecha(fecha)
                .horaInicio(horaInicio)
                .horaFin(horaFin)
                .cliente(cliente)
                .profesional(profesional)
                .estado(ReservaEstado.CREADA)
                .build();

        when(findByIdClientePort.findById(clienteId.toString()))
                .thenReturn(Uni.createFrom().item(cliente));

        when(findByIdProfesionalPort.findById(profesionalId.toString()))
                .thenReturn(Uni.createFrom().item(profesional));

        when(existsHorarioDisponiblePort.existsHorarioDisponible(
                eq(profesional.getId()),
                eq(fecha),
                eq(horaInicio),
                eq(horaFin)
        )).thenReturn(Uni.createFrom().item(true));

        when(existsReservaSolapadaPort.existsReservaSolapada(
                eq(profesional.getId()),
                eq(fecha),
                eq(horaInicio),
                eq(horaFin)
        )).thenReturn(Uni.createFrom().item(false));

        when(saveReservaPort.save(any(Reserva.class)))
                .thenReturn(Uni.createFrom().item(reserva));

        UniAssertSubscriber<ApiResponse<UUID>> subscriber =
                reservaService
                        .execute(command)
                        .subscribe()
                        .withSubscriber(UniAssertSubscriber.create());

        ApiResponse<UUID> response =
                subscriber.assertCompleted().getItem();

        assertNotNull(response);
        assertEquals(reservaId, response.getData());

        verify(findByIdClientePort)
                .findById(clienteId.toString());

        verify(findByIdProfesionalPort)
                .findById(profesionalId.toString());

        verify(existsHorarioDisponiblePort)
                .existsHorarioDisponible(
                        eq(profesional.getId()),
                        eq(fecha),
                        eq(horaInicio),
                        eq(horaFin)
                );

        verify(existsReservaSolapadaPort)
                .existsReservaSolapada(
                        eq(profesional.getId()),
                        eq(fecha),
                        eq(horaInicio),
                        eq(horaFin)
                );

        verify(saveReservaPort)
                .save(any(Reserva.class));
    }

    @Test
    void registrarReserva_whenHoraInicioIsAfterHoraFin() {

        UUID clienteId = UUID.randomUUID();
        UUID profesionalId = UUID.randomUUID();

        LocalDate fecha = LocalDate.of(2026, 10, 1);
        LocalTime horaInicio = LocalTime.of(11, 0);
        LocalTime horaFin = LocalTime.of(10, 0);

        RegistrarReservaCommand command =
                new RegistrarReservaCommand(
                        clienteId,
                        profesionalId,
                        fecha,
                        horaInicio,
                        horaFin
                );

        UniAssertSubscriber<ApiResponse<UUID>> subscriber =
                reservaService
                        .execute(command)
                        .subscribe()
                        .withSubscriber(UniAssertSubscriber.create());

        subscriber.assertFailedWith(
                BusinessException.class,
                ErrorType.VALIDATION_ERROR_HORA.getDescription()
        );

        verifyNoInteractions(findByIdClientePort);
        verifyNoInteractions(findByIdProfesionalPort);
        verifyNoInteractions(existsHorarioDisponiblePort);
        verifyNoInteractions(existsReservaSolapadaPort);
        verifyNoInteractions(saveReservaPort);
    }

    @Test
    void registrarReserva_whenClienteDoesNotExist() {

        UUID clienteId = UUID.randomUUID();
        UUID profesionalId = UUID.randomUUID();

        LocalDate fecha = LocalDate.of(2026, 10, 1);
        LocalTime horaInicio = LocalTime.of(10, 0);
        LocalTime horaFin = LocalTime.of(11, 0);

        RegistrarReservaCommand command =
                new RegistrarReservaCommand(
                        clienteId,
                        profesionalId,
                        fecha,
                        horaInicio,
                        horaFin
                );

        when(findByIdClientePort.findById(clienteId.toString()))
                .thenReturn(Uni.createFrom().nullItem());

        UniAssertSubscriber<ApiResponse<UUID>> subscriber =
                reservaService
                        .execute(command)
                        .subscribe()
                        .withSubscriber(UniAssertSubscriber.create());

        subscriber.assertFailedWith(
                BusinessException.class,
                ErrorType.BUSINESS_NOT_FOUND_ERROR.getDescription()
        );

        verify(findByIdClientePort)
                .findById(clienteId.toString());

        verifyNoInteractions(findByIdProfesionalPort);
        verifyNoInteractions(existsHorarioDisponiblePort);
        verifyNoInteractions(existsReservaSolapadaPort);
        verifyNoInteractions(saveReservaPort);
    }

    @Test
    void registrarReserva_whenClienteIsNotActive() {

        UUID clienteId = UUID.randomUUID();
        UUID profesionalId = UUID.randomUUID();

        LocalDate fecha = LocalDate.of(2026, 10, 1);
        LocalTime horaInicio = LocalTime.of(10, 0);
        LocalTime horaFin = LocalTime.of(11, 0);

        RegistrarReservaCommand command =
                new RegistrarReservaCommand(
                        clienteId,
                        profesionalId,
                        fecha,
                        horaInicio,
                        horaFin
                );

        Cliente cliente = Cliente.builder()
                .id(clienteId)
                .estadoActivo(false)
                .build();

        when(findByIdClientePort.findById(clienteId.toString()))
                .thenReturn(Uni.createFrom().item(cliente));

        UniAssertSubscriber<ApiResponse<UUID>> subscriber =
                reservaService
                        .execute(command)
                        .subscribe()
                        .withSubscriber(UniAssertSubscriber.create());

        subscriber.assertFailedWith(
                BusinessException.class,
                ErrorType.BUSINESS_NOT_ACTIVATED_CLIENTE.getDescription()
        );

        verify(findByIdClientePort)
                .findById(clienteId.toString());

        verifyNoInteractions(findByIdProfesionalPort);
        verifyNoInteractions(existsHorarioDisponiblePort);
        verifyNoInteractions(existsReservaSolapadaPort);
        verifyNoInteractions(saveReservaPort);
    }

    @Test
    void registrarReserva_whenProfesionalDoesNotExist() {

        UUID clienteId = UUID.randomUUID();
        UUID profesionalId = UUID.randomUUID();

        LocalDate fecha = LocalDate.of(2026, 10, 1);
        LocalTime horaInicio = LocalTime.of(10, 0);
        LocalTime horaFin = LocalTime.of(11, 0);

        RegistrarReservaCommand command =
                new RegistrarReservaCommand(
                        clienteId,
                        profesionalId,
                        fecha,
                        horaInicio,
                        horaFin
                );

        Cliente cliente = Cliente.builder()
                .id(clienteId)
                .estadoActivo(true)
                .build();

        when(findByIdClientePort.findById(clienteId.toString()))
                .thenReturn(Uni.createFrom().item(cliente));

        when(findByIdProfesionalPort.findById(profesionalId.toString()))
                .thenReturn(Uni.createFrom().nullItem());

        UniAssertSubscriber<ApiResponse<UUID>> subscriber =
                reservaService
                        .execute(command)
                        .subscribe()
                        .withSubscriber(UniAssertSubscriber.create());

        subscriber.assertFailedWith(
                BusinessException.class,
                ErrorType.BUSINESS_NOT_FOUND_ERROR.getDescription()
        );

        verify(findByIdClientePort)
                .findById(clienteId.toString());

        verify(findByIdProfesionalPort)
                .findById(profesionalId.toString());

        verifyNoInteractions(existsHorarioDisponiblePort);
        verifyNoInteractions(existsReservaSolapadaPort);
        verifyNoInteractions(saveReservaPort);
    }

    @Test
    void registrarReserva_whenProfesionalIsNotActive() {

        UUID clienteId = UUID.randomUUID();
        UUID profesionalId = UUID.randomUUID();

        LocalDate fecha = LocalDate.of(2026, 10, 1);
        LocalTime horaInicio = LocalTime.of(10, 0);
        LocalTime horaFin = LocalTime.of(11, 0);

        RegistrarReservaCommand command =
                new RegistrarReservaCommand(
                        clienteId,
                        profesionalId,
                        fecha,
                        horaInicio,
                        horaFin
                );

        Cliente cliente = Cliente.builder()
                .id(clienteId)
                .estadoActivo(true)
                .build();

        Profesional profesional = Profesional.builder()
                .id(profesionalId)
                .estadoActivo(false)
                .build();

        when(findByIdClientePort.findById(clienteId.toString()))
                .thenReturn(Uni.createFrom().item(cliente));

        when(findByIdProfesionalPort.findById(profesionalId.toString()))
                .thenReturn(Uni.createFrom().item(profesional));

        UniAssertSubscriber<ApiResponse<UUID>> subscriber =
                reservaService
                        .execute(command)
                        .subscribe()
                        .withSubscriber(UniAssertSubscriber.create());

        subscriber.assertFailedWith(
                BusinessException.class,
                ErrorType.BUSINESS_NOT_ACTIVATED_PROFESIONAL.getDescription()
        );

        verify(findByIdClientePort)
                .findById(clienteId.toString());

        verify(findByIdProfesionalPort)
                .findById(profesionalId.toString());

        verifyNoInteractions(existsHorarioDisponiblePort);
        verifyNoInteractions(existsReservaSolapadaPort);
        verifyNoInteractions(saveReservaPort);
    }

    @Test
    void registrarReserva_whenHorarioIsNotAvailable() {

        UUID clienteId = UUID.randomUUID();
        UUID profesionalId = UUID.randomUUID();

        LocalDate fecha = LocalDate.of(2026, 10, 1);
        LocalTime horaInicio = LocalTime.of(10, 0);
        LocalTime horaFin = LocalTime.of(11, 0);

        RegistrarReservaCommand command =
                new RegistrarReservaCommand(
                        clienteId,
                        profesionalId,
                        fecha,
                        horaInicio,
                        horaFin
                );

        Cliente cliente = Cliente.builder()
                .id(clienteId)
                .estadoActivo(true)
                .build();

        Profesional profesional = Profesional.builder()
                .id(profesionalId)
                .estadoActivo(true)
                .build();

        when(findByIdClientePort.findById(clienteId.toString()))
                .thenReturn(Uni.createFrom().item(cliente));

        when(findByIdProfesionalPort.findById(profesionalId.toString()))
                .thenReturn(Uni.createFrom().item(profesional));

        when(existsHorarioDisponiblePort.existsHorarioDisponible(
                eq(profesional.getId()),
                eq(fecha),
                eq(horaInicio),
                eq(horaFin)
        )).thenReturn(Uni.createFrom().item(false));

        UniAssertSubscriber<ApiResponse<UUID>> subscriber =
                reservaService
                        .execute(command)
                        .subscribe()
                        .withSubscriber(UniAssertSubscriber.create());

        subscriber.assertFailedWith(
                BusinessException.class,
                ErrorType.BUSINESS_NOT_HORARIO_DISPONIBLE.getDescription()
        );

        verify(existsHorarioDisponiblePort)
                .existsHorarioDisponible(
                        eq(profesional.getId()),
                        eq(fecha),
                        eq(horaInicio),
                        eq(horaFin)
                );

        verifyNoInteractions(existsReservaSolapadaPort);
        verifyNoInteractions(saveReservaPort);
    }

    @Test
    void registrarReserva_whenReservaIsAlreadyExists() {

        UUID clienteId = UUID.randomUUID();
        UUID profesionalId = UUID.randomUUID();

        LocalDate fecha = LocalDate.of(2026, 10, 1);
        LocalTime horaInicio = LocalTime.of(10, 0);
        LocalTime horaFin = LocalTime.of(11, 0);

        RegistrarReservaCommand command =
                new RegistrarReservaCommand(
                        clienteId,
                        profesionalId,
                        fecha,
                        horaInicio,
                        horaFin
                );

        Cliente cliente = Cliente.builder()
                .id(clienteId)
                .estadoActivo(true)
                .build();

        Profesional profesional = Profesional.builder()
                .id(profesionalId)
                .estadoActivo(true)
                .build();

        when(findByIdClientePort.findById(clienteId.toString()))
                .thenReturn(Uni.createFrom().item(cliente));

        when(findByIdProfesionalPort.findById(profesionalId.toString()))
                .thenReturn(Uni.createFrom().item(profesional));

        when(existsHorarioDisponiblePort.existsHorarioDisponible(
                eq(profesional.getId()),
                eq(fecha),
                eq(horaInicio),
                eq(horaFin)
        )).thenReturn(Uni.createFrom().item(true));

        when(existsReservaSolapadaPort.existsReservaSolapada(
                eq(profesional.getId()),
                eq(fecha),
                eq(horaInicio),
                eq(horaFin)
        )).thenReturn(Uni.createFrom().item(true));

        UniAssertSubscriber<ApiResponse<UUID>> subscriber =
                reservaService
                        .execute(command)
                        .subscribe()
                        .withSubscriber(UniAssertSubscriber.create());

        subscriber.assertFailedWith(
                BusinessException.class,
                ErrorType.BUSINESS_CON_RESERVA.getDescription()
        );

        verify(existsHorarioDisponiblePort)
                .existsHorarioDisponible(
                        eq(profesional.getId()),
                        eq(fecha),
                        eq(horaInicio),
                        eq(horaFin)
                );

        verify(existsReservaSolapadaPort)
                .existsReservaSolapada(
                        eq(profesional.getId()),
                        eq(fecha),
                        eq(horaInicio),
                        eq(horaFin)
                );

        verifyNoInteractions(saveReservaPort);
    }


    //TEST de cancelar
    @Test
    void cancelarReserva_whenDataIsValid() {
        UUID reservaId = UUID.randomUUID();

        Reserva reserva = Reserva.builder()
                .id(reservaId)
                .estado(ReservaEstado.CREADA)
                .build();

        when(reservaFindById.findById(reservaId.toString()))
                .thenReturn(Uni.createFrom().item(reserva));

        UniAssertSubscriber<ApiResponse<Reserva>> subscriber =
                reservaService.cancelar(reservaId)
                        .subscribe()
                        .withSubscriber(UniAssertSubscriber.create());

        ApiResponse<Reserva> response = subscriber.assertCompleted().getItem();

        assertNotNull(response);
        assertEquals(response.getData().getEstado(), ReservaEstado.CANCELADA);

        verify(reservaFindById).findById(reservaId.toString());
        verify(cancelReservaPort).cancelar(reservaId);
    }

    @Test
    void cancelarReserva_whenDataNotFound() {
        UUID reservaId = UUID.randomUUID();

        Reserva reserva = Reserva.builder()
                .id(reservaId)
                .estado(ReservaEstado.CREADA)
                .build();

        when(reservaFindById.findById(reservaId.toString()))
                .thenReturn(Uni.createFrom().nullItem());

        UniAssertSubscriber<ApiResponse<Reserva>> subscriber =
                reservaService.cancelar(reservaId)
                        .subscribe()
                        .withSubscriber(UniAssertSubscriber.create());

        subscriber.assertFailedWith(
                BusinessException.class,
                ErrorType.BUSINESS_NOT_FOUND_ERROR.getDescription()
        );

        verifyNoInteractions(cancelReservaPort);

    }

    @Test
    void cancelarReserva_whenReservaIsNotCreada() {
        UUID reservaId = UUID.randomUUID();

        Reserva reserva = Reserva.builder()
                .id(reservaId)
                .estado(ReservaEstado.CANCELADA)
                .build();

        when(reservaFindById.findById(reservaId.toString()))
                .thenReturn(Uni.createFrom().item(reserva));

        UniAssertSubscriber<ApiResponse<Reserva>> subscriber =
                reservaService.cancelar(reservaId)
                        .subscribe()
                        .withSubscriber(UniAssertSubscriber.create());

        subscriber.assertFailedWith(
                BusinessException.class,
                ErrorType.VALIDATION_ERROR_CANCELAR.getDescription()
        );

        verifyNoInteractions(cancelReservaPort);

    }

}