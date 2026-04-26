package com.requisitos.avaliacaofilmes.TelaScore.dominio.social;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNull;

import com.requisitos.avaliacaofilmes.TelaScore.dominio.social.conexao.Conexao;
import com.requisitos.avaliacaofilmes.TelaScore.dominio.social.conexao.ConexaoId;
import com.requisitos.avaliacaofilmes.TelaScore.dominio.social.conexao.ConexaoServico;
import com.requisitos.avaliacaofilmes.TelaScore.dominio.social.conexao.ConexaoRepositorio;
import com.requisitos.avaliacaofilmes.TelaScore.dominio.identidade.usuario.UsuarioId;

import io.cucumber.java.pt.Dado;
import io.cucumber.java.pt.E;
import io.cucumber.java.pt.Então;
import io.cucumber.java.pt.Quando;

public class ConexaoSteps {

    private UsuarioId seguidorId;
    private Conexao conexao;
    private ConexaoServico conexaoServico;
    private ConexaoRepositorio repositorio;
    private Exception excecaoCapturada;

    // ─────────────────────────────────────────────
    // Backgrounds / Dados compartilhados
    // ─────────────────────────────────────────────

    @Dado("que {string} está autenticada com ID {int}")
    public void que_usuario_esta_autenticado_com_id(String nomeUsuario, Integer id) {
        this.seguidorId = new UsuarioId(id);
        this.repositorio = new ConexaoRepositorioEmMemoria();
        this.conexaoServico = new ConexaoServico(repositorio);
        this.excecaoCapturada = null;
        this.conexao = null;
    }

    @Dado("já existe uma conexão entre o usuário {int} e o usuário {int}")
    public void ja_existe_uma_conexao_entre_usuarios(Integer idSeguidor, Integer idSeguido) {
        Conexao conexaoExistente = new Conexao(
            new ConexaoId(1),
            new UsuarioId(idSeguidor),
            new UsuarioId(idSeguido)
        );
        repositorio.salvar(conexaoExistente);
    }

    // ─────────────────────────────────────────────
    // Cenário 1: Usuário adiciona amigo
    // ─────────────────────────────────────────────

    @Quando("ela segue o usuário com ID {int}")
    public void ela_segue_o_usuario_com_id(Integer idSeguido) {
        try {
            Conexao novaConexao = new Conexao(
                new ConexaoId(2),
                seguidorId,
                new UsuarioId(idSeguido)
            );
            conexaoServico.seguirUsuario(novaConexao);
            this.conexao = novaConexao;
        } catch (Exception e) {
            this.excecaoCapturada = e;
        }
    }

    @Então("a conexão deve ser criada com sucesso")
    public void a_conexao_deve_ser_criada_com_sucesso() {
        assertNull(excecaoCapturada, "Não deveria ter dado erro");
        assertNotNull(conexao, "A conexão deveria ter sido criada");
    }

    // ─────────────────────────────────────────────
    // Cenário 2: Usuário adiciona a si mesmo
    // ─────────────────────────────────────────────

    @Quando("ela tenta seguir a si mesma com ID {int}")
    public void ela_tenta_seguir_a_si_mesma_com_id(Integer id) {
        try {
            this.conexao = new Conexao(
                new ConexaoId(3),
                seguidorId,
                new UsuarioId(id)
            );
        } catch (Exception e) {
            this.excecaoCapturada = e;
        }
    }

    @Então("o sistema rejeita a operação")
    public void o_sistema_rejeita_a_operacao() {
        assertNotNull(excecaoCapturada, "O sistema deveria ter lançado uma exceção");
    }

    @E("retorna o erro {string}")
    public void retorna_o_erro(String mensagemEsperada) {
        assertNotNull(excecaoCapturada, "Nenhuma exceção capturada");
        assertEquals(mensagemEsperada, excecaoCapturada.getMessage());
    }

    // ─────────────────────────────────────────────
    // Cenário 3: Usuário tenta seguir alguém que já segue
    // ─────────────────────────────────────────────

    @Quando("ela tenta seguir novamente o usuário com ID {int}")
    public void ela_tenta_seguir_novamente_o_usuario_com_id(Integer idSeguido) {
        try {
            Conexao novaConexao = new Conexao(
                new ConexaoId(4),
                seguidorId,
                new UsuarioId(idSeguido)
            );
            conexaoServico.seguirUsuario(novaConexao);
            this.conexao = novaConexao;
        } catch (Exception e) {
            this.excecaoCapturada = e;
        }
    }

    @E("retorna o erro de conexão {string}")
    public void retorna_o_erro_de_conexao(String mensagemEsperada) {
        assertNotNull(excecaoCapturada, "Nenhuma exceção capturada");
        assertEquals(mensagemEsperada, excecaoCapturada.getMessage());
    }

    // ─────────────────────────────────────────────
    // Cenário 4: Usuário deixa de seguir outro usuário
    // ─────────────────────────────────────────────

    @Quando("ela deixa de seguir o usuário com ID {int}")
    public void ela_deixa_de_seguir_o_usuario_com_id(Integer idSeguido) {
        try {
            Conexao conexaoExistente = repositorio.buscarConexao(seguidorId, new UsuarioId(idSeguido));
            conexaoServico.deixarDeSeguir(conexaoExistente.getId());
        } catch (Exception e) {
            this.excecaoCapturada = e;
        }
    }

    @Então("a conexão deve ser removida com sucesso")
    public void a_conexao_deve_ser_removida_com_sucesso() {
        assertNull(excecaoCapturada, "Não deveria ter dado erro ao remover");
        Conexao conexaoRemovida = repositorio.buscarConexao(
            seguidorId,
            new UsuarioId(2)
        );
        assertNull(conexaoRemovida, "A conexão deveria ter sido removida do repositório");
    }

    // ─────────────────────────────────────────────
    // Repositório em memória para os testes
    // ─────────────────────────────────────────────

    private static class ConexaoRepositorioEmMemoria implements ConexaoRepositorio {
        private final java.util.List<Conexao> conexoes = new java.util.ArrayList<>();

        @Override
        public void salvar(Conexao conexao) {
            conexoes.add(conexao);
        }

        @Override
        public void remover(ConexaoId id) {
            conexoes.removeIf(c -> c.getId().equals(id));
        }

        @Override
        public Conexao buscarConexao(UsuarioId seguidorId, UsuarioId seguidoId) {
            return conexoes.stream()
                .filter(c -> c.getSeguidorId().equals(seguidorId) && c.getSeguidoId().equals(seguidoId))
                .findFirst()
                .orElse(null);
        }

        @Override
        public java.util.List<Conexao> buscarSeguidoresDe(UsuarioId usuarioId) {
            return conexoes.stream()
                .filter(c -> c.getSeguidoId().equals(usuarioId))
                .toList();
        }

        @Override
        public java.util.List<Conexao> buscarSeguidosPor(UsuarioId usuarioId) {
            return conexoes.stream()
                .filter(c -> c.getSeguidorId().equals(usuarioId))
                .toList();
        }
    }
}