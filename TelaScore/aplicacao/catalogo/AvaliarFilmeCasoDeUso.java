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
		// 1. Garante que o filme existe antes de deixar avaliar
		FilmeId filmeId = new FilmeId(comando.filmeId());
		if (filmeRepositorio.obter(filmeId) == null) {
			throw new IllegalArgumentException("Não é possível avaliar um filme que não existe.");
		}
		
		UsuarioId usuarioId = new UsuarioId(comando.usuarioId());
		
		// 2. Valida a nota de 1 a 5 estrelas usando o nosso Objeto de Valor do Domínio!
		Nota nota = new Nota(comando.valorNota());
		
		// 3. Pede um novo ID
		AvaliacaoId avaliacaoId = new AvaliacaoId(geradorId.gerarProximoIdAvaliacao());
		
		// 4. Monta a Avaliação
		Avaliacao avaliacao = new Avaliacao(avaliacaoId, filmeId, usuarioId, nota);
		avaliacao.setComentario(comando.comentario());
		
		// 5. Salva na base de dados
		avaliacaoRepositorio.salvar(avaliacao);
		
		// NOTA ARQUITETURAL: No futuro, é aqui que chamaríamos um "PublicadorDeEventos"
		// para avisar o dominio-analise que o usuário deve ganhar pontos de recompensa!
	}
}