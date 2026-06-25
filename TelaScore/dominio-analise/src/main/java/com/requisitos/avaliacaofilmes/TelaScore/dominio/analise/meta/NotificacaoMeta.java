package com.requisitos.avaliacaofilmes.TelaScore.dominio.analise.meta;

import java.time.LocalDateTime;

import com.requisitos.avaliacaofilmes.TelaScore.dominio.identidade.usuario.UsuarioId;

public record NotificacaoMeta(
        int id,
        UsuarioId usuarioId,
        MetaId metaId,
        String tituloMeta,
        int pontosGanhos,
        LocalDateTime dataCriacao,
        boolean lida,
        String tipo,
        String titulo,
        String mensagem,
        String rota) {
}
