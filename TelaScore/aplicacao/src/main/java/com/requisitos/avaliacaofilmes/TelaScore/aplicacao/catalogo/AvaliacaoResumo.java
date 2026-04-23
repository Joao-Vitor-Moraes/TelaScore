package com.requisitos.avaliacaofilmes.TelaScore.aplicacao.catalogo;

import java.time.LocalDate;

public record AvaliacaoResumo(
    int avaliacaoId,
    int usuarioId,
    int valorNota,
    String resenha,
    LocalDate dataAvaliacao
) {}
