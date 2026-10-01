package org.mitocode.infraestructure.adapters.output.persistence.entities;

import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.mitocode.domain.enums.ReservaEstado;

import java.time.LocalDate;
import java.time.LocalTime;
import java.util.UUID;

@Entity
@Table(name = "reservas")
@Getter
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class ReservaEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "profesional_id", nullable = false)
    @JsonIgnore
    private ProfesionalEntity profesional;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "cliente_id", nullable = false)
    @JsonIgnore
    private ClienteEntity cliente;
    private LocalDate fecha;
    @Column(name = "hora_inicio")
    private LocalTime horaInicio;
    @Column(name = "hora_fin")
    private LocalTime horaFin;
    @Enumerated(EnumType.STRING)
    private ReservaEstado estado;
}
