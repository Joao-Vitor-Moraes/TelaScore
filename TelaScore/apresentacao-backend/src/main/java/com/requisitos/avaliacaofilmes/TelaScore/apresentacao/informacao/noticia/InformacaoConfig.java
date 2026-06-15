package com.requisitos.avaliacaofilmes.TelaScore.apresentacao.informacao.noticia;

import com.requisitos.avaliacaofilmes.TelaScore.aplicacao.informacao.noticia.AdicionarNoticiaCasoDeUso;
import com.requisitos.avaliacaofilmes.TelaScore.aplicacao.informacao.noticia.PesquisarNoticiasCasoDeUso;
import com.requisitos.avaliacaofilmes.TelaScore.aplicacao.informacao.noticia.RemoverNoticiaCasoDeUso;
import com.requisitos.avaliacaofilmes.TelaScore.dominio.informacao.noticia.NoticiaRepositorio;
import com.requisitos.avaliacaofilmes.TelaScore.dominio.informacao.noticia.NoticiaServico;
import com.requisitos.avaliacaofilmes.TelaScore.infraestrutura.informacao.noticia.NoticiaRepositorioImpl;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class InformacaoConfig {

    @Bean
    public NoticiaServico noticiaServico(NoticiaRepositorio noticiaRepositorio) {
        return new NoticiaServico(noticiaRepositorio);
    }

    @Bean
    public AdicionarNoticiaCasoDeUso adicionarNoticiaCasoDeUso(NoticiaServico noticiaServico) {
        return new AdicionarNoticiaCasoDeUso(noticiaServico);
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