package com.requisitos.avaliacaofilmes.TelaScore.infraestrutura.analise.quiz;

import com.requisitos.avaliacaofilmes.TelaScore.dominio.analise.quiz.Alternativa;
import com.requisitos.avaliacaofilmes.TelaScore.dominio.analise.quiz.Pergunta;
import com.requisitos.avaliacaofilmes.TelaScore.dominio.analise.quiz.Quiz;
import com.requisitos.avaliacaofilmes.TelaScore.dominio.analise.quiz.QuizId;
import com.requisitos.avaliacaofilmes.TelaScore.dominio.analise.quiz.QuizRepositorio;
import com.requisitos.avaliacaofilmes.TelaScore.dominio.analise.quiz.TentativaQuiz;
import com.requisitos.avaliacaofilmes.TelaScore.dominio.identidade.usuario.UsuarioId;
import com.requisitos.avaliacaofilmes.TelaScore.infraestrutura.analise.quiz.entidades.AlternativaEntity;
import com.requisitos.avaliacaofilmes.TelaScore.infraestrutura.analise.quiz.entidades.PerguntaEntity;
import com.requisitos.avaliacaofilmes.TelaScore.infraestrutura.analise.quiz.entidades.QuizEntity;
import com.requisitos.avaliacaofilmes.TelaScore.infraestrutura.analise.quiz.entidades.TentativaQuizEntity;
import jakarta.persistence.EntityManager;
import java.util.List;

public class QuizRepositorioImpl implements QuizRepositorio {
    private final EntityManager entityManager;

    public QuizRepositorioImpl(EntityManager entityManager) {
        this.entityManager = entityManager;
    }

    @Override
    public void salvar(Quiz quiz) {
        QuizEntity entity = new QuizEntity();
        entity.setId(quiz.getId().getId());
        entity.setTitulo(quiz.getTitulo());
        entity.setDescricao(quiz.getDescricao());
        entity.setPerguntas(quiz.getPerguntas().stream()
                .map(this::mapearPerguntaEntity)
                .toList());

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
        return entity == null ? null : mapearDominio(entity);
    }

    @Override
    public List<Quiz> listar() {
        return entityManager.createQuery("SELECT q FROM QuizEntity q ORDER BY q.id DESC", QuizEntity.class)
                .getResultList()
                .stream()
                .map(this::mapearDominio)
                .toList();
    }

    @Override
    public void remover(QuizId id) {
        entityManager.getTransaction().begin();
        entityManager.createQuery("DELETE FROM TentativaQuizEntity t WHERE t.quizId = :quizId")
                .setParameter("quizId", id.getId())
                .executeUpdate();
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
                tentativa.getDataTentativa());

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
                    totalPerguntas.intValue());
        }).toList();
    }

    private PerguntaEntity mapearPerguntaEntity(Pergunta pergunta) {
        PerguntaEntity entity = new PerguntaEntity();
        entity.setTexto(pergunta.getEnunciado());
        entity.setAlternativas(pergunta.getAlternativas().stream()
                .map(this::mapearAlternativaEntity)
                .toList());
        return entity;
    }

    private AlternativaEntity mapearAlternativaEntity(Alternativa alternativa) {
        AlternativaEntity entity = new AlternativaEntity();
        entity.setTexto(alternativa.getTexto());
        entity.setCorreta(alternativa.isCorreta());
        return entity;
    }

    private Quiz mapearDominio(QuizEntity entity) {
        Quiz quiz = new Quiz(new QuizId(entity.getId()), entity.getTitulo(), entity.getDescricao());
        entity.getPerguntas().forEach(perguntaEntity -> {
            List<Alternativa> alternativas = perguntaEntity.getAlternativas().stream()
                    .map(alt -> new Alternativa(alt.getTexto(), alt.isCorreta()))
                    .toList();
            quiz.adicionarPergunta(new Pergunta(perguntaEntity.getTexto(), alternativas));
        });
        return quiz;
    }
}
