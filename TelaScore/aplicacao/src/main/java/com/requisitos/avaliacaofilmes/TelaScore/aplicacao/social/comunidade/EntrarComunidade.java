package com.requisitos.avaliacaofilmes.TelaScore.aplicacao.social.comunidade;

import com.requisitos.avaliacaofilmes.TelaScore.dominio.identidade.usuario.UsuarioId;

public interface EntrarComunidade {
    void executar(int comunidadeId, UsuarioId usuarioId);
}