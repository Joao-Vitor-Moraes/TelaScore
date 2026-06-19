package com.requisitos.avaliacaofilmes.TelaScore.apresentacao.informacao.calendario;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import com.requisitos.avaliacaofilmes.TelaScore.aplicacao.identidade.GeradorId;
import com.requisitos.avaliacaofilmes.TelaScore.aplicacao.informacao.calendario.AlternarLembreteCasoDeUso;
import com.requisitos.avaliacaofilmes.TelaScore.aplicacao.informacao.calendario.CriarCalendarioCasoDeUso;
import com.requisitos.avaliacaofilmes.TelaScore.aplicacao.informacao.calendario.DispararLembretesCasoDeUso;
import com.requisitos.avaliacaofilmes.TelaScore.aplicacao.informacao.calendario.ObterCalendarioCasoDeUso;
import com.requisitos.avaliacaofilmes.TelaScore.aplicacao.informacao.calendario.RegistrarFilmeNoCalendarioCasoDeUso;
import com.requisitos.avaliacaofilmes.TelaScore.aplicacao.informacao.calendario.RemoverFilmeDoCalendarioCasoDeUso;
import com.requisitos.avaliacaofilmes.TelaScore.dominio.informacao.calendario.CalendarioRepositorio;
import com.requisitos.avaliacaofilmes.TelaScore.dominio.informacao.calendario.CalendarioServico;
import com.requisitos.avaliacaofilmes.TelaScore.dominio.informacao.calendario.ObservadorEstreia;

@Configuration
public class CalendarioConfig {

    @Bean
    public ObservadorEstreia observadorEstreiaLog() {
        return (usuarioId, entrada) -> System.out.println(
                "[LEMBRETE] Estreia do filme " + entrada.getFilmeId().getCodigo()
                        + " chegou para o usuário " + usuarioId.getId());
    }

    @Bean
    public CalendarioServico calendarioServico(CalendarioRepositorio calendarioRepositorio,
                                              ObservadorEstreia observadorEstreiaLog) {
        return new CalendarioServico(calendarioRepositorio, observadorEstreiaLog);
    }

    @Bean
    public CriarCalendarioCasoDeUso criarCalendarioCasoDeUso(CalendarioRepositorio calendarioRepositorio,
                                                            GeradorId geradorId) {
        return new CriarCalendarioCasoDeUso(calendarioRepositorio, geradorId);
    }

    @Bean
    public RegistrarFilmeNoCalendarioCasoDeUso registrarFilmeNoCalendarioCasoDeUso(
            CriarCalendarioCasoDeUso criarCalendarioCasoDeUso, CalendarioServico calendarioServico) {
        return new RegistrarFilmeNoCalendarioCasoDeUso(criarCalendarioCasoDeUso, calendarioServico);
    }

    @Bean
    public RemoverFilmeDoCalendarioCasoDeUso removerFilmeDoCalendarioCasoDeUso(
            CalendarioRepositorio calendarioRepositorio) {
        return new RemoverFilmeDoCalendarioCasoDeUso(calendarioRepositorio);
    }

    @Bean
    public AlternarLembreteCasoDeUso alternarLembreteCasoDeUso(CalendarioRepositorio calendarioRepositorio) {
        return new AlternarLembreteCasoDeUso(calendarioRepositorio);
    }

    @Bean
    public ObterCalendarioCasoDeUso obterCalendarioCasoDeUso(CalendarioRepositorio calendarioRepositorio) {
        return new ObterCalendarioCasoDeUso(calendarioRepositorio);
    }

    @Bean
    public DispararLembretesCasoDeUso dispararLembretesCasoDeUso(CalendarioServico calendarioServico) {
        return new DispararLembretesCasoDeUso(calendarioServico);
    }
}
