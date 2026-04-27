package com.requisitos.avaliacaofilmes.TelaScore.dominio.catalogo.solicitacao;

import static org.apache.commons.lang3.Validate.notNull;

import com.requisitos.avaliacaofilmes.TelaScore.dominio.catalogo.filme.FilmeRepositorio;
import com.requisitos.avaliacaofilmes.TelaScore.dominio.identidade.usuario.PapelUsuario;
import com.requisitos.avaliacaofilmes.TelaScore.dominio.identidade.usuario.Usuario;
import com.requisitos.avaliacaofilmes.TelaScore.dominio.identidade.usuario.UsuarioId;

public class SolicitacaoServico {
    private final SolicitacaoRepositorio repositorio;
    private final FilmeRepositorio filmeRepositorio;

    public SolicitacaoServico(SolicitacaoRepositorio repositorio, FilmeRepositorio filmeRepositorio) {
        notNull(repositorio, "O repositório de solicitações não pode ser nulo");
        notNull(filmeRepositorio, "O repositório de filmes não pode ser nulo");
        this.repositorio = repositorio;
        this.filmeRepositorio = filmeRepositorio;
    }

    public void enviarSolicitacao(SolicitacaoFilme solicitacao) {
        notNull(solicitacao, "A solicitação não pode ser nula");
        if (filmeRepositorio.existeComTitulo(solicitacao.getTituloSugerido())) {
            throw new IllegalStateException("Filme já cadastrado");
        }
        repositorio.salvar(solicitacao);
    }

    public void avaliarSolicitacao(SolicitacaoFilme solicitacao, Usuario avaliador, boolean aprovado) {
        notNull(solicitacao, "A solicitação não pode ser nula");
        notNull(avaliador, "O avaliador não pode ser nulo");

        if (avaliador.getPapel() != PapelUsuario.ADMIN) {
            throw new IllegalStateException("Apenas administradores podem avaliar solicitações de filmes.");
        }

        if (aprovado) {
            solicitacao.aprovar();
        } else {
            solicitacao.rejeitar();
        }

        repositorio.salvar(solicitacao);
    }

    public void cancelarSolicitacao(SolicitacaoFilme solicitacao, UsuarioId usuarioId) {
        notNull(solicitacao, "A solicitação não pode ser nula");
        notNull(usuarioId, "O ID do usuário não pode ser nulo");

        solicitacao.cancelar(usuarioId);
        repositorio.salvar(solicitacao);
    }

    public void solicitarAjustes(SolicitacaoFilme solicitacao, Usuario avaliador, String feedback) {
        notNull(solicitacao, "A solicitação não pode ser nula");
        notNull(avaliador, "O avaliador não pode ser nulo");

        if (avaliador.getPapel() != PapelUsuario.ADMIN) {
            throw new IllegalStateException("Apenas administradores podem solicitar ajustes.");
        }

        solicitacao.solicitarAjustes(feedback);
        repositorio.salvar(solicitacao);
    }
}