package com.requisitos.avaliacaofilmes.TelaScore.dominio.analise;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNull;

import java.time.LocalDate;

import com.requisitos.avaliacaofilmes.TelaScore.dominio.analise.meta.Meta;
import com.requisitos.avaliacaofilmes.TelaScore.dominio.analise.meta.MetaId;
import com.requisitos.avaliacaofilmes.TelaScore.dominio.identidade.usuario.UsuarioId;

import io.cucumber.java.pt.Dado;
import io.cucumber.java.pt.E;
import io.cucumber.java.pt.Então;
import io.cucumber.java.pt.Quando;

public class MetaSteps {

    private UsuarioId usuarioId;
    private Meta meta;
    private Exception excecaoCapturada;

    @Dado("que o usuário {string} com ID {int} quer criar uma meta")
    public void preparar_usuario_para_meta(String nome, Integer id) {
        this.usuarioId = new UsuarioId(id);
        this.excecaoCapturada = null;
    }

    @Quando("ele cria uma meta chamada {string} com alvo {int} para o futuro")
    public void criar_meta_valida(String titulo, Integer alvo) {
        try {
            this.meta = new Meta(new MetaId(1), usuarioId, titulo, alvo, LocalDate.now().plusDays(30));
        } catch (Exception e) {
            this.excecaoCapturada = e;
        }
    }

    @Quando("ele tenta criar uma meta com alvo {int} para {int} dias atrás")
    public void criar_meta_passado(Integer alvo, Integer dias) {
        try {
            this.meta = new Meta(new MetaId(1), usuarioId, "Inválida", alvo, LocalDate.now().minusDays(dias));
        } catch (Exception e) {
            this.excecaoCapturada = e;
        }
    }

    @Então("a meta é criada com sucesso")
    public void meta_criada_sucesso() {
        assertNull(excecaoCapturada);
        assertNotNull(meta);
    }

    @E("pertence ao usuário {int}")
    public void pertence_ao_usuario(Integer idEsperado) {
        assertEquals(idEsperado, meta.getUsuarioId().getId());
    }

    @E("o status inicial deve ser {string}")
    public void status_inicial(String statusEsperado) {
        assertEquals(statusEsperado, meta.getStatus().name());
    }

    @Dado("que o usuário {string} com ID {int} tem a meta {string} criada em andamento com alvo {int} e progresso {int}")
    public void meta_em_andamento(String nome, Integer idUser, String titulo, Integer alvo, Integer progresso) {
        this.meta = new Meta(new MetaId(1), new UsuarioId(idUser), titulo, alvo, LocalDate.now().plusDays(10));
        this.excecaoCapturada = null;
    }

    @Quando("ele adiciona {int} de progresso à meta")
    public void adiciona_progresso(Integer valor) {
        try {
            this.meta.adicionarProgresso(valor);
        } catch (Exception e) {
            this.excecaoCapturada = e;
        }
    }

    @Então("a meta deve registrar {int} de quantidade atual")
    public void verifica_quantidade(Integer valorEsperado) {
        assertEquals(valorEsperado, meta.getQuantidadeAtual());
    }

    @E("o status da meta deve continuar {string}")
    public void verifica_status_continuar(String statusEsperado) {
        assertEquals(statusEsperado, meta.getStatus().name());
    }

    @Dado("que o usuário {string} com ID {int} tem a meta {string} que está com status {string}")
    public void meta_com_status_especifico(String nome, Integer idUser, String titulo, String statusAtual) {
        this.meta = new Meta(new MetaId(1), new UsuarioId(idUser), titulo, 10, LocalDate.now().plusDays(10));
        if (statusAtual.equals("CANCELADA")) {
            this.meta.cancelar(); 
        } else if (statusAtual.equals("FALHADA")) {
            this.meta.cancelar(); 
        }
        this.excecaoCapturada = null;
    }

    @Quando("ele tenta adicionar {int} de progresso à meta")
    public void tenta_adicionar_progresso(Integer valor) {
        adiciona_progresso(valor);
    }

    @Dado("que o usuário {string} com ID {int} tem a meta {string} criada com alvo {int} e progresso {int}")
    public void meta_quase_concluida(String nome, Integer idUser, String titulo, Integer alvo, Integer progresso) {
        this.meta = new Meta(new MetaId(1), new UsuarioId(idUser), titulo, alvo, LocalDate.now().plusDays(10));
        this.meta.adicionarProgresso(progresso); 
        this.excecaoCapturada = null;
    }

    @E("ele adicionou {int} de progresso à meta")
    public void e_ele_adicionou_progresso(Integer valor) {
        adiciona_progresso(valor);
    }

    @E("o status da meta mudou para {string}")
    public void status_mudou_para(String statusEsperado) {
        assertEquals(statusEsperado, meta.getStatus().name());
    }

    @Quando("ele remove {int} de progresso da meta por ter registrado errado")
    public void remove_progresso(Integer valor) {
        try {
            this.meta.removerProgresso(valor);
        } catch (Exception e) {
            this.excecaoCapturada = e;
        }
    }

    @Então("a quantidade atual deve ser {int}")
    public void verifica_quantidade_pos_remocao(Integer valorEsperado) {
        assertEquals(valorEsperado, meta.getQuantidadeAtual());
    }

    @E("a meta deve voltar para o status {string}")
    public void verifica_voltou_status(String statusEsperado) {
        assertEquals(statusEsperado, meta.getStatus().name());
    }

    @Quando("ele tenta estender o prazo da meta para a próxima semana")
    public void tenta_estender_prazo() {
        try {
            this.meta.estenderPrazo(LocalDate.now().plusDays(15));
        } catch (Exception e) {
            this.excecaoCapturada = e;
        }
    }

    @Quando("ele tenta estender o prazo da meta para ontem")
    public void ele_tenta_estender_o_prazo_da_meta_para_ontem() {
        try {
            this.meta.estenderPrazo(LocalDate.now().minusDays(1));
        } catch (Exception e) {
            this.excecaoCapturada = e;
        }
    }

    @Então("a meta é atualizada com sucesso")
    public void a_meta_e_atualizada_com_sucesso() {
        assertNull(excecaoCapturada, "Não deveria lançar exceção.");
    }

    @Então("o sistema rejeita a operação")
    public void rejeita_operacao() {
        assertNotNull(excecaoCapturada, "O sistema deveria ter lançado uma exceção na Meta");
    }

    @E("retorna o erro {string}")
    public void verifica_erro(String mensagemEsperada) {
        assertNotNull(excecaoCapturada, "Nenhuma exceção capturada para a Meta");
        assertEquals(mensagemEsperada, excecaoCapturada.getMessage());
    }
}