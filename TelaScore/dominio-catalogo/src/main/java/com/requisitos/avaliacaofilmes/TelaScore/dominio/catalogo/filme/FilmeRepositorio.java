package com.requisitos.avaliacaofilmes.TelaScore.dominio.catalogo.filme;



import java.util.List;

public interface FilmeRepositorio {
	void salvar(Filme filme);
	Filme obter(FilmeId id);
	void remover(FilmeId id);
	boolean existeComTitulo(String titulo);
	List<Filme> listarTodos();

	
}