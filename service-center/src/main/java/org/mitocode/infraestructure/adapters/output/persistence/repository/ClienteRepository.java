package org.mitocode.infraestructure.adapters.output.persistence.repository;

import io.quarkus.hibernate.reactive.panache.PanacheRepositoryBase;
import io.quarkus.hibernate.reactive.panache.common.WithSession;
import io.quarkus.hibernate.reactive.panache.common.WithTransaction;
import io.quarkus.panache.common.Page;
import io.quarkus.panache.common.Sort;
import io.smallrye.mutiny.Uni;
import jakarta.enterprise.context.ApplicationScoped;
import org.mitocode.application.port.out.cliente.*;
import org.mitocode.application.response.PageResponseDto;
import org.mitocode.domain.model.Cliente;
import org.mitocode.infraestructure.adapters.output.persistence.entities.ClienteEntity;

import java.util.List;
import java.util.UUID;

@ApplicationScoped
public class ClienteRepository implements PanacheRepositoryBase<ClienteEntity, UUID>,
        SaveClientePort, ListAllClientePort, FindByIdClientePort, UpdateClienteByIdPort, DeleteByIdClientePort {

    @Override
    @WithTransaction
    public Uni<Cliente> save(Cliente cliente) {
        return this.persist(toEntity(cliente))
                .map(this::toDomain);
    }

    @Override
    @WithSession
    public Uni<PageResponseDto<Cliente>> listAll(int page, int limit) {

        var query = this.findAll(Sort.by("apellidos").ascending()).page(Page.of(page - 1, limit));

        return Uni.combine().all().unis(
                query.list(),
                query.count(),
                query.pageCount()
        ).asTuple()
                .map(tuple ->
                        new PageResponseDto<>(toDomain(tuple.getItem1()), page, limit, tuple.getItem2(), tuple.getItem3()));
    }


    @Override
    @WithSession
    public Uni<Cliente> findById(String id) {
        return this.findById(UUID.fromString(id)).map(this::toDomain);

    }

    @Override
    @WithTransaction
    public Uni<Cliente> update(Cliente cliente) {
        return this.update(
                        "nombres = ?1, apellidos = ?2, email = ?3, telefono = ?4, estadoActivo = ?5 where id = ?6",
                        cliente.getNombres(),
                        cliente.getApellidos(),
                        cliente.getEmail(),
                        cliente.getTelefono(),
                        cliente.isEstadoActivo(),
                        cliente.getId()
                )
                .replaceWith(cliente);

    }

    private ClienteEntity toEntity(Cliente cliente) {
        return new ClienteEntity(cliente.getId(), cliente.getNombres(), cliente.getApellidos(), cliente.getEmail(), cliente.getTelefono(), cliente.isEstadoActivo());
    }

    private Cliente toDomain(ClienteEntity clienteEntity) {
        if (clienteEntity == null) {
            return null;
        }
        return new Cliente(clienteEntity.getId(), clienteEntity.getNombres(), clienteEntity.getApellidos(), clienteEntity.getEmail(), clienteEntity.getTelefono(), clienteEntity.isEstadoActivo());
    }

    private List<Cliente> toDomain(List<ClienteEntity> entities) {
        return entities.stream()
                .map(this::toDomain)
                .toList();
    }

    @Override
    @WithTransaction
    public Uni<Void> deleteById(String id) {
        return this.update(
                "estadoActivo = false where id = ?1",
                UUID.fromString(id)
        ).replaceWithVoid();
    }
}
