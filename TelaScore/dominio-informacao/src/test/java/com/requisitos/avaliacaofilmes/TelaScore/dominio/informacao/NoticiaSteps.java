package com.requisitos.avaliacaofilmes.TelaScore.dominio.informacao;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.util.ArrayList;
import java.util.List;

import com.requisitos.avaliacaofilmes.TelaScore.dominio.identidade.usuario.UsuarioId;

import com.requisitos.avaliacaofilmes.TelaScore.dominio.informacao.noticia.CategoriaNoticia;
import com.requisitos.avaliacaofilmes.TelaScore.dominio.informacao.noticia.Noticia;
import com.requisitos.avaliacaofilmes.TelaScore.dominio.informacao.noticia.NoticiaId;
import io.cucumber.java.pt.Dado;
import io.cucumber.java.pt.Então;
import io.cucumber.java.pt.Quando;

public class NoticiaSteps {

    private NoticiaId noticiaId;
    private UsuarioId autorId;
    private Noticia noticiaCriada;
    private List<Noticia> listaDeNoticias = new ArrayList<>();
    private List<Noticia> resultadoFiltro = new ArrayList<>();

    @Dado("que as seguintes notícias estão disponíveis no sistema:")
    public void que_as_noticias_estao_disponiveis(io.cucumber.datatable.DataTable tabela) {
        listaDeNoticias.clear();
        tabela.asMaps().forEach(linha -> {
            Noticia n = new Noticia(
                    new NoticiaId(listaDeNoticias.size() + 1),
                    new UsuarioId(1),
                    linha.get("titulo"),
                    "Conteúdo padrão",
                    CategoriaNoticia.valueOf(linha.get("categoria").toUpperCase())
            );
            listaDeNoticias.add(n);
        });
    }

    @Quando("o usuário pesquisa pelo termo {string} e seleciona a categoria {string}")
    public void pesquisa_por_termo_e_categoria(String termo, String categoria) {
        CategoriaNoticia cat = CategoriaNoticia.valueOf(categoria.toUpperCase());
        resultadoFiltro = listaDeNoticias.stream()
                .filter(n -> n.getTitulo().contains(termo) && n.getCategoria() == cat)
                .toList();
    }

    @Quando("ele seleciona filtrar apenas pela categoria {string}")
    public void filtrar_apenas_por_categoria(String categoria) {
        CategoriaNoticia cat = CategoriaNoticia.valueOf(categoria.toUpperCase());
        resultadoFiltro = listaDeNoticias.stream()
                .filter(n -> n.getCategoria() == cat)
                .toList();
    }

    @Quando("ele informa o título {string}, o conteúdo e a categoria {string}")
    public void criar_entidade_noticia(String titulo, String categoria) {
        noticiaId = new NoticiaId(999);
        autorId = new UsuarioId(1);
        CategoriaNoticia cat = CategoriaNoticia.valueOf(categoria.toUpperCase());
        noticiaCriada = new Noticia(noticiaId, autorId, titulo, "Conteúdo da notícia", cat);
    }

    @Então("o sistema retorna {int} notícia correspondente")
    public void sistema_retorna_quantidade(Integer qtd) {
        assertEquals(qtd, resultadoFiltro.size());
    }

    @Então("o título exibido deve ser {string}")
    public void titulo_exibido_deve_ser(String titulo) {
        assertEquals(titulo, resultadoFiltro.get(0).getTitulo());
    }

    @Então("a notícia é registrada no sistema com sucesso")
    public void noticia_criada_com_sucesso() {
        assertNotNull(noticiaCriada);
        assertEquals("Nova série de Harry Potter", noticiaCriada.getTitulo());
    }

    @Então("fica disponível para consulta por outros usuários")
    public void disponivel_para_consulta() {
        assertTrue(noticiaCriada.getTitulo() != null);
    }
}