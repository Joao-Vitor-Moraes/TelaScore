package com.requisitos.avaliacaofilmes.TelaScore.apresentacao.analise;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import com.requisitos.avaliacaofilmes.TelaScore.dominio.analise.meta.MetaRepositorio;
import com.requisitos.avaliacaofilmes.TelaScore.aplicacao.analise.meta.CriarMetaCasoDeUso;
import com.requisitos.avaliacaofilmes.TelaScore.aplicacao.analise.meta.AdicionarProgressoMetaCasoDeUso;
import com.requisitos.avaliacaofilmes.TelaScore.aplicacao.analise.meta.RemoverProgressoMetaCasoDeUso;
import com.requisitos.avaliacaofilmes.TelaScore.aplicacao.analise.meta.EstenderPrazoMetaCasoDeUso;

import com.requisitos.avaliacaofilmes.TelaScore.dominio.analise.recomendacao.RecomendacaoRepositorio;
import com.requisitos.avaliacaofilmes.TelaScore.dominio.analise.recomendacao.RecomendacaoServico;
import com.requisitos.avaliacaofilmes.TelaScore.aplicacao.analise.recomendacao.EnviarRecomendacaoCasoDeUso;
import com.requisitos.avaliacaofilmes.TelaScore.aplicacao.analise.recomendacao.ResponderRecomendacaoCasoDeUso;

import com.requisitos.avaliacaofilmes.TelaScore.aplicacao.identidade.GeradorId;

@Configuration
public class AnaliseConfig {

    @Bean
    public CriarMetaCasoDeUso criarMetaCasoDeUso(MetaRepositorio metaRepositorio, GeradorId geradorId) {
        return new CriarMetaCasoDeUso(metaRepositorio, geradorId);
    }

    @Bean
    public AdicionarProgressoMetaCasoDeUso adicionarProgressoMetaCasoDeUso(MetaRepositorio metaRepositorio) {
        return new AdicionarProgressoMetaCasoDeUso(metaRepositorio);
    }

    @Bean
    public RemoverProgressoMetaCasoDeUso removerProgressoMetaCasoDeUso(MetaRepositorio metaRepositorio) {
        return new RemoverProgressoMetaCasoDeUso(metaRepositorio);
    }

    @Bean
    public EstenderPrazoMetaCasoDeUso estenderPrazoMetaCasoDeUso(MetaRepositorio metaRepositorio) {
        return new EstenderPrazoMetaCasoDeUso(metaRepositorio);
    }

    @Bean
    public EnviarRecomendacaoCasoDeUso enviarRecomendacaoCasoDeUso(RecomendacaoServico recomendacaoServico) {
        return new EnviarRecomendacaoCasoDeUso(recomendacaoServico);
    }
    @Bean
    public ResponderRecomendacaoCasoDeUso responderRecomendacaoCasoDeUso(RecomendacaoRepositorio recomendacaoRepositorio) {
        return new ResponderRecomendacaoCasoDeUso(recomendacaoRepositorio);
    }
}