package com.requisitos.avaliacaofilmes.TelaScore.aplicacao.catalogo;

import com.requisitos.avaliacaofilmes.TelaScore.dominio.catalogo.solicitacao.SolicitacaoFilme;
import com.requisitos.avaliacaofilmes.TelaScore.dominio.catalogo.solicitacao.SolicitacaoId;
import com.requisitos.avaliacaofilmes.TelaScore.dominio.catalogo.solicitacao.SolicitacaoRepositorio;
import com.requisitos.avaliacaofilmes.TelaScore.dominio.catalogo.solicitacao.SolicitacaoServico;
import com.requisitos.avaliacaofilmes.TelaScore.dominio.identidade.usuario.Usuario;
import com.requisitos.avaliacaofilmes.TelaScore.dominio.identidade.usuario.UsuarioId;
import com.requisitos.avaliacaofilmes.TelaScore.dominio.identidade.usuario.UsuarioRepositorio;

public class AvaliarSolicitacaoFilmeCasoDeUso {

	private final SolicitacaoRepositorio solicitacaoRepositorio;
	private final UsuarioRepositorio usuarioRepositorio;
	private final SolicitacaoServico solicitacaoServico;

	public AvaliarSolicitacaoFilmeCasoDeUso(
			SolicitacaoRepositorio solicitacaoRepositorio,
			UsuarioRepositorio usuarioRepositorio,
			SolicitacaoServico solicitacaoServico) {
		this.solicitacaoRepositorio = solicitacaoRepositorio;
		this.usuarioRepositorio = usuarioRepositorio;
		this.solicitacaoServico = solicitacaoServico;
	}

	public void executar(AvaliarSolicitacaoFilmeComando comando) {
		Usuario avaliador = usuarioRepositorio.obter(new UsuarioId(comando.avaliadorId()));

		if (avaliador == null) {
			throw new IllegalArgumentException("Avaliador não encontrado no sistema.");
		}

		SolicitacaoFilme solicitacao = solicitacaoRepositorio.obter(new SolicitacaoId(comando.solicitacaoId()));

		if (solicitacao == null) {
			throw new IllegalArgumentException("Solicitação de filme não encontrada.");
		}

		solicitacaoServico.avaliarSolicitacao(solicitacao, avaliador, comando.aprovar());
	}
}
