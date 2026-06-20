package com.requisitos.avaliacaofilmes.TelaScore.aplicacao.social.denuncia;

import java.util.List;

import com.requisitos.avaliacaofilmes.TelaScore.dominio.identidade.usuario.UsuarioId;
import com.requisitos.avaliacaofilmes.TelaScore.dominio.social.denuncia.DenunciaRepositorio;

public class ListarDenunciasPorUsuarioCasoDeUso {
	private final DenunciaRepositorio repositorio;

	public ListarDenunciasPorUsuarioCasoDeUso(DenunciaRepositorio repositorio) {
		this.repositorio = repositorio;
	}

	public List<DenunciaResumo> executar(int denuncianteId) {
		return MapeadorDenunciasComIterador.paraResumo(
			repositorio.listarPorUsuario(new UsuarioId(denuncianteId)));
	}
}
