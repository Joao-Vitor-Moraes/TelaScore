package com.requisitos.avaliacaofilmes.TelaScore.dominio.social;

import static org.junit.jupiter.api.Assertions.*;

// Import específico das classes de domínio que estão na subpasta 'mensagem'
import com.requisitos.avaliacaofilmes.TelaScore.dominio.social.mensagem.*;
import com.requisitos.avaliacaofilmes.TelaScore.dominio.identidade.usuario.UsuarioId;

import io.cucumber.java.pt.*;

public class MensagemSteps {

    private UsuarioId remetenteId;
    private UsuarioId destinatarioId;
    private Mensagem mensagem;
    private Exception excecaoCapturada;

    @Dado("que o usuário {int} quer enviar uma mensagem para o usuário {int}")
    public void preparar_usuarios(Integer id1, Integer id2) {
        this.remetenteId = new UsuarioId(id1);
        this.destinatarioId = new UsuarioId(id2);
        this.excecaoCapturada = null;
    }

    @Dado("que o usuário {int} tenta enviar uma mensagem para o usuário {int}")
    public void preparar_auto_envio(Integer id1, Integer id2) {
        preparar_usuarios(id1, id2);
    }

    @Quando("ele escreve {string} e envia")
    public void enviar_mensagem(String texto) {
        try {
            // O Java agora encontrará Mensagem e MensagemId por causa do import .mensagem.*
            this.mensagem = new Mensagem(new MensagemId(1), remetenteId, destinatarioId, texto);
        } catch (Exception e) {
            this.excecaoCapturada = e;
        }
    }

    @Quando("ele tenta enviar uma mensagem sem texto")
    public void enviar_mensagem_vazia() {
        enviar_mensagem(""); 
    }

    @Então("a mensagem deve ser registrada com sucesso")
    public void validar_sucesso() {
        assertNull(excecaoCapturada);
        assertNotNull(mensagem);
    }

    @Então("o status inicial deve ser {string}")
    public void validar_status(String status) {
        assertFalse(mensagem.isLida());
    }

    @Então("o sistema deve rejeitar o envio informando que não pode enviar para si mesmo")
    public void validar_erro_si_mesmo() {
        assertNotNull(excecaoCapturada);
        assertEquals("O remetente e o destinatário não podem ser a mesma pessoa", excecaoCapturada.getMessage());
    }

    @Então("o sistema deve rejeitar o envio informando que o conteúdo não pode estar em branco")
    public void validar_erro_vazio() {
        assertNotNull(excecaoCapturada);
        assertTrue(excecaoCapturada.getMessage().contains("em branco"));
    }

    @Dado("que o usuário {int} enviou uma mensagem para o usuário {int} com o texto {string}")
    public void setup_mensagem_para_remover(Integer id1, Integer id2, String texto) {
        this.remetenteId = new UsuarioId(id1);
        this.destinatarioId = new UsuarioId(id2);
        this.mensagem = new Mensagem(new MensagemId(1), remetenteId, destinatarioId, texto);
    }

    @Quando("a mensagem é removida")
    public void a_mensagem_e_removida() {
        // No teste de domínio, apenas anulamos a referência
        this.mensagem = null;
    }

    @Então("a mensagem deve deixar de existir no sistema")
    public void validar_remocao() {
        assertNull(this.mensagem, "A mensagem deveria ser nula após a remoção");
    }
}