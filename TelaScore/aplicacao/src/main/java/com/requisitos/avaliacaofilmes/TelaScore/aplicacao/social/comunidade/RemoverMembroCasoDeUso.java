package com.requisitos.avaliacaofilmes.TelaScore.aplicacao.social.comunidade;

import com.requisitos.avaliacaofilmes.TelaScore.dominio.identidade.usuario.UsuarioId;
import com.requisitos.avaliacaofilmes.TelaScore.dominio.social.comunidade.ComunidadeId;
import com.requisitos.avaliacaofilmes.TelaScore.dominio.social.comunidade.ComunidadeRepositorio;
import com.requisitos.avaliacaofilmes.TelaScore.dominio.social.comunidade.MembroComunidade;
import com.requisitos.avaliacaofilmes.TelaScore.dominio.social.comunidade.PapelComunidade;

import java.util.List;

public class RemoverMembroCasoDeUso {
    private final ComunidadeRepositorio repositorio;

    public RemoverMembroCasoDeUso(ComunidadeRepositorio repositorio) {
        this.repositorio = repositorio;
    }

    public void executar(int comunidadId, int usuarioId, int solicitanteId) {
        ComunidadeId cid = new ComunidadeId(comunidadId);
        UsuarioId uidRemovido = new UsuarioId(usuarioId);

        boolean alvoEhCriador = repositorio.verificarSeEhCriador(cid, uidRemovido);

        if (alvoEhCriador) {
            throw new IllegalArgumentException("O criador nao pode abandonar a comunidade. Exclua a comunidade para encerra-la.");
        }

        if (usuarioId != solicitanteId) {
            List<MembroComunidade> membros = repositorio.buscarMembrosDaComunidade(cid);

            PapelComunidade papelSolicitante = membros.stream()
                    .filter(m -> m.getUsuarioId().getId() == solicitanteId)
                    .map(MembroComunidade::getPapel)
                    .findFirst()
                    .orElseThrow(() -> new IllegalArgumentException("O solicitante nao faz parte desta comunidade."));

            if (papelSolicitante != PapelComunidade.CRIADOR && papelSolicitante != PapelComunidade.MODERADOR) {
                throw new SecurityException("Acesso negado: Apenas criadores e moderadores podem remover outros membros.");
            }
        }

        repositorio.removerMembro(cid, uidRemovido);
    }
}