package com.requisitos.avaliacaofilmes.TelaScore.aplicacao.informacao.calendario;

import com.requisitos.avaliacaofilmes.TelaScore.dominio.catalogo.filme.FilmeId;
import com.requisitos.avaliacaofilmes.TelaScore.dominio.identidade.usuario.UsuarioId;
import com.requisitos.avaliacaofilmes.TelaScore.dominio.informacao.calendario.CalendarioEstreia;
import com.requisitos.avaliacaofilmes.TelaScore.dominio.informacao.calendario.CalendarioRepositorio;

public class RemoverFilmeDoCalendarioCasoDeUso {

    private final CalendarioRepositorio repositorio;

    public RemoverFilmeDoCalendarioCasoDeUso(CalendarioRepositorio repositorio) {
        this.repositorio = repositorio;
    }

    public void executar(int usuarioId, String filmeId) {
        CalendarioEstreia calendario = repositorio.obterPorUsuario(new UsuarioId(usuarioId));
        if (calendario == null) {
            return;
        }

        calendario.removerFilme(new FilmeId(filmeId));
        repositorio.salvar(calendario);
    }
}
