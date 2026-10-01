package org.mitocode.infraestructure.adapters.output.persistence.repository;

import io.quarkus.hibernate.reactive.panache.PanacheRepositoryBase;
import io.quarkus.hibernate.reactive.panache.common.WithSession;
import io.quarkus.hibernate.reactive.panache.common.WithTransaction;
import io.smallrye.mutiny.Multi;
import io.smallrye.mutiny.Uni;
import jakarta.enterprise.context.ApplicationScoped;
import org.hibernate.reactive.mutiny.Mutiny;
import org.mitocode.application.port.out.reserva.*;
import org.mitocode.domain.enums.ReservaEstado;
import org.mitocode.domain.model.Cliente;
import org.mitocode.domain.model.Profesional;
import org.mitocode.domain.model.Reserva;
import org.mitocode.infraestructure.adapters.output.persistence.entities.ClienteEntity;
import org.mitocode.infraestructure.adapters.output.persistence.entities.ProfesionalEntity;
import org.mitocode.infraestructure.adapters.output.persistence.entities.ReservaEntity;

import java.time.LocalDate;
import java.time.LocalTime;
import java.util.List;
import java.util.UUID;

@ApplicationScoped
public class ReservaRepository implements PanacheRepositoryBase<ReservaEntity, UUID>,
        ExistsReservaSolapadaPort, SaveReservaPort, CancelReservaPort, ReservaFindById, ListAllByProfesionalPort {

    @Override
    @WithSession
    public Uni<Boolean> existsReservaSolapada(UUID profesionalId, LocalDate fecha, LocalTime horaInicio, LocalTime horaFin) {
        return this.count("profesional.id = ?1 and fecha = ?2 and estado = ?3 and horaInicio <?4 and horaFin >?5",
                profesionalId, fecha, ReservaEstado.CREADA, horaFin, horaInicio)
                .map(count -> count > 0);
    }

    @Override
    @WithTransaction
    public Uni<Reserva> save(Reserva reserva) {
        ReservaEntity entity = ReservaEntity.builder()
                .fecha(reserva.getFecha())
                .horaInicio(reserva.getHoraInicio())
                .horaFin(reserva.getHoraFin())
                .cliente(toEntity(reserva.getCliente()))
                .profesional(toEntity(reserva.getProfesional()))
                .estado(reserva.getEstado())
                .build();

        return this.persist(entity)
                .replaceWith(entity)
                .map(this::toDomain);
    }

    @Override
    @WithTransaction
    public Uni<Boolean> cancelar(UUID reservaId) {
        return this.update(
                "estado = ?1 where id = ?2",
                ReservaEstado.CANCELADA,
                reservaId
        ).map(count -> count > 0);
    }

    @Override
    @WithSession
    public Uni<Reserva> findById(String id) {
        return this.findById(UUID.fromString(id))
                .map(this::toDomainCancelar);
    }

    @Override
    @WithSession
    public Uni<List<Reserva>> listAllByProfesional(List<UUID> profesionalIds) {

        return this.find(
                        "select r " +
                                "from ReservaEntity r " +
                                "join fetch r.cliente " +
                                "join fetch r.profesional " +
                                "where r.profesional.id in (?1) " +
                                "and r.estado = ?2",
                        profesionalIds,
                        ReservaEstado.CREADA
                )
                .list()
                .map(list -> list.stream()
                        .map(this::toDomain)
                        .toList()
                );
    }

    private ClienteEntity toEntity(Cliente cliente) {
        return new ClienteEntity(cliente.getId(), cliente.getNombres(), cliente.getApellidos(), cliente.getEmail(), cliente.getTelefono(), cliente.isEstadoActivo());
    }

    private ProfesionalEntity toEntity(Profesional profesional) {
        return new ProfesionalEntity(profesional.getId(), profesional.getNombres(), profesional.getApellidos(), profesional.getEspecialidad(), profesional.isEstadoActivo(), null);
    }

    private Reserva toDomain(ReservaEntity entity) {
        if (entity == null) {
            return null;
        }
        return new Reserva(entity.getId(), entity.getFecha(), entity.getHoraInicio(), entity.getHoraFin(),
                toDomain(entity.getCliente()) ,toDomain(entity.getProfesional()), entity.getEstado());
    }

    private Reserva toDomainCancelar(ReservaEntity entity) {
        if (entity == null) {
            return null;
        }
        return new Reserva(entity.getId(), entity.getEstado());
    }

    private Cliente toDomain(ClienteEntity clienteEntity) {
        if (clienteEntity == null) {
            return null;
        }
        return new Cliente(clienteEntity.getId(), clienteEntity.getNombres(), clienteEntity.getApellidos(), clienteEntity.getEmail(), clienteEntity.getTelefono(), clienteEntity.isEstadoActivo());
    }

    private Profesional toDomain(ProfesionalEntity entity) {
        if (entity == null) {
            return null;
        }
        return new Profesional(entity.getId(), entity.getNombres(), entity.getApellidos(), entity.getEspecialidad(), entity.isEstadoActivo());
    }



}
