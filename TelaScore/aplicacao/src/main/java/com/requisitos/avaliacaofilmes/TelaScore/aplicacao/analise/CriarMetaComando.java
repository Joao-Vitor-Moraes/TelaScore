package com.requisitos.avaliacaofilmes.TelaScore.aplicacao.analise;

import java.time.LocalDate;

public record CriarMetaComando(
		int usuarioId,
		String titulo,
		int quantidadeAlvo,
		LocalDate dataPrazo
) {}