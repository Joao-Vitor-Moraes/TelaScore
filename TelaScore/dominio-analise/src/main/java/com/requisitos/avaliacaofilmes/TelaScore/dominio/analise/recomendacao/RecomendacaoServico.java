package com.requisitos.avaliacaofilmes.TelaScore.dominio.analise.recomendacao;

import static org.apache.commons.lang3.Validate.notNull;
import java.util.List;
import com.requisitos.avaliacaofilmes.TelaScore.dominio.identidade.usuario.UsuarioId;

public class RecomendacaoServico {
	private final RecomendacaoRepositorio repositorio;

	public RecomendacaoServico(RecomendacaoRepositorio repositorio) {
		notNull(repositorio, "O repositório de recomendações não pode ser nulo");
		this.repositorio = repositorio;
	}

	public void atualizarRecomendacoes(UsuarioId usuarioId, List<Recomendacao> novasRecomendacoes) {
		notNull(usuarioId, "O id do utilizador não pode ser nulo");
		notNull(novasRecomendacoes, "A lista de novas recomendações não pode ser nula");
		
		repositorio.removerAntigasPorUsuario(usuarioId);
		
		for (Recomendacao recomendacao : novasRecomendacoes) {
			repositorio.salvar(recomendacao);
		}
	}
}