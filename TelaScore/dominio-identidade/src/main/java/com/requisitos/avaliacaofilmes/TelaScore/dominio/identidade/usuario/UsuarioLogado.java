package com.requisitos.avaliacaofilmes.TelaScore.dominio.identidade.usuario;

import static org.apache.commons.lang3.Validate.notNull;

public class UsuarioLogado {

    private final UsuarioId id;
    private final PapelUsuario papel;

    public UsuarioLogado(UsuarioId id, PapelUsuario papel) {
        notNull(id, "O id do usuário logado não pode ser nulo");
        notNull(papel, "O papel do usuário logado não pode ser nulo");

        this.id = id;
        this.papel = papel;
    }

    public UsuarioId getId() {
        return id;
    }

    public PapelUsuario getPapel() {
        return papel;
    }

    public boolean isAdmin() {
        return papel == PapelUsuario.ADMIN;
    }

    public boolean isMesmoUsuario(UsuarioId usuarioId) {
        notNull(usuarioId, "O id do usuário não pode ser nulo");
        return id.equals(usuarioId);
    }
}
