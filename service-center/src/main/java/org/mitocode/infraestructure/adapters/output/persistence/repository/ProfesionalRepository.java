package org.mitocode.infraestructure.adapters.output.persistence.repository;

import io.quarkus.hibernate.reactive.panache.PanacheRepositoryBase;
import io.quarkus.hibernate.reactive.panache.common.WithSession;
import io.quarkus.hibernate.reactive.panache.common.WithTransaction;
import io.quarkus.panache.common.Page;
import io.quarkus.panache.common.Sort;
import io.smallrye.mutiny.Uni;
import jakarta.enterprise.context.ApplicationScoped;
import org.mitocode.application.port.out.cliente.FindByIdClientePort;
import org.mitocode.application.port.out.cliente.ListAllClientePort;
import org.mitocode.application.port.out.cliente.SaveClientePort;
import org.mitocode.application.port.out.cliente.UpdateClienteByIdPort;
import org.mitocode.application.port.out.profesional.*;
import org.mitocode.application.response.PageResponseDto;
import org.mitocode.domain.model.Cliente;
import org.mitocode.domain.model.Profesional;
import org.mitocode.infraestructure.adapters.output.persistence.entities.ClienteEntity;
import org.mitocode.infraestructure.adapters.output.persistence.entities.ProfesionalEntity;

import java.util.List;
import java.util.UUID;

@ApplicationScoped
public class ProfesionalRepository implements PanacheRepositoryBase<ProfesionalEntity, UUID>,
        SaveProfesionalPort, ListAllProfesionalPort, FindByIdProfesionalPort, UpdateProfesionalByIdPort,
        ListAllProfesionalByFechaPort {

    @Override
    @WithTransaction
    public Uni<Profesional> save(Profesional profesional) {
        return this.persist(toEntity(profesional))
                .map(this::toDomain);
    }

    @Override
    @WithSession
    public Uni<PageResponseDto<Profesional>> listAll(int page, int limit) {

        var query = this.findAll(Sort.by("especialidad").ascending()).page(Page.of(page - 1, limit));

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
    public Uni<Profesional> findById(String id) {
        return this.findById(UUID.fromString(id)).map(this::toDomain);

    }

    @Override
    @WithTransaction
    public Uni<Profesional> update(Profesional profesional) {
        return this.update(
                        "nombres = ?1, apellidos = ?2, especialidad = ?3 where id = ?4",
                        profesional.getNombres(),
                        profesional.getApellidos(),
                        profesional.getEspecialidad(),
                        profesional.getId()
                )
                .replaceWith(profesional);

    }

    @Override
    @WithSession
    public Uni<PageResponseDto<Profesional>> listAllByFecha(int page, int limit) {
        var query = this.findAll(Sort.by("especialidad").ascending()).page(Page.of(page - 1, limit));

        return Uni.combine().all().unis(
                        query.list(),
                        query.count(),
                        query.pageCount()
                ).asTuple()
                .map(tuple ->
                        new PageResponseDto<>(toDomain(tuple.getItem1()), page, limit, tuple.getItem2(), tuple.getItem3()));
    }

    private ProfesionalEntity toEntity(Profesional profesional) {
        return new ProfesionalEntity(profesional.getId(), profesional.getNombres(), profesional.getApellidos(), profesional.getEspecialidad(), profesional.isEstadoActivo(), null);
    }

    private Profesional toDomain(ProfesionalEntity entity) {
        if(entity == null) {
            return null;
        }
        return new Profesional(entity.getId(), entity.getNombres(), entity.getApellidos(), entity.getEspecialidad(), entity.isEstadoActivo());
    }

    private List<Profesional> toDomain(List<ProfesionalEntity> entities) {
        return entities.stream()
                .map(this::toDomain)
                .toList();
    }
}
