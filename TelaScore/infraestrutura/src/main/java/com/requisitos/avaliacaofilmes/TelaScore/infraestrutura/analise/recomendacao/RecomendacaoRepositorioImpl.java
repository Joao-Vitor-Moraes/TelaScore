package com.requisitos.avaliacaofilmes.TelaScore.infraestrutura.analise.recomendacao;

import java.util.ArrayList;
import java.util.List;

import com.requisitos.avaliacaofilmes.TelaScore.dominio.analise.recomendacao.Recomendacao;
import com.requisitos.avaliacaofilmes.TelaScore.dominio.analise.recomendacao.RecomendacaoId;
import com.requisitos.avaliacaofilmes.TelaScore.dominio.analise.recomendacao.RecomendacaoRepositorio;
import com.requisitos.avaliacaofilmes.TelaScore.dominio.analise.recomendacao.StatusRecomendacao;
import com.requisitos.avaliacaofilmes.TelaScore.dominio.analise.recomendacao.TipoConteudo;
import com.requisitos.avaliacaofilmes.TelaScore.dominio.identidade.usuario.UsuarioId;
import com.requisitos.avaliacaofilmes.TelaScore.infraestrutura.analise.recomendacao.entidades.RecomendacaoEntity;
import com.requisitos.avaliacaofilmes.TelaScore.infraestrutura.config.ConexaoBanco;

import jakarta.persistence.EntityManager;
import jakarta.persistence.EntityTransaction;

public class RecomendacaoRepositorioImpl implements RecomendacaoRepositorio {

    @Override
    public void salvar(Recomendacao recomendacao) {
        EntityManager em = ConexaoBanco.obterEntityManager();
        EntityTransaction tx = em.getTransaction();

        try {
            tx.begin();
            RecomendacaoEntity entity = em.find(RecomendacaoEntity.class, recomendacao.getId().getId());
            if (entity == null) {
                entity = new RecomendacaoEntity();
                entity.setId(recomendacao.getId().getId());
            }

            entity.setUsuarioId(recomendacao.getUsuarioId().getId());
            entity.setConteudoId(recomendacao.getConteudoId());
            entity.setTipoConteudo(recomendacao.getTipoConteudo().name());
            entity.setPontuacaoCompatibilidade(recomendacao.getPontuacaoCompatibilidade());
            entity.setRemetenteId(recomendacao.getRemetenteId() != null ? recomendacao.getRemetenteId().getId() : null);
            entity.setMensagem(recomendacao.getMensagem());
            entity.setDataGeracao(recomendacao.getDataGeracao());
            entity.setStatus(recomendacao.getStatus().name());
            entity.setComentarioResposta(recomendacao.getComentarioResposta());
            entity.setNotaPosterior(recomendacao.getNotaPosterior());
            entity.setAvaliacaoPosterior(recomendacao.getAvaliacaoPosterior());

            if (!em.contains(entity)) {
                em.persist(entity);
            } else {
                em.merge(entity);
            }
            tx.commit();
        } catch (Exception e) {
            if (tx.isActive()) tx.rollback();
            throw new RuntimeException("Erro ao salvar recomendação no banco.", e);
        } finally {
            em.close();
        }
    }

    @Override
    public void removerAntigasPorUsuario(UsuarioId usuarioId) {
        EntityManager em = ConexaoBanco.obterEntityManager();
        EntityTransaction tx = em.getTransaction();

        try {
            tx.begin();
            em.createQuery("DELETE FROM RecomendacaoEntity r WHERE r.usuarioId = :uid")
              .setParameter("uid", usuarioId.getId())
              .executeUpdate();
            tx.commit();
        } catch (Exception e) {
            if (tx.isActive()) tx.rollback();
            throw new RuntimeException("Erro ao limpar histórico de recomendações.", e);
        } finally {
            em.close();
        }
    }

    @Override
    public Recomendacao obter(RecomendacaoId id) {
        EntityManager em = ConexaoBanco.obterEntityManager();
        try {
            RecomendacaoEntity entity = em.find(RecomendacaoEntity.class, id.getId());
            if (entity == null) return null;
            return mapearParaDominio(entity);
        } finally {
            em.close();
        }
    }

    @Override
    public List<Recomendacao> buscarTopRecomendacoesPorUsuario(UsuarioId usuarioId, int limite) {
        EntityManager em = ConexaoBanco.obterEntityManager();
        try {
            List<RecomendacaoEntity> entities = em.createQuery(
                    "SELECT r FROM RecomendacaoEntity r WHERE r.usuarioId = :uid "
                    + "ORDER BY CASE WHEN r.status = 'PENDENTE' THEN 0 ELSE 1 END, r.dataGeracao DESC",
                    RecomendacaoEntity.class)
                    .setParameter("uid", usuarioId.getId())
                    .setMaxResults(limite)
                    .getResultList();

            List<Recomendacao> recomendacoes = new ArrayList<>();
            for (RecomendacaoEntity entity : entities) {
                recomendacoes.add(mapearParaDominio(entity));
            }
            return recomendacoes;
        } finally {
            em.close();
        }
    }

    @Override
    public List<Recomendacao> buscarRecomendacoesSociaisPorUsuario(UsuarioId usuarioId) {
        EntityManager em = ConexaoBanco.obterEntityManager();
        try {
            List<RecomendacaoEntity> entities = em.createQuery(
                    "SELECT r FROM RecomendacaoEntity r WHERE r.usuarioId = :uid AND r.remetenteId IS NOT NULL "
                    + "ORDER BY CASE WHEN r.status = 'PENDENTE' THEN 0 ELSE 1 END, r.dataGeracao DESC",
                    RecomendacaoEntity.class)
                    .setParameter("uid", usuarioId.getId())
                    .getResultList();

            List<Recomendacao> recomendacoes = new ArrayList<>();
            for (RecomendacaoEntity entity : entities) {
                recomendacoes.add(mapearParaDominio(entity));
            }
            return recomendacoes;
        } finally {
            em.close();
        }
    }

    @Override
    public List<Recomendacao> buscarPendentesPorUsuario(UsuarioId usuarioId) {
        EntityManager em = ConexaoBanco.obterEntityManager();
        try {
            List<RecomendacaoEntity> entities = em.createQuery(
                    "SELECT r FROM RecomendacaoEntity r WHERE r.usuarioId = :uid AND r.status = :status", RecomendacaoEntity.class)
                    .setParameter("uid", usuarioId.getId())
                    .setParameter("status", StatusRecomendacao.PENDENTE.name())
                    .getResultList();

            List<Recomendacao> recomendacoes = new ArrayList<>();
            for (RecomendacaoEntity entity : entities) {
                recomendacoes.add(mapearParaDominio(entity));
            }
            return recomendacoes;
        } finally {
            em.close();
        }
    }

    @Override
    public List<Recomendacao> buscarEnviadasPorUsuario(UsuarioId remetenteId) {
        EntityManager em = ConexaoBanco.obterEntityManager();
        try {
            return em.createQuery(
                    "SELECT r FROM RecomendacaoEntity r WHERE r.remetenteId = :uid ORDER BY r.dataGeracao DESC",
                    RecomendacaoEntity.class)
                    .setParameter("uid", remetenteId.getId())
                    .getResultList()
                    .stream()
                    .map(this::mapearParaDominio)
                    .toList();
        } finally {
            em.close();
        }
    }

    private Recomendacao mapearParaDominio(RecomendacaoEntity entity) {
        UsuarioId remetenteId = entity.getRemetenteId() != null ? new UsuarioId(entity.getRemetenteId()) : null;

        return new Recomendacao(
            new RecomendacaoId(entity.getId()),
            new UsuarioId(entity.getUsuarioId()),
            entity.getConteudoId(),
            TipoConteudo.valueOf(entity.getTipoConteudo()),
            entity.getPontuacaoCompatibilidade(),
            remetenteId,
            entity.getMensagem(),
            entity.getDataGeracao(),
            StatusRecomendacao.valueOf(entity.getStatus()),
            entity.getComentarioResposta(),
            entity.getNotaPosterior(),
            entity.getAvaliacaoPosterior()
        );
    }
}
