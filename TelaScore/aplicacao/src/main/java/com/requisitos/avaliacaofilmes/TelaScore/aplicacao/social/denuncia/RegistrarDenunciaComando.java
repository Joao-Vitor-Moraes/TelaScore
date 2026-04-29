package com.requisitos.avaliacaofilmes.TelaScore.aplicacao.social.denuncia;

public record RegistrarDenunciaComando(
	int denuncianteId,
	int alvoId,
	String tipoAlvo,
	String motivo,
	String descricao
) {}
