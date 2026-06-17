package com.requisitos.avaliacaofilmes.TelaScore.apresentacao.catalogo;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import com.requisitos.avaliacaofilmes.TelaScore.aplicacao.catalogo.AdicionarColaboradorListaCasoDeUso;
import com.requisitos.avaliacaofilmes.TelaScore.aplicacao.catalogo.RemoverColaboradorListaCasoDeUso;
import com.requisitos.avaliacaofilmes.TelaScore.aplicacao.catalogo.AvaliarSolicitacaoFilmeCasoDeUso;
import com.requisitos.avaliacaofilmes.TelaScore.aplicacao.catalogo.SolicitarAjustesFilmeCasoDeUso;
import com.requisitos.avaliacaofilmes.TelaScore.aplicacao.catalogo.ListarListasPorUsuarioCasoDeUso;
import com.requisitos.avaliacaofilmes.TelaScore.aplicacao.catalogo.ListarSolicitacoesPorSolicitanteCasoDeUso;
import com.requisitos.avaliacaofilmes.TelaScore.aplicacao.catalogo.ListarSolicitacoesPorStatusCasoDeUso;
import com.requisitos.avaliacaofilmes.TelaScore.aplicacao.catalogo.ObterListaCasoDeUso;
import com.requisitos.avaliacaofilmes.TelaScore.aplicacao.catalogo.RemoverListaCasoDeUso;
import com.requisitos.avaliacaofilmes.TelaScore.aplicacao.catalogo.AdicionarFilmeNaListaCasoDeUso;
import com.requisitos.avaliacaofilmes.TelaScore.aplicacao.catalogo.AtualizarAvaliacaoCasoDeUso;
import com.requisitos.avaliacaofilmes.TelaScore.aplicacao.catalogo.AtualizarFilmeCasoDeUso;
import com.requisitos.avaliacaofilmes.TelaScore.aplicacao.catalogo.AvaliarFilmeCasoDeUso;
import com.requisitos.avaliacaofilmes.TelaScore.aplicacao.catalogo.CadastrarFilmeCasoDeUso;
import com.requisitos.avaliacaofilmes.TelaScore.aplicacao.catalogo.ListarFilmesCasoDeUso;
import com.requisitos.avaliacaofilmes.TelaScore.aplicacao.catalogo.CancelarSolicitacaoFilmeCasoDeUso;
import com.requisitos.avaliacaofilmes.TelaScore.aplicacao.catalogo.ConsultarItensListaCasoDeUso;
import com.requisitos.avaliacaofilmes.TelaScore.aplicacao.catalogo.CriarListaCasoDeUso;
import com.requisitos.avaliacaofilmes.TelaScore.aplicacao.catalogo.EditarListaCasoDeUso;
import com.requisitos.avaliacaofilmes.TelaScore.aplicacao.catalogo.EditarSolicitacaoCasoDeUso;
import com.requisitos.avaliacaofilmes.TelaScore.aplicacao.catalogo.ListarAvaliacoesPorFilmeCasoDeUso;
import com.requisitos.avaliacaofilmes.TelaScore.aplicacao.catalogo.ObterAvaliacaoCasoDeUso;
import com.requisitos.avaliacaofilmes.TelaScore.aplicacao.catalogo.ObterFilmeCasoDeUso;
import com.requisitos.avaliacaofilmes.TelaScore.aplicacao.catalogo.ObterSolicitacaoCasoDeUso;
import com.requisitos.avaliacaofilmes.TelaScore.aplicacao.catalogo.RemoverAvaliacaoCasoDeUso;
import com.requisitos.avaliacaofilmes.TelaScore.aplicacao.catalogo.RemoverFilmeCasoDeUso;
import com.requisitos.avaliacaofilmes.TelaScore.aplicacao.catalogo.RegistrarFilmeAssistidoCasoDeUso;
import com.requisitos.avaliacaofilmes.TelaScore.aplicacao.catalogo.RemoverFilmeDaListaCasoDeUso;
import com.requisitos.avaliacaofilmes.TelaScore.aplicacao.catalogo.ReordenarItemListaCasoDeUso;
import com.requisitos.avaliacaofilmes.TelaScore.aplicacao.catalogo.SolicitarFilmeCasoDeUso;
import com.requisitos.avaliacaofilmes.TelaScore.aplicacao.catalogo.TornarListaColaborativaCasoDeUso;
import com.requisitos.avaliacaofilmes.TelaScore.aplicacao.identidade.GeradorId;
import com.requisitos.avaliacaofilmes.TelaScore.dominio.catalogo.avaliacao.AvaliacaoRepositorio;
import com.requisitos.avaliacaofilmes.TelaScore.dominio.catalogo.diretor.DiretorRepositorio;
import com.requisitos.avaliacaofilmes.TelaScore.dominio.catalogo.filme.FilmeRepositorio;
import com.requisitos.avaliacaofilmes.TelaScore.dominio.catalogo.lista.ListaRepositorio;
import com.requisitos.avaliacaofilmes.TelaScore.dominio.catalogo.lista.ListaServico;
import com.requisitos.avaliacaofilmes.TelaScore.dominio.catalogo.solicitacao.SolicitacaoRepositorio;
import com.requisitos.avaliacaofilmes.TelaScore.dominio.catalogo.solicitacao.SolicitacaoServico;
import com.requisitos.avaliacaofilmes.TelaScore.dominio.identidade.usuario.UsuarioRepositorio;

@Configuration
public class CatalogoConfig {

    @Bean
    public CriarListaCasoDeUso criarListaCasoDeUso(ListaServico listaServico, GeradorId geradorId) {
        return new CriarListaCasoDeUso(listaServico, geradorId);
    }

    @Bean
    public ConsultarItensListaCasoDeUso consultarItensListaCasoDeUso(ListaRepositorio listaRepositorio, FilmeRepositorio filmeRepositorio) {
        return new ConsultarItensListaCasoDeUso(listaRepositorio, filmeRepositorio);
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
    public RemoverColaboradorListaCasoDeUso removerColaboradorListaCasoDeUso(ListaRepositorio listaRepositorio,
            ListaServico listaServico) {
        return new RemoverColaboradorListaCasoDeUso(listaRepositorio, listaServico);
    }

    @Bean
    public RegistrarFilmeAssistidoCasoDeUso registrarFilmeAssistidoCasoDeUso(ListaRepositorio listaRepositorio,
            ListaServico listaServico) {
        return new RegistrarFilmeAssistidoCasoDeUso(listaRepositorio, listaServico);
    }

    @Bean
    public EditarSolicitacaoCasoDeUso editarSolicitacaoCasoDeUso(SolicitacaoRepositorio solicitacaoRepositorio) {
        return new EditarSolicitacaoCasoDeUso(solicitacaoRepositorio);
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

    @Bean
    public EditarListaCasoDeUso editarListaCasoDeUso(ListaRepositorio listaRepositorio, ListaServico listaServico) {
        return new EditarListaCasoDeUso(listaRepositorio, listaServico);
    }

    @Bean
    public ObterListaCasoDeUso obterListaCasoDeUso(ListaRepositorio listaRepositorio) {
        return new ObterListaCasoDeUso(listaRepositorio);
    }

    @Bean
    public ListarListasPorUsuarioCasoDeUso listarListasPorUsuarioCasoDeUso(ListaRepositorio listaRepositorio) {
        return new ListarListasPorUsuarioCasoDeUso(listaRepositorio);
    }

    @Bean
    public RemoverListaCasoDeUso removerListaCasoDeUso(ListaRepositorio listaRepositorio) {
        return new RemoverListaCasoDeUso(listaRepositorio);
    }

    @Bean
    public AvaliarSolicitacaoFilmeCasoDeUso avaliarSolicitacaoFilmeCasoDeUso(
            SolicitacaoRepositorio solicitacaoRepositorio,
            UsuarioRepositorio usuarioRepositorio,
            SolicitacaoServico solicitacaoServico,
            FilmeRepositorio filmeRepositorio,
            GeradorId geradorId) {
        return new AvaliarSolicitacaoFilmeCasoDeUso(solicitacaoRepositorio, usuarioRepositorio, solicitacaoServico, filmeRepositorio, geradorId);
    }

    @Bean
    public SolicitarAjustesFilmeCasoDeUso solicitarAjustesFilmeCasoDeUso(
            SolicitacaoRepositorio solicitacaoRepositorio,
            UsuarioRepositorio usuarioRepositorio,
            SolicitacaoServico solicitacaoServico) {
        return new SolicitarAjustesFilmeCasoDeUso(solicitacaoRepositorio, usuarioRepositorio, solicitacaoServico);
    }

    @Bean
    public ObterSolicitacaoCasoDeUso obterSolicitacaoCasoDeUso(SolicitacaoRepositorio solicitacaoRepositorio) {
        return new ObterSolicitacaoCasoDeUso(solicitacaoRepositorio);
    }

    @Bean
    public ListarSolicitacoesPorSolicitanteCasoDeUso listarSolicitacoesPorSolicitanteCasoDeUso(
            SolicitacaoRepositorio solicitacaoRepositorio) {
        return new ListarSolicitacoesPorSolicitanteCasoDeUso(solicitacaoRepositorio);
    }

    @Bean
    public ListarSolicitacoesPorStatusCasoDeUso listarSolicitacoesPorStatusCasoDeUso(
            SolicitacaoRepositorio solicitacaoRepositorio) {
        return new ListarSolicitacoesPorStatusCasoDeUso(solicitacaoRepositorio);
    }

    // ─── Filmes ───────────────────────────────────────────────────────────────────

    @Bean
    public ListarFilmesCasoDeUso listarFilmesCasoDeUso(FilmeRepositorio filmeRepositorio,
            AvaliacaoRepositorio avaliacaoRepositorio, DiretorRepositorio diretorRepositorio) {
        return new ListarFilmesCasoDeUso(filmeRepositorio, avaliacaoRepositorio, diretorRepositorio);
    }

    @Bean
    public CadastrarFilmeCasoDeUso cadastrarFilmeCasoDeUso(FilmeRepositorio filmeRepositorio,
            DiretorRepositorio diretorRepositorio, GeradorId geradorId) {
        return new CadastrarFilmeCasoDeUso(filmeRepositorio, diretorRepositorio, geradorId);
    }

    @Bean
    public ObterFilmeCasoDeUso obterFilmeCasoDeUso(FilmeRepositorio filmeRepositorio,
            DiretorRepositorio diretorRepositorio, AvaliacaoRepositorio avaliacaoRepositorio) {
        return new ObterFilmeCasoDeUso(filmeRepositorio, diretorRepositorio, avaliacaoRepositorio);
    }

    @Bean
    public AtualizarFilmeCasoDeUso atualizarFilmeCasoDeUso(FilmeRepositorio filmeRepositorio) {
        return new AtualizarFilmeCasoDeUso(filmeRepositorio);
    }

    @Bean
    public RemoverFilmeCasoDeUso removerFilmeCasoDeUso(FilmeRepositorio filmeRepositorio,
            AvaliacaoRepositorio avaliacaoRepositorio) {
        return new RemoverFilmeCasoDeUso(filmeRepositorio, avaliacaoRepositorio);
    }

    // ─── Avaliações ───────────────────────────────────────────────────────────────

    @Bean
    public AvaliarFilmeCasoDeUso avaliarFilmeCasoDeUso(AvaliacaoRepositorio avaliacaoRepositorio,
            FilmeRepositorio filmeRepositorio, GeradorId geradorId) {
        return new AvaliarFilmeCasoDeUso(avaliacaoRepositorio, filmeRepositorio, geradorId);
    }

    @Bean
    public ObterAvaliacaoCasoDeUso obterAvaliacaoCasoDeUso(AvaliacaoRepositorio avaliacaoRepositorio) {
        return new ObterAvaliacaoCasoDeUso(avaliacaoRepositorio);
    }

    @Bean
    public AtualizarAvaliacaoCasoDeUso atualizarAvaliacaoCasoDeUso(AvaliacaoRepositorio avaliacaoRepositorio) {
        return new AtualizarAvaliacaoCasoDeUso(avaliacaoRepositorio);
    }

    @Bean
    public RemoverAvaliacaoCasoDeUso removerAvaliacaoCasoDeUso(AvaliacaoRepositorio avaliacaoRepositorio) {
        return new RemoverAvaliacaoCasoDeUso(avaliacaoRepositorio);
    }

    @Bean
    public ListarAvaliacoesPorFilmeCasoDeUso listarAvaliacoesPorFilmeCasoDeUso(
            AvaliacaoRepositorio avaliacaoRepositorio, FilmeRepositorio filmeRepositorio) {
        return new ListarAvaliacoesPorFilmeCasoDeUso(avaliacaoRepositorio, filmeRepositorio);
    }
}
