package com.requisitos.avaliacaofilmes.TelaScore.aplicacao.social.comunidade;

import com.requisitos.avaliacaofilmes.TelaScore.dominio.identidade.usuario.UsuarioId;
import com.requisitos.avaliacaofilmes.TelaScore.dominio.social.comunidade.ComunidadeId;
import com.requisitos.avaliacaofilmes.TelaScore.dominio.social.comunidade.ComunidadeRepositorio;

public class ExcluirComunidadeCasoDeUso {
    private final ComunidadeRepositorio repositorio;

    public ExcluirComunidadeCasoDeUso(ComunidadeRepositorio repositorio) {
        this.repositorio = repositorio;
    }

    public void executar(int comunidadeId, int solicitanteId) {
        ComunidadeId cid = new ComunidadeId(comunidadeId);
        UsuarioId uidSolicitante = new UsuarioId(solicitanteId);

        boolean ehCriador = repositorio.verificarSeEhCriador(cid, uidSolicitante);

        if (!ehCriador) {
            throw new SecurityException("Apenas o criador pode excluir a comunidade.");
        }

        repositorio.excluirComunidade(cid);
    }
}