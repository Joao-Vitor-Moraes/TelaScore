package com.requisitos.avaliacaofilmes.TelaScore.infraestrutura.informacao.noticia;

import static org.junit.jupiter.api.Assertions.*;

import com.requisitos.avaliacaofilmes.TelaScore.dominio.identidade.usuario.UsuarioId;
import com.requisitos.avaliacaofilmes.TelaScore.dominio.informacao.noticia.CategoriaNoticia;
import com.requisitos.avaliacaofilmes.TelaScore.dominio.informacao.noticia.Noticia;
import com.requisitos.avaliacaofilmes.TelaScore.dominio.informacao.noticia.NoticiaId;
import com.requisitos.avaliacaofilmes.TelaScore.dominio.informacao.noticia.NoticiaRepositorio;
import com.requisitos.avaliacaofilmes.TelaScore.infraestrutura.config.ConexaoBanco;
import jakarta.persistence.EntityManager;
import jakarta.persistence.EntityTransaction;
import java.util.List;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

public class NoticiaRepositorioImplTest {

    private final NoticiaRepositorio repositorio = new NoticiaRepositorioImpl();
    private static final int AUTOR_TESTE_ID = 123;

    @BeforeEach
    void criarAutorDeTeste() {
        EntityManager em = ConexaoBanco.obterEntityManager();
        EntityTransaction tx = em.getTransaction();
        try {
            tx.begin();
            em.createNativeQuery("""
                    INSERT INTO usuario (id, email, senha, papel_usuario, data_cadastro, apelido)
                    VALUES (:id, 'autor_noticia@test.com', '123', 'CINEFILO', CURRENT_TIMESTAMP, 'autor_noticia')
                    ON DUPLICATE KEY UPDATE id=id
                    """)
                    .setParameter("id", AUTOR_TESTE_ID)
                    .executeUpdate();
            tx.commit();
        } catch (Exception e) {
            if (tx.isActive()) tx.rollback();
            throw e;
        } finally {
            em.close();
        }
    }

    @AfterEach
    void removerAutorDeTeste() {
        EntityManager em = ConexaoBanco.obterEntityManager();
        EntityTransaction tx = em.getTransaction();
        try {
            tx.begin();
            em.createNativeQuery("DELETE FROM noticia WHERE autor_id = :id").setParameter("id", AUTOR_TESTE_ID).executeUpdate();
            em.createNativeQuery("DELETE FROM usuario WHERE id = :id").setParameter("id", AUTOR_TESTE_ID).executeUpdate();
            tx.commit();
        } catch (Exception e) {
            if (tx.isActive()) tx.rollback();
        } finally {
            em.close();
        }
    }

    @Test
    public void deveSalvarEBuscarUmaNoticiaNoBanco() {
        NoticiaId noticiaId = new NoticiaId(888);
        UsuarioId autorId = new UsuarioId(AUTOR_TESTE_ID);

        Noticia noticia = new Noticia(
                noticiaId,
                autorId,
                "Festival de Cannes anuncia os vencedores da Palma de Ouro",
                "O júri oficial revelou a lista dos filmes premiados nesta edição histórica do festival internacional.",
                CategoriaNoticia.LANCAMENTO
        );

        repositorio.salvar(noticia);

        Noticia buscada = repositorio.obter(noticiaId);
        assertNotNull(buscada);
        assertEquals("Festival de Cannes anuncia os vencedores da Palma de Ouro", buscada.getTitulo());
        assertEquals(CategoriaNoticia.LANCAMENTO, buscada.getCategoria());

        List<Noticia> recentes = repositorio.buscarRecentes(1);
        assertTrue(recentes.size() > 0);

        List<Noticia> porFiltro = repositorio.buscarPorFiltros("Cannes", CategoriaNoticia.LANCAMENTO);
        assertTrue(porFiltro.size() > 0);

        repositorio.remover(noticiaId);
        assertNull(repositorio.obter(noticiaId));
    }
}
