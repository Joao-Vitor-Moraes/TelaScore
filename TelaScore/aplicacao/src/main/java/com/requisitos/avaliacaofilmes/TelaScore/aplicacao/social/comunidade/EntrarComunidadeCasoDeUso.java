package com.requisitos.avaliacaofilmes.TelaScore.aplicacao.social.comunidade;

import com.requisitos.avaliacaofilmes.TelaScore.dominio.identidade.usuario.UsuarioId;
import com.requisitos.avaliacaofilmes.TelaScore.dominio.social.comunidade.*;

public class EntrarComunidadeCasoDeUso implements EntrarComunidade {
    private final ComunidadeServico servico;

    public EntrarComunidadeCasoDeUso(ComunidadeRepositorio repositorio) {
        this.servico = new ComunidadeServico(repositorio);
    }

    @Override
    public void executar(int comunidadeId, UsuarioId usuarioId) {
        servico.entrarNaComunidade(new ComunidadeId(comunidadeId), usuarioId);
    }
}