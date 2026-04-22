package com.requisitos.avaliacaofilmes.TelaScore.dominio.catalogo.lista;

import static org.apache.commons.lang3.Validate.isTrue;
import static org.apache.commons.lang3.Validate.notNull;

import java.time.LocalDate;

import com.requisitos.avaliacaofilmes.TelaScore.dominio.catalogo.filme.FilmeId;

public class ItemLista {
	private final FilmeId filmeId;
	private Integer posicao;
	private final LocalDate dataAdicao;

	public ItemLista(FilmeId filmeId, Integer posicao) {
		notNull(filmeId, "O id do filme não pode ser nulo");
		
		if (posicao != null) {
			isTrue(posicao > 0, "A posição deve ser maior que zero");
		}

		this.filmeId = filmeId;
		this.posicao = posicao;
		this.dataAdicao = LocalDate.now();
	}

	public FilmeId getFilmeId() { return filmeId; }
	public Integer getPosicao() { return posicao; }
	public LocalDate getDataAdicao() { return dataAdicao; }

	public void setPosicao(Integer posicao) {
		if (posicao != null) {
			isTrue(posicao > 0, "A posição deve ser maior que zero");
		}
		this.posicao = posicao;
	}
}