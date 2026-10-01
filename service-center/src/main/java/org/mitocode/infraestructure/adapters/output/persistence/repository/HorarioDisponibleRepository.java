package org.mitocode.infraestructure.adapters.output.persistence.repository;

import io.quarkus.hibernate.reactive.panache.PanacheRepositoryBase;
import io.quarkus.hibernate.reactive.panache.common.WithSession;
import io.quarkus.hibernate.reactive.panache.common.WithTransaction;
import io.smallrye.mutiny.Uni;
import jakarta.enterprise.context.ApplicationScoped;
import org.mitocode.application.port.out.horariodisponible.ExistsHorarioSolapadoPort;
import org.mitocode.application.port.out.horariodisponible.SaveHorarioDisponiblePort;
import org.mitocode.application.port.out.horariodisponible.ExistsHorarioDisponibleReservaPort;
import org.mitocode.domain.model.HorarioDisponible;
import org.mitocode.domain.model.Profesional;
import org.mitocode.infraestructure.adapters.output.persistence.entities.HorarioDisponibleEntity;
import org.mitocode.infraestructure.adapters.output.persistence.entities.ProfesionalEntity;

import java.time.LocalDate;
import java.time.LocalTime;
import java.util.UUID;

@ApplicationScoped
public class HorarioDisponibleRepository implements PanacheRepositoryBase<HorarioDisponibleEntity, UUID> ,
        ExistsHorarioSolapadoPort, SaveHorarioDisponiblePort, ExistsHorarioDisponibleReservaPort {

    @Override
    @WithSession
    public Uni<Boolean> existsSolapamiento(UUID profesionalId, LocalDate fecha, LocalTime horaInicio, LocalTime horaFin) {
        return this.count(
                "profesional.id = ?1 " +
                        "and fecha = ?2 " +
                        "and horaInicio < ?3 " +
                        "and horaFin > ?4",
                profesionalId,
                fecha,
                horaFin,
                horaInicio
        ).map(count -> count > 0);
    }

    @Override
    @WithTransaction
    public Uni<HorarioDisponible> save(HorarioDisponible horario) {
        HorarioDisponibleEntity entity = HorarioDisponibleEntity.builder()
                .profesional(toEntity(horario.getProfesional()))
                .fecha(horario.getFecha())
                .horaInicio(horario.getHoraInicio())
                .horaFin(horario.getHoraFin())
                .build();

        return this.persist(entity)
                .replaceWith(entity)
                .map(this::toDomain);
    }

    @Override
    @WithSession
    public Uni<Boolean> existsHorarioDisponible(UUID profesionalId, LocalDate fecha, LocalTime horaInicio, LocalTime horaFin) {
        return this.count("profesional.id = ?1 and fecha = ?2 and horaInicio <= ?3 and horaFin >=?4",
                profesionalId, fecha, horaInicio, horaFin
        ).map(count -> count > 0);
    }


    private ProfesionalEntity toEntity(Profesional profesional) {
        return new ProfesionalEntity(profesional.getId(), profesional.getNombres(), profesional.getApellidos(), profesional.getEspecialidad(), profesional.isEstadoActivo(), null);
    }

    private HorarioDisponible toDomain(HorarioDisponibleEntity entity) {
        if (entity == null) {
            return null;
        }
        return new HorarioDisponible(entity.getId(), toDomain(entity.getProfesional()), entity.getFecha(), entity.getHoraInicio(), entity.getHoraFin());
    }

    private Profesional toDomain(ProfesionalEntity entity) {
        if (entity == null) {
            return null;
        }
        return new Profesional(entity.getId(), entity.getNombres(), entity.getApellidos(), entity.getEspecialidad(), entity.isEstadoActivo());
    }
}
