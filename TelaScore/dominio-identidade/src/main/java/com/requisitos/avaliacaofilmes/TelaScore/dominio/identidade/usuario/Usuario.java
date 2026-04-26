package com.requisitos.avaliacaofilmes.TelaScore.dominio.identidade.usuario;

import static org.apache.commons.lang3.Validate.notBlank;
import static org.apache.commons.lang3.Validate.notNull;

public class Usuario {
	private final UsuarioId id;

	private String nome;
	private Email email;
	private Senha senha;
	private PapelUsuario papel;

	public Usuario(UsuarioId id, String nome, Email email, Senha senha, PapelUsuario papel) {
		notNull(id, "O id do usuário não pode ser nulo");
		notNull(papel, "O papel do usuário não pode ser nulo");
		this.id = id;

		setNome(nome);
		setEmail(email);
		setSenha(senha);
		this.papel = papel;
	}

	public UsuarioId getId() {
		return id;
	}

	public String getNome() {
		return nome;
	}

	private void setNome(String nome) {
		notNull(nome, "O nome não pode ser nulo");
		notBlank(nome, "O nome não pode estar em branco");

		this.nome = nome;
	}

	public Email getEmail() {
		return email;
	}

	private void setEmail(Email email) {
		notNull(email, "O e-mail não pode ser nulo");

		this.email = email;
	}

	public Senha getSenha() {
		return senha;
	}

	private void setSenha(Senha senha) {
		notNull(senha, "A senha não pode ser nula");

		this.senha = senha;
	}

	public PapelUsuario getPapel() {
		return papel;
	}

	public void setPapel(PapelUsuario papel) {
		notNull(papel, "O papel do usuário não pode ser nulo");
		this.papel = papel;
	}

	@Override
	public String toString() {
		return nome;
	}
}
