package com.requisitos.avaliacaofilmes.TelaScore.aplicacao.identidade;

import com.requisitos.avaliacaofilmes.TelaScore.dominio.identidade.usuario.Usuario;
import com.requisitos.avaliacaofilmes.TelaScore.dominio.identidade.usuario.UsuarioId;
import com.requisitos.avaliacaofilmes.TelaScore.dominio.identidade.usuario.UsuarioLogado;
import com.requisitos.avaliacaofilmes.TelaScore.dominio.identidade.usuario.UsuarioServico;

public class ObterMeuUsuarioCasoDeUso {

    private final UsuarioServico usuarioServico;
    private final SessaoUsuario sessaoUsuario;

    public ObterMeuUsuarioCasoDeUso(UsuarioServico usuarioServico, SessaoUsuario sessaoUsuario) {
        this.usuarioServico = usuarioServico;
        this.sessaoUsuario = sessaoUsuario;
    }

    public Usuario executar() {
        UsuarioLogado usuarioLogado = sessaoUsuario.obterUsuarioLogado();

        if (usuarioLogado == null) {
            throw new IllegalStateException("Usuario nao esta logado");
        }

        Usuario usuario = usuarioServico.obter(new UsuarioId(usuarioLogado.getId().getId()));

        if (usuario == null) {
            throw new IllegalArgumentException("Usuario nao encontrado");
        }

        return usuario;
    }
}
