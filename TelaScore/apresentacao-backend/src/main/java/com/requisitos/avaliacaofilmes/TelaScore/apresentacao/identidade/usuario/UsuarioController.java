package com.requisitos.avaliacaofilmes.TelaScore.apresentacao.identidade.usuario;

import com.requisitos.avaliacaofilmes.TelaScore.aplicacao.identidade.CadastrarUsuarioComando;
import com.requisitos.avaliacaofilmes.TelaScore.aplicacao.identidade.CadastrarUsuarioCasoDeUso;
import com.requisitos.avaliacaofilmes.TelaScore.aplicacao.identidade.LoginUsuarioComando;
import com.requisitos.avaliacaofilmes.TelaScore.aplicacao.identidade.LoginUsuarioCasoDeUso;
import com.requisitos.avaliacaofilmes.TelaScore.aplicacao.identidade.SessaoUsuario;
import com.requisitos.avaliacaofilmes.TelaScore.apresentacao.seguranca.TokenServico;
import com.requisitos.avaliacaofilmes.TelaScore.apresentacao.seguranca.TokenServico.TokenGerado;
import com.requisitos.avaliacaofilmes.TelaScore.dominio.identidade.usuario.UsuarioLogado;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/identidade/usuario")
public class UsuarioController {

    private final CadastrarUsuarioCasoDeUso cadastrarUsuario;
    private final LoginUsuarioCasoDeUso loginUsuario;
    private final SessaoUsuario sessaoUsuario;
    private final TokenServico tokenServico;

    public UsuarioController(
            CadastrarUsuarioCasoDeUso cadastrarUsuario,
            LoginUsuarioCasoDeUso loginUsuario,
            SessaoUsuario sessaoUsuario,
            TokenServico tokenServico) {
        this.cadastrarUsuario = cadastrarUsuario;
        this.loginUsuario = loginUsuario;
        this.sessaoUsuario = sessaoUsuario;
        this.tokenServico = tokenServico;
    }

    @PostMapping({"/registrar"})
    public ResponseEntity<?> registrar(@RequestBody RegistrarRequest request) {
        if (request == null || request.nome() == null || request.email() == null || request.senha() == null) {
            return ResponseEntity.badRequest().body(new ErroLoginResponse("Dados de cadastro invalidos"));
        }

        try {
            cadastrarUsuario.executar(new CadastrarUsuarioComando(request.nome(), request.email(), request.senha()));
            UsuarioLogado usuarioLogado = loginUsuario.executar(new LoginUsuarioComando(request.email(), request.senha()));
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

    @PostMapping("/logout")
    public ResponseEntity<Void> logout() {
        sessaoUsuario.encerrar();
        return ResponseEntity.noContent().build();
    }

    public static record LoginRequest(String email, String senha) {
    }

    public static record RegistrarRequest(String nome, String email, String senha) {
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

    public static record ErroLoginResponse(String mensagem) {
    }
}
