package com.requisitos.avaliacaofilmes.TelaScore.apresentacao.identidade.usuario;

import com.requisitos.avaliacaofilmes.TelaScore.aplicacao.identidade.SessaoUsuario;
import com.requisitos.avaliacaofilmes.TelaScore.dominio.identidade.usuario.UsuarioLogado;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardCopyOption;
import java.util.Map;
import java.util.Set;
import java.util.UUID;

import org.springframework.core.io.Resource;
import org.springframework.core.io.UrlResource;
import org.springframework.http.CacheControl;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.server.ResponseStatusException;
import org.springframework.http.HttpStatus;

@RestController
@RequestMapping("/api/identidade/usuario")
public class AvatarController {

    private static final long TAMANHO_MAXIMO = 5L * 1024 * 1024;
    private static final Set<String> TIPOS_PERMITIDOS = Set.of(
            MediaType.IMAGE_JPEG_VALUE,
            MediaType.IMAGE_PNG_VALUE,
            "image/webp",
            MediaType.IMAGE_GIF_VALUE);

    private final SessaoUsuario sessaoUsuario;
    private final Path diretorio;

    public AvatarController(SessaoUsuario sessaoUsuario) {
        this.sessaoUsuario = sessaoUsuario;
        String configurado = System.getenv("TELASCORE_UPLOAD_DIR");
        this.diretorio = Path.of(configurado == null || configurado.isBlank()
                ? "uploads/avatars"
                : configurado).toAbsolutePath().normalize();
    }

    @PostMapping(value = "/meu-usuario/avatar", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ResponseEntity<?> enviar(@RequestParam("arquivo") MultipartFile arquivo) {
        UsuarioLogado usuario = sessaoUsuario.obterUsuarioLogado();
        if (usuario == null) {
            throw new ResponseStatusException(HttpStatus.UNAUTHORIZED, "Faça login para alterar sua foto.");
        }
        validar(arquivo);

        try {
            Files.createDirectories(diretorio);
            String extensao = extensaoPara(arquivo.getContentType());
            String nome = usuario.getId().getId() + "-" + UUID.randomUUID() + extensao;
            Path destino = diretorio.resolve(nome).normalize();
            if (!destino.getParent().equals(diretorio)) {
                throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Nome de arquivo inválido.");
            }
            Files.copy(arquivo.getInputStream(), destino, StandardCopyOption.REPLACE_EXISTING);
            return ResponseEntity.ok(Map.of("url", "/api/identidade/usuario/avatar/" + nome));
        } catch (IOException e) {
            throw new ResponseStatusException(HttpStatus.INTERNAL_SERVER_ERROR, "Não foi possível salvar a imagem.");
        }
    }

    @GetMapping("/avatar/{nome}")
    public ResponseEntity<Resource> obter(@PathVariable String nome) {
        if (!nome.matches("[a-zA-Z0-9._-]+")) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND);
        }
        try {
            Path arquivo = diretorio.resolve(nome).normalize();
            if (!arquivo.getParent().equals(diretorio) || !Files.isRegularFile(arquivo)) {
                throw new ResponseStatusException(HttpStatus.NOT_FOUND);
            }
            Resource recurso = new UrlResource(arquivo.toUri());
            String tipo = Files.probeContentType(arquivo);
            return ResponseEntity.ok()
                    .cacheControl(CacheControl.noCache())
                    .header(HttpHeaders.CONTENT_TYPE, tipo == null ? MediaType.APPLICATION_OCTET_STREAM_VALUE : tipo)
                    .body(recurso);
        } catch (IOException e) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND);
        }
    }

    private void validar(MultipartFile arquivo) {
        if (arquivo == null || arquivo.isEmpty()) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Selecione uma imagem.");
        }
        if (arquivo.getSize() > TAMANHO_MAXIMO) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "A imagem deve ter no máximo 5 MB.");
        }
        if (!TIPOS_PERMITIDOS.contains(arquivo.getContentType())) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Use uma imagem JPG, PNG, WEBP ou GIF.");
        }
    }

    private String extensaoPara(String tipo) {
        return switch (tipo) {
            case MediaType.IMAGE_PNG_VALUE -> ".png";
            case "image/webp" -> ".webp";
            case MediaType.IMAGE_GIF_VALUE -> ".gif";
            default -> ".jpg";
        };
    }
}
