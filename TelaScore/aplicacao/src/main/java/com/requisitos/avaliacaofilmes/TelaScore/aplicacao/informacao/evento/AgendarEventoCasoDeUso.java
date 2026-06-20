package com.requisitos.avaliacaofilmes.TelaScore.aplicacao.informacao.evento;

import com.requisitos.avaliacaofilmes.TelaScore.aplicacao.identidade.GeradorId;
import com.requisitos.avaliacaofilmes.TelaScore.dominio.identidade.usuario.UsuarioId;
import com.requisitos.avaliacaofilmes.TelaScore.dominio.informacao.evento.Evento;
import com.requisitos.avaliacaofilmes.TelaScore.dominio.informacao.evento.EventoId;
import com.requisitos.avaliacaofilmes.TelaScore.dominio.informacao.evento.EventoServico;
import com.requisitos.avaliacaofilmes.TelaScore.dominio.informacao.evento.Visibilidade;

public class AgendarEventoCasoDeUso {

    private final EventoServico servico;
    private final GeradorId geradorId;

    public AgendarEventoCasoDeUso(EventoServico servico, GeradorId geradorId) {
        this.servico = servico;
        this.geradorId = geradorId;
    }

    public int executar(AgendarEventoComando comando) {
        EventoId novoId = new EventoId(geradorId.gerarProximoIdEvento());
        UsuarioId criadorId = new UsuarioId(comando.criadorId());

        Evento evento = new Evento(novoId, criadorId, comando.titulo(), comando.dataHora());
        evento.setDescricao(comando.descricao());

        if (comando.visibilidade() != null && !comando.visibilidade().isBlank()) {
            evento.setVisibilidade(Visibilidade.valueOf(comando.visibilidade().toUpperCase()));
        }
        if (comando.comunidadesConvidadas() != null) {
            comando.comunidadesConvidadas().forEach(evento::convidarComunidade);
        }
        if (comando.convidados() != null) {
            comando.convidados().forEach(evento::convidarUsuario);
        }

        servico.agendarEvento(evento);
        return novoId.getId();
    }
}
