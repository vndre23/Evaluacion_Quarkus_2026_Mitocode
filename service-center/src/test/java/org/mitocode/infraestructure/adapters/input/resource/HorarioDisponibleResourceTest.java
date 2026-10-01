package org.mitocode.infraestructure.adapters.input.resource;

import io.quarkus.test.InjectMock;
import io.quarkus.test.common.http.TestHTTPEndpoint;
import io.quarkus.test.junit.QuarkusTest;
import io.restassured.http.ContentType;
import io.smallrye.mutiny.Uni;
import org.junit.jupiter.api.Test;
import org.mitocode.application.exceptions.BusinessException;
import org.mitocode.application.port.in.AsignarHorarioProfesionalUseCase;
import org.mitocode.application.response.ApiResponse;
import org.mitocode.domain.enums.ErrorType;
import org.mitocode.domain.model.HorarioDisponible;
import org.mitocode.infraestructure.adapters.input.resource.dto.AsignarHorarioRequestDto;

import java.time.LocalDate;
import java.time.LocalTime;
import java.util.UUID;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;
import static io.restassured.RestAssured.given;
import static org.hamcrest.Matchers.equalTo;

import static org.junit.jupiter.api.Assertions.*;

@QuarkusTest
@TestHTTPEndpoint(HorarioDisponibleResource.class)
class HorarioDisponibleResourceTest {

    @InjectMock
    private AsignarHorarioProfesionalUseCase asignarHorarioProfesionalUseCase;

    @Test
    void asignarHorario_returnCreated() {


        UUID profesionalId = UUID.randomUUID();
        UUID horarioId = UUID.randomUUID();
        LocalDate fecha = LocalDate.of(2026, 10, 1);
        LocalTime horaInicio = LocalTime.of(9, 0);
        LocalTime horaFin = LocalTime.of(10, 0);

        AsignarHorarioRequestDto requestDto =
                new AsignarHorarioRequestDto(profesionalId.toString(), fecha, horaInicio, horaFin);

        ApiResponse<UUID> apiResponse = new ApiResponse<>();
        apiResponse.setData(horarioId);

        when(asignarHorarioProfesionalUseCase.asignarHorarioDisponible(any()))
                .thenReturn(Uni.createFrom().item(apiResponse));

        given()
                .contentType("application/json")
                .body(requestDto)
                .when()
                .post()
                .then()
                .statusCode(201)
                .body("data", equalTo(horarioId.toString()));
    }


    @Test
    void asignarHorario_returnNotFound() {

        UUID profesionalId = UUID.randomUUID();

        LocalDate fecha = LocalDate.of(2026, 10, 1);
        LocalTime horaInicio = LocalTime.of(9, 0);
        LocalTime horaFin = LocalTime.of(10, 0);

        AsignarHorarioRequestDto requestDto =
                new AsignarHorarioRequestDto(
                        profesionalId.toString(),
                        fecha,
                        horaInicio,
                        horaFin
                );

        when(asignarHorarioProfesionalUseCase.asignarHorarioDisponible(any()))
                .thenReturn(
                        Uni.createFrom().failure(
                                new BusinessException(
                                        ErrorType.BUSINESS_NOT_FOUND_ERROR,
                                        ErrorType.BUSINESS_NOT_FOUND_ERROR.getDescription()
                                )
                        )
                );

        given()
                .contentType(ContentType.JSON)
                .body(requestDto)
                .when()
                .post()
                .then()
                .statusCode(404)
                .body("typeError", equalTo("BUSINESS_NOT_FOUND_ERROR"));
    }
}