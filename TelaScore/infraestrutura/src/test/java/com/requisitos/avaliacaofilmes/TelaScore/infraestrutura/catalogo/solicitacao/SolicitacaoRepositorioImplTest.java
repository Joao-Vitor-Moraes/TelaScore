package com.requisitos.avaliacaofilmes.TelaScore.infraestrutura.catalogo.solicitacao;

import com.requisitos.avaliacaofilmes.TelaScore.dominio.catalogo.solicitacao.SolicitacaoFilme;
import com.requisitos.avaliacaofilmes.TelaScore.dominio.catalogo.solicitacao.SolicitacaoId;
import com.requisitos.avaliacaofilmes.TelaScore.dominio.catalogo.solicitacao.StatusSolicitacao;
import com.requisitos.avaliacaofilmes.TelaScore.dominio.identidade.usuario.UsuarioId;
import com.requisitos.avaliacaofilmes.TelaScore.infraestrutura.catalogo.solicitacao.entidades.SolicitacaoFilmeEntity;
import com.requisitos.avaliacaofilmes.TelaScore.infraestrutura.config.ConexaoBanco;
import jakarta.persistence.EntityManager;
import jakarta.persistence.EntityTransaction;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

class SolicitacaoRepositorioImplTest {

    private static final int ID_SOLICITACAO_1 = 99901;
    private static final int ID_SOLICITACAO_2 = 99902;
    private static final int SOLICITANTE_ID = 99902;

    private SolicitacaoRepositorioImpl repositorio;

    @BeforeAll
    static void criarUsuarioDeTeste() {
        EntityManager em = ConexaoBanco.obterEntityManager();
        EntityTransaction tx = em.getTransaction();
        try {
            tx.begin();
            em.createNativeQuery(
                "INSERT INTO usuario (id, email, senha, papel_usuario) VALUES (:id, :email, :senha, 'CINEFILO') " +
                "ON DUPLICATE KEY UPDATE email = email"
            )
            .setParameter("id", SOLICITANTE_ID)
            .setParameter("email", "usuario_teste_solicitacao@test.com")
            .setParameter("senha", "senha_teste")
            .executeUpdate();
            tx.commit();
        } catch (Exception e) {
            if (tx.isActive()) tx.rollback();
            throw new RuntimeException("Falha ao criar usuário de teste", e);
        } finally {
            em.close();
        }
    }

    @AfterAll
    static void removerUsuarioDeTeste() {
        EntityManager em = ConexaoBanco.obterEntityManager();
        EntityTransaction tx = em.getTransaction();
        try {
            tx.begin();
            em.createNativeQuery("DELETE FROM usuario WHERE id = :id")
              .setParameter("id", SOLICITANTE_ID)
              .executeUpdate();
            tx.commit();
        } catch (Exception e) {
            if (tx.isActive()) tx.rollback();
        } finally {
            em.close();
        }
    }

    @BeforeEach
    void setUp() {
        repositorio = new SolicitacaoRepositorioImpl();
        limparSolicitacoesDeTeste();
    }

    @AfterEach
    void tearDown() {
        limparSolicitacoesDeTeste();
    }

    @Test
    void salvar_devePersistirSolicitacaoNoMysql() {
        SolicitacaoFilme solicitacao = new SolicitacaoFilme(
                new SolicitacaoId(ID_SOLICITACAO_1),
                new UsuarioId(SOLICITANTE_ID),
                "Filme de Teste"
        );
        solicitacao.setJustificativa("Justificativa de teste");

        repositorio.salvar(solicitacao);

        SolicitacaoFilme recuperada = repositorio.obter(new SolicitacaoId(ID_SOLICITACAO_1));
        assertNotNull(recuperada, "Solicitação deveria ter sido salva no banco");
        assertEquals("Filme de Teste", recuperada.getTituloSugerido());
        assertEquals("Justificativa de teste", recuperada.getJustificativa());
        assertEquals(StatusSolicitacao.PENDENTE, recuperada.getStatus());
        assertNotNull(recuperada.getDataCriacao());
    }

    @Test
    void obter_deveRetornarNullParaIdInexistente() {
        SolicitacaoFilme resultado = repositorio.obter(new SolicitacaoId(99999));

        assertNull(resultado);
    }

    @Test
    void salvar_deveAtualizarStatusParaAprovado() {
        SolicitacaoFilme solicitacao = new SolicitacaoFilme(
                new SolicitacaoId(ID_SOLICITACAO_1),
                new UsuarioId(SOLICITANTE_ID),
                "Filme para Aprovação"
        );
        repositorio.salvar(solicitacao);

        solicitacao.aprovar();
        repositorio.salvar(solicitacao);

        SolicitacaoFilme recuperada = repositorio.obter(new SolicitacaoId(ID_SOLICITACAO_1));
        assertNotNull(recuperada);
        assertEquals(StatusSolicitacao.APROVADA, recuperada.getStatus());
    }

    @Test
    void pesquisarPorStatus_deveRetornarApenasAsSolicitacoesComStatusCorreto() {
        SolicitacaoFilme pendente = new SolicitacaoFilme(
                new SolicitacaoId(ID_SOLICITACAO_1),
                new UsuarioId(SOLICITANTE_ID),
                "Filme Pendente"
        );
        SolicitacaoFilme aAprovar = new SolicitacaoFilme(
                new SolicitacaoId(ID_SOLICITACAO_2),
                new UsuarioId(SOLICITANTE_ID),
                "Filme Aprovado"
        );
        repositorio.salvar(pendente);
        repositorio.salvar(aAprovar);
        aAprovar.aprovar();
        repositorio.salvar(aAprovar);

        List<SolicitacaoFilme> pendentes = repositorio.pesquisarPorStatus(StatusSolicitacao.PENDENTE);

        assertTrue(pendentes.stream().anyMatch(s -> s.getId().getId() == ID_SOLICITACAO_1),
                "A solicitação pendente deveria estar na lista");
        assertTrue(pendentes.stream().noneMatch(s -> s.getId().getId() == ID_SOLICITACAO_2),
                "A solicitação aprovada não deveria estar na lista de pendentes");
    }

    @Test
    void pesquisarPorSolicitante_deveRetornarTodasAsSolicitacoesDoUsuario() {
        SolicitacaoFilme s1 = new SolicitacaoFilme(
                new SolicitacaoId(ID_SOLICITACAO_1),
                new UsuarioId(SOLICITANTE_ID),
                "Primeiro Filme"
        );
        SolicitacaoFilme s2 = new SolicitacaoFilme(
                new SolicitacaoId(ID_SOLICITACAO_2),
                new UsuarioId(SOLICITANTE_ID),
                "Segundo Filme"
        );
        repositorio.salvar(s1);
        repositorio.salvar(s2);

        List<SolicitacaoFilme> resultado = repositorio.pesquisarPorSolicitante(new UsuarioId(SOLICITANTE_ID));

        assertEquals(2, resultado.size());
        assertTrue(resultado.stream().anyMatch(s -> s.getTituloSugerido().equals("Primeiro Filme")));
        assertTrue(resultado.stream().anyMatch(s -> s.getTituloSugerido().equals("Segundo Filme")));
    }

    private void limparSolicitacoesDeTeste() {
        EntityManager em = ConexaoBanco.obterEntityManager();
        EntityTransaction tx = em.getTransaction();
        try {
            tx.begin();
            for (int id : new int[]{ID_SOLICITACAO_1, ID_SOLICITACAO_2}) {
                SolicitacaoFilmeEntity entity = em.find(SolicitacaoFilmeEntity.class, id);
                if (entity != null) {
                    em.remove(entity);
                }
            }
            tx.commit();
        } catch (Exception e) {
            if (tx.isActive()) tx.rollback();
        } finally {
            em.close();
        }
    }
}
