package com.requisitos.avaliacaofilmes.TelaScore.apresentacao.identidade.usuario;

import com.requisitos.avaliacaofilmes.TelaScore.aplicacao.identidade.CadastrarUsuarioCasoDeUso;
import com.requisitos.avaliacaofilmes.TelaScore.aplicacao.identidade.GeradorId;
import com.requisitos.avaliacaofilmes.TelaScore.aplicacao.identidade.LoginUsuarioCasoDeUso;
import com.requisitos.avaliacaofilmes.TelaScore.aplicacao.identidade.SessaoUsuario;
import com.requisitos.avaliacaofilmes.TelaScore.dominio.identidade.usuario.Email;
import com.requisitos.avaliacaofilmes.TelaScore.dominio.identidade.usuario.PapelUsuario;
import com.requisitos.avaliacaofilmes.TelaScore.dominio.identidade.usuario.Senha;
import com.requisitos.avaliacaofilmes.TelaScore.dominio.identidade.usuario.Usuario;
import com.requisitos.avaliacaofilmes.TelaScore.dominio.identidade.usuario.UsuarioId;
import com.requisitos.avaliacaofilmes.TelaScore.dominio.identidade.usuario.UsuarioRepositorio;
import com.requisitos.avaliacaofilmes.TelaScore.dominio.identidade.usuario.UsuarioServico;

import org.springframework.boot.ApplicationRunner;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.context.annotation.SessionScope;

@Configuration
public class UsuarioConfig {

    private static final String ADMIN_EMAIL = "admin@admin.com";
    private static final String ADMIN_SENHA = "admin123";

    @Bean
    public UsuarioServico usuarioServico(UsuarioRepositorio usuarioRepositorio) {
        return new UsuarioServico(usuarioRepositorio);
    }

    @Bean
    @SessionScope
    public SessaoUsuario sessaoUsuario() {
        return new SessaoUsuario();
    }

    @Bean
    public LoginUsuarioCasoDeUso loginUsuarioCasoDeUso(UsuarioServico usuarioServico, SessaoUsuario sessaoUsuario) {
        return new LoginUsuarioCasoDeUso(usuarioServico, sessaoUsuario);
    }

    @Bean
    public CadastrarUsuarioCasoDeUso cadastrarUsuarioCasoDeUso(
            UsuarioServico usuarioServico,
            GeradorId geradorId) {
        return new CadastrarUsuarioCasoDeUso(usuarioServico, geradorId);
    }

    @Bean
    public ApplicationRunner adminInicializador(
            UsuarioRepositorio usuarioRepositorio,
            UsuarioServico usuarioServico) {
        return args -> {
            Email email = new Email(ADMIN_EMAIL);
            usuarioRepositorio.removerPorEmail(email);

            Usuario admin = new Usuario(
                    new UsuarioId(2),
                    "Administrador",
                    email,
                    new Senha(ADMIN_SENHA),
                    PapelUsuario.ADMIN);

            usuarioServico.salvar(admin);
        };
    }
}
