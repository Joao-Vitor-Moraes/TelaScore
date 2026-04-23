package com.requisitos.avaliacaofilmes.TelaScore.aplicacao.catalogo;

import com.requisitos.avaliacaofilmes.TelaScore.dominio.catalogo.filme.FilmeId;
import com.requisitos.avaliacaofilmes.TelaScore.dominio.catalogo.lista.Lista;
import com.requisitos.avaliacaofilmes.TelaScore.dominio.catalogo.lista.ListaId;
import com.requisitos.avaliacaofilmes.TelaScore.dominio.catalogo.lista.ListaRepositorio;
import com.requisitos.avaliacaofilmes.TelaScore.dominio.identidade.usuario.UsuarioId;

public class ReordenarItemListaCasoDeUso {

	private final ListaRepositorio listaRepositorio;

	public ReordenarItemListaCasoDeUso(ListaRepositorio listaRepositorio) {
		this.listaRepositorio = listaRepositorio;
	}

	public void executar(ReordenarItemListaComando comando) {
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

		if (!lista.isRanqueada()) {
			throw new IllegalStateException("Esta lista não é ranqueada, portanto não suporta reordenação manual.");
		}

		lista.getItens().stream()
			.filter(item -> item.getFilmeId().equals(filmeId))
			.findFirst()
			.ifPresent(item -> item.setPosicao(comando.novaPosicao()));

		listaRepositorio.salvar(lista);
	}
}