package com.requisitos.avaliacaofilmes.TelaScore.apresentacao.analise;

import org.springframework.boot.ApplicationRunner;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import com.requisitos.avaliacaofilmes.TelaScore.aplicacao.analise.meta.*;
import com.requisitos.avaliacaofilmes.TelaScore.aplicacao.analise.meta.template.AtualizadorMetaComNotificacao;
import com.requisitos.avaliacaofilmes.TelaScore.aplicacao.analise.meta.template.AtualizadorMetaSilencioso;
import com.requisitos.avaliacaofilmes.TelaScore.aplicacao.analise.quiz.CriarQuizCasoDeUso;
import com.requisitos.avaliacaofilmes.TelaScore.aplicacao.analise.quiz.RemoverQuizCasoDeUso;
import com.requisitos.avaliacaofilmes.TelaScore.aplicacao.analise.quiz.ResponderQuizCasoDeUso;
import com.requisitos.avaliacaofilmes.TelaScore.aplicacao.analise.recomendacao.*;
import com.requisitos.avaliacaofilmes.TelaScore.aplicacao.analise.recompensa.ConcederPontosCasoDeUso;
import com.requisitos.avaliacaofilmes.TelaScore.aplicacao.analise.recompensa.ConsultarTotalPontosCasoDeUso;
import com.requisitos.avaliacaofilmes.TelaScore.aplicacao.analise.recompensa.ListarHistoricoPontosCasoDeUso;
import com.requisitos.avaliacaofilmes.TelaScore.aplicacao.identidade.GeradorId;
import com.requisitos.avaliacaofilmes.TelaScore.dominio.analise.meta.MetaRepositorio;
import com.requisitos.avaliacaofilmes.TelaScore.dominio.analise.meta.MetaSistema;
import com.requisitos.avaliacaofilmes.TelaScore.dominio.analise.meta.MetaSistemaRepositorio;
import com.requisitos.avaliacaofilmes.TelaScore.dominio.analise.meta.NotificacaoMetaRepositorio;
import com.requisitos.avaliacaofilmes.TelaScore.dominio.analise.quiz.QuizRepositorio;
import com.requisitos.avaliacaofilmes.TelaScore.dominio.analise.quiz.QuizServico;
import com.requisitos.avaliacaofilmes.TelaScore.dominio.analise.recomendacao.*;
import com.requisitos.avaliacaofilmes.TelaScore.dominio.analise.recompensa.*;
import com.requisitos.avaliacaofilmes.TelaScore.dominio.identidade.usuario.UsuarioRepositorio;
import com.requisitos.avaliacaofilmes.TelaScore.infraestrutura.analise.recompensa.EstrategiaPontuacaoFactory;

@Configuration
public class AnaliseConfig {
    @Bean
    public CriarMetaCasoDeUso criarMetaCasoDeUso(MetaRepositorio repositorio, GeradorId geradorId) {
        return new CriarMetaCasoDeUso(repositorio, geradorId);
    }

    @Bean
    public PontuacaoServico pontuacaoServico(RegistroPontuacaoRepositorio repositorio) {
        return new PontuacaoServico(repositorio);
    }

    @Bean
    public EstrategiaPontuacaoFactory estrategiaPontuacaoFactory() {
        return new EstrategiaPontuacaoFactory();
    }

    @Bean
    public ConcederPontosCasoDeUso concederPontosCasoDeUso(
            PontuacaoServico pontuacao,
            EstrategiaPontuacaoFactory estrategias) {
        return new ConcederPontosCasoDeUso(pontuacao, estrategias.obter(AcaoPontuada.AVALIAR_FILME));
    }

    @Bean
    public ConsultarTotalPontosCasoDeUso consultarTotalPontosCasoDeUso(
            RegistroPontuacaoRepositorio repositorio) {
        return new ConsultarTotalPontosCasoDeUso(repositorio);
    }

    @Bean
    public ListarHistoricoPontosCasoDeUso listarHistoricoPontosCasoDeUso(
            RegistroPontuacaoRepositorio repositorio) {
        return new ListarHistoricoPontosCasoDeUso(repositorio);
    }

    @Bean
    public AtualizadorMetaComNotificacao atualizadorMetaComNotificacao(
            MetaRepositorio metas,
            NotificacaoMetaRepositorio notificacoes) {
        return new AtualizadorMetaComNotificacao(metas, notificacoes);
    }

    @Bean
    public AtualizadorMetaSilencioso atualizadorMetaSilencioso(MetaRepositorio metas) {
        return new AtualizadorMetaSilencioso(metas);
    }

    @Bean
    public AdicionarProgressoMetaCasoDeUso adicionarProgressoMetaCasoDeUso(
            AtualizadorMetaComNotificacao comFeedback,
            AtualizadorMetaSilencioso silencioso) {
        return new AdicionarProgressoMetaCasoDeUso(comFeedback, silencioso);
    }

    @Bean
    public RemoverProgressoMetaCasoDeUso removerProgressoMetaCasoDeUso(MetaRepositorio repositorio) {
        return new RemoverProgressoMetaCasoDeUso(repositorio);
    }

    @Bean
    public EstenderPrazoMetaCasoDeUso estenderPrazoMetaCasoDeUso(MetaRepositorio repositorio) {
        return new EstenderPrazoMetaCasoDeUso(repositorio);
    }

    @Bean
    public ListarMetasPorUsuarioCasoDeUso listarMetasPorUsuarioCasoDeUso(
            MetaRepositorio repositorio, MetaSistemaRepositorio sistemas, GeradorId geradorId) {
        return new ListarMetasPorUsuarioCasoDeUso(repositorio, sistemas, geradorId);
    }

    @Bean
    public CriarMetaSistemaCasoDeUso criarMetaSistemaCasoDeUso(
            MetaSistemaRepositorio sistemas, MetaRepositorio metas,
            UsuarioRepositorio usuarios, GeradorId geradorId) {
        return new CriarMetaSistemaCasoDeUso(sistemas, metas, usuarios, geradorId);
    }

    @Bean
    public ApplicationRunner metasSistemaIniciais(MetaSistemaRepositorio repositorio) {
        return args -> {
            if (!repositorio.listarAtivas().isEmpty()) return;
            repositorio.salvar(new MetaSistema(repositorio.proximoId(),
                    "Começar minha jornada no cinema", 5, 30, 2));
            repositorio.salvar(new MetaSistema(repositorio.proximoId(),
                    "Descobrir novos favoritos", 10, 90, 2));
            repositorio.salvar(new MetaSistema(repositorio.proximoId(),
                    "Desafio cinéfilo do ano", 25, 365, 2));
        };
    }

    @Bean
    public QuizServico quizServico(QuizRepositorio quizRepositorio) {
        return new QuizServico(quizRepositorio);
    }

    @Bean
    public CriarQuizCasoDeUso criarQuizCasoDeUso(QuizRepositorio quizRepositorio,
                                                   QuizServico quizServico,
                                                   GeradorId geradorId) {
        return new CriarQuizCasoDeUso(quizRepositorio, quizServico, geradorId);
    }

    @Bean
    public RemoverQuizCasoDeUso removerQuizCasoDeUso(QuizRepositorio quizRepositorio) {
        return new RemoverQuizCasoDeUso(quizRepositorio);
    }

    @Bean
    public ResponderQuizCasoDeUso responderQuizCasoDeUso(QuizRepositorio quizRepositorio) {
        return new ResponderQuizCasoDeUso(quizRepositorio);
    }

    @Bean
    public RecomendacaoServico recomendacaoServico(RecomendacaoRepositorio repositorio) {
        return new RecomendacaoServico(repositorio);
    }

    @Bean
    public EnviarRecomendacaoCasoDeUso enviarRecomendacaoCasoDeUso(
            RecomendacaoServico servico, GeradorId geradorId) {
        return new EnviarRecomendacaoCasoDeUso(servico, geradorId);
    }

    @Bean
    public ResponderRecomendacaoCasoDeUso responderRecomendacaoCasoDeUso(
            RecomendacaoRepositorio repositorio) {
        return new ResponderRecomendacaoCasoDeUso(repositorio);
    }

    @Bean
    public ListarRecomendacoesPorUsuarioCasoDeUso listarRecomendacoesPorUsuarioCasoDeUso(
            RecomendacaoRepositorio repositorio) {
        return new ListarRecomendacoesPorUsuarioCasoDeUso(repositorio);
    }
}
