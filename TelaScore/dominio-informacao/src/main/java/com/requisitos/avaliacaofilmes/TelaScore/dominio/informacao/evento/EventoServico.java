package com.requisitos.avaliacaofilmes.TelaScore.dominio.informacao.evento;

import static org.apache.commons.lang3.Validate.notNull;

public class EventoServico {
	private final EventoRepositorio repositorio;

	public EventoServico(EventoRepositorio repositorio) {
		notNull(repositorio, "O repositório de eventos não pode ser nulo");
		this.repositorio = repositorio;
	}

	public void agendarEvento(Evento evento) {
		notNull(evento, "O evento não pode ser nulo");
		
		repositorio.salvar(evento);
	}
	
	public void cancelarEvento(EventoId id) {
		notNull(id, "O id do evento não pode ser nulo");
		repositorio.remover(id);
	}
}