package com.requisitos.avaliacaofilmes.TelaScore.apresentacao.social;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import com.requisitos.avaliacaofilmes.TelaScore.aplicacao.identidade.GeradorId;
import com.requisitos.avaliacaofilmes.TelaScore.aplicacao.social.RemoverMensagemCasoDeUso;
import com.requisitos.avaliacaofilmes.TelaScore.aplicacao.social.comunidade.CriarComunidadeCasoDeUso;
import com.requisitos.avaliacaofilmes.TelaScore.aplicacao.social.comunidade.EntrarComunidade;
import com.requisitos.avaliacaofilmes.TelaScore.aplicacao.social.comunidade.EntrarComunidadeCasoDeUso;
import com.requisitos.avaliacaofilmes.TelaScore.aplicacao.social.comunidade.EntrarComunidadeProxy;
import com.requisitos.avaliacaofilmes.TelaScore.aplicacao.social.comunidade.EnviarMensagemCasoDeUso;
import com.requisitos.avaliacaofilmes.TelaScore.aplicacao.social.comunidade.ExcluirComunidadeCasoDeUso;
import com.requisitos.avaliacaofilmes.TelaScore.aplicacao.social.comunidade.ExcluirMensagemCasoDeUso;
import com.requisitos.avaliacaofilmes.TelaScore.aplicacao.social.comunidade.ListarComunidadesUsuarioCasoDeUso;
import com.requisitos.avaliacaofilmes.TelaScore.aplicacao.social.comunidade.ListarMembrosComunidadeCasoDeUso;
import com.requisitos.avaliacaofilmes.TelaScore.aplicacao.social.comunidade.ListarMensagensCasoDeUso;
import com.requisitos.avaliacaofilmes.TelaScore.aplicacao.social.comunidade.ListarTodasComunidadesCasoDeUso;
import com.requisitos.avaliacaofilmes.TelaScore.aplicacao.social.comunidade.PromoverMembroCasoDeUso;
import com.requisitos.avaliacaofilmes.TelaScore.aplicacao.social.comunidade.RebaixarMembroCasoDeUso;
import com.requisitos.avaliacaofilmes.TelaScore.aplicacao.social.comunidade.RemoverMembroCasoDeUso;
import com.requisitos.avaliacaofilmes.TelaScore.aplicacao.social.conexao.DeixarDeSeguirCasoDeUso;
import com.requisitos.avaliacaofilmes.TelaScore.aplicacao.social.conexao.ListarConexoesCasoDeUso;
import com.requisitos.avaliacaofilmes.TelaScore.aplicacao.social.conexao.SeguirUsuarioCasoDeUso;
import com.requisitos.avaliacaofilmes.TelaScore.dominio.identidade.usuario.UsuarioRepositorio;
import com.requisitos.avaliacaofilmes.TelaScore.aplicacao.social.denuncia.AvaliarDenunciaCasoDeUso;
import com.requisitos.avaliacaofilmes.TelaScore.aplicacao.social.denuncia.ListarDenunciasPendentesCasoDeUso;
import com.requisitos.avaliacaofilmes.TelaScore.aplicacao.social.denuncia.ListarDenunciasPorStatusCasoDeUso;
import com.requisitos.avaliacaofilmes.TelaScore.aplicacao.social.denuncia.ListarDenunciasPorUsuarioCasoDeUso;
import com.requisitos.avaliacaofilmes.TelaScore.aplicacao.social.denuncia.ListarTodasDenunciasCasoDeUso;
import com.requisitos.avaliacaofilmes.TelaScore.aplicacao.social.denuncia.RegistrarDenunciaCasoDeUso;
import com.requisitos.avaliacaofilmes.TelaScore.dominio.social.comunidade.ComunidadeRepositorio;
import com.requisitos.avaliacaofilmes.TelaScore.dominio.social.conexao.ConexaoRepositorio;
import com.requisitos.avaliacaofilmes.TelaScore.dominio.social.denuncia.DenunciaRepositorio;
import com.requisitos.avaliacaofilmes.TelaScore.dominio.social.denuncia.DenunciaServico;
import com.requisitos.avaliacaofilmes.TelaScore.dominio.social.mensagem.MensagemRepositorio;
import com.requisitos.avaliacaofilmes.TelaScore.dominio.social.mensagem.MensagemServico;

@Configuration
public class SocialConfig {

    @Bean
    public CriarComunidadeCasoDeUso criarComunidadeCasoDeUso(ComunidadeRepositorio comunidadeRepositorio, GeradorId geradorId) {
        return new CriarComunidadeCasoDeUso(comunidadeRepositorio, geradorId);
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

    @Bean
    public SeguirUsuarioCasoDeUso seguirUsuarioCasoDeUso(ConexaoRepositorio conexaoRepositorio, GeradorId geradorId) {
        return new SeguirUsuarioCasoDeUso(conexaoRepositorio, geradorId);
    }

    @Bean
    public DeixarDeSeguirCasoDeUso deixarDeSeguirCasoDeUso(ConexaoRepositorio conexaoRepositorio) {
        return new DeixarDeSeguirCasoDeUso(conexaoRepositorio);
    }

    @Bean
    public ListarConexoesCasoDeUso listarConexoesCasoDeUso(ConexaoRepositorio conexaoRepositorio,
                                                           UsuarioRepositorio usuarioRepositorio) {
        return new ListarConexoesCasoDeUso(conexaoRepositorio, usuarioRepositorio);
    }

    @Bean
    public MensagemServico mensagemServico(MensagemRepositorio mensagemRepositorio) {
        return new MensagemServico(mensagemRepositorio);
    }

    @Bean
    public com.requisitos.avaliacaofilmes.TelaScore.aplicacao.social.EnviarMensagemCasoDeUso enviarMensagemDiretaCasoDeUso(
            MensagemRepositorio mensagemRepositorio,
            MensagemServico mensagemServico,
            GeradorId geradorId) {
        return new com.requisitos.avaliacaofilmes.TelaScore.aplicacao.social.EnviarMensagemCasoDeUso(
                mensagemRepositorio, mensagemServico, geradorId);
    }

    @Bean
    public RemoverMensagemCasoDeUso removerMensagemCasoDeUso(MensagemRepositorio mensagemRepositorio) {
        return new RemoverMensagemCasoDeUso(mensagemRepositorio);
    }

    @Bean
    public DenunciaServico denunciaServico(DenunciaRepositorio denunciaRepositorio) {
        return new DenunciaServico(denunciaRepositorio);
    }

    @Bean
    public RegistrarDenunciaCasoDeUso registrarDenunciaCasoDeUso(DenunciaServico denunciaServico, GeradorId geradorId) {
        return new RegistrarDenunciaCasoDeUso(denunciaServico, geradorId);
    }

    @Bean
    public ListarDenunciasPorUsuarioCasoDeUso listarDenunciasPorUsuarioCasoDeUso(DenunciaRepositorio denunciaRepositorio) {
        return new ListarDenunciasPorUsuarioCasoDeUso(denunciaRepositorio);
    }

    @Bean
    public ListarDenunciasPendentesCasoDeUso listarDenunciasPendentesCasoDeUso(DenunciaRepositorio denunciaRepositorio) {
        return new ListarDenunciasPendentesCasoDeUso(denunciaRepositorio);
    }

    @Bean
    public ListarTodasDenunciasCasoDeUso listarTodasDenunciasCasoDeUso(DenunciaRepositorio denunciaRepositorio) {
        return new ListarTodasDenunciasCasoDeUso(denunciaRepositorio);
    }

    @Bean
    public ListarDenunciasPorStatusCasoDeUso listarDenunciasPorStatusCasoDeUso(DenunciaRepositorio denunciaRepositorio) {
        return new ListarDenunciasPorStatusCasoDeUso(denunciaRepositorio);
    }

    @Bean
    public AvaliarDenunciaCasoDeUso avaliarDenunciaCasoDeUso(DenunciaRepositorio denunciaRepositorio, DenunciaServico denunciaServico) {
        return new AvaliarDenunciaCasoDeUso(denunciaRepositorio, denunciaServico);
    }
}
