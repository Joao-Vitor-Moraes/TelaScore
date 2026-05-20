package com.requisitos.avaliacaofilmes.TelaScore.infraestrutura.config;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

public class ConexaoBanco {
    private static final String URL = "jdbc:mysql://localhost:3306/telascore_db?useSSL=false&serverTimezone=UTC";
    private static final String USER = "root";
    private static final String PASSWORD = "Barbosa09_";

    public static Connection obterConexao() throws SQLException {
        return DriverManager.getConnection(URL, USER, PASSWORD);
    }
}