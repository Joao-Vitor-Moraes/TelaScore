package com.requisitos.avaliacaofilmes.TelaScore.dominio.analise.meta;

import java.util.List;

import com.requisitos.avaliacaofilmes.TelaScore.dominio.identidade.usuario.UsuarioId;

public interface NotificacaoMetaRepositorio {
    default void criar(UsuarioId usuarioId, MetaId metaId, String tituloMeta) {
        criar(usuarioId, metaId, tituloMeta, 0);
    }
    void criar(UsuarioId usuarioId, MetaId metaId, String tituloMeta, int pontosGanhos);
    List<NotificacaoMeta> listarNaoLidas(UsuarioId usuarioId);
    void marcarComoLida(int notificacaoId, UsuarioId usuarioId);
}
