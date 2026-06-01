package com.requisitos.avaliacaofilmes.TelaScore.apresentacao.catalogo;

import com.requisitos.avaliacaofilmes.TelaScore.aplicacao.catalogo.*;
import com.requisitos.avaliacaofilmes.TelaScore.aplicacao.identidade.GeradorId;
import com.requisitos.avaliacaofilmes.TelaScore.dominio.catalogo.filme.FilmeRepositorio;
import com.requisitos.avaliacaofilmes.TelaScore.dominio.catalogo.lista.ListaRepositorio;
import com.requisitos.avaliacaofilmes.TelaScore.dominio.catalogo.lista.ListaServico;
import com.requisitos.avaliacaofilmes.TelaScore.dominio.catalogo.solicitacao.SolicitacaoRepositorio;
import com.requisitos.avaliacaofilmes.TelaScore.dominio.catalogo.solicitacao.SolicitacaoServico;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class CatalogoConfig {

    @Bean
    public CriarListaCasoDeUso criarListaCasoDeUso(ListaServico listaServico, GeradorId geradorId) {
        return new CriarListaCasoDeUso(listaServico, geradorId);
    }

    @Bean
    public ConsultarItensListaCasoDeUso consultarItensListaCasoDeUso(ListaRepositorio listaRepositorio) {
        return new ConsultarItensListaCasoDeUso(listaRepositorio);
    }

    @Bean
    public AdicionarFilmeNaListaCasoDeUso adicionarFilmeNaListaCasoDeUso(ListaRepositorio listaRepositorio,
            FilmeRepositorio filmeRepositorio, ListaServico listaServico) {
        return new AdicionarFilmeNaListaCasoDeUso(listaRepositorio, filmeRepositorio, listaServico);
    }

    @Bean
    public RemoverFilmeDaListaCasoDeUso removerFilmeDaListaCasoDeUso(ListaRepositorio listaRepositorio,
            ListaServico listaServico) {
        return new RemoverFilmeDaListaCasoDeUso(listaRepositorio, listaServico);
    }

    @Bean
    public ReordenarItemListaCasoDeUso reordenarItemListaCasoDeUso(ListaRepositorio listaRepositorio,
            ListaServico listaServico) {
        return new ReordenarItemListaCasoDeUso(listaRepositorio, listaServico);
    }

    @Bean
    public TornarListaColaborativaCasoDeUso tornarListaColaborativaCasoDeUso(ListaRepositorio listaRepositorio,
            ListaServico listaServico) {
        return new TornarListaColaborativaCasoDeUso(listaRepositorio, listaServico);
    }

    @Bean
    public AdicionarColaboradorListaCasoDeUso adicionarColaboradorListaCasoDeUso(ListaRepositorio listaRepositorio,
            ListaServico listaServico) {
        return new AdicionarColaboradorListaCasoDeUso(listaRepositorio, listaServico);
    }

    @Bean
    public SolicitarFilmeCasoDeUso solicitarFilmeCasoDeUso(SolicitacaoServico solicitacaoServico, GeradorId geradorId) {
        return new SolicitarFilmeCasoDeUso(solicitacaoServico, geradorId);
    }

    @Bean
    public CancelarSolicitacaoFilmeCasoDeUso cancelarSolicitacaoFilmeCasoDeUso(
            SolicitacaoRepositorio solicitacaoRepositorio, SolicitacaoServico solicitacaoServico) {
        return new CancelarSolicitacaoFilmeCasoDeUso(solicitacaoRepositorio, solicitacaoServico);
    }

    // AvaliarSolicitacaoFilmeCasoDeUso aguardando UsuarioRepositorioImpl do módulo identidade
}
