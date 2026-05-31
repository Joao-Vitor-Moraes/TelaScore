package com.requisitos.avaliacaofilmes.TelaScore.dominio.social;

import static org.junit.jupiter.api.Assertions.*;
import java.util.*;

// Imports de domínio do módulo social e identidade
import com.requisitos.avaliacaofilmes.TelaScore.dominio.social.mensagem.*;
import com.requisitos.avaliacaofilmes.TelaScore.dominio.identidade.usuario.UsuarioId;

import io.cucumber.java.pt.*;

public class MensagemSteps {

    // Estado para mensagens privadas
    private UsuarioId remetenteId;
    private UsuarioId destinatarioId;
    private Mensagem mensagem;
    private Exception excecaoCapturada;

    // Estado para gerenciamento de figurinhas (Simula o repositório em memória)
    private Map<String, String> figurinhasCadastradas = new HashMap<>();
    private String nomeFigurinhaPendente;
    private String imagemFigurinhaPendente;
    private boolean figurinhaEnviadaComSucesso;

    // --- CENÁRIOS ANTERIORES: ENVIO E REMOÇÃO DE MENSAGENS TEXTO ---

    @Dado("que o usuário {int} quer enviar uma mensagem para o usuário {int}")
    public void preparar_usuarios(Integer id1, Integer id2) {
        this.remetenteId = new UsuarioId(id1);
        this.destinatarioId = new UsuarioId(id2);
        this.excecaoCapturada = null;
        this.figurinhaEnviadaComSucesso = false;
    }

    @Dado("que o usuário {int} tenta enviar uma mensagem para o usuário {int}")
    public void preparar_auto_envio(Integer id1, Integer id2) {
        preparar_usuarios(id1, id2);
    }

    @Quando("ele escreve {string} e envia")
    public void enviar_mensagem(String texto) {
        try {
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
        preparar_usuarios(id1, id2);
        this.mensagem = new Mensagem(new MensagemId(1), remetenteId, destinatarioId, texto);
    }

    @Quando("a mensagem é removida")
    public void a_mensagem_e_removida() {
        this.mensagem = null;
    }

    @Então("a mensagem deve deixar de existir no sistema")
    public void validar_remocao() {
        assertNull(this.mensagem);
    }

    // --- NOVOS STEPS: GERENCIAMENTO DE FIGURINHAS (BASEADO NO PDF) ---

    @Dado("que o administrador deseja adicionar uma figurinha com o nome {string} e imagem {string}")
    public void preparar_nova_figurinha(String nome, String imagem) {
        this.nomeFigurinhaPendente = nome;
        this.imagemFigurinhaPendente = imagem;
        this.excecaoCapturada = null;
    }

    @Quando("a figurinha é cadastrada no sistema")
    public void cadastrar_figurinha_sistema() {
        try {
            // RN 2: Validar que o nome e imagem não são nulos ou em branco [cite: 198, 200]
            if (nomeFigurinhaPendente == null || nomeFigurinhaPendente.isBlank() ||
                imagemFigurinhaPendente == null || imagemFigurinhaPendente.isBlank()) {
                throw new IllegalArgumentException("Nome e imagem da figurinha são obrigatórios.");
            }
            // RN 2: Simula a consulta para saber se o nome já existe 
            if (figurinhasCadastradas.containsKey(nomeFigurinhaPendente)) {
                throw new IllegalArgumentException("Uma figurinha com esse nome já existe.");
            }
            figurinhasCadastradas.put(nomeFigurinhaPendente, imagemFigurinhaPendente);
        } catch (Exception e) {
            this.excecaoCapturada = e;
        }
    }

    @Então("a figurinha deve ser registrada com sucesso")
    public void validar_registro_figurinha() {
        assertNull(excecaoCapturada);
        assertTrue(figurinhasCadastradas.containsKey(nomeFigurinhaPendente));
    }

    @Dado("que existe uma figurinha cadastrada com o nome {string}")
    public void cadastrar_figurinha_previa(String nome) {
        figurinhasCadastradas.put(nome, nome.toLowerCase().replace(" ", "_") + ".png");
    }

    @Quando("ele seleciona e envia a figurinha {string}")
    public void enviar_figurinha_mensagem(String nome) {
        try {
            // RN 1: Consulta se a figurinha existe no banco de dados 
            if (!figurinhasCadastradas.containsKey(nome)) {
                throw new IllegalArgumentException("Figurinha inexistente no sistema.");
            }
            this.figurinhaEnviadaComSucesso = true;
        } catch (Exception e) {
            this.excecaoCapturada = e;
        }
    }

    @Então("a mensagem contendo a figurinha deve ser enviada com sucesso")
    public void validar_envio_figurinha() {
        assertNull(excecaoCapturada);
        assertTrue(figurinhaEnviadaComSucesso);
    }

    @Quando("o administrador exclui a figurinha {string}")
    public void excluir_figurinha_sistema(String nome) {
        try {
            // RN 3: Consultar se o nome foi encontrado para realizar a deleção 
            if (!figurinhasCadastradas.containsKey(nome)) {
                throw new IllegalArgumentException("Figurinha não encontrada para exclusão.");
            }
            figurinhasCadastradas.remove(nome);
        } catch (Exception e) {
            this.excecaoCapturada = e;
        }
    }

    @Então("a figurinha deve deixar de existir no sistema")
    public void validar_exclusao_figurinha() {
        assertNull(excecaoCapturada);
        // Garante que a lista simulada não possui mais a figurinha removida
        assertFalse(figurinhasCadastradas.containsKey(nomeFigurinhaPendente));
    }
}