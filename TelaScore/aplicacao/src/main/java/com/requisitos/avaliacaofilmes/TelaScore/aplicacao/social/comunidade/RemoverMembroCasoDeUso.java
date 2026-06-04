package com.requisitos.avaliacaofilmes.TelaScore.aplicacao.social.comunidade;

import com.requisitos.avaliacaofilmes.TelaScore.dominio.identidade.usuario.UsuarioId;
import com.requisitos.avaliacaofilmes.TelaScore.dominio.social.comunidade.ComunidadeId;
import com.requisitos.avaliacaofilmes.TelaScore.dominio.social.comunidade.ComunidadeRepositorio;

public class RemoverMembroCasoDeUso {
    private final ComunidadeRepositorio repositorio;

    public RemoverMembroCasoDeUso(ComunidadeRepositorio repositorio) {
        this.repositorio = repositorio;
    }

    public void executar(int comunidadeId, int usuarioId, int solicitanteId) {
        ComunidadeId cid = new ComunidadeId(comunidadeId);
        UsuarioId uidRemovido = new UsuarioId(usuarioId);

        boolean alvoEhCriador = repositorio.verificarSeEhCriador(cid, uidRemovido);

        if (alvoEhCriador) {
            throw new IllegalArgumentException("O criador nao pode abandonar a comunidade. Exclua a comunidade para encerra-la.");
        }

        repositorio.removerMembro(cid, uidRemovido);
    }
}