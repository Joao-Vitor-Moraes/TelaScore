package com.requisitos.avaliacaofilmes.TelaScore.dominio.identidade.perfil;

import static org.apache.commons.lang3.Validate.notNull;

import com.requisitos.avaliacaofilmes.TelaScore.dominio.identidade.usuario.UsuarioId;

public class Perfil {
	private final PerfilId id;
	private final UsuarioId usuarioId;

	private Apelido apelido;
	private String biografia;
	private String avatarUrl;

	public Perfil(PerfilId id, UsuarioId usuarioId, Apelido apelido) {
		notNull(id, "O id do perfil não pode ser nulo");
		notNull(usuarioId, "O id do usuário não pode ser nulo");
		this.id = id;
		this.usuarioId = usuarioId;
		
		setApelido(apelido);
	}

	public PerfilId getId() { return id; }
	public UsuarioId getUsuarioId() { return usuarioId; }

	public void setApelido(Apelido apelido) {
		notNull(apelido, "O apelido não pode ser nulo");
		this.apelido = apelido;
	}

	public Apelido getApelido() { return apelido; }

	public void setBiografia(String biografia) {
		this.biografia = biografia;
	}

	public String getBiografia() { return biografia; }

	public void setAvatarUrl(String avatarUrl) {
		this.avatarUrl = avatarUrl;
	}

	public String getAvatarUrl() { return avatarUrl; }
}