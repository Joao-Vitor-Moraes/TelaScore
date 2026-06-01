package com.requisitos.avaliacaofilmes.TelaScore.infraestrutura.config;

import com.requisitos.avaliacaofilmes.TelaScore.aplicacao.identidade.GeradorId;
import com.requisitos.avaliacaofilmes.TelaScore.dominio.catalogo.filme.FilmeRepositorio;
import com.requisitos.avaliacaofilmes.TelaScore.dominio.catalogo.lista.ListaRepositorio;
import com.requisitos.avaliacaofilmes.TelaScore.dominio.catalogo.lista.ListaServico;
import com.requisitos.avaliacaofilmes.TelaScore.dominio.catalogo.solicitacao.SolicitacaoRepositorio;
import com.requisitos.avaliacaofilmes.TelaScore.dominio.catalogo.solicitacao.SolicitacaoServico;
import com.requisitos.avaliacaofilmes.TelaScore.infraestrutura.catalogo.filme.FilmeRepositorioImpl;
import com.requisitos.avaliacaofilmes.TelaScore.infraestrutura.catalogo.lista.ListaRepositorioImpl;
import com.requisitos.avaliacaofilmes.TelaScore.infraestrutura.catalogo.solicitacao.SolicitacaoRepositorioImpl;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class InfraestruturaConfig {

    @Bean
    public GeradorId geradorId() {
        return new GeradorIdImpl();
    }

    @Bean
    public ListaRepositorio listaRepositorio() {
        return new ListaRepositorioImpl();
    }

    @Bean
    public SolicitacaoRepositorio solicitacaoRepositorio() {
        return new SolicitacaoRepositorioImpl();
    }

    @Bean
    public FilmeRepositorio filmeRepositorio() {
        return new FilmeRepositorioImpl();
    }

    @Bean
    public ListaServico listaServico(ListaRepositorio listaRepositorio) {
        return new ListaServico(listaRepositorio);
    }

    @Bean
    public SolicitacaoServico solicitacaoServico(SolicitacaoRepositorio solicitacaoRepositorio,
            FilmeRepositorio filmeRepositorio) {
        return new SolicitacaoServico(solicitacaoRepositorio, filmeRepositorio);
    }
}
