package com.requisitos.avaliacaofilmes.TelaScore.aplicacao.social.comunidade;

import com.requisitos.avaliacaofilmes.TelaScore.aplicacao.identidade.GeradorId;
import com.requisitos.avaliacaofilmes.TelaScore.dominio.social.comunidade.*;

public class CriarComunidadeCasoDeUso {
    private final ComunidadeServico servico;
    private final GeradorId geradorId;

    public CriarComunidadeCasoDeUso(ComunidadeRepositorio repositorio, GeradorId geradorId) {
        this.servico = new ComunidadeServico(repositorio);
        this.geradorId = geradorId;
    }

    public void executar(CriarComunidadeComando comando) {
        int idComunidade = comando.getIdSugerido() > 0
                ? comando.getIdSugerido()
                : geradorId.gerarProximoIdComunidade();
        ComunidadeId id = new ComunidadeId(idComunidade);
        Comunidade novaComunidade = new Comunidade(id, comando.getNome(), comando.getDescricao());

        servico.criarComunidade(novaComunidade, comando.getCriadorId());
    }
}
