package com.requisitos.avaliacaofilmes.TelaScore.aplicacao.social.comunidade;

import java.util.List;
import com.requisitos.avaliacaofilmes.TelaScore.dominio.identidade.usuario.UsuarioId;
import com.requisitos.avaliacaofilmes.TelaScore.dominio.social.comunidade.ComunidadeRepositorio;
import com.requisitos.avaliacaofilmes.TelaScore.dominio.social.comunidade.ComunidadeUsuarioResumo;

public class ListarComunidadesUsuarioCasoDeUso {
    private final ComunidadeRepositorio repositorio;

    public ListarComunidadesUsuarioCasoDeUso(ComunidadeRepositorio repositorio) {
        this.repositorio = repositorio;
    }

    public List<ComunidadeUsuarioResumo> executar(int usuarioId) {
        return repositorio.buscarComunidadesDoUsuario(new UsuarioId(usuarioId));
    }
}