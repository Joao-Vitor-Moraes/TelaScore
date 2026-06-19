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
        String status,
        String statusDescricao,
        boolean metaDoSistema,
        String tipo) {

    public static MetaResumo de(Meta meta) {
        return new MetaResumo(
                meta.getId().getId(),
                meta.getUsuarioId().getId(),
                meta.getTitulo(),
                meta.getQuantidadeAlvo(),
                meta.getQuantidadeAtual(),
                meta.getDataPrazo(),
                meta.getStatus().name(),
                formatarStatus(meta.getStatus().name()),
                meta.getMetaSistemaId() != null,
                meta.getTipo().name());
    }

    private static String formatarStatus(String status) {
        return switch (status) {
            case "EM_ANDAMENTO" -> "Em andamento";
            case "CONCLUIDA" -> "Concluída";
            case "FALHADA" -> "Não concluída";
            case "CANCELADA" -> "Cancelada";
            default -> status;
        };
    }
}
