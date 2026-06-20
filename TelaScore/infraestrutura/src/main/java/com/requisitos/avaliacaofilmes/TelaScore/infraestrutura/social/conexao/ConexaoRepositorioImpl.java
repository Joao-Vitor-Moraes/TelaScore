package com.requisitos.avaliacaofilmes.TelaScore.infraestrutura.social.conexao;

import java.util.List;

import com.requisitos.avaliacaofilmes.TelaScore.dominio.identidade.usuario.UsuarioId;
import com.requisitos.avaliacaofilmes.TelaScore.dominio.social.conexao.Conexao;
import com.requisitos.avaliacaofilmes.TelaScore.dominio.social.conexao.ConexaoId;
import com.requisitos.avaliacaofilmes.TelaScore.dominio.social.conexao.ConexaoRepositorio;
import com.requisitos.avaliacaofilmes.TelaScore.infraestrutura.config.ConexaoBanco;
import com.requisitos.avaliacaofilmes.TelaScore.infraestrutura.social.conexao.entidades.ConexaoEntity;

import jakarta.persistence.EntityManager;
import jakarta.persistence.EntityTransaction;

public class ConexaoRepositorioImpl implements ConexaoRepositorio {

    @Override
    public void salvar(Conexao conexao) {
        EntityManager em = ConexaoBanco.obterEntityManager();
        EntityTransaction tx = em.getTransaction();
        try {
            tx.begin();
            ConexaoEntity entity = new ConexaoEntity();
            entity.setId(conexao.getId().getId());
            entity.setSeguidorId(conexao.getSeguidorId().getId());
            entity.setSeguidoId(conexao.getSeguidoId().getId());
            entity.setDataCriacao(conexao.getDataCriacao());
            em.persist(entity);
            tx.commit();
        } catch (Exception e) {
            if (tx.isActive()) tx.rollback();
            throw new RuntimeException("Erro ao salvar conexao no banco de dados", e);
        } finally {
            em.close();
        }
    }

    @Override
    public void remover(ConexaoId id) {
        EntityManager em = ConexaoBanco.obterEntityManager();
        EntityTransaction tx = em.getTransaction();
        try {
            tx.begin();
            ConexaoEntity entity = em.find(ConexaoEntity.class, id.getId());
            if (entity != null) em.remove(entity);
            tx.commit();
        } catch (Exception e) {
            if (tx.isActive()) tx.rollback();
            throw new RuntimeException("Erro ao remover conexao do banco de dados", e);
        } finally {
            em.close();
        }
    }

    @Override
    public Conexao buscarConexao(UsuarioId seguidorId, UsuarioId seguidoId) {
        EntityManager em = ConexaoBanco.obterEntityManager();
        try {
            return em.createQuery(
                    "SELECT c FROM ConexaoEntity c WHERE c.seguidorId = :seguidorId AND c.seguidoId = :seguidoId",
                    ConexaoEntity.class)
                .setParameter("seguidorId", seguidorId.getId())
                .setParameter("seguidoId", seguidoId.getId())
                .getResultStream()
                .findFirst()
                .map(this::paraDominio)
                .orElse(null);
        } finally {
            em.close();
        }
    }

    @Override
    public List<Conexao> buscarSeguidoresDe(UsuarioId usuarioId) {
        return buscarPorCampo("seguidoId", usuarioId);
    }

    @Override
    public List<Conexao> buscarSeguidosPor(UsuarioId usuarioId) {
        return buscarPorCampo("seguidorId", usuarioId);
    }

    private List<Conexao> buscarPorCampo(String campo, UsuarioId usuarioId) {
        EntityManager em = ConexaoBanco.obterEntityManager();
        try {
            return em.createQuery(
                    "SELECT c FROM ConexaoEntity c WHERE c." + campo + " = :usuarioId ORDER BY c.dataCriacao DESC",
                    ConexaoEntity.class)
                .setParameter("usuarioId", usuarioId.getId())
                .getResultList()
                .stream()
                .map(this::paraDominio)
                .toList();
        } finally {
            em.close();
        }
    }

    private Conexao paraDominio(ConexaoEntity entity) {
        return new Conexao(
            new ConexaoId(entity.getId()),
            new UsuarioId(entity.getSeguidorId()),
            new UsuarioId(entity.getSeguidoId())
        );
    }
}
