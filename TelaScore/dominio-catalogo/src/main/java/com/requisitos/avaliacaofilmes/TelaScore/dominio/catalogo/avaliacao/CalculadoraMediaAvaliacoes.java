package com.requisitos.avaliacaofilmes.TelaScore.dominio.catalogo.avaliacao;

import java.util.List;

public class CalculadoraMediaAvaliacoes {

    public double calcular(List<Avaliacao> avaliacoes) {
        if (avaliacoes == null || avaliacoes.isEmpty()) {
            return 0.0;
        }
        return avaliacoes.stream()
                .mapToInt(a -> a.getNota().getValor())
                .average()
                .orElse(0.0);
    }
}
