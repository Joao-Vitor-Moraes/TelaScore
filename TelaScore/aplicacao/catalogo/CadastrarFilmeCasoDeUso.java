package com.requisitos.avaliacaofilmes.TelaScore.aplicacao.catalogo.servico;

import com.requisitos.avaliacaofilmes.TelaScore.aplicacao.catalogo.dto.CadastrarFilmeComando;
import com.requisitos.avaliacaofilmes.TelaScore.aplicacao.identidade.servico.GeradorId;
import com.requisitos.avaliacaofilmes.TelaScore.dominio.catalogo.diretor.DiretorId;
import com.requisitos.avaliacaofilmes.TelaScore.dominio.catalogo.diretor.DiretorRepositorio;
import com.requisitos.avaliacaofilmes.TelaScore.dominio.catalogo.filme.Filme;
import com.requisitos.avaliacaofilmes.TelaScore.dominio.catalogo.filme.FilmeId;
import com.requisitos.avaliacaofilmes.TelaScore.dominio.catalogo.filme.FilmeRepositorio;

public class CadastrarFilmeCasoDeUso {
	
	private final FilmeRepositorio filmeRepositorio;
	private final DiretorRepositorio diretorRepositorio;
	private final GeradorId geradorId;

	public CadastrarFilmeCasoDeUso(FilmeRepositorio filmeRepositorio, DiretorRepositorio diretorRepositorio, GeradorId geradorId) {
		this.filmeRepositorio = filmeRepositorio;
		this.diretorRepositorio = diretorRepositorio;
		this.geradorId = geradorId;
	}

	public void executar(CadastrarFilmeComando comando) {
		// 1. Verifica se o Diretor selecionado realmente existe no banco de dados
		DiretorId diretorId = new DiretorId(comando.diretorId());
		if (diretorRepositorio.obter(diretorId) == null) {
			throw new IllegalArgumentException("O diretor informado não existe no sistema.");
		}
		
		// 2. Pede um novo ID ao banco
		FilmeId novoFilmeId = new FilmeId(geradorId.gerarProximoIdFilme());
		
		// 3. Cria a entidade de Domínio (As regras como "Ano > 1888" são validadas aqui automaticamente!)
		Filme filme = new Filme(novoFilmeId, comando.titulo(), comando.anoLancamento(), diretorId);
		filme.setSinopse(comando.sinopse());
		
		// 4. Salva no banco de dados
		filmeRepositorio.salvar(filme);
	}
}