package org.mitocode.application.service;

import io.smallrye.mutiny.Uni;
import jakarta.enterprise.context.ApplicationScoped;
import org.hibernate.exception.ConstraintViolationException;
import lombok.extern.slf4j.Slf4j;
import org.mitocode.application.command.CreateUpdateClientCommand;
import org.mitocode.application.exceptions.BusinessException;
import org.mitocode.application.port.in.CrudClienteUseCase;
import org.mitocode.application.port.out.cliente.FindByIdClientePort;
import org.mitocode.application.port.out.cliente.ListAllClientePort;
import org.mitocode.application.port.out.cliente.SaveClientePort;
import org.mitocode.application.port.out.cliente.UpdateClienteByIdPort;
import org.mitocode.application.response.ApiResponse;
import org.mitocode.application.response.PageResponseDto;
import org.mitocode.domain.enums.ErrorType;
import org.mitocode.domain.model.Cliente;

@ApplicationScoped
@Slf4j
public class ClienteService implements CrudClienteUseCase {

    private final SaveClientePort saveClientePort;

    private final ListAllClientePort listAllClientePort;

    private final FindByIdClientePort findByIdClientePort;

    private final UpdateClienteByIdPort updateClienteByIdPort;

    public ClienteService(SaveClientePort saveClientePort, ListAllClientePort listAllClientePort, FindByIdClientePort findByIdClientePort, UpdateClienteByIdPort updateClienteByIdPort) {
        this.saveClientePort = saveClientePort;
        this.listAllClientePort = listAllClientePort;
        this.findByIdClientePort = findByIdClientePort;
        this.updateClienteByIdPort = updateClienteByIdPort;
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

    @Override
    public Uni<Void> deleteById(String id) {
        return null;
    }

    private Cliente toDomain(CreateUpdateClientCommand command) {
        return new Cliente(command.nombres(), command.apellidos(), command.email(), command.telefono());
    }

}
