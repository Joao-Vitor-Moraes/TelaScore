package com.requisitos.avaliacaofilmes.TelaScore.aplicacao.social.denuncia;

import java.util.List;

import com.requisitos.avaliacaofilmes.TelaScore.dominio.social.denuncia.DenunciaRepositorio;

public class ListarTodasDenunciasCasoDeUso {
	private final DenunciaRepositorio repositorio;

	public ListarTodasDenunciasCasoDeUso(DenunciaRepositorio repositorio) {
		this.repositorio = repositorio;
	}

	public List<DenunciaResumo> executar() {
		return MapeadorDenunciasComIterador.paraResumo(repositorio.listarTodas());
	}
}
