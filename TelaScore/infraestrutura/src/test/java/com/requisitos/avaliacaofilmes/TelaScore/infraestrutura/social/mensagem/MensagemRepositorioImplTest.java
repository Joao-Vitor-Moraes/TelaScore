package com.requisitos.avaliacaofilmes.TelaScore.infraestrutura.social.mensagem;

import com.requisitos.avaliacaofilmes.TelaScore.dominio.social.mensagem.*;
import com.requisitos.avaliacaofilmes.TelaScore.dominio.identidade.usuario.UsuarioId;
import com.requisitos.avaliacaofilmes.TelaScore.infraestrutura.config.ConexaoBanco;
import jakarta.persistence.EntityManager;
import jakarta.persistence.EntityTransaction;
import org.junit.jupiter.api.*;
import static org.junit.jupiter.api.Assertions.*;

class MensagemRepositorioImplTest {

    private static final int REMETENTE_ID = 99911;
    private static final int DESTINATARIO_ID = 99912;
    private static final int MENSAGEM_ID = 99915;

    private MensagemRepositorioImpl repositorio;

    @BeforeAll
    static void criarUsuariosDeTeste() {
        EntityManager em = ConexaoBanco.obterEntityManager();
        EntityTransaction tx = em.getTransaction();
        try {
            tx.begin();
            // Insere o remetente
            em.createNativeQuery("INSERT INTO usuario (id, email, senha) VALUES (:id, 'rem@t.com', '123') ON DUPLICATE KEY UPDATE id=id").setParameter("id", REMETENTE_ID).executeUpdate();
            // Insere o destinatário
            em.createNativeQuery("INSERT INTO usuario (id, email, senha) VALUES (:id, 'dest@t.com', '123') ON DUPLICATE KEY UPDATE id=id").setParameter("id", DESTINATARIO_ID).executeUpdate();
            tx.commit();
        } catch (Exception e) {
            if (tx.isActive()) tx.rollback();
        } finally { em.close(); }
    }

    @BeforeEach
    void setUp() {
        EntityManager em = ConexaoBanco.obterEntityManager();
        repositorio = new MensagemRepositorioImpl(em);
        limparMensagens();
    }

    @Test
    void salvar_devePersistirMensagemNoBanco() {
        Mensagem mensagem = new Mensagem(
            new MensagemId(MENSAGEM_ID),
            new UsuarioId(REMETENTE_ID),
            new UsuarioId(DESTINATARIO_ID),
            "Olá, isso é um teste de infraestrutura!"
        );

        repositorio.salvar(mensagem);

        Mensagem recuperada = repositorio.obter(new MensagemId(MENSAGEM_ID));
        assertNotNull(recuperada);
        assertEquals("Olá, isso é um teste de infraestrutura!", recuperada.getConteudo());
    }

    private void limparMensagens() {
        EntityManager em = ConexaoBanco.obterEntityManager();
        em.getTransaction().begin();
        em.createNativeQuery("DELETE FROM mensagem WHERE id = :id").setParameter("id", MENSAGEM_ID).executeUpdate();
        em.getTransaction().commit();
        em.close();
    }
}