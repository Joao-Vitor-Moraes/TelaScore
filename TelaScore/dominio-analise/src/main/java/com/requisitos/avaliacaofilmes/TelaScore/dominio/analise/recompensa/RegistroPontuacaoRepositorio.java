package com.requisitos.avaliacaofilmes.TelaScore.dominio.analise.recompensa;

import java.util.List;
import com.requisitos.avaliacaofilmes.TelaScore.dominio.identidade.usuario.UsuarioId;

public interface RegistroPontuacaoRepositorio {
	void salvar(RegistroPontuacao registro);
	
	List<RegistroPontuacao> buscarPorUsuario(UsuarioId usuarioId);
	
	Integer calcularTotalPontos(UsuarioId usuarioId);
}