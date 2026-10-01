package org.mitocode.infraestructure.adapters.output.persistence.entities;

import com.fasterxml.jackson.annotation.JsonInclude;
import jakarta.persistence.*;
import lombok.*;
import org.mitocode.domain.model.Reserva;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

@Entity
@Table(name = "profesionales")
@Getter
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class ProfesionalEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;
    private String nombres;
    private String apellidos;
    private String especialidad;

    @Column(name = "estado_activo")
    private boolean estadoActivo;

    @Builder.Default
    @OneToMany(mappedBy = "profesional")
    private List<ReservaEntity> reservas = new ArrayList<>();

}
