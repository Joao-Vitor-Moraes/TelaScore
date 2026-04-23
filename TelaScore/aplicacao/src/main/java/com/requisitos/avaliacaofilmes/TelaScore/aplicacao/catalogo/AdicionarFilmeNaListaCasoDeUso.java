package com.requisitos.avaliacaofilmes.TelaScore.aplicacao.catalogo;

import com.requisitos.avaliacaofilmes.TelaScore.dominio.catalogo.filme.FilmeId;
import com.requisitos.avaliacaofilmes.TelaScore.dominio.catalogo.filme.FilmeRepositorio;
import com.requisitos.avaliacaofilmes.TelaScore.dominio.catalogo.lista.ItemLista;
import com.requisitos.avaliacaofilmes.TelaScore.dominio.catalogo.lista.Lista;
import com.requisitos.avaliacaofilmes.TelaScore.dominio.catalogo.lista.ListaId;
import com.requisitos.avaliacaofilmes.TelaScore.dominio.catalogo.lista.ListaRepositorio;
import com.requisitos.avaliacaofilmes.TelaScore.dominio.identidade.usuario.UsuarioId;

public class AdicionarFilmeNaListaCasoDeUso {

	private final ListaRepositorio listaRepositorio;
	private final FilmeRepositorio filmeRepositorio;

	public AdicionarFilmeNaListaCasoDeUso(ListaRepositorio listaRepositorio, FilmeRepositorio filmeRepositorio) {
		this.listaRepositorio = listaRepositorio;
		this.filmeRepositorio = filmeRepositorio;
	}

	public void executar(AdicionarFilmeNaListaComando comando) {
		ListaId listaId = new ListaId(comando.listaId());
		UsuarioId usuarioId = new UsuarioId(comando.usuarioId());
		FilmeId filmeId = new FilmeId(String.valueOf(comando.filmeId()));

		Lista lista = listaRepositorio.obter(listaId);
		if (lista == null) {
			throw new IllegalArgumentException("A lista informada não existe.");
		}

		if (!lista.getDonoId().equals(usuarioId)) {
			throw new IllegalStateException("Apenas o criador da lista tem permissão para modificá-la.");
		}

		if (filmeRepositorio.obter(filmeId) == null) {
			throw new IllegalArgumentException("O filme informado não existe no catálogo do sistema.");
		}

		lista.adicionarItem(new ItemLista(filmeId, null));

		listaRepositorio.salvar(lista);
	}
}