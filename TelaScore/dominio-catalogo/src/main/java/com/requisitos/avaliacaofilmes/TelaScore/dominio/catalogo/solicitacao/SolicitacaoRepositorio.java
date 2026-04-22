package com.requisitos.avaliacaofilmes.TelaScore.dominio.catalogo.solicitacao;

import java.util.List;
import com.requisitos.avaliacaofilmes.TelaScore.dominio.identidade.usuario.UsuarioId;

public interface SolicitacaoRepositorio {
	void salvar(SolicitacaoFilme solicitacao);
	SolicitacaoFilme obter(SolicitacaoId id);
	List<SolicitacaoFilme> pesquisarPorStatus(StatusSolicitacao status);
	List<SolicitacaoFilme> pesquisarPorSolicitante(UsuarioId solicitanteId);
}