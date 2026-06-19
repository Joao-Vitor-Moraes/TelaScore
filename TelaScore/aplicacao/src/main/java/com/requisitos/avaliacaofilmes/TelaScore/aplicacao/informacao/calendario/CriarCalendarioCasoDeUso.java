package com.requisitos.avaliacaofilmes.TelaScore.aplicacao.informacao.calendario;

import com.requisitos.avaliacaofilmes.TelaScore.aplicacao.identidade.GeradorId;
import com.requisitos.avaliacaofilmes.TelaScore.dominio.identidade.usuario.UsuarioId;
import com.requisitos.avaliacaofilmes.TelaScore.dominio.informacao.calendario.CalendarioEstreia;
import com.requisitos.avaliacaofilmes.TelaScore.dominio.informacao.calendario.CalendarioId;
import com.requisitos.avaliacaofilmes.TelaScore.dominio.informacao.calendario.CalendarioRepositorio;

public class CriarCalendarioCasoDeUso {

    private final CalendarioRepositorio repositorio;
    private final GeradorId geradorId;

    public CriarCalendarioCasoDeUso(CalendarioRepositorio repositorio, GeradorId geradorId) {
        this.repositorio = repositorio;
        this.geradorId = geradorId;
    }

    public void executar(int usuarioIdInformado) {
        UsuarioId usuarioId = new UsuarioId(usuarioIdInformado);

        if (repositorio.obterPorUsuario(usuarioId) != null) {
            return;
        }

        CalendarioId novoId = new CalendarioId(geradorId.gerarProximoIdCalendario());
        repositorio.salvar(new CalendarioEstreia(novoId, usuarioId));
    }
}
