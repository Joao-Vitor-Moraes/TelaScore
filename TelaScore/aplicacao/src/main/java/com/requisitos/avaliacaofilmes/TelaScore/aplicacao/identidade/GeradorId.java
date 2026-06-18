package com.requisitos.avaliacaofilmes.TelaScore.aplicacao.identidade;

public interface GeradorId {
	int gerarProximoIdUsuario();

	int gerarProximoIdFilme();
	int gerarProximoIdAvaliacao();

	int gerarProximoIdMeta();
	int gerarProximoIdRecomendacao();
	int gerarProximoIdRegistroPontuacao();

	int gerarProximoIdEvento();
	int gerarProximoIdNoticia();

	int gerarProximoIdConexao();
	int gerarProximoIdComunidade();
	int gerarProximoIdMensagem();
	int gerarProximoIdDenuncia();

	int gerarProximoIdLista();
	int gerarProximoIdSolicitacao();

	int gerarProximoIdQuiz();
}
