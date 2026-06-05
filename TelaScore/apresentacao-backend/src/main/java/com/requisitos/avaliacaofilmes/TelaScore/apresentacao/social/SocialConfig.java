package com.requisitos.avaliacaofilmes.TelaScore.apresentacao.social;

import com.requisitos.avaliacaofilmes.TelaScore.aplicacao.social.comunidade.*;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import com.requisitos.avaliacaofilmes.TelaScore.dominio.social.comunidade.ComunidadeRepositorio;

@Configuration
public class SocialConfig {

    @Bean
    public CriarComunidadeCasoDeUso criarComunidadeCasoDeUso(ComunidadeRepositorio comunidadeRepositorio) {
        return new CriarComunidadeCasoDeUso(comunidadeRepositorio);
    }

    @Bean
    public EntrarComunidade entrarComunidade(ComunidadeRepositorio comunidadeRepositorio) {
        EntrarComunidadeCasoDeUso real = new EntrarComunidadeCasoDeUso(comunidadeRepositorio);
        return new EntrarComunidadeProxy(real);
    }

    @Bean
    public RemoverMembroCasoDeUso removerMembroCasoDeUso(ComunidadeRepositorio comunidadeRepositorio) {
        return new RemoverMembroCasoDeUso(comunidadeRepositorio);
    }

    @Bean
    public ListarComunidadesUsuarioCasoDeUso listarComunidadesUsuarioCasoDeUso(ComunidadeRepositorio comunidadeRepositorio) {
        return new ListarComunidadesUsuarioCasoDeUso(comunidadeRepositorio);
    }

    @Bean
    public ListarTodasComunidadesCasoDeUso listarTodasComunidadesCasoDeUso(ComunidadeRepositorio comunidadeRepositorio) {
        return new ListarTodasComunidadesCasoDeUso(comunidadeRepositorio);
    }

    @Bean
    public ListarMembrosComunidadeCasoDeUso listarMembrosComunidadeCasoDeUso(ComunidadeRepositorio comunidadeRepositorio) {
        return new ListarMembrosComunidadeCasoDeUso(comunidadeRepositorio);
    }

    @Bean
    public PromoverMembroCasoDeUso promoverMembroCasoDeUso(ComunidadeRepositorio comunidadeRepositorio) {
        return new PromoverMembroCasoDeUso(comunidadeRepositorio);
    }

    @Bean
    public ExcluirComunidadeCasoDeUso excluirComunidadeCasoDeUso(ComunidadeRepositorio comunidadeRepositorio) {
        return new ExcluirComunidadeCasoDeUso(comunidadeRepositorio);
    }

    @Bean
    public RebaixarMembroCasoDeUso rebaixarMembroCasoDeUso(ComunidadeRepositorio comunidadeRepositorio) {
        return new RebaixarMembroCasoDeUso(comunidadeRepositorio);
    }

    @Bean
    public EnviarMensagemCasoDeUso enviarMensagemCasoDeUso(ComunidadeRepositorio comunidadeRepositorio) {
        return new EnviarMensagemCasoDeUso(comunidadeRepositorio);
    }

    @Bean
    public ListarMensagensCasoDeUso listarMensagensCasoDeUso(ComunidadeRepositorio comunidadeRepositorio) {
        return new ListarMensagensCasoDeUso(comunidadeRepositorio);
    }

    @Bean
    public ExcluirMensagemCasoDeUso excluirMensagemCasoDeUso(ComunidadeRepositorio comunidadeRepositorio) {
        return new ExcluirMensagemCasoDeUso(comunidadeRepositorio);
    }
}