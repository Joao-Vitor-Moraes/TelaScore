package com.requisitos.avaliacaofilmes.TelaScore.dominio.informacao.evento;

import java.time.LocalDateTime;
import java.util.List;

public interface EventoRepositorio {
	void salvar(Evento evento);
	Evento obter(EventoId id);
	void remover(EventoId id);
	
	List<Evento> buscarEventosFuturos(LocalDateTime aPartirDe);
}