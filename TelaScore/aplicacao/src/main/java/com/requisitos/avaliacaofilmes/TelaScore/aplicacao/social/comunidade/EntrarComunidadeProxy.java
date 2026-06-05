package com.requisitos.avaliacaofilmes.TelaScore.aplicacao.social.comunidade;

import com.requisitos.avaliacaofilmes.TelaScore.dominio.identidade.usuario.UsuarioId;
import com.requisitos.avaliacaofilmes.TelaScore.dominio.social.comunidade.ComunidadeRepositorio;

public class EntrarComunidadeProxy implements EntrarComunidade {

    private final EntrarComunidade casoDeUsoReal;
    private final ComunidadeRepositorio repositorio;

    public EntrarComunidadeProxy(EntrarComunidade casoDeUsoReal, ComunidadeRepositorio repositorio) {
        this.casoDeUsoReal = casoDeUsoReal;
        this.repositorio = repositorio;
    }

    @Override
    public void executar(int comunidadeId, UsuarioId usuarioId) {
        if (repositorio.isUsuarioBloqueado(usuarioId)) {
            throw new SecurityException("Acesso negado: Usuário temporariamente bloqueado por moderação.");
        }

        casoDeUsoReal.executar(comunidadeId, usuarioId);
    }
}