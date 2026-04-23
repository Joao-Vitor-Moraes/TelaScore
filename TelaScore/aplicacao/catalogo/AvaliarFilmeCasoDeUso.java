package com.requisitos.avaliacaofilmes.TelaScore.aplicacao.catalogo.servico;

import com.requisitos.avaliacaofilmes.TelaScore.aplicacao.catalogo.dto.AvaliarFilmeComando;
import com.requisitos.avaliacaofilmes.TelaScore.aplicacao.identidade.servico.GeradorId;
import com.requisitos.avaliacaofilmes.TelaScore.dominio.catalogo.avaliacao.Avaliacao;
import com.requisitos.avaliacaofilmes.TelaScore.dominio.catalogo.avaliacao.AvaliacaoId;
import com.requisitos.avaliacaofilmes.TelaScore.dominio.catalogo.avaliacao.AvaliacaoRepositorio;
import com.requisitos.avaliacaofilmes.TelaScore.dominio.catalogo.avaliacao.Nota;
import com.requisitos.avaliacaofilmes.TelaScore.dominio.catalogo.filme.FilmeId;
import com.requisitos.avaliacaofilmes.TelaScore.dominio.catalogo.filme.FilmeRepositorio;
import com.requisitos.avaliacaofilmes.TelaScore.dominio.identidade.usuario.UsuarioId;

public class AvaliarFilmeCasoDeUso {

	private final AvaliacaoRepositorio avaliacaoRepositorio;
	private final FilmeRepositorio filmeRepositorio;
	private final GeradorId geradorId;

	public AvaliarFilmeCasoDeUso(AvaliacaoRepositorio avaliacaoRepositorio, FilmeRepositorio filmeRepositorio, GeradorId geradorId) {
		this.avaliacaoRepositorio = avaliacaoRepositorio;
		this.filmeRepositorio = filmeRepositorio;
		this.geradorId = geradorId;
	}

	public void executar(AvaliarFilmeComando comando) {
		FilmeId filmeId = new FilmeId(String.valueOf(comando.filmeId()));
		if (filmeRepositorio.obter(filmeId) == null) {
			throw new IllegalArgumentException("Não é possível avaliar um filme que não existe.");
		}
		
		UsuarioId usuarioId = new UsuarioId(comando.usuarioId());
		
		Nota nota = new Nota(comando.valorNota());
		
		AvaliacaoId avaliacaoId = new AvaliacaoId(geradorId.gerarProximoIdAvaliacao());
		
		Avaliacao avaliacao = new Avaliacao(avaliacaoId, filmeId, usuarioId, nota, comando.comentario());
		
		avaliacaoRepositorio.salvar(avaliacao);

		
		// NOTA ARQUITETURAL: No futuro, é aqui que chamaríamos um "PublicadorDeEventos"
	}
}