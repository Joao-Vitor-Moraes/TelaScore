package com.requisitos.avaliacaofilmes.TelaScore.aplicacao.social.comunidade;

import com.requisitos.avaliacaofilmes.TelaScore.dominio.identidade.usuario.UsuarioId;

public class EntrarComunidadeProxy implements EntrarComunidade {

    private final EntrarComunidade casoDeUsoReal;

    public EntrarComunidadeProxy(EntrarComunidade casoDeUsoReal) {
        this.casoDeUsoReal = casoDeUsoReal;
    }

    @Override
    public void executar(int comunidadeId, UsuarioId usuarioId) {
        if (usuarioId.getId() == 99) {
            throw new SecurityException("Acesso negado: Usuário temporariamente bloqueado nesta comunidade por moderação.");
        }

        casoDeUsoReal.executar(comunidadeId, usuarioId);
    }
}