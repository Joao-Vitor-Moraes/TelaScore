package com.requisitos.avaliacaofilmes.TelaScore.infraestrutura.config;

import com.requisitos.avaliacaofilmes.TelaScore.aplicacao.identidade.GeradorId;
import com.requisitos.avaliacaofilmes.TelaScore.dominio.analise.meta.MetaRepositorio;
import com.requisitos.avaliacaofilmes.TelaScore.dominio.analise.quiz.QuizRepositorio;
import com.requisitos.avaliacaofilmes.TelaScore.dominio.analise.recomendacao.RecomendacaoRepositorio;
import com.requisitos.avaliacaofilmes.TelaScore.dominio.catalogo.avaliacao.AvaliacaoRepositorio;
import com.requisitos.avaliacaofilmes.TelaScore.dominio.catalogo.diretor.DiretorRepositorio;
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
import com.requisitos.avaliacaofilmes.TelaScore.dominio.informacao.noticia.NoticiaRepositorio;
import com.requisitos.avaliacaofilmes.TelaScore.dominio.social.comunidade.ComunidadeRepositorio;
import com.requisitos.avaliacaofilmes.TelaScore.dominio.social.mensagem.MensagemRepositorio;
import com.requisitos.avaliacaofilmes.TelaScore.infraestrutura.analise.meta.MetaRepositorioImpl;
import com.requisitos.avaliacaofilmes.TelaScore.infraestrutura.analise.quiz.QuizRepositorioImpl;
import com.requisitos.avaliacaofilmes.TelaScore.infraestrutura.analise.recomendacao.RecomendacaoRepositorioImpl;
import com.requisitos.avaliacaofilmes.TelaScore.infraestrutura.catalogo.avaliacao.AvaliacaoRepositorioImpl;
import com.requisitos.avaliacaofilmes.TelaScore.infraestrutura.catalogo.diretor.DiretorRepositorioImpl;
import com.requisitos.avaliacaofilmes.TelaScore.infraestrutura.catalogo.filme.FilmeRepositorioImpl;
import com.requisitos.avaliacaofilmes.TelaScore.infraestrutura.catalogo.lista.ListaRepositorioImpl;
import com.requisitos.avaliacaofilmes.TelaScore.infraestrutura.catalogo.solicitacao.SolicitacaoRepositorioImpl;
import com.requisitos.avaliacaofilmes.TelaScore.infraestrutura.informacao.noticia.NoticiaRepositorioImpl;
import com.requisitos.avaliacaofilmes.TelaScore.infraestrutura.social.comunidade.ComunidadeRepositorioImpl;
import com.requisitos.avaliacaofilmes.TelaScore.infraestrutura.social.mensagem.MensagemRepositorioImpl;
import com.requisitos.avaliacaofilmes.TelaScore.infraestrutura.social.mensagem.MensagemRepositorioLoggingDecorator;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class InfraestruturaConfig {

    @Bean
    public GeradorId geradorId() {
        return new GeradorIdImpl();
    }

    // catalogo
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
    public AvaliacaoRepositorio avaliacaoRepositorio() {
        return new AvaliacaoRepositorioImpl();
    }

    @Bean
    public DiretorRepositorio diretorRepositorio() {
        return new DiretorRepositorioImpl();
    }

    // analise
    @Bean
    public MetaRepositorio metaRepositorio() {
        return new MetaRepositorioImpl();
    }

    @Bean
    public RecomendacaoRepositorio recomendacaoRepositorio() {
        return new RecomendacaoRepositorioImpl();
    }

    @Bean
    public QuizRepositorio quizRepositorio() {
        return new QuizRepositorioImpl(ConexaoBanco.obterEntityManager());
    }

    // informacao
    @Bean
    public NoticiaRepositorio noticiaRepositorio() {
        return new NoticiaRepositorioImpl();
    }

    // social
    @Bean
    public ComunidadeRepositorio comunidadeRepositorio() {
        return new ComunidadeRepositorioImpl();
    }

    @Bean
    public MensagemRepositorio mensagemRepositorio() {
        return new MensagemRepositorioLoggingDecorator(
                new MensagemRepositorioImpl(ConexaoBanco.obterEntityManager()));
    }

    // servicos de dominio
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
