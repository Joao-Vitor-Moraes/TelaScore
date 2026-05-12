package com.requisitos.avaliacaofilmes.TelaScore.aplicacao.catalogo;

import com.requisitos.avaliacaofilmes.TelaScore.dominio.catalogo.filme.FilmeId;
import com.requisitos.avaliacaofilmes.TelaScore.dominio.catalogo.lista.Lista;
import com.requisitos.avaliacaofilmes.TelaScore.dominio.catalogo.lista.ListaId;
import com.requisitos.avaliacaofilmes.TelaScore.dominio.catalogo.lista.ListaRepositorio;
import com.requisitos.avaliacaofilmes.TelaScore.dominio.catalogo.lista.ListaServico;
import com.requisitos.avaliacaofilmes.TelaScore.dominio.identidade.usuario.UsuarioId;

public class RemoverFilmeDaListaCasoDeUso {

	private final ListaRepositorio listaRepositorio;
	private final ListaServico listaServico;

	public RemoverFilmeDaListaCasoDeUso(ListaRepositorio listaRepositorio, ListaServico listaServico) {
		this.listaRepositorio = listaRepositorio;
		this.listaServico = listaServico;
	}

	public void executar(RemoverFilmeDaListaComando comando) {
		ListaId listaId = new ListaId(comando.listaId());
		UsuarioId usuarioId = new UsuarioId(comando.usuarioId());
		FilmeId filmeId = new FilmeId(String.valueOf(comando.filmeId()));

		Lista lista = listaRepositorio.obter(listaId);
		if (lista == null) {
			throw new IllegalArgumentException("A lista informada não existe.");
		}

		listaServico.removerFilme(lista, filmeId, usuarioId); 
	}
}