package com.requisitos.avaliacaofilmes.TelaScore.aplicacao.social.comunidade;

import java.util.List;
import com.requisitos.avaliacaofilmes.TelaScore.dominio.social.comunidade.ComunidadeId;
import com.requisitos.avaliacaofilmes.TelaScore.dominio.social.comunidade.ComunidadeRepositorio;
import com.requisitos.avaliacaofilmes.TelaScore.dominio.social.comunidade.MembroComunidade;

public class ListarMembrosComunidadeCasoDeUso {
    private final ComunidadeRepositorio repositorio;

    public ListarMembrosComunidadeCasoDeUso(ComunidadeRepositorio repositorio) {
        this.repositorio = repositorio;
    }

    public List<MembroComunidade> executar(int comunidadeId) {
        return repositorio.buscarMembrosDaComunidade(new ComunidadeId(comunidadeId));
    }
}