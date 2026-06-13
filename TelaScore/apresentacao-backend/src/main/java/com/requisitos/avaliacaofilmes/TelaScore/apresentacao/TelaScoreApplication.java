package com.requisitos.avaliacaofilmes.TelaScore.apresentacao;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication(scanBasePackages = {
        "com.requisitos.avaliacaofilmes.TelaScore.apresentacao",
        "com.requisitos.avaliacaofilmes.TelaScore.infraestrutura"
})
public class TelaScoreApplication {
    public static void main(String[] args) {
        org.springframework.boot.SpringApplication.run(TelaScoreApplication.class, args);
    }
}
