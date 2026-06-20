package com.requisitos.avaliacaofilmes.TelaScore.infraestrutura.analise.quiz;

import com.requisitos.avaliacaofilmes.TelaScore.dominio.analise.quiz.*;
import com.requisitos.avaliacaofilmes.TelaScore.dominio.identidade.usuario.UsuarioId;
import com.requisitos.avaliacaofilmes.TelaScore.infraestrutura.analise.quiz.entidades.*;
import jakarta.persistence.EntityManager;
import java.util.List;

public class QuizRepositorioImpl implements QuizRepositorio {
    private final EntityManager entityManager;

    public QuizRepositorioImpl(EntityManager entityManager) {
        this.entityManager = entityManager;
    }

    private Quiz mapearParaDominio(QuizEntity entity) {
        Quiz quiz = new Quiz(
            new QuizId(entity.getId()),
            entity.getTitulo(),
            entity.getDescricao()
        );

        entity.getPerguntas().forEach(perguntaEntity -> {
            List<Alternativa> alternativasDominio = perguntaEntity.getAlternativas().stream().map(altEntity ->
                new Alternativa(altEntity.getTexto(), altEntity.isCorreta())
            ).collect(java.util.stream.Collectors.toList());

            Pergunta perguntaDominio = new Pergunta(perguntaEntity.getTexto(), alternativasDominio);
            quiz.adicionarPergunta(perguntaDominio);
        });

        return quiz;
    }

    @Override
    public void salvar(Quiz quiz) {
        QuizEntity entity = new QuizEntity();
        
        // Define o ID se o quiz já existir no banco (para caso de atualização/merge)
        if (quiz.getId() != null) {
            entity.setId(quiz.getId().getId());
        }
        entity.setTitulo(quiz.getTitulo());
        entity.setDescricao(quiz.getDescricao());

        // CONVERSÃO: Transforma o Domínio (Quiz -> Pergunta) em Entidades JPA
        List<PerguntaEntity> perguntasEntities = quiz.getPerguntas().stream().map(pDom -> {
            PerguntaEntity pEntity = new PerguntaEntity();
            
            // AQUI ESTÁ O AJUSTE: Usando getEnunciado() que existe no seu Domínio!
            pEntity.setTexto(pDom.getEnunciado()); 

            // Transforma as Alternativas do Domínio para Entidades JPA
            List<AlternativaEntity> alternativasEntities = pDom.getAlternativas().stream().map(aDom -> {
                AlternativaEntity aEntity = new AlternativaEntity();
                aEntity.setTexto(aDom.getTexto());
                aEntity.setCorreta(aDom.isCorreta());
                return aEntity;
            }).collect(java.util.stream.Collectors.toList());

            pEntity.setAlternativas(alternativasEntities);
            return pEntity;
        }).collect(java.util.stream.Collectors.toList());

        entity.setPerguntas(perguntasEntities);

        // Faz a mágica acontecer no MySQL através do Hibernate nativo do projeto
        entityManager.getTransaction().begin();
        if (entityManager.find(QuizEntity.class, entity.getId()) != null) {
            entityManager.merge(entity);
        } else {
            entityManager.persist(entity);
        }
        entityManager.getTransaction().commit();
    }

    @Override
    public List<Quiz> listarTodos() {
        List<QuizEntity> entities = entityManager.createQuery(
            "SELECT DISTINCT q FROM QuizEntity q " +
            "LEFT JOIN FETCH q.perguntas p " +
            "LEFT JOIN FETCH p.alternativas",
            QuizEntity.class
        ).getResultList();

        return entities.stream()
            .map(this::mapearParaDominio)
            .collect(java.util.stream.Collectors.toList());
    }

    @Override
    public Quiz obter(QuizId id) { 
        QuizEntity entity = entityManager.find(QuizEntity.class, id.getId());
        if (entity == null) return null;

        return mapearParaDominio(entity);
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
    public void salvarTentativa(TentativaQuiz tentativa) {
        TentativaQuizEntity entity = new TentativaQuizEntity(
            tentativa.getQuizId().getId(),
            tentativa.getUsuarioId().getId(),
            tentativa.getAcertos(),
            tentativa.getDataTentativa()
        );

        entityManager.getTransaction().begin();
        entityManager.persist(entity);
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
            Long totalPerguntas = entityManager.createQuery(
                "SELECT COUNT(p) FROM QuizEntity q JOIN q.perguntas p WHERE q.id = :qId", Long.class)
                .setParameter("qId", entity.getQuizId())
                .getSingleResult();

            return new TentativaQuiz(
                new QuizId(entity.getQuizId()),
                new UsuarioId(entity.getUsuarioId()),
                entity.getPontuacao(),
                totalPerguntas.intValue()
            );
        }).collect(java.util.stream.Collectors.toList());
    }
}