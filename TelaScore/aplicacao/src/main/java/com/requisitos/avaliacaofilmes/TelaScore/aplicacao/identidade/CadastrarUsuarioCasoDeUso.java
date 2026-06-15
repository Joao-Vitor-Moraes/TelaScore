package com.requisitos.avaliacaofilmes.TelaScore.aplicacao.identidade;

import com.requisitos.avaliacaofilmes.TelaScore.dominio.identidade.usuario.Apelido;
import com.requisitos.avaliacaofilmes.TelaScore.dominio.identidade.usuario.Email;
import com.requisitos.avaliacaofilmes.TelaScore.dominio.identidade.usuario.PapelUsuario;
import com.requisitos.avaliacaofilmes.TelaScore.dominio.identidade.usuario.Senha;
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

        UsuarioId usuarioId = new UsuarioId(geradorId.gerarProximoIdUsuario());

        Usuario usuario = new Usuario(
            usuarioId,
            comando.nome(),
            email,
            new Senha(comando.senha()),
            PapelUsuario.CINEFILO,
            new Apelido(apelidoOuNome(comando.apelido(), comando.nome())),
            comando.biografia(),
            comando.avatarUrl()
        );

        usuarioServico.salvar(usuario);
    }

    private String apelidoOuNome(String apelido, String nome) {
        if (apelido != null && !apelido.isBlank()) {
            return apelido;
        }

        String apelidoPadrao = nome == null ? "usuario" : nome.trim();
        if (apelidoPadrao.isBlank()) {
            apelidoPadrao = "usuario";
        }
        if (apelidoPadrao.length() < 3) {
            apelidoPadrao = (apelidoPadrao + "usr").substring(0, 3);
        }
        return apelidoPadrao.length() > 20 ? apelidoPadrao.substring(0, 20) : apelidoPadrao;
    }

}
