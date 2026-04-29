package com.requisitos.avaliacaofilmes.TelaScore.dominio.social.denuncia;

import java.util.List;

import com.requisitos.avaliacaofilmes.TelaScore.dominio.identidade.usuario.UsuarioId;

public interface DenunciaRepositorio {
	void salvar(Denuncia denuncia);
	Denuncia obter(DenunciaId id);
	List<Denuncia> listarPorStatus(StatusDenuncia status);
	List<Denuncia> listarPorUsuario(UsuarioId denuncianteId);
	boolean existeDenunciaDoUsuarioParaAlvo(UsuarioId denuncianteId, TipoAlvoDenuncia tipoAlvo, int alvoId);
}
