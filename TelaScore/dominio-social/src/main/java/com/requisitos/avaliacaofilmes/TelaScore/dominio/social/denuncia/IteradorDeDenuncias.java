package com.requisitos.avaliacaofilmes.TelaScore.dominio.social.denuncia;

import java.util.Iterator;

public interface IteradorDeDenuncias extends Iterator<Denuncia> {
	boolean temProxima();
	Denuncia proxima();

	@Override
	default boolean hasNext() {
		return temProxima();
	}

	@Override
	default Denuncia next() {
		return proxima();
	}
}
