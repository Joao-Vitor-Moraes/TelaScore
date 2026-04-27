package com.requisitos.avaliacaofilmes.TelaScore.dominio.social;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

import com.requisitos.avaliacaofilmes.TelaScore.dominio.identidade.usuario.UsuarioId;
import com.requisitos.avaliacaofilmes.TelaScore.dominio.social.comunidade.Comunidade;
import com.requisitos.avaliacaofilmes.TelaScore.dominio.social.comunidade.ComunidadeId;
import com.requisitos.avaliacaofilmes.TelaScore.dominio.social.comunidade.MembroComunidade;
import com.requisitos.avaliacaofilmes.TelaScore.dominio.social.comunidade.PapelComunidade;
import io.cucumber.java.pt.Dado;
import io.cucumber.java.pt.Então;
import io.cucumber.java.pt.Quando;

public class ComunidadeSteps {

    private Comunidade comunidade;
    private MembroComunidade membro;
    private Exception excecaoCapturada;
    private String nomeTemp;
    private String descTemp;
    private Integer idTemp;


    @Dado("que o usuário {int} é o {string} de uma comunidade")
    public void preparar_membro_criador(Integer id, String papel) {
        ComunidadeId cid = new ComunidadeId(1);
        membro = new MembroComunidade(cid, new UsuarioId(id), PapelComunidade.valueOf(papel.toUpperCase()));
    }

    @Quando("o sistema tenta rebaixar o papel do usuário {int} para {string}")
    public void tenta_rebaixar_papel(Integer id, String novoPapel) {
        try {
            membro.rebaixarAMembro();
        } catch (Exception e) {
            excecaoCapturada = e;
        }
    }

    @Então("uma exceção de estado inválido deve ser lançada")
    public void validar_excecao() {
        assertNotNull(excecaoCapturada, "Deveria ter lançado uma exceção");
        assertTrue(excecaoCapturada instanceof IllegalStateException);
    }

    @Então("o papel do usuário {int} deve permanecer como {string}")
    public void validar_papel_permanece(Integer id, String papel) {
        assertEquals(PapelComunidade.valueOf(papel.toUpperCase()), membro.getPapel());
    }

    @Dado("que o usuário {int} é um {string} comum")
    public void preparar_membro_comum(Integer id, String papel) {
        membro = new MembroComunidade(new ComunidadeId(1), new UsuarioId(id), PapelComunidade.valueOf(papel.toUpperCase()));
    }

    @Quando("o usuário {int} é promovido a moderador")
    public void promover_usuario(Integer id) {
        membro.promoverAModerador();
    }

    @Então("o papel atualizado deve ser {string}")
    public void validar_papel_atualizado(String papel) {
        assertEquals(PapelComunidade.valueOf(papel.toUpperCase()), membro.getPapel());
    }


    @Dado("que eu informo o ID {int} e uma descrição válida")
    public void preparar_dados_basicos(Integer id) {
        this.idTemp = id;
        this.descTemp = "Descrição válida";
    }

    @Dado("o nome da comunidade é {string}")
    public void definir_nome(String nome) {
        this.nomeTemp = nome;
    }

    @Quando("eu tento instanciar a comunidade")
    public void tentar_instanciar() {
        try {
            comunidade = new Comunidade(new ComunidadeId(idTemp), nomeTemp, descTemp);
        } catch (Exception e) {
            excecaoCapturada = e;
        }
    }

    @Então("o sistema deve lançar um erro de validação de campo obrigatório")
    public void validar_erro_campo() {
        assertNotNull(excecaoCapturada, "Deveria ter ocorrido um erro de validação");
    }

    @Dado("que eu informo o nome {string} e descrição {string}")
    public void preparar_dados_completos(String nome, String desc) {
        this.nomeTemp = nome;
        this.descTemp = desc;
        this.idTemp = 1;
    }

    @Quando("a comunidade é criada")
    public void criar_comunidade_valida() {
        comunidade = new Comunidade(new ComunidadeId(idTemp), nomeTemp, descTemp);
    }

    @Então("a data de criação deve ser registrada automaticamente com o horário atual")
    public void validar_data() {
        assertNotNull(comunidade.getDataCriacao());
    }
}