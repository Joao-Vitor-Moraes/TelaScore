package com.requisitos.avaliacaofilmes.TelaScore.aplicacao.catalogo;

import com.requisitos.avaliacaofilmes.TelaScore.aplicacao.identidade.GeradorId;
import com.requisitos.avaliacaofilmes.TelaScore.dominio.catalogo.filme.Filme;
import com.requisitos.avaliacaofilmes.TelaScore.dominio.catalogo.filme.FilmeId;
import com.requisitos.avaliacaofilmes.TelaScore.dominio.catalogo.filme.FilmeRepositorio;
import com.requisitos.avaliacaofilmes.TelaScore.dominio.catalogo.solicitacao.SolicitacaoFilme;
import com.requisitos.avaliacaofilmes.TelaScore.dominio.catalogo.solicitacao.SolicitacaoId;
import com.requisitos.avaliacaofilmes.TelaScore.dominio.catalogo.solicitacao.SolicitacaoRepositorio;
import com.requisitos.avaliacaofilmes.TelaScore.dominio.catalogo.solicitacao.SolicitacaoServico;
import com.requisitos.avaliacaofilmes.TelaScore.dominio.identidade.usuario.Usuario;
import com.requisitos.avaliacaofilmes.TelaScore.dominio.identidade.usuario.UsuarioId;
import com.requisitos.avaliacaofilmes.TelaScore.dominio.identidade.usuario.UsuarioRepositorio;

import java.util.Collections;

public class AvaliarSolicitacaoFilmeCasoDeUso {

	private final SolicitacaoRepositorio solicitacaoRepositorio;
	private final UsuarioRepositorio usuarioRepositorio;
	private final SolicitacaoServico solicitacaoServico;
	private final FilmeRepositorio filmeRepositorio;
	private final GeradorId geradorId;

	public AvaliarSolicitacaoFilmeCasoDeUso(
			SolicitacaoRepositorio solicitacaoRepositorio,
			UsuarioRepositorio usuarioRepositorio,
			SolicitacaoServico solicitacaoServico,
			FilmeRepositorio filmeRepositorio,
			GeradorId geradorId) {
		this.solicitacaoRepositorio = solicitacaoRepositorio;
		this.usuarioRepositorio = usuarioRepositorio;
		this.solicitacaoServico = solicitacaoServico;
		this.filmeRepositorio = filmeRepositorio;
		this.geradorId = geradorId;
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

		if (comando.aprovar()) {
			FilmeId novoId = new FilmeId(String.valueOf(geradorId.gerarProximoIdFilme()));
			Filme filme = new Filme(novoId, solicitacao.getTituloSugerido(),
					solicitacao.getJustificativa() != null ? solicitacao.getJustificativa() : "",
					solicitacao.getAno(),
					Collections.emptyList());
			filme.setImagemUrl(solicitacao.getFotoUrl());
			filmeRepositorio.salvar(filme);
		}
	}
}
