package org.mitocode.application.exceptions;

import jakarta.ws.rs.core.Response;
import jakarta.ws.rs.ext.ExceptionMapper;
import jakarta.ws.rs.ext.Provider;
import lombok.extern.slf4j.Slf4j;
import org.mitocode.domain.enums.ErrorType;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

@Slf4j
@Provider
public class GlobalExceptionMapper implements ExceptionMapper<Throwable> {

    @Override
    public Response toResponse(Throwable exception) {
        log.error("Error no controlado: {}", exception.getMessage());
        List<ErrorDetailDto> list = List.of(new ErrorDetailDto(exception.getMessage()));
        return Response
                .status(Response.Status.INTERNAL_SERVER_ERROR)
                .entity(
                        ApiErrorResponse
                                .builder()
                                .errorId(UUID.randomUUID().toString())
                                .typeError(ErrorType.GENERIC_ERROR.name())
                                .message(ErrorType.GENERIC_ERROR.getDescription())
                                .status(ErrorType.GENERIC_ERROR.getStatus().getStatusCode())
                                .timestamp(LocalDateTime.now())
                                .errorDetails(list)
                                .build()
                ).build();
    }
}