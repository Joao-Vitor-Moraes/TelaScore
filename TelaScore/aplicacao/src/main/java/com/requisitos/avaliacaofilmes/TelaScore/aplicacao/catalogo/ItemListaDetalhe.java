package com.requisitos.avaliacaofilmes.TelaScore.aplicacao.catalogo;

import java.time.LocalDate;

public record ItemListaDetalhe(
        int filmeId,
        String titulo,
        String imagemUrl,
        Integer posicao,
        LocalDate dataAdicao
) {}
