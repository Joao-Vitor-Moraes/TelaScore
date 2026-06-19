package com.requisitos.avaliacaofilmes.TelaScore.dominio.social.comunidade;

import com.requisitos.avaliacaofilmes.TelaScore.dominio.identidade.usuario.UsuarioId;

public class MembroComunidade {
	private final ComunidadeId comunidadeId;
	private final UsuarioId usuarioId;
	private final String usuarioApelido;
	private PapelComunidade papel;

	public MembroComunidade(ComunidadeId comunidadeId, UsuarioId usuarioId, PapelComunidade papel) {
		this.comunidadeId = comunidadeId;
		this.usuarioId = usuarioId;
		this.usuarioApelido = null;
		this.papel = papel;
	}

	public MembroComunidade(ComunidadeId comunidadId, UsuarioId usuarioId, String usuarioApelido, PapelComunidade papel) {
		this.comunidadeId = comunidadId;
		this.usuarioId = usuarioId;
		this.usuarioApelido = usuarioApelido;
		this.papel = papel;
	}

	public ComunidadeId getComunidadeId() { return comunidadeId; }
	public UsuarioId getUsuarioId() { return usuarioId; }
	public String getUsuarioApelido() { return usuarioApelido; }
	public PapelComunidade getPapel() { return papel; }
	public void setPapel(PapelComunidade papel) { this.papel = papel; }
}