package com.requisitos.avaliacaofilmes.TelaScore.dominio.catalogo.solicitacao;

import static org.apache.commons.lang3.Validate.notNull;

import com.requisitos.avaliacaofilmes.TelaScore.dominio.identidade.usuario.PapelUsuario;
import com.requisitos.avaliacaofilmes.TelaScore.dominio.identidade.usuario.Usuario;

public class SolicitacaoServico {
	private final SolicitacaoRepositorio repositorio;

	public SolicitacaoServico(SolicitacaoRepositorio repositorio) {
		notNull(repositorio, "O repositório de solicitações não pode ser nulo");
		this.repositorio = repositorio;
	}

	public void enviarSolicitacao(SolicitacaoFilme solicitacao) {
		notNull(solicitacao, "A solicitação não pode ser nula");
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
}