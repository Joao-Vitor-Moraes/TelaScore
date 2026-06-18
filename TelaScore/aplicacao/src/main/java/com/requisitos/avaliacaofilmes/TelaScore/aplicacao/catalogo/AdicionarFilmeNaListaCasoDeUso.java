package com.requisitos.avaliacaofilmes.TelaScore.aplicacao.catalogo;

import com.requisitos.avaliacaofilmes.TelaScore.dominio.catalogo.avaliacao.AvaliacaoRepositorio;
import com.requisitos.avaliacaofilmes.TelaScore.dominio.catalogo.filme.FilmeId;
import com.requisitos.avaliacaofilmes.TelaScore.dominio.catalogo.filme.FilmeRepositorio;
import com.requisitos.avaliacaofilmes.TelaScore.dominio.catalogo.lista.ItemLista;
import com.requisitos.avaliacaofilmes.TelaScore.dominio.catalogo.lista.Lista;
import com.requisitos.avaliacaofilmes.TelaScore.dominio.catalogo.lista.ListaId;
import com.requisitos.avaliacaofilmes.TelaScore.dominio.catalogo.lista.ListaRepositorio;
import com.requisitos.avaliacaofilmes.TelaScore.dominio.catalogo.lista.ListaServico;
import com.requisitos.avaliacaofilmes.TelaScore.dominio.identidade.usuario.UsuarioId;

public class AdicionarFilmeNaListaCasoDeUso {

	private final ListaRepositorio listaRepositorio;
	private final FilmeRepositorio filmeRepositorio;
	private final ListaServico listaServico;
	private final AvaliacaoRepositorio avaliacaoRepositorio;

	public AdicionarFilmeNaListaCasoDeUso(ListaRepositorio listaRepositorio, FilmeRepositorio filmeRepositorio,
			ListaServico listaServico, AvaliacaoRepositorio avaliacaoRepositorio) {
		this.listaRepositorio = listaRepositorio;
		this.filmeRepositorio = filmeRepositorio;
		this.listaServico = listaServico;
		this.avaliacaoRepositorio = avaliacaoRepositorio;
	}

	public void executar(AdicionarFilmeNaListaComando comando) {
		ListaId listaId = new ListaId(comando.listaId());
		UsuarioId usuarioId = new UsuarioId(comando.usuarioId());
		FilmeId filmeId = new FilmeId(String.valueOf(comando.filmeId()));

		Lista lista = listaRepositorio.obter(listaId);
		if (lista == null) {
			throw new IllegalArgumentException("A lista informada não existe.");
		}

		if (filmeRepositorio.obter(filmeId) == null) {
			throw new IllegalArgumentException("O filme informado não existe no catálogo do sistema.");
		}

		boolean filmeJaFoiAssistido = avaliacaoRepositorio.existeAvaliacaoPorFilmeEUsuario(filmeId, usuarioId);

		listaServico.adicionarFilme(lista, new ItemLista(filmeId, null), usuarioId, filmeJaFoiAssistido);
	}
}