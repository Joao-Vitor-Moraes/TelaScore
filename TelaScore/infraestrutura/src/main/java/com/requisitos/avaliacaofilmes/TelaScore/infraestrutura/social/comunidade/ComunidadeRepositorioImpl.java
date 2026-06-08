package com.requisitos.avaliacaofilmes.TelaScore.infraestrutura.social.comunidade;

import java.time.LocalDateTime;
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

            em.createNativeQuery("INSERT INTO comunidade (id, nome, descricao) VALUES (?1, ?2, ?3)")
                    .setParameter(1, comunidade.getId().getId())
                    .setParameter(2, comunidade.getNome())
                    .setParameter(3, comunidade.getDescricao())
                    .executeUpdate();

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

            em.createNativeQuery("INSERT INTO membro_comunidade (comunidade_id, usuario_id, papel) VALUES (?1, ?2, ?3)")
                    .setParameter(1, membro.getComunidadeId().getId())
                    .setParameter(2, membro.getUsuarioId().getId())
                    .setParameter(3, membro.getPapel().name())
                    .executeUpdate();

            tx.commit();
        } catch (Exception e) {
            if (tx.isActive()) tx.rollback();
            throw new RuntimeException("Erro ao salvar membro da comunidade.", e);
        } finally {
            em.close();
        }
    }

    public List<Comunidade> listarTodas() {
        EntityManager em = ConexaoBanco.obterEntityManager();
        try {
            List<Object[]> rows = em.createNativeQuery("SELECT id, nome, descricao FROM comunidade").getResultList();

            List<Comunidade> comunidades = new ArrayList<>();
            for (Object[] row : rows) {
                int id = ((Number) row[0]).intValue();
                String nome = (String) row[1];
                String descricao = (String) row[2];

                comunidades.add(new Comunidade(
                        new ComunidadeId(id),
                        nome,
                        descricao
                ));
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
            List<Object[]> rows = em.createNativeQuery(
                            "SELECT c.id, c.nome, c.descricao, m.papel FROM comunidade c " +
                                    "INNER JOIN membro_comunidade m ON c.id = m.comunidade_id " +
                                    "WHERE m.usuario_id = ?1")
                    .setParameter(1, uid.getId())
                    .getResultList();

            List<ComunidadeUsuarioResumo> comunidades = new ArrayList<>();
            for (Object[] row : rows) {
                int id = ((Number) row[0]).intValue();
                String nome = (String) row[1];
                String descricao = (String) row[2];
                String papel = (String) row[3];

                comunidades.add(new ComunidadeUsuarioResumo(id, nome, descricao, papel));
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
            List<Object[]> rows = em.createNativeQuery(
                            "SELECT comunidade_id, usuario_id, papel FROM membro_comunidade WHERE comunidade_id = ?1")
                    .setParameter(1, cid.getId())
                    .getResultList();

            List<MembroComunidade> membros = new ArrayList<>();
            for (Object[] row : rows) {
                int comunidadeId = ((Number) row[0]).intValue();
                int usuarioId = ((Number) row[1]).intValue();
                String papel = (String) row[2];

                membros.add(new MembroComunidade(
                        new ComunidadeId(comunidadeId),
                        new UsuarioId(usuarioId),
                        PapelComunidade.valueOf(papel)
                ));
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

            em.createNativeQuery("UPDATE membro_comunidade SET papel = ?1 WHERE comunidade_id = ?2 AND usuario_id = ?3")
                    .setParameter(1, novoPapel.name())
                    .setParameter(2, cid.getId())
                    .setParameter(3, uid.getId())
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
            List<String> resultados = em.createNativeQuery(
                            "SELECT papel FROM membro_comunidade WHERE comunidade_id = ?1 AND usuario_id = ?2")
                    .setParameter(1, cid.getId())
                    .setParameter(2, uid.getId())
                    .getResultList();

            if (resultados.isEmpty()) {
                return false;
            }

            String papel = resultados.get(0);
            return "CRIADOR".equalsIgnoreCase(papel);
        } finally {
            em.close();
        }
    }

    @Override
    public boolean existeMembro(ComunidadeId cid, UsuarioId uid) {
        EntityManager em = ConexaoBanco.obterEntityManager();
        try {
            Long contagem = ((Number) em.createNativeQuery(
                            "SELECT COUNT(*) FROM membro_comunidade WHERE comunidade_id = ?1 AND usuario_id = ?2")
                    .setParameter(1, cid.getId())
                    .setParameter(2, uid.getId())
                    .getSingleResult()).longValue();

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

            em.createNativeQuery("DELETE FROM membro_comunidade WHERE comunidade_id = ?1")
                    .setParameter(1, cid.getId())
                    .executeUpdate();

            em.createNativeQuery("DELETE FROM comunidade WHERE id = ?1")
                    .setParameter(1, cid.getId())
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
            em.createNativeQuery(
                            "INSERT INTO mensagem_comunidade (comunidade_id, usuario_id, conteudo, enviado_em) VALUES (?1, ?2, ?3, ?4)")
                    .setParameter(1, mensagem.comunidadeId())
                    .setParameter(2, mensagem.usuarioId())
                    .setParameter(3, mensagem.conteudo())
                    .setParameter(4, java.sql.Timestamp.valueOf(mensagem.enviadoEm()))
                    .executeUpdate();
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
            List<Object[]> rows = em.createNativeQuery(
                            "SELECT id, comunidade_id, usuario_id, conteudo, enviado_em FROM mensagem_comunidade " +
                                    "WHERE comunidade_id = ?1 ORDER BY enviado_em ASC")
                    .setParameter(1, cid.getId())
                    .getResultList();

            List<MensagemComunidade> mensagens = new ArrayList<>();
            for (Object[] row : rows) {
                int id = ((Number) row[0]).intValue();
                int comunidadeId = ((Number) row[1]).intValue();
                int usuarioId = ((Number) row[2]).intValue();
                String conteudo = (String) row[3];

                LocalDateTime enviadoEm;
                if (row[4] instanceof java.sql.Timestamp) {
                    enviadoEm = ((java.sql.Timestamp) row[4]).toLocalDateTime();
                } else {
                    enviadoEm = (LocalDateTime) row[4];
                }

                mensagens.add(new MensagemComunidade(id, comunidadeId, usuarioId, conteudo, enviadoEm));
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
            List<Object[]> rows = em.createNativeQuery(
                            "SELECT id, comunidade_id, usuario_id, conteudo, enviado_em FROM mensagem_comunidade WHERE id = ?1")
                    .setParameter(1, mensagemId)
                    .getResultList();

            if (rows.isEmpty()) return null;

            Object[] row = rows.get(0);
            int id = ((Number) row[0]).intValue();
            int comunidadeId = ((Number) row[1]).intValue();
            int usuarioId = ((Number) row[2]).intValue();
            String conteudo = (String) row[3];
            LocalDateTime enviadoEm = row[4] instanceof java.sql.Timestamp ?
                    ((java.sql.Timestamp) row[4]).toLocalDateTime() : (LocalDateTime) row[4];

            return new MensagemComunidade(id, comunidadeId, usuarioId, conteudo, enviadoEm);
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
            em.createNativeQuery("DELETE FROM mensagem_comunidade WHERE id = ?1")
                    .setParameter(1, mensagemId)
                    .executeUpdate();
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