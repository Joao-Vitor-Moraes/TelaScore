package com.requisitos.avaliacaofilmes.TelaScore.dominio.catalogo;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

import com.requisitos.avaliacaofilmes.TelaScore.dominio.catalogo.lista.Lista;
import com.requisitos.avaliacaofilmes.TelaScore.dominio.catalogo.lista.ListaId;
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
        // Inicializamos os IDs fakes e limpamos os estados
        donoId = new UsuarioId(idUsuario);
        listaId = new ListaId(100); 
        listaCriada = null;
        excecaoCapturada = null;
    }

    @Quando("ela cria uma lista chamada {string}")
    public void ela_cria_uma_lista_chamada(String titulo) {
        try {
            // Tentamos criar a lista instanciando diretamente o Domínio
            listaCriada = new Lista(listaId, donoId, titulo, "Meus filmes preferidos", false);
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
            // Enviamos uma String em branco ("") para acionar a barreira de segurança do Validate.notBlank
            listaCriada = new Lista(listaId, donoId, "", "Lista sem nome não pode", false);
        } catch (Exception e) {
            excecaoCapturada = e;
        }
    }

    @Então("o sistema rejeita a operação")
    public void o_sistema_rejeita_a_operacao() {
        assertNotNull(excecaoCapturada, "Uma exceção deveria ter sido lançada pela classe Lista");
        assertEquals(IllegalArgumentException.class, excecaoCapturada.getClass());
    }

    @E("retorna o erro {string}")
    public void retorna_o_erro(String mensagemEsperada) {
        assertEquals(mensagemEsperada, excecaoCapturada.getMessage());
    }
}