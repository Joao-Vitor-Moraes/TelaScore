package com.requisitos.avaliacaofilmes.TelaScore.aplicacao.informacao.evento;

import java.time.LocalDateTime;
import java.util.List;

public record AgendarEventoComando(
        int criadorId,
        String titulo,
        String descricao,
        LocalDateTime dataHora,
        String visibilidade,
        List<Integer> comunidadesConvidadas,
        List<Integer> convidados) {}
