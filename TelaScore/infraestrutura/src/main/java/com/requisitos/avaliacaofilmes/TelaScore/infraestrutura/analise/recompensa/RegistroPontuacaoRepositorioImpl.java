package com.requisitos.avaliacaofilmes.TelaScore.infraestrutura.analise.recompensa;

import com.requisitos.avaliacaofilmes.TelaScore.dominio.analise.recompensa.AcaoPontuada;
import com.requisitos.avaliacaofilmes.TelaScore.dominio.analise.recompensa.Pontos;
import com.requisitos.avaliacaofilmes.TelaScore.dominio.analise.recompensa.RegistroPontuacao;
import com.requisitos.avaliacaofilmes.TelaScore.dominio.analise.recompensa.RegistroPontuacaoId;
import com.requisitos.avaliacaofilmes.TelaScore.dominio.analise.recompensa.RegistroPontuacaoRepositorio;
import com.requisitos.avaliacaofilmes.TelaScore.dominio.identidade.usuario.UsuarioId;
import com.requisitos.avaliacaofilmes.TelaScore.infraestrutura.config.ConexaoBanco;
import jakarta.persistence.EntityManager;
import jakarta.persistence.EntityTransaction;
import java.util.ArrayList;
import java.util.List;
import com.requisitos.avaliacaofilmes.TelaScore.infraestrutura.analise.recompensa.entidades.RegistroPontuacaoEntity;
import com.requisitos.avaliacaofilmes.TelaScore.dominio.analise.recompensa.AcaoPontuada;
import com.requisitos.avaliacaofilmes.TelaScore.dominio.analise.recompensa.RegistroPontuacaoId;
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
        entity.setTipoAcao(registro.getAcao().name()); // enum → String
        entity.setDataHora(registro.getDataRegistro());

        em.persist(entity);
        tx.commit();
    } catch (Exception e) {
        if (tx.isActive()) tx.rollback();
        throw new RuntimeException("Erro ao salvar registro de pontuação.", e);
    } finally {
        em.close();
    }
}

    @Override
    public List<RegistroPontuacao> buscarPorUsuario(UsuarioId usuarioId) {
        EntityManager em = ConexaoBanco.obterEntityManager();
        try {
            List<RegistroPontuacaoEntity> entities = em.createQuery(
                "SELECT r FROM RegistroPontuacaoEntity r WHERE r.usuarioId = :uid AND r.tipoAcao <> :acaoMeta",
                RegistroPontuacaoEntity.class)
                .setParameter("uid", usuarioId.getId())
                .setParameter("acaoMeta", AcaoPontuada.COMPLETAR_META.name())
                .getResultList();

            List<RegistroPontuacao> lista = new ArrayList<>();
            for (RegistroPontuacaoEntity e : entities) {
                lista.add(mapearParaDominio(e));
            }
            return lista;
        } finally {
            em.close();
        }
    }

    @Override
    public Integer calcularTotalPontos(UsuarioId usuarioId) {
        EntityManager em = ConexaoBanco.obterEntityManager();
        try {
            Long total = em.createQuery(
                "SELECT SUM(r.pontos) FROM RegistroPontuacaoEntity r WHERE r.usuarioId = :uid AND r.tipoAcao <> :acaoMeta",
                Long.class)
                .setParameter("uid", usuarioId.getId())
                .setParameter("acaoMeta", AcaoPontuada.COMPLETAR_META.name())
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

    private RegistroPontuacao mapearParaDominio(RegistroPontuacaoEntity e) {
    return new RegistroPontuacao(
        new RegistroPontuacaoId(e.getId()),
        new UsuarioId(e.getUsuarioId()),  // precisa aceitar String — veja abaixo
        new Pontos(e.getPontos()),
        AcaoPontuada.valueOf(e.getTipoAcao()) // String → enum
    );
}
}
