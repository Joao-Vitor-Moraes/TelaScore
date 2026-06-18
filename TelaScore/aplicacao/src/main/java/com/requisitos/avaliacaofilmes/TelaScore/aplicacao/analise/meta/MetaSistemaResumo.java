package com.requisitos.avaliacaofilmes.TelaScore.aplicacao.analise.meta;

import com.requisitos.avaliacaofilmes.TelaScore.dominio.analise.meta.MetaSistema;

public record MetaSistemaResumo(int id, String titulo, int quantidadeAlvo, int duracaoDias) {
    public static MetaSistemaResumo de(MetaSistema meta) {
        return new MetaSistemaResumo(
                meta.getId(), meta.getTitulo(), meta.getQuantidadeAlvo(), meta.getDuracaoDias());
    }
}
