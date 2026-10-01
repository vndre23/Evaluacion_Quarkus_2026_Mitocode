package org.mitocode.application.exceptions;

import jakarta.ws.rs.core.Response;
import jakarta.ws.rs.ext.ExceptionMapper;
import jakarta.ws.rs.ext.Provider;

@Provider
public class BusinessExceptionMapper
        implements ExceptionMapper<BusinessException> {

    @Override
    public Response toResponse(BusinessException exception) {

        return Response
                .status(exception.getType().getStatus())
                .entity(ApiErrorResponse.builder()
                        .errorId(exception.getId().toString())
                        .typeError(exception.getType().toString())
                        .message(exception.getDescription())
                        .status(null)
                        .build())
                .build();
    }
}
