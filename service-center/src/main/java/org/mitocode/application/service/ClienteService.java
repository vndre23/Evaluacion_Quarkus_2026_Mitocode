package org.mitocode.application.service;

import io.smallrye.mutiny.Uni;
import jakarta.enterprise.context.ApplicationScoped;
import org.eclipse.microprofile.faulttolerance.CircuitBreaker;
import org.eclipse.microprofile.faulttolerance.Fallback;
import org.eclipse.microprofile.faulttolerance.Retry;
import org.eclipse.microprofile.faulttolerance.Timeout;
import org.hibernate.exception.ConstraintViolationException;
import lombok.extern.slf4j.Slf4j;
import org.mitocode.application.command.CreateUpdateClientCommand;
import org.mitocode.application.exceptions.BusinessException;
import org.mitocode.application.port.in.CrudClienteUseCase;
import org.mitocode.application.port.out.cliente.*;
import org.mitocode.application.response.ApiResponse;
import org.mitocode.application.response.PageResponseDto;
import org.mitocode.domain.enums.ErrorType;
import org.mitocode.domain.enums.ReservaEstado;
import org.mitocode.domain.model.Cliente;

import java.util.UUID;

@ApplicationScoped
@Slf4j
public class ClienteService implements CrudClienteUseCase {

    private final SaveClientePort saveClientePort;

    private final ListAllClientePort listAllClientePort;

    private final FindByIdClientePort findByIdClientePort;

    private final UpdateClienteByIdPort updateClienteByIdPort;

    private final DeleteByIdClientePort deleteByIdClientePort;

    public ClienteService(SaveClientePort saveClientePort, ListAllClientePort listAllClientePort, FindByIdClientePort findByIdClientePort, UpdateClienteByIdPort updateClienteByIdPort, DeleteByIdClientePort deleteByIdClientePort) {
        this.saveClientePort = saveClientePort;
        this.listAllClientePort = listAllClientePort;
        this.findByIdClientePort = findByIdClientePort;
        this.updateClienteByIdPort = updateClienteByIdPort;
        this.deleteByIdClientePort = deleteByIdClientePort;
    }

    @Override
    public Uni<ApiResponse<Cliente>> create(CreateUpdateClientCommand command) {
        Cliente cliente = toDomain(command);
        return saveClientePort.save(cliente)
                .onFailure(ConstraintViolationException.class)
                .transform(error -> new BusinessException(
                        ErrorType.BUSINESS_DUPLICATE_ERROR,
                        ErrorType.BUSINESS_DUPLICATE_ERROR.getDescription()
                ))
                .map(c -> {
                    ApiResponse<Cliente> response = new ApiResponse<>();
                    response.setData(new Cliente(c.getId()));
                    return response;
                });
    }

    @Override
    public Uni<ApiResponse<Cliente>> update(CreateUpdateClientCommand command, String id) {
        Cliente cliente = toDomain(command);

        return this.findByIdClientePort.findById(id)
                .onItem().ifNull().failWith(
                        () -> new BusinessException(
                ErrorType.BUSINESS_NOT_FOUND_ERROR,
                ErrorType.BUSINESS_NOT_FOUND_ERROR.getDescription())
                ).flatMap(c -> {
                    cliente.setId(c.getId());
                    return this.updateClienteByIdPort.update(cliente);
                }).map(c -> {
                    ApiResponse<Cliente> response = new ApiResponse<>();
                    response.setData(c);
                    return response;
                });
    }

    @Override
    public Uni<PageResponseDto<Cliente>> listAll(int page, int limit) {
        return listAllClientePort.listAll(page, limit);
    }

    @Override
    @Fallback(fallbackMethod = "recoverFindById")
    @CircuitBreaker(
            requestVolumeThreshold = 4,
            failureRatio = 0.5,
            delay = 10000
    )
    @Retry(
            maxRetries = 2,
            delay = 500
    )
    @Timeout(2000)
    public Uni<ApiResponse<Cliente>> findById(String id) {
        return this.findByIdClientePort.findById(id)
                .onItem().ifNull().failWith(() ->
                        new BusinessException(
                                ErrorType.BUSINESS_NOT_FOUND_ERROR,
                                ErrorType.BUSINESS_NOT_FOUND_ERROR.getDescription()))
                .map(c -> {
                    ApiResponse<Cliente> response = new ApiResponse<>();
                    response.setData(c);
                    return response;
                });

    }

    private Uni<ApiResponse<Cliente>> recoverFindById(String id) {
        ApiResponse<Cliente> response = new ApiResponse<>();
        response.setData(Cliente.builder()
                        .id(UUID.randomUUID())
                        .nombres("Cliente Fallback")
                        .apellidos("Cliente Fallback")
                        .email("email@fallback.com")
                        .estadoActivo(false)
                .build());

        return Uni.createFrom().item(response);
    }

    @Override
    public Uni<Void> deleteById(String id) {
        return this.findByIdClientePort.findById(id)
                .onItem().ifNull().failWith(() ->
                        new BusinessException(
                                ErrorType.BUSINESS_NOT_FOUND_ERROR,
                                ErrorType.BUSINESS_NOT_FOUND_ERROR.getDescription()))
                .flatMap(cliente -> deleteByIdClientePort.deleteById(cliente.getId().toString()));

    }

    private Cliente toDomain(CreateUpdateClientCommand command) {
        return new Cliente(command.nombres(), command.apellidos(), command.email(), command.telefono());
    }

}
