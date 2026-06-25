package com.requisitos.avaliacaofilmes.TelaScore.apresentacao.informacao.noticia;

import com.requisitos.avaliacaofilmes.TelaScore.aplicacao.informacao.noticia.AdicionarNoticiaCasoDeUso;
import com.requisitos.avaliacaofilmes.TelaScore.aplicacao.informacao.noticia.PesquisarNoticiasCasoDeUso;
import com.requisitos.avaliacaofilmes.TelaScore.aplicacao.informacao.noticia.RemoverNoticiaCasoDeUso;
import com.requisitos.avaliacaofilmes.TelaScore.aplicacao.identidade.GeradorId;
import com.requisitos.avaliacaofilmes.TelaScore.dominio.informacao.noticia.NoticiaRepositorio;
import com.requisitos.avaliacaofilmes.TelaScore.dominio.informacao.noticia.NoticiaServico;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class InformacaoConfig {

    @Bean
    public NoticiaServico noticiaServico(NoticiaRepositorio noticiaRepositorio) {
        return new NoticiaServico(noticiaRepositorio);
    }

    @Bean
    public AdicionarNoticiaCasoDeUso adicionarNoticiaCasoDeUso(NoticiaServico noticiaServico, GeradorId geradorId) {
        return new AdicionarNoticiaCasoDeUso(noticiaServico, geradorId);
    }

    @Bean
    public PesquisarNoticiasCasoDeUso pesquisarNoticiasCasoDeUso(NoticiaRepositorio noticiaRepositorio) {
        return new PesquisarNoticiasCasoDeUso(noticiaRepositorio);
    }

    @Bean
    public RemoverNoticiaCasoDeUso removerNoticiaCasoDeUso(NoticiaServico noticiaServico) {
        return new RemoverNoticiaCasoDeUso(noticiaServico);
    }
}
