package com.requisitos.avaliacaofilmes.TelaScore.dominio.catalogo.lista;

import static org.apache.commons.lang3.Validate.notNull;

public class ListaServico {
	private final ListaRepositorio listaRepositorio;

	public ListaServico(ListaRepositorio listaRepositorio) {
		notNull(listaRepositorio, "O repositório de listas não pode ser nulo");
		this.listaRepositorio = listaRepositorio;
	}

	public void salvar(Lista lista) {
		notNull(lista, "A lista não pode ser nula");
		listaRepositorio.salvar(lista);
	}
}