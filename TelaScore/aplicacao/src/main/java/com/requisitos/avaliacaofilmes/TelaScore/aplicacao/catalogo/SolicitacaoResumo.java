package com.requisitos.avaliacaofilmes.TelaScore.aplicacao.catalogo;

import java.time.LocalDateTime;

public record SolicitacaoResumo(
		int id,
		int solicitanteId,
		String tituloSugerido,
		String justificativa,
		String pais,
		Integer ano,
		String fotoUrl,
		String status,
		LocalDateTime dataCriacao,
		String feedbackAdmin
) {}
