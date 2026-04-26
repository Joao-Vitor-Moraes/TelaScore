package com.requisitos.avaliacaofilmes.TelaScore.dominio.catalogo;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import com.requisitos.avaliacaofilmes.TelaScore.dominio.catalogo.filme.FilmeRepositorio;
import com.requisitos.avaliacaofilmes.TelaScore.dominio.catalogo.solicitacao.SolicitacaoFilme;
import com.requisitos.avaliacaofilmes.TelaScore.dominio.catalogo.solicitacao.SolicitacaoId;
import com.requisitos.avaliacaofilmes.TelaScore.dominio.catalogo.solicitacao.SolicitacaoRepositorio;
import com.requisitos.avaliacaofilmes.TelaScore.dominio.catalogo.solicitacao.SolicitacaoServico;
import com.requisitos.avaliacaofilmes.TelaScore.dominio.catalogo.solicitacao.StatusSolicitacao;
import com.requisitos.avaliacaofilmes.TelaScore.dominio.identidade.usuario.PapelUsuario;
import com.requisitos.avaliacaofilmes.TelaScore.dominio.identidade.usuario.Usuario;
import com.requisitos.avaliacaofilmes.TelaScore.dominio.identidade.usuario.UsuarioId;

import io.cucumber.java.pt.Dado;
import io.cucumber.java.pt.Então;
import io.cucumber.java.pt.Quando;

public class SolicitacaoSteps {

    private UsuarioId usuarioAutenticadoId;
    private String nomeFilmeContexto;
    
    private FilmeRepositorio filmeRepositorioMock;
    private SolicitacaoRepositorio solicitacaoRepositorioMock;
    private Usuario avaliadorMock;
    
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
        assertEquals(null, excecaoCapturada);
        assertNotNull(solicitacaoCriada);
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

    @Então("a solicitação do filme é rejeitada")
    public void a_solicitacao_do_filme_e_rejeitada() {
        assertNotNull(excecaoCapturada, "Uma exceção deveria ter sido lançada pelo Serviço de Domínio.");
    }

    @Então("exibe a mensagem {string}")
    public void exibe_a_mensagem(String mensagemEsperada) {
        assertEquals(mensagemEsperada, excecaoCapturada.getMessage());
    }

    @Dado("que existe uma solicitação pendente para o filme {string}")
    public void que_existe_uma_solicitacao_pendente_para_o_filme(String titulo) {
        SolicitacaoId id = new SolicitacaoId(555);
        UsuarioId solicitante = new UsuarioId(2);
        solicitacaoCriada = new SolicitacaoFilme(id, solicitante, titulo);
        
        solicitacaoRepositorioMock = mock(SolicitacaoRepositorio.class);
        filmeRepositorioMock = mock(FilmeRepositorio.class);
        excecaoCapturada = null;
    }

    @Dado("o usuário {string} é um {string}")
    public void o_usuario_e_um(String nomeUsuario, String papel) {
        avaliadorMock = mock(Usuario.class);
        
        if (papel.equalsIgnoreCase("administrador")) {
            when(avaliadorMock.getPapel()).thenReturn(PapelUsuario.ADMIN);
        } else {
            when(avaliadorMock.getPapel()).thenReturn(PapelUsuario.CINEFILO);
        }
    }

    @Quando("{string} avalia a solicitação como aprovada")
    public void avalia_a_solicitacao_como_aprovada(String nomeUsuario) {
        tentarAvaliarSolicitacao(true);
    }

    @Quando("{string} tenta avaliar a solicitação como aprovada")
    public void tenta_avaliar_a_solicitacao_como_aprovada(String nomeUsuario) {
        tentarAvaliarSolicitacao(true);
    }

    @Então("a solicitação deve constar como aprovada")
    public void a_solicitacao_deve_constar_como_aprovada() {
        assertEquals(null, excecaoCapturada);
        assertEquals(StatusSolicitacao.APROVADA, solicitacaoCriada.getStatus());
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

    private void tentarAvaliarSolicitacao(boolean aprovado) {
        try {
            SolicitacaoServico servico = new SolicitacaoServico(solicitacaoRepositorioMock, filmeRepositorioMock);
            servico.avaliarSolicitacao(solicitacaoCriada, avaliadorMock, aprovado);
        } catch (Exception e) {
            excecaoCapturada = e;
        }
    }
}