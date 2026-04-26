package com.requisitos.avaliacaofilmes.TelaScore.aplicacao.identidade;

import static org.apache.commons.lang3.Validate.notNull;

import com.requisitos.avaliacaofilmes.TelaScore.dominio.identidade.usuario.UsuarioLogado;

public class SessaoUsuario {

    private UsuarioLogado usuarioLogado;

    public void iniciar(UsuarioLogado usuarioLogado) {
        notNull(usuarioLogado, "O usuário logado não pode ser nulo");
        this.usuarioLogado = usuarioLogado;
    }

    public UsuarioLogado obterUsuarioLogado() {
        return usuarioLogado;
    }

    public boolean estaLogado() {
        return usuarioLogado != null;
    }

    public void encerrar() {
        usuarioLogado = null;
    }
}
