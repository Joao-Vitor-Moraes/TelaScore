package com.requisitos.avaliacaofilmes.TelaScore.infraestrutura.catalogo.avaliacao;

import com.requisitos.avaliacaofilmes.TelaScore.dominio.catalogo.avaliacao.Avaliacao;
import com.requisitos.avaliacaofilmes.TelaScore.dominio.catalogo.avaliacao.AvaliacaoId;
import com.requisitos.avaliacaofilmes.TelaScore.dominio.catalogo.avaliacao.AvaliacaoRepositorio;
import com.requisitos.avaliacaofilmes.TelaScore.dominio.catalogo.avaliacao.Nota;
import com.requisitos.avaliacaofilmes.TelaScore.dominio.catalogo.filme.FilmeId;
import com.requisitos.avaliacaofilmes.TelaScore.dominio.identidade.usuario.UsuarioId;
import com.requisitos.avaliacaofilmes.TelaScore.infraestrutura.catalogo.avaliacao.entidades.AvaliacaoEntity;
import com.requisitos.avaliacaofilmes.TelaScore.infraestrutura.config.ConexaoBanco;

import jakarta.persistence.EntityManager;
import jakarta.persistence.EntityTransaction;

import java.util.ArrayList;
import java.util.List;

public class AvaliacaoRepositorioImpl implements AvaliacaoRepositorio {

    @Override
    public void salvar(Avaliacao avaliacao) {
        EntityManager em = ConexaoBanco.obterEntityManager();
        EntityTransaction tx = em.getTransaction();

        try {
            tx.begin();

            AvaliacaoEntity entity = em.find(AvaliacaoEntity.class, avaliacao.getId().getId());
            if (entity == null) {
                // Primeira vez salvando — preenche campos imutáveis
                entity = new AvaliacaoEntity();
                entity.setId(avaliacao.getId().getId());
                entity.setFilmeId(avaliacao.getFilmeId().getCodigo());
                entity.setUsuarioId(avaliacao.getUsuarioId().getId());
                entity.setDataAvaliacao(avaliacao.getDataAvaliacao());
            }

            // Campos mutáveis — atualizados sempre
            entity.setNota(avaliacao.getNota().getValor());
            entity.setResenha(avaliacao.getResenha());

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
            throw new RuntimeException("Erro ao salvar a avaliação no banco de dados.", e);
        } finally {
            em.close();
        }
    }

    @Override
    public Avaliacao obter(AvaliacaoId id) {
        EntityManager em = ConexaoBanco.obterEntityManager();
        try {
            AvaliacaoEntity entity = em.find(AvaliacaoEntity.class, id.getId());
            if (entity != null) {
                return mapearParaDominio(entity);
            }
            return null;
        } finally {
            em.close();
        }
    }

    @Override
    public void remover(AvaliacaoId id) {
        EntityManager em = ConexaoBanco.obterEntityManager();
        EntityTransaction tx = em.getTransaction();

        try {
            tx.begin();
            AvaliacaoEntity entity = em.find(AvaliacaoEntity.class, id.getId());
            if (entity != null) {
                em.remove(entity);
            }
            tx.commit();
        } catch (Exception e) {
            if (tx.isActive()) {
                tx.rollback();
            }
            throw new RuntimeException("Erro ao remover a avaliação do banco de dados.", e);
        } finally {
            em.close();
        }
    }

    @Override
    public List<Avaliacao> pesquisarPorFilme(FilmeId filmeId) {
        EntityManager em = ConexaoBanco.obterEntityManager();
        try {
            List<AvaliacaoEntity> entities = em
                    .createQuery("SELECT a FROM AvaliacaoEntity a WHERE a.filmeId = :filmeId",
                            AvaliacaoEntity.class)
                    .setParameter("filmeId", filmeId.getCodigo())
                    .getResultList();

            List<Avaliacao> avaliacoes = new ArrayList<>();
            for (AvaliacaoEntity entity : entities) {
                avaliacoes.add(mapearParaDominio(entity));
            }
            return avaliacoes;
        } finally {
            em.close();
        }
    }

    @Override
    public List<Avaliacao> pesquisarPorUsuario(UsuarioId usuarioId) {
        EntityManager em = ConexaoBanco.obterEntityManager();
        try {
            List<AvaliacaoEntity> entities = em
                    .createQuery("SELECT a FROM AvaliacaoEntity a WHERE a.usuarioId = :usuarioId",
                            AvaliacaoEntity.class)
                    .setParameter("usuarioId", usuarioId.getId())
                    .getResultList();

            List<Avaliacao> avaliacoes = new ArrayList<>();
            for (AvaliacaoEntity entity : entities) {
                avaliacoes.add(mapearParaDominio(entity));
            }
            return avaliacoes;
        } finally {
            em.close();
        }
    }

    private Avaliacao mapearParaDominio(AvaliacaoEntity entity) {
        AvaliacaoId avaliacaoId = new AvaliacaoId(entity.getId());
        FilmeId filmeId = new FilmeId(entity.getFilmeId());
        UsuarioId usuarioId = new UsuarioId(entity.getUsuarioId());
        Nota nota = new Nota(entity.getNota());

        return new Avaliacao(avaliacaoId, filmeId, usuarioId, nota, entity.getResenha());
    }
}