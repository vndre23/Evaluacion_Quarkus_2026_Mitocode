package org.mitocode.infraestructure.adapters.output.persistence.entities;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.util.UUID;

@Entity
@Table(name = "clientes")
@Getter

@Builder
@AllArgsConstructor
@NoArgsConstructor
public class ClienteEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    public UUID id;

    private String nombres;
    private String apellidos;
    private String email;
    private String telefono;
    @Column(name = "estado_activo")
    private boolean estadoActivo;

}
