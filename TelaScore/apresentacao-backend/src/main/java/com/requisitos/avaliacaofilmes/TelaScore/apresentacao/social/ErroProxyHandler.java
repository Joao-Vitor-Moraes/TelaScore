package com.requisitos.avaliacaofilmes.TelaScore.apresentacao.social;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

@RestControllerAdvice
public class ErroProxyHandler {

    @ExceptionHandler(SecurityException.class)
    public ResponseEntity<String> tratarErroDeLimite(SecurityException ex) {
        return ResponseEntity
                .status(HttpStatus.BAD_REQUEST)
                .body(ex.getMessage());
    }
}