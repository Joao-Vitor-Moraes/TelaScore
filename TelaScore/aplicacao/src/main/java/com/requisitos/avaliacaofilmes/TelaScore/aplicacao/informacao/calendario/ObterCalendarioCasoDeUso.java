package com.requisitos.avaliacaofilmes.TelaScore.aplicacao.informacao.calendario;

import java.util.List;

import com.requisitos.avaliacaofilmes.TelaScore.aplicacao.informacao.calendario.CalendarioResumo.EntradaCalendarioResumo;
import com.requisitos.avaliacaofilmes.TelaScore.dominio.identidade.usuario.UsuarioId;
import com.requisitos.avaliacaofilmes.TelaScore.dominio.informacao.calendario.CalendarioEstreia;
import com.requisitos.avaliacaofilmes.TelaScore.dominio.informacao.calendario.CalendarioRepositorio;

public class ObterCalendarioCasoDeUso {

    private final CalendarioRepositorio repositorio;

    public ObterCalendarioCasoDeUso(CalendarioRepositorio repositorio) {
        this.repositorio = repositorio;
    }

    public CalendarioResumo executar(int usuarioId) {
        CalendarioEstreia calendario = repositorio.obterPorUsuario(new UsuarioId(usuarioId));
        if (calendario == null) {
            return null;
        }

        List<EntradaCalendarioResumo> entradas = calendario.getEntradas().stream()
                .map(entrada -> new EntradaCalendarioResumo(
                        entrada.getFilmeId().getCodigo(),
                        entrada.getDataEstreiaPrevista(),
                        entrada.isLembreteAtivo()))
                .toList();

        return new CalendarioResumo(calendario.getUsuarioId().getId(), entradas);
    }
}
