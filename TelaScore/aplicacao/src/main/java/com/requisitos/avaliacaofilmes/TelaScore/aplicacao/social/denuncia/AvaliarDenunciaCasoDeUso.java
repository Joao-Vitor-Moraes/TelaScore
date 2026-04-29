package com.requisitos.avaliacaofilmes.TelaScore.aplicacao.social.denuncia;

import com.requisitos.avaliacaofilmes.TelaScore.dominio.social.denuncia.Denuncia;
import com.requisitos.avaliacaofilmes.TelaScore.dominio.social.denuncia.DenunciaId;
import com.requisitos.avaliacaofilmes.TelaScore.dominio.social.denuncia.DenunciaRepositorio;
import com.requisitos.avaliacaofilmes.TelaScore.dominio.social.denuncia.DenunciaServico;

public class AvaliarDenunciaCasoDeUso {
	private final DenunciaRepositorio repositorio;
	private final DenunciaServico servico;

	public AvaliarDenunciaCasoDeUso(DenunciaRepositorio repositorio, DenunciaServico servico) {
		this.repositorio = repositorio;
		this.servico = servico;
	}

	public DenunciaResumo executar(AvaliarDenunciaComando comando) {
		Denuncia denuncia = repositorio.obter(new DenunciaId(comando.denunciaId()));

		if ("ACEITAR".equalsIgnoreCase(comando.decisao())) {
			servico.aceitar(denuncia);
		} else if ("REJEITAR".equalsIgnoreCase(comando.decisao())) {
			servico.rejeitar(denuncia);
		} else if ("ANALISAR".equalsIgnoreCase(comando.decisao())) {
			servico.colocarEmAnalise(denuncia);
		} else {
			throw new IllegalArgumentException("Decisão de denúncia inválida");
		}

		return DenunciaResumo.de(denuncia);
	}
}
