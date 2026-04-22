package com.requisitos.avaliacaofilmes.TelaScore.dominio.catalogo.filme;

public interface FilmeRepositorio {
	void salvar(Filme filme);
	Filme obter(FilmeId id);
}