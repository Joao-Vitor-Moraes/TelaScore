package com.requisitos.avaliacaofilmes.TelaScore.dominio.analise.recomendacao;

import java.util.List;
import com.requisitos.avaliacaofilmes.TelaScore.dominio.identidade.usuario.UsuarioId;

public interface RecomendacaoRepositorio {
    void salvar(Recomendacao recomendacao);
    Recomendacao obter(RecomendacaoId id);
    void removerAntigasPorUsuario(UsuarioId usuarioId); 
    List<Recomendacao> buscarTopRecomendacoesPorUsuario(UsuarioId usuarioId, int limite);
    List<Recomendacao> buscarRecomendacoesSociaisPorUsuario(UsuarioId usuarioId);
    List<Recomendacao> buscarPendentesPorUsuario(UsuarioId usuarioId);
}