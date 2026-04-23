package com.requisitos.avaliacaofilmes.TelaScore.dominio.social.conexao;

import static org.apache.commons.lang3.Validate.isTrue;
import static org.apache.commons.lang3.Validate.notNull;

import java.time.LocalDateTime;

import com.requisitos.avaliacaofilmes.TelaScore.dominio.identidade.usuario.UsuarioId;

public class Conexao {
	private final ConexaoId id;
	private final UsuarioId seguidorId;
	private final UsuarioId seguidoId;
	
	private final LocalDateTime dataCriacao;

	public Conexao(ConexaoId id, UsuarioId seguidorId, UsuarioId seguidoId) {
		notNull(id, "O id da conexão não pode ser nulo");
		notNull(seguidorId, "O id do seguidor não pode ser nulo");
		notNull(seguidoId, "O id do seguido não pode ser nulo");
		
		isTrue(!seguidorId.equals(seguidoId), "Um utilizador não pode seguir a si próprio");
		
		this.id = id;
		this.seguidorId = seguidorId;
		this.seguidoId = seguidoId;
		this.dataCriacao = LocalDateTime.now();
	}

	public ConexaoId getId() { return id; }
	public UsuarioId getSeguidorId() { return seguidorId; }
	public UsuarioId getSeguidoId() { return seguidoId; }
	public LocalDateTime getDataCriacao() { return dataCriacao; }
}