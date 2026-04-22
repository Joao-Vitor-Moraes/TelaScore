package com.requisitos.avaliacaofilmes.TelaScore.dominio.catalogo.diretor;

public interface DiretorRepositorio {
	void salvar(Diretor diretor);
	Diretor obter(DiretorId id);
}