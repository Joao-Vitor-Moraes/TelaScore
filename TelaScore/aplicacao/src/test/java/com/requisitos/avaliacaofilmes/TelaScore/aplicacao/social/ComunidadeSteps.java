package com.requisitos.avaliacaofilmes.TelaScore.aplicacao.social;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.util.ArrayList;
import java.util.List;

import com.requisitos.avaliacaofilmes.TelaScore.aplicacao.social.comunidade.EntrarComunidade;
import com.requisitos.avaliacaofilmes.TelaScore.aplicacao.social.comunidade.EntrarComunidadeCasoDeUso;
import com.requisitos.avaliacaofilmes.TelaScore.aplicacao.social.comunidade.EntrarComunidadeProxy;
import com.requisitos.avaliacaofilmes.TelaScore.dominio.identidade.usuario.UsuarioId;
import com.requisitos.avaliacaofilmes.TelaScore.dominio.social.comunidade.*;

import io.cucumber.java.pt.Dado;
import io.cucumber.java.pt.Quando;
import io.cucumber.java.pt.Então;

public class ComunidadeSteps {

    private List<Comunidade> bancoComunidades = new ArrayList<>();
    private List<MembroComunidade> bancoMembros = new ArrayList<>();
    private ComunidadeServico servico;

    private EntrarComunidade entrarComunidadeCasoDeUso;
    private Exception excecaoCapturada;

    private final ComunidadeRepositorio repositorio = new ComunidadeRepositorio() {
        @Override public void salvarComunidade(Comunidade c) { bancoComunidades.add(c); }
        @Override public void salvarMembro(MembroComunidade m) { bancoMembros.add(m); }
        @Override public List<Comunidade> listarTodas() { return bancoComunidades; }
        @Override public Comunidade obterComunidade(ComunidadeId id) {
            return bancoComunidades.stream().filter(c -> c.getId().equals(id)).findFirst().orElse(null);
        }
        @Override public void removerMembro(ComunidadeId cid, UsuarioId uid) {
            bancoMembros.removeIf(m -> m.getComunidadeId().equals(cid) && m.getUsuarioId().equals(uid));
        }
        @Override public List<ComunidadeUsuarioResumo> buscarComunidadesDoUsuario(UsuarioId uid) { return new ArrayList<>(); }
        @Override public List<MembroComunidade> buscarMembrosDaComunidade(ComunidadeId cid) { return bancoMembros; }
        @Override public void atualizarPapelMembro(ComunidadeId cid, UsuarioId uid, PapelComunidade novoPapel) {}
        @Override public boolean verificarSeEhCriador(ComunidadeId cid, UsuarioId uid) { return false; }
        @Override public boolean existeMembro(ComunidadeId cid, UsuarioId uid) { return false; }
        @Override public void excluirComunidade(ComunidadeId cid) {}
        @Override public void salvarMensagem(MensagemComunidade mensagem) {}
        @Override public List<MensagemComunidade> buscarMensagensDaComunidade(ComunidadeId cid) { return new ArrayList<>(); }
        @Override public MensagemComunidade obterMensagemPorId(int mensagemId) { return null; }
        @Override public void excluirMensagem(int mensagemId) {}
    };

    private void inicializarComponentes() {
        servico = new ComunidadeServico(repositorio);
        EntrarComunidade casoReal = new EntrarComunidadeCasoDeUso(repositorio);
        entrarComunidadeCasoDeUso = new EntrarComunidadeProxy(casoReal);
    }

    @Dado("que o usuário {int} deseja criar a comunidade {string}")
    public void que_o_usuario_deseja_criar_a_comunidade(Integer idUsuario, String nome) {
        inicializarComponentes();
    }

    @Quando("o serviço de criação é acionado para o usuário {int}")
    public void o_servico_de_criacao_e_acionado_para_o_usuario(Integer idUsuario) {
        Comunidade nova = new Comunidade(new ComunidadeId(1), "Nolan Fans", "Fãs do diretor");
        servico.criarComunidade(nova, new UsuarioId(idUsuario));
    }

    @Então("a comunidade deve ser persistida no repositório")
    public void a_comunidade_deve_ser_persistida_no_repositório() {
        assertTrue(bancoComunidades.size() > 0);
    }

    @Então("o usuário {int} deve constar na lista de membros com o papel {string}")
    public void o_usuário_deve_constar_na_lista_de_membros_com_o_papel(Integer uid, String papel) {
        String p = papel.replace("\"", "");
        boolean existe = bancoMembros.stream()
                .anyMatch(m -> m.getUsuarioId().getId() == uid && m.getPapel().name().equalsIgnoreCase(p));
        assertTrue(existe);
    }

    @Dado("que existe a comunidade {string} com ID {int}")
    public void que_existe_a_comunidade_com_id(String nome, Integer id) {
        inicializarComponentes();
        repositorio.salvarComunidade(new Comunidade(new ComunidadeId(id), nome, "Desc"));
    }

    @Quando("o usuário {int} solicita entrar na comunidade {int}")
    public void o_usuario_solicita_entrar_na_comunidade(Integer uid, Integer cid) {
        try {
            int tentativas = uid == 99 ? 4 : 1;
            for (int tentativa = 0; tentativa < tentativas; tentativa++) {
                entrarComunidadeCasoDeUso.executar(cid, new UsuarioId(uid));
            }
        } catch (Exception e) {
            excecaoCapturada = e;
        }
    }

    @Então("o serviço deve registrar o usuário {int} como um novo {string}")
    public void o_serviço_deve_registrar_o_usuário_como_um_novo(Integer uid, String papel) {
        String p = papel.replace("\"", "");
        boolean existe = bancoMembros.stream()
                .anyMatch(m -> m.getUsuarioId().getId() == uid && m.getPapel().name().equalsIgnoreCase(p));
        assertTrue(existe);
    }

    @Então("o repositório deve confirmar a existência de {int} novo vínculo social")
    public void o_repositório_deve_confirmar_a_existência_de_novo_vínculo_social(Integer qtd) {
        assertEquals(qtd, bancoMembros.size());
    }

    @Dado("que o usuário {int} é membro da comunidade {int}")
    public void que_o_usuário_é_membro_da_comunidade(Integer uid, Integer cid) {
        inicializarComponentes();
        repositorio.salvarMembro(new MembroComunidade(new ComunidadeId(cid), new UsuarioId(uid), PapelComunidade.MEMBRO));
    }

    @Quando("o comando de remoção é executado para o usuário {int} na comunidade {int}")
    public void o_comando_de_remoção_é_executado_para_o_usuário_na_comunidade(Integer uid, Integer cid) {
        repositorio.removerMembro(new ComunidadeId(cid), new UsuarioId(uid));
    }

    @Então("o vínculo do usuário {int} com a comunidade {int} deve ser excluído do repositório")
    public void o_vínculo_do_usuário_com_a_comunidade_deve_ser_excluído_do_repositório(Integer uid, Integer cid) {
        boolean aindaExiste = bancoMembros.stream()
                .anyMatch(m -> m.getUsuarioId().getId() == uid && m.getComunidadeId().getId() == cid);
        assertTrue(!aindaExiste);
    }

    @Então("o sistema deve barrar a entrada informando uma violação de segurança")
    public void o_sistema_deve_barrar_a_entrada_informando_uma_violacao_de_seguranca() {
        assertNotNull(excecaoCapturada);
        assertTrue(excecaoCapturada instanceof SecurityException);
        assertEquals("Acesso negado: Limite de requisicoes atingido. Aguarde um minuto para entrar em novas comunidades.", excecaoCapturada.getMessage());
    }
}
