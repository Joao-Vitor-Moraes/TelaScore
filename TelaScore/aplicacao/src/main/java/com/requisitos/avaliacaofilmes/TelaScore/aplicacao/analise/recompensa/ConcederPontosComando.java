package com.requisitos.avaliacaofilmes.TelaScore.aplicacao.analise.recompensa;

import com.requisitos.avaliacaofilmes.TelaScore.dominio.analise.recompensa.AcaoPontuada;

public record ConcederPontosComando(
		int usuarioId,
		AcaoPontuada acao,
		int quantidadePontos
) {}