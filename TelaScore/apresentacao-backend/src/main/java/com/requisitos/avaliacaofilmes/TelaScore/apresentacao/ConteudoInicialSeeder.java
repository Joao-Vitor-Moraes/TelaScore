package com.requisitos.avaliacaofilmes.TelaScore.apresentacao;

import com.requisitos.avaliacaofilmes.TelaScore.infraestrutura.analise.quiz.entidades.AlternativaEntity;
import com.requisitos.avaliacaofilmes.TelaScore.infraestrutura.analise.quiz.entidades.PerguntaEntity;
import com.requisitos.avaliacaofilmes.TelaScore.infraestrutura.analise.quiz.entidades.QuizEntity;
import com.requisitos.avaliacaofilmes.TelaScore.infraestrutura.config.ConexaoBanco;
import jakarta.persistence.EntityManager;
import jakarta.persistence.EntityTransaction;
import jakarta.persistence.Query;
import java.time.LocalDateTime;
import java.util.List;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;

@Component
public class ConteudoInicialSeeder implements CommandLineRunner {
    private static final int USUARIO_CURADORIA_ID = 9001;

    @Override
    public void run(String... args) {
        EntityManager em = ConexaoBanco.obterEntityManager();
        EntityTransaction tx = em.getTransaction();
        try {
            tx.begin();
            criarUsuarioCuradoria(em);
            criarComunidadeGeral(em);
            criarMetasSistema(em);
            criarQuizzes(em);
            criarNoticias(em);
            criarEventos(em);
            tx.commit();
        } catch (RuntimeException e) {
            if (tx.isActive()) tx.rollback();
            throw e;
        } finally {
            em.close();
        }
    }

    private void criarUsuarioCuradoria(EntityManager em) {
        executar(em, """
                INSERT INTO usuario (id, nome, email, senha, papel_usuario, data_cadastro, apelido, biografia, avatar_url)
                VALUES (:id, 'Curadoria TelaScore', 'curadoria@telascore.local', 'seeded-content', 'ADMIN',
                        :agora, '@telascore', 'Perfil do sistema usado para publicar conteúdo inicial do TelaScore.', NULL)
                ON DUPLICATE KEY UPDATE
                    nome = VALUES(nome),
                    papel_usuario = VALUES(papel_usuario),
                    biografia = VALUES(biografia)
                """)
                .setParameter("id", USUARIO_CURADORIA_ID)
                .setParameter("agora", LocalDateTime.now())
                .executeUpdate();
    }

    private void criarComunidadeGeral(EntityManager em) {
        executar(em, """
                INSERT INTO comunidade (id, nome, descricao)
                VALUES (9001, 'Geral', 'Espaço inicial para conversas abertas sobre filmes, listas, metas e recomendações do TelaScore.')
                ON DUPLICATE KEY UPDATE
                    nome = VALUES(nome),
                    descricao = VALUES(descricao)
                """)
                .executeUpdate();

        executar(em, """
                INSERT INTO membro_comunidade (comunidade_id, usuario_id, papel)
                SELECT 9001, :usuario, 'CRIADOR'
                WHERE NOT EXISTS (
                    SELECT 1 FROM membro_comunidade
                    WHERE comunidade_id = 9001 AND usuario_id = :usuario
                )
                """)
                .setParameter("usuario", USUARIO_CURADORIA_ID)
                .executeUpdate();
    }

    private void criarMetasSistema(EntityManager em) {
        List<MetaSeed> metas = List.of(
                new MetaSeed(9001, "Começar minha jornada no cinema", 5, 30),
                new MetaSeed(9002, "Assistir 10 filmes no mês", 10, 30),
                new MetaSeed(9003, "Maratona de fim de semana", 3, 7),
                new MetaSeed(9004, "Descobrir novos favoritos", 10, 45),
                new MetaSeed(9005, "Desafio cinéfilo do ano", 25, 365),
                new MetaSeed(9006, "Semana do terror", 4, 14),
                new MetaSeed(9007, "Explorar ficção científica", 5, 30),
                new MetaSeed(9008, "Clássicos essenciais", 8, 90),
                new MetaSeed(9009, "Cinema nacional em foco", 5, 60),
                new MetaSeed(9010, "Animações para aquecer o coração", 5, 45),
                new MetaSeed(9011, "Documentários que mudam perspectiva", 4, 45),
                new MetaSeed(9012, "Comédias para respirar", 6, 30));

        for (MetaSeed meta : metas) {
            executar(em, """
                    INSERT INTO meta_sistema (id, titulo, quantidade_alvo, duracao_dias, criada_por_usuario_id, ativa)
                    VALUES (:id, :titulo, :quantidade, :dias, :usuario, true)
                    ON DUPLICATE KEY UPDATE
                        titulo = VALUES(titulo),
                        quantidade_alvo = VALUES(quantidade_alvo),
                        duracao_dias = VALUES(duracao_dias),
                        criada_por_usuario_id = VALUES(criada_por_usuario_id),
                        ativa = VALUES(ativa)
                    """)
                    .setParameter("id", meta.id())
                    .setParameter("titulo", meta.titulo())
                    .setParameter("quantidade", meta.quantidade())
                    .setParameter("dias", meta.dias())
                    .setParameter("usuario", USUARIO_CURADORIA_ID)
                    .executeUpdate();
        }
    }

    private void criarQuizzes(EntityManager em) {
        salvarQuiz(em, quiz(9001, "Fundamentos do cinema",
                "Conceitos básicos para aquecer antes de mergulhar nas listas.",
                pergunta("O que é plano-sequência?",
                        correta("Uma cena filmada em uma tomada contínua"),
                        errada("Uma lista de cenas apagadas"),
                        errada("Um resumo escrito do roteiro"),
                        errada("A trilha sonora principal")),
                pergunta("Qual área cuida principalmente da luz e da composição visual?",
                        correta("Fotografia"),
                        errada("Foley"),
                        errada("Distribuição"),
                        errada("Legendagem")),
                pergunta("O que a montagem organiza em um filme?",
                        correta("A ordem e o ritmo dos planos"),
                        errada("A venda dos ingressos"),
                        errada("A escolha do elenco"),
                        errada("A fabricação dos cenários"))));

        salvarQuiz(em, quiz(9002, "Terror sem tropeçar",
                "Um quiz curto sobre clichês, subgêneros e linguagem do medo.",
                pergunta("Qual subgênero costuma envolver assassinos perseguindo vítimas?",
                        correta("Slasher"),
                        errada("Musical"),
                        errada("Road movie"),
                        errada("Mockumentary esportivo")),
                pergunta("O jump scare depende principalmente de quê?",
                        correta("Quebra brusca de expectativa"),
                        errada("Diálogo improvisado"),
                        errada("Narrador em off"),
                        errada("Cena pós-créditos")),
                pergunta("O horror psicológico costuma valorizar mais:",
                        correta("Ambiguidade e tensão mental"),
                        errada("Competições culinárias"),
                        errada("Coreografias de dança"),
                        errada("Tutoriais científicos"))));

        salvarQuiz(em, quiz(9003, "Ficção científica essencial",
                "Ideias recorrentes em histórias de futuro, tecnologia e humanidade.",
                pergunta("Viagens no tempo normalmente exploram qual tema?",
                        correta("Paradoxos e consequências"),
                        errada("Receitas tradicionais"),
                        errada("Regras de futebol"),
                        errada("Moda medieval")),
                pergunta("Uma distopia geralmente mostra:",
                        correta("Uma sociedade opressiva ou degradada"),
                        errada("Um paraíso sem conflitos"),
                        errada("Um documentário de bastidores"),
                        errada("Uma competição de talentos")),
                pergunta("Androides e inteligências artificiais costumam levantar questões sobre:",
                        correta("Identidade e consciência"),
                        errada("Câmbio monetário"),
                        errada("Jardinagem urbana"),
                        errada("Arquitetura barroca"))));

        salvarQuiz(em, quiz(9004, "Animação em detalhes",
                "Da técnica ao impacto emocional dos filmes animados.",
                pergunta("Stop motion é uma técnica baseada em:",
                        correta("Fotografar objetos quadro a quadro"),
                        errada("Filmar apenas debaixo d'água"),
                        errada("Projetar o filme ao contrário"),
                        errada("Gravar áudio antes do roteiro")),
                pergunta("Storyboard serve para:",
                        correta("Planejar visualmente cenas e enquadramentos"),
                        errada("Armazenar ingressos vendidos"),
                        errada("Escolher a classificação indicativa"),
                        errada("Medir volume da sala")),
                pergunta("Em animação, acting se refere a:",
                        correta("Expressão e intenção dos personagens"),
                        errada("Preço do streaming"),
                        errada("Formato do arquivo final"),
                        errada("Número de salas de cinema"))));

        salvarQuiz(em, quiz(9005, "Cinema brasileiro",
                "Um passeio por temas e formas frequentes do nosso cinema.",
                pergunta("O Cinema Novo ficou associado a qual ideia?",
                        correta("Crítica social e invenção estética"),
                        errada("Apenas super-heróis digitais"),
                        errada("Somente remakes estrangeiros"),
                        errada("Filmes sem personagens")),
                pergunta("Documentários brasileiros muitas vezes se destacam por:",
                        correta("Olhar social e proximidade com personagens reais"),
                        errada("Ausência total de realidade"),
                        errada("Uso obrigatório de fantasia medieval"),
                        errada("Regras fixas de musical")),
                pergunta("Uma mostra de cinema nacional ajuda o público a:",
                        correta("Conhecer diferentes regiões e vozes do país"),
                        errada("Evitar qualquer obra local"),
                        errada("Substituir legendas por trailers"),
                        errada("Transformar filmes em jogos de tabuleiro"))));

        salvarQuiz(em, quiz(9006, "Trilha e som",
                "Como música, silêncio e efeitos constroem emoção.",
                pergunta("Foley é a criação de:",
                        correta("Efeitos sonoros performados em estúdio"),
                        errada("Cartazes promocionais"),
                        errada("Filtros de cor"),
                        errada("Contratos de distribuição")),
                pergunta("Silêncio em uma cena pode funcionar como:",
                        correta("Recurso dramático de tensão ou foco"),
                        errada("Erro inevitável de projeção"),
                        errada("Legenda automática"),
                        errada("Sinônimo de comédia")),
                pergunta("Leitmotiv é um tema musical associado a:",
                        correta("Personagem, ideia ou situação recorrente"),
                        errada("Número da sala"),
                        errada("Tamanho do pôster"),
                        errada("Idioma da legenda"))));

        salvarQuiz(em, quiz(9007, "Direção e atuação",
                "Perguntas sobre interpretação, mise-en-scène e condução de cena.",
                pergunta("Mise-en-scène envolve:",
                        correta("Elementos colocados em cena e sua organização"),
                        errada("Somente a bilheteria"),
                        errada("A velocidade do download"),
                        errada("A cor do ingresso")),
                pergunta("Uma atuação contida costuma apostar mais em:",
                        correta("Gestos sutis e economia emocional"),
                        errada("Gritos constantes sem contexto"),
                        errada("Troca de câmera a cada segundo"),
                        errada("Efeitos de explosão")),
                pergunta("Direção de atores busca alinhar interpretação com:",
                        correta("Tom, conflito e intenção da cena"),
                        errada("Preço dos snacks"),
                        errada("Tempo do intervalo"),
                        errada("Tamanho do arquivo"))));

        salvarQuiz(em, quiz(9008, "Gêneros cinematográficos",
                "Reconheça marcas de gêneros e misturas que aparecem nas recomendações.",
                pergunta("Um thriller normalmente prioriza:",
                        correta("Suspense, risco e tensão progressiva"),
                        errada("Apenas números musicais"),
                        errada("Aulas de história sem conflito"),
                        errada("Cenas de bastidor do set")),
                pergunta("Comédia romântica costuma combinar:",
                        correta("Humor, afeto e desencontros"),
                        errada("Terror cósmico e tribunal"),
                        errada("Manual técnico e silêncio"),
                        errada("Noticiário e placar esportivo")),
                pergunta("Road movie é marcado por:",
                        correta("Viagem que transforma personagens"),
                        errada("Filme feito sem movimento"),
                        errada("Apenas cenas em elevador"),
                        errada("História sem deslocamento"))));
    }

    private void salvarQuiz(EntityManager em, QuizEntity quiz) {
        executar(em, "DELETE FROM alternativa WHERE pergunta_id IN (SELECT id FROM pergunta WHERE quiz_id = :id)")
                .setParameter("id", quiz.getId())
                .executeUpdate();
        executar(em, "DELETE FROM pergunta WHERE quiz_id = :id")
                .setParameter("id", quiz.getId())
                .executeUpdate();
        executar(em, "DELETE FROM quiz WHERE id = :id")
                .setParameter("id", quiz.getId())
                .executeUpdate();
        em.persist(quiz);
    }

    private void criarNoticias(EntityManager em) {
        List<NoticiaSeed> noticias = List.of(
                new NoticiaSeed(9001, "Boas-vindas ao TelaScore", "LANCAMENTO",
                        "O TelaScore começa com metas, listas, quizzes e eventos para ajudar você a transformar filmes vistos em memória organizada."),
                new NoticiaSeed(9002, "Como usar a lista Filmes vistos", "LANCAMENTO",
                        "Toda avaliação marcada como assistida passa a alimentar automaticamente a coleção Filmes vistos do seu perfil."),
                new NoticiaSeed(9003, "Meta da semana: descubra um gênero novo", "LANCAMENTO",
                        "Escolha um gênero que você quase nunca assiste e registre pelo menos uma avaliação até o fim da semana."),
                new NoticiaSeed(9004, "Curadoria recomenda: terror para iniciantes", "CRITICA",
                        "Comece por obras que trabalham atmosfera antes do susto fácil e use sua resenha para registrar o que funcionou."),
                new NoticiaSeed(9005, "Quizzes agora valem como aquecimento cinéfilo", "LANCAMENTO",
                        "Os quizzes foram pensados para testar repertório sem exigir memória enciclopédica."),
                new NoticiaSeed(9006, "Guia rápido de notas", "CRITICA",
                        "Use cinco estrelas para favoritos, três para experiências medianas e uma para filmes que realmente não funcionaram para você."),
                new NoticiaSeed(9007, "Cinema nacional em destaque", "CRITICA",
                        "Separe um espaço nas próximas listas para produções brasileiras, de clássicos a estreias recentes."),
                new NoticiaSeed(9008, "Watchlist boa também precisa de poda", "CRITICA",
                        "Revise sua watchlist de tempos em tempos e mova prioridades reais para o topo."),
                new NoticiaSeed(9009, "Eventos ajudam a assistir junto", "LANCAMENTO",
                        "Sessões coletivas, estreias e desafios de fim de semana ficam mais fáceis de acompanhar pelo calendário."),
                new NoticiaSeed(9010, "Resenhas curtas também contam", "CRITICA",
                        "Uma frase honesta já ajuda seu perfil a ganhar personalidade e melhora sua memória sobre cada filme."));

        int horas = 2;
        for (NoticiaSeed noticia : noticias) {
            executar(em, """
                    INSERT INTO noticia (id, titulo, conteudo, autor_id, data_publicacao, categoria, filme_id)
                    VALUES (:id, :titulo, :conteudo, :autor, :data, :categoria, NULL)
                    ON DUPLICATE KEY UPDATE
                        titulo = VALUES(titulo),
                        conteudo = VALUES(conteudo),
                        autor_id = VALUES(autor_id),
                        data_publicacao = VALUES(data_publicacao),
                        categoria = VALUES(categoria),
                        filme_id = VALUES(filme_id)
                    """)
                    .setParameter("id", noticia.id())
                    .setParameter("titulo", noticia.titulo())
                    .setParameter("conteudo", noticia.conteudo())
                    .setParameter("autor", USUARIO_CURADORIA_ID)
                    .setParameter("data", LocalDateTime.now().minusHours(horas++))
                    .setParameter("categoria", noticia.categoria())
                    .executeUpdate();
        }
    }

    private void criarEventos(EntityManager em) {
        List<EventoSeed> eventos = List.of(
                new EventoSeed(9001, "Sessão de abertura: favoritos recentes", "Assista a um filme visto recentemente e compartilhe uma resenha curta.", 3),
                new EventoSeed(9002, "Noite do terror atmosférico", "Escolha um terror de clima pesado e compare as sensações depois.", 7),
                new EventoSeed(9003, "Clube TelaScore: ficção científica", "Uma rodada para discutir tecnologia, futuro e humanidade no cinema.", 10),
                new EventoSeed(9004, "Maratona de animações", "Sessão leve para revisitar animações marcantes e descobrir novas.", 14),
                new EventoSeed(9005, "Domingo de cinema brasileiro", "Indicações nacionais para movimentar listas e metas de gênero.", 18),
                new EventoSeed(9006, "Desafio 3 filmes em 7 dias", "Comece uma meta curta, registre as avaliações e acompanhe o progresso.", 21),
                new EventoSeed(9007, "Quiz ao vivo: gêneros cinematográficos", "Use os quizzes do site como aquecimento antes da conversa.", 25),
                new EventoSeed(9008, "Revisão de watchlist", "Momento para limpar pendências e escolher os próximos filmes da semana.", 30));

        for (EventoSeed evento : eventos) {
            executar(em, """
                    INSERT INTO evento (id, criador_id, titulo, descricao, data_hora, visibilidade)
                    VALUES (:id, :criador, :titulo, :descricao, :data, 'PUBLICO')
                    ON DUPLICATE KEY UPDATE
                        criador_id = VALUES(criador_id),
                        titulo = VALUES(titulo),
                        descricao = VALUES(descricao),
                        data_hora = VALUES(data_hora),
                        visibilidade = VALUES(visibilidade)
                    """)
                    .setParameter("id", evento.id())
                    .setParameter("criador", USUARIO_CURADORIA_ID)
                    .setParameter("titulo", evento.titulo())
                    .setParameter("descricao", evento.descricao())
                    .setParameter("data", LocalDateTime.now().plusDays(evento.dias()).withHour(20).withMinute(0).withSecond(0).withNano(0))
                    .executeUpdate();
        }
    }

    private Query executar(EntityManager em, String sql) {
        return em.createNativeQuery(sql);
    }

    private QuizEntity quiz(int id, String titulo, String descricao, PerguntaEntity... perguntas) {
        QuizEntity quiz = new QuizEntity();
        quiz.setId(id);
        quiz.setTitulo(titulo);
        quiz.setDescricao(descricao);
        quiz.setPerguntas(List.of(perguntas));
        return quiz;
    }

    private PerguntaEntity pergunta(String texto, AlternativaEntity... alternativas) {
        PerguntaEntity pergunta = new PerguntaEntity();
        pergunta.setTexto(texto);
        pergunta.setAlternativas(List.of(alternativas));
        return pergunta;
    }

    private AlternativaEntity correta(String texto) {
        return alternativa(texto, true);
    }

    private AlternativaEntity errada(String texto) {
        return alternativa(texto, false);
    }

    private AlternativaEntity alternativa(String texto, boolean correta) {
        AlternativaEntity alternativa = new AlternativaEntity();
        alternativa.setTexto(texto);
        alternativa.setCorreta(correta);
        return alternativa;
    }

    private record MetaSeed(int id, String titulo, int quantidade, int dias) {}
    private record NoticiaSeed(int id, String titulo, String categoria, String conteudo) {}
    private record EventoSeed(int id, String titulo, String descricao, int dias) {}
}
