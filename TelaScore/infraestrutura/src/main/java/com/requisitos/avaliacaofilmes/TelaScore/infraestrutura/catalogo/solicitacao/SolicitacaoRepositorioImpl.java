package com.requisitos.avaliacaofilmes.TelaScore.infraestrutura.catalogo.solicitacao;

import java.util.ArrayList;
import java.util.List;

import com.requisitos.avaliacaofilmes.TelaScore.dominio.catalogo.solicitacao.SolicitacaoFilme;
import com.requisitos.avaliacaofilmes.TelaScore.dominio.catalogo.solicitacao.SolicitacaoId;
import com.requisitos.avaliacaofilmes.TelaScore.dominio.catalogo.solicitacao.SolicitacaoRepositorio;
import com.requisitos.avaliacaofilmes.TelaScore.dominio.catalogo.solicitacao.StatusSolicitacao;
import com.requisitos.avaliacaofilmes.TelaScore.dominio.identidade.usuario.UsuarioId;
import com.requisitos.avaliacaofilmes.TelaScore.infraestrutura.catalogo.solicitacao.entidades.SolicitacaoFilmeEntity;
import com.requisitos.avaliacaofilmes.TelaScore.infraestrutura.config.ConexaoBanco;

import jakarta.persistence.EntityManager;
import jakarta.persistence.EntityTransaction;

public class SolicitacaoRepositorioImpl implements SolicitacaoRepositorio {

    @Override
    public void salvar(SolicitacaoFilme solicitacao) {
        EntityManager em = ConexaoBanco.obterEntityManager();
        EntityTransaction tx = em.getTransaction();
        
        try {
            tx.begin();
            
            SolicitacaoFilmeEntity entity = em.find(SolicitacaoFilmeEntity.class, solicitacao.getId().getId());
            if (entity == null) {
                entity = new SolicitacaoFilmeEntity();
                entity.setId(solicitacao.getId().getId());
                entity.setSolicitanteId(solicitacao.getSolicitanteId().getId());
                entity.setDataCriacao(solicitacao.getDataCriacao());
            }

            entity.setTituloSugerido(solicitacao.getTituloSugerido());
            entity.setJustificativa(solicitacao.getJustificativa());
            entity.setPais(solicitacao.getPais());
            entity.setAno(solicitacao.getAno());
            entity.setGenero(solicitacao.getGenero());
            entity.setFotoUrl(solicitacao.getFotoUrl());
            entity.setStatus(solicitacao.getStatus().name());
            entity.setFeedbackAdmin(solicitacao.getFeedbackAdmin());
            
            em.persist(entity);
            
            tx.commit();
        } catch (Exception e) {
            if (tx.isActive()) {
                tx.rollback();
            }
            throw new RuntimeException("Erro ao salvar solicitação de filme no banco de dados", e);
        } finally {
            em.close();
        }
    }

    @Override
    public SolicitacaoFilme obter(SolicitacaoId id) {
        EntityManager em = ConexaoBanco.obterEntityManager();
        try {
            SolicitacaoFilmeEntity entity = em.find(SolicitacaoFilmeEntity.class, id.getId());
            if (entity != null) {
                return mapearParaDominio(entity);
            }
            return null;
        } finally {
            em.close();
        }
    }

    @Override
    public List<SolicitacaoFilme> pesquisarPorStatus(StatusSolicitacao status) {
        EntityManager em = ConexaoBanco.obterEntityManager();
        try {
            List<SolicitacaoFilmeEntity> entities = em.createQuery("SELECT s FROM SolicitacaoFilmeEntity s WHERE s.status = :status", SolicitacaoFilmeEntity.class)
                    .setParameter("status", status.name())
                    .getResultList();
            
            List<SolicitacaoFilme> solicitacoes = new ArrayList<>();
            for (SolicitacaoFilmeEntity entity : entities) {
                solicitacoes.add(mapearParaDominio(entity));
            }
            return solicitacoes;
        } finally {
            em.close();
        }
    }

    @Override
    public List<SolicitacaoFilme> pesquisarPorSolicitante(UsuarioId solicitanteId) {
        EntityManager em = ConexaoBanco.obterEntityManager();
        try {
            List<SolicitacaoFilmeEntity> entities = em.createQuery("SELECT s FROM SolicitacaoFilmeEntity s WHERE s.solicitanteId = :solicitanteId", SolicitacaoFilmeEntity.class)
                    .setParameter("solicitanteId", solicitanteId.getId())
                    .getResultList();
            
            List<SolicitacaoFilme> solicitacoes = new ArrayList<>();
            for (SolicitacaoFilmeEntity entity : entities) {
                solicitacoes.add(mapearParaDominio(entity));
            }
            return solicitacoes;
        } finally {
            em.close();
        }
    }

    private SolicitacaoFilme mapearParaDominio(SolicitacaoFilmeEntity entity) {
        SolicitacaoId id = new SolicitacaoId(entity.getId());
        UsuarioId solicitanteId = new UsuarioId(entity.getSolicitanteId());
        StatusSolicitacao status = StatusSolicitacao.valueOf(entity.getStatus());
        
        return SolicitacaoFilme.restaurar(id, solicitanteId, entity.getTituloSugerido(),
                entity.getJustificativa(), entity.getPais(), entity.getAno(), entity.getGenero(), entity.getFotoUrl(),
                status, entity.getDataCriacao(), entity.getFeedbackAdmin());
    }
}
