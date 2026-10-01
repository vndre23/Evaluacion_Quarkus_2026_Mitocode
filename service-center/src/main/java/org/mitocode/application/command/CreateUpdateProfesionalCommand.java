package org.mitocode.application.command;

public record CreateUpdateProfesionalCommand(
        String nombres,
        String apellidos,
        String especialidad
) {
}
