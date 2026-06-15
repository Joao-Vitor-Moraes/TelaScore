package com.requisitos.avaliacaofilmes.TelaScore.infraestrutura.identidade.usuario;

import com.requisitos.avaliacaofilmes.TelaScore.dominio.identidade.usuario.Apelido;
import com.requisitos.avaliacaofilmes.TelaScore.dominio.identidade.usuario.Email;
import com.requisitos.avaliacaofilmes.TelaScore.dominio.identidade.usuario.PapelUsuario;
import com.requisitos.avaliacaofilmes.TelaScore.dominio.identidade.usuario.Senha;
import com.requisitos.avaliacaofilmes.TelaScore.dominio.identidade.usuario.Usuario;
import com.requisitos.avaliacaofilmes.TelaScore.dominio.identidade.usuario.UsuarioId;
import com.requisitos.avaliacaofilmes.TelaScore.dominio.identidade.usuario.UsuarioRepositorio;
import com.requisitos.avaliacaofilmes.TelaScore.infraestrutura.config.ConexaoBanco;
import jakarta.persistence.EntityManager;
import jakarta.persistence.EntityTransaction;
import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

public class UsuarioRepositorioImpl implements UsuarioRepositorio {

    @Override
    public void salvar(Usuario usuario) {
        EntityManager em = ConexaoBanco.obterEntityManager();
        EntityTransaction tx = em.getTransaction();

        try {
            tx.begin();

            em.createNativeQuery("""
                    INSERT INTO usuario (
                        id, nome, email, senha, papel_usuario, data_cadastro,
                        apelido, biografia, avatar_url
                    )
                    VALUES (
                        :id, :nome, :email, :senha, :papel, :dataCadastro,
                        :apelido, :biografia, :avatarUrl
                    )
                    ON DUPLICATE KEY UPDATE
                        nome = VALUES(nome),
                        email = VALUES(email),
                        senha = VALUES(senha),
                        papel_usuario = VALUES(papel_usuario),
                        apelido = VALUES(apelido),
                        biografia = VALUES(biografia),
                        avatar_url = VALUES(avatar_url)
                    """)
                    .setParameter("id", usuario.getId().getId())
                    .setParameter("nome", usuario.getNome())
                    .setParameter("email", usuario.getEmail().getEndereco())
                    .setParameter("senha", usuario.getSenha().getValor())
                    .setParameter("papel", usuario.getPapel().name())
                    .setParameter("dataCadastro", LocalDateTime.now())
                    .setParameter("apelido", usuario.getApelido().getValor())
                    .setParameter("biografia", usuario.getBiografia())
                    .setParameter("avatarUrl", usuario.getAvatarUrl())
                    .executeUpdate();

            tx.commit();
        } catch (Exception e) {
            if (tx.isActive()) {
                tx.rollback();
            }
            throw new RuntimeException("Erro ao salvar usuario no banco de dados.", e);
        } finally {
            em.close();
        }
    }

    @Override
    public Usuario obter(UsuarioId id) {
        EntityManager em = ConexaoBanco.obterEntityManager();
        try {
            List<?> resultados = em.createNativeQuery("""
                    SELECT id, nome, email, senha, papel_usuario, apelido, biografia, avatar_url
                    FROM usuario
                    WHERE id = :id
                    """)
                    .setParameter("id", id.getId())
                    .getResultList();

            return resultados.isEmpty() ? null : mapearLinhaParaDominio((Object[]) resultados.get(0));
        } finally {
            em.close();
        }
    }

    @Override
    public Usuario obterPorEmail(Email email) {
        EntityManager em = ConexaoBanco.obterEntityManager();
        try {
            List<?> resultados = em.createNativeQuery("""
                    SELECT id, nome, email, senha, papel_usuario, apelido, biografia, avatar_url
                    FROM usuario
                    WHERE LOWER(email) = LOWER(:email)
                    LIMIT 1
                    """)
                    .setParameter("email", email.getEndereco())
                    .getResultList();

            return resultados.isEmpty() ? null : mapearLinhaParaDominio((Object[]) resultados.get(0));
        } finally {
            em.close();
        }
    }

    @Override
    public void remover(UsuarioId id) {
        EntityManager em = ConexaoBanco.obterEntityManager();
        EntityTransaction tx = em.getTransaction();

        try {
            tx.begin();
            em.createNativeQuery("DELETE FROM usuario WHERE id = :id")
                    .setParameter("id", id.getId())
                    .executeUpdate();
            tx.commit();
        } catch (Exception e) {
            if (tx.isActive()) {
                tx.rollback();
            }
            throw new RuntimeException("Erro ao remover usuario do banco de dados.", e);
        } finally {
            em.close();
        }
    }

    @Override
    public void removerPorEmail(Email email) {
        EntityManager em = ConexaoBanco.obterEntityManager();
        EntityTransaction tx = em.getTransaction();

        try {
            tx.begin();
            em.createNativeQuery("DELETE FROM usuario WHERE LOWER(email) = LOWER(:email)")
                    .setParameter("email", email.getEndereco())
                    .executeUpdate();
            tx.commit();
        } catch (Exception e) {
            if (tx.isActive()) {
                tx.rollback();
            }
            throw new RuntimeException("Erro ao remover usuario por e-mail do banco de dados.", e);
        } finally {
            em.close();
        }
    }

    @Override
    public List<Usuario> listarTodos() {
        EntityManager em = ConexaoBanco.obterEntityManager();
        try {
            List<?> resultados = em.createNativeQuery("""
                    SELECT id, nome, email, senha, papel_usuario, apelido, biografia, avatar_url
                    FROM usuario
                    ORDER BY id
                    """)
                    .getResultList();

            return resultados.stream()
                    .map(resultado -> mapearLinhaParaDominio((Object[]) resultado))
                    .collect(Collectors.toList());
        } finally {
            em.close();
        }
    }

    private Usuario mapearLinhaParaDominio(Object[] linha) {
        Integer id = ((Number) linha[0]).intValue();
        String nome = (String) linha[1];
        String enderecoEmail = (String) linha[2];
        String valorSenha = (String) linha[3];
        String papelUsuario = (String) linha[4];
        String apelido = (String) linha[5];
        String biografia = (String) linha[6];
        String avatarUrl = (String) linha[7];

        Email email = new Email(enderecoEmail);
        Senha senha = new Senha(valorSenha);
        PapelUsuario papel = PapelUsuario.valueOf(papelUsuario);

        String nomeDominio = valorOuPadrao(nome, enderecoEmail);
        Apelido apelidoDominio = new Apelido(apelidoOuPadrao(apelido, nomeDominio, enderecoEmail));

        return new Usuario(
                new UsuarioId(id),
                nomeDominio,
                email,
                senha,
                papel,
                apelidoDominio,
                biografia,
                avatarUrl);
    }

    private String valorOuPadrao(String valor, String padrao) {
        return valor == null || valor.isBlank() ? padrao : valor;
    }

    private String apelidoOuPadrao(String apelido, String nome, String email) {
        if (apelido != null && !apelido.isBlank()) {
            return limitarApelido(apelido);
        }

        String base = valorOuPadrao(nome, email);
        int arroba = base.indexOf('@');
        if (arroba > 0) {
            base = base.substring(0, arroba);
        }

        return limitarApelido(base);
    }

    private String limitarApelido(String valor) {
        String apelido = valor == null ? "usuario" : valor.trim();
        if (apelido.isBlank()) {
            apelido = "usuario";
        }
        if (apelido.length() < 3) {
            apelido = (apelido + "usr").substring(0, 3);
        }
        return apelido.length() > 20 ? apelido.substring(0, 20) : apelido;
    }

}
