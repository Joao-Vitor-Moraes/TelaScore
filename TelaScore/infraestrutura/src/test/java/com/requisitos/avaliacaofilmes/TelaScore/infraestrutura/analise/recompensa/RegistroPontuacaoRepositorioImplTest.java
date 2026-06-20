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
    try {
        repositorio.buscarPorUsuario(testeUsuarioId); // sem atribuir variável
    } catch (Exception e) {
        // ignora
    }
}

    @Test
    public void deveSalvarEBuscarRegistroPorUsuario() {
        // Arrange
        RegistroPontuacao registro = new RegistroPontuacao(
            testeRegistroId,
            testeUsuarioId,
            new Pontos(100),
            AcaoPontuada.AVALIAR_FILME
        );

        // Act
        repositorio.salvar(registro);

        // Assert
        List<RegistroPontuacao> registros = repositorio.buscarPorUsuario(testeUsuarioId);
        assertFalse(registros.isEmpty(), "A lista de registros não deveria estar vazia.");
        assertEquals(testeUsuarioId.getId(), registros.get(0).getUsuarioId().getId());
        assertEquals(AcaoPontuada.AVALIAR_FILME, registros.get(0).getAcao());
    }

    @Test
    public void deveCalcularTotalDePontosPorUsuario() {
        // Arrange
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

        // Act
        repositorio.salvar(primeiroRegistro);
        repositorio.salvar(segundoRegistro);

        // Assert
        Integer total = repositorio.calcularTotalPontos(testeUsuarioId);
        assertNotNull(total, "O total de pontos não deveria ser nulo.");
        assertEquals(400, total, "O total de pontos deveria ser 400.");
    }

    @Test
    public void deveRetornarZeroQuandoUsuarioSemPontos() {
        // Arrange — usuário sem nenhum registro
        UsuarioId usuarioSemPontos = new UsuarioId(6666);

        // Act
        Integer total = repositorio.calcularTotalPontos(usuarioSemPontos);

        // Assert
        assertEquals(0, total, "Usuário sem registros deveria ter 0 pontos.");
    }

    @Test
    public void deveRetornarListaVaziaParaUsuarioSemRegistros() {
        // Arrange
        UsuarioId usuarioSemRegistros = new UsuarioId(5555);

        // Act
        List<RegistroPontuacao> registros = repositorio.buscarPorUsuario(usuarioSemRegistros);

        // Assert
        assertNotNull(registros, "A lista não deveria ser nula.");
        assertTrue(registros.isEmpty(), "A lista deveria estar vazia para usuário sem registros.");
    }
}