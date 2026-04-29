package com.requisitos.avaliacaofilmes.TelaScore.dominio.catalogo;

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertAll;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

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
    private String sinopse;
    private DiretorId diretorId;

    private Filme filmeCriado;
    private Exception excecaoCapturada;

    // ─── Contexto: criando um filme do zero ──────────────────────────────────

    @Dado("que eu quero cadastrar um filme com o título {string} lançado em {int}")
    public void que_eu_quero_cadastrar_um_filme(String titulo, Integer ano) {
        this.titulo = titulo;
        this.anoLancamento = ano;
        this.sinopse = null;

        filmeCriado = null;
        excecaoCapturada = null;
    }

    @E("que o filme tem o diretor de id {int}")
    public void que_o_filme_tem_o_diretor_de_id(Integer idDiretor) {
        this.diretorId = new DiretorId(idDiretor);
    }

    @E("que o filme tem a sinopse {string}")
    public void que_o_filme_tem_a_sinopse(String sinopse) {
        // String vazia representa sinopse em branco — passamos direto para o domínio validar
        this.sinopse = sinopse.isBlank() ? sinopse : sinopse;
    }

    @Quando("eu tento criar o filme")
    public void eu_tento_criar_o_filme() {
        try {
            FilmeId filmeId = new FilmeId("1");
            filmeCriado = new Filme(filmeId, titulo, sinopse, anoLancamento, List.of(diretorId));
        } catch (Exception e) {
            excecaoCapturada = e;
        }
    }

    // ─── Contexto: atualizando um filme existente ────────────────────────────

    @Dado("que existe um filme cadastrado com o título {string} lançado em {int} com o diretor de id {int}")
    public void que_existe_um_filme_cadastrado(String titulo, Integer ano, Integer idDiretor) {
        filmeCriado = null;
        excecaoCapturada = null;

        // Cria o filme inicial que será manipulado nos próximos passos
        FilmeId filmeId = new FilmeId("1");
        DiretorId diretor = new DiretorId(idDiretor);
        filmeCriado = new Filme(filmeId, titulo, null, ano, List.of(diretor));
    }

    @Quando("eu atualizo o título do filme para {string}")
    public void eu_atualizo_o_titulo_do_filme_para(String novoTitulo) {
        try {
            filmeCriado.setTitulo(novoTitulo);
        } catch (Exception e) {
            excecaoCapturada = e;
        }
    }

    @Quando("eu atualizo o ano de lançamento para {int}")
    public void eu_atualizo_o_ano_de_lancamento_para(Integer novoAno) {
        try {
            filmeCriado.setAnoLancamento(novoAno);
        } catch (Exception e) {
            excecaoCapturada = e;
        }
    }

    @Quando("eu adiciono o diretor de id {int} ao filme")
    public void eu_adiciono_o_diretor_ao_filme(Integer idDiretor) {
        try {
            filmeCriado.adicionarDiretor(new DiretorId(idDiretor));
        } catch (Exception e) {
            excecaoCapturada = e;
        }
    }

    // ─── Verificações de sucesso ──────────────────────────────────────────────

    @Então("o filme deve ser criado com sucesso")
    public void o_filme_deve_ser_criado_com_sucesso() {
        assertAll("filme criado com sucesso",
            () -> assertNull(excecaoCapturada, "Nenhuma exceção deveria ter sido lançada"),
            () -> assertNotNull(filmeCriado, "A entidade Filme deveria ter sido criada"),
            () -> assertNotNull(filmeCriado.getId(), "O id do filme não pode ser nulo")
        );
    }

    @E("o filme deve ter o título {string}")
    public void o_filme_deve_ter_o_titulo(String tituloEsperado) {
        assertEquals(tituloEsperado, filmeCriado.getTitulo(),
            "O título do filme não corresponde ao esperado");
    }

    @E("o filme deve ter o ano de lançamento {int}")
    public void o_filme_deve_ter_o_ano_de_lancamento(Integer anoEsperado) {
        assertEquals(anoEsperado, filmeCriado.getAnoLancamento(),
            "O ano de lançamento não corresponde ao esperado");
    }

    @E("o filme deve ter pelo menos um diretor")
    public void o_filme_deve_ter_pelo_menos_um_diretor() {
        assertTrue(filmeCriado.getDiretores().size() >= 1,
            "O filme deveria ter pelo menos um diretor cadastrado");
    }

    @E("a sinopse do filme deve ser {string}")
    public void a_sinopse_do_filme_deve_ser(String sinopseEsperada) {
        assertEquals(sinopseEsperada, filmeCriado.getSinopse(),
            "A sinopse do filme não corresponde ao esperado");
    }

    // ─── Verificações de erro ─────────────────────────────────────────────────

    @Então("o sistema deve bloquear a criação do filme informando que o título é inválido")
    public void o_sistema_deve_bloquear_criacao_titulo_invalido() {
        assertAll("título inválido bloqueado na criação",
            () -> assertNotNull(excecaoCapturada, "Uma exceção deveria ter sido lançada"),
            () -> assertEquals(IllegalArgumentException.class, excecaoCapturada.getClass(),
                "A exceção deveria ser IllegalArgumentException")
        );
    }

    @Então("o sistema deve bloquear a criação do filme informando que a sinopse é inválida")
    public void o_sistema_deve_bloquear_criacao_sinopse_invalida() {
        assertAll("sinopse inválida bloqueada na criação",
            () -> assertNotNull(excecaoCapturada, "Uma exceção deveria ter sido lançada"),
            () -> assertEquals(IllegalArgumentException.class, excecaoCapturada.getClass(),
                "A exceção deveria ser IllegalArgumentException")
        );
    }

    @Então("o sistema deve bloquear a atualização do filme informando que o título é inválido")
    public void o_sistema_deve_bloquear_atualizacao_titulo_invalido() {
        assertAll("título inválido bloqueado na atualização",
            () -> assertNotNull(excecaoCapturada, "Uma exceção deveria ter sido lançada ao tentar atualizar"),
            () -> assertEquals(IllegalArgumentException.class, excecaoCapturada.getClass(),
                "A exceção deveria ser IllegalArgumentException")
        );
    }
}