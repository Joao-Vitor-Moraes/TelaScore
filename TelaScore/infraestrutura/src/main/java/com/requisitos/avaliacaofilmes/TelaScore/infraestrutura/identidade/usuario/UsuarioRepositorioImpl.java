package com.requisitos.avaliacaofilmes.TelaScore.infraestrutura.identidade.usuario;

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
                    INSERT INTO usuario (id, email, senha, papel_usuario, data_cadastro)
                    VALUES (:id, :email, :senha, :papel, :dataCadastro)
                    ON DUPLICATE KEY UPDATE
                        email = VALUES(email),
                        senha = VALUES(senha),
                        papel_usuario = VALUES(papel_usuario)
                    """)
                    .setParameter("id", usuario.getId().getId())
                    .setParameter("email", usuario.getEmail().getEndereco())
                    .setParameter("senha", usuario.getSenha().getValor())
                    .setParameter("papel", usuario.getPapel().name())
                    .setParameter("dataCadastro", LocalDateTime.now())
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
                    SELECT id, email, senha, papel_usuario
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
                    SELECT id, email, senha, papel_usuario
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
                    SELECT id, email, senha, papel_usuario
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
        String enderecoEmail = (String) linha[1];
        String valorSenha = (String) linha[2];
        String papelUsuario = (String) linha[3];

        Email email = new Email(enderecoEmail);
        Senha senha = new Senha(valorSenha);
        PapelUsuario papel = PapelUsuario.valueOf(papelUsuario);

        return new Usuario(new UsuarioId(id), enderecoEmail, email, senha, papel);
    }
}
