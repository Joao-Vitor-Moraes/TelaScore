package com.requisitos.avaliacaofilmes.TelaScore.dominio.catalogo;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

import com.requisitos.avaliacaofilmes.TelaScore.dominio.catalogo.avaliacao.Avaliacao;
import com.requisitos.avaliacaofilmes.TelaScore.dominio.catalogo.avaliacao.AvaliacaoId;
import com.requisitos.avaliacaofilmes.TelaScore.dominio.catalogo.avaliacao.Nota;
import com.requisitos.avaliacaofilmes.TelaScore.dominio.catalogo.filme.FilmeId;
import com.requisitos.avaliacaofilmes.TelaScore.dominio.identidade.usuario.UsuarioId;

import io.cucumber.java.pt.Dado;
import io.cucumber.java.pt.Então;
import io.cucumber.java.pt.Quando;

public class AvaliacaoSteps {

    private FilmeId filmeId;
    private UsuarioId usuarioId;
    private AvaliacaoId avaliacaoId;
    
    private Avaliacao avaliacaoCriada;
    private Exception excecaoCapturada;

    @Dado("que eu quero avaliar o filme {string} como o usuário {int}")
    public void que_eu_quero_avaliar_o_filme_como_o_usuário(String idFilme, Integer idUsuario) {
        filmeId = new FilmeId(idFilme);
        usuarioId = new UsuarioId(idUsuario);
        avaliacaoId = new AvaliacaoId(1);

        excecaoCapturada = null;
        avaliacaoCriada = null;
    }

    @Quando("eu tento criar uma avaliação com a nota {int}")
    public void eu_tento_criar_uma_avaliação_com_a_nota(Integer valorNota) {
        try {
            Nota nota = new Nota(valorNota);
            String resenha = new String("Ótimo filme! Recomendo a todos.");
            avaliacaoCriada = new Avaliacao(avaliacaoId, filmeId, usuarioId, nota, resenha);
        } catch (Exception e) {
            excecaoCapturada = e;
        }
    }

    @Então("a avaliação deve ser criada com sucesso")
    public void a_avaliação_deve_ser_criada_com_sucesso() {
        assertEquals(null, excecaoCapturada, "Nenhuma exceção deveria ter sido lançada");
        assertNotNull(avaliacaoCriada, "A entidade Avaliacao deveria ter sido criada");
    }

    @Então("o sistema deve bloquear a criação informando que a nota é inválida")
    public void o_sistema_deve_bloquear_a_criação_informando_que_a_nota_é_inválida() {
        assertNotNull(excecaoCapturada, "Uma exceção deveria ter sido lançada pela classe Nota");
        assertEquals(IllegalArgumentException.class, excecaoCapturada.getClass());
        

    }
}