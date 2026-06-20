package com.requisitos.avaliacaofilmes.TelaScore.dominio.social.denuncia;

import java.util.List;

public class ColecaoDenuncias {
	private final List<Denuncia> denuncias;

	public ColecaoDenuncias(List<Denuncia> denuncias) {
		this.denuncias = List.copyOf(denuncias);
	}

	public IteradorDeDenuncias criarIterador() {
		return new IteradorSequencialDeDenuncias(denuncias);
	}
}
