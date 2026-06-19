package com.requisitos.avaliacaofilmes.TelaScore.aplicacao.social.denuncia;

import java.util.List;

import com.requisitos.avaliacaofilmes.TelaScore.dominio.social.denuncia.DenunciaRepositorio;
import com.requisitos.avaliacaofilmes.TelaScore.dominio.social.denuncia.StatusDenuncia;

public class ListarDenunciasPorStatusCasoDeUso {
	private final DenunciaRepositorio repositorio;

	public ListarDenunciasPorStatusCasoDeUso(DenunciaRepositorio repositorio) {
		this.repositorio = repositorio;
	}

	public List<DenunciaResumo> executar(String status) {
		return repositorio.listarPorStatus(StatusDenuncia.valueOf(status.trim().toUpperCase()))
			.stream()
			.map(DenunciaResumo::de)
			.toList();
	}
}
