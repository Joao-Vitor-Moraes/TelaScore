package com.requisitos.avaliacaofilmes.TelaScore.aplicacao.informacao.evento;

import com.requisitos.avaliacaofilmes.TelaScore.dominio.identidade.usuario.Usuario;
import com.requisitos.avaliacaofilmes.TelaScore.dominio.identidade.usuario.UsuarioRepositorio;
import com.requisitos.avaliacaofilmes.TelaScore.dominio.informacao.evento.Evento;
import com.requisitos.avaliacaofilmes.TelaScore.dominio.informacao.evento.RespostaEvento;

final class EventoResumoMapper {

    private EventoResumoMapper() {}

    static EventoResumo map(Evento evento, Integer usuarioId, UsuarioRepositorio usuarioRepositorio) {
        String criadorNome = "Usuário";
        Usuario criador = usuarioRepositorio.obter(evento.getCriadorId());
        if (criador != null) {
            criadorNome = criador.getNome();
        }

        String minhaResposta = null;
        if (usuarioId != null) {
            RespostaEvento resposta = evento.getRespostas().get(usuarioId);
            if (resposta != null) {
                minhaResposta = resposta.name();
            }
        }

        return new EventoResumo(
                evento.getId().getId(),
                evento.getCriadorId().getId(),
                criadorNome,
                evento.getTitulo(),
                evento.getDescricao(),
                evento.getDataHora(),
                evento.getVisibilidade().name(),
                evento.totalConfirmados(),
                evento.totalRecusados(),
                minhaResposta);
    }
}
