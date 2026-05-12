package com.requisitos.avaliacaofilmes.TelaScore.aplicacao.catalogo;

import com.requisitos.avaliacaofilmes.TelaScore.dominio.catalogo.solicitacao.SolicitacaoFilme;
import com.requisitos.avaliacaofilmes.TelaScore.dominio.catalogo.solicitacao.SolicitacaoId;
import com.requisitos.avaliacaofilmes.TelaScore.dominio.catalogo.solicitacao.SolicitacaoRepositorio;
import com.requisitos.avaliacaofilmes.TelaScore.dominio.catalogo.solicitacao.SolicitacaoServico;
import com.requisitos.avaliacaofilmes.TelaScore.dominio.identidade.usuario.UsuarioId;

public class CancelarSolicitacaoFilmeCasoDeUso {

	private final SolicitacaoRepositorio solicitacaoRepositorio;
	private final SolicitacaoServico solicitacaoServico;

	public CancelarSolicitacaoFilmeCasoDeUso(
			SolicitacaoRepositorio solicitacaoRepositorio,
			SolicitacaoServico solicitacaoServico) {
		this.solicitacaoRepositorio = solicitacaoRepositorio;
		this.solicitacaoServico = solicitacaoServico;
	}

	public void executar(CancelarSolicitacaoFilmeComando comando) {
		SolicitacaoFilme solicitacao = solicitacaoRepositorio.obter(new SolicitacaoId(comando.solicitacaoId()));

		if (solicitacao == null) {
			throw new IllegalArgumentException("Solicitação de filme não encontrada.");
		}

		solicitacaoServico.cancelarSolicitacao(solicitacao, new UsuarioId(comando.usuarioId()));
	}
}
