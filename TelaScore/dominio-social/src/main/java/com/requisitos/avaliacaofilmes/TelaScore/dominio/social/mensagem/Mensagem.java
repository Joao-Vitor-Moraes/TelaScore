package com.requisitos.avaliacaofilmes.TelaScore.dominio.social.mensagem;

import static org.apache.commons.lang3.Validate.isTrue;
import static org.apache.commons.lang3.Validate.notBlank;
import static org.apache.commons.lang3.Validate.notNull;

import java.time.LocalDateTime;

import com.requisitos.avaliacaofilmes.TelaScore.dominio.identidade.usuario.UsuarioId;

public class Mensagem {
	private final MensagemId id;
	private final UsuarioId remetenteId;
	private final UsuarioId destinatarioId;

	private String conteudo;
	private final LocalDateTime dataEnvio;
	private boolean lida;

	public Mensagem(MensagemId id, UsuarioId remetenteId, UsuarioId destinatarioId, String conteudo) {
		this(id, remetenteId, destinatarioId, conteudo, LocalDateTime.now(), false);
	}

	public Mensagem(MensagemId id, UsuarioId remetenteId, UsuarioId destinatarioId, String conteudo,
					LocalDateTime dataEnvio, boolean lida) {
		notNull(id, "O id da mensagem nao pode ser nulo");
		notNull(remetenteId, "O id do remetente nao pode ser nulo");
		notNull(destinatarioId, "O id do destinatario nao pode ser nulo");
		notNull(dataEnvio, "A data de envio da mensagem nao pode ser nula");

		isTrue(!remetenteId.equals(destinatarioId), "O remetente e o destinatário não podem ser a mesma pessoa");

		this.id = id;
		this.remetenteId = remetenteId;
		this.destinatarioId = destinatarioId;
		this.dataEnvio = dataEnvio;
		this.lida = lida;

		setConteudo(conteudo);
	}

	public MensagemId getId() { return id; }
	public UsuarioId getRemetenteId() { return remetenteId; }
	public UsuarioId getDestinatarioId() { return destinatarioId; }
	public LocalDateTime getDataEnvio() { return dataEnvio; }
	public boolean isLida() { return lida; }

	public void setConteudo(String conteudo) {
		notNull(conteudo, "O conteudo da mensagem nao pode ser nulo");
		notBlank(conteudo, "O conteudo da mensagem nao pode estar em branco");
		this.conteudo = conteudo;
	}

	public String getConteudo() { return conteudo; }

	public void marcarComoLida() {
		this.lida = true;
	}
}
