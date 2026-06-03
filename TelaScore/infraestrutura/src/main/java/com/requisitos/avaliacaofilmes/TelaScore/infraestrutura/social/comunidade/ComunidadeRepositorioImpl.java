package com.requisitos.avaliacaofilmes.TelaScore.infraestrutura.social.comunidade;

import java.util.ArrayList;
import java.util.List;

import com.requisitos.avaliacaofilmes.TelaScore.dominio.identidade.usuario.UsuarioId;
import com.requisitos.avaliacaofilmes.TelaScore.dominio.social.comunidade.*;
import com.requisitos.avaliacaofilmes.TelaScore.infraestrutura.social.comunidade.entidades.ComunidadeEntity;
import com.requisitos.avaliacaofilmes.TelaScore.infraestrutura.social.comunidade.entidades.MembroComunidadeEntity;
import com.requisitos.avaliacaofilmes.TelaScore.infraestrutura.config.ConexaoBanco;

import jakarta.persistence.EntityManager;
import jakarta.persistence.EntityTransaction;

public class ComunidadeRepositorioImpl implements ComunidadeRepositorio {

    @Override
    public void salvarComunidade(Comunidade comunidade) {
        EntityManager em = ConexaoBanco.obterEntityManager();
        EntityTransaction tx = em.getTransaction();
        try {
            tx.begin();
            ComunidadeEntity entity = em.find(ComunidadeEntity.class, comunidade.getId().getId());

            if (entity == null) {
                entity = new ComunidadeEntity();
                entity.setId(comunidade.getId().getId());
            }
            entity.setNome(comunidade.getNome());
            entity.setDescricao(comunidade.getDescricao());

            if (!em.contains(entity)) {
                em.persist(entity);
            } else {
                em.merge(entity);
            }
            tx.commit();
        } catch (Exception e) {
            if (tx.isActive()) tx.rollback();
            throw new RuntimeException("Erro ao salvar comunidade no banco.", e);
        } finally {
            em.close();
        }
    }

    @Override
    public void salvarMembro(MembroComunidade membro) {
        EntityManager em = ConexaoBanco.obterEntityManager();
        EntityTransaction tx = em.getTransaction();
        try {
            tx.begin();
            MembroComunidadeEntity entity = new MembroComunidadeEntity();
            entity.setComunidadeId(membro.getComunidadeId().getId());
            entity.setUsuarioId(membro.getUsuarioId().getId());
            entity.setPapel(membro.getPapel().name());

            em.persist(entity);
            tx.commit();
        } catch (Exception e) {
            if (tx.isActive()) tx.rollback();
            throw new RuntimeException("Erro ao salvar membro da comunidade.", e);
        } finally {
            em.close();
        }
    }

    @Override
    public Comunidade obterComunidade(ComunidadeId id) {
        EntityManager em = ConexaoBanco.obterEntityManager();
        try {
            ComunidadeEntity entity = em.find(ComunidadeEntity.class, id.getId());
            if (entity == null) return null;
            return mapearParaDominio(entity);
        } finally {
            em.close();
        }
    }

    @Override
    public void removerMembro(ComunidadeId cid, UsuarioId uid) {
        EntityManager em = ConexaoBanco.obterEntityManager();
        EntityTransaction tx = em.getTransaction();
        try {
            tx.begin();
            em.createQuery("DELETE FROM MembroComunidadeEntity m WHERE m.comunidadeId = :cid AND m.usuarioId = :uid")
                    .setParameter("cid", cid.getId())
                    .setParameter("uid", uid.getId())
                    .executeUpdate();
            tx.commit();
        } catch (Exception e) {
            if (tx.isActive()) tx.rollback();
            throw new RuntimeException("Erro ao remover membro da comunidade.", e);
        } finally {
            em.close();
        }
    }

    @Override
    public List<Comunidade> buscarComunidadesDoUsuario(UsuarioId uid) {
        EntityManager em = ConexaoBanco.obterEntityManager();
        try {
            List<ComunidadeEntity> entities = em.createQuery(
                            "SELECT c FROM ComunidadeEntity c WHERE c.id IN " +
                                    "(SELECT m.comunidadeId FROM MembroComunidadeEntity m WHERE m.usuarioId = :uid)", ComunidadeEntity.class)
                    .setParameter("uid", uid.getId())
                    .getResultList();

            List<Comunidade> comunidades = new ArrayList<>();
            for (ComunidadeEntity entity : entities) {
                comunidades.add(mapearParaDominio(entity));
            }
            return comunidades;
        } finally {
            em.close();
        }
    }

    @Override
    public List<MembroComunidade> buscarMembrosDaComunidade(ComunidadeId cid) {
        EntityManager em = ConexaoBanco.obterEntityManager();
        try {
            List<MembroComunidadeEntity> entities = em.createQuery(
                            "SELECT m FROM MembroComunidadeEntity m WHERE m.comunidadeId = :cid", MembroComunidadeEntity.class)
                    .setParameter("cid", cid.getId())
                    .getResultList();

            List<MembroComunidade> membros = new ArrayList<>();
            for (MembroComunidadeEntity entity : entities) {
                membros.add(mapearParaMembroDominio(entity));
            }
            return membros;
        } finally {
            em.close();
        }
    }

    private Comunidade mapearParaDominio(ComunidadeEntity entity) {
        return new Comunidade(
                new ComunidadeId(entity.getId()),
                entity.getNome(),
                entity.getDescricao()
        );
    }

    private MembroComunidade mapearParaMembroDominio(MembroComunidadeEntity entity) {
        return new MembroComunidade(
                new ComunidadeId(entity.getComunidadeId()),
                new UsuarioId(entity.getUsuarioId()),
                PapelComunidade.valueOf(entity.getPapel())
        );
    }
}