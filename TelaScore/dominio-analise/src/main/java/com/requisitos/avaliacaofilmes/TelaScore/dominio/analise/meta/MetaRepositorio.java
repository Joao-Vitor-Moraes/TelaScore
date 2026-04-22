package com.requisitos.avaliacaofilmes.TelaScore.dominio.analise.meta;

import java.util.List;
import com.requisitos.avaliacaofilmes.TelaScore.dominio.identidade.usuario.UsuarioId;

public interface MetaRepositorio {
	void salvar(Meta meta);
	
	Meta obter(MetaId id);
	
	List<Meta> buscarPorUsuario(UsuarioId usuarioId);
}