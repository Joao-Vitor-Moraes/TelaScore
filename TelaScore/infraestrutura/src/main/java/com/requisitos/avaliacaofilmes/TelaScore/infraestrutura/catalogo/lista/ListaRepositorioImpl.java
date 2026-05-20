package com.requisitos.avaliacaofilmes.TelaScore.infraestrutura.catalogo.lista;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

import com.requisitos.avaliacaofilmes.TelaScore.dominio.catalogo.filme.FilmeId;
import com.requisitos.avaliacaofilmes.TelaScore.dominio.catalogo.lista.ItemLista;
import com.requisitos.avaliacaofilmes.TelaScore.dominio.catalogo.lista.Lista;
import com.requisitos.avaliacaofilmes.TelaScore.dominio.catalogo.lista.ListaId;
import com.requisitos.avaliacaofilmes.TelaScore.dominio.catalogo.lista.ListaRepositorio;
import com.requisitos.avaliacaofilmes.TelaScore.dominio.catalogo.lista.TipoLista;
import com.requisitos.avaliacaofilmes.TelaScore.dominio.catalogo.lista.Visibilidade;
import com.requisitos.avaliacaofilmes.TelaScore.dominio.identidade.usuario.UsuarioId;
import com.requisitos.avaliacaofilmes.TelaScore.infraestrutura.catalogo.lista.entidades.ItemListaEntity;
import com.requisitos.avaliacaofilmes.TelaScore.infraestrutura.catalogo.lista.entidades.ListaEntity;
import com.requisitos.avaliacaofilmes.TelaScore.infraestrutura.config.ConexaoBanco;

import jakarta.persistence.EntityManager;
import jakarta.persistence.EntityTransaction;

public class ListaRepositorioImpl implements ListaRepositorio {

    @Override
    public void salvar(Lista lista) {
        EntityManager em = ConexaoBanco.obterEntityManager();
        EntityTransaction tx = em.getTransaction();
        
        try {
            tx.begin();
            
            ListaEntity entity = em.find(ListaEntity.class, lista.getId().getId());
            if (entity == null) {
                entity = new ListaEntity();
                entity.setId(lista.getId().getId());
                entity.setDonoId(lista.getDonoId().getId());
            }
            
            entity.setTitulo(lista.getTitulo());
            entity.setDescricao(lista.getDescricao());
            entity.setRanqueada(lista.isRanqueada());
            entity.setVisibilidade(lista.getVisibilidade().name());
            entity.setTipo(lista.getTipo().name());
            entity.setColaborativa(lista.isColaborativa());
            
            entity.getColaboradores().clear();
            entity.getColaboradores().addAll(lista.getColaboradores().stream()
                    .map(UsuarioId::getId).collect(Collectors.toList()));
            
            entity.getItens().clear();
            int posicaoAtual = 1;
            for (ItemLista item : lista.getItens()) {
                ItemListaEntity itemEntity = new ItemListaEntity();
                itemEntity.setLista(entity);
                itemEntity.setFilmeId(item.getFilmeId().getCodigo());
                itemEntity.setDataAdicao(item.getDataAdicao());
                if (lista.isRanqueada()) {
                    itemEntity.setPosicao(posicaoAtual++);
                }
                entity.getItens().add(itemEntity);
            }
            
            if (!em.contains(entity)) {
                em.merge(entity);
            } else {
                em.persist(entity);
            }
            
            tx.commit();
        } catch (Exception e) {
            if (tx.isActive()) {
                tx.rollback();
            }
            throw new RuntimeException("Erro ao salvar a Lista no banco de dados.", e);
        } finally {
            em.close();
        }
    }

    @Override
    public Lista obter(ListaId id) {
        EntityManager em = ConexaoBanco.obterEntityManager();
        try {
            ListaEntity entity = em.find(ListaEntity.class, id.getId());
            if (entity != null) {
                return mapearParaDominio(entity);
            }
            return null;
        } finally {
            em.close();
        }
    }

    @Override
    public void remover(ListaId id) {
        EntityManager em = ConexaoBanco.obterEntityManager();
        EntityTransaction tx = em.getTransaction();
        
        try {
            tx.begin();
            ListaEntity entity = em.find(ListaEntity.class, id.getId());
            if (entity != null) {
                em.remove(entity);
            }
            tx.commit();
        } catch (Exception e) {
            if (tx.isActive()) {
                tx.rollback();
            }
            throw new RuntimeException("Erro ao remover lista", e);
        } finally {
            em.close();
        }
    }

    @Override
    public List<Lista> pesquisarPorDono(UsuarioId donoId) {
        EntityManager em = ConexaoBanco.obterEntityManager();
        try {
            List<ListaEntity> entities = em.createQuery("SELECT l FROM ListaEntity l WHERE l.donoId = :donoId", ListaEntity.class)
                    .setParameter("donoId", donoId.getId())
                    .getResultList();
            
            List<Lista> listas = new ArrayList<>();
            for (ListaEntity entity : entities) {
                listas.add(mapearParaDominio(entity));
            }
            return listas;
        } finally {
            em.close();
        }
    }

    private Lista mapearParaDominio(ListaEntity entity) {
        ListaId listaId = new ListaId(entity.getId());
        UsuarioId donoId = new UsuarioId(entity.getDonoId());
        Visibilidade visibilidade = Visibilidade.valueOf(entity.getVisibilidade());
        TipoLista tipo = TipoLista.valueOf(entity.getTipo());
        
        List<UsuarioId> colaboradores = entity.getColaboradores().stream()
                .map(UsuarioId::new).collect(Collectors.toList());
                
        List<ItemLista> itens = entity.getItens().stream()
                .map(i -> ItemLista.restaurar(new FilmeId(i.getFilmeId()), i.getPosicao(), i.getDataAdicao()))
                .collect(Collectors.toList());

        return Lista.restaurar(listaId, donoId, entity.getTitulo(), entity.getDescricao(), 
                entity.isRanqueada(), visibilidade, tipo, entity.isColaborativa(), colaboradores, itens);
    }
}
