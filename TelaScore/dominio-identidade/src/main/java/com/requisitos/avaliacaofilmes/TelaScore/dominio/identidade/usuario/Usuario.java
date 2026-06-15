package com.requisitos.avaliacaofilmes.TelaScore.dominio.identidade.usuario;

import static org.apache.commons.lang3.Validate.notBlank;
import static org.apache.commons.lang3.Validate.notNull;

public class Usuario {
	private final UsuarioId id;

	private String nome;
	private Email email;
	private Senha senha;
	private PapelUsuario papel;
	private Apelido apelido;
	private String biografia;
	private String avatarUrl;

	public Usuario(UsuarioId id, String nome, Email email, Senha senha, PapelUsuario papel) {
		this(id, nome, email, senha, papel, new Apelido(apelidoPadrao(nome)), null, null);
	}

	public Usuario(UsuarioId id, String nome, Email email, Senha senha, PapelUsuario papel,
			Apelido apelido, String biografia, String avatarUrl) {
		notNull(id, "O id do usuario nao pode ser nulo");
		notNull(papel, "O papel do usuario nao pode ser nulo");
		this.id = id;

		setNome(nome);
		setEmail(email);
		setSenha(senha);
		this.papel = papel;
		setApelido(apelido);
		setBiografia(biografia);
		setAvatarUrl(avatarUrl);
	}

	public UsuarioId getId() {
		return id;
	}

	public String getNome() {
		return nome;
	}

	private void setNome(String nome) {
		notNull(nome, "O nome nao pode ser nulo");
		notBlank(nome, "O nome nao pode estar em branco");

		this.nome = nome;
	}

	public Email getEmail() {
		return email;
	}

	private void setEmail(Email email) {
		notNull(email, "O e-mail nao pode ser nulo");

		this.email = email;
	}

	public Senha getSenha() {
		return senha;
	}

	private void setSenha(Senha senha) {
		notNull(senha, "A senha nao pode ser nula");

		this.senha = senha;
	}

	public PapelUsuario getPapel() {
		return papel;
	}

	public void setPapel(PapelUsuario papel) {
		notNull(papel, "O papel do usuario nao pode ser nulo");
		this.papel = papel;
	}

	public Apelido getApelido() {
		return apelido;
	}

	public void setApelido(Apelido apelido) {
		notNull(apelido, "O apelido nao pode ser nulo");
		this.apelido = apelido;
	}

	public String getBiografia() {
		return biografia;
	}

	public void setBiografia(String biografia) {
		this.biografia = biografia;
	}

	public String getAvatarUrl() {
		return avatarUrl;
	}

	public void setAvatarUrl(String avatarUrl) {
		this.avatarUrl = avatarUrl;
	}

	@Override
	public String toString() {
		return nome;
	}

	private static String apelidoPadrao(String nome) {
		String valor = nome == null ? "usuario" : nome.trim();
		if (valor.isBlank()) {
			valor = "usuario";
		}
		if (valor.length() < 3) {
			valor = (valor + "usr").substring(0, 3);
		}
		return valor.length() > 20 ? valor.substring(0, 20) : valor;
	}
}
