package com.requisitos.avaliacaofilmes.TelaScore.infraestrutura.social.comunidade;

import static org.junit.jupiter.api.Assertions.*;

import java.util.List;

import com.requisitos.avaliacaofilmes.TelaScore.dominio.social.comunidade.*;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import com.requisitos.avaliacaofilmes.TelaScore.dominio.identidade.usuario.UsuarioId;

public class ComunidadeRepositorioImplTest {

    private ComunidadeRepositorioImpl repositorio;

    @BeforeEach
    public void setUp() {
        repositorio = new ComunidadeRepositorioImpl();
    }

    @Test
    public void deveSalvarEObterComunidadeEMembro() {
        ComunidadeId comunidadeId = new ComunidadeId(100);
        UsuarioId usuarioId = new UsuarioId(500);

        Comunidade novaComunidade = new Comunidade(comunidadeId, "Clube do Filme", "Comunidade para testes");
        repositorio.salvarComunidade(novaComunidade);

        Comunidade comunidadeSalva = repositorio.obterComunidade(comunidadeId);
        assertNotNull(comunidadeSalva);
        assertEquals("Clube do Filme", comunidadeSalva.getNome());

        MembroComunidade membro = new MembroComunidade(comunidadeId, usuarioId, PapelComunidade.MEMBRO);
        repositorio.salvarMembro(membro);

        List<MembroComunidade> membros = repositorio.buscarMembrosDaComunidade(comunidadeId);
        assertFalse(membros.isEmpty());
        assertEquals(usuarioId.getId(), membros.get(0).getUsuarioId().getId());

        List<ComunidadeUsuarioResumo> comunidadesDoUsuario = repositorio.buscarComunidadesDoUsuario(usuarioId);
        assertFalse(comunidadesDoUsuario.isEmpty());

        repositorio.removerMembro(comunidadeId, usuarioId);
        List<MembroComunidade> membrosAposRemocao = repositorio.buscarMembrosDaComunidade(comunidadeId);
        assertTrue(membrosAposRemocao.isEmpty());
    }
}