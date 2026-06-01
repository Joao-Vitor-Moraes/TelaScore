package com.requisitos.avaliacaofilmes.TelaScore.infraestrutura.config;

import com.requisitos.avaliacaofilmes.TelaScore.aplicacao.identidade.GeradorId;

import jakarta.persistence.EntityManager;

public class GeradorIdImpl implements GeradorId {

    private int proximoId(String jpql) {
        EntityManager em = ConexaoBanco.obterEntityManager();
        try {
            Integer max = (Integer) em.createQuery(jpql).getSingleResult();
            return max == null ? 1 : max + 1;
        } finally {
            em.close();
        }
    }

    @Override
    public int gerarProximoIdLista() {
        return proximoId("SELECT MAX(l.id) FROM ListaEntity l");
    }

    @Override
    public int gerarProximoIdSolicitacao() {
        return proximoId("SELECT MAX(s.id) FROM SolicitacaoFilmeEntity s");
    }

    @Override
    public int gerarProximoIdAvaliacao() {
        return proximoId("SELECT MAX(a.id) FROM AvaliacaoEntity a");
    }

    @Override
    public int gerarProximoIdMeta() {
        return proximoId("SELECT MAX(m.id) FROM MetaEntity m");
    }

    @Override
    public int gerarProximoIdFilme() {
        throw new UnsupportedOperationException("Geração de ID de filme não implementada neste módulo");
    }

    @Override
    public int gerarProximoIdUsuario() {
        throw new UnsupportedOperationException("Geração de ID de usuário não implementada neste módulo");
    }

    @Override
    public int gerarProximoIdPerfil() {
        throw new UnsupportedOperationException("Geração de ID de perfil não implementada neste módulo");
    }

    @Override
    public int gerarProximoIdRegistroPontuacao() {
        throw new UnsupportedOperationException("Geração de ID de registro de pontuação não implementada neste módulo");
    }

    @Override
    public int gerarProximoIdEvento() {
        throw new UnsupportedOperationException("Geração de ID de evento não implementada neste módulo");
    }

    @Override
    public int gerarProximoIdNoticia() {
        throw new UnsupportedOperationException("Geração de ID de notícia não implementada neste módulo");
    }

    @Override
    public int gerarProximoIdConexao() {
        throw new UnsupportedOperationException("Geração de ID de conexão não implementada neste módulo");
    }

    @Override
    public int gerarProximoIdComunidade() {
        throw new UnsupportedOperationException("Geração de ID de comunidade não implementada neste módulo");
    }

    @Override
    public int gerarProximoIdMensagem() {
        throw new UnsupportedOperationException("Geração de ID de mensagem não implementada neste módulo");
    }

    @Override
    public int gerarProximoIdDenuncia() {
        throw new UnsupportedOperationException("Geração de ID de denúncia não implementada neste módulo");
    }

    @Override
    public int gerarProximoIdQuiz() {
        throw new UnsupportedOperationException("Geração de ID de quiz não implementada neste módulo");
    }
}
