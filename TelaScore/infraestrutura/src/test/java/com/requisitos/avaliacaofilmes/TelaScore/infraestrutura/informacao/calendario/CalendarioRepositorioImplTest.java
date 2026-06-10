package com.requisitos.avaliacaofilmes.TelaScore.infraestrutura.informacao.calendario;

import static org.junit.jupiter.api.Assertions.*;

import com.requisitos.avaliacaofilmes.TelaScore.dominio.catalogo.filme.FilmeId;
import com.requisitos.avaliacaofilmes.TelaScore.dominio.identidade.usuario.UsuarioId;
import com.requisitos.avaliacaofilmes.TelaScore.dominio.informacao.calendario.CalendarioEstreia;
import com.requisitos.avaliacaofilmes.TelaScore.dominio.informacao.calendario.CalendarioId;
import com.requisitos.avaliacaofilmes.TelaScore.dominio.informacao.calendario.CalendarioRepositorio;
import com.requisitos.avaliacaofilmes.TelaScore.dominio.informacao.calendario.EntradaCalendario;
import java.time.LocalDate;
import java.util.List;
import org.junit.jupiter.api.Test;

public class CalendarioRepositorioImplTest {

    private final CalendarioRepositorio repositorio = new CalendarioRepositorioImpl();

    @Test
    public void deveSalvarERecuperarUmCalendarioComEntradasPreservandoLembrete() {
        CalendarioId calendarioId = new CalendarioId(555);
        UsuarioId usuarioId = new UsuarioId(456);

        CalendarioEstreia calendario = new CalendarioEstreia(calendarioId, usuarioId);

        FilmeId filmeComLembrete = new FilmeId("f-100");
        FilmeId filmeSemLembrete = new FilmeId("f-200");
        LocalDate estreia = LocalDate.now().plusDays(15);

        EntradaCalendario entradaAtiva = new EntradaCalendario(filmeComLembrete, estreia);

        EntradaCalendario entradaInativa = new EntradaCalendario(filmeSemLembrete, estreia);
        entradaInativa.alternarLembrete(); // lembreteAtivo -> false

        calendario.adicionarFilme(entradaAtiva);
        calendario.adicionarFilme(entradaInativa);

        repositorio.salvar(calendario);

        CalendarioEstreia recuperado = repositorio.obterPorUsuario(usuarioId);
        assertNotNull(recuperado);
        assertEquals(calendarioId, recuperado.getId());
        assertEquals(usuarioId, recuperado.getUsuarioId());

        List<EntradaCalendario> entradas = recuperado.getEntradas();
        assertEquals(2, entradas.size());

        EntradaCalendario recAtiva = entradas.stream()
                .filter(e -> e.getFilmeId().equals(filmeComLembrete))
                .findFirst().orElseThrow();
        EntradaCalendario recInativa = entradas.stream()
                .filter(e -> e.getFilmeId().equals(filmeSemLembrete))
                .findFirst().orElseThrow();

        assertEquals(estreia, recAtiva.getDataEstreiaPrevista());
        assertTrue(recAtiva.isLembreteAtivo());
        assertFalse(recInativa.isLembreteAtivo());
    }
}
