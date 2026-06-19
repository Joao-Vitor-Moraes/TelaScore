package com.requisitos.avaliacaofilmes.TelaScore.aplicacao.identidade;

import com.requisitos.avaliacaofilmes.TelaScore.dominio.identidade.usuario.UsuarioId;
import com.requisitos.avaliacaofilmes.TelaScore.dominio.identidade.usuario.UsuarioLogado;
import com.requisitos.avaliacaofilmes.TelaScore.dominio.identidade.usuario.UsuarioServico;

public class RemoverUsuarioCasoDeUso {

    private final UsuarioServico usuarioServico;
    private final SessaoUsuario sessaoUsuario;

    public RemoverUsuarioCasoDeUso(UsuarioServico usuarioServico, SessaoUsuario sessaoUsuario) {
        this.usuarioServico = usuarioServico;
        this.sessaoUsuario = sessaoUsuario;
    }

    public void executar(RemoverUsuarioComando comando) {
        UsuarioLogado usuarioLogado = sessaoUsuario.obterUsuarioLogado();

        if (usuarioLogado == null) {
            throw new IllegalStateException("Usuário não está logado");
        }

        if (!usuarioLogado.isAdmin()) {
            throw new IllegalStateException("Apenas administradores podem remover usuários");
        }

        UsuarioId usuarioId = new UsuarioId(comando.usuarioId());
        usuarioServico.remover(usuarioId);
    }
}
