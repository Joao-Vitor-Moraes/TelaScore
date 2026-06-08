package com.requisitos.avaliacaofilmes.TelaScore.infraestrutura.social.mensagem;

import com.requisitos.avaliacaofilmes.TelaScore.dominio.social.mensagem.*;
import com.requisitos.avaliacaofilmes.TelaScore.dominio.identidade.usuario.UsuarioId;
import com.requisitos.avaliacaofilmes.TelaScore.infraestrutura.social.mensagem.entidades.MensagemEntity;
import jakarta.persistence.EntityManager;
import java.util.List;
import java.util.stream.Collectors;

public class MensagemRepositorioImpl implements MensagemRepositorio {
    private final EntityManager entityManager;

    public MensagemRepositorioImpl(EntityManager entityManager) {
        this.entityManager = entityManager;
    }

    @Override
    public void salvar(Mensagem mensagem) {
        MensagemEntity entity = new MensagemEntity(
            mensagem.getId().getId(),
            mensagem.getRemetenteId().getId(),
            mensagem.getDestinatarioId().getId(),
            mensagem.getConteudo(),
            mensagem.isLida(),
            mensagem.getDataEnvio()
        );
        
        entityManager.getTransaction().begin();
        if (entityManager.find(MensagemEntity.class, entity.getId()) != null) {
            entityManager.merge(entity);
        } else {
            entityManager.persist(entity);
        }
        entityManager.getTransaction().commit();
    }

    @Override
    public void remover(MensagemId id) {
        entityManager.getTransaction().begin();
        MensagemEntity entity = entityManager.find(MensagemEntity.class, id.getId());
        if (entity != null) {
            entityManager.remove(entity);
        }
        entityManager.getTransaction().commit();
    }

    @Override
    public Mensagem obter(MensagemId id) {
        MensagemEntity entity = entityManager.find(MensagemEntity.class, id.getId());
        if (entity == null) return null;
        return new Mensagem(new MensagemId(entity.getId()), new UsuarioId(entity.getRemetenteId()), new UsuarioId(entity.getDestinatarioId()), entity.getConteudo());
    }

    @Override
    public List<Mensagem> buscarConversa(UsuarioId usuarioA, UsuarioId usuarioB) {
        List<MensagemEntity> resultados = entityManager.createQuery(
            "SELECT m FROM MensagemEntity m WHERE (m.remetenteId = :a AND m.destinatarioId = :b) OR (m.remetenteId = :b AND m.destinatarioId = :a) ORDER BY m.dataEnvio ASC", 
            MensagemEntity.class)
            .setParameter("a", usuarioA.getId())
            .setParameter("b", usuarioB.getId())
            .getResultList();

        return resultados.stream().map(entity -> new Mensagem(
            new MensagemId(entity.getId()), new UsuarioId(entity.getRemetenteId()), new UsuarioId(entity.getDestinatarioId()), entity.getConteudo()
        )).collect(Collectors.toList());
    }

    @Override
    public int contarMensagensNaoLidas(UsuarioId destinatarioId) {
        Long contagem = entityManager.createQuery(
            "SELECT COUNT(m) FROM MensagemEntity m WHERE m.destinatarioId = :id AND m.lida = false", Long.class)
            .setParameter("id", destinatarioId.getId())
            .getSingleResult();
        return contagem.intValue();
    }
}