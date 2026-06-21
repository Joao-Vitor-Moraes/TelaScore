package com.requisitos.avaliacaofilmes.TelaScore.aplicacao.catalogo;

import java.util.List;

public record AtualizarFilmeComando(
        String filmeId,
        String titulo,       
        String sinopse,      
        Integer anoLancamento,
        List<String> generos
) {}
