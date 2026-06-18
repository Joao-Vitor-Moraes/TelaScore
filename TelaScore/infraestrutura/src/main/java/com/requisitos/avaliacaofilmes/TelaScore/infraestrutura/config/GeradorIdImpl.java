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

    private int proximoIdNativo(String sql) {
        EntityManager em = ConexaoBanco.obterEntityManager();
        try {
            Number max = (Number) em.createNativeQuery(sql).getSingleResult();
            return max == null ? 1 : max.intValue() + 1;
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
    public int gerarProximoIdRecomendacao() {
        return proximoId("SELECT MAX(r.id) FROM RecomendacaoEntity r");
    }

    @Override
    public int gerarProximoIdUsuario() {
        return proximoIdNativo("SELECT MAX(id) FROM usuario");
    }

    @Override
    public int gerarProximoIdFilme() {
        return proximoIdNativo("SELECT MAX(CAST(id AS UNSIGNED)) FROM filme");
    }

    @Override
    public int gerarProximoIdRegistroPontuacao() {
        throw new UnsupportedOperationException("Geracao de ID de registro de pontuacao nao implementada neste modulo");
    }

    @Override
    public int gerarProximoIdEvento() {
        return proximoId("SELECT MAX(e.id) FROM EventoEntity e");
    }

    @Override
    public int gerarProximoIdNoticia() {
        throw new UnsupportedOperationException("Geracao de ID de noticia nao implementada neste modulo");
    }

    @Override
    public int gerarProximoIdConexao() {
        throw new UnsupportedOperationException("Geracao de ID de conexao nao implementada neste modulo");
    }

    @Override
    public int gerarProximoIdComunidade() {
        throw new UnsupportedOperationException("Geracao de ID de comunidade nao implementada neste modulo");
    }

    @Override
    public int gerarProximoIdMensagem() {
        throw new UnsupportedOperationException("Geracao de ID de mensagem nao implementada neste modulo");
    }

    @Override
    public int gerarProximoIdDenuncia() {
        return proximoIdNativo("SELECT MAX(id) FROM denuncia");
    }

    @Override
    public int gerarProximoIdQuiz() {
        throw new UnsupportedOperationException("Geracao de ID de quiz nao implementada neste modulo");
    }
}
