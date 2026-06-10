package com.requisitos.avaliacaofilmes.TelaScore.aplicacao.catalogo;

public record EditarSolicitacaoComando(
        int solicitacaoId,
        int solicitanteId,
        String tituloSugerido,
        String justificativa,
        String pais,
        Integer ano,
        String genero,
        String fotoUrl
) {}
