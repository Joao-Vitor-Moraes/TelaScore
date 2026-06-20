package com.requisitos.avaliacaofilmes.TelaScore.apresentacao.informacao.evento;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import com.requisitos.avaliacaofilmes.TelaScore.aplicacao.identidade.GeradorId;
import com.requisitos.avaliacaofilmes.TelaScore.aplicacao.informacao.evento.AgendarEventoCasoDeUso;
import com.requisitos.avaliacaofilmes.TelaScore.aplicacao.informacao.evento.CancelarEventoCasoDeUso;
import com.requisitos.avaliacaofilmes.TelaScore.aplicacao.informacao.evento.ListarEventosVisiveisCasoDeUso;
import com.requisitos.avaliacaofilmes.TelaScore.aplicacao.informacao.evento.ObterEventoCasoDeUso;
import com.requisitos.avaliacaofilmes.TelaScore.aplicacao.informacao.evento.ResponderEventoCasoDeUso;
import com.requisitos.avaliacaofilmes.TelaScore.aplicacao.social.conexao.ListarAmigosCasoDeUso;
import com.requisitos.avaliacaofilmes.TelaScore.dominio.identidade.usuario.UsuarioRepositorio;
import com.requisitos.avaliacaofilmes.TelaScore.dominio.informacao.evento.EventoRepositorio;
import com.requisitos.avaliacaofilmes.TelaScore.dominio.informacao.evento.EventoServico;
import com.requisitos.avaliacaofilmes.TelaScore.dominio.social.comunidade.ComunidadeRepositorio;
import com.requisitos.avaliacaofilmes.TelaScore.dominio.social.conexao.ConexaoRepositorio;

@Configuration
public class EventoConfig {

    @Bean
    public EventoServico eventoServico(EventoRepositorio eventoRepositorio) {
        return new EventoServico(eventoRepositorio);
    }

    @Bean
    public AgendarEventoCasoDeUso agendarEventoCasoDeUso(EventoServico eventoServico, GeradorId geradorId) {
        return new AgendarEventoCasoDeUso(eventoServico, geradorId);
    }

    @Bean
    public CancelarEventoCasoDeUso cancelarEventoCasoDeUso(EventoServico eventoServico) {
        return new CancelarEventoCasoDeUso(eventoServico);
    }

    @Bean
    public ResponderEventoCasoDeUso responderEventoCasoDeUso(EventoRepositorio eventoRepositorio) {
        return new ResponderEventoCasoDeUso(eventoRepositorio);
    }

    @Bean
    public ObterEventoCasoDeUso obterEventoCasoDeUso(EventoRepositorio eventoRepositorio,
                                                     UsuarioRepositorio usuarioRepositorio) {
        return new ObterEventoCasoDeUso(eventoRepositorio, usuarioRepositorio);
    }

    @Bean
    public ListarEventosVisiveisCasoDeUso listarEventosVisiveisCasoDeUso(
            EventoRepositorio eventoRepositorio,
            ConexaoRepositorio conexaoRepositorio,
            ComunidadeRepositorio comunidadeRepositorio,
            UsuarioRepositorio usuarioRepositorio) {
        return new ListarEventosVisiveisCasoDeUso(
                eventoRepositorio, conexaoRepositorio, comunidadeRepositorio, usuarioRepositorio);
    }

    @Bean
    public ListarAmigosCasoDeUso listarAmigosCasoDeUso(ConexaoRepositorio conexaoRepositorio,
                                                       UsuarioRepositorio usuarioRepositorio) {
        return new ListarAmigosCasoDeUso(conexaoRepositorio, usuarioRepositorio);
    }
}
