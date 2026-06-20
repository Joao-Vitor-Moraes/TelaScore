package com.requisitos.avaliacaofilmes.TelaScore.aplicacao.informacao.evento;

import com.requisitos.avaliacaofilmes.TelaScore.dominio.identidade.usuario.UsuarioId;
import com.requisitos.avaliacaofilmes.TelaScore.dominio.informacao.evento.Evento;
import com.requisitos.avaliacaofilmes.TelaScore.dominio.informacao.evento.EventoId;
import com.requisitos.avaliacaofilmes.TelaScore.dominio.informacao.evento.EventoRepositorio;
import com.requisitos.avaliacaofilmes.TelaScore.dominio.informacao.evento.RespostaEvento;

public class ResponderEventoCasoDeUso {

    private final EventoRepositorio repositorio;

    public ResponderEventoCasoDeUso(EventoRepositorio repositorio) {
        this.repositorio = repositorio;
    }

    public void executar(int eventoId, int usuarioId, String resposta) {
        Evento evento = repositorio.obter(new EventoId(eventoId));
        if (evento == null) {
            throw new IllegalArgumentException("Evento não encontrado: " + eventoId);
        }

        evento.responder(new UsuarioId(usuarioId), RespostaEvento.valueOf(resposta.toUpperCase()));
        repositorio.salvar(evento);
    }
}
