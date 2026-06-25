package com.requisitos.avaliacaofilmes.TelaScore.infraestrutura.analise.recompensa;

import static org.junit.jupiter.api.Assertions.*;

import java.util.List;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import com.requisitos.avaliacaofilmes.TelaScore.dominio.analise.recompensa.AcaoPontuada;
import com.requisitos.avaliacaofilmes.TelaScore.dominio.analise.recompensa.Pontos;
import com.requisitos.avaliacaofilmes.TelaScore.dominio.analise.recompensa.RegistroPontuacao;
import com.requisitos.avaliacaofilmes.TelaScore.dominio.analise.recompensa.RegistroPontuacaoId;
import com.requisitos.avaliacaofilmes.TelaScore.dominio.identidade.usuario.UsuarioId;
import com.requisitos.avaliacaofilmes.TelaScore.infraestrutura.config.ConexaoBanco;

import jakarta.persistence.EntityManager;
import jakarta.persistence.EntityTransaction;

public class RegistroPontuacaoRepositorioImplTest {

    private RegistroPontuacaoRepositorioImpl repositorio;

    private final UsuarioId testeUsuarioId = new UsuarioId(7777);
    private final RegistroPontuacaoId testeRegistroId = new RegistroPontuacaoId(9999);

    @BeforeEach
    public void setUp() {
        repositorio = new RegistroPontuacaoRepositorioImpl();
        limparBancoDeDados();
    }

    @AfterEach
    public void tearDown() {
        limparBancoDeDados();
    }

    private void limparBancoDeDados() {
        EntityManager em = ConexaoBanco.obterEntityManager();
        EntityTransaction tx = em.getTransaction();
        try {
            tx.begin();
            em.createNativeQuery("DELETE FROM registro_pontuacao WHERE usuario_id = :usuarioId OR id IN (9991, 9992, 9999)")
                    .setParameter("usuarioId", testeUsuarioId.getId())
                    .executeUpdate();
            tx.commit();
        } catch (Exception e) {
            if (tx.isActive()) tx.rollback();
        } finally {
            em.close();
        }
    }

    @Test
    public void deveSalvarEBuscarRegistroPorUsuario() {
        RegistroPontuacao registro = new RegistroPontuacao(
            testeRegistroId,
            testeUsuarioId,
            new Pontos(100),
            AcaoPontuada.AVALIAR_FILME
        );

        repositorio.salvar(registro);

        List<RegistroPontuacao> registros = repositorio.buscarPorUsuario(testeUsuarioId);
        assertFalse(registros.isEmpty(), "A lista de registros nao deveria estar vazia.");
        assertEquals(testeUsuarioId.getId(), registros.get(0).getUsuarioId().getId());
        assertEquals(AcaoPontuada.AVALIAR_FILME, registros.get(0).getAcao());
    }

    @Test
    public void deveCalcularTotalDePontosPorUsuario() {
        RegistroPontuacao primeiroRegistro = new RegistroPontuacao(
            new RegistroPontuacaoId(9991),
            testeUsuarioId,
            new Pontos(100),
            AcaoPontuada.AVALIAR_FILME
        );

        RegistroPontuacao segundoRegistro = new RegistroPontuacao(
            new RegistroPontuacaoId(9992),
            testeUsuarioId,
            new Pontos(300),
            AcaoPontuada.CONVIDAR_AMIGO
        );

        repositorio.salvar(primeiroRegistro);
        repositorio.salvar(segundoRegistro);

        Integer total = repositorio.calcularTotalPontos(testeUsuarioId);
        assertNotNull(total, "O total de pontos nao deveria ser nulo.");
        assertEquals(400, total, "O total de pontos deveria ser 400.");
    }

    @Test
    public void deveRetornarZeroQuandoUsuarioSemPontos() {
        UsuarioId usuarioSemPontos = new UsuarioId(6666);

        Integer total = repositorio.calcularTotalPontos(usuarioSemPontos);

        assertEquals(0, total, "Usuario sem registros deveria ter 0 pontos.");
    }

    @Test
    public void deveRetornarListaVaziaParaUsuarioSemRegistros() {
        UsuarioId usuarioSemRegistros = new UsuarioId(5555);

        List<RegistroPontuacao> registros = repositorio.buscarPorUsuario(usuarioSemRegistros);

        assertNotNull(registros, "A lista nao deveria ser nula.");
        assertTrue(registros.isEmpty(), "A lista deveria estar vazia para usuario sem registros.");
    }
}
