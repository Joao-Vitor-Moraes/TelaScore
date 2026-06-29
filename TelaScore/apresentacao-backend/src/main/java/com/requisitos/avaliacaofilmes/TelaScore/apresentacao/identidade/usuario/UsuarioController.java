package com.requisitos.avaliacaofilmes.TelaScore.apresentacao.identidade.usuario;

import com.requisitos.avaliacaofilmes.TelaScore.aplicacao.identidade.CadastrarUsuarioComando;
import com.requisitos.avaliacaofilmes.TelaScore.aplicacao.identidade.CadastrarUsuarioCasoDeUso;
import com.requisitos.avaliacaofilmes.TelaScore.aplicacao.identidade.EditarMeuUsuarioCasoDeUso;
import com.requisitos.avaliacaofilmes.TelaScore.aplicacao.identidade.EditarMeuUsuarioComando;
import com.requisitos.avaliacaofilmes.TelaScore.aplicacao.identidade.EditarUsuarioCasoDeUso;
import com.requisitos.avaliacaofilmes.TelaScore.aplicacao.identidade.EditarUsuarioComando;
import com.requisitos.avaliacaofilmes.TelaScore.aplicacao.identidade.ListarUsuariosCasoDeUso;
import com.requisitos.avaliacaofilmes.TelaScore.aplicacao.identidade.ListarUsuariosComando;
import com.requisitos.avaliacaofilmes.TelaScore.aplicacao.identidade.LoginUsuarioComando;
import com.requisitos.avaliacaofilmes.TelaScore.aplicacao.identidade.LoginUsuarioCasoDeUso;
import com.requisitos.avaliacaofilmes.TelaScore.aplicacao.identidade.ObterMeuUsuarioCasoDeUso;
import com.requisitos.avaliacaofilmes.TelaScore.aplicacao.identidade.RemoverUsuarioCasoDeUso;
import com.requisitos.avaliacaofilmes.TelaScore.aplicacao.identidade.RemoverUsuarioComando;
import com.requisitos.avaliacaofilmes.TelaScore.aplicacao.identidade.SessaoUsuario;
import com.requisitos.avaliacaofilmes.TelaScore.aplicacao.catalogo.ListaAssistidosServico;
import com.requisitos.avaliacaofilmes.TelaScore.apresentacao.seguranca.TokenServico;
import com.requisitos.avaliacaofilmes.TelaScore.apresentacao.seguranca.TokenServico.TokenGerado;
import com.requisitos.avaliacaofilmes.TelaScore.dominio.identidade.usuario.Usuario;
import com.requisitos.avaliacaofilmes.TelaScore.dominio.identidade.usuario.UsuarioId;
import com.requisitos.avaliacaofilmes.TelaScore.dominio.identidade.usuario.UsuarioLogado;
import com.requisitos.avaliacaofilmes.TelaScore.dominio.identidade.usuario.UsuarioServico;

import java.util.List;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.server.ResponseStatusException;

@RestController
@RequestMapping("/api/identidade/usuario")
public class UsuarioController {

    private final CadastrarUsuarioCasoDeUso cadastrarUsuario;
    private final LoginUsuarioCasoDeUso loginUsuario;
    private final ListarUsuariosCasoDeUso listarUsuarios;
    private final ObterMeuUsuarioCasoDeUso obterMeuUsuario;
    private final EditarMeuUsuarioCasoDeUso editarMeuUsuario;
    private final EditarUsuarioCasoDeUso editarUsuario;
    private final RemoverUsuarioCasoDeUso removerUsuario;
    private final SessaoUsuario sessaoUsuario;
    private final TokenServico tokenServico;
    private final UsuarioServico usuarioServico;
    private final ListaAssistidosServico listaAssistidosServico;

    public UsuarioController(
            CadastrarUsuarioCasoDeUso cadastrarUsuario,
            LoginUsuarioCasoDeUso loginUsuario,
            ListarUsuariosCasoDeUso listarUsuarios,
            ObterMeuUsuarioCasoDeUso obterMeuUsuario,
            EditarMeuUsuarioCasoDeUso editarMeuUsuario,
            EditarUsuarioCasoDeUso editarUsuario,
            RemoverUsuarioCasoDeUso removerUsuario,
            SessaoUsuario sessaoUsuario,
            TokenServico tokenServico,
            UsuarioServico usuarioServico,
            ListaAssistidosServico listaAssistidosServico) {
        this.cadastrarUsuario = cadastrarUsuario;
        this.loginUsuario = loginUsuario;
        this.listarUsuarios = listarUsuarios;
        this.obterMeuUsuario = obterMeuUsuario;
        this.editarMeuUsuario = editarMeuUsuario;
        this.editarUsuario = editarUsuario;
        this.removerUsuario = removerUsuario;
        this.sessaoUsuario = sessaoUsuario;
        this.tokenServico = tokenServico;
        this.usuarioServico = usuarioServico;
        this.listaAssistidosServico = listaAssistidosServico;
    }

    @PostMapping({"/registrar"})
    public ResponseEntity<?> registrar(@RequestBody RegistrarRequest request) {
        if (request == null || request.nome() == null || request.email() == null || request.senha() == null) {
            return ResponseEntity.badRequest().body(new ErroLoginResponse("Dados de cadastro invalidos"));
        }

        try {
            cadastrarUsuario.executar(new CadastrarUsuarioComando(
                    request.nome(),
                    request.email(),
                    request.senha(),
                    request.apelido(),
                    request.biografia(),
                    request.avatarUrl()));
            UsuarioLogado usuarioLogado = loginUsuario.executar(new LoginUsuarioComando(request.email(), request.senha()));
            listaAssistidosServico.garantirLista(new UsuarioId(usuarioLogado.getId().getId()));
            TokenGerado token = tokenServico.gerar(usuarioLogado);
            return ResponseEntity.status(HttpStatus.CREATED).body(LoginResponse.de(usuarioLogado, token));
        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest().body(new ErroLoginResponse(e.getMessage()));
        }
    }

    @PostMapping("/login")
    public ResponseEntity<?> login(@RequestBody LoginRequest request) {
        if (request == null || request.email() == null || request.senha() == null) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(new ErroLoginResponse("Erro ao fazer login"));
        }

        try {
            UsuarioLogado usuarioLogado = loginUsuario.executar(new LoginUsuarioComando(request.email(), request.senha()));
            TokenGerado token = tokenServico.gerar(usuarioLogado);
            return ResponseEntity.ok(LoginResponse.de(usuarioLogado, token));
        } catch (IllegalArgumentException e) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(new ErroLoginResponse("Erro ao fazer login"));
        }
    }

    @GetMapping("/sessao")
    public ResponseEntity<UsuarioAutenticadoResponse> obterSessao() {
        UsuarioLogado usuarioLogado = sessaoUsuario.obterUsuarioLogado();
        if (usuarioLogado == null) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
        }

        return ResponseEntity.ok(UsuarioAutenticadoResponse.de(usuarioLogado));
    }

    @GetMapping("/meu-usuario")
    public ResponseEntity<?> obterMeuUsuario() {
        try {
            return ResponseEntity.ok(UsuarioResponse.de(obterMeuUsuario.executar()));
        } catch (IllegalStateException e) {
            return tratarErroDePermissao(e);
        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest().body(new ErroLoginResponse(e.getMessage()));
        }
    }

    @PutMapping("/meu-usuario")
    public ResponseEntity<?> editarMeuUsuario(@RequestBody EditarMeuUsuarioRequest request) {
        if (request == null) {
            return ResponseEntity.badRequest().body(new ErroLoginResponse("Dados de usuario invalidos"));
        }

        try {
            Usuario usuario = editarMeuUsuario.executar(new EditarMeuUsuarioComando(
                    request.nome(),
                    request.apelido(),
                    request.biografia(),
                    request.avatarUrl()));

            return ResponseEntity.ok(UsuarioResponse.de(usuario));
        } catch (IllegalStateException e) {
            return tratarErroDePermissao(e);
        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest().body(new ErroLoginResponse(e.getMessage()));
        }
    }

    @GetMapping
    public ResponseEntity<?> listar() {
        try {
            List<UsuarioResponse> usuarios = listarUsuarios.executar(new ListarUsuariosComando()).stream()
                    .map(UsuarioResponse::de)
                    .toList();

            return ResponseEntity.ok(usuarios);
        } catch (IllegalStateException e) {
            HttpStatus status = e.getMessage() != null && e.getMessage().contains("logado")
                    ? HttpStatus.UNAUTHORIZED
                    : HttpStatus.FORBIDDEN;
            return ResponseEntity.status(status).body(new ErroLoginResponse(e.getMessage()));
        }
    }

    @GetMapping("/{id}")
    public ResponseEntity<?> obterPorId(@PathVariable int id) {
        UsuarioLogado atual = sessaoUsuario.obterUsuarioLogado();
        if (atual == null) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
        }
        return usuarioServico.listarTodos().stream()
                .filter(u -> u.getId().getId() == id)
                .findFirst()
                .<ResponseEntity<?>>map(u -> ResponseEntity.ok(PerfilPublicoResponse.de(u)))
                .orElseGet(() -> ResponseEntity.notFound().build());
    }

    @GetMapping("/buscar")
    public List<UsuarioPublicoResponse> buscarPorApelido(@RequestParam(defaultValue = "") String apelido) {
        UsuarioLogado atual = sessaoUsuario.obterUsuarioLogado();
        if (atual == null) {
            throw new ResponseStatusException(HttpStatus.UNAUTHORIZED, "Faça login para buscar usuários.");
        }

        String termo = apelido.trim().replaceFirst("^@", "").toLowerCase();
        if (termo.length() < 2) {
            return List.of();
        }

        return usuarioServico.listarTodos().stream()
                .filter(usuario -> usuario.getId().getId() != atual.getId().getId())
                .filter(usuario -> usuario.getApelido().getValor().toLowerCase().contains(termo))
                .limit(8)
                .map(UsuarioPublicoResponse::de)
                .toList();
    }

    @PutMapping("/{id}")
    public ResponseEntity<?> editar(@PathVariable int id, @RequestBody EditarUsuarioRequest request) {
        if (request == null) {
            return ResponseEntity.badRequest().body(new ErroLoginResponse("Dados de usuario invalidos"));
        }

        try {
            Usuario usuario = editarUsuario.executar(new EditarUsuarioComando(
                    id,
                    request.nome(),
                    request.email(),
                    request.papel(),
                    request.apelido(),
                    request.biografia(),
                    request.avatarUrl()));

            return ResponseEntity.ok(UsuarioResponse.de(usuario));
        } catch (IllegalStateException e) {
            return tratarErroDePermissao(e);
        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest().body(new ErroLoginResponse(e.getMessage()));
        }
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<?> remover(@PathVariable int id) {
        try {
            removerUsuario.executar(new RemoverUsuarioComando(id));
            return ResponseEntity.noContent().build();
        } catch (IllegalStateException e) {
            return tratarErroDePermissao(e);
        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest().body(new ErroLoginResponse(e.getMessage()));
        }
    }

    @PostMapping("/logout")
    public ResponseEntity<Void> logout() {
        sessaoUsuario.encerrar();
        return ResponseEntity.noContent().build();
    }

    public static record LoginRequest(String email, String senha) {
    }

    public static record RegistrarRequest(
            String nome,
            String email,
            String senha,
            String apelido,
            String biografia,
            String avatarUrl) {
    }

    public static record EditarUsuarioRequest(
            String nome,
            String email,
            String papel,
            String apelido,
            String biografia,
            String avatarUrl) {
    }

    public static record EditarMeuUsuarioRequest(
            String nome,
            String apelido,
            String biografia,
            String avatarUrl) {
    }

    public static record LoginResponse(
            int id,
            String papel,
            String token,
            String tipoToken,
            long expiraEmSegundos) {
        static LoginResponse de(UsuarioLogado usuarioLogado, TokenGerado token) {
            return new LoginResponse(
                    usuarioLogado.getId().getId(),
                    usuarioLogado.getPapel().name(),
                    token.valor(),
                    "Bearer",
                    token.expiraEmSegundos());
        }
    }

    public static record UsuarioAutenticadoResponse(int id, String papel) {
        static UsuarioAutenticadoResponse de(UsuarioLogado usuarioLogado) {
            return new UsuarioAutenticadoResponse(usuarioLogado.getId().getId(), usuarioLogado.getPapel().name());
        }
    }

    public static record UsuarioResponse(
            int id,
            String nome,
            String email,
            String papel,
            String apelido,
            String biografia,
            String avatarUrl) {
        static UsuarioResponse de(Usuario usuario) {
            return new UsuarioResponse(
                    usuario.getId().getId(),
                    usuario.getNome(),
                    usuario.getEmail().getEndereco(),
                    usuario.getPapel().name(),
                    usuario.getApelido().getValor(),
                    usuario.getBiografia(),
                    usuario.getAvatarUrl());
        }
    }

    public static record UsuarioPublicoResponse(int id, String nome, String apelido, String avatarUrl) {
        static UsuarioPublicoResponse de(Usuario usuario) {
            return new UsuarioPublicoResponse(
                    usuario.getId().getId(),
                    usuario.getNome(),
                    usuario.getApelido().getValor(),
                    usuario.getAvatarUrl());
        }
    }

    public static record ErroLoginResponse(String mensagem) {
    }

    public static record PerfilPublicoResponse(int id, String nome, String apelido, String papel, String biografia, String avatarUrl) {
        static PerfilPublicoResponse de(Usuario usuario) {
            return new PerfilPublicoResponse(
                    usuario.getId().getId(),
                    usuario.getNome(),
                    usuario.getApelido().getValor(),
                    usuario.getPapel().name(),
                    usuario.getBiografia(),
                    usuario.getAvatarUrl());
        }
    }

    private ResponseEntity<ErroLoginResponse> tratarErroDePermissao(IllegalStateException e) {
        HttpStatus status = e.getMessage() != null && e.getMessage().contains("logado")
                ? HttpStatus.UNAUTHORIZED
                : HttpStatus.FORBIDDEN;
        return ResponseEntity.status(status).body(new ErroLoginResponse(e.getMessage()));
    }
}
