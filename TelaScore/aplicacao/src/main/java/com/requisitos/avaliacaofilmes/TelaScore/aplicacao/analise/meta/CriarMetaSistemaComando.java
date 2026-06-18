package com.requisitos.avaliacaofilmes.TelaScore.aplicacao.analise.meta;

public record CriarMetaSistemaComando(
        String titulo,
        int quantidadeAlvo,
        int duracaoDias,
        int adminId) {
}
