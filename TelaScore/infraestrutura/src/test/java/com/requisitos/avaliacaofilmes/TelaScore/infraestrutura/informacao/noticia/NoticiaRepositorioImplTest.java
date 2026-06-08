package com.requisitos.avaliacaofilmes.TelaScore.infraestrutura.informacao.noticia;

import static org.junit.jupiter.api.Assertions.*;

import com.requisitos.avaliacaofilmes.TelaScore.dominio.identidade.usuario.UsuarioId;
import com.requisitos.avaliacaofilmes.TelaScore.dominio.informacao.noticia.CategoriaNoticia;
import com.requisitos.avaliacaofilmes.TelaScore.dominio.informacao.noticia.Noticia;
import com.requisitos.avaliacaofilmes.TelaScore.dominio.informacao.noticia.NoticiaId;
import com.requisitos.avaliacaofilmes.TelaScore.dominio.informacao.noticia.NoticiaRepositorio;
import java.util.List;
import org.junit.jupiter.api.Test;

public class NoticiaRepositorioImplTest {

    private final NoticiaRepositorio repositorio = new NoticiaRepositorioImpl();

    @Test
    public void deveSalvarEBuscarUmaNoticiaNoBanco() {
        NoticiaId noticiaId = new NoticiaId(888);
        UsuarioId autorId = new UsuarioId(123);

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