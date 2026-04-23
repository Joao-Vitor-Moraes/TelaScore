package com.requisitos.avaliacaofilmes.TelaScore.aplicacao.catalogo;

import com.requisitos.avaliacaofilmes.TelaScore.aplicacao.identidade.GeradorId;
import com.requisitos.avaliacaofilmes.TelaScore.dominio.catalogo.lista.Lista;
import com.requisitos.avaliacaofilmes.TelaScore.dominio.catalogo.lista.ListaId;
import com.requisitos.avaliacaofilmes.TelaScore.dominio.catalogo.lista.ListaRepositorio;
import com.requisitos.avaliacaofilmes.TelaScore.dominio.identidade.usuario.UsuarioId;

public class CriarListaCasoDeUso {

	private final ListaRepositorio listaRepositorio;
	private final GeradorId geradorId;

	public CriarListaCasoDeUso(ListaRepositorio listaRepositorio, GeradorId geradorId) {
		this.listaRepositorio = listaRepositorio;
		this.geradorId = geradorId;
	}

	public void executar(CriarListaComando comando) {
		UsuarioId criadorId = new UsuarioId(comando.criadorId());
		ListaId novaListaId = new ListaId(geradorId.gerarProximoIdLista());
		
		Lista lista = new Lista(novaListaId, criadorId, comando.nome(), comando.descricao(), comando.rankeada());
		
		listaRepositorio.salvar(lista);
	}
}