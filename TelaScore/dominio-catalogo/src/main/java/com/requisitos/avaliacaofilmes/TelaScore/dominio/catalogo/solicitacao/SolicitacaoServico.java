package com.requisitos.avaliacaofilmes.TelaScore.dominio.catalogo.solicitacao;

import static org.apache.commons.lang3.Validate.notNull;

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
}