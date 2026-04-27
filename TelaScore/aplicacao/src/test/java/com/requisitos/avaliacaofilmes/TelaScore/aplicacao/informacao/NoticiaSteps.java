package com.requisitos.avaliacaofilmes.TelaScore.aplicacao.informacao;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import com.requisitos.avaliacaofilmes.TelaScore.aplicacao.informacao.noticia.AdicionarNoticiaCasoDeUso;
import com.requisitos.avaliacaofilmes.TelaScore.aplicacao.informacao.noticia.AdicionarNoticiaComando;
import com.requisitos.avaliacaofilmes.TelaScore.aplicacao.informacao.noticia.NoticiaResumo;
import com.requisitos.avaliacaofilmes.TelaScore.aplicacao.informacao.noticia.PesquisarNoticiasCasoDeUso;
import com.requisitos.avaliacaofilmes.TelaScore.dominio.identidade.usuario.UsuarioId;
import com.requisitos.avaliacaofilmes.TelaScore.dominio.informacao.noticia.CategoriaNoticia;
import com.requisitos.avaliacaofilmes.TelaScore.dominio.informacao.noticia.Noticia;
import com.requisitos.avaliacaofilmes.TelaScore.dominio.informacao.noticia.NoticiaId;
import com.requisitos.avaliacaofilmes.TelaScore.dominio.informacao.noticia.NoticiaRepositorio;
import com.requisitos.avaliacaofilmes.TelaScore.dominio.informacao.noticia.NoticiaServico;

import io.cucumber.datatable.DataTable;
import io.cucumber.java.pt.Dado;
import io.cucumber.java.pt.Então;
import io.cucumber.java.pt.Quando;

public class NoticiaSteps {

    private List<Noticia> bancoFake = new ArrayList<>();
    private List<NoticiaResumo> resultadoPesquisa;
    private PesquisarNoticiasCasoDeUso pesquisarCasoDeUso;
    private AdicionarNoticiaCasoDeUso adicionarCasoDeUso;

    // Repositório Mock para simular o banco de dados na camada de aplicação
    private final NoticiaRepositorio repositorio = new NoticiaRepositorio() {
        @Override public void salvar(Noticia n) { bancoFake.add(n); }
        @Override public Noticia obter(NoticiaId id) { return null; }
        @Override public void remover(NoticiaId id) { }
        @Override public List<Noticia> buscarRecentes(int l) { return bancoFake; }
        @Override public List<Noticia> buscarPorAutor(UsuarioId a) { return null; }
        @Override public List<Noticia> buscarPorFiltros(String termo, CategoriaNoticia cat) {
            return bancoFake.stream()
                    .filter(n -> (termo == null || n.getTitulo().toLowerCase().contains(termo.toLowerCase())))
                    .filter(n -> (cat == null || n.getCategoria() == cat))
                    .toList();
        }
    };

    @Dado("que as seguintes notícias estão disponíveis no sistema:")
    public void que_noticias_disponiveis(DataTable tabela) {
        bancoFake.clear();
        for (Map<String, String> linha : tabela.asMaps()) {
            Noticia n = new Noticia(
                    new NoticiaId(bancoFake.size() + 1),
                    new UsuarioId(1),
                    linha.get("titulo"),
                    "Conteúdo padrão",
                    CategoriaNoticia.valueOf(linha.get("categoria").toUpperCase())
            );
            bancoFake.add(n);
        }
    }

    @Quando("o usuário pesquisa pelo termo {string} e seleciona a categoria {string}")
    public void usuario_pesquisa_termo_e_categoria(String termo, String categoria) {
        pesquisarCasoDeUso = new PesquisarNoticiasCasoDeUso(repositorio);
        resultadoPesquisa = pesquisarCasoDeUso.executar(termo, categoria);
    }

    @Quando("ele seleciona filtrar apenas pela categoria {string}")
    public void usuario_filtra_por_categoria(String categoria) {
        pesquisarCasoDeUso = new PesquisarNoticiasCasoDeUso(repositorio);
        resultadoPesquisa = pesquisarCasoDeUso.executar(null, categoria);
    }

    @Dado("que o usuário com ID {int} deseja publicar uma informação")
    public void usuario_deseja_publicar(Integer idUsuario) {
        NoticiaServico servico = new NoticiaServico(repositorio);
        adicionarCasoDeUso = new AdicionarNoticiaCasoDeUso(servico);
    }

    @Quando("ele informa o título {string}, o conteúdo e a categoria {string}")
    public void informa_dados_publicacao(String titulo, String categoria) {
        AdicionarNoticiaComando comando = new AdicionarNoticiaComando(1, titulo, "Conteúdo da notícia", categoria);
        adicionarCasoDeUso.executar(comando);
    }

    @Então("o sistema retorna {int} notícia correspondente")
    public void sistema_retorna_qtd(Integer qtd) {
        assertEquals(qtd, resultadoPesquisa.size());
    }

    @Então("o título exibido deve ser {string}")
    public void titulo_deve_ser(String esperado) {
        assertEquals(esperado, resultadoPesquisa.get(0).titulo());
    }

    @Então("a notícia é registrada no sistema com sucesso")
    public void noticia_registrada() {
        assertFalse(bancoFake.isEmpty());
    }

    @Então("fica disponível para consulta por outros usuários")
    public void disponivel_consulta() {
        String ultimoTitulo = bancoFake.get(bancoFake.size() - 1).getTitulo();
        var busca = repositorio.buscarPorFiltros(ultimoTitulo, null);
        assertFalse(busca.isEmpty());
    }
}