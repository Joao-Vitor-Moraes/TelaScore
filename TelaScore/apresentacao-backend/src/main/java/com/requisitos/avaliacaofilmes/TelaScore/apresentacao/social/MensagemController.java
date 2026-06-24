package com.requisitos.avaliacaofilmes.TelaScore.apresentacao.social;

import com.requisitos.avaliacaofilmes.TelaScore.aplicacao.social.EnviarMensagemCasoDeUso;
import com.requisitos.avaliacaofilmes.TelaScore.aplicacao.social.EnviarMensagemComando;
import com.requisitos.avaliacaofilmes.TelaScore.aplicacao.social.RemoverMensagemCasoDeUso;
import com.requisitos.avaliacaofilmes.TelaScore.aplicacao.social.RemoverMensagemComando;
import com.requisitos.avaliacaofilmes.TelaScore.dominio.identidade.usuario.UsuarioId;
import com.requisitos.avaliacaofilmes.TelaScore.dominio.social.conexao.ConexaoRepositorio;
import com.requisitos.avaliacaofilmes.TelaScore.dominio.social.mensagem.Mensagem;
import com.requisitos.avaliacaofilmes.TelaScore.dominio.social.mensagem.MensagemId;
import com.requisitos.avaliacaofilmes.TelaScore.dominio.social.mensagem.MensagemRepositorio;
import java.time.format.DateTimeFormatter;
import java.util.List;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.server.ResponseStatusException;

@RestController
@RequestMapping("/api/mensagens")
public class MensagemController {

    private static final DateTimeFormatter ISO_FORMATTER = DateTimeFormatter.ISO_LOCAL_DATE_TIME;

    private final EnviarMensagemCasoDeUso enviarMensagem;
    private final RemoverMensagemCasoDeUso removerMensagem;
    private final MensagemRepositorio mensagemRepositorio;
    private final ConexaoRepositorio conexaoRepositorio;

    public MensagemController(EnviarMensagemCasoDeUso enviarMensagem,
                              RemoverMensagemCasoDeUso removerMensagem,
                              MensagemRepositorio mensagemRepositorio,
                              ConexaoRepositorio conexaoRepositorio) {
        this.enviarMensagem = enviarMensagem;
        this.removerMensagem = removerMensagem;
        this.mensagemRepositorio = mensagemRepositorio;
        this.conexaoRepositorio = conexaoRepositorio;
    }

    @GetMapping("/privadas/{amigoId}")
    public ResponseEntity<List<MensagemResponse>> listarConversa(@PathVariable int amigoId,
                                                                 @RequestParam int usuarioId) {
        exigirAmizade(usuarioId, amigoId);
        List<MensagemResponse> mensagens = mensagemRepositorio
                .buscarConversa(new UsuarioId(usuarioId), new UsuarioId(amigoId))
                .stream()
                .map(this::toResponse)
                .toList();
        return ResponseEntity.ok(mensagens);
    }

    @PostMapping("/privadas")
    public ResponseEntity<MensagemResponse> enviarPrivada(@RequestBody EnviarMensagemRequest body) {
        exigirAmizade(body.remetenteId(), body.destinatarioId());
        String texto = normalizarTexto(body.texto(), body.figurinha());
        Mensagem mensagem = enviarMensagem.executar(
                new EnviarMensagemComando(body.remetenteId(), body.destinatarioId(), texto));
        return ResponseEntity.status(HttpStatus.CREATED).body(toResponse(mensagem));
    }

    @PutMapping("/privadas/{mensagemId}")
    public ResponseEntity<MensagemResponse> editarPrivada(@PathVariable int mensagemId,
                                                          @RequestBody EditarMensagemRequest body) {
        Mensagem mensagem = obterMensagem(mensagemId);
        if (mensagem.getRemetenteId().getId() != body.usuarioId()) {
            throw new ResponseStatusException(HttpStatus.FORBIDDEN, "Apenas o remetente pode editar a mensagem.");
        }

        int outroUsuario = mensagem.getDestinatarioId().getId();
        exigirAmizade(body.usuarioId(), outroUsuario);
        mensagem.setConteudo(normalizarTexto(body.texto(), null));
        mensagemRepositorio.salvar(mensagem);
        return ResponseEntity.ok(toResponse(mensagem));
    }

    @DeleteMapping("/privadas/{mensagemId}")
    public ResponseEntity<Void> removerPrivada(@PathVariable int mensagemId,
                                               @RequestParam int usuarioId) {
        Mensagem mensagem = obterMensagem(mensagemId);
        boolean participante = mensagem.getRemetenteId().getId() == usuarioId
                || mensagem.getDestinatarioId().getId() == usuarioId;
        if (!participante) {
            throw new ResponseStatusException(HttpStatus.FORBIDDEN, "Voce nao participa desta conversa.");
        }
        removerMensagem.executar(new RemoverMensagemComando(mensagemId));
        return ResponseEntity.noContent().build();
    }

    @PostMapping
    public ResponseEntity<MensagemResponse> enviar(@RequestBody EnviarMensagemRequest body) {
        Mensagem mensagem = enviarMensagem.executar(
                new EnviarMensagemComando(body.remetenteId(), body.destinatarioId(), normalizarTexto(body.texto(), null)));
        return ResponseEntity.status(HttpStatus.CREATED).body(toResponse(mensagem));
    }

    @DeleteMapping("/{mensagemId}")
    public ResponseEntity<Void> remover(@PathVariable int mensagemId) {
        removerMensagem.executar(new RemoverMensagemComando(mensagemId));
        return ResponseEntity.noContent().build();
    }

    private void exigirAmizade(int usuarioA, int usuarioB) {
        UsuarioId a = new UsuarioId(usuarioA);
        UsuarioId b = new UsuarioId(usuarioB);
        boolean ida = conexaoRepositorio.buscarConexao(a, b) != null;
        boolean volta = conexaoRepositorio.buscarConexao(b, a) != null;
        if (!ida || !volta) {
            throw new ResponseStatusException(HttpStatus.FORBIDDEN, "Mensagens privadas estao disponiveis apenas entre amigos.");
        }
    }

    private Mensagem obterMensagem(int mensagemId) {
        Mensagem mensagem = mensagemRepositorio.obter(new MensagemId(mensagemId));
        if (mensagem == null) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "Mensagem nao encontrada.");
        }
        return mensagem;
    }

    private String normalizarTexto(String texto, String figurinha) {
        String conteudo = texto == null ? "" : texto.trim();
        if (figurinha != null && !figurinha.isBlank()) {
            conteudo = conteudo.isBlank() ? figurinha.trim() : conteudo + "\n" + figurinha.trim();
        }
        if (conteudo.isBlank()) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "A mensagem nao pode estar vazia.");
        }
        return conteudo;
    }

    private MensagemResponse toResponse(Mensagem mensagem) {
        String data = mensagem.getDataEnvio().format(ISO_FORMATTER);
        return new MensagemResponse(
                mensagem.getId().getId(),
                mensagem.getRemetenteId().getId(),
                mensagem.getDestinatarioId().getId(),
                mensagem.getConteudo(),
                data,
                data,
                mensagem.isLida()
        );
    }

    public static record EnviarMensagemRequest(int remetenteId, int destinatarioId, String texto, String figurinha) {}

    public static record EditarMensagemRequest(int usuarioId, String texto) {}

    public static record MensagemResponse(int id, int remetenteId, int destinatarioId, String texto,
                                          String data, String dataEnvio, boolean lida) {}
}
