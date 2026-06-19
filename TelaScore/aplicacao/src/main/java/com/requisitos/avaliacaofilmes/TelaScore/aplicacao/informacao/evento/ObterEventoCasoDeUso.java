package com.requisitos.avaliacaofilmes.TelaScore.aplicacao.informacao.evento;

import com.requisitos.avaliacaofilmes.TelaScore.dominio.informacao.evento.Evento;
import com.requisitos.avaliacaofilmes.TelaScore.dominio.informacao.evento.EventoId;
import com.requisitos.avaliacaofilmes.TelaScore.dominio.informacao.evento.EventoRepositorio;

public class ObterEventoCasoDeUso {

    private final EventoRepositorio repositorio;

    public ObterEventoCasoDeUso(EventoRepositorio repositorio) {
        this.repositorio = repositorio;
    }

    public EventoResumo executar(int eventoId) {
        Evento evento = repositorio.obter(new EventoId(eventoId));
        if (evento == null) {
            return null;
        }

        return new EventoResumo(
                evento.getId().getId(),
                evento.getCriadorId().getId(),
                evento.getTitulo(),
                evento.getDescricao(),
                evento.getDataHora());
    }
}
