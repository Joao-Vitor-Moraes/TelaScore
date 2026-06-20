package com.requisitos.avaliacaofilmes.TelaScore.dominio.social.denuncia;

import java.util.List;
import java.util.NoSuchElementException;

class IteradorSequencialDeDenuncias implements IteradorDeDenuncias {
	private final List<Denuncia> denuncias;
	private int indiceAtual;

	IteradorSequencialDeDenuncias(List<Denuncia> denuncias) {
		this.denuncias = List.copyOf(denuncias);
	}

	@Override
	public boolean temProxima() {
		return indiceAtual < denuncias.size();
	}

	@Override
	public Denuncia proxima() {
		if (!temProxima()) {
			throw new NoSuchElementException("Nao ha mais denuncias para percorrer");
		}
		return denuncias.get(indiceAtual++);
	}
}
