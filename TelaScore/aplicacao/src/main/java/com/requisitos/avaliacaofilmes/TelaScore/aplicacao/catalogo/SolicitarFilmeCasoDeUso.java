package com.requisitos.avaliacaofilmes.TelaScore.aplicacao.catalogo;

import com.requisitos.avaliacaofilmes.TelaScore.aplicacao.identidade.GeradorId;
import com.requisitos.avaliacaofilmes.TelaScore.dominio.catalogo.solicitacao.SolicitacaoFilme;
import com.requisitos.avaliacaofilmes.TelaScore.dominio.catalogo.solicitacao.SolicitacaoId;
import com.requisitos.avaliacaofilmes.TelaScore.dominio.catalogo.solicitacao.SolicitacaoServico;
import com.requisitos.avaliacaofilmes.TelaScore.dominio.identidade.usuario.UsuarioId;

public class SolicitarFilmeCasoDeUso {

	private final SolicitacaoServico solicitacaoServico;
	private final GeradorId geradorId;

	public SolicitarFilmeCasoDeUso(SolicitacaoServico solicitacaoServico, GeradorId geradorId) {
		this.solicitacaoServico = solicitacaoServico;
		this.geradorId = geradorId;
	}

	public SolicitacaoResumo executar(SolicitarFilmeComando comando) {
		SolicitacaoId novaSolicitacaoId = new SolicitacaoId(geradorId.gerarProximoIdSolicitacao());
		UsuarioId solicitanteId = new UsuarioId(comando.solicitanteId());

		SolicitacaoFilme solicitacao = new SolicitacaoFilme(novaSolicitacaoId, solicitanteId, comando.tituloSugerido());
		if (comando.justificativa() != null && !comando.justificativa().isBlank()) {
			solicitacao.setJustificativa(comando.justificativa());
		}

		solicitacaoServico.enviarSolicitacao(solicitacao);

		return new SolicitacaoResumo(
				solicitacao.getId().getId(),
				solicitacao.getSolicitanteId().getId(),
				solicitacao.getTituloSugerido(),
				solicitacao.getJustificativa(),
				solicitacao.getStatus().name(),
				solicitacao.getDataCriacao()
		);
	}
}
