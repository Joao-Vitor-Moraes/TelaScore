package com.requisitos.avaliacaofilmes.TelaScore.dominio.catalogo;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.atomic.AtomicInteger;

import static org.junit.jupiter.api.Assertions.assertAll;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

import com.requisitos.avaliacaofilmes.TelaScore.dominio.catalogo.avaliacao.Avaliacao;
import com.requisitos.avaliacaofilmes.TelaScore.dominio.catalogo.avaliacao.AvaliacaoId;
import com.requisitos.avaliacaofilmes.TelaScore.dominio.catalogo.avaliacao.Nota;
import com.requisitos.avaliacaofilmes.TelaScore.dominio.catalogo.avaliacao.observer.AvaliacaoObserver;
import com.requisitos.avaliacaofilmes.TelaScore.dominio.catalogo.avaliacao.AvaliacaoServico;
import com.requisitos.avaliacaofilmes.TelaScore.dominio.catalogo.filme.FilmeId;
import com.requisitos.avaliacaofilmes.TelaScore.dominio.identidade.usuario.UsuarioId;

import io.cucumber.java.pt.Dado;
import io.cucumber.java.pt.E;
import io.cucumber.java.pt.Então;
import io.cucumber.java.pt.Quando;

public class AvaliacaoSteps {
    private static final AtomicInteger contadorId = new AtomicInteger(1);

    private FilmeId filmeId;
    private UsuarioId usuarioId;
    private AvaliacaoId avaliacaoId;

    private Avaliacao avaliacaoCriada;
    private Exception excecaoCapturada;

    // Usado no cenário do Observer
    private final List<Avaliacao> avaliacoesNotificadas = new ArrayList<>();


    @Dado("que eu quero avaliar o filme {string} como o usuário {int}")
    public void que_eu_quero_avaliar_o_filme_como_o_usuario(String idFilme, Integer idUsuario) {
        filmeId = new FilmeId(idFilme);
        usuarioId = new UsuarioId(idUsuario);
        // ID único por cenário — evita conflito entre execuções
        avaliacaoId = new AvaliacaoId(contadorId.getAndIncrement());

        avaliacaoCriada = null;
        excecaoCapturada = null;
        avaliacoesNotificadas.clear();
    }

    @Quando("eu tento criar uma avaliação com a nota {int} e a resenha {string}")
    public void eu_tento_criar_uma_avaliacao_com_nota_e_resenha(Integer valorNota, String resenha) {
        try {
            Nota nota = new Nota(valorNota);
            avaliacaoCriada = new Avaliacao(avaliacaoId, filmeId, usuarioId, nota, resenha, "PUBLICA");
        } catch (Exception e) {
            excecaoCapturada = e;
        }
    }

    @Quando("eu tento criar uma avaliação com a nota {int} e sem resenha")
    public void eu_tento_criar_uma_avaliacao_sem_resenha(Integer valorNota) {
        try {
            Nota nota = new Nota(valorNota);
            avaliacaoCriada = new Avaliacao(avaliacaoId, filmeId, usuarioId, nota, null, "PUBLICA");
        } catch (Exception e) {
            excecaoCapturada = e;
        }
    }


    @Dado("que existe uma avaliação criada com a nota {int} e a resenha {string}")
    public void que_existe_uma_avaliacao_criada(Integer valorNota, String resenha) {
        filmeId = new FilmeId("FILME-001");
        usuarioId = new UsuarioId(10);
        avaliacaoId = new AvaliacaoId(contadorId.getAndIncrement());

        avaliacaoCriada = null;
        excecaoCapturada = null;

        Nota nota = new Nota(valorNota);
        avaliacaoCriada = new Avaliacao(avaliacaoId, filmeId, usuarioId, nota, resenha, "PUBLICA");
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


    @Quando("eu registro a avaliação com nota {int} via serviço com observer")
    public void eu_registro_a_avaliacao_via_servico_com_observer(Integer valorNota) {
        try {
            // Repositório fake em memória — sem banco de dados necessário
            var repositorioFake = new AvaliacaoRepositorioFake();

            AvaliacaoServico servico = new AvaliacaoServico(repositorioFake);

            // Observer fake que captura a avaliação notificada para verificação
            servico.adicionarObserver((AvaliacaoObserver) avaliacao ->
                    avaliacoesNotificadas.add(avaliacao));

            Nota nota = new Nota(valorNota);
            avaliacaoCriada = new Avaliacao(avaliacaoId, filmeId, usuarioId, nota, "Resenha de teste.", "PUBLICA");

            servico.registrarAvaliacao(avaliacaoCriada);
        } catch (Exception e) {
            excecaoCapturada = e;
        }
    }


    @Então("a avaliação deve ser criada com sucesso")
    public void a_avaliacao_deve_ser_criada_com_sucesso() {
        assertAll("avaliação criada com sucesso",
            () -> assertNull(excecaoCapturada,
                    "Nenhuma exceção deveria ter sido lançada"),
            () -> assertNotNull(avaliacaoCriada,
                    "A entidade Avaliacao deveria ter sido criada"),
            () -> assertNotNull(avaliacaoCriada.getId(),
                    "O id da avaliação não pode ser nulo"),
            // Verifica os valores exatos, não só a existência
            () -> assertEquals(filmeId, avaliacaoCriada.getFilmeId(),
                    "O filmeId registrado não corresponde ao informado"),
            () -> assertEquals(usuarioId, avaliacaoCriada.getUsuarioId(),
                    "O usuarioId registrado não corresponde ao informado")
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
        assertNull(avaliacaoCriada.getResenha(),
            "A resenha deveria ser nula quando não informada");
    }

    @E("a data da avaliação deve ser a data de hoje")
    public void a_data_da_avaliacao_deve_ser_hoje() {
        assertEquals(LocalDate.now(), avaliacaoCriada.getDataAvaliacao(),
            "A data da avaliação deveria ser a data atual");
    }

    @Então("o observer deve ter sido notificado com a avaliação")
    public void o_observer_deve_ter_sido_notificado() {
        assertAll("observer notificado corretamente",
            () -> assertTrue(avaliacoesNotificadas.size() >= 1,
                    "O observer deveria ter sido chamado pelo menos uma vez"),
            () -> assertEquals(avaliacaoCriada.getId(), avaliacoesNotificadas.get(0).getId(),
                    "O observer deveria ter recebido a avaliação correta")
        );
    }


    @Então("o sistema deve bloquear a criação informando que a nota é inválida")
    public void o_sistema_deve_bloquear_a_criacao_nota_invalida() {
        assertAll("nota inválida bloqueada na criação",
            () -> assertNotNull(excecaoCapturada,
                    "Uma exceção deveria ter sido lançada pela classe Nota"),
            () -> assertEquals(IllegalArgumentException.class, excecaoCapturada.getClass(),
                    "A exceção deveria ser IllegalArgumentException")
        );
    }

    @Então("o sistema deve bloquear a atualização informando que a nota é inválida")
    public void o_sistema_deve_bloquear_a_atualizacao_nota_invalida() {
        assertAll("nota inválida bloqueada na atualização",
            () -> assertNotNull(excecaoCapturada,
                    "Uma exceção deveria ter sido lançada ao tentar atualizar"),
            () -> assertEquals(IllegalArgumentException.class, excecaoCapturada.getClass(),
                    "A exceção deveria ser IllegalArgumentException")
        );
    }
}