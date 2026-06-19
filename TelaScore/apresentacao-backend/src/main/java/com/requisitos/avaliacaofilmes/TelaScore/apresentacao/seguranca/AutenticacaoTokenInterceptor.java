package com.requisitos.avaliacaofilmes.TelaScore.apresentacao.seguranca;

import com.requisitos.avaliacaofilmes.TelaScore.aplicacao.identidade.SessaoUsuario;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

import org.springframework.stereotype.Component;
import org.springframework.web.servlet.HandlerInterceptor;

@Component
public class AutenticacaoTokenInterceptor implements HandlerInterceptor {

    private static final String PREFIXO_BEARER = "Bearer ";

    private final TokenServico tokenServico;
    private final SessaoUsuario sessaoUsuario;

    public AutenticacaoTokenInterceptor(TokenServico tokenServico, SessaoUsuario sessaoUsuario) {
        this.tokenServico = tokenServico;
        this.sessaoUsuario = sessaoUsuario;
    }

    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) {
        sessaoUsuario.encerrar();
        String token = extrairToken(request);
        tokenServico.validar(token).ifPresent(sessaoUsuario::iniciar);
        return true;
    }

    private String extrairToken(HttpServletRequest request) {
        String authorization = request.getHeader("Authorization");
        if (authorization == null || !authorization.startsWith(PREFIXO_BEARER)) {
            return null;
        }

        return authorization.substring(PREFIXO_BEARER.length()).trim();
    }
}
