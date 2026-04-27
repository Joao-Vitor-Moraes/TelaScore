package com.requisitos.avaliacaofilmes.TelaScore.aplicacao.social.comunidade;

import com.requisitos.avaliacaofilmes.TelaScore.dominio.identidade.usuario.UsuarioId;
import com.requisitos.avaliacaofilmes.TelaScore.dominio.social.comunidade.*;

public class RemoverMembroCasoDeUso {
    private final ComunidadeRepositorio repositorio;

    public RemoverMembroCasoDeUso(ComunidadeRepositorio repositorio) {
        this.repositorio = repositorio;
    }

    public void executar(int comunidadeId, UsuarioId usuarioId) {
        repositorio.removerMembro(new ComunidadeId(comunidadeId), usuarioId);
    }
}