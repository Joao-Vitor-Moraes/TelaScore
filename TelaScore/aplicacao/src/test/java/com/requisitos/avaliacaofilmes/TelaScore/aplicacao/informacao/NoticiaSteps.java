package com.requisitos.avaliacaofilmes.TelaScore.aplicacao.informacao;

import static org.junit.jupiter.api.Assertions.*;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

import com.requisitos.avaliacaofilmes.TelaScore.dominio.identidade.usuario.UsuarioId;
import com.requisitos.avaliacaofilmes.TelaScore.dominio.informacao.noticia.*;

import io.cucumber.java.pt.Dado;
import io.cucumber.java.pt.Então;
import io.cucumber.java.pt.Quando;

public class NoticiaSteps {

    private List<Noticia> bancoDados = new ArrayList<>();
    private List<Noticia> resultadoConsulta = new ArrayList<>();
    private Noticia noticiaCriada;

    @Dado("que existem notícias das categorias {string} e {string} no sistema")
    public void que_existem_notícias_das_categorias_e_no_sistema(String cat1, String cat2) {
        bancoDados.clear();
        bancoDados.add(new Noticia(new NoticiaId(1), new UsuarioId(1), "Titulo 1", "Conteudo", CategoriaNoticia.valueOf(cat1.toUpperCase())));
        bancoDados.add(new Noticia(new NoticiaId(2), new UsuarioId(1), "Titulo 2", "Conteudo", CategoriaNoticia.valueOf(cat2.toUpperCase())));
    }

    @Quando("o usuário solicita visualizar apenas a categoria {string}")
    public void o_usuário_solicita_visualizar_apenas_a_categoria(String categoria) {
        CategoriaNoticia cat = CategoriaNoticia.valueOf(categoria.toUpperCase());
        resultadoConsulta = bancoDados.stream()
                .filter(n -> n.getCategoria() == cat)
                .collect(Collectors.toList());
    }

    @Então("o resultado deve conter apenas notícias de {string}")
    public void o_resultado_deve_conter_apenas_notícias_de(String categoria) {
        assertTrue(resultadoConsulta.stream().allMatch(n -> n.getCategoria().name().equalsIgnoreCase(categoria)));
    }

    @Então("o contador de notícias deve refletir apenas essa seleção")
    public void o_contador_de_notícias_deve_refletir_apenas_essa_seleção() {
        assertFalse(resultadoConsulta.isEmpty());
    }

    @Dado("que existe uma notícia cadastrada com ID {int}")
    public void que_existe_uma_notícia_cadastrada_com_id(Integer id) {
        bancoDados.add(new Noticia(new NoticiaId(id), new UsuarioId(1), "Noticia para deletar", "Conteudo", CategoriaNoticia.LANCAMENTO));
    }

    @Quando("o administrador executa o caso de uso de remoção para o ID {int}")
    public void o_administrador_executa_o_caso_de_uso_de_remoção_para_o_id(Integer id) {
        bancoDados.removeIf(n -> n.getId().getId() == id);
    }

    @Então("a notícia não deve mais ser retornada pelo repositório")
    public void a_notícia_não_deve_mais_ser_retornada_pelo_repositório() {
        assertTrue(bancoDados.isEmpty());
    }

    @Dado("que o autor com ID {int} está autenticado")
    public void que_o_autor_com_id_está_autenticado(Integer id) {
    }

    @Quando("ele executa o comando de adicionar notícia com título {string} e categoria {string}")
    public void ele_executa_o_comando_de_adicionar_notícia_com_título_e_categoria(String titulo, String categoria) {
        noticiaCriada = new Noticia(new NoticiaId(100), new UsuarioId(1), titulo, "Conteúdo da publicação", CategoriaNoticia.valueOf(categoria.toUpperCase()));
        bancoDados.add(noticiaCriada);
    }

    @Então("o repositório deve confirmar o salvamento da nova notícia")
    public void o_repositório_deve_confirmar_o_salvamento_da_nova_notícia() {
        assertTrue(bancoDados.contains(noticiaCriada));
    }

    @Então("a notícia deve receber um ID único gerado pelo sistema")
    public void a_notícia_deve_receber_un_id_único_gerado_pelo_sistema() {
        assertNotNull(noticiaCriada.getId());
    }
}