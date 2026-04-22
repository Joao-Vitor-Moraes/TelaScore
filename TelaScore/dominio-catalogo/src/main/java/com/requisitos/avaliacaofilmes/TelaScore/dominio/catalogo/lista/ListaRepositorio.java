package com.requisitos.avaliacaofilmes.TelaScore.dominio.catalogo.lista;

import java.util.List;
import com.requisitos.avaliacaofilmes.TelaScore.dominio.identidade.usuario.UsuarioId;

public interface ListaRepositorio {
	void salvar(Lista lista);
	Lista obter(ListaId id);
	void remover(ListaId id);
	
	List<Lista> pesquisarPorDono(UsuarioId donoId);
}