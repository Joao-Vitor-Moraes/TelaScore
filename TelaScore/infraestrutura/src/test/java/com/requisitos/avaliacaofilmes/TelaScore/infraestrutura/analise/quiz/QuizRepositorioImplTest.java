package com.requisitos.avaliacaofilmes.TelaScore.infraestrutura.analise.quiz;

import com.requisitos.avaliacaofilmes.TelaScore.dominio.analise.quiz.*;
import com.requisitos.avaliacaofilmes.TelaScore.dominio.identidade.usuario.UsuarioId;
import com.requisitos.avaliacaofilmes.TelaScore.infraestrutura.config.ConexaoBanco;
import jakarta.persistence.EntityManager;
import jakarta.persistence.EntityTransaction;
import org.junit.jupiter.api.*;
import java.util.List;
import static org.junit.jupiter.api.Assertions.*;

class QuizRepositorioImplTest {

    private static final int USUARIO_TESTE_ID = 99955;
    private static final int QUIZ_TESTE_ID = 88855;
    private QuizRepositorioImpl repositorio;

    @BeforeAll
    static void prepararMassaDeDados() {
        EntityManager em = ConexaoBanco.obterEntityManager();
        EntityTransaction tx = em.getTransaction();
        try {
            tx.begin();
            // Cria usuário fantasma isolado para FK da tentativa 
            em.createNativeQuery("""
                    INSERT INTO usuario (id, email, senha, papel_usuario, data_cadastro)
                    VALUES (:id, 'quiz_usr@test.com', '123', 'CINEFILO', CURRENT_TIMESTAMP)
                    ON DUPLICATE KEY UPDATE id=id
                    """)
              .setParameter("id", USUARIO_TESTE_ID).executeUpdate();
            
            // Cria um quiz base direto via SQL 
            em.createNativeQuery("INSERT INTO quiz (id, titulo, descricao) VALUES (:id, 'Cinema Teste', 'Desc') ON DUPLICATE KEY UPDATE id=id")
              .setParameter("id", QUIZ_TESTE_ID).executeUpdate();

            // Cria uma pergunta vinculada ao quiz para o teste do Passo 1 não dar divisão por zero 
            em.createNativeQuery("INSERT INTO pergunta (id, quiz_id, texto) VALUES (77755, :qId, 'Pergunta Teste') ON DUPLICATE KEY UPDATE id=id")
              .setParameter("qId", QUIZ_TESTE_ID).executeUpdate();
            
            tx.commit();
        } catch (Exception e) {
            if (tx.isActive()) tx.rollback();
        } finally { em.close(); }
    }

    @BeforeEach
    void setUp() {
        EntityManager em = ConexaoBanco.obterEntityManager();
        repositorio = new QuizRepositorioImpl(em);
        limparTentativas();
    }

    @Test
    void buscarTentativasPorUsuario_deveRetornarListaDeTentativasDoBanco() {
        EntityManager em = ConexaoBanco.obterEntityManager();
        em.getTransaction().begin();
        // Simula uma tentativa direto no banco 
        em.createNativeQuery("INSERT INTO tentativa_quiz (quiz_id, usuario_id, pontuacao, data_tentativa) VALUES (:qId, :uId, 1, CURRENT_TIMESTAMP)")
          .setParameter("qId", QUIZ_TESTE_ID)
          .setParameter("uId", USUARIO_TESTE_ID)
          .executeUpdate();
        em.getTransaction().commit();
        em.close();

        List<TentativaQuiz> tentativas = repositorio.buscarTentativasPorUsuario(new UsuarioId(USUARIO_TESTE_ID));
        
        assertFalse(tentativas.isEmpty(), "Deveria listar a tentativa cadastrada");
        assertEquals(QUIZ_TESTE_ID, tentativas.get(0).getQuizId().getId());
        assertEquals(1, tentativas.get(0).getAcertos());
    }

    @Test
    void obter_deveRetornarNullParaIdInexistente() {
        Quiz resultado = repositorio.obter(new QuizId(12345));
        assertNull(resultado);
    }

    private void limparTentativas() {
        EntityManager em = ConexaoBanco.obterEntityManager();
        em.getTransaction().begin();
        em.createNativeQuery("DELETE FROM tentativa_quiz WHERE usuario_id = :uId").setParameter("uId", USUARIO_TESTE_ID).executeUpdate();
        em.getTransaction().commit();
        em.close();
    }
}
