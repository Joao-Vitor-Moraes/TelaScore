package com.requisitos.avaliacaofilmes.TelaScore.apresentacao.analise;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import com.requisitos.avaliacaofilmes.TelaScore.aplicacao.analise.recomendacao.EnviarRecomendacaoCasoDeUso;
import com.requisitos.avaliacaofilmes.TelaScore.aplicacao.analise.recomendacao.EnviarRecomendacaoComando;
import com.requisitos.avaliacaofilmes.TelaScore.aplicacao.analise.recomendacao.ResponderRecomendacaoCasoDeUso;
import com.requisitos.avaliacaofilmes.TelaScore.aplicacao.analise.recomendacao.ResponderRecomendacaoComando;
import com.requisitos.avaliacaofilmes.TelaScore.aplicacao.analise.recomendacao.ListarRecomendacoesPorUsuarioCasoDeUso;
import com.requisitos.avaliacaofilmes.TelaScore.aplicacao.analise.recomendacao.RecomendacaoResumo;
import com.requisitos.avaliacaofilmes.TelaScore.aplicacao.identidade.SessaoUsuario;
import com.requisitos.avaliacaofilmes.TelaScore.dominio.analise.recomendacao.Recomendacao;
import com.requisitos.avaliacaofilmes.TelaScore.dominio.analise.recomendacao.RecomendacaoId;
import com.requisitos.avaliacaofilmes.TelaScore.dominio.analise.recomendacao.RecomendacaoRepositorio;
import com.requisitos.avaliacaofilmes.TelaScore.dominio.analise.recomendacao.TipoConteudo;
import com.requisitos.avaliacaofilmes.TelaScore.dominio.identidade.usuario.UsuarioLogado;
import java.util.List;
import org.springframework.http.HttpStatus;
import org.springframework.web.server.ResponseStatusException;

@RestController
@RequestMapping("/api/recomendacoes")
public class RecomendacaoController {

    private final EnviarRecomendacaoCasoDeUso enviarRecomendacaoCasoDeUso;
    private final ResponderRecomendacaoCasoDeUso responderRecomendacaoCasoDeUso;
    private final ListarRecomendacoesPorUsuarioCasoDeUso listarRecomendacoesCasoDeUso;
    private final RecomendacaoRepositorio recomendacoes;
    private final SessaoUsuario sessao;

    public RecomendacaoController(EnviarRecomendacaoCasoDeUso enviarRecomendacaoCasoDeUso,
                                  ResponderRecomendacaoCasoDeUso responderRecomendacaoCasoDeUso,
                                  ListarRecomendacoesPorUsuarioCasoDeUso listarRecomendacoesCasoDeUso,
                                  RecomendacaoRepositorio recomendacoes,
                                  SessaoUsuario sessao) {
        this.enviarRecomendacaoCasoDeUso = enviarRecomendacaoCasoDeUso;
        this.responderRecomendacaoCasoDeUso = responderRecomendacaoCasoDeUso;
        this.listarRecomendacoesCasoDeUso = listarRecomendacoesCasoDeUso;
        this.recomendacoes = recomendacoes;
        this.sessao = sessao;
    }

    @GetMapping
    public List<RecomendacaoResumo> listar() {
        return listarRecomendacoesCasoDeUso.executar(usuarioAtual().getId().getId());
    }

    @PostMapping
    public ResponseEntity<String> enviarRecomendacao(@RequestBody EnviarRecomendacaoRequest request) {
        try {
            enviarRecomendacaoCasoDeUso.executar(new EnviarRecomendacaoComando(
                    usuarioAtual().getId().getId(),
                    request.destinatarioId(),
                    request.conteudoId(),
                    request.tipoConteudo(),
                    request.mensagem()));
            return ResponseEntity.ok("Recomendação enviada com sucesso!");
        } catch (ResponseStatusException e) {
            throw e;
        } catch (Exception e) {
            return ResponseEntity.badRequest().body("Erro ao enviar recomendação: " + e.getMessage());
        }
    }

    @PutMapping("/reagir")
    public ResponseEntity<String> responderRecomendacao(@RequestBody ResponderRecomendacaoComando comando) {
        try {
            Recomendacao recomendacao = recomendacoes.obter(new RecomendacaoId(comando.recomendacaoId()));
            if (recomendacao == null) {
                throw new IllegalArgumentException("Recomendação não encontrada.");
            }
            if (!usuarioAtual().isMesmoUsuario(recomendacao.getUsuarioId())) {
                throw new ResponseStatusException(HttpStatus.FORBIDDEN, "Esta recomendação pertence a outro usuário.");
            }
            responderRecomendacaoCasoDeUso.executar(comando);
            return ResponseEntity.ok("Resposta registrada com sucesso!");
        } catch (ResponseStatusException e) {
            throw e;
        } catch (Exception e) {
            return ResponseEntity.badRequest().body("Erro ao responder recomendação: " + e.getMessage());
        }
    }

    private UsuarioLogado usuarioAtual() {
        UsuarioLogado usuario = sessao.obterUsuarioLogado();
        if (usuario == null) {
            throw new ResponseStatusException(HttpStatus.UNAUTHORIZED, "Faça login para acessar recomendações.");
        }
        return usuario;
    }

    public record EnviarRecomendacaoRequest(
            int destinatarioId,
            String conteudoId,
            TipoConteudo tipoConteudo,
            String mensagem) {
    }
}
