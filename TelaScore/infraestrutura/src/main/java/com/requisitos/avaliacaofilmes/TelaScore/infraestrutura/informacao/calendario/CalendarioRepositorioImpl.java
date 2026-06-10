package com.requisitos.avaliacaofilmes.TelaScore.infraestrutura.informacao.calendario;

import java.util.List;

import com.requisitos.avaliacaofilmes.TelaScore.dominio.catalogo.filme.FilmeId;
import com.requisitos.avaliacaofilmes.TelaScore.dominio.identidade.usuario.UsuarioId;
import com.requisitos.avaliacaofilmes.TelaScore.dominio.informacao.calendario.CalendarioEstreia;
import com.requisitos.avaliacaofilmes.TelaScore.dominio.informacao.calendario.CalendarioId;
import com.requisitos.avaliacaofilmes.TelaScore.dominio.informacao.calendario.CalendarioRepositorio;
import com.requisitos.avaliacaofilmes.TelaScore.dominio.informacao.calendario.EntradaCalendario;
import com.requisitos.avaliacaofilmes.TelaScore.infraestrutura.config.ConexaoBanco;
import com.requisitos.avaliacaofilmes.TelaScore.infraestrutura.informacao.calendario.entidades.CalendarioEntity;
import com.requisitos.avaliacaofilmes.TelaScore.infraestrutura.informacao.calendario.entidades.EntradaCalendarioEntity;

import jakarta.persistence.EntityManager;
import jakarta.persistence.EntityTransaction;

public class CalendarioRepositorioImpl implements CalendarioRepositorio {

    @Override
    public void salvar(CalendarioEstreia calendario) {
        EntityManager em = ConexaoBanco.obterEntityManager();
        EntityTransaction tx = em.getTransaction();

        try {
            tx.begin();

            CalendarioEntity entity = em.find(CalendarioEntity.class, calendario.getId().getId());
            if (entity == null) {
                entity = new CalendarioEntity();
                entity.setId(calendario.getId().getId());
                entity.setUsuarioId(calendario.getUsuarioId().getId());
            }

            entity.getEntradas().clear();
            em.flush();

            for (EntradaCalendario entrada : calendario.getEntradas()) {
                EntradaCalendarioEntity entradaEntity = new EntradaCalendarioEntity();
                entradaEntity.setCalendario(entity);
                entradaEntity.setFilmeId(entrada.getFilmeId().getCodigo());
                entradaEntity.setDataEstreiaPrevista(entrada.getDataEstreiaPrevista());
                entradaEntity.setLembreteAtivo(entrada.isLembreteAtivo());
                entity.getEntradas().add(entradaEntity);
            }

            if (!em.contains(entity)) {
                em.persist(entity);
            }

            tx.commit();
        } catch (Exception e) {
            if (tx.isActive()) tx.rollback();
            throw new RuntimeException("Erro ao salvar o calendário no banco de dados.", e);
        } finally {
            em.close();
        }
    }

    @Override
    public CalendarioEstreia obterPorUsuario(UsuarioId usuarioId) {
        EntityManager em = ConexaoBanco.obterEntityManager();
        try {
            List<CalendarioEntity> entities = em.createQuery(
                    "SELECT c FROM CalendarioEntity c WHERE c.usuarioId = :uid", CalendarioEntity.class)
                    .setParameter("uid", usuarioId.getId())
                    .getResultList();

            if (entities.isEmpty()) {
                return null;
            }
            return mapearParaDominio(entities.get(0));
        } finally {
            em.close();
        }
    }

    private CalendarioEstreia mapearParaDominio(CalendarioEntity entity) {
        CalendarioEstreia calendario = new CalendarioEstreia(
                new CalendarioId(entity.getId()),
                new UsuarioId(entity.getUsuarioId()));

        for (EntradaCalendarioEntity entradaEntity : entity.getEntradas()) {
            calendario.adicionarFilme(EntradaCalendario.restaurar(
                    new FilmeId(entradaEntity.getFilmeId()),
                    entradaEntity.getDataEstreiaPrevista(),
                    entradaEntity.isLembreteAtivo()));
        }

        return calendario;
    }
}
