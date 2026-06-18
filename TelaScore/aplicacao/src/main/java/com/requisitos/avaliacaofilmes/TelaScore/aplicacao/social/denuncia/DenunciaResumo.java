package com.requisitos.avaliacaofilmes.TelaScore.aplicacao.social.denuncia;

import java.time.LocalDateTime;

import com.requisitos.avaliacaofilmes.TelaScore.dominio.social.denuncia.Denuncia;

public record DenunciaResumo(
	int id,
	int denuncianteId,
	int alvoId,
	String tipoAlvo,
	String motivo,
	String descricao,
	String linkOcorrencia,
	LocalDateTime dataCriacao,
	String status
) {
	public static DenunciaResumo de(Denuncia denuncia) {
		return new DenunciaResumo(
			denuncia.getId().getId(),
			denuncia.getDenuncianteId().getId(),
			denuncia.getAlvoId(),
			denuncia.getTipoAlvo().name(),
			denuncia.getMotivo().name(),
			denuncia.getDescricao(),
			denuncia.getLinkOcorrencia(),
			denuncia.getDataCriacao(),
			denuncia.getStatus().name()
		);
	}
}
