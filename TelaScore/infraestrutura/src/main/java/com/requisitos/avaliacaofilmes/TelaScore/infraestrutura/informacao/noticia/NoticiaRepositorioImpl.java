package com.requisitos.avaliacaofilmes.TelaScore.infraestrutura.informacao.noticia;

import com.requisitos.avaliacaofilmes.TelaScore.dominio.identidade.usuario.UsuarioId;
import com.requisitos.avaliacaofilmes.TelaScore.dominio.informacao.noticia.CategoriaNoticia;
import com.requisitos.avaliacaofilmes.TelaScore.dominio.informacao.noticia.Noticia;
import com.requisitos.avaliacaofilmes.TelaScore.dominio.informacao.noticia.NoticiaId;
import com.requisitos.avaliacaofilmes.TelaScore.dominio.informacao.noticia.NoticiaRepositorio;
import com.requisitos.avaliacaofilmes.TelaScore.infraestrutura.informacao.noticia.entidades.NoticiaEntity;
import com.requisitos.avaliacaofilmes.TelaScore.infraestrutura.identidade.usuario.entidades.UsuarioEntity;
import com.requisitos.avaliacaofilmes.TelaScore.infraestrutura.catalogo.filme.entidades.FilmeEntity;
import com.requisitos.avaliacaofilmes.TelaScore.infraestrutura.config.ConexaoBanco;
import jakarta.persistence.EntityManager;
import jakarta.persistence.TypedQuery;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

public class NoticiaRepositorioImpl implements NoticiaRepositorio {

    private EntityManager obterEntityManager() {
        return ConexaoBanco.obterEntityManager();
    }

    @Override
    public void salvar(Noticia noticia) {
        EntityManager em = obterEntityManager();
        try {
            em.getTransaction().begin();
            NoticiaEntity entity = em.find(NoticiaEntity.class, noticia.getId().getId());
            if (entity == null) {
                entity = new NoticiaEntity();
                entity.setId(noticia.getId().getId());
            }
            entity.setTitulo(noticia.getTitulo());
            entity.setConteudo(noticia.getConteudo());
            entity.setDataPublicacao(noticia.getDataPublicacao());
            entity.setCategoria(noticia.getCategoria().name());

            UsuarioEntity autorRef = em.getReference(UsuarioEntity.class, noticia.getAutorId().getId());
            entity.setAutor(autorRef);
            entity.setFilme(noticia.getFilmeId() == null ? null : em.getReference(FilmeEntity.class, noticia.getFilmeId()));

            em.merge(entity);
            em.getTransaction().commit();
        } finally {
            em.close();
        }
    }

    @Override
    public Noticia obter(NoticiaId id) {
        EntityManager em = obterEntityManager();
        try {
            List<NoticiaEntity> resultados = em.createQuery(
                            "SELECT n FROM NoticiaEntity n LEFT JOIN FETCH n.autor LEFT JOIN FETCH n.filme WHERE n.id = :id", NoticiaEntity.class)
                    .setParameter("id", id.getId())
                    .getResultList();

            if (resultados.isEmpty()) return null;
            NoticiaEntity entity = resultados.get(0);

            int autorId = entity.getAutor() != null ? entity.getAutor().getId() : 0;
            String apelido = entity.getAutor() != null ? entity.getAutor().getApelido() : "Desconhecido";

            return mapearDominio(entity, autorId, apelido);
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        } finally {
            em.close();
        }
    }

    @Override
    public void remover(NoticiaId id) {
        EntityManager em = obterEntityManager();
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
        EntityManager em = obterEntityManager();
        try {
            List<NoticiaEntity> entidades = em.createQuery(
                            "SELECT n FROM NoticiaEntity n LEFT JOIN FETCH n.autor LEFT JOIN FETCH n.filme ORDER BY n.dataPublicacao DESC", NoticiaEntity.class)
                    .setMaxResults(limite)
                    .getResultList();
            return mapearLista(entidades);
        } catch (Exception e) {
            e.printStackTrace();
            return new ArrayList<>();
        } finally {
            em.close();
        }
    }

    @Override
    public List<Noticia> buscarPorAutor(UsuarioId autorId) {
        EntityManager em = obterEntityManager();
        try {
            List<NoticiaEntity> entidades = em.createQuery(
                            "SELECT n FROM NoticiaEntity n LEFT JOIN FETCH n.autor LEFT JOIN FETCH n.filme WHERE n.autor.id = :autorId ORDER BY n.dataPublicacao DESC", NoticiaEntity.class)
                    .setParameter("autorId", autorId.getId())
                    .getResultList();
            return mapearLista(entidades);
        } catch (Exception e) {
            e.printStackTrace();
            return new ArrayList<>();
        } finally {
            em.close();
        }
    }

    @Override
    public List<Noticia> buscarPorFiltros(String termo, CategoriaNoticia categoria) {
        EntityManager em = obterEntityManager();
        try {
            StringBuilder jpql = new StringBuilder("SELECT n FROM NoticiaEntity n LEFT JOIN FETCH n.autor LEFT JOIN FETCH n.filme WHERE 1=1");
            if (termo != null && !termo.trim().isEmpty()) {
                jpql.append(" AND (n.titulo LIKE :termo OR n.conteudo LIKE :termo)");
            }
            if (categoria != null) {
                jpql.append(" AND (UPPER(n.categoria) = :catName OR UPPER(n.categoria) = :catDesc)");
            }
            jpql.append(" ORDER BY n.dataPublicacao DESC");

            TypedQuery<NoticiaEntity> query = em.createQuery(jpql.toString(), NoticiaEntity.class);
            if (termo != null && !termo.trim().isEmpty()) {
                query.setParameter("termo", "%" + termo.trim() + "%");
            }
            if (categoria != null) {
                query.setParameter("catName", categoria.name().toUpperCase());
                query.setParameter("catDesc", categoria.getDescricao().toUpperCase());
            }

            return mapearLista(query.getResultList());
        } catch (Exception e) {
            e.printStackTrace();
            return new ArrayList<>();
        } finally {
            em.close();
        }
    }

    private List<Noticia> mapearLista(List<NoticiaEntity> entidades) {
        List<Noticia> listaDominio = new ArrayList<>();
        for (NoticiaEntity entity : entidades) {
            try {
                int autorId = entity.getAutor() != null ? entity.getAutor().getId() : 0;
                String apelido = entity.getAutor() != null ? entity.getAutor().getApelido() : "Desconhecido";

                listaDominio.add(mapearDominio(entity, autorId, apelido));
            } catch (Exception e) {
                System.err.println(e.getMessage());
            }
        }
        return listaDominio;
    }

    private Noticia mapearDominio(NoticiaEntity entity, int autorId, String apelido) {
        FilmeEntity filme = entity.getFilme();
        return new Noticia(
                new NoticiaId(entity.getId()),
                new UsuarioId(autorId),
                apelido,
                entity.getTitulo(),
                entity.getConteudo(),
                entity.getDataPublicacao(),
                mapearCategoriaSafe(entity.getCategoria()),
                filme != null ? filme.getId() : null,
                filme != null ? filme.getTitulo() : null,
                filme != null ? filme.getImagemUrl() : null
        );
    }

    private CategoriaNoticia mapearCategoriaSafe(String valor) {
        if (valor == null) return CategoriaNoticia.LANCAMENTO;
        String valorLimpo = valor.trim().toUpperCase();
        try {
            return CategoriaNoticia.valueOf(valorLimpo);
        } catch (IllegalArgumentException e) {
            for (CategoriaNoticia cat : CategoriaNoticia.values()) {
                if (cat.getDescricao().equalsIgnoreCase(valorLimpo) ||
                        cat.name().equalsIgnoreCase(valorLimpo) ||
                        cat.getDescricao().toUpperCase().equalsIgnoreCase(valorLimpo)) {
                    return cat;
                }
            }
            return CategoriaNoticia.LANCAMENTO;
        }
    }
}
