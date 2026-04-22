package com.requisitos.avaliacaofilmes.TelaScore.dominio.analise.meta;

import static org.apache.commons.lang3.Validate.notNull;

public class MetaServico {
	private final MetaRepositorio repositorio;

	public MetaServico(MetaRepositorio repositorio) {
		notNull(repositorio, "O repositório de metas não pode ser nulo");
		this.repositorio = repositorio;
	}

	public void criarMeta(Meta meta) {
		notNull(meta, "A meta não pode ser nula");
		repositorio.salvar(meta);
	}
	
	public void atualizarProgresso(Meta meta, int quantidade) {
		notNull(meta, "A meta não pode ser nula");
		
		meta.adicionarProgresso(quantidade);
		repositorio.salvar(meta);
	}
}