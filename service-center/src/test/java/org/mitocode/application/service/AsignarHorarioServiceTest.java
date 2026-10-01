package org.mitocode.application.service;

import io.smallrye.mutiny.Uni;
import io.smallrye.mutiny.helpers.test.UniAssertSubscriber;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mitocode.application.command.AsignarHorarioProfesionalCommand;
import org.mitocode.application.exceptions.BusinessException;
import org.mitocode.application.port.out.horariodisponible.ExistsHorarioSolapadoPort;
import org.mitocode.application.port.out.horariodisponible.SaveHorarioDisponiblePort;
import org.mitocode.application.port.out.profesional.FindByIdProfesionalPort;
import org.mitocode.application.response.ApiResponse;
import org.mitocode.domain.enums.ErrorType;
import org.mitocode.domain.model.HorarioDisponible;
import org.mitocode.domain.model.Profesional;
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
class AsignarHorarioServiceTest {

    @Mock
    private ExistsHorarioSolapadoPort existsHorarioSolapadoPort;
    @Mock
    private SaveHorarioDisponiblePort saveHorarioDisponiblePort;
    @Mock
    private FindByIdProfesionalPort findByIdProfesionalPort;

    private AsignarHorarioService asignarHorarioService;

    //para el body
    private UUID profesionalId;
    private UUID horarioId;
    private LocalDate fecha;
    private LocalTime horaInicio;
    private LocalTime horaFin;

    @BeforeEach
    void setUp() {
        asignarHorarioService = new AsignarHorarioService(existsHorarioSolapadoPort,
                saveHorarioDisponiblePort, findByIdProfesionalPort);

    }

    @Test
    void asignarHorarioDisponible_createsHorario_whenDataIsValid() {

        profesionalId = UUID.randomUUID();
        horarioId = UUID.randomUUID();
        fecha = LocalDate.of(2026, 10, 1);
        horaInicio = LocalTime.of(9, 0);
        horaFin = LocalTime.of(10, 0);

        AsignarHorarioProfesionalCommand command =
                new AsignarHorarioProfesionalCommand( profesionalId, fecha, horaInicio, horaFin );
        Profesional profesional = Profesional.builder()
                .id(profesionalId).build();
        HorarioDisponible horarioGuardado = HorarioDisponible.builder()
                .id(horarioId)
                .profesional(profesional)
                .fecha(fecha)
                .horaInicio(horaInicio)
                .horaFin(horaFin)
                .build();

        when(findByIdProfesionalPort.findById(profesionalId.toString()))
                .thenReturn(Uni.createFrom().item(profesional));

        when(existsHorarioSolapadoPort
                .existsSolapamiento(eq(profesional.getId()), eq(fecha), eq(horaInicio), eq(horaFin)))
                .thenReturn(Uni.createFrom().item(false));

        when(saveHorarioDisponiblePort.save(any(HorarioDisponible.class)))
                .thenReturn(Uni.createFrom().item(horarioGuardado));

        UniAssertSubscriber<ApiResponse<UUID>> subscriber =
                asignarHorarioService.asignarHorarioDisponible(command)
                        .subscribe()
                        .withSubscriber(UniAssertSubscriber.create());

        ApiResponse<UUID> response = subscriber.assertCompleted().getItem();

        assertNotNull(response);
        assertEquals(horarioId, response.getData());

        verify(findByIdProfesionalPort).findById(profesionalId.toString());
        verify(existsHorarioSolapadoPort)
                .existsSolapamiento(eq(profesionalId), eq(fecha), eq(horaInicio), eq(horaFin));
        verify(saveHorarioDisponiblePort).save(any(HorarioDisponible.class));
    }

    @Test
    void asignarHorarioDisponible_returnsValidationError_whenHoraInicioIsAfterHoraFin() {
        profesionalId = UUID.randomUUID();
        fecha = LocalDate.of(2026, 9, 30);
        horaInicio = LocalTime.of(11, 0);
        horaFin = LocalTime.of(10, 0);

        AsignarHorarioProfesionalCommand command =
                new AsignarHorarioProfesionalCommand(
                        profesionalId,
                        fecha,
                        horaInicio,
                        horaFin
                );
        UniAssertSubscriber<ApiResponse<UUID>> subscriber =
                asignarHorarioService
                        .asignarHorarioDisponible(command)
                        .subscribe()
                        .withSubscriber(UniAssertSubscriber.create());

        subscriber.assertFailedWith(
                BusinessException.class,
                ErrorType.VALIDATION_ERROR_HORA.getDescription()
        );

        verifyNoInteractions(findByIdProfesionalPort);
        verifyNoInteractions(existsHorarioSolapadoPort);
        verifyNoInteractions(saveHorarioDisponiblePort);
    }

    @Test
    void asignarHorarioDisponible_returnsValidationError_whenHoraInicioEqualsHoraFin() {

        profesionalId = UUID.randomUUID();

        fecha = LocalDate.of(2026, 9, 30);
        LocalTime hora = LocalTime.of(10, 0);

        AsignarHorarioProfesionalCommand command =
                new AsignarHorarioProfesionalCommand(
                        profesionalId,
                        fecha,
                        hora,
                        hora
                );

        UniAssertSubscriber<ApiResponse<UUID>> subscriber =
                asignarHorarioService
                        .asignarHorarioDisponible(command)
                        .subscribe()
                        .withSubscriber(UniAssertSubscriber.create());

        subscriber.assertFailedWith(
                BusinessException.class,
                ErrorType.VALIDATION_ERROR_HORA.getDescription()
        );

        verifyNoInteractions(findByIdProfesionalPort);
        verifyNoInteractions(existsHorarioSolapadoPort);
        verifyNoInteractions(saveHorarioDisponiblePort);
    }

    @Test
    void asignarHorarioDisponible_returnsNotFound_whenProfesionalDoesNotExist() {

        profesionalId = UUID.randomUUID();

        fecha = LocalDate.of(2026, 9, 30);
        horaInicio = LocalTime.of(9, 0);
        horaFin = LocalTime.of(10, 0);

        AsignarHorarioProfesionalCommand command =
                new AsignarHorarioProfesionalCommand(
                        profesionalId,
                        fecha,
                        horaInicio,
                        horaFin
                );

        when(findByIdProfesionalPort.findById(profesionalId.toString()))
                .thenReturn(Uni.createFrom().nullItem());

        UniAssertSubscriber<ApiResponse<UUID>> subscriber =
                asignarHorarioService
                        .asignarHorarioDisponible(command)
                        .subscribe()
                        .withSubscriber(UniAssertSubscriber.create());

        subscriber.assertFailedWith(
                BusinessException.class,
                ErrorType.BUSINESS_NOT_FOUND_ERROR.getDescription()
        );

        verify(findByIdProfesionalPort)
                .findById(profesionalId.toString());

        verifyNoInteractions(existsHorarioSolapadoPort);
        verifyNoInteractions(saveHorarioDisponiblePort);
    }

    @Test
    void asignarHorarioDisponible_returnsOverlapError_whenHorarioAlreadyExists() {

        profesionalId = UUID.randomUUID();

        fecha = LocalDate.of(2026, 9, 30);
        horaInicio = LocalTime.of(9, 0);
        horaFin = LocalTime.of(10, 0);

        AsignarHorarioProfesionalCommand command =
                new AsignarHorarioProfesionalCommand(
                        profesionalId,
                        fecha,
                        horaInicio,
                        horaFin
                );

        Profesional profesional = Profesional.builder()
                .id(profesionalId)
                .build();

        when(findByIdProfesionalPort.findById(profesionalId.toString()))
                .thenReturn(Uni.createFrom().item(profesional));

        when(existsHorarioSolapadoPort.existsSolapamiento(
                eq(profesional.getId()),
                eq(fecha),
                eq(horaInicio),
                eq(horaFin)
        )).thenReturn(Uni.createFrom().item(true));

        UniAssertSubscriber<ApiResponse<UUID>> subscriber =
                asignarHorarioService
                        .asignarHorarioDisponible(command)
                        .subscribe()
                        .withSubscriber(UniAssertSubscriber.create());

        subscriber.assertFailedWith(
                BusinessException.class,
                ErrorType.BUSINESS_HORARIO_SOLAPADO.getDescription()
        );

        verify(findByIdProfesionalPort)
                .findById(profesionalId.toString());

        verify(existsHorarioSolapadoPort)
                .existsSolapamiento(
                        eq(profesional.getId()),
                        eq(fecha),
                        eq(horaInicio),
                        eq(horaFin)
                );

        verifyNoInteractions(saveHorarioDisponiblePort);
    }


}