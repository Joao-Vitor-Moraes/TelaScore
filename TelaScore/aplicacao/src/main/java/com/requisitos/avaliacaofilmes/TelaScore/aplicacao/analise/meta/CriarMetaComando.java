package com.requisitos.avaliacaofilmes.TelaScore.aplicacao.analise.meta;

import java.time.LocalDate;
import com.requisitos.avaliacaofilmes.TelaScore.dominio.analise.meta.TipoMeta;

public record CriarMetaComando(
		int usuarioId,
		String titulo,
		int quantidadeAlvo,
		LocalDate dataPrazo,
		TipoMeta tipo
) {}
