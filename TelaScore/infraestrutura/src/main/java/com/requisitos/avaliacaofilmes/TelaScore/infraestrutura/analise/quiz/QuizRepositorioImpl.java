package com.requisitos.avaliacaofilmes.TelaScore.infraestrutura.analise.quiz;

import java.util.List;

import com.requisitos.avaliacaofilmes.TelaScore.dominio.analise.quiz.*;
import com.requisitos.avaliacaofilmes.TelaScore.dominio.identidade.usuario.UsuarioId;
import com.requisitos.avaliacaofilmes.TelaScore.infraestrutura.analise.quiz.entidades.QuizEntity;
import com.requisitos.avaliacaofilmes.TelaScore.infraestrutura.analise.quiz.entidades.TentativaQuizEntity;

import jakarta.persistence.EntityManager;

public class QuizRepositorioImpl implements QuizRepositorio {
    private final EntityManager entityManager;

    public QuizRepositorioImpl(EntityManager entityManager) {
        this.entityManager = entityManager;
    }

    @Override
    public void salvar(Quiz quiz) {
        // Mapeamento simplificado: Converte o domínio na entidade JPA para persistir
        QuizEntity entity = new QuizEntity(); // preencha com os dados vindos de 'quiz'
        
        entityManager.getTransaction().begin();
        if (entityManager.find(QuizEntity.class, entity.getId()) != null) {
            entityManager.merge(entity);
        } else {
            entityManager.persist(entity);
        }
        entityManager.getTransaction().commit();
    }

    @Override
    public Quiz obter(QuizId id) {
        QuizEntity entity = entityManager.find(QuizEntity.class, id.getId());
        if (entity == null) return null;
        // Converte de volta da Entidade JPA para o seu objeto de Domínio do Quiz e retorna
        return null; 
    }

    @Override
    public void remover(QuizId id) {
        entityManager.getTransaction().begin();
        QuizEntity entity = entityManager.find(QuizEntity.class, id.getId());
        if (entity != null) {
            entityManager.remove(entity);
        }
        entityManager.getTransaction().commit();
    }
    @Override
    public List<TentativaQuiz> buscarTentativasPorUsuario(UsuarioId usuarioId) {
        List<TentativaQuizEntity> resultados = entityManager.createQuery(
            "SELECT t FROM TentativaQuizEntity t WHERE t.usuarioId = :usrId ORDER BY t.dataTentativa DESC", 
            TentativaQuizEntity.class)
            .setParameter("usrId", usuarioId.getId())
            .getResultList();

        return resultados.stream().map(entity -> {
            // Descobre o total de perguntas que o quiz possui cadastrado
            Long totalPerguntas = entityManager.createQuery(
                "SELECT COUNT(p) FROM QuizEntity q JOIN q.perguntas p WHERE q.id = :qId", Long.class)
                .setParameter("qId", entity.getQuizId())
                .getSingleResult();

            // Agora passamos os 4 parâmetros exatos exigidos pelo construtor do Domínio
            return new TentativaQuiz(
                new QuizId(entity.getQuizId()),
                new UsuarioId(entity.getUsuarioId()),
                entity.getPontuacao(),       // acertos
                totalPerguntas.intValue()    // total de perguntas
            );
        }).collect(java.util.stream.Collectors.toList());
    }

    @Override
    public void salvarTentativa(TentativaQuiz tentativa) {
        // Converte o objeto de domínio para a entidade que o JPA entende
        TentativaQuizEntity entity = new TentativaQuizEntity(
            tentativa.getQuizId().getId(),
            tentativa.getUsuarioId().getId(),
            tentativa.getAcertos(),
            tentativa.getDataTentativa()
        );

        entityManager.getTransaction().begin();
        // Como tentativas são sempre registros novos (histórico), usamos apenas o persist
        entityManager.persist(entity);
        entityManager.getTransaction().commit();
    }
}