package com.requisitos.avaliacaofilmes.TelaScore.aplicacao.analise.recomendacao;

import com.requisitos.avaliacaofilmes.TelaScore.aplicacao.identidade.GeradorId;
import com.requisitos.avaliacaofilmes.TelaScore.dominio.analise.recomendacao.Recomendacao;
import com.requisitos.avaliacaofilmes.TelaScore.dominio.analise.recomendacao.RecomendacaoId;
import com.requisitos.avaliacaofilmes.TelaScore.dominio.analise.recomendacao.RecomendacaoRepositorio;
import com.requisitos.avaliacaofilmes.TelaScore.dominio.catalogo.filme.FilmeId;
import com.requisitos.avaliacaofilmes.TelaScore.dominio.identidade.usuario.UsuarioId;

public class EnviarRecomendacaoCasoDeUso {

    private final RecomendacaoRepositorio repositorio;
    private final GeradorId geradorId;

    public EnviarRecomendacaoCasoDeUso(RecomendacaoRepositorio repositorio, GeradorId geradorId) {
        this.repositorio = repositorio;
        this.geradorId = geradorId;
    }

    public void executar(EnviarRecomendacaoComando comando) {
        UsuarioId remetente = new UsuarioId(comando.remetenteId());
        UsuarioId destinatario = new UsuarioId(comando.destinatarioId());
        FilmeId filme = new FilmeId(comando.filmeId());
        RecomendacaoId id = new RecomendacaoId(geradorId.gerarProximoIdMeta()); 
        Recomendacao recomendacao = new Recomendacao(
            id, destinatario, filme, comando.pesoAcerto(), remetente, comando.mensagem()
        );

        repositorio.salvar(recomendacao);
    }
}