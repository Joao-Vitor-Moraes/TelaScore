package com.requisitos.avaliacaofilmes.TelaScore.aplicacao.identidade;

import com.requisitos.avaliacaofilmes.TelaScore.dominio.identidade.usuario.Apelido;
import com.requisitos.avaliacaofilmes.TelaScore.dominio.identidade.usuario.Usuario;
import com.requisitos.avaliacaofilmes.TelaScore.dominio.identidade.usuario.UsuarioId;
import com.requisitos.avaliacaofilmes.TelaScore.dominio.identidade.usuario.UsuarioLogado;
import com.requisitos.avaliacaofilmes.TelaScore.dominio.identidade.usuario.UsuarioServico;

public class EditarMeuUsuarioCasoDeUso {

    private final UsuarioServico usuarioServico;
    private final SessaoUsuario sessaoUsuario;

    public EditarMeuUsuarioCasoDeUso(UsuarioServico usuarioServico, SessaoUsuario sessaoUsuario) {
        this.usuarioServico = usuarioServico;
        this.sessaoUsuario = sessaoUsuario;
    }

    public Usuario executar(EditarMeuUsuarioComando comando) {
        UsuarioLogado usuarioLogado = sessaoUsuario.obterUsuarioLogado();

        if (usuarioLogado == null) {
            throw new IllegalStateException("Usuario nao esta logado");
        }

        UsuarioId usuarioId = new UsuarioId(usuarioLogado.getId().getId());
        Usuario usuarioAtual = usuarioServico.obter(usuarioId);

        if (usuarioAtual == null) {
            throw new IllegalArgumentException("Usuario nao encontrado");
        }

        Apelido apelido = apelidoAtualizado(comando.apelido(), usuarioAtual);
        validarApelidoDisponivel(apelido, usuarioId);

        Usuario usuarioEditado = new Usuario(
                usuarioId,
                valorOuAtual(comando.nome(), usuarioAtual.getNome()),
                usuarioAtual.getEmail(),
                usuarioAtual.getSenha(),
                usuarioAtual.getPapel(),
                apelido,
                comando.biografia(),
                comando.avatarUrl());

        usuarioServico.salvar(usuarioEditado);
        return usuarioEditado;
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
