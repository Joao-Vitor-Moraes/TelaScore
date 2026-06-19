package com.requisitos.avaliacaofilmes.TelaScore.dominio.identidade;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNull;

import java.text.Normalizer;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import com.requisitos.avaliacaofilmes.TelaScore.dominio.identidade.usuario.Apelido;
import com.requisitos.avaliacaofilmes.TelaScore.dominio.identidade.usuario.Email;
import com.requisitos.avaliacaofilmes.TelaScore.dominio.identidade.usuario.PapelUsuario;
import com.requisitos.avaliacaofilmes.TelaScore.dominio.identidade.usuario.Senha;
import com.requisitos.avaliacaofilmes.TelaScore.dominio.identidade.usuario.Usuario;
import com.requisitos.avaliacaofilmes.TelaScore.dominio.identidade.usuario.UsuarioId;
import com.requisitos.avaliacaofilmes.TelaScore.dominio.identidade.usuario.UsuarioRepositorio;
import com.requisitos.avaliacaofilmes.TelaScore.dominio.identidade.usuario.UsuarioServico;

import io.cucumber.java.pt.Dado;
import io.cucumber.java.pt.E;
import io.cucumber.java.pt.Entao;
import io.cucumber.java.pt.Quando;

public class UsuarioSteps {

    private UsuarioRepositorioMemoria repositorio;
    private UsuarioServico usuarioServico;

    private Usuario usuarioCriado;
    private Usuario usuarioAutenticado;
    private List<Usuario> usuariosRetornados;
    private Exception excecaoCapturada;

    private String nome;
    private String email;
    private String senha;
    private String apelido;
    private String biografia;
    private String avatarUrl;

    private void prepararDominio() {
        repositorio = new UsuarioRepositorioMemoria();
        usuarioServico = new UsuarioServico(repositorio);
        usuarioCriado = null;
        usuarioAutenticado = null;
        usuariosRetornados = null;
        excecaoCapturada = null;
        nome = null;
        email = null;
        senha = null;
        apelido = null;
        biografia = null;
        avatarUrl = null;
    }

    @Dado("que desejo cadastrar um usuario chamado {string}")
    public void que_desejo_cadastrar_um_usuario_chamado(String nome) {
        prepararDominio();
        this.nome = nome;
    }

    @Dado("que desejo cadastrar um usuario sem nome")
    public void que_desejo_cadastrar_um_usuario_sem_nome() {
        prepararDominio();
        nome = "";
    }

    @E("informo o e-mail {string}")
    public void informo_o_email(String email) {
        this.email = email;
    }

    @E("informo a senha {string}")
    public void informo_a_senha(String senha) {
        this.senha = senha;
    }

    @E("informo o apelido {string}")
    public void informo_o_apelido(String apelido) {
        this.apelido = apelido;
    }

    @E("informo a biografia {string}")
    public void informo_a_biografia(String biografia) {
        this.biografia = biografia;
    }

    @E("informo a URL da imagem {string}")
    public void informo_a_url_da_imagem(String avatarUrl) {
        this.avatarUrl = avatarUrl;
    }

    @Quando("crio o usuario no dominio")
    public void crio_o_usuario_no_dominio() {
        try {
            usuarioCriado = new Usuario(
                    new UsuarioId(1),
                    nome,
                    new Email(email),
                    new Senha(senha),
                    PapelUsuario.CINEFILO,
                    new Apelido(apelidoOuNome()),
                    biografia,
                    avatarUrl);
            usuarioServico.salvar(usuarioCriado);
        } catch (Exception e) {
            excecaoCapturada = e;
        }
    }

    @Entao("o usuario deve ser criado com sucesso")
    public void o_usuario_deve_ser_criado_com_sucesso() {
        assertNull(excecaoCapturada);
        assertNotNull(usuarioCriado);
        assertNotNull(usuarioServico.obter(usuarioCriado.getId()));
    }

    @E("o usuario deve receber o papel {string}")
    public void o_usuario_deve_receber_o_papel(String papel) {
        assertEquals(papel, usuarioCriado.getPapel().name());
    }

    @E("o usuario deve ter o apelido {string}")
    public void o_usuario_deve_ter_o_apelido(String apelidoEsperado) {
        assertEquals(apelidoEsperado, usuarioCriado.getApelido().getValor());
    }

    @E("o usuario deve ter a biografia {string}")
    public void o_usuario_deve_ter_a_biografia(String biografiaEsperada) {
        assertEquals(biografiaEsperada, usuarioCriado.getBiografia());
    }

    @E("o usuario deve ter a URL da imagem {string}")
    public void o_usuario_deve_ter_a_url_da_imagem(String avatarUrlEsperada) {
        assertEquals(avatarUrlEsperada, usuarioCriado.getAvatarUrl());
    }

    @E("o usuario nao deve ter biografia")
    public void o_usuario_nao_deve_ter_biografia() {
        assertNull(usuarioCriado.getBiografia());
    }

    @E("o usuario nao deve ter URL da imagem")
    public void o_usuario_nao_deve_ter_url_da_imagem() {
        assertNull(usuarioCriado.getAvatarUrl());
    }

    @Entao("o sistema deve bloquear o cadastro com erro {string}")
    public void o_sistema_deve_bloquear_o_cadastro_com_erro(String mensagemErro) {
        assertNotNull(excecaoCapturada);
        assertEquals(normalizar(mensagemErro), normalizar(excecaoCapturada.getMessage()));
    }

    @Dado("que existem {int} usuarios cadastrados no repositorio")
    public void que_existem_usuarios_cadastrados_no_repositorio(Integer quantidade) {
        prepararDominio();
        for (int i = 1; i <= quantidade; i++) {
            usuarioServico.salvar(usuarioPadrao(i, PapelUsuario.CINEFILO));
        }
    }

    @Dado("que nao existem usuarios cadastrados no repositorio")
    public void que_nao_existem_usuarios_cadastrados_no_repositorio() {
        prepararDominio();
    }

    @Quando("solicito a listagem de usuarios no dominio")
    public void solicito_a_listagem_de_usuarios_no_dominio() {
        usuariosRetornados = usuarioServico.listarTodos();
    }

    @Entao("devem ser retornados {int} usuarios cadastrados")
    public void devem_ser_retornados_usuarios_cadastrados(Integer quantidadeEsperada) {
        assertNotNull(usuariosRetornados);
        assertEquals(quantidadeEsperada, usuariosRetornados.size());
    }

    @Entao("deve ser retornada uma lista vazia")
    public void deve_ser_retornada_uma_lista_vazia() {
        assertNotNull(usuariosRetornados);
        assertEquals(0, usuariosRetornados.size());
    }

    @Dado("que existe um usuario {string} cadastrado com e-mail {string} e senha {string}")
    public void que_existe_um_usuario_cadastrado_com_email_e_senha(String papel, String email, String senha) {
        prepararDominio();
        Usuario usuario = new Usuario(
                new UsuarioId(1),
                "Usuario Teste",
                new Email(email),
                new Senha(senha),
                PapelUsuario.valueOf(papel));
        usuarioServico.salvar(usuario);
    }

    @Dado("que nao existe usuario cadastrado com e-mail {string}")
    public void que_nao_existe_usuario_cadastrado_com_email(String email) {
        prepararDominio();
        this.email = email;
    }

    @Quando("valido login com e-mail {string} e senha {string} no dominio")
    public void valido_login_com_email_e_senha_no_dominio(String email, String senha) {
        try {
            Usuario usuario = usuarioServico.obterPorEmail(new Email(email));
            if (usuario == null || !usuario.getSenha().getValor().equals(new Senha(senha).getValor())) {
                throw new IllegalArgumentException("Erro ao fazer login");
            }
            usuarioAutenticado = usuario;
        } catch (Exception e) {
            excecaoCapturada = e;
        }
    }

    @Entao("o login deve ser validado com sucesso")
    public void o_login_deve_ser_validado_com_sucesso() {
        assertNull(excecaoCapturada);
        assertNotNull(usuarioAutenticado);
    }

    @E("o usuario autenticado deve ter ID {int}")
    public void o_usuario_autenticado_deve_ter_id(Integer idEsperado) {
        assertNotNull(usuarioAutenticado);
        assertEquals(idEsperado, usuarioAutenticado.getId().getId());
    }

    @E("o usuario autenticado deve ter papel {string}")
    public void o_usuario_autenticado_deve_ter_papel(String papelEsperado) {
        assertNotNull(usuarioAutenticado);
        assertEquals(papelEsperado, usuarioAutenticado.getPapel().name());
    }

    @Entao("o login deve ser rejeitado com erro {string}")
    public void o_login_deve_ser_rejeitado_com_erro(String mensagemErro) {
        assertNotNull(excecaoCapturada);
        assertNull(usuarioAutenticado);
        assertEquals(mensagemErro, excecaoCapturada.getMessage());
    }

    @Dado("que existe um usuario cadastrado com ID {int}")
    public void que_existe_um_usuario_cadastrado_com_id(Integer id) {
        prepararDominio();
        usuarioServico.salvar(usuarioPadrao(id, PapelUsuario.CINEFILO));
    }

    @Dado("que nao existe usuario cadastrado com ID {int}")
    public void que_nao_existe_usuario_cadastrado_com_id(Integer id) {
        prepararDominio();
    }

    @Quando("solicito a remocao do usuario com ID {int} no dominio")
    public void solicito_a_remocao_do_usuario_com_id_no_dominio(Integer id) {
        try {
            usuarioServico.remover(new UsuarioId(id));
        } catch (Exception e) {
            excecaoCapturada = e;
        }
    }

    @Entao("o usuario deve ser removido com sucesso")
    public void o_usuario_deve_ser_removido_com_sucesso() {
        assertNull(excecaoCapturada);
        assertEquals(0, usuarioServico.listarTodos().size());
    }

    @Entao("o sistema deve bloquear a remocao com erro {string}")
    public void o_sistema_deve_bloquear_a_remocao_com_erro(String mensagemErro) {
        assertNotNull(excecaoCapturada);
        assertEquals(normalizar(mensagemErro), normalizar(excecaoCapturada.getMessage()));
    }

    private String apelidoOuNome() {
        if (apelido != null && !apelido.isBlank()) {
            return apelido;
        }

        String apelidoPadrao = nome == null ? "usuario" : nome.trim();
        if (apelidoPadrao.isBlank()) {
            apelidoPadrao = "usuario";
        }
        if (apelidoPadrao.length() < 3) {
            apelidoPadrao = (apelidoPadrao + "usr").substring(0, 3);
        }
        return apelidoPadrao.length() > 20 ? apelidoPadrao.substring(0, 20) : apelidoPadrao;
    }

    private Usuario usuarioPadrao(int id, PapelUsuario papel) {
        return new Usuario(
                new UsuarioId(id),
                "Usuario " + id,
                new Email("usuario" + id + "@teste.com"),
                new Senha("123456"),
                papel);
    }

    private String normalizar(String valor) {
        if (valor == null) {
            return null;
        }
        return Normalizer.normalize(valor, Normalizer.Form.NFD)
                .replaceAll("\\p{M}", "")
                .replace("Ã¡", "a")
                .replace("Ã©", "e")
                .replace("Ã­", "i")
                .replace("Ã³", "o")
                .replace("Ãº", "u")
                .replace("Ã£", "a")
                .replace("Ã§", "c")
                .replace("Ãª", "e");
    }

    private static class UsuarioRepositorioMemoria implements UsuarioRepositorio {
        private final Map<Integer, Usuario> usuarios = new LinkedHashMap<>();

        @Override
        public void salvar(Usuario usuario) {
            usuarios.put(usuario.getId().getId(), usuario);
        }

        @Override
        public Usuario obter(UsuarioId id) {
            return usuarios.get(id.getId());
        }

        @Override
        public Usuario obterPorEmail(Email email) {
            return usuarios.values().stream()
                    .filter(usuario -> usuario.getEmail().getEndereco().equalsIgnoreCase(email.getEndereco()))
                    .findFirst()
                    .orElse(null);
        }

        @Override
        public void remover(UsuarioId id) {
            usuarios.remove(id.getId());
        }

        @Override
        public void removerPorEmail(Email email) {
            Usuario usuario = obterPorEmail(email);
            if (usuario != null) {
                usuarios.remove(usuario.getId().getId());
            }
        }

        @Override
        public List<Usuario> listarTodos() {
            return new ArrayList<>(usuarios.values());
        }
    }
}
