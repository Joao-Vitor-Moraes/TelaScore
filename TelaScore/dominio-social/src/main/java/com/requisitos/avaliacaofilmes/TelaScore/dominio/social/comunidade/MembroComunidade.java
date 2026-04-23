package com.requisitos.avaliacaofilmes.TelaScore.dominio.social.comunidade;

import static org.apache.commons.lang3.Validate.notNull;
import java.time.LocalDateTime;

import com.requisitos.avaliacaofilmes.TelaScore.dominio.identidade.usuario.UsuarioId;

public class MembroComunidade {
	private final ComunidadeId comunidadeId;
	private final UsuarioId usuarioId;
	
	private PapelComunidade papel;
	private final LocalDateTime dataEntrada;

	public MembroComunidade(ComunidadeId comunidadeId, UsuarioId usuarioId, PapelComunidade papel) {
		notNull(comunidadeId, "O id da comunidade não pode ser nulo");
		notNull(usuarioId, "O id do utilizador não pode ser nulo");
		notNull(papel, "O papel na comunidade não pode ser nulo");
		
		this.comunidadeId = comunidadeId;
		this.usuarioId = usuarioId;
		this.papel = papel;
		this.dataEntrada = LocalDateTime.now();
	}

	public ComunidadeId getComunidadeId() { return comunidadeId; }
	public UsuarioId getUsuarioId() { return usuarioId; }
	public LocalDateTime getDataEntrada() { return dataEntrada; }
	public PapelComunidade getPapel() { return papel; }

	public void promoverAModerador() {
		this.papel = PapelComunidade.MODERADOR;
	}
	
	public void rebaixarAMembro() {
		if (this.papel == PapelComunidade.CRIADOR) {
			throw new IllegalStateException("O criador da comunidade não pode ser rebaixado.");
		}
		this.papel = PapelComunidade.MEMBRO;
	}
}