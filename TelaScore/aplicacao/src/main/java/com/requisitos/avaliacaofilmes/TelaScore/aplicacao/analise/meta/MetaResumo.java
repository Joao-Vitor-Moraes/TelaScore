package com.requisitos.avaliacaofilmes.TelaScore.aplicacao.analise.meta;

import java.time.LocalDate;

import com.requisitos.avaliacaofilmes.TelaScore.dominio.analise.meta.Meta;

public record MetaResumo(
        int id,
        int usuarioId,
        String titulo,
        int quantidadeAlvo,
        int quantidadeAtual,
        LocalDate dataPrazo,
        String status) {

    public static MetaResumo de(Meta meta) {
        return new MetaResumo(
                meta.getId().getId(),
                meta.getUsuarioId().getId(),
                meta.getTitulo(),
                meta.getQuantidadeAlvo(),
                meta.getQuantidadeAtual(),
                meta.getDataPrazo(),
                meta.getStatus().name());
    }
}
