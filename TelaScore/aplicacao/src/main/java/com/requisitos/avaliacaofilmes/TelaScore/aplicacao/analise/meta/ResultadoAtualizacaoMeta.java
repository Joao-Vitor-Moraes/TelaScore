package com.requisitos.avaliacaofilmes.TelaScore.aplicacao.analise.meta;

public record ResultadoAtualizacaoMeta(
        int metaId,
        int quantidadeAtual,
        int quantidadeAlvo,
        String status,
        String mensagem,
        int pontosGanhos,
        int totalPontos) {
}
