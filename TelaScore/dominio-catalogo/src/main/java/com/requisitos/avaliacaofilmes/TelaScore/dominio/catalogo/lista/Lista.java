package com.requisitos.avaliacaofilmes.TelaScore.dominio.catalogo.lista;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import static org.apache.commons.lang3.Validate.notBlank;
import static org.apache.commons.lang3.Validate.notNull;

import com.requisitos.avaliacaofilmes.TelaScore.dominio.identidade.usuario.UsuarioId;

public class Lista {
	private final ListaId id;
	private final UsuarioId donoId;

	private String titulo;
	private String descricao;
	private final boolean ranqueada;

	private final List<ItemLista> itens = new ArrayList<>();

	public Lista(ListaId id, UsuarioId donoId, String titulo, String descricao, boolean ranqueada) {
		notNull(id, "O id da lista não pode ser nulo");
		notNull(donoId, "O id do dono da lista não pode ser nulo");
		
		this.id = id;
		this.donoId = donoId;
		this.ranqueada = ranqueada;
		
		setTitulo(titulo);
		setDescricao(descricao);
	}

	public ListaId getId() { return id; }
	public UsuarioId getDonoId() { return donoId; }
	public boolean isRanqueada() { return ranqueada; }

	public void setTitulo(String titulo) {
		notNull(titulo, "O título da lista não pode ser nulo");
		notBlank(titulo, "O título não pode estar em branco");
		this.titulo = titulo;
	}
	public String getTitulo() { return titulo; }

	public void setDescricao(String descricao) {
		this.descricao = descricao;
	}
	public String getDescricao() { return descricao; }

	public List<ItemLista> getItens() {
		return Collections.unmodifiableList(itens);
	}

	private void garantirPermissao(UsuarioId usuarioId) {
		if (!this.donoId.equals(usuarioId)) {
			throw new IllegalStateException("Apenas o criador da lista tem permissão para modificá-la.");
		}
	}

	public void adicionarItem(ItemLista item, UsuarioId usuarioId) {
		garantirPermissao(usuarioId);
		notNull(item, "O item não pode ser nulo");
		
		boolean filmeJaExiste = itens.stream()
				.anyMatch(i -> i.getFilmeId().equals(item.getFilmeId()));
		
		if (filmeJaExiste) {
			throw new IllegalArgumentException("O filme já existe nesta lista");
		}
		
		itens.add(item);
	}
	
	public void removerItemPorFilme(com.requisitos.avaliacaofilmes.TelaScore.dominio.catalogo.filme.FilmeId filmeId, UsuarioId usuarioId) {
		garantirPermissao(usuarioId);
		notNull(filmeId, "O id do filme não pode ser nulo");
		itens.removeIf(item -> item.getFilmeId().equals(filmeId));
	}

	
	public void moverFilmeParaPosicao(com.requisitos.avaliacaofilmes.TelaScore.dominio.catalogo.filme.FilmeId filmeId, int novaPosicao, UsuarioId usuarioId) {
		garantirPermissao(usuarioId);
		if (!this.ranqueada) {
			throw new IllegalStateException("Apenas listas ranqueadas permitem reordenação manual");
		}
		
		notNull(filmeId, "O id do filme não pode ser nulo");
		
		if (novaPosicao < 1 || novaPosicao > itens.size()) {
			throw new IllegalArgumentException("Posição inválida");
		}
		
		ItemLista itemParaMover = itens.stream()
				.filter(i -> i.getFilmeId().equals(filmeId))
				.findFirst()
				.orElseThrow(() -> new IllegalArgumentException("O filme não está nesta lista"));
		
		itens.remove(itemParaMover);
		itens.add(novaPosicao - 1, itemParaMover);
	}
}