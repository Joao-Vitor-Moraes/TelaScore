package com.requisitos.avaliacaofilmes.TelaScore.infraestrutura.identidade.perfil;

import com.requisitos.avaliacaofilmes.TelaScore.dominio.identidade.perfil.Apelido;
import com.requisitos.avaliacaofilmes.TelaScore.dominio.identidade.perfil.Perfil;
import com.requisitos.avaliacaofilmes.TelaScore.dominio.identidade.perfil.PerfilId;
import com.requisitos.avaliacaofilmes.TelaScore.dominio.identidade.perfil.PerfilRepositorio;
import com.requisitos.avaliacaofilmes.TelaScore.dominio.identidade.usuario.UsuarioId;
import com.requisitos.avaliacaofilmes.TelaScore.infraestrutura.config.ConexaoBanco;

import jakarta.persistence.EntityManager;
import jakarta.persistence.EntityTransaction;
import java.util.List;

public class PerfilRepositorioImpl implements PerfilRepositorio {

    @Override
    public void salvar(Perfil perfil) {
        EntityManager em = ConexaoBanco.obterEntityManager();
        EntityTransaction tx = em.getTransaction();

        try {
            tx.begin();
            if (existe(em, perfil.getId())) {
                em.createNativeQuery("""
                        UPDATE perfil
                        SET usuario_id = :usuarioId,
                            apelido = :apelido,
                            biografia = :biografia,
                            avatar_url = :avatarUrl
                        WHERE id = :id
                        """)
                        .setParameter("id", perfil.getId().getId())
                        .setParameter("usuarioId", perfil.getUsuarioId().getId())
                        .setParameter("apelido", perfil.getApelido().getValor())
                        .setParameter("biografia", perfil.getBiografia())
                        .setParameter("avatarUrl", perfil.getAvatarUrl())
                        .executeUpdate();
            } else {
                em.createNativeQuery("""
                        INSERT INTO perfil (id, usuario_id, apelido, biografia, avatar_url)
                        VALUES (:id, :usuarioId, :apelido, :biografia, :avatarUrl)
                        """)
                        .setParameter("id", perfil.getId().getId())
                        .setParameter("usuarioId", perfil.getUsuarioId().getId())
                        .setParameter("apelido", perfil.getApelido().getValor())
                        .setParameter("biografia", perfil.getBiografia())
                        .setParameter("avatarUrl", perfil.getAvatarUrl())
                        .executeUpdate();
            }
            tx.commit();
        } catch (Exception e) {
            if (tx.isActive()) {
                tx.rollback();
            }
            throw new RuntimeException("Erro ao salvar perfil no banco de dados.", e);
        } finally {
            em.close();
        }
    }

    @Override
    public Perfil obter(PerfilId id) {
        EntityManager em = ConexaoBanco.obterEntityManager();
        try {
            List<?> resultados = em.createNativeQuery("""
                    SELECT id, usuario_id, apelido, biografia, avatar_url
                    FROM perfil
                    WHERE id = :id
                    """)
                    .setParameter("id", id.getId())
                    .getResultList();

            return resultados.isEmpty() ? null : mapearLinhaParaDominio((Object[]) resultados.get(0));
        } finally {
            em.close();
        }
    }

    @Override
    public Perfil obterPorUsuario(UsuarioId usuarioId) {
        EntityManager em = ConexaoBanco.obterEntityManager();
        try {
            List<?> resultados = em.createNativeQuery("""
                    SELECT id, usuario_id, apelido, biografia, avatar_url
                    FROM perfil
                    WHERE usuario_id = :usuarioId
                    LIMIT 1
                    """)
                    .setParameter("usuarioId", usuarioId.getId())
                    .getResultList();

            return resultados.isEmpty() ? null : mapearLinhaParaDominio((Object[]) resultados.get(0));
        } finally {
            em.close();
        }
    }

    @Override
    public void removerPorUsuario(UsuarioId usuarioId) {
        EntityManager em = ConexaoBanco.obterEntityManager();
        EntityTransaction tx = em.getTransaction();

        try {
            tx.begin();
            em.createNativeQuery("DELETE FROM perfil WHERE usuario_id = :usuarioId")
                    .setParameter("usuarioId", usuarioId.getId())
                    .executeUpdate();
            tx.commit();
        } catch (Exception e) {
            if (tx.isActive()) {
                tx.rollback();
            }
            throw new RuntimeException("Erro ao remover perfil do banco de dados.", e);
        } finally {
            em.close();
        }
    }

    private Perfil mapearLinhaParaDominio(Object[] linha) {
        Perfil perfil = new Perfil(
                new PerfilId(((Number) linha[0]).intValue()),
                new UsuarioId(((Number) linha[1]).intValue()),
                new Apelido((String) linha[2]));

        perfil.setBiografia((String) linha[3]);
        perfil.setAvatarUrl((String) linha[4]);

        return perfil;
    }

    private boolean existe(EntityManager em, PerfilId id) {
        Number quantidade = (Number) em.createNativeQuery("SELECT COUNT(*) FROM perfil WHERE id = :id")
                .setParameter("id", id.getId())
                .getSingleResult();
        return quantidade.intValue() > 0;
    }
}
