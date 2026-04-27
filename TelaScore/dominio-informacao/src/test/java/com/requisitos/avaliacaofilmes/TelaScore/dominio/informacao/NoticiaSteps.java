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
    private String categoriaDefinida;
    private String tituloTemp;
    private Exception excecaoCapturada;

    @Dado("que as seguintes notícias estão disponíveis no sistema:")
    public void que_as_seguintes_notícias_estão_disponíveis_no_sistema(io.cucumber.datatable.DataTable tabela) {
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

    @Dado("que eu defino a categoria como {string}")
    public void que_eu_defino_a_categoria_como(String categoria) {
        this.categoriaDefinida = categoria.replace("\"", "");
    }

    @Dado("que eu informo o título {string}")
    public void que_eu_informo_o_título(String titulo) {
        this.tituloTemp = titulo;
    }

    @Dado("a categoria {string}")
    public void a_categoria(String categoria) {
        this.categoriaDefinida = categoria.replace("\"", "");
    }

    @Dado("que eu tento criar uma notícia com o título {string}")
    public void que_eu_tento_criar_uma_notícia_com_o_título(String titulo) {
        this.tituloTemp = titulo;
    }

    @Quando("a entidade é criada")
    public void a_entidade_é_criada() {
        noticiaId = new NoticiaId(123);
        autorId = new UsuarioId(1);
        CategoriaNoticia cat = CategoriaNoticia.valueOf(categoriaDefinida.toUpperCase());
        noticiaCriada = new Noticia(noticiaId, autorId, "Título Genérico", "Conteúdo com opinião", cat);
    }

    @Quando("eu tento criar a entidade de notícia")
    public void eu_tento_criar_a_entidade_de_notícia() {
        noticiaId = new NoticiaId(1);
        autorId = new UsuarioId(1);
        CategoriaNoticia cat = CategoriaNoticia.valueOf(categoriaDefinida.toUpperCase());
        noticiaCriada = new Noticia(noticiaId, autorId, tituloTemp, "Conteúdo válido", cat);
    }

    @Quando("eu tento processar a criação")
    public void eu_tento_processar_a_criação() {
        try {
            noticiaId = new NoticiaId(1);
            autorId = new UsuarioId(1);
            CategoriaNoticia cat = (categoriaDefinida != null)
                    ? CategoriaNoticia.valueOf(categoriaDefinida.toUpperCase())
                    : CategoriaNoticia.LANCAMENTO;

            noticiaCriada = new Noticia(noticiaId, autorId, tituloTemp, "Conteúdo", cat);
        } catch (Exception e) {
            excecaoCapturada = e;
        }
    }

    @Quando("o usuário pesquisa pelo termo {string} e seleciona a categoria {string}")
    public void o_usuário_pesquisa_pelo_termo_e_seleciona_a_categoria(String termo, String categoria) {
        CategoriaNoticia cat = CategoriaNoticia.valueOf(categoria.toUpperCase());
        resultadoFiltro = listaDeNoticias.stream()
                .filter(n -> n.getTitulo().contains(termo) && n.getCategoria() == cat)
                .toList();
    }

    @Quando("ele seleciona filtrar apenas pela categoria {string}")
    public void ele_seleciona_filtrar_apenas_pela_categoria(String categoria) {
        CategoriaNoticia cat = CategoriaNoticia.valueOf(categoria.toUpperCase());
        resultadoFiltro = listaDeNoticias.stream()
                .filter(n -> n.getCategoria() == cat)
                .toList();
    }

    @Quando("ele informa o título {string}, o conteúdo e a categoria {string}")
    public void ele_informa_o_título_o_conteúdo_e_a_categoria(String titulo, String categoria) {
        noticiaId = new NoticiaId(999);
        autorId = new UsuarioId(1);
        CategoriaNoticia cat = CategoriaNoticia.valueOf(categoria.toUpperCase());
        noticiaCriada = new Noticia(noticiaId, autorId, titulo, "Conteúdo da notícia", cat);
    }

    @Então("o sistema retorna {int} notícia correspondente")
    public void o_sistema_retorna_notícia_correspondente(Integer qtd) {
        assertEquals(qtd, resultadoFiltro.size());
    }

    @Então("o título exibido deve ser {string}")
    public void o_título_exibido_deve_ser(String titulo) {
        assertEquals(titulo, resultadoFiltro.get(0).getTitulo());
    }

    @Então("a notícia é registrada no sistema com sucesso")
    public void a_notícia_é_registrada_no_sistema_com_sucesso() {
        assertNotNull(noticiaCriada);
    }

    @Então("fica disponível para consulta por outros usuários")
    public void fica_disponível_para_consulta_por_outros_usuários() {
        assertTrue(noticiaCriada.getTitulo() != null);
    }

    @Então("o sistema deve permitir que o conteúdo contenha a opinião do autor")
    public void o_sistema_deve_permitir_que_o_conteúdo_contenha_a_opinião_do_autor() {
        assertNotNull(noticiaCriada);
        assertEquals(CategoriaNoticia.CRITICA, noticiaCriada.getCategoria());
    }

    @Então("a notícia deve ser instanciada com a categoria {string}")
    public void a_notícia_deve_ser_instanciada_com_a_categoria(String categoria) {
        assertEquals(categoria.replace("\"", "").toUpperCase(), noticiaCriada.getCategoria().name());
    }

    @Então("a data de publicação deve ser registrada")
    public void a_data_de_publicação_deve_ser_registrada() {
        assertNotNull(noticiaCriada.getDataPublicacao());
    }

    @Então("o sistema deve lançar um erro de domínio informando {string}")
    public void o_sistema_deve_lançar_um_erro_de_domínio_informando(String mensagem) {
        assertNotNull(excecaoCapturada);
        assertTrue(excecaoCapturada.getMessage().contains(mensagem.replace("\"", "")));
    }
}