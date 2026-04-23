package com.requisitos.avaliacaofilmes.TelaScore.dominio.social.comunidade;

import static org.apache.commons.lang3.Validate.notBlank;
import static org.apache.commons.lang3.Validate.notNull;

import java.time.LocalDateTime;

public class Comunidade {
	private final ComunidadeId id;
	
	private String nome;
	private String descricao;
	private String regras;
	private final LocalDateTime dataCriacao;

	public Comunidade(ComunidadeId id, String nome, String descricao) {
		notNull(id, "O id da comunidade não pode ser nulo");
		this.id = id;
		this.dataCriacao = LocalDateTime.now();
		
		setNome(nome);
		setDescricao(descricao);
	}

	public ComunidadeId getId() { return id; }
	public LocalDateTime getDataCriacao() { return dataCriacao; }

	public final void setNome(String nome) {
		notNull(nome, "O nome da comunidade não pode ser nulo");
		notBlank(nome, "O nome não pode estar em branco");
		this.nome = nome;
	}
	public String getNome() { return nome; }

	public final void setDescricao(String descricao) {
		notNull(descricao, "A descrição não pode ser nula");
		notBlank(descricao, "A descrição não pode estar em branco");
		this.descricao = descricao;
	}
	public String getDescricao() { return descricao; }

	public final void setRegras(String regras) {
		this.regras = regras;
	}
	public String getRegras() { return regras; }
}