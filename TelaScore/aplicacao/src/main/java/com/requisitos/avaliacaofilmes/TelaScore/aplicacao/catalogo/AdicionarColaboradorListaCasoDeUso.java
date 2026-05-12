package com.requisitos.avaliacaofilmes.TelaScore.aplicacao.catalogo;

import com.requisitos.avaliacaofilmes.TelaScore.dominio.catalogo.lista.Lista;
import com.requisitos.avaliacaofilmes.TelaScore.dominio.catalogo.lista.ListaId;
import com.requisitos.avaliacaofilmes.TelaScore.dominio.catalogo.lista.ListaRepositorio;
import com.requisitos.avaliacaofilmes.TelaScore.dominio.catalogo.lista.ListaServico;
import com.requisitos.avaliacaofilmes.TelaScore.dominio.identidade.usuario.UsuarioId;

public class AdicionarColaboradorListaCasoDeUso {

	private final ListaRepositorio listaRepositorio;
	private final ListaServico listaServico;

	public AdicionarColaboradorListaCasoDeUso(ListaRepositorio listaRepositorio, ListaServico listaServico) {
		this.listaRepositorio = listaRepositorio;
		this.listaServico = listaServico;
	}

	public void executar(AdicionarColaboradorListaComando comando) {
		ListaId listaId = new ListaId(comando.listaId());
		UsuarioId donoId = new UsuarioId(comando.donoId());
		UsuarioId novoColaboradorId = new UsuarioId(comando.novoColaboradorId());

		Lista lista = listaRepositorio.obter(listaId);
		if (lista == null) {
			throw new IllegalArgumentException("A lista informada não existe.");
		}

		listaServico.adicionarColaborador(lista, donoId, novoColaboradorId);
	}
}
