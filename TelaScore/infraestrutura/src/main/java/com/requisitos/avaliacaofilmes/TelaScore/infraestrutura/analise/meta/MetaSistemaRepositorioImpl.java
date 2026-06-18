package com.requisitos.avaliacaofilmes.TelaScore.infraestrutura.analise.meta;

import java.util.List;

import com.requisitos.avaliacaofilmes.TelaScore.dominio.analise.meta.MetaSistema;
import com.requisitos.avaliacaofilmes.TelaScore.dominio.analise.meta.MetaSistemaRepositorio;
import com.requisitos.avaliacaofilmes.TelaScore.infraestrutura.analise.meta.entidades.MetaSistemaEntity;
import com.requisitos.avaliacaofilmes.TelaScore.infraestrutura.config.ConexaoBanco;

import jakarta.persistence.EntityManager;
import jakarta.persistence.EntityTransaction;

public class MetaSistemaRepositorioImpl implements MetaSistemaRepositorio {
    @Override
    public void salvar(MetaSistema meta) {
        EntityManager em = ConexaoBanco.obterEntityManager();
        EntityTransaction tx = em.getTransaction();
        try {
            tx.begin();
            MetaSistemaEntity entity = em.find(MetaSistemaEntity.class, meta.getId());
            if (entity == null) entity = new MetaSistemaEntity();
            entity.setId(meta.getId());
            entity.setTitulo(meta.getTitulo());
            entity.setQuantidadeAlvo(meta.getQuantidadeAlvo());
            entity.setDuracaoDias(meta.getDuracaoDias());
            entity.setCriadaPorUsuarioId(meta.getCriadaPorUsuarioId());
            entity.setAtiva(meta.isAtiva());
            em.merge(entity);
            tx.commit();
        } catch (Exception e) {
            if (tx.isActive()) tx.rollback();
            throw new RuntimeException("Erro ao salvar meta de sistema.", e);
        } finally {
            em.close();
        }
    }

    @Override
    public List<MetaSistema> listarAtivas() {
        EntityManager em = ConexaoBanco.obterEntityManager();
        try {
            return em.createQuery(
                    "SELECT m FROM MetaSistemaEntity m WHERE m.ativa = true ORDER BY m.id",
                    MetaSistemaEntity.class)
                    .getResultList().stream()
                    .map(e -> new MetaSistema(e.getId(), e.getTitulo(), e.getQuantidadeAlvo(),
                            e.getDuracaoDias(), e.getCriadaPorUsuarioId(), e.getAtiva()))
                    .toList();
        } finally {
            em.close();
        }
    }

    @Override
    public int proximoId() {
        EntityManager em = ConexaoBanco.obterEntityManager();
        try {
            Integer max = em.createQuery("SELECT MAX(m.id) FROM MetaSistemaEntity m", Integer.class)
                    .getSingleResult();
            return max == null ? 1 : max + 1;
        } finally {
            em.close();
        }
    }
}
