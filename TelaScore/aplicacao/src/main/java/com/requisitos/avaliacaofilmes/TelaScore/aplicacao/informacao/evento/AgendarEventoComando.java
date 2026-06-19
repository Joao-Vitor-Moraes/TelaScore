package com.requisitos.avaliacaofilmes.TelaScore.aplicacao.informacao.evento;

import java.time.LocalDateTime;

public record AgendarEventoComando(int criadorId, String titulo, String descricao, LocalDateTime dataHora) {}
