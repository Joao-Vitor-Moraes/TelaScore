package com.requisitos.avaliacaofilmes.TelaScore.dominio.social.denuncia;

import static org.apache.commons.lang3.Validate.isTrue;
import static org.apache.commons.lang3.Validate.notNull;

public class DenunciaServico {
	private final DenunciaRepositorio repositorio;

	public DenunciaServico(DenunciaRepositorio repositorio) {
		notNull(repositorio, "O repositório de denúncias não pode ser nulo");
		this.repositorio = repositorio;
	}

	public void registrar(Denuncia denuncia) {
		notNull(denuncia, "A denúncia não pode ser nula");
		isTrue(!repositorio.existeDenunciaDoUsuarioParaAlvo(
				denuncia.getDenuncianteId(),
				denuncia.getTipoAlvo(),
				denuncia.getAlvoId()),
				"O usuário já denunciou este alvo");

		repositorio.salvar(denuncia);
	}

	public void colocarEmAnalise(Denuncia denuncia) {
		notNull(denuncia, "A denúncia não pode ser nula");
		denuncia.colocarEmAnalise();
		repositorio.salvar(denuncia);
	}

	public void aceitar(Denuncia denuncia) {
		notNull(denuncia, "A denúncia não pode ser nula");
		denuncia.aceitar();
		repositorio.salvar(denuncia);
	}

	public void rejeitar(Denuncia denuncia) {
		notNull(denuncia, "A denúncia não pode ser nula");
		denuncia.rejeitar();
		repositorio.salvar(denuncia);
	}
}
