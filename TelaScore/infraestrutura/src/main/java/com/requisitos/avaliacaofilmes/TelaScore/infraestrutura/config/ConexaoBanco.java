package com.requisitos.avaliacaofilmes.TelaScore.infraestrutura.config;

import jakarta.persistence.EntityManager;
import jakarta.persistence.EntityManagerFactory;
import jakarta.persistence.Persistence;

import java.util.HashMap;
import java.util.Map;

public class ConexaoBanco {
    private static final EntityManagerFactory emf;

    static {
        try {
            Map<String, String> props = new HashMap<>();
            props.put("jakarta.persistence.jdbc.url",
                    env("DB_URL", "jdbc:mysql://localhost:3306/telascore_db?useSSL=false&allowPublicKeyRetrieval=true&serverTimezone=UTC"));
            props.put("jakarta.persistence.jdbc.user",
                    env("DB_USER", "root"));
            props.put("jakarta.persistence.jdbc.password",
                    env("DB_PASSWORD", "Barbosa09_"));
            emf = Persistence.createEntityManagerFactory("telascorePU", props);
        } catch (Throwable ex) {
            System.err.println("Initial EntityManagerFactory creation failed." + ex);
            throw new ExceptionInInitializerError(ex);
        }
    }

    private static String env(String nome, String padrao) {
        String valor = System.getenv(nome);
        return (valor != null && !valor.isBlank()) ? valor : padrao;
    }

    public static EntityManager obterEntityManager() {
        return emf.createEntityManager();
    }
}