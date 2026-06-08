package com.requisitos.avaliacaofilmes.TelaScore.aplicacao.social.comunidade;

import java.util.List;
import com.requisitos.avaliacaofilmes.TelaScore.dominio.social.comunidade.ComunidadeId;
import com.requisitos.avaliacaofilmes.TelaScore.dominio.social.comunidade.ComunidadeRepositorio;
import com.requisitos.avaliacaofilmes.TelaScore.dominio.social.comunidade.MensagemComunidade;

public class ListarMensagensCasoDeUso {
    private final ComunidadeRepositorio repositorio;

    public ListarMensagensCasoDeUso(ComunidadeRepositorio repositorio) {
        this.repositorio = repositorio;
    }

    public List<MensagemComunidade> executar(int comunidadeId) {
        return repositorio.buscarMensagensDaComunidade(new ComunidadeId(comunidadeId));
    }
}