package com.requisitos.avaliacaofilmes.TelaScore.aplicacao.informacao.evento;

import com.requisitos.avaliacaofilmes.TelaScore.dominio.identidade.usuario.UsuarioRepositorio;
import com.requisitos.avaliacaofilmes.TelaScore.dominio.informacao.evento.Evento;
import com.requisitos.avaliacaofilmes.TelaScore.dominio.informacao.evento.EventoId;
import com.requisitos.avaliacaofilmes.TelaScore.dominio.informacao.evento.EventoRepositorio;

public class ObterEventoCasoDeUso {

    private final EventoRepositorio repositorio;
    private final UsuarioRepositorio usuarioRepositorio;

    public ObterEventoCasoDeUso(EventoRepositorio repositorio, UsuarioRepositorio usuarioRepositorio) {
        this.repositorio = repositorio;
        this.usuarioRepositorio = usuarioRepositorio;
    }

    public EventoResumo executar(int eventoId, Integer usuarioId) {
        Evento evento = repositorio.obter(new EventoId(eventoId));
        if (evento == null) {
            return null;
        }
        return EventoResumoMapper.map(evento, usuarioId, usuarioRepositorio);
    }
}
