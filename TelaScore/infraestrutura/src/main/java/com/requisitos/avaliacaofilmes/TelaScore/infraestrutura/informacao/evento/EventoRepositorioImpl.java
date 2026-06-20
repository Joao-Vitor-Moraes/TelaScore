package com.requisitos.avaliacaofilmes.TelaScore.infraestrutura.informacao.evento;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import com.requisitos.avaliacaofilmes.TelaScore.dominio.identidade.usuario.UsuarioId;
import com.requisitos.avaliacaofilmes.TelaScore.dominio.informacao.evento.Evento;
import com.requisitos.avaliacaofilmes.TelaScore.dominio.informacao.evento.EventoId;
import com.requisitos.avaliacaofilmes.TelaScore.dominio.informacao.evento.EventoRepositorio;
import com.requisitos.avaliacaofilmes.TelaScore.dominio.informacao.evento.RespostaEvento;
import com.requisitos.avaliacaofilmes.TelaScore.dominio.informacao.evento.Visibilidade;
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

            boolean novo = false;
            if (entity == null) {
                entity = new EventoEntity();
                entity.setId(evento.getId().getId());
                novo = true;
            }

            entity.setCriadorId(evento.getCriadorId().getId());
            entity.setTitulo(evento.getTitulo());
            entity.setDescricao(evento.getDescricao());
            entity.setDataHora(evento.getDataHora());
            entity.setVisibilidade(evento.getVisibilidade().name());

            entity.getComunidadesConvidadas().clear();
            entity.getComunidadesConvidadas().addAll(evento.getComunidadesConvidadas());

            entity.getConvidados().clear();
            entity.getConvidados().addAll(evento.getConvidados());

            entity.getRespostas().clear();
            for (Map.Entry<Integer, RespostaEvento> entrada : evento.getRespostas().entrySet()) {
                entity.getRespostas().put(entrada.getKey(), entrada.getValue().name());
            }

            if (novo) {
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
        Visibilidade visibilidade = entity.getVisibilidade() != null
                ? Visibilidade.valueOf(entity.getVisibilidade())
                : Visibilidade.PUBLICO;

        Evento evento = Evento.restaurar(
                new EventoId(entity.getId()),
                new UsuarioId(entity.getCriadorId()),
                entity.getTitulo(),
                entity.getDescricao(),
                entity.getDataHora(),
                visibilidade);

        entity.getComunidadesConvidadas().forEach(evento::convidarComunidade);
        entity.getConvidados().forEach(evento::convidarUsuario);
        entity.getRespostas().forEach((usuarioId, resposta) ->
                evento.responder(new UsuarioId(usuarioId), RespostaEvento.valueOf(resposta)));

        return evento;
    }
}
