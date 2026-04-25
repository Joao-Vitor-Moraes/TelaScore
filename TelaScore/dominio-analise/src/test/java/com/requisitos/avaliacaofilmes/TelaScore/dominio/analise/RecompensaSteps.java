package com.requisitos.avaliacaofilmes.TelaScore.dominio.analise;

import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNull;

import com.requisitos.avaliacaofilmes.TelaScore.dominio.analise.recompensa.AcaoPontuada;
import com.requisitos.avaliacaofilmes.TelaScore.dominio.analise.recompensa.Pontos;
import com.requisitos.avaliacaofilmes.TelaScore.dominio.analise.recompensa.RegistroPontuacao;
import com.requisitos.avaliacaofilmes.TelaScore.dominio.analise.recompensa.RegistroPontuacaoId;
import com.requisitos.avaliacaofilmes.TelaScore.dominio.identidade.usuario.UsuarioId;

import io.cucumber.java.pt.Dado;
import io.cucumber.java.pt.Então;
import io.cucumber.java.pt.Quando;

public class RecompensaSteps {

    private UsuarioId usuarioId;
    private AcaoPontuada acao;
    private Pontos pontos;
    private RegistroPontuacao registroPontuacao;
    private Exception excecaoCapturada;

    @Dado("que o usuário {string} realizou uma avaliação")
    public void que_o_usuario_realizou_uma_avaliacao(String nomeUsuario) {
        this.usuarioId = new UsuarioId(1);
        this.acao = AcaoPontuada.AVALIAR_FILME;
        this.pontos = new Pontos(10);
        this.excecaoCapturada = null;
        this.registroPontuacao = null;
    }

    @Quando("o sistema calcula pontos")
    public void o_sistema_calcula_pontos() {
        try {
            this.registroPontuacao = new RegistroPontuacao(
                new RegistroPontuacaoId(1),
                usuarioId,
                pontos,
                acao
            );
        } catch (Exception e) {
            this.excecaoCapturada = e;
        }
    }

    @Então("pontos são adicionados ao perfil")
    public void pontos_sao_adicionados_ao_perfil() {
        assertNull(excecaoCapturada, "Não deveria ter dado erro");
        assertNotNull(registroPontuacao, "O registro de pontuação deveria ter sido criado");
        assertNotNull(registroPontuacao.getPontosGanhos(), "Os pontos ganhos não deveriam ser nulos");
    }
    

    @Dado("que uma ação inválida foi realizada")
    public void que_uma_acao_invalida_foi_realizada() {
        this.usuarioId = new UsuarioId(2);
        this.acao = null;
        this.pontos = null;
        this.excecaoCapturada = null;
        this.registroPontuacao = null;
    }

    @Então("nenhum ponto é atribuído")
    public void nenhum_ponto_e_atribuido() {
        assertNotNull(excecaoCapturada, "O sistema deveria ter lançado uma exceção para ação inválida");
        assertNull(registroPontuacao, "Nenhum registro de pontuação deveria ter sido criado");
    }
}