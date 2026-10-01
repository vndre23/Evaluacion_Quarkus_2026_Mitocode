CREATE TABLE reservas (
                                    id UUID PRIMARY KEY,
                                    profesional_id UUID NOT NULL REFERENCES profesionales(id),
                                    cliente_id UUID NOT NULL REFERENCES clientes(id),
                                    fecha DATE NOT NULL,
                                    hora_inicio TIME NOT NULL,
                                    hora_fin TIME NOT NULL,
                                    estado VARCHAR NOT NULL
);

