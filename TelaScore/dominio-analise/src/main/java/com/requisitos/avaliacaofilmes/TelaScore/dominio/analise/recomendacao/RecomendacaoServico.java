package com.requisitos.avaliacaofilmes.TelaScore.dominio.analise.recomendacao;

import static org.apache.commons.lang3.Validate.notNull;
import static org.apache.commons.lang3.Validate.isTrue;
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

    public void enviarParaAmigo(UsuarioId remetenteId, UsuarioId destinatarioId, String conteudoId, TipoConteudo tipoConteudo, String mensagem) {
        notNull(remetenteId, "O remetente não pode ser nulo");
        notNull(destinatarioId, "O destinatário não pode ser nulo");
        notNull(conteudoId, "O conteúdo não pode ser nulo");
        notNull(tipoConteudo, "O tipo não pode ser nulo");
        
        isTrue(!remetenteId.equals(destinatarioId), "Você não pode enviar uma recomendação para si mesmo");
        
        Recomendacao recomendacaoSocial = new Recomendacao(
            null, 
            destinatarioId,
            conteudoId,
            tipoConteudo,
            100.0,
            remetenteId,
            mensagem
        );

        repositorio.salvar(recomendacaoSocial);
    }

    public void marcarComoVisualizada(Recomendacao recomendacao) {
        notNull(recomendacao, "A recomendação não pode ser nula");
        
        recomendacao.marcarComoVisualizada();
        repositorio.salvar(recomendacao);
    }
    
    public void reagirARecomendacao(Recomendacao recomendacao, boolean aceitou) {
        notNull(recomendacao, "A recomendação não pode ser nula");
        
        if (aceitou) {
            recomendacao.aceitar();
        } else {
            recomendacao.rejeitar();
        }
        
        repositorio.salvar(recomendacao);
    }
}