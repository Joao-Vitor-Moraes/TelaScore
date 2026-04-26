package com.requisitos.avaliacaofilmes.TelaScore.aplicacao.identidade;

import com.requisitos.avaliacaofilmes.TelaScore.dominio.identidade.usuario.Email;
import com.requisitos.avaliacaofilmes.TelaScore.dominio.identidade.usuario.PapelUsuario;
import com.requisitos.avaliacaofilmes.TelaScore.dominio.identidade.usuario.Usuario;
import com.requisitos.avaliacaofilmes.TelaScore.dominio.identidade.usuario.UsuarioId;
import com.requisitos.avaliacaofilmes.TelaScore.dominio.identidade.usuario.UsuarioServico;

public class CadastrarUsuarioCasoDeUso {

    private final UsuarioServico usuarioServico;
    private final GeradorId geradorId;

    public CadastrarUsuarioCasoDeUso(UsuarioServico usuarioServico, GeradorId geradorId) {
        this.usuarioServico = usuarioServico;
        this.geradorId = geradorId;
    }

    public void executar(CadastrarUsuarioComando comando) {
        Email email = new Email(comando.email());

        if (usuarioServico.obterPorEmail(email) != null) {
            throw new IllegalArgumentException("Já existe um usuário cadastrado com este e-mail");
        }

        Usuario usuario = new Usuario(
            new UsuarioId(geradorId.gerarProximoIdUsuario()),
            comando.nome(),
            email,
            PapelUsuario.CINEFILO
        );

        usuarioServico.salvar(usuario);
    }
}