package com.requisitos.avaliacaofilmes.TelaScore.infraestrutura.catalogo.lista;

import com.requisitos.avaliacaofilmes.TelaScore.dominio.catalogo.lista.Lista;
import com.requisitos.avaliacaofilmes.TelaScore.dominio.catalogo.lista.ListaId;
import com.requisitos.avaliacaofilmes.TelaScore.dominio.catalogo.lista.TipoLista;
import com.requisitos.avaliacaofilmes.TelaScore.dominio.catalogo.lista.Visibilidade;
import com.requisitos.avaliacaofilmes.TelaScore.dominio.identidade.usuario.UsuarioId;
import com.requisitos.avaliacaofilmes.TelaScore.infraestrutura.catalogo.lista.entidades.ListaEntity;
import com.requisitos.avaliacaofilmes.TelaScore.infraestrutura.config.ConexaoBanco;
import jakarta.persistence.EntityManager;
import jakarta.persistence.EntityTransaction;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

class ListaRepositorioImplTest {

    private static final int ID_LISTA_1 = 99901;
    private static final int ID_LISTA_2 = 99902;
    private static final int DONO_ID = 99901;

    private ListaRepositorioImpl repositorio;

    @BeforeAll
    static void criarUsuarioDeTeste() {
        EntityManager em = ConexaoBanco.obterEntityManager();
        EntityTransaction tx = em.getTransaction();
        try {
            tx.begin();
            em.createNativeQuery(
                "INSERT INTO usuario (id, email, senha, papel_usuario, data_cadastro) VALUES (:id, :email, :senha, 'CINEFILO', CURRENT_TIMESTAMP) " +
                "ON DUPLICATE KEY UPDATE email = email"
            )
            .setParameter("id", DONO_ID)
            .setParameter("email", "usuario_teste_lista@test.com")
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
              .setParameter("id", DONO_ID)
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
        repositorio = new ListaRepositorioImpl();
        limparListasDeTeste();
    }

    @AfterEach
    void tearDown() {
        limparListasDeTeste();
    }

    @Test
    void salvar_devePersistirListaNoMysql() {
        Lista lista = new Lista(
                new ListaId(ID_LISTA_1),
                new UsuarioId(DONO_ID),
                "Lista de Teste",
                "Descrição de teste",
                false,
                Visibilidade.PUBLICA,
                TipoLista.NORMAL
        );

        repositorio.salvar(lista);

        Lista recuperada = repositorio.obter(new ListaId(ID_LISTA_1));
        assertNotNull(recuperada, "Lista deveria ter sido salva no banco");
        assertEquals("Lista de Teste", recuperada.getTitulo());
        assertEquals("Descrição de teste", recuperada.getDescricao());
        assertEquals(Visibilidade.PUBLICA, recuperada.getVisibilidade());
        assertEquals(TipoLista.NORMAL, recuperada.getTipo());
        assertFalse(recuperada.isRanqueada());
        assertFalse(recuperada.isColaborativa());
    }

    @Test
    void obter_deveRetornarNullParaIdInexistente() {
        Lista resultado = repositorio.obter(new ListaId(99999));

        assertNull(resultado);
    }

    @Test
    void salvar_deveAtualizarTituloExistente() {
        Lista lista = new Lista(
                new ListaId(ID_LISTA_1),
                new UsuarioId(DONO_ID),
                "Título Original",
                null,
                false,
                Visibilidade.PRIVADA,
                TipoLista.WATCHLIST
        );
        repositorio.salvar(lista);

        Lista atualizada = Lista.restaurar(
                new ListaId(ID_LISTA_1),
                new UsuarioId(DONO_ID),
                "Título Atualizado",
                null,
                false,
                Visibilidade.PRIVADA,
                TipoLista.WATCHLIST,
                false,
                new ArrayList<>(),
                new ArrayList<>()
        );
        repositorio.salvar(atualizada);

        Lista recuperada = repositorio.obter(new ListaId(ID_LISTA_1));
        assertNotNull(recuperada);
        assertEquals("Título Atualizado", recuperada.getTitulo());
    }

    @Test
    void remover_deveExcluirListaDoBanco() {
        Lista lista = new Lista(
                new ListaId(ID_LISTA_1),
                new UsuarioId(DONO_ID),
                "Para Remover",
                null,
                false,
                Visibilidade.PRIVADA,
                TipoLista.NORMAL
        );
        repositorio.salvar(lista);

        repositorio.remover(new ListaId(ID_LISTA_1));

        Lista resultado = repositorio.obter(new ListaId(ID_LISTA_1));
        assertNull(resultado, "Lista deveria ter sido removida do banco");
    }

    @Test
    void pesquisarPorDono_deveRetornarTodasAsListasDoUsuario() {
        Lista lista1 = new Lista(
                new ListaId(ID_LISTA_1),
                new UsuarioId(DONO_ID),
                "Lista 1",
                null,
                false,
                Visibilidade.PUBLICA,
                TipoLista.NORMAL
        );
        Lista lista2 = new Lista(
                new ListaId(ID_LISTA_2),
                new UsuarioId(DONO_ID),
                "Lista 2",
                null,
                false,
                Visibilidade.PRIVADA,
                TipoLista.WATCHLIST
        );
        repositorio.salvar(lista1);
        repositorio.salvar(lista2);

        List<Lista> resultado = repositorio.pesquisarPorDono(new UsuarioId(DONO_ID));

        assertEquals(2, resultado.size());
        assertTrue(resultado.stream().anyMatch(l -> l.getId().getId() == ID_LISTA_1));
        assertTrue(resultado.stream().anyMatch(l -> l.getId().getId() == ID_LISTA_2));
    }

    private void limparListasDeTeste() {
        EntityManager em = ConexaoBanco.obterEntityManager();
        EntityTransaction tx = em.getTransaction();
        try {
            tx.begin();
            for (int id : new int[]{ID_LISTA_1, ID_LISTA_2}) {
                ListaEntity entity = em.find(ListaEntity.class, id);
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
