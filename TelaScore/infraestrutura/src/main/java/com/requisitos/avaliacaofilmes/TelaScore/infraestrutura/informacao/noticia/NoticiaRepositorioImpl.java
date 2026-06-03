package com.requisitos.avaliacaofilmes.TelaScore.infraestrutura.informacao.noticia;

import com.requisitos.avaliacaofilmes.TelaScore.dominio.identidade.usuario.UsuarioId;
import com.requisitos.avaliacaofilmes.TelaScore.dominio.informacao.noticia.CategoriaNoticia;
import com.requisitos.avaliacaofilmes.TelaScore.dominio.informacao.noticia.Noticia;
import com.requisitos.avaliacaofilmes.TelaScore.dominio.informacao.noticia.NoticiaId;
import com.requisitos.avaliacaofilmes.TelaScore.dominio.informacao.noticia.NoticiaRepositorio;
import com.requisitos.avaliacaofilmes.TelaScore.infraestrutura.informacao.noticia.entidades.NoticiaEntity;
import jakarta.persistence.EntityManager;
import jakarta.persistence.EntityManagerFactory;
import jakarta.persistence.Persistence;
import jakarta.persistence.TypedQuery;
import java.util.ArrayList;
import java.util.List;

public class NoticiaRepositorioImpl implements NoticiaRepositorio {

    private final EntityManagerFactory emf = Persistence.createEntityManagerFactory("telascorePU");

    @Override
    public void salvar(Noticia noticia) {
        EntityManager em = emf.createEntityManager();
        try {
            em.getTransaction().begin();
            NoticiaEntity entity = new NoticiaEntity();
            entity.setId(noticia.getId().getId());
            entity.setTitulo(noticia.getTitulo());
            entity.setConteudo(noticia.getConteudo());
            entity.setAutorId(noticia.getAutorId().getId());
            entity.setDataPublicacao(noticia.getDataPublicacao());
            entity.setCategoria(noticia.getCategoria().name());
            em.merge(entity);
            em.getTransaction().commit();
        } finally {
            em.close();
        }
    }

    @Override
    public Noticia obter(NoticiaId id) {
        EntityManager em = emf.createEntityManager();
        try {
            NoticiaEntity entity = em.find(NoticiaEntity.class, id.getId());
            if (entity == null) return null;

            return new Noticia(
                    new NoticiaId(entity.getId()),
                    new UsuarioId(entity.getAutorId()),
                    entity.getTitulo(),
                    entity.getConteudo(),
                    CategoriaNoticia.valueOf(entity.getCategoria())
            );
        } finally {
            em.close();
        }
    }

    @Override
    public void remover(NoticiaId id) {
        EntityManager em = emf.createEntityManager();
        try {
            em.getTransaction().begin();
            NoticiaEntity entity = em.find(NoticiaEntity.class, id.getId());
            if (entity != null) {
                em.remove(entity);
            }
            em.getTransaction().commit();
        } finally {
            em.close();
        }
    }

    @Override
    public List<Noticia> buscarRecentes(int limite) {
        EntityManager em = emf.createEntityManager();
        try {
            List<NoticiaEntity> entidades = em.createQuery(
                            "FROM NoticiaEntity ORDER BY dataPublicacao DESC", NoticiaEntity.class)
                    .setMaxResults(limite)
                    .getResultList();
            return mapearLista(entidades);
        } finally {
            em.close();
        }
    }

    @Override
    public List<Noticia> buscarPorAutor(UsuarioId autorId) {
        EntityManager em = emf.createEntityManager();
        try {
            List<NoticiaEntity> entidades = em.createQuery(
                            "FROM NoticiaEntity WHERE autorId = :autorId", NoticiaEntity.class)
                    .setParameter("autorId", autorId.getId())
                    .getResultList();
            return mapearLista(entidades);
        } finally {
            em.close();
        }
    }

    @Override
    public List<Noticia> buscarPorFiltros(String termo, CategoriaNoticia categoria) {
        EntityManager em = emf.createEntityManager();
        try {
            StringBuilder jpql = new StringBuilder("FROM NoticiaEntity WHERE 1=1");
            if (termo != null && !termo.trim().isEmpty()) {
                jpql.append(" AND (titulo LIKE :termo OR conteudo LIKE :termo)");
            }
            if (categoria != null) {
                jpql.append(" AND categoria = :categoria");
            }

            TypedQuery<NoticiaEntity> query = em.createQuery(jpql.toString(), NoticiaEntity.class);
            if (termo != null && !termo.trim().isEmpty()) {
                query.setParameter("termo", "%" + termo + "%");
            }
            if (categoria != null) {
                query.setParameter("categoria", categoria.name());
            }

            return mapearLista(query.getResultList());
        } finally {
            em.close();
        }
    }

    private List<Noticia> mapearLista(List<NoticiaEntity> entidades) {
        List<Noticia> listaDominio = new ArrayList<>();
        for (NoticiaEntity entity : entidades) {
            listaDominio.add(new Noticia(
                    new NoticiaId(entity.getId()),
                    new UsuarioId(entity.getAutorId()),
                    entity.getTitulo(),
                    entity.getConteudo(),
                    CategoriaNoticia.valueOf(entity.getCategoria())
            ));
        }
        return listaDominio;
    }
}