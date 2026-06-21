package com.requisitos.avaliacaofilmes.TelaScore.infraestrutura.analise.meta;

import java.util.ArrayList;
import java.util.List;

import com.requisitos.avaliacaofilmes.TelaScore.dominio.analise.meta.Meta;
import com.requisitos.avaliacaofilmes.TelaScore.dominio.analise.meta.MetaId;
import com.requisitos.avaliacaofilmes.TelaScore.dominio.analise.meta.MetaRepositorio;
import com.requisitos.avaliacaofilmes.TelaScore.dominio.analise.meta.StatusMeta;
import com.requisitos.avaliacaofilmes.TelaScore.dominio.analise.meta.TipoMeta;
import com.requisitos.avaliacaofilmes.TelaScore.dominio.identidade.usuario.UsuarioId;
import com.requisitos.avaliacaofilmes.TelaScore.infraestrutura.analise.meta.entidades.MetaEntity;
import com.requisitos.avaliacaofilmes.TelaScore.infraestrutura.config.ConexaoBanco;

import jakarta.persistence.EntityManager;
import jakarta.persistence.EntityTransaction;

public class MetaRepositorioImpl implements MetaRepositorio {

    @Override
    public void remover(MetaId id) {
        EntityManager em = ConexaoBanco.obterEntityManager();
        EntityTransaction tx = em.getTransaction();
        try {
            tx.begin();
            MetaEntity entity = em.find(MetaEntity.class, id.getId());
            if (entity != null) em.remove(entity);
            tx.commit();
        } catch (Exception e) {
            if (tx.isActive()) tx.rollback();
            throw new RuntimeException("Erro ao remover meta do banco.", e);
        } finally {
            em.close();
        }
    }

    @Override
    public void salvar(Meta meta) {
        EntityManager em = ConexaoBanco.obterEntityManager();
        EntityTransaction tx = em.getTransaction();

        try {
            tx.begin();
            MetaEntity entity = em.find(MetaEntity.class, meta.getId().getId());
            
            if (entity == null) {
                entity = new MetaEntity();
                entity.setId(meta.getId().getId());
            }

            entity.setUsuarioId(meta.getUsuarioId().getId());
            entity.setTitulo(meta.getTitulo());
            entity.setQuantidadeAlvo(meta.getQuantidadeAlvo());
            entity.setQuantidadeAtual(meta.getQuantidadeAtual());
            entity.setDataPrazo(meta.getDataPrazo());
            entity.setStatus(meta.getStatus().name());
            entity.setTipo(meta.getTipo().name());
            entity.setGeneroAlvo(meta.getGeneroAlvo());
            entity.setMetaSistemaId(meta.getMetaSistemaId());
            entity.setPontosConcedidos(meta.isPontosConcedidos());
            entity.setNotificacaoAtiva(meta.isNotificacaoAtiva());

            if (!em.contains(entity)) {
                em.persist(entity);
            } else {
                em.merge(entity);
            }
            tx.commit();
        } catch (Exception e) {
            if (tx.isActive()) tx.rollback();
            throw new RuntimeException("Erro ao salvar meta no banco.", e);
        } finally {
            em.close();
        }
    }

    @Override
    public Meta obter(MetaId id) {
        EntityManager em = ConexaoBanco.obterEntityManager();
        try {
            MetaEntity entity = em.find(MetaEntity.class, id.getId());
            if (entity == null) {
                return null;
            }
            return mapearParaDominio(entity);
        } finally {
            em.close();
        }
    }

    @Override
    public List<Meta> buscarPorUsuario(UsuarioId usuarioId) {
        EntityManager em = ConexaoBanco.obterEntityManager();
        try {
            List<MetaEntity> entities = em.createQuery(
                    "SELECT m FROM MetaEntity m WHERE m.usuarioId = :uid", MetaEntity.class)
                    .setParameter("uid", usuarioId.getId())
                    .getResultList();
            
            List<Meta> metas = new ArrayList<>();
            for (MetaEntity entity : entities) {
                metas.add(mapearParaDominio(entity));
            }
            return metas;
        } finally {
            em.close();
        }
    }

    @Override
    public List<Meta> buscarEmAndamentoPorUsuario(UsuarioId usuarioId) {
        EntityManager em = ConexaoBanco.obterEntityManager();
        try {
            List<MetaEntity> entities = em.createQuery(
                    "SELECT m FROM MetaEntity m WHERE m.usuarioId = :uid AND m.status = :status", MetaEntity.class)
                    .setParameter("uid", usuarioId.getId())
                    .setParameter("status", StatusMeta.EM_ANDAMENTO.name())
                    .getResultList();
            
            List<Meta> metas = new ArrayList<>();
            for (MetaEntity entity : entities) {
                metas.add(mapearParaDominio(entity));
            }
            return metas;
        } finally {
            em.close();
        }
    }

    private Meta mapearParaDominio(MetaEntity entity) {
        return new Meta(
            new MetaId(entity.getId()),
            new UsuarioId(entity.getUsuarioId()),
            entity.getTitulo(),
            entity.getQuantidadeAlvo(),
            entity.getQuantidadeAtual(),
            entity.getDataPrazo(),
            StatusMeta.valueOf(entity.getStatus()),
            entity.getMetaSistemaId(),
            Boolean.TRUE.equals(entity.getPontosConcedidos()),
            converterTipo(entity.getTipo()),
            entity.getGeneroAlvo(),
            entity.getNotificacaoAtiva() == null || Boolean.TRUE.equals(entity.getNotificacaoAtiva())
        );
    }

    private TipoMeta converterTipo(String tipo) {
        if (tipo == null || tipo.isBlank()) {
            return TipoMeta.FILMES;
        }
        try {
            return TipoMeta.valueOf(tipo.trim().toUpperCase());
        } catch (IllegalArgumentException e) {
            return TipoMeta.FILMES;
        }
    }
}
