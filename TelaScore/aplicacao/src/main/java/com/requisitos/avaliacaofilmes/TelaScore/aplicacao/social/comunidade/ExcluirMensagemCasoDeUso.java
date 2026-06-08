package com.requisitos.avaliacaofilmes.TelaScore.aplicacao.social.comunidade;

import com.requisitos.avaliacaofilmes.TelaScore.dominio.social.comunidade.*;
import java.util.List;

public class ExcluirMensagemCasoDeUso {
    private final ComunidadeRepositorio repositorio;

    public ExcluirMensagemCasoDeUso(ComunidadeRepositorio repositorio) {
        this.repositorio = repositorio;
    }

    public void executar(int mensagemId, int solicitanteId) {
        MensagemComunidade mensagem = repositorio.obterMensagemPorId(mensagemId);
        if (mensagem == null) {
            throw new IllegalArgumentException("Mensagem nao encontrada.");
        }

        ComunidadeId cid = new ComunidadeId(mensagem.comunidadeId());
        List<MembroComunidade> membros = repositorio.buscarMembrosDaComunidade(cid);

        PapelComunidade papelSolicitante = membros.stream()
                .filter(m -> m.getUsuarioId().getId() == solicitanteId)
                .map(MembroComunidade::getPapel)
                .findFirst()
                .orElseThrow(() -> new SecurityException("Acesso negado: O solicitante nao faz parte desta comunidade."));

        if (papelSolicitante != PapelComunidade.CRIADOR && papelSolicitante != PapelComunidade.MODERADOR) {
            throw new SecurityException("Acesso negado: Apenas criadores e moderadores podem excluir mensagens.");
        }

        repositorio.excluirMensagem(mensagemId);
    }
}