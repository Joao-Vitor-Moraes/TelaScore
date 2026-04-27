package com.requisitos.avaliacaofilmes.TelaScore.aplicacao.social.comunidade;

import com.requisitos.avaliacaofilmes.TelaScore.dominio.social.comunidade.*;

public class CriarComunidadeCasoDeUso {
    private final ComunidadeServico servico;

    public CriarComunidadeCasoDeUso(ComunidadeRepositorio repositorio) {
        this.servico = new ComunidadeServico(repositorio);
    }

    public void executar(CriarComunidadeComando comando) {
        ComunidadeId id = new ComunidadeId(comando.getIdSugerido());
        Comunidade novaComunidade = new Comunidade(id, comando.getNome(), comando.getDescricao());

        servico.criarComunidade(novaComunidade, comando.getCriadorId());
    }
}