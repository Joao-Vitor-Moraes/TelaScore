package com.requisitos.avaliacaofilmes.TelaScore.infraestrutura.social.comunidade;

import static org.junit.jupiter.api.Assertions.*;

import java.util.List;

import com.requisitos.avaliacaofilmes.TelaScore.dominio.social.comunidade.*;
import com.requisitos.avaliacaofilmes.TelaScore.infraestrutura.config.ConexaoBanco;
import jakarta.persistence.EntityManager;
import jakarta.persistence.EntityTransaction;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import com.requisitos.avaliacaofilmes.TelaScore.dominio.identidade.usuario.UsuarioId;

public class ComunidadeRepositorioImplTest {

    private ComunidadeRepositorioImpl repositorio;

    private final ComunidadeId testeComunidadeId = new ComunidadeId(9999);
    private final UsuarioId testeUsuarioId = new UsuarioId(8888);

    @BeforeEach
    public void setUp() {
        repositorio = new ComunidadeRepositorioImpl();
        limparBancoDeDados();
        criarUsuarioDeTeste();
    }

    @AfterEach
    public void tearDown() {
        limparBancoDeDados();
        removerUsuarioDeTeste();
    }

    private void limparBancoDeDados() {
        try {
            repositorio.excluirComunidade(testeComunidadeId);
        } catch (Exception e) {
        }
    }

    private void criarUsuarioDeTeste() {
        EntityManager em = ConexaoBanco.obterEntityManager();
        EntityTransaction tx = em.getTransaction();
        try {
            tx.begin();
            em.createNativeQuery("""
                    INSERT INTO usuario (id, email, senha, papel_usuario, data_cadastro, apelido)
                    VALUES (:id, 'membro_comunidade@test.com', '123', 'CINEFILO', CURRENT_TIMESTAMP, 'membro_comunidade')
                    ON DUPLICATE KEY UPDATE id=id
                    """)
                    .setParameter("id", testeUsuarioId.getId())
                    .executeUpdate();
            tx.commit();
        } catch (Exception e) {
            if (tx.isActive()) tx.rollback();
            throw e;
        } finally {
            em.close();
        }
    }

    private void removerUsuarioDeTeste() {
        EntityManager em = ConexaoBanco.obterEntityManager();
        EntityTransaction tx = em.getTransaction();
        try {
            tx.begin();
            em.createNativeQuery("DELETE FROM usuario WHERE id = :id")
                    .setParameter("id", testeUsuarioId.getId())
                    .executeUpdate();
            tx.commit();
        } catch (Exception e) {
            if (tx.isActive()) tx.rollback();
        } finally {
            em.close();
        }
    }

    @Test
    public void deveSalvarEObterComunidadeEMembro() {
        Comunidade novaComunidade = new Comunidade(testeComunidadeId, "Clube do Filme", "Comunidade para testes");
        repositorio.salvarComunidade(novaComunidade);

        Comunidade comunidadeSalva = repositorio.obterComunidade(testeComunidadeId);
        assertNotNull(comunidadeSalva, "A comunidade deveria ter sido encontrada no banco.");
        assertEquals("Clube do Filme", comunidadeSalva.getNome());

        MembroComunidade membro = new MembroComunidade(testeComunidadeId, testeUsuarioId, PapelComunidade.MEMBRO);
        repositorio.salvarMembro(membro);

        List<MembroComunidade> membros = repositorio.buscarMembrosDaComunidade(testeComunidadeId);
        assertFalse(membros.isEmpty(), "A lista de membros não deveria estar vazia.");
        assertEquals(testeUsuarioId.getId(), membros.get(0).getUsuarioId().getId());

        List<ComunidadeUsuarioResumo> comunidadesDoUsuario = repositorio.buscarComunidadesDoUsuario(testeUsuarioId);
        assertFalse(comunidadesDoUsuario.isEmpty(), "O usuário deveria estar associado a pelo menos uma comunidade.");

        ComunidadeUsuarioResumo resumo = comunidadesDoUsuario.get(0);
        assertEquals("Clube do Filme", resumo.nome());
        assertEquals("MEMBRO", resumo.papel());

        repositorio.removerMembro(testeComunidadeId, testeUsuarioId);

        List<MembroComunidade> membrosAposRemocao = repositorio.buscarMembrosDaComunidade(testeComunidadeId);
        assertTrue(membrosAposRemocao.isEmpty(), "A lista de membros deveria estar vazia após a coleção.");
    }
}
