package com.requisitos.avaliacaofilmes.TelaScore.aplicacao.social.denuncia;

import java.util.ArrayList;
import java.util.List;

import com.requisitos.avaliacaofilmes.TelaScore.dominio.social.denuncia.ColecaoDenuncias;
import com.requisitos.avaliacaofilmes.TelaScore.dominio.social.denuncia.Denuncia;
import com.requisitos.avaliacaofilmes.TelaScore.dominio.social.denuncia.IteradorDeDenuncias;

class MapeadorDenunciasComIterador {
	private MapeadorDenunciasComIterador() {
	}

	static List<DenunciaResumo> paraResumo(List<Denuncia> denuncias) {
		IteradorDeDenuncias iterador = new ColecaoDenuncias(denuncias).criarIterador();
		List<DenunciaResumo> resumos = new ArrayList<>();

		while (iterador.temProxima()) {
			resumos.add(DenunciaResumo.de(iterador.proxima()));
		}

		return resumos;
	}
}
