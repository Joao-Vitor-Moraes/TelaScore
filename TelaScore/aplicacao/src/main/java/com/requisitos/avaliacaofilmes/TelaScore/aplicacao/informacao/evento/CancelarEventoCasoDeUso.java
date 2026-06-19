package com.requisitos.avaliacaofilmes.TelaScore.aplicacao.informacao.evento;

import com.requisitos.avaliacaofilmes.TelaScore.dominio.informacao.evento.EventoId;
import com.requisitos.avaliacaofilmes.TelaScore.dominio.informacao.evento.EventoServico;

public class CancelarEventoCasoDeUso {

    private final EventoServico servico;

    public CancelarEventoCasoDeUso(EventoServico servico) {
        this.servico = servico;
    }

    public void executar(int eventoId) {
        servico.cancelarEvento(new EventoId(eventoId));
    }
}
