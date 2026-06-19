package com.requisitos.avaliacaofilmes.TelaScore.apresentacao.informacao.evento;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import com.requisitos.avaliacaofilmes.TelaScore.aplicacao.identidade.GeradorId;
import com.requisitos.avaliacaofilmes.TelaScore.aplicacao.informacao.evento.AgendarEventoCasoDeUso;
import com.requisitos.avaliacaofilmes.TelaScore.aplicacao.informacao.evento.CancelarEventoCasoDeUso;
import com.requisitos.avaliacaofilmes.TelaScore.aplicacao.informacao.evento.ListarEventosFuturosCasoDeUso;
import com.requisitos.avaliacaofilmes.TelaScore.aplicacao.informacao.evento.ObterEventoCasoDeUso;
import com.requisitos.avaliacaofilmes.TelaScore.dominio.informacao.evento.EventoRepositorio;
import com.requisitos.avaliacaofilmes.TelaScore.dominio.informacao.evento.EventoServico;

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
    public ListarEventosFuturosCasoDeUso listarEventosFuturosCasoDeUso(EventoRepositorio eventoRepositorio) {
        return new ListarEventosFuturosCasoDeUso(eventoRepositorio);
    }

    @Bean
    public ObterEventoCasoDeUso obterEventoCasoDeUso(EventoRepositorio eventoRepositorio) {
        return new ObterEventoCasoDeUso(eventoRepositorio);
    }
}
