package org.mitocode.domain.enums;

import jakarta.ws.rs.core.Response;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor(access = AccessLevel.PACKAGE)
public enum ErrorType {
    VALIDATION_ERROR("V-01", "Error en la validación de datos", Response.Status.BAD_REQUEST),
    VALIDATION_ERROR_HORA("V-02", "La hora de inicio debe ser menor que la hora de fin", Response.Status.BAD_REQUEST),
    VALIDATION_ERROR_CANCELAR("V-03", "Solo se pueden cancelar reservas creadas", Response.Status.BAD_REQUEST),
    GENERIC_ERROR("G-02", "Error interno del sistema. Reintentar más tarde", Response.Status.INTERNAL_SERVER_ERROR),
    BUSINESS_NOT_FOUND_ERROR("B-03", "Valor no encontrado", Response.Status.NOT_FOUND),
    BUSINESS_NOT_ACTIVATED_PROFESIONAL("B-04", "El profesional no está activo", Response.Status.CONFLICT),
    BUSINESS_NOT_ACTIVATED_CLIENTE("B-04", "El cliente no está activo", Response.Status.CONFLICT),
    BUSINESS_NOT_HORARIO_DISPONIBLE("B-05", "No existe un horario disponible que cubra el intervalo solicitado", Response.Status.CONFLICT),
    BUSINESS_CON_RESERVA("B-06", "El profesional ya tiene una reserva en ese intervalo", Response.Status.CONFLICT),
    BUSINESS_DUPLICATE_ERROR("B-07", "El email ya se encuentra registrado", Response.Status.CONFLICT),
    BUSINESS_HORARIO_SOLAPADO("B-07", "El profesional ya tiene un horario que se solapa con el horario indicado", Response.Status.CONFLICT),

    UNAUTHORIZED_ERROR("S-04", "No Autorizado", Response.Status.UNAUTHORIZED),
    FORBIDDEN_ERROR("S-05", "Acceso no Permitido", Response.Status.FORBIDDEN);

    private final String code;
    private final String description;
    private final Response.Status status;
}
