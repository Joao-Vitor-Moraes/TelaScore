package com.requisitos.avaliacaofilmes.TelaScore.aplicacao.analise.quiz;

import java.util.List;

// Aqui definimos a estrutura de dados que a aplicação aceita receber
public record CriarQuizComando(
    int id,
    String titulo,
    String descricao,
    List<PerguntaInfo> perguntas
) {
    // Sub-estrutura para as perguntas dentro do comando
    public record PerguntaInfo(
        String enunciado,
        List<AlternativaInfo> alternativas
    ) {}

    // Sub-estrutura para as alternativas
    public record AlternativaInfo(
        String texto,
        boolean correta
    ) {}
}