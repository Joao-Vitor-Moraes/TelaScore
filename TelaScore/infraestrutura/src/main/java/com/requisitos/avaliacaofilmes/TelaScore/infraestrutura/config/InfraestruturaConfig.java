package com.requisitos.avaliacaofilmes.TelaScore.infraestrutura.config;

import com.requisitos.avaliacaofilmes.TelaScore.aplicacao.identidade.GeradorId;
import com.requisitos.avaliacaofilmes.TelaScore.dominio.catalogo.filme.FilmeRepositorio;
import com.requisitos.avaliacaofilmes.TelaScore.dominio.catalogo.lista.ListaRepositorio;
import com.requisitos.avaliacaofilmes.TelaScore.dominio.catalogo.lista.ListaServico;
import com.requisitos.avaliacaofilmes.TelaScore.dominio.catalogo.solicitacao.SolicitacaoRepositorio;
import com.requisitos.avaliacaofilmes.TelaScore.dominio.catalogo.solicitacao.SolicitacaoServico;
import com.requisitos.avaliacaofilmes.TelaScore.dominio.identidade.perfil.PerfilRepositorio;
import com.requisitos.avaliacaofilmes.TelaScore.dominio.identidade.usuario.UsuarioRepositorio;
import com.requisitos.avaliacaofilmes.TelaScore.infraestrutura.catalogo.filme.FilmeRepositorioImpl;
import com.requisitos.avaliacaofilmes.TelaScore.infraestrutura.catalogo.lista.ListaRepositorioImpl;
import com.requisitos.avaliacaofilmes.TelaScore.infraestrutura.catalogo.solicitacao.SolicitacaoRepositorioImpl;
import com.requisitos.avaliacaofilmes.TelaScore.infraestrutura.identidade.perfil.PerfilRepositorioImpl;
import com.requisitos.avaliacaofilmes.TelaScore.infraestrutura.identidade.usuario.UsuarioRepositorioImpl;

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
    public UsuarioRepositorio usuarioRepositorio() {
        return new UsuarioRepositorioImpl();
    }

    @Bean
    public PerfilRepositorio perfilRepositorio() {
        return new PerfilRepositorioImpl();
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
