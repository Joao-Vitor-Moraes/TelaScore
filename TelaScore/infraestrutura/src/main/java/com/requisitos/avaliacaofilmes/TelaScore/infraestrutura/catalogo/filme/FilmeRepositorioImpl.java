package com.requisitos.avaliacaofilmes.TelaScore.infraestrutura.catalogo.filme;

import java.util.List;
import java.util.stream.Collectors;

import com.requisitos.avaliacaofilmes.TelaScore.dominio.catalogo.diretor.DiretorId;
import com.requisitos.avaliacaofilmes.TelaScore.dominio.catalogo.filme.Filme;
import com.requisitos.avaliacaofilmes.TelaScore.dominio.catalogo.filme.FilmeId;
import com.requisitos.avaliacaofilmes.TelaScore.dominio.catalogo.filme.FilmeRepositorio;
import com.requisitos.avaliacaofilmes.TelaScore.infraestrutura.catalogo.filme.entidades.FilmeEntity;
import com.requisitos.avaliacaofilmes.TelaScore.infraestrutura.config.ConexaoBanco;

import jakarta.persistence.EntityManager;
import jakarta.persistence.EntityTransaction;

public class FilmeRepositorioImpl implements FilmeRepositorio {
    @Override
    public void salvar(Filme filme) {
        EntityManager em = ConexaoBanco.obterEntityManager();
        EntityTransaction tx = em.getTransaction();

        try {
            tx.begin();

            FilmeEntity entity = em.find(FilmeEntity.class, filme.getId().getCodigo());
            if (entity == null) {
                entity = new FilmeEntity();
                entity.setId(filme.getId().getCodigo());
            }

            entity.setTitulo(filme.getTitulo());
            entity.setSinopse(filme.getSinopse());
            entity.setAnoLancamento(filme.getAnoLancamento());
            entity.setImagemUrl(filme.getImagemUrl());

            // Converte lista de DiretorId para lista de Integer
            List<Integer> diretoresIds = filme.getDiretores().stream()
                    .map(DiretorId::getId)
                    .collect(Collectors.toList());
            entity.setDiretores(diretoresIds);

            if (!em.contains(entity)) {
                em.merge(entity);
            } else {
                em.persist(entity);
            }

            tx.commit();
        } catch (Exception e) {
            if (tx.isActive()) {
                tx.rollback();
            }
            throw new RuntimeException("Erro ao salvar o filme no banco de dados.", e);
        } finally {
            em.close();
        }
    }

    @Override
    public Filme obter(FilmeId id) {
        EntityManager em = ConexaoBanco.obterEntityManager();
        try {
            FilmeEntity entity = em.find(FilmeEntity.class, id.getCodigo());
            if (entity != null) {
                return mapearParaDominio(entity);
            }
            return null;
        } finally {
            em.close();
        }
    }

    @Override
    public void remover(FilmeId id) {
        EntityManager em = ConexaoBanco.obterEntityManager();
        EntityTransaction tx = em.getTransaction();

        try {
            tx.begin();
            FilmeEntity entity = em.find(FilmeEntity.class, id.getCodigo());
            if (entity != null) {
                em.remove(entity);
            }
            tx.commit();
        } catch (Exception e) {
            if (tx.isActive()) {
                tx.rollback();
            }
            throw new RuntimeException("Erro ao remover o filme do banco de dados.", e);
        } finally {
            em.close();
        }
    }

    private Filme mapearParaDominio(FilmeEntity entity) {
        FilmeId filmeId = new FilmeId(entity.getId());

        List<DiretorId> diretores = entity.getDiretores().stream()
                .map(DiretorId::new)
                .collect(Collectors.toList());

        Filme filme = new Filme(filmeId, entity.getTitulo(), entity.getSinopse(),
                entity.getAnoLancamento(), diretores);
        filme.setImagemUrl(entity.getImagemUrl());
        return filme;
    }

    @Override
    public List<Filme> listarTodos() {
        EntityManager em = ConexaoBanco.obterEntityManager();
        try {
            return em.createQuery("SELECT f FROM FilmeEntity f ORDER BY f.titulo", FilmeEntity.class)
                    .getResultList()
                    .stream()
                    .map(this::mapearParaDominio)
                    .collect(Collectors.toList());
        } finally {
            em.close();
        }
    }

    @Override
public boolean existeComTitulo(String titulo) {
    EntityManager em = ConexaoBanco.obterEntityManager();
    try {
        Long quantidade = em.createQuery(
                "SELECT COUNT(f) FROM FilmeEntity f WHERE LOWER(f.titulo) = LOWER(:titulo)",
                Long.class)
                .setParameter("titulo", titulo)
                .getSingleResult();

        return quantidade > 0;
    } finally {
        em.close();
    }
}
    
}
