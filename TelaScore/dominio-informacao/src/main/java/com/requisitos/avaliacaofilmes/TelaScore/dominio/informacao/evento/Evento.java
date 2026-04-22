package com.requisitos.avaliacaofilmes.TelaScore.dominio.informacao.evento;

import static org.apache.commons.lang3.Validate.isTrue;
import static org.apache.commons.lang3.Validate.notBlank;
import static org.apache.commons.lang3.Validate.notNull;

import java.time.LocalDateTime;

import com.requisitos.avaliacaofilmes.TelaScore.dominio.identidade.usuario.UsuarioId;

public class Evento {
	private final EventoId id;
	private final UsuarioId criadorId;
	
	private String titulo;
	private String descricao;
	private LocalDateTime dataHora;

	public Evento(EventoId id, UsuarioId criadorId, String titulo, LocalDateTime dataHora) {
		notNull(id, "O id do evento não pode ser nulo");
		notNull(criadorId, "O id do criador não pode ser nulo");
		
		this.id = id;
		this.criadorId = criadorId;
	
		setTitulo(titulo);
		setDataHora(dataHora);
	}

	public EventoId getId() { return id; }
	public UsuarioId getCriadorId() { return criadorId; }

	public void setTitulo(String titulo) {
		notNull(titulo, "O título do evento não pode ser nulo");
		notBlank(titulo, "O título não pode estar em branco");
		this.titulo = titulo;
	}
	public String getTitulo() { return titulo; }

	public void setDescricao(String descricao) {
		this.descricao = descricao;
	}
	public String getDescricao() { return descricao; }

	public void setDataHora(LocalDateTime dataHora) {
		notNull(dataHora, "A data e hora do evento não podem ser nulas");
		
		isTrue(dataHora.isAfter(LocalDateTime.now()), "A data e hora do evento devem ser no futuro");
		
		this.dataHora = dataHora;
	}
	public LocalDateTime getDataHora() { return dataHora; }
}