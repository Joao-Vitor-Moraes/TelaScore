package com.requisitos.avaliacaofilmes.TelaScore.infraestrutura.analise.recompensa;

import com.requisitos.avaliacaofilmes.TelaScore.dominio.analise.recompensa.AcaoPontuada;
import com.requisitos.avaliacaofilmes.TelaScore.dominio.analise.recompensa.Pontos;
import com.requisitos.avaliacaofilmes.TelaScore.dominio.analise.recompensa.RegistroPontuacao;
import com.requisitos.avaliacaofilmes.TelaScore.dominio.analise.recompensa.RegistroPontuacaoId;
import com.requisitos.avaliacaofilmes.TelaScore.dominio.analise.recompensa.RegistroPontuacaoRepositorio;
import com.requisitos.avaliacaofilmes.TelaScore.dominio.identidade.usuario.UsuarioId;
import com.requisitos.avaliacaofilmes.TelaScore.infraestrutura.analise.recompensa.entidades.RegistroPontuacaoEntity;
import com.requisitos.avaliacaofilmes.TelaScore.infraestrutura.config.ConexaoBanco;
import jakarta.persistence.EntityManager;
import jakarta.persistence.EntityTransaction;
import java.util.List;

public class RegistroPontuacaoRepositorioImpl implements RegistroPontuacaoRepositorio {

    @Override
    public void salvar(RegistroPontuacao registro) {
        EntityManager em = ConexaoBanco.obterEntityManager();
        EntityTransaction tx = em.getTransaction();

        try {
            tx.begin();
            RegistroPontuacaoEntity entity = new RegistroPontuacaoEntity();
            entity.setId(registro.getId().getId());
            entity.setUsuarioId(registro.getUsuarioId().getId());
            entity.setPontos(registro.getPontosGanhos().getQuantidade());
            entity.setTipoAcao(registro.getAcao().name());
            entity.setDataHora(registro.getDataRegistro());

            em.persist(entity);
            tx.commit();
        } catch (Exception e) {
            if (tx.isActive()) tx.rollback();
            throw new RuntimeException("Erro ao salvar registro de pontuacao.", e);
        } finally {
            em.close();
        }
    }

    @Override
    public List<RegistroPontuacao> buscarPorUsuario(UsuarioId usuarioId) {
        EntityManager em = ConexaoBanco.obterEntityManager();
        try {
            return em.createQuery("""
                    SELECT r FROM RegistroPontuacaoEntity r
                    WHERE r.usuarioId = :uid
                    ORDER BY r.dataHora DESC, r.id DESC
                    """, RegistroPontuacaoEntity.class)
                    .setParameter("uid", usuarioId.getId())
                    .getResultList().stream()
                    .map(this::mapearParaDominio)
                    .toList();
        } finally {
            em.close();
        }
    }

    @Override
    public Integer calcularTotalPontos(UsuarioId usuarioId) {
        EntityManager em = ConexaoBanco.obterEntityManager();
        try {
            Long total = em.createQuery(
                    "SELECT SUM(r.pontos) FROM RegistroPontuacaoEntity r WHERE r.usuarioId = :uid",
                    Long.class)
                    .setParameter("uid", usuarioId.getId())
                    .getSingleResult();

            return total != null ? total.intValue() : 0;
        } finally {
            em.close();
        }
    }

    @Override
    public int proximoId() {
        EntityManager em = ConexaoBanco.obterEntityManager();
        try {
            Integer max = em.createQuery(
                    "SELECT MAX(r.id) FROM RegistroPontuacaoEntity r", Integer.class)
                    .getSingleResult();
            return max == null ? 1 : max + 1;
        } finally {
            em.close();
        }
    }

    private RegistroPontuacao mapearParaDominio(RegistroPontuacaoEntity entity) {
        return new RegistroPontuacao(
                new RegistroPontuacaoId(entity.getId()),
                new UsuarioId(entity.getUsuarioId()),
                new Pontos(entity.getPontos()),
                AcaoPontuada.valueOf(entity.getTipoAcao()),
                entity.getDataHora());
    }
}
