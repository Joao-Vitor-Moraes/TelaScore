package com.requisitos.avaliacaofilmes.TelaScore.apresentacao.seguranca;

import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.InterceptorRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

@Configuration
public class TokenMvcConfig implements WebMvcConfigurer {

    private final AutenticacaoTokenInterceptor autenticacaoTokenInterceptor;

    public TokenMvcConfig(AutenticacaoTokenInterceptor autenticacaoTokenInterceptor) {
        this.autenticacaoTokenInterceptor = autenticacaoTokenInterceptor;
    }

    @Override
    public void addInterceptors(InterceptorRegistry registry) {
        registry.addInterceptor(autenticacaoTokenInterceptor);
    }
}
