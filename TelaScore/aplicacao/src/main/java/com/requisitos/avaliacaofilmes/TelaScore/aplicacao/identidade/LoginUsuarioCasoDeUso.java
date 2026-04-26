package com.requisitos.avaliacaofilmes.TelaScore.aplicacao.identidade;

import com.requisitos.avaliacaofilmes.TelaScore.dominio.identidade.usuario.Email;
import com.requisitos.avaliacaofilmes.TelaScore.dominio.identidade.usuario.Senha;
import com.requisitos.avaliacaofilmes.TelaScore.dominio.identidade.usuario.Usuario;
import com.requisitos.avaliacaofilmes.TelaScore.dominio.identidade.usuario.UsuarioLogado;
import com.requisitos.avaliacaofilmes.TelaScore.dominio.identidade.usuario.UsuarioServico;

public class LoginUsuarioCasoDeUso {

    private final UsuarioServico usuarioServico;
    private final SessaoUsuario sessaoUsuario;

    public LoginUsuarioCasoDeUso(UsuarioServico usuarioServico, SessaoUsuario sessaoUsuario) {
        this.usuarioServico = usuarioServico;
        this.sessaoUsuario = sessaoUsuario;
    }

    public UsuarioLogado executar(LoginUsuarioComando comando) {
        Email email = new Email(comando.email());
        Senha senha = new Senha(comando.senha());

        Usuario usuario = usuarioServico.obterPorEmail(email);

        if (usuario == null) {
            throw new IllegalArgumentException("Erro ao fazer login");
        }

        if (!usuario.getSenha().getValor().equals(senha.getValor())) {
            throw new IllegalArgumentException("Erro ao fazer login");
        }

        UsuarioLogado usuarioLogado = new UsuarioLogado(usuario.getId(), usuario.getPapel());
        sessaoUsuario.iniciar(usuarioLogado);

        return usuarioLogado;
    }
}
