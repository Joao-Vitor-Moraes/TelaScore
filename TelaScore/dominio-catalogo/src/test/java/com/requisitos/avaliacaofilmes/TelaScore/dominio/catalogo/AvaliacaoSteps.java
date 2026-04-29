package com.requisitos.avaliacaofilmes.TelaScore.dominio.catalogo;

import java.time.LocalDate;

import static org.junit.jupiter.api.Assertions.assertAll;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNull;

import com.requisitos.avaliacaofilmes.TelaScore.dominio.catalogo.avaliacao.Avaliacao;
import com.requisitos.avaliacaofilmes.TelaScore.dominio.catalogo.avaliacao.AvaliacaoId;
import com.requisitos.avaliacaofilmes.TelaScore.dominio.catalogo.avaliacao.Nota;
import com.requisitos.avaliacaofilmes.TelaScore.dominio.catalogo.filme.FilmeId;
import com.requisitos.avaliacaofilmes.TelaScore.dominio.identidade.usuario.UsuarioId;

import io.cucumber.java.pt.Dado;
import io.cucumber.java.pt.E;
import io.cucumber.java.pt.Então;
import io.cucumber.java.pt.Quando;

public class AvaliacaoSteps {

    private FilmeId filmeId;
    private UsuarioId usuarioId;
    private AvaliacaoId avaliacaoId;

    private Avaliacao avaliacaoCriada;
    private Exception excecaoCapturada;

    // ─── Contexto: criando uma avaliação do zero ─────────────────────────────

    @Dado("que eu quero avaliar o filme {string} como o usuário {int}")
    public void que_eu_quero_avaliar_o_filme_como_o_usuario(String idFilme, Integer idUsuario) {
        filmeId = new FilmeId(idFilme);
        usuarioId = new UsuarioId(idUsuario);
        avaliacaoId = new AvaliacaoId(1);

        avaliacaoCriada = null;
        excecaoCapturada = null;
    }

    @Quando("eu tento criar uma avaliação com a nota {int} e a resenha {string}")
    public void eu_tento_criar_uma_avaliacao_com_nota_e_resenha(Integer valorNota, String resenha) {
        try {
            Nota nota = new Nota(valorNota);
            avaliacaoCriada = new Avaliacao(avaliacaoId, filmeId, usuarioId, nota, resenha);
        } catch (Exception e) {
            excecaoCapturada = e;
        }
    }

    @Quando("eu tento criar uma avaliação com a nota {int} e sem resenha")
    public void eu_tento_criar_uma_avaliacao_sem_resenha(Integer valorNota) {
        try {
            Nota nota = new Nota(valorNota);
            avaliacaoCriada = new Avaliacao(avaliacaoId, filmeId, usuarioId, nota, null);
        } catch (Exception e) {
            excecaoCapturada = e;
        }
    }

    // ─── Contexto: atualizando uma avaliação existente ───────────────────────

    @Dado("que existe uma avaliação criada com a nota {int} e a resenha {string}")
    public void que_existe_uma_avaliacao_criada(Integer valorNota, String resenha) {
        filmeId = new FilmeId("FILME-001");
        usuarioId = new UsuarioId(10);
        avaliacaoId = new AvaliacaoId(1);

        avaliacaoCriada = null;
        excecaoCapturada = null;

        // Cria a avaliação inicial que será atualizada nos próximos passos
        Nota nota = new Nota(valorNota);
        avaliacaoCriada = new Avaliacao(avaliacaoId, filmeId, usuarioId, nota, resenha);
    }

    @Quando("eu atualizo a nota para {int}")
    public void eu_atualizo_a_nota_para(Integer novaNota) {
        try {
            avaliacaoCriada.setNota(new Nota(novaNota));
        } catch (Exception e) {
            excecaoCapturada = e;
        }
    }

    @Quando("eu atualizo a resenha para {string}")
    public void eu_atualizo_a_resenha_para(String novaResenha) {
        try {
            avaliacaoCriada.setResenha(novaResenha);
        } catch (Exception e) {
            excecaoCapturada = e;
        }
    }

    // ─── Verificações de sucesso ──────────────────────────────────────────────

    @Então("a avaliação deve ser criada com sucesso")
    public void a_avaliacao_deve_ser_criada_com_sucesso() {
        assertAll("avaliação criada com sucesso",
            () -> assertNull(excecaoCapturada, "Nenhuma exceção deveria ter sido lançada"),
            () -> assertNotNull(avaliacaoCriada, "A entidade Avaliacao deveria ter sido criada"),
            () -> assertNotNull(avaliacaoCriada.getId(), "O id da avaliação não pode ser nulo"),
            () -> assertNotNull(avaliacaoCriada.getFilmeId(), "O filmeId não pode ser nulo"),
            () -> assertNotNull(avaliacaoCriada.getUsuarioId(), "O usuarioId não pode ser nulo")
        );
    }

    @E("a nota registrada deve ser {int}")
    public void a_nota_registrada_deve_ser(Integer valorEsperado) {
        assertEquals(valorEsperado, avaliacaoCriada.getNota().getValor(),
            "A nota registrada não corresponde ao valor esperado");
    }

    @E("a resenha registrada deve ser {string}")
    public void a_resenha_registrada_deve_ser(String resenhaEsperada) {
        assertEquals(resenhaEsperada, avaliacaoCriada.getResenha(),
            "A resenha registrada não corresponde ao valor esperado");
    }

    @E("a resenha deve estar vazia")
    public void a_resenha_deve_estar_vazia() {
        assertNull(avaliacaoCriada.getResenha(), "A resenha deveria ser nula quando não informada");
    }

    @E("a data da avaliação deve ser a data de hoje")
    public void a_data_da_avaliacao_deve_ser_hoje() {
        assertEquals(LocalDate.now(), avaliacaoCriada.getDataAvaliacao(),
            "A data da avaliação deveria ser a data atual");
    }

    // ─── Verificações de erro ─────────────────────────────────────────────────

    @Então("o sistema deve bloquear a criação informando que a nota é inválida")
    public void o_sistema_deve_bloquear_a_criacao_nota_invalida() {
        assertAll("nota inválida bloqueada na criação",
            () -> assertNotNull(excecaoCapturada, "Uma exceção deveria ter sido lançada pela classe Nota"),
            () -> assertEquals(IllegalArgumentException.class, excecaoCapturada.getClass(),
                "A exceção deveria ser IllegalArgumentException")
        );
    }

    @Então("o sistema deve bloquear a atualização informando que a nota é inválida")
    public void o_sistema_deve_bloquear_a_atualizacao_nota_invalida() {
        assertAll("nota inválida bloqueada na atualização",
            () -> assertNotNull(excecaoCapturada, "Uma exceção deveria ter sido lançada ao tentar atualizar"),
            () -> assertEquals(IllegalArgumentException.class, excecaoCapturada.getClass(),
                "A exceção deveria ser IllegalArgumentException")
        );
    }
}