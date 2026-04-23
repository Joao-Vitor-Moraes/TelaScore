package com.requisitos.avaliacaofilmes.TelaScore.dominio.social.temporaria;

import com.requisitos.avaliacaofilmes.TelaScore.dominio.identidade.usuario.UsuarioId;
import static org.apache.commons.lang3.Validate.notBlank;
import static org.apache.commons.lang3.Validate.notNull;
import java.time.LocalDateTime;

public class Comentario {
    private final ComentarioId id;
    private final UsuarioId autorId;
    
    private final String alvoId; 
    private final TipoAlvoInteracao tipoAlvo; 
    
    private String texto;
    private final LocalDateTime dataCriacao;
    private LocalDateTime dataEdicao;

    public Comentario(ComentarioId id, UsuarioId autorId, String alvoId, TipoAlvoInteracao tipoAlvo, String texto) {
        notNull(id, "ID é obrigatório");
        notNull(autorId, "Autor é obrigatório");
        notBlank(alvoId, "O alvo do comentário não pode estar vazio");
        notNull(tipoAlvo, "O tipo de alvo é obrigatório");
        
        this.id = id;
        this.autorId = autorId;
        this.alvoId = alvoId;
        this.tipoAlvo = tipoAlvo;
        this.dataCriacao = LocalDateTime.now();
        
        editarTexto(texto);
    }

    public void editarTexto(String novoTexto) {
        notBlank(novoTexto, "O comentário não pode ser vazio");
        if (novoTexto.length() > 500) {
            throw new IllegalArgumentException("O comentário excede o limite de 500 caracteres.");
        }
        this.texto = novoTexto;
        this.dataEdicao = LocalDateTime.now();
    }
}