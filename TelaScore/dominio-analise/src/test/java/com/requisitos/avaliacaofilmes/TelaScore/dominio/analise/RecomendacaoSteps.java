package com.requisitos.avaliacaofilmes.TelaScore.dominio.analise;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNull;

import com.requisitos.avaliacaofilmes.TelaScore.dominio.analise.recomendacao.Recomendacao;
import com.requisitos.avaliacaofilmes.TelaScore.dominio.analise.recomendacao.RecomendacaoId;
import com.requisitos.avaliacaofilmes.TelaScore.dominio.catalogo.filme.FilmeId;
import com.requisitos.avaliacaofilmes.TelaScore.dominio.identidade.usuario.UsuarioId;

import io.cucumber.java.pt.Dado;
import io.cucumber.java.pt.E;
import io.cucumber.java.pt.Então;
import io.cucumber.java.pt.Quando;

public class RecomendacaoSteps {

    private UsuarioId remetenteId;
    private UsuarioId destinatarioId;
    private FilmeId filmeId;
    private Recomendacao recomendacao;
    private Exception excecaoCapturada;

    @Dado("que o usuário remetente com ID {int} quer recomendar o filme {string}")
    public void que_o_usuario_remetente_com_id_quer_recomendar_o_filme(Integer id, String codFilme) {
        this.remetenteId = new UsuarioId(id);
        this.filmeId = new FilmeId(codFilme);
        this.excecaoCapturada = null;
    }

    @Quando("ele envia a recomendação para o destinatário com ID {int} com a mensagem {string}")
    public void ele_envia_a_recomendacao_para_o_destinatario_com_id_com_a_mensagem(Integer idDest, String msg) {
        this.destinatarioId = new UsuarioId(idDest);
        try {
            this.recomendacao = new Recomendacao(new RecomendacaoId(1), destinatarioId, filmeId, 100.0, remetenteId, msg);
        } catch (Exception e) {
            this.excecaoCapturada = e;
        }
    }

    @Quando("ele tenta enviar a recomendação para o destinatário com ID {int}")
    public void ele_tenta_enviar_a_recomendacao_para_o_destinatario_com_id(Integer idDest) {
        this.destinatarioId = new UsuarioId(idDest);
        try {
            this.recomendacao = new Recomendacao(new RecomendacaoId(1), destinatarioId, filmeId, 100.0, remetenteId, "Indicação");
        } catch (Exception e) {
            this.excecaoCapturada = e;
        }
    }

    @Então("a recomendação deve ser criada com sucesso")
    public void a_recomendacao_deve_ser_criada_com_sucesso() {
        assertNull(excecaoCapturada, "Não deveria ter dado erro");
        assertNotNull(recomendacao);
    }

    @E("o status inicial da recomendação deve ser {string}")
    public void o_status_inicial_da_recomendacao_deve_ser(String statusEsperado) {
        assertEquals(statusEsperado, recomendacao.getStatus().name());
    }

    @Então("o sistema deve bloquear a criação informando que a operação é inválida")
    public void o_sistema_deve_bloquear_a_criacao_informando_que_a_operacao_e_invalida() {
        assertNotNull(excecaoCapturada, "O sistema deveria ter lançado uma exceção de validação");
    }

    @Dado("que o usuário com ID {int} possui uma recomendação pendente do filme {string}")
    public void possui_uma_recomendacao_pendente(Integer idDest, String codFilme) {
        this.recomendacao = new Recomendacao(new RecomendacaoId(1), new UsuarioId(idDest), new FilmeId(codFilme), 95.0, new UsuarioId(99), "Veja!");
        this.excecaoCapturada = null;
    }

    @Quando("ele reage aceitando a recomendação")
    public void ele_reage_aceitando_a_recomendacao() {
        try {
            this.recomendacao.aceitar();
        } catch (Exception e) {
            this.excecaoCapturada = e;
        }
    }

    @Então("a recomendação deve ser atualizada com sucesso")
    public void a_recomendacao_deve_ser_atualizada_com_sucesso() {
        assertNull(excecaoCapturada);
    }

    @E("o status da recomendação deve mudar para {string}")
    public void o_status_da_recomendacao_deve_mudar_para(String statusEsperado) {
        assertEquals(statusEsperado, recomendacao.getStatus().name());
    }

    @Dado("que o usuário com ID {int} possui uma recomendação do filme {string} que está com status {string}")
    public void possui_uma_recomendacao_com_status_rejeitada(Integer idDest, String codFilme, String statusAtual) {
        this.recomendacao = new Recomendacao(new RecomendacaoId(1), new UsuarioId(idDest), new FilmeId(codFilme), 95.0, new UsuarioId(99), "Veja!");
        if (statusAtual.equals("REJEITADA")) {
            this.recomendacao.rejeitar(); 
        }
        this.excecaoCapturada = null;
    }

    @Quando("ele tenta reagir aceitando a recomendação")
    public void ele_tenta_reagir_aceitando_a_recomendacao() {
        ele_reage_aceitando_a_recomendacao();
    }

    @Então("o sistema rejeita a recomendação")
    public void o_sistema_rejeita_a_recomendacao() {
        assertNotNull(excecaoCapturada, "O sistema deveria ter lançado uma exceção na Recomendação");
    }

    @E("a recomendação retorna o erro {string}")
    public void a_recomendacao_retorna_o_erro(String mensagemEsperada) {
        assertNotNull(excecaoCapturada, "Nenhuma exceção capturada para a Recomendação");
        assertEquals(mensagemEsperada, excecaoCapturada.getMessage());
    }
}