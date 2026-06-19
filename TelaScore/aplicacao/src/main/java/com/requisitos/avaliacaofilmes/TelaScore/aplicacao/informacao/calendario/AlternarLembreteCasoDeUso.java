package com.requisitos.avaliacaofilmes.TelaScore.aplicacao.informacao.calendario;

import com.requisitos.avaliacaofilmes.TelaScore.dominio.catalogo.filme.FilmeId;
import com.requisitos.avaliacaofilmes.TelaScore.dominio.identidade.usuario.UsuarioId;
import com.requisitos.avaliacaofilmes.TelaScore.dominio.informacao.calendario.CalendarioEstreia;
import com.requisitos.avaliacaofilmes.TelaScore.dominio.informacao.calendario.CalendarioRepositorio;
import com.requisitos.avaliacaofilmes.TelaScore.dominio.informacao.calendario.EntradaCalendario;

public class AlternarLembreteCasoDeUso {

    private final CalendarioRepositorio repositorio;

    public AlternarLembreteCasoDeUso(CalendarioRepositorio repositorio) {
        this.repositorio = repositorio;
    }

    public void executar(int usuarioId, String filmeId) {
        CalendarioEstreia calendario = repositorio.obterPorUsuario(new UsuarioId(usuarioId));
        if (calendario == null) {
            return;
        }

        FilmeId alvo = new FilmeId(filmeId);
        for (EntradaCalendario entrada : calendario.getEntradas()) {
            if (entrada.getFilmeId().equals(alvo)) {
                entrada.alternarLembrete();
            }
        }

        repositorio.salvar(calendario);
    }
}
