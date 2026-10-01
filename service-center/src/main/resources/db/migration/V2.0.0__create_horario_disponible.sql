CREATE TABLE horario_disponible (
                                    id UUID PRIMARY KEY,
                                    profesional_id UUID NOT NULL REFERENCES profesionales(id),
                                    fecha DATE NOT NULL,
                                    hora_inicio TIME NOT NULL,
                                    hora_fin TIME NOT NULL
);

