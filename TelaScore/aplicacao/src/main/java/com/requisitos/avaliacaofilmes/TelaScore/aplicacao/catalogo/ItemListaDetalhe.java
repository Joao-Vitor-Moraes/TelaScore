package com.requisitos.avaliacaofilmes.TelaScore.aplicacao.catalogo;

import java.time.LocalDate;

public record ItemListaDetalhe(
        String filmeId,
        Integer posicao,
        LocalDate dataAdicao
) {}
