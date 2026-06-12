package com.requisitos.avaliacaofilmes.TelaScore.infraestrutura.social.comunidade;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

import com.requisitos.avaliacaofilmes.TelaScore.dominio.identidade.usuario.UsuarioId;
import com.requisitos.avaliacaofilmes.TelaScore.dominio.social.comunidade.*;
import com.requisitos.avaliacaofilmes.TelaScore.infraestrutura.social.comunidade.entidades.ComunidadeEntity;
import com.requisitos.avaliacaofilmes.TelaScore.infraestrutura.social.comunidade.entidades.MembroComunidadeEntity;
import com.requisitos.avaliacaofilmes.TelaScore.infraestrutura.social.comunidade.entidades.MensagemComunidadeEntity;
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
                em.merge(entity);
            } else {
                em.persist(entity);
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

            List<MembroComunidadeEntity> resultados = em.createQuery(
                            "SELECT m FROM MembroComunidadeEntity m WHERE m.comunidadeId = :cid AND m.usuarioId = :uid",
                            MembroComunidadeEntity.class)
                    .setParameter("cid", membro.getComunidadeId().getId())
                    .setParameter("uid", membro.getUsuarioId().getId())
                    .getResultList();

            MembroComunidadeEntity entity;
            if (resultados.isEmpty()) {
                entity = new MembroComunidadeEntity();
                entity.setComunidadeId(membro.getComunidadeId().getId());
                entity.setUsuarioId(membro.getUsuarioId().getId());
            } else {
                entity = resultados.get(0);
            }

            entity.setPapel(membro.getPapel().name());

            if (!em.contains(entity)) {
                em.merge(entity);
            } else {
                em.persist(entity);
            }
            tx.commit();
        } catch (Exception e) {
            if (tx.isActive()) tx.rollback();
            throw new RuntimeException("Erro ao salvar membro da comunidade.", e);
        } finally {
            em.close();
        }
    }

    @Override
    public List<Comunidade> listarTodas() {
        EntityManager em = ConexaoBanco.obterEntityManager();
        try {
            List<ComunidadeEntity> entities = em.createQuery(
                            "SELECT c FROM ComunidadeEntity c", ComunidadeEntity.class)
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
    public List<ComunidadeUsuarioResumo> buscarComunidadesDoUsuario(UsuarioId uid) {
        EntityManager em = ConexaoBanco.obterEntityManager();
        try {
            List<MembroComunidadeEntity> membros = em.createQuery(
                            "SELECT m FROM MembroComunidadeEntity m WHERE m.usuarioId = :uid", MembroComunidadeEntity.class)
                    .setParameter("uid", uid.getId())
                    .getResultList();

            List<ComunidadeUsuarioResumo> comunidades = new ArrayList<>();
            for (MembroComunidadeEntity m : membros) {
                ComunidadeEntity c = em.find(ComunidadeEntity.class, m.getComunidadeId());
                if (c != null) {
                    comunidades.add(new ComunidadeUsuarioResumo(c.getId(), c.getNome(), c.getDescricao(), m.getPapel()));
                }
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

    @Override
    public void atualizarPapelMembro(ComunidadeId cid, UsuarioId uid, PapelComunidade novoPapel) {
        EntityManager em = ConexaoBanco.obterEntityManager();
        EntityTransaction tx = em.getTransaction();
        try {
            tx.begin();
            em.createQuery("UPDATE MembroComunidadeEntity m SET m.papel = :novoPapel WHERE m.comunidadeId = :cid AND m.usuarioId = :uid")
                    .setParameter("novoPapel", novoPapel.name())
                    .setParameter("cid", cid.getId())
                    .setParameter("uid", uid.getId())
                    .executeUpdate();
            tx.commit();
        } catch (Exception e) {
            if (tx.isActive()) tx.rollback();
            throw new RuntimeException("Erro ao atualizar papel do membro.", e);
        } finally {
            em.close();
        }
    }

    @Override
    public boolean verificarSeEhCriador(ComunidadeId cid, UsuarioId uid) {
        EntityManager em = ConexaoBanco.obterEntityManager();
        try {
            List<String> resultados = em.createQuery(
                            "SELECT m.papel FROM MembroComunidadeEntity m WHERE m.comunidadeId = :cid AND m.usuarioId = :uid", String.class)
                    .setParameter("cid", cid.getId())
                    .setParameter("uid", uid.getId())
                    .getResultList();

            if (resultados.isEmpty()) return false;
            return "CRIADOR".equalsIgnoreCase(resultados.get(0));
        } finally {
            em.close();
        }
    }

    @Override
    public boolean existeMembro(ComunidadeId cid, UsuarioId uid) {
        EntityManager em = ConexaoBanco.obterEntityManager();
        try {
            Long contagem = em.createQuery(
                            "SELECT COUNT(m) FROM MembroComunidadeEntity m WHERE m.comunidadeId = :cid AND m.usuarioId = :uid", Long.class)
                    .setParameter("cid", cid.getId())
                    .setParameter("uid", uid.getId())
                    .getSingleResult();

            return contagem > 0;
        } finally {
            em.close();
        }
    }

    @Override
    public void excluirComunidade(ComunidadeId cid) {
        EntityManager em = ConexaoBanco.obterEntityManager();
        EntityTransaction tx = em.getTransaction();
        try {
            tx.begin();
            em.createQuery("DELETE FROM MembroComunidadeEntity m WHERE m.comunidadeId = :cid")
                    .setParameter("cid", cid.getId())
                    .executeUpdate();

            em.createQuery("DELETE FROM ComunidadeEntity c WHERE c.id = :cid")
                    .setParameter("cid", cid.getId())
                    .executeUpdate();
            tx.commit();
        } catch (Exception e) {
            if (tx.isActive()) tx.rollback();
            throw new RuntimeException("Erro ao excluir comunidade.", e);
        } finally {
            em.close();
        }
    }

    @Override
    public void salvarMensagem(MensagemComunidade mensagem) {
        EntityManager em = ConexaoBanco.obterEntityManager();
        EntityTransaction tx = em.getTransaction();
        try {
            tx.begin();
            MensagemComunidadeEntity entity = null;
            if (mensagem.id() > 0) {
                entity = em.find(MensagemComunidadeEntity.class, mensagem.id());
            }

            if (entity == null) {
                entity = new MensagemComunidadeEntity();
            }

            entity.setComunidadeId(mensagem.comunidadeId());
            entity.setUsuarioId(mensagem.usuarioId());
            entity.setConteudo(mensagem.conteudo());
            entity.setEnviadoEm(mensagem.enviadoEm());

            if (!em.contains(entity)) {
                em.merge(entity);
            } else {
                em.persist(entity);
            }
            tx.commit();
        } catch (Exception e) {
            if (tx.isActive()) tx.rollback();
            throw new RuntimeException("Erro ao salvar mensagem.", e);
        } finally {
            em.close();
        }
    }

    @Override
    public List<MensagemComunidade> buscarMensagensDaComunidade(ComunidadeId cid) {
        EntityManager em = ConexaoBanco.obterEntityManager();
        try {
            List<MensagemComunidadeEntity> entities = em.createQuery(
                            "SELECT m FROM MensagemComunidadeEntity m WHERE m.comunidadeId = :cid ORDER BY m.enviadoEm ASC",
                            MensagemComunidadeEntity.class)
                    .setParameter("cid", cid.getId())
                    .getResultList();

            List<MensagemComunidade> mensagens = new ArrayList<>();
            for (MensagemComunidadeEntity entity : entities) {
                mensagens.add(new MensagemComunidade(
                        entity.getId(),
                        entity.getComunidadeId(),
                        entity.getUsuarioId(),
                        entity.getConteudo(),
                        entity.getEnviadoEm()
                ));
            }
            return mensagens;
        } finally {
            em.close();
        }
    }

    @Override
    public MensagemComunidade obterMensagemPorId(int mensagemId) {
        EntityManager em = ConexaoBanco.obterEntityManager();
        try {
            MensagemComunidadeEntity entity = em.find(MensagemComunidadeEntity.class, mensagemId);
            if (entity == null) return null;
            return new MensagemComunidade(
                    entity.getId(),
                    entity.getComunidadeId(),
                    entity.getUsuarioId(),
                    entity.getConteudo(),
                    entity.getEnviadoEm()
            );
        } finally {
            em.close();
        }
    }

    @Override
    public void excluirMensagem(int mensagemId) {
        EntityManager em = ConexaoBanco.obterEntityManager();
        EntityTransaction tx = em.getTransaction();
        try {
            tx.begin();
            MensagemComunidadeEntity entity = em.find(MensagemComunidadeEntity.class, mensagemId);
            if (entity != null) {
                em.remove(entity);
            }
            tx.commit();
        } catch (Exception e) {
            if (tx.isActive()) tx.rollback();
            throw new RuntimeException("Erro ao excluir mensagem.", e);
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