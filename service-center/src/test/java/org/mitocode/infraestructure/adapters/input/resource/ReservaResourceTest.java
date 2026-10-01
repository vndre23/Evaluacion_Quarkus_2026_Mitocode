package org.mitocode.infraestructure.adapters.input.resource;

import io.quarkus.test.InjectMock;
import io.quarkus.test.common.http.TestHTTPEndpoint;
import io.quarkus.test.junit.QuarkusTest;
import io.smallrye.mutiny.Uni;
import org.junit.jupiter.api.Test;
import org.mitocode.application.response.ApiResponse;
import org.mitocode.application.service.ReservaService;
import org.mitocode.domain.enums.ReservaEstado;
import org.mitocode.domain.model.Reserva;

import java.time.LocalDate;
import java.time.LocalTime;
import java.util.UUID;

import static org.mockito.Mockito.when;
import static io.restassured.RestAssured.given;
import static org.hamcrest.Matchers.equalTo;

@QuarkusTest
@TestHTTPEndpoint(ReservaResource.class)
class ReservaResourceTest {

    @InjectMock
    private ReservaService reservaService;

    @Test
    void reservar_returnCreated() {

        UUID clienteId = UUID.randomUUID();
        UUID profesionalId = UUID.randomUUID();
        UUID reservaId = UUID.randomUUID();

        Reserva reservaResponse = Reserva.builder()
                .id(reservaId)
                .estado(ReservaEstado.CREADA)
                .fecha(LocalDate.of(2026, 10, 1))
                .horaInicio(LocalTime.of(10, 0))
                .horaFin(LocalTime.of(11, 0))
                .build();

        ApiResponse<UUID> apiResponse = new ApiResponse<>();
        apiResponse.setData(reservaId);

        when(reservaService.execute(
                org.mockito.ArgumentMatchers.any()
        )).thenReturn(Uni.createFrom().item(apiResponse));

        given()
                .contentType("application/json")
                .body("""
                        {
                            "cliente": "%s",
                            "profesional": "%s",
                            "fecha": "2026-10-01",
                            "horaInicio": "10:00:00",
                            "horaFin": "11:00:00"
                        }
                        """.formatted(
                        clienteId,
                        profesionalId
                ))
                .when()
                .post()
                .then()
                .statusCode(201)
                .body("data", equalTo(reservaId.toString()));
    }

    @Test
    void cancelarReserva_returnOk() {

        UUID reservaId = UUID.randomUUID();
        Reserva reservaResponse = Reserva.builder()
                .estado(ReservaEstado.CANCELADA)
                .id(reservaId)
                .build();

        ApiResponse<Reserva> apiResponse = new ApiResponse<>();
        apiResponse.setData(reservaResponse);

        when(reservaService.cancelar(reservaId)).thenReturn(Uni.createFrom().item(apiResponse));

        given()
                .pathParam("id", reservaId)
                .when()
                .patch("/cancelar/{id}")
                .then()
                .statusCode(200)
                .body("data.id", equalTo(reservaId.toString()))
                .body("data.estado", equalTo("CANCELADA"));
    }

}