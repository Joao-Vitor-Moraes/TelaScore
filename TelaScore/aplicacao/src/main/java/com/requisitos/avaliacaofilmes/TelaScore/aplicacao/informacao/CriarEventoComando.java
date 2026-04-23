package com.requisitos.avaliacaofilmes.TelaScore.aplicacao.informacao;

import java.time.LocalDateTime;

public record CriarEventoComando(
		int criadorId,
		String titulo,
		String descricao,
		LocalDateTime dataHora
) {}