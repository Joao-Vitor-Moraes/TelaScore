package com.requisitos.avaliacaofilmes.TelaScore.aplicacao.social.denuncia;

import java.util.List;

import com.requisitos.avaliacaofilmes.TelaScore.dominio.social.denuncia.DenunciaRepositorio;
import com.requisitos.avaliacaofilmes.TelaScore.dominio.social.denuncia.StatusDenuncia;

public class ListarDenunciasPendentesCasoDeUso {
	private final DenunciaRepositorio repositorio;

	public ListarDenunciasPendentesCasoDeUso(DenunciaRepositorio repositorio) {
		this.repositorio = repositorio;
	}

	public List<DenunciaResumo> executar() {
		return repositorio.listarPorStatus(StatusDenuncia.PENDENTE)
			.stream()
			.map(DenunciaResumo::de)
			.toList();
	}
}
