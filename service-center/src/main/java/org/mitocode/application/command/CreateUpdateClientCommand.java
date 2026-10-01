package org.mitocode.application.command;

public record CreateUpdateClientCommand(
        String nombres,
        String apellidos,
        String email,
        String telefono
) {
}
