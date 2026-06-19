package com.requisitos.avaliacaofilmes.TelaScore.aplicacao.identidade;

import com.requisitos.avaliacaofilmes.TelaScore.dominio.identidade.usuario.Apelido;
import com.requisitos.avaliacaofilmes.TelaScore.dominio.identidade.usuario.Email;
import com.requisitos.avaliacaofilmes.TelaScore.dominio.identidade.usuario.PapelUsuario;
import com.requisitos.avaliacaofilmes.TelaScore.dominio.identidade.usuario.Usuario;
import com.requisitos.avaliacaofilmes.TelaScore.dominio.identidade.usuario.UsuarioId;
import com.requisitos.avaliacaofilmes.TelaScore.dominio.identidade.usuario.UsuarioLogado;
import com.requisitos.avaliacaofilmes.TelaScore.dominio.identidade.usuario.UsuarioServico;

public class EditarUsuarioCasoDeUso {

    private final UsuarioServico usuarioServico;
    private final SessaoUsuario sessaoUsuario;

    public EditarUsuarioCasoDeUso(UsuarioServico usuarioServico, SessaoUsuario sessaoUsuario) {
        this.usuarioServico = usuarioServico;
        this.sessaoUsuario = sessaoUsuario;
    }

    public Usuario executar(EditarUsuarioComando comando) {
        UsuarioLogado usuarioLogado = sessaoUsuario.obterUsuarioLogado();

        if (usuarioLogado == null) {
            throw new IllegalStateException("Usuario nao esta logado");
        }

        if (!usuarioLogado.isAdmin()) {
            throw new IllegalStateException("Apenas administradores podem editar usuarios");
        }

        UsuarioId usuarioId = new UsuarioId(comando.usuarioId());
        Usuario usuarioAtual = usuarioServico.obter(usuarioId);

        if (usuarioAtual == null) {
            throw new IllegalArgumentException("O usuario informado nao existe");
        }

        Email email = emailAtualizado(comando.email(), usuarioAtual);
        validarEmailDisponivel(email, usuarioId);
        Apelido apelido = apelidoAtualizado(comando.apelido(), usuarioAtual);
        validarApelidoDisponivel(apelido, usuarioId);

        Usuario usuarioEditado = new Usuario(
                usuarioId,
                valorOuAtual(comando.nome(), usuarioAtual.getNome()),
                email,
                usuarioAtual.getSenha(),
                papelAtualizado(comando.papel(), usuarioAtual.getPapel()),
                apelido,
                comando.biografia(),
                comando.avatarUrl());

        usuarioServico.salvar(usuarioEditado);
        return usuarioEditado;
    }

    private Email emailAtualizado(String email, Usuario usuarioAtual) {
        if (email == null || email.isBlank()) {
            return usuarioAtual.getEmail();
        }
        return new Email(email);
    }

    private void validarEmailDisponivel(Email email, UsuarioId usuarioId) {
        Usuario usuarioComEmail = usuarioServico.obterPorEmail(email);
        if (usuarioComEmail != null && !usuarioComEmail.getId().equals(usuarioId)) {
            throw new IllegalArgumentException("Ja existe um usuario cadastrado com este e-mail");
        }
    }

    private PapelUsuario papelAtualizado(String papel, PapelUsuario papelAtual) {
        if (papel == null || papel.isBlank()) {
            return papelAtual;
        }
        return PapelUsuario.valueOf(papel);
    }

    private Apelido apelidoAtualizado(String apelido, Usuario usuarioAtual) {
        if (apelido == null || apelido.isBlank()) {
            return usuarioAtual.getApelido();
        }
        return new Apelido(apelido);
    }

    private void validarApelidoDisponivel(Apelido apelido, UsuarioId usuarioId) {
        boolean apelidoEmUso = usuarioServico.listarTodos().stream()
                .anyMatch(usuario -> usuario.getApelido().getValor().equalsIgnoreCase(apelido.getValor())
                        && !usuario.getId().equals(usuarioId));

        if (apelidoEmUso) {
            throw new IllegalArgumentException("Ja existe um usuario cadastrado com este apelido");
        }
    }

    private String valorOuAtual(String valor, String atual) {
        return valor == null || valor.isBlank() ? atual : valor;
    }
}
