package com.requisitos.avaliacaofilmes.TelaScore.aplicacao.identidade;

import com.requisitos.avaliacaofilmes.TelaScore.dominio.identidade.usuario.PapelUsuario;
import com.requisitos.avaliacaofilmes.TelaScore.dominio.identidade.usuario.Usuario;
import com.requisitos.avaliacaofilmes.TelaScore.dominio.identidade.usuario.UsuarioId;
import com.requisitos.avaliacaofilmes.TelaScore.dominio.identidade.usuario.UsuarioServico;

public class RemoverUsuarioCasoDeUso {

    private final UsuarioServico usuarioServico;

    public RemoverUsuarioCasoDeUso(UsuarioServico usuarioServico) {
        this.usuarioServico = usuarioServico;
    }

    public void executar(RemoverUsuarioComando comando) {
        Usuario administrador = usuarioServico.obter(new UsuarioId(comando.administradorId()));

        if (administrador == null) {
            throw new IllegalArgumentException("O usuário administrador não existe");
        }

        if (administrador.getPapel() != PapelUsuario.ADMIN) {
            throw new IllegalStateException("Apenas administradores podem remover usuários");
        }

        UsuarioId usuarioId = new UsuarioId(comando.usuarioId());
        usuarioServico.remover(usuarioId);
    }
}
