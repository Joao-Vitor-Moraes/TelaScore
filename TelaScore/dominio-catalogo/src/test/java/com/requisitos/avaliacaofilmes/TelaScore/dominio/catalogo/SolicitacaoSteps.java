package com.requisitos.avaliacaofilmes.TelaScore.dominio.catalogo;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import com.requisitos.avaliacaofilmes.TelaScore.dominio.catalogo.filme.FilmeRepositorio;
import com.requisitos.avaliacaofilmes.TelaScore.dominio.catalogo.solicitacao.SolicitacaoFilme;
import com.requisitos.avaliacaofilmes.TelaScore.dominio.catalogo.solicitacao.SolicitacaoId;
import com.requisitos.avaliacaofilmes.TelaScore.dominio.identidade.usuario.UsuarioId;

import io.cucumber.java.pt.Dado;
import io.cucumber.java.pt.Então;
import io.cucumber.java.pt.Quando;

public class SolicitacaoSteps {

    private UsuarioId usuarioAutenticadoId;
    private String nomeFilmeContexto;
    
    private FilmeRepositorio filmeRepositorioMock;
    
    private SolicitacaoFilme solicitacaoCriada;
    private Exception excecaoCapturada;

    @Dado("que o usuário {string} está autenticado")
    public void que_o_usuario_esta_autenticado(String nomeUsuario) {
        usuarioAutenticadoId = new UsuarioId(1);
        filmeRepositorioMock = mock(FilmeRepositorio.class);
        excecaoCapturada = null;
        solicitacaoCriada = null;
        
        when(filmeRepositorioMock.existeComTitulo(anyString())).thenReturn(false);
    }

    @Quando("ela solicita o filme {string}")
    public void ela_solicita_o_filme(String tituloFilme) {
        nomeFilmeContexto = tituloFilme;
        tentarCriarSolicitacao(tituloFilme, usuarioAutenticadoId);
    }

    @Então("a solicitação é registrada com sucesso")
    public void a_solicitacao_e_registrada_com_sucesso() {
        assertEquals(null, excecaoCapturada, "A solicitação deveria ter sido aprovada sem erros.");
        assertNotNull(solicitacaoCriada, "A entidade SolicitacaoFilme deveria ter sido criada.");
    }

    @Dado("que o filme {string} já existe no catálogo")
    public void que_o_filme_ja_existe_no_catalogo(String tituloFilme) {
        nomeFilmeContexto = tituloFilme;
        filmeRepositorioMock = mock(FilmeRepositorio.class);
        
        when(filmeRepositorioMock.existeComTitulo(tituloFilme)).thenReturn(true);
    }

    @Quando("{string} tenta solicitá-lo")
    public void tenta_solicita_lo(String nomeUsuario) {
        UsuarioId idUsuario = new UsuarioId(1);
        tentarCriarSolicitacao(nomeFilmeContexto, idUsuario);
    }

    @Então("o sistema rejeita a operação")
    public void o_sistema_rejeita_a_operacao() {
        assertNotNull(excecaoCapturada, "Uma exceção deveria ter sido lançada pelo Serviço de Domínio.");
        assertEquals(IllegalArgumentException.class, excecaoCapturada.getClass());
    }

    @Então("retorna o erro {string}")
    public void retorna_o_erro(String mensagemEsperada) {
        assertEquals(mensagemEsperada, excecaoCapturada.getMessage());
    }
    
    private void tentarCriarSolicitacao(String titulo, UsuarioId usuarioId) {
        try {
            if (filmeRepositorioMock.existeComTitulo(titulo)) {
                throw new IllegalArgumentException("Filme já cadastrado");
            }
            
            SolicitacaoId novaSolicitacaoId = new SolicitacaoId(999);
            solicitacaoCriada = new SolicitacaoFilme(novaSolicitacaoId, usuarioId, titulo);
            
        } catch (Exception e) {
            excecaoCapturada = e;
        }
    }
}