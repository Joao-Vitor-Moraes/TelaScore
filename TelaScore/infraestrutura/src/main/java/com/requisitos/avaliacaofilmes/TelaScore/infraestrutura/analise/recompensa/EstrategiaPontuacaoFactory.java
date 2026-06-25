package com.requisitos.avaliacaofilmes.TelaScore.infraestrutura.analise.recompensa;

import org.springframework.stereotype.Component;

import com.requisitos.avaliacaofilmes.TelaScore.dominio.analise.recompensa.AcaoPontuada;
import com.requisitos.avaliacaofilmes.TelaScore.dominio.analise.recompensa.EstrategiaPontuacao;
import com.requisitos.avaliacaofilmes.TelaScore.dominio.analise.recompensa.EstrategiaPontuacaoProvider;
import com.requisitos.avaliacaofilmes.TelaScore.dominio.analise.recompensa.strategy.PontuacaoPorAtividade;

@Component
public class EstrategiaPontuacaoFactory implements EstrategiaPontuacaoProvider {

    private final EstrategiaPontuacao estrategia = new PontuacaoPorAtividade();

    @Override
    public EstrategiaPontuacao obter(AcaoPontuada acao) {
        return estrategia;
    }
}
