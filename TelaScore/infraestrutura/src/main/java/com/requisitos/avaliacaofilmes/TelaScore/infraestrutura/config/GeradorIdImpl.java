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
        return proximoIdNativo("SELECT MAX(id) FROM registro_pontuacao");
    }

    @Override
    public int gerarProximoIdEvento() {
        return proximoId("SELECT MAX(e.id) FROM EventoEntity e");
    }

    @Override
    public int gerarProximoIdCalendario() {
        return proximoId("SELECT MAX(c.id) FROM CalendarioEntity c");
    }

    @Override
    public int gerarProximoIdNoticia() {
        return proximoIdNativo("SELECT MAX(id) FROM noticia");
    }

    @Override
    public int gerarProximoIdConexao() {
        return proximoIdNativo("SELECT MAX(id) FROM conexao");
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
        return proximoIdNativo("SELECT MAX(id) FROM quiz");
    }
}
