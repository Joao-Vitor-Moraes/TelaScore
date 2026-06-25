package com.requisitos.avaliacaofilmes.TelaScore.infraestrutura.analise.meta;

import java.time.LocalDateTime;
import java.util.List;

import com.requisitos.avaliacaofilmes.TelaScore.dominio.analise.meta.MetaId;
import com.requisitos.avaliacaofilmes.TelaScore.dominio.analise.meta.NotificacaoMeta;
import com.requisitos.avaliacaofilmes.TelaScore.dominio.analise.meta.NotificacaoMetaRepositorio;
import com.requisitos.avaliacaofilmes.TelaScore.dominio.identidade.usuario.UsuarioId;
import com.requisitos.avaliacaofilmes.TelaScore.infraestrutura.analise.meta.entidades.NotificacaoMetaEntity;
import com.requisitos.avaliacaofilmes.TelaScore.infraestrutura.config.ConexaoBanco;

import jakarta.persistence.EntityManager;
import jakarta.persistence.EntityTransaction;

public class NotificacaoMetaRepositorioImpl implements NotificacaoMetaRepositorio {
    @Override
    public void criar(UsuarioId usuarioId, MetaId metaId, String tituloMeta, int pontosGanhos) {
        EntityManager em = ConexaoBanco.obterEntityManager();
        EntityTransaction tx = em.getTransaction();
        try {
            tx.begin();
            NotificacaoMetaEntity entity = new NotificacaoMetaEntity();
            entity.setUsuarioId(usuarioId.getId());
            entity.setMetaId(metaId.getId());
            entity.setTituloMeta(tituloMeta);
            entity.setPontosGanhos(pontosGanhos);
            entity.setTipo("META");
            entity.setTitulo("Meta concluida");
            entity.setMensagem(tituloMeta + " concluida" + (pontosGanhos > 0 ? " +" + pontosGanhos + " pontos." : "."));
            entity.setRota("/metas");
            entity.setDataCriacao(LocalDateTime.now());
            entity.setLida(false);
            em.persist(entity);
            tx.commit();
        } catch (RuntimeException e) {
            if (tx.isActive()) tx.rollback();
            throw e;
        } finally {
            em.close();
        }
    }

    @Override
    public void criarSistema(UsuarioId usuarioId, String tipo, String titulo, String mensagem, String rota) {
        EntityManager em = ConexaoBanco.obterEntityManager();
        EntityTransaction tx = em.getTransaction();
        try {
            tx.begin();
            NotificacaoMetaEntity entity = new NotificacaoMetaEntity();
            entity.setUsuarioId(usuarioId.getId());
            entity.setMetaId(1);
            entity.setTituloMeta(titulo);
            entity.setPontosGanhos(0);
            entity.setTipo(tipo == null || tipo.isBlank() ? "SISTEMA" : tipo.trim());
            entity.setTitulo(titulo == null || titulo.isBlank() ? "Notificacao" : titulo.trim());
            entity.setMensagem(mensagem == null || mensagem.isBlank() ? null : mensagem.trim());
            entity.setRota(rota == null || rota.isBlank() ? "/" : rota.trim());
            entity.setDataCriacao(LocalDateTime.now());
            entity.setLida(false);
            em.persist(entity);
            tx.commit();
        } catch (RuntimeException e) {
            if (tx.isActive()) tx.rollback();
            throw e;
        } finally {
            em.close();
        }
    }

    @Override
    public List<NotificacaoMeta> listarNaoLidas(UsuarioId usuarioId) {
        EntityManager em = ConexaoBanco.obterEntityManager();
        try {
            return em.createQuery("""
                    SELECT n FROM NotificacaoMetaEntity n
                    WHERE n.usuarioId = :usuarioId AND n.lida = false
                    ORDER BY n.dataCriacao DESC
                    """, NotificacaoMetaEntity.class)
                    .setParameter("usuarioId", usuarioId.getId())
                    .getResultList().stream()
                    .map(this::paraDominio)
                    .toList();
        } finally {
            em.close();
        }
    }

    @Override
    public void marcarComoLida(int notificacaoId, UsuarioId usuarioId) {
        EntityManager em = ConexaoBanco.obterEntityManager();
        EntityTransaction tx = em.getTransaction();
        try {
            tx.begin();
            NotificacaoMetaEntity entity = em.find(NotificacaoMetaEntity.class, notificacaoId);
            if (entity != null && entity.getUsuarioId().equals(usuarioId.getId())) {
                entity.setLida(true);
            }
            tx.commit();
        } catch (RuntimeException e) {
            if (tx.isActive()) tx.rollback();
            throw e;
        } finally {
            em.close();
        }
    }

    private NotificacaoMeta paraDominio(NotificacaoMetaEntity entity) {
        return new NotificacaoMeta(
                entity.getId(),
                new UsuarioId(entity.getUsuarioId()),
                !"META".equals(entity.getTipo()) ? null : new MetaId(entity.getMetaId()),
                entity.getTituloMeta(),
                entity.getPontosGanhos(),
                entity.getDataCriacao(),
                entity.getLida(),
                entity.getTipo(),
                entity.getTitulo(),
                entity.getMensagem(),
                entity.getRota());
    }
}
