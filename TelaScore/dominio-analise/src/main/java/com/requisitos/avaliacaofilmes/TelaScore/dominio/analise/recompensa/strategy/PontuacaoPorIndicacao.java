package com.requisitos.avaliacaofilmes.TelaScore.dominio.analise.recompensa.strategy;

import com.requisitos.avaliacaofilmes.TelaScore.dominio.analise.recompensa.AcaoPontuada;
import com.requisitos.avaliacaofilmes.TelaScore.dominio.analise.recompensa.EstrategiaPontuacao;
import com.requisitos.avaliacaofilmes.TelaScore.dominio.analise.recompensa.Pontos;

public class PontuacaoPorIndicacao implements EstrategiaPontuacao {
    @Override
    public Pontos calcular(AcaoPontuada acao) {
        return new Pontos(300);
    }
}