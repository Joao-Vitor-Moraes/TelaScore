package com.requisitos.avaliacaofilmes.TelaScore.infraestrutura.social.mensagem;

import com.requisitos.avaliacaofilmes.TelaScore.dominio.identidade.usuario.UsuarioId;
import com.requisitos.avaliacaofilmes.TelaScore.dominio.social.mensagem.Mensagem;
import com.requisitos.avaliacaofilmes.TelaScore.dominio.social.mensagem.MensagemId;
import com.requisitos.avaliacaofilmes.TelaScore.dominio.social.mensagem.MensagemRepositorio;
import com.requisitos.avaliacaofilmes.TelaScore.infraestrutura.config.ConexaoBanco;
import com.requisitos.avaliacaofilmes.TelaScore.infraestrutura.social.mensagem.entidades.MensagemEntity;
import jakarta.persistence.EntityManager;
import java.util.List;

public class MensagemRepositorioImpl implements MensagemRepositorio {
    private final EntityManager entityManagerExterno;

    public MensagemRepositorioImpl() {
        this.entityManagerExterno = null;
    }

    public MensagemRepositorioImpl(EntityManager entityManager) {
        this.entityManagerExterno = entityManager;
    }

    @Override
    public void salvar(Mensagem mensagem) {
        EntityManager em = obterEntityManager();
        try {
            MensagemEntity entity = toEntity(mensagem);
            em.getTransaction().begin();
            if (em.find(MensagemEntity.class, entity.getId()) != null) {
                em.merge(entity);
            } else {
                em.persist(entity);
            }
            em.getTransaction().commit();
        } catch (RuntimeException ex) {
            if (em.getTransaction().isActive()) {
                em.getTransaction().rollback();
            }
            throw ex;
        } finally {
            fecharSeInterno(em);
        }
    }

    @Override
    public void remover(MensagemId id) {
        EntityManager em = obterEntityManager();
        try {
            em.getTransaction().begin();
            MensagemEntity entity = em.find(MensagemEntity.class, id.getId());
            if (entity != null) {
                em.remove(entity);
            }
            em.getTransaction().commit();
        } catch (RuntimeException ex) {
            if (em.getTransaction().isActive()) {
                em.getTransaction().rollback();
            }
            throw ex;
        } finally {
            fecharSeInterno(em);
        }
    }

    @Override
    public Mensagem obter(MensagemId id) {
        EntityManager em = obterEntityManager();
        try {
            MensagemEntity entity = em.find(MensagemEntity.class, id.getId());
            return entity == null ? null : toDomain(entity);
        } finally {
            fecharSeInterno(em);
        }
    }

    @Override
    public List<Mensagem> buscarConversa(UsuarioId usuarioA, UsuarioId usuarioB) {
        EntityManager em = obterEntityManager();
        try {
            return em.createQuery(
                    "SELECT m FROM MensagemEntity m " +
                            "WHERE (m.remetenteId = :a AND m.destinatarioId = :b) " +
                            "OR (m.remetenteId = :b AND m.destinatarioId = :a) " +
                            "ORDER BY m.dataEnvio ASC, m.id ASC",
                    MensagemEntity.class)
                    .setParameter("a", usuarioA.getId())
                    .setParameter("b", usuarioB.getId())
                    .getResultList()
                    .stream()
                    .map(this::toDomain)
                    .toList();
        } finally {
            fecharSeInterno(em);
        }
    }

    @Override
    public int contarMensagensNaoLidas(UsuarioId destinatarioId) {
        EntityManager em = obterEntityManager();
        try {
            Long contagem = em.createQuery(
                    "SELECT COUNT(m) FROM MensagemEntity m WHERE m.destinatarioId = :id AND m.lida = false",
                    Long.class)
                    .setParameter("id", destinatarioId.getId())
                    .getSingleResult();
            return contagem.intValue();
        } finally {
            fecharSeInterno(em);
        }
    }

    private EntityManager obterEntityManager() {
        return entityManagerExterno != null ? entityManagerExterno : ConexaoBanco.obterEntityManager();
    }

    private void fecharSeInterno(EntityManager em) {
        if (entityManagerExterno == null) {
            em.close();
        }
    }

    private MensagemEntity toEntity(Mensagem mensagem) {
        return new MensagemEntity(
                mensagem.getId().getId(),
                mensagem.getRemetenteId().getId(),
                mensagem.getDestinatarioId().getId(),
                mensagem.getConteudo(),
                mensagem.isLida(),
                mensagem.getDataEnvio()
        );
    }

    private Mensagem toDomain(MensagemEntity entity) {
        return new Mensagem(
                new MensagemId(entity.getId()),
                new UsuarioId(entity.getRemetenteId()),
                new UsuarioId(entity.getDestinatarioId()),
                entity.getConteudo(),
                entity.getDataEnvio(),
                entity.isLida()
        );
    }
}
