package com.requisitos.avaliacaofilmes.TelaScore.infraestrutura.informacao.evento;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

import com.requisitos.avaliacaofilmes.TelaScore.dominio.identidade.usuario.UsuarioId;
import com.requisitos.avaliacaofilmes.TelaScore.dominio.informacao.evento.Evento;
import com.requisitos.avaliacaofilmes.TelaScore.dominio.informacao.evento.EventoId;
import com.requisitos.avaliacaofilmes.TelaScore.dominio.informacao.evento.EventoRepositorio;
import com.requisitos.avaliacaofilmes.TelaScore.infraestrutura.config.ConexaoBanco;
import com.requisitos.avaliacaofilmes.TelaScore.infraestrutura.informacao.evento.entidades.EventoEntity;

import jakarta.persistence.EntityManager;
import jakarta.persistence.EntityTransaction;

public class EventoRepositorioImpl implements EventoRepositorio {

    @Override
    public void salvar(Evento evento) {
        EntityManager em = ConexaoBanco.obterEntityManager();
        EntityTransaction tx = em.getTransaction();

        try {
            tx.begin();
            EventoEntity entity = em.find(EventoEntity.class, evento.getId().getId());

            if (entity == null) {
                entity = new EventoEntity();
                entity.setId(evento.getId().getId());
            }

            entity.setCriadorId(evento.getCriadorId().getId());
            entity.setTitulo(evento.getTitulo());
            entity.setDescricao(evento.getDescricao());
            entity.setDataHora(evento.getDataHora());

            if (!em.contains(entity)) {
                em.merge(entity);
            } else {
                em.persist(entity);
            }
            tx.commit();
        } catch (Exception e) {
            if (tx.isActive()) tx.rollback();
            throw new RuntimeException("Erro ao salvar evento no banco.", e);
        } finally {
            em.close();
        }
    }

    @Override
    public Evento obter(EventoId id) {
        EntityManager em = ConexaoBanco.obterEntityManager();
        try {
            EventoEntity entity = em.find(EventoEntity.class, id.getId());
            if (entity == null) {
                return null;
            }
            return mapearParaDominio(entity);
        } finally {
            em.close();
        }
    }

    @Override
    public void remover(EventoId id) {
        EntityManager em = ConexaoBanco.obterEntityManager();
        EntityTransaction tx = em.getTransaction();
        try {
            tx.begin();
            EventoEntity entity = em.find(EventoEntity.class, id.getId());
            if (entity != null) {
                em.remove(entity);
            }
            tx.commit();
        } catch (Exception e) {
            if (tx.isActive()) tx.rollback();
            throw new RuntimeException("Erro ao remover evento.", e);
        } finally {
            em.close();
        }
    }

    @Override
    public List<Evento> buscarEventosFuturos(LocalDateTime aPartirDe) {
        EntityManager em = ConexaoBanco.obterEntityManager();
        try {
            List<EventoEntity> entities = em.createQuery(
                    "SELECT e FROM EventoEntity e WHERE e.dataHora >= :aPartirDe ORDER BY e.dataHora ASC",
                    EventoEntity.class)
                    .setParameter("aPartirDe", aPartirDe)
                    .getResultList();

            List<Evento> eventos = new ArrayList<>();
            for (EventoEntity entity : entities) {
                eventos.add(mapearParaDominio(entity));
            }
            return eventos;
        } finally {
            em.close();
        }
    }

    private Evento mapearParaDominio(EventoEntity entity) {
        return Evento.restaurar(
                new EventoId(entity.getId()),
                new UsuarioId(entity.getCriadorId()),
                entity.getTitulo(),
                entity.getDescricao(),
                entity.getDataHora()
        );
    }
}
