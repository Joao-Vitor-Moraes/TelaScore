package com.requisitos.avaliacaofilmes.TelaScore.dominio.catalogo;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

import com.requisitos.avaliacaofilmes.TelaScore.dominio.catalogo.lista.Lista;
import com.requisitos.avaliacaofilmes.TelaScore.dominio.catalogo.lista.ListaId;
import com.requisitos.avaliacaofilmes.TelaScore.dominio.catalogo.lista.TipoLista;
import com.requisitos.avaliacaofilmes.TelaScore.dominio.catalogo.lista.Visibilidade;
import com.requisitos.avaliacaofilmes.TelaScore.dominio.identidade.usuario.UsuarioId;

import io.cucumber.java.pt.Dado;
import io.cucumber.java.pt.E;
import io.cucumber.java.pt.Então;
import io.cucumber.java.pt.Quando;

public class ListaSteps {

    private UsuarioId donoId;
    private ListaId listaId;
    private Lista listaCriada;
    private Exception excecaoCapturada;

    @Dado("que o usuário {string} com ID {int} quer criar uma lista")
    public void que_o_usuario_com_id_quer_criar_uma_lista(String nomeUsuario, Integer idUsuario) {
        donoId = new UsuarioId(idUsuario);
        listaId = new ListaId(100); 
        listaCriada = null;
        excecaoCapturada = null;
    }

    @Quando("ela cria uma lista chamada {string}")
    public void ela_cria_uma_lista_chamada(String titulo) {
        try {
            listaCriada = new Lista(listaId, donoId, titulo, "Meus filmes preferidos", false, Visibilidade.PUBLICA, TipoLista.NORMAL);
        } catch (Exception e) {
            excecaoCapturada = e;
        }
    }

    @Então("a lista é criada com sucesso")
    public void a_lista_é_criada_com_sucesso() {
        assertEquals(null, excecaoCapturada, "Não deveria dar erro ao criar uma lista válida");
        assertNotNull(listaCriada, "A entidade Lista deveria ter sido criada");
    }

    @E("pertence ao usuário {int}")
    public void pertence_ao_usuario(Integer idDonoEsperado) {
        assertEquals(idDonoEsperado, listaCriada.getDonoId().getId());
    }

    @Quando("ela tenta criar uma lista sem nome")
    public void ela_tenta_criar_uma_lista_sem_nome() {
        try {
            listaCriada = new Lista(listaId, donoId, "", "Lista sem nome não pode", false, Visibilidade.PUBLICA, TipoLista.NORMAL);
        } catch (Exception e) {
            excecaoCapturada = e;
        }
    }

    @Então("o sistema rejeita a operação")
    public void o_sistema_rejeita_a_operacao() {
        assertNotNull(excecaoCapturada, "Uma exceção deveria ter sido lançada pela classe Lista");
    }

    @E("retorna o erro {string}")
    public void retorna_o_erro(String mensagemEsperada) {
        assertEquals(mensagemEsperada, excecaoCapturada.getMessage());
    }
    
    @Dado("que a usuária {string} com ID {int} tem uma lista criada")
    public void que_a_usuaria_com_id_tem_uma_lista_criada(String nomeUsuario, Integer idUsuario) {
        donoId = new UsuarioId(idUsuario);
        listaId = new ListaId(100); 
        listaCriada = new Lista(listaId, donoId, "Lista de Teste", "Descrição", false, Visibilidade.PUBLICA, TipoLista.NORMAL);
        excecaoCapturada = null;
    }

    @Quando("ela adiciona o filme com ID {int} à lista")
    public void ela_adiciona_o_filme_com_id_a_lista(Integer idFilme) {
        try {
            com.requisitos.avaliacaofilmes.TelaScore.dominio.catalogo.filme.FilmeId filmeId = 
                new com.requisitos.avaliacaofilmes.TelaScore.dominio.catalogo.filme.FilmeId(String.valueOf(idFilme));
            com.requisitos.avaliacaofilmes.TelaScore.dominio.catalogo.lista.ItemLista novoItem = 
                new com.requisitos.avaliacaofilmes.TelaScore.dominio.catalogo.lista.ItemLista(filmeId, null);
            
            listaCriada.adicionarItem(novoItem, donoId, true);
        } catch (Exception e) {
            excecaoCapturada = e;
        }
    }

    @Então("a lista deve conter {int} item")
    public void a_lista_deve_conter_item(Integer quantidadeEsperada) {
        assertEquals(null, excecaoCapturada);
        assertEquals(quantidadeEsperada, listaCriada.getItens().size(), "A quantidade de itens na lista está incorreta");
    }

    @E("o filme com ID {int} já está na lista")
    public void o_filme_com_id_ja_esta_na_lista(Integer idFilme) {
        com.requisitos.avaliacaofilmes.TelaScore.dominio.catalogo.filme.FilmeId filmeId = 
            new com.requisitos.avaliacaofilmes.TelaScore.dominio.catalogo.filme.FilmeId(String.valueOf(idFilme));
        com.requisitos.avaliacaofilmes.TelaScore.dominio.catalogo.lista.ItemLista itemExistente = 
            new com.requisitos.avaliacaofilmes.TelaScore.dominio.catalogo.lista.ItemLista(filmeId, null);
        
        listaCriada.adicionarItem(itemExistente, donoId, true);
    }

    @Quando("ela tenta adicionar o filme com ID {int} novamente")
    public void ela_tenta_adicionar_o_filme_com_id_novamente(Integer idFilme) {
        ela_adiciona_o_filme_com_id_a_lista(idFilme);
    }

    @Dado("que a usuária {string} com ID {int} tem uma lista ranqueada criada")
    public void que_a_usuaria_com_id_tem_uma_lista_ranqueada_criada(String nomeUsuario, Integer idUsuario) {
        donoId = new UsuarioId(idUsuario);
        listaId = new ListaId(100); 
        listaCriada = new Lista(listaId, donoId, "Meu Top Filmes", "Descrição", true, Visibilidade.PUBLICA, TipoLista.NORMAL);
        excecaoCapturada = null;
    }

    @Dado("ela adicionou o filme com ID {int} na lista")
    public void ela_adicionou_o_filme_com_id_na_lista(Integer idFilme) {
        com.requisitos.avaliacaofilmes.TelaScore.dominio.catalogo.filme.FilmeId filmeId = 
            new com.requisitos.avaliacaofilmes.TelaScore.dominio.catalogo.filme.FilmeId(String.valueOf(idFilme));
        com.requisitos.avaliacaofilmes.TelaScore.dominio.catalogo.lista.ItemLista novoItem = 
            new com.requisitos.avaliacaofilmes.TelaScore.dominio.catalogo.lista.ItemLista(filmeId, null);
        
        listaCriada.adicionarItem(novoItem, donoId, true);
    }

    @Quando("ela move o filme com ID {int} para a posição {int}")
    public void ela_move_o_filme_com_id_para_a_posicao(Integer idFilme, Integer novaPosicao) {
        try {
            com.requisitos.avaliacaofilmes.TelaScore.dominio.catalogo.filme.FilmeId filmeId = 
                new com.requisitos.avaliacaofilmes.TelaScore.dominio.catalogo.filme.FilmeId(String.valueOf(idFilme));
            listaCriada.moverFilmeParaPosicao(filmeId, novaPosicao, donoId);
        } catch (Exception e) {
            excecaoCapturada = e;
        }
    }

    @Quando("ela tenta mover o filme com ID {int} para a posição {int}")
    public void ela_tenta_mover_o_filme_com_id_para_a_posicao(Integer idFilme, Integer novaPosicao) {
        ela_move_o_filme_com_id_para_a_posicao(idFilme, novaPosicao);
    }

    @Então("o filme com ID {int} deve estar na posição {int}")
    public void o_filme_com_id_deve_estar_na_posicao(Integer idFilmeEsperado, Integer posicao) {
        assertEquals(null, excecaoCapturada, "Não deveria dar erro ao mover o filme");
        com.requisitos.avaliacaofilmes.TelaScore.dominio.catalogo.lista.ItemLista itemNaPosicao = 
            listaCriada.getItens().get(posicao - 1);
        assertEquals(String.valueOf(idFilmeEsperado), itemNaPosicao.getFilmeId().getCodigo());
    }


    @Dado("que a usuária {string} tornou a lista colaborativa")
    public void que_a_usuaria_tornou_a_lista_colaborativa(String nomeUsuario) {
        listaCriada.tornarColaborativa();
    }

    @Dado("adicionou o usuário com ID {int} como colaborador")
    public void adicionou_o_usuario_com_id_como_colaborador(Integer idColaborador) {
        listaCriada.adicionarColaborador(donoId, new UsuarioId(idColaborador));
    }

    @Quando("o usuário com ID {int} adiciona o filme com ID {int} à lista")
    public void o_usuario_com_id_adiciona_o_filme_com_id_a_lista(Integer idUsuario, Integer idFilme) {
        try {
            com.requisitos.avaliacaofilmes.TelaScore.dominio.catalogo.filme.FilmeId filmeId = 
                new com.requisitos.avaliacaofilmes.TelaScore.dominio.catalogo.filme.FilmeId(String.valueOf(idFilme));
            com.requisitos.avaliacaofilmes.TelaScore.dominio.catalogo.lista.ItemLista novoItem = 
                new com.requisitos.avaliacaofilmes.TelaScore.dominio.catalogo.lista.ItemLista(filmeId, null);
            listaCriada.adicionarItem(novoItem, new UsuarioId(idUsuario), true);
        } catch (Exception e) {
            excecaoCapturada = e;
        }
    }

    @Quando("ela tenta adicionar o filme com ID {int} à lista informando que não assistiu")
    public void ela_tenta_adicionar_o_filme_com_id_a_lista_informando_que_nao_assistiu(Integer idFilme) {
        try {
            com.requisitos.avaliacaofilmes.TelaScore.dominio.catalogo.filme.FilmeId filmeId = 
                new com.requisitos.avaliacaofilmes.TelaScore.dominio.catalogo.filme.FilmeId(String.valueOf(idFilme));
            com.requisitos.avaliacaofilmes.TelaScore.dominio.catalogo.lista.ItemLista novoItem = 
                new com.requisitos.avaliacaofilmes.TelaScore.dominio.catalogo.lista.ItemLista(filmeId, null);
            listaCriada.adicionarItem(novoItem, donoId, false);
        } catch (Exception e) {
            excecaoCapturada = e;
        }
    }

    @Dado("que a usuária {string} com ID {int} tem uma watchlist criada")
    public void que_a_usuaria_com_id_tem_uma_watchlist_criada(String nomeUsuario, Integer idUsuario) {
        donoId = new UsuarioId(idUsuario);
        listaId = new ListaId(100); 
        listaCriada = new Lista(listaId, donoId, "Minha Watchlist", "Para ver depois", false, Visibilidade.PRIVADA, TipoLista.WATCHLIST);
        excecaoCapturada = null;
    }

    @Dado("ela adicionou o filme com ID {int} à watchlist")
    public void ela_adicionou_o_filme_com_id_a_watchlist(Integer idFilme) {
        com.requisitos.avaliacaofilmes.TelaScore.dominio.catalogo.filme.FilmeId filmeId = 
            new com.requisitos.avaliacaofilmes.TelaScore.dominio.catalogo.filme.FilmeId(String.valueOf(idFilme));
        com.requisitos.avaliacaofilmes.TelaScore.dominio.catalogo.lista.ItemLista novoItem = 
            new com.requisitos.avaliacaofilmes.TelaScore.dominio.catalogo.lista.ItemLista(filmeId, null);
        listaCriada.adicionarItem(novoItem, donoId, false);
    }

    @Quando("ela registra que assistiu ao filme com ID {int}")
    public void ela_registra_que_assistiu_ao_filme_com_id(Integer idFilme) {
        com.requisitos.avaliacaofilmes.TelaScore.dominio.catalogo.filme.FilmeId filmeId = 
            new com.requisitos.avaliacaofilmes.TelaScore.dominio.catalogo.filme.FilmeId(String.valueOf(idFilme));
        listaCriada.processarFilmeAssistido(filmeId, donoId);
    }
}