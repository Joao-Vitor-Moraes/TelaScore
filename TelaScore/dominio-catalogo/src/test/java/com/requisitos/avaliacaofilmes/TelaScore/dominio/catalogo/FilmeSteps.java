package com.requisitos.avaliacaofilmes.TelaScore.dominio.catalogo;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNull;

import java.util.List;

import com.requisitos.avaliacaofilmes.TelaScore.dominio.catalogo.diretor.DiretorId;
import com.requisitos.avaliacaofilmes.TelaScore.dominio.catalogo.filme.Filme;
import com.requisitos.avaliacaofilmes.TelaScore.dominio.catalogo.filme.FilmeId;

import io.cucumber.java.pt.Dado;
import io.cucumber.java.pt.E;
import io.cucumber.java.pt.Então;
import io.cucumber.java.pt.Quando;

public class FilmeSteps {

    private String titulo;
    private Integer anoLancamento;
    private DiretorId diretorId;

    private Filme filmeCriado;
    private Exception excecaoCapturada;

    @Dado("que eu quero cadastrar um filme com o título {string} lançado em {int}")
    public void que_eu_quero_cadastrar_um_filme_com_o_titulo_lancado_em(String titulo, Integer ano) {
        this.titulo = titulo;
        this.anoLancamento = ano;

        filmeCriado = null;
        excecaoCapturada = null;
    }

    @E("que o filme tem o diretor de id {int}")
    public void que_o_filme_tem_o_diretor_de_id(Integer idDiretor) {
        this.diretorId = new DiretorId(idDiretor);
    }

    @Quando("eu tento criar o filme")
    public void eu_tento_criar_o_filme() {
        try {
            FilmeId filmeId = new FilmeId("1");
            filmeCriado = new Filme(filmeId, titulo, null, anoLancamento, List.of(diretorId));
        } catch (Exception e) {
            excecaoCapturada = e;
        }
    }

    @Então("o filme deve ser criado com sucesso")
    public void o_filme_deve_ser_criado_com_sucesso() {
        assertNull(excecaoCapturada, "Nenhuma exceção deveria ter sido lançada");
        assertNotNull(filmeCriado, "A entidade Filme deveria ter sido criada");
    }

    @E("o filme deve ter o título {string}")
    public void o_filme_deve_ter_o_titulo(String tituloEsperado) {
        assertEquals(tituloEsperado, filmeCriado.getTitulo());
    }

    @Então("o sistema deve bloquear a criação do filme informando que o título é inválido")
    public void o_sistema_deve_bloquear_a_criacao_do_filme_informando_que_o_titulo_e_invalido() {
        assertNotNull(excecaoCapturada, "Uma exceção deveria ter sido lançada");
        // O Validate do domínio lança IllegalArgumentException para título em branco
        assertEquals(IllegalArgumentException.class, excecaoCapturada.getClass());
    }
}