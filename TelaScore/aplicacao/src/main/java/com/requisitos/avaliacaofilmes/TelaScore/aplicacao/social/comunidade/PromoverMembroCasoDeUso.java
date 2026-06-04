package com.requisitos.avaliacaofilmes.TelaScore.aplicacao.social.comunidade;

import com.requisitos.avaliacaofilmes.TelaScore.dominio.identidade.usuario.UsuarioId;
import com.requisitos.avaliacaofilmes.TelaScore.dominio.social.comunidade.ComunidadeId;
import com.requisitos.avaliacaofilmes.TelaScore.dominio.social.comunidade.ComunidadeRepositorio;
import com.requisitos.avaliacaofilmes.TelaScore.dominio.social.comunidade.PapelComunidade;

public class PromoverMembroCasoDeUso {
    private final ComunidadeRepositorio repositorio;

    public PromoverMembroCasoDeUso(ComunidadeRepositorio repositorio) {
        this.repositorio = repositorio;
    }

    public void executar(int comunidadeId, int usuarioId, int solicitanteId) {
        ComunidadeId cid = new ComunidadeId(comunidadeId);
        UsuarioId uidSolicitante = new UsuarioId(solicitanteId);

        boolean ehCriador = repositorio.verificarSeEhCriador(cid, uidSolicitante);

        if (!ehCriador) {
            throw new SecurityException("Apenas o criador da comunidade pode promover membros.");
        }

        repositorio.atualizarPapelMembro(
                cid,
                new UsuarioId(usuarioId),
                PapelComunidade.MODERADOR
        );
    }
}