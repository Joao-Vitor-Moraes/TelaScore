package com.requisitos.avaliacaofilmes.TelaScore.infraestrutura.catalogo.diretor;

import com.requisitos.avaliacaofilmes.TelaScore.dominio.catalogo.diretor.Diretor;
import com.requisitos.avaliacaofilmes.TelaScore.dominio.catalogo.diretor.DiretorId;
import com.requisitos.avaliacaofilmes.TelaScore.dominio.catalogo.diretor.DiretorRepositorio;
import com.requisitos.avaliacaofilmes.TelaScore.infraestrutura.catalogo.diretor.entidades.DiretorEntity;
import com.requisitos.avaliacaofilmes.TelaScore.infraestrutura.config.ConexaoBanco;

import jakarta.persistence.EntityManager;
import jakarta.persistence.EntityTransaction;

import org.springframework.stereotype.Repository;

@Repository
public class DiretorRepositorioImpl implements DiretorRepositorio {

    @Override
    public void salvar(Diretor diretor) {
        EntityManager em = ConexaoBanco.obterEntityManager();
        EntityTransaction tx = em.getTransaction();

        try {
            tx.begin();

            DiretorEntity entity = em.find(DiretorEntity.class, diretor.getId().getId());
            if (entity == null) {
                entity = new DiretorEntity();
                entity.setId(diretor.getId().getId());
            }

            entity.setNome(diretor.getNome());

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
            throw new RuntimeException("Erro ao salvar o diretor no banco de dados.", e);
        } finally {
            em.close();
        }
    }

    @Override
    public Diretor obter(DiretorId id) {
        EntityManager em = ConexaoBanco.obterEntityManager();
        try {
            DiretorEntity entity = em.find(DiretorEntity.class, id.getId());
            if (entity != null) {
                return new Diretor(new DiretorId(entity.getId()), entity.getNome());
            }
            return null;
        } finally {
            em.close();
        }
    }
}