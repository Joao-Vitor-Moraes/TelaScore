package com.requisitos.avaliacaofilmes.TelaScore.aplicacao.informacao.evento;

import java.time.LocalDateTime;

public record EventoResumo(
        int id,
        int criadorId,
        String criadorNome,
        String titulo,
        String descricao,
        LocalDateTime dataHora,
        String visibilidade,
        long confirmados,
        long recusados,
        String minhaResposta) {}
