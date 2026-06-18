package com.requisitos.avaliacaofilmes.TelaScore.aplicacao.catalogo;

import com.requisitos.avaliacaofilmes.TelaScore.aplicacao.identidade.GeradorId;
import com.requisitos.avaliacaofilmes.TelaScore.dominio.catalogo.avaliacao.Avaliacao;
import com.requisitos.avaliacaofilmes.TelaScore.dominio.catalogo.avaliacao.AvaliacaoId;
import com.requisitos.avaliacaofilmes.TelaScore.dominio.catalogo.avaliacao.AvaliacaoRepositorio;
import com.requisitos.avaliacaofilmes.TelaScore.dominio.catalogo.avaliacao.Nota;
import com.requisitos.avaliacaofilmes.TelaScore.dominio.catalogo.filme.FilmeId;
import com.requisitos.avaliacaofilmes.TelaScore.dominio.catalogo.filme.FilmeRepositorio;
import com.requisitos.avaliacaofilmes.TelaScore.dominio.catalogo.lista.Lista;
import com.requisitos.avaliacaofilmes.TelaScore.dominio.catalogo.lista.ListaRepositorio;
import com.requisitos.avaliacaofilmes.TelaScore.dominio.catalogo.lista.ListaServico;
import com.requisitos.avaliacaofilmes.TelaScore.dominio.catalogo.lista.TipoLista;
import com.requisitos.avaliacaofilmes.TelaScore.dominio.identidade.usuario.UsuarioId;

public class AvaliarFilmeCasoDeUso {

	private final AvaliacaoRepositorio avaliacaoRepositorio;
	private final FilmeRepositorio filmeRepositorio;
	private final GeradorId geradorId;
	private final ListaRepositorio listaRepositorio;
	private final ListaServico listaServico;

	public AvaliarFilmeCasoDeUso(AvaliacaoRepositorio avaliacaoRepositorio, FilmeRepositorio filmeRepositorio,
			GeradorId geradorId, ListaRepositorio listaRepositorio, ListaServico listaServico) {
		this.avaliacaoRepositorio = avaliacaoRepositorio;
		this.filmeRepositorio = filmeRepositorio;
		this.geradorId = geradorId;
		this.listaRepositorio = listaRepositorio;
		this.listaServico = listaServico;
	}

	public void executar(AvaliarFilmeComando comando) {
		FilmeId filmeId = new FilmeId(String.valueOf(comando.filmeId()));
		if (filmeRepositorio.obter(filmeId) == null) {
			throw new IllegalArgumentException("Não é possível avaliar um filme que não existe.");
		}

		UsuarioId usuarioId = new UsuarioId(comando.usuarioId());

		Nota nota = new Nota(comando.valorNota());

		AvaliacaoId avaliacaoId = new AvaliacaoId(geradorId.gerarProximoIdAvaliacao());

		Avaliacao avaliacao = new Avaliacao(avaliacaoId, filmeId, usuarioId, nota, comando.comentario(), comando.visibilidade());

		avaliacaoRepositorio.salvar(avaliacao);

		for (Lista lista : listaRepositorio.pesquisarPorDono(usuarioId)) {
			if (lista.getTipo() == TipoLista.WATCHLIST) {
				listaServico.registrarFilmeAssistido(lista, filmeId, usuarioId);
			}
		}
	}
}