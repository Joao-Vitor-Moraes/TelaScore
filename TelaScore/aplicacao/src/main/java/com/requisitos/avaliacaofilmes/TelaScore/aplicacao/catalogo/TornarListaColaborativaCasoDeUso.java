package com.requisitos.avaliacaofilmes.TelaScore.aplicacao.catalogo;

import com.requisitos.avaliacaofilmes.TelaScore.dominio.catalogo.lista.Lista;
import com.requisitos.avaliacaofilmes.TelaScore.dominio.catalogo.lista.ListaId;
import com.requisitos.avaliacaofilmes.TelaScore.dominio.catalogo.lista.ListaRepositorio;
import com.requisitos.avaliacaofilmes.TelaScore.dominio.catalogo.lista.ListaServico;
import com.requisitos.avaliacaofilmes.TelaScore.dominio.identidade.usuario.UsuarioId;

public class TornarListaColaborativaCasoDeUso {

	private final ListaRepositorio listaRepositorio;
	private final ListaServico listaServico;

	public TornarListaColaborativaCasoDeUso(ListaRepositorio listaRepositorio, ListaServico listaServico) {
		this.listaRepositorio = listaRepositorio;
		this.listaServico = listaServico;
	}

	public void executar(TornarListaColaborativaComando comando) {
		ListaId listaId = new ListaId(comando.listaId());
		UsuarioId usuarioId = new UsuarioId(comando.usuarioId());

		Lista lista = listaRepositorio.obter(listaId);
		if (lista == null) {
			throw new IllegalArgumentException("A lista informada não existe.");
		}

		listaServico.tornarColaborativa(lista, usuarioId);
	}
}
