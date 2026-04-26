package com.requisitos.avaliacaofilmes.TelaScore.aplicacao.catalogo;

import io.cucumber.java.en.Given;
import io.cucumber.java.en.When;
import io.cucumber.java.en.Then;

import org.mockito.Mockito;
import static org.mockito.Mockito.*;
import static org.junit.jupiter.api.Assertions.*;

import com.requisitos.avaliacaofilmes.TelaScore.aplicacao.identidade.GeradorId;
import com.requisitos.avaliacaofilmes.TelaScore.dominio.catalogo.solicitacao.SolicitacaoRepositorio;
import com.requisitos.avaliacaofilmes.TelaScore.dominio.catalogo.solicitacao.SolicitacaoServico;
import com.requisitos.avaliacaofilmes.TelaScore.dominio.catalogo.filme.FilmeRepositorio;

public class SolicitacaoFilmesSteps {

    private GeradorId geradorId;
    private SolicitacaoRepositorio solicitacaoRepositorio;
    private FilmeRepositorio filmeRepositorio;
    private SolicitacaoServico solicitacaoServico;
    private SolicitarFilmeCasoDeUso solicitarFilmeCasoDeUso;

    private SolicitarFilmeComando comando;
    private SolicitacaoResumo resumo;
    private Exception excecao;
    private String nomeUsuarioLogado;

    @Given("que o usuário {string} está autenticado")
    public void que_o_usuario_esta_autenticado(String usuario) {
        nomeUsuarioLogado = usuario;
        
        geradorId = mock(GeradorId.class);
        when(geradorId.gerarProximoIdSolicitacao()).thenReturn(1);
        
        solicitacaoRepositorio = mock(SolicitacaoRepositorio.class);
        filmeRepositorio = mock(FilmeRepositorio.class);
        when(filmeRepositorio.existeComTitulo(anyString())).thenReturn(false);
        
        solicitacaoServico = new SolicitacaoServico(solicitacaoRepositorio, filmeRepositorio);
        solicitarFilmeCasoDeUso = new SolicitarFilmeCasoDeUso(solicitacaoServico, geradorId);
    }

    @When("ela solicita o filme {string}")
    public void ela_solicita_o_filme(String titulo) {
        comando = new SolicitarFilmeComando(10, titulo, "Justificativa");
        try {
            resumo = solicitarFilmeCasoDeUso.executar(comando);
        } catch (Exception e) {
            excecao = e;
        }
    }

    @Then("a solicitação é registrada com sucesso")
    public void a_solicitacao_e_registrada_com_sucesso() {
        assertNull(excecao);
        assertNotNull(resumo);
        assertEquals(1, resumo.id());
        verify(solicitacaoRepositorio, times(1)).salvar(any());
    }

    @Given("que o filme {string} já existe no catálogo")
    public void que_o_filme_ja_existe_no_catalogo(String titulo) {
        geradorId = mock(GeradorId.class);
        solicitacaoRepositorio = mock(SolicitacaoRepositorio.class);
        filmeRepositorio = mock(FilmeRepositorio.class);
        
        when(filmeRepositorio.existeComTitulo(titulo)).thenReturn(true);
        
        solicitacaoServico = new SolicitacaoServico(solicitacaoRepositorio, filmeRepositorio);
        solicitarFilmeCasoDeUso = new SolicitarFilmeCasoDeUso(solicitacaoServico, geradorId);
    }

    @When("{string} tenta solicitá-lo")
    public void tenta_solicita_lo(String usuario) {
        comando = new SolicitarFilmeComando(10, "Interestelar", "Justificativa");
        try {
            resumo = solicitarFilmeCasoDeUso.executar(comando);
        } catch (Exception e) {
            excecao = e;
        }
    }

    @Then("o sistema rejeita a operação")
    public void o_sistema_rejeita_a_operacao() {
        assertNotNull(excecao);
        verify(solicitacaoRepositorio, never()).salvar(any());
    }

    @Then("retorna o erro {string}")
    public void retorna_o_erro(String mensagemErro) {
        assertNotNull(excecao);
        assertEquals(mensagemErro, excecao.getMessage());
    }
}
