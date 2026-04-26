package com.requisitos.avaliacaofilmes.TelaScore.aplicacao.identidade;

import java.util.List;

import com.requisitos.avaliacaofilmes.TelaScore.dominio.identidade.usuario.PapelUsuario;
import com.requisitos.avaliacaofilmes.TelaScore.dominio.identidade.usuario.Usuario;
import com.requisitos.avaliacaofilmes.TelaScore.dominio.identidade.usuario.UsuarioId;
import com.requisitos.avaliacaofilmes.TelaScore.dominio.identidade.usuario.UsuarioServico;

public class ListarUsuariosCasoDeUso {

    private final UsuarioServico usuarioServico;

    public ListarUsuariosCasoDeUso(UsuarioServico usuarioServico) {
        this.usuarioServico = usuarioServico;
    }

    public List<Usuario> executar(ListarUsuariosComando comando) {
        Usuario administrador = usuarioServico.obter(new UsuarioId(comando.administradorId()));

        if (administrador == null) {
            throw new IllegalArgumentException("O usuário administrador não existe");
        }

        if (administrador.getPapel() != PapelUsuario.ADMIN) {
            throw new IllegalStateException("Apenas administradores podem listar usuários");
        }

        return usuarioServico.listarTodos();
    }
}
