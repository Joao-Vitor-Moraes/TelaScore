package com.requisitos.avaliacaofilmes.TelaScore.apresentacao.catalogo;

import java.net.URI;
import java.net.URLEncoder;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.nio.charset.StandardCharsets;
import java.time.Duration;
import java.time.LocalDate;
import java.util.HashSet;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Set;

import org.springframework.boot.ApplicationRunner;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.annotation.Order;

import com.requisitos.avaliacaofilmes.TelaScore.dominio.catalogo.filme.Filme;
import com.requisitos.avaliacaofilmes.TelaScore.dominio.catalogo.filme.FilmeId;
import com.requisitos.avaliacaofilmes.TelaScore.dominio.catalogo.filme.FilmeRepositorio;

import tools.jackson.databind.JsonNode;
import tools.jackson.databind.ObjectMapper;

@Configuration
public class TmdbCatalogoConfig {

    public static final int PREFIXO_ID_TMDB = 2_000_000;

    private static final String CHAVE_PADRAO_DESENVOLVIMENTO = "65683c0b3525f96ddbf4fcba6a7b4a57";
    private static final String IMAGEM_BASE = "https://image.tmdb.org/t/p/w500";
    private static final int PAGINAS_PADRAO = 20;
    private static final int MAXIMO_PAGINAS = 500;
    private static final int PAGINAS_ESTREIAS = 3;

    @Bean
    @Order(2)
    public ApplicationRunner importarCatalogoTmdb(
            FilmeRepositorio repositorio,
            ObjectMapper objectMapper) {
        return args -> {
            String apiKey = variavel("TMDB_API_KEY", CHAVE_PADRAO_DESENVOLVIMENTO);
            int paginas = numeroDePaginas();

            List<Filme> catalogoAtual = repositorio.listarTodos();
            Set<String> idsExistentes = new HashSet<>();
            Set<String> titulosExistentes = new HashSet<>();
            catalogoAtual.forEach(filme -> {
                idsExistentes.add(filme.getId().getCodigo());
                titulosExistentes.add(normalizarTitulo(filme.getTitulo()));
            });

            long quantidadeJaImportada = catalogoAtual.stream()
                    .map(Filme::getId)
                    .map(FilmeId::getCodigo)
                    .mapToInt(this::converterId)
                    .filter(id -> id >= PREFIXO_ID_TMDB)
                    .count();

            HttpClient cliente = HttpClient.newBuilder()
                    .connectTimeout(Duration.ofSeconds(8))
                    .build();
            Map<Integer, String> generosTmdb = consultarGeneros(cliente, objectMapper, apiKey);

            if (quantidadeJaImportada >= paginas * 15L) {
                atualizarGenerosExistentes(cliente, objectMapper, apiKey, repositorio, idsExistentes, generosTmdb, paginas);
                System.out.printf("Catálogo TMDB já possui %d filmes importados.%n", quantidadeJaImportada);
            } else {
                int importados = 0;
                for (int pagina = 1; pagina <= paginas; pagina++) {
                    try {
                        JsonNode resposta = consultarPagina(cliente, objectMapper, apiKey, pagina);
                        for (JsonNode item : resposta.path("results")) {
                            if (importarFilme(item, repositorio, idsExistentes, titulosExistentes, generosTmdb)) {
                                importados++;
                            }
                        }
                    } catch (Exception erro) {
                        System.err.printf(
                                "Não foi possível importar a página %d do TMDB. O catálogo local continuará disponível. Motivo: %s%n",
                                pagina,
                                erro.getMessage());
                        break;
                    }
                }
                System.out.printf("Catálogo TMDB atualizado: %d novos filmes importados.%n", importados);
            }

            // Próximas estreias: filmes com lançamento futuro, para alimentar o calendário de estreias.
            int estreias = 0;
            String hoje = LocalDate.now().toString();
            for (int pagina = 1; pagina <= PAGINAS_ESTREIAS; pagina++) {
                try {
                    JsonNode resposta = consultarEstreias(cliente, objectMapper, apiKey, pagina, hoje);
                    for (JsonNode item : resposta.path("results")) {
                        if (importarFilme(item, repositorio, idsExistentes, titulosExistentes, generosTmdb)) {
                            estreias++;
                        }
                    }
                } catch (Exception erro) {
                    System.err.printf(
                            "Não foi possível importar a página %d de estreias do TMDB. Motivo: %s%n",
                            pagina,
                            erro.getMessage());
                    break;
                }
            }
            System.out.printf("Estreias futuras importadas do TMDB: %d.%n", estreias);
        };
    }

    private JsonNode consultarPagina(
            HttpClient cliente,
            ObjectMapper objectMapper,
            String apiKey,
            int pagina) throws Exception {
        String url = "https://api.themoviedb.org/3/discover/movie"
                + "?api_key=" + URLEncoder.encode(apiKey, StandardCharsets.UTF_8)
                + "&language=pt-BR"
                + "&region=BR"
                + "&include_adult=false"
                + "&sort_by=popularity.desc"
                + "&vote_count.gte=150"
                + "&page=" + pagina;

        HttpRequest requisicao = HttpRequest.newBuilder(URI.create(url))
                .timeout(Duration.ofSeconds(12))
                .header("Accept", "application/json")
                .GET()
                .build();

        HttpResponse<String> resposta = cliente.send(requisicao, HttpResponse.BodyHandlers.ofString());
        if (resposta.statusCode() < 200 || resposta.statusCode() >= 300) {
            throw new IllegalStateException("TMDB respondeu HTTP " + resposta.statusCode());
        }
        return objectMapper.readTree(resposta.body());
    }

    private JsonNode consultarEstreias(
            HttpClient cliente,
            ObjectMapper objectMapper,
            String apiKey,
            int pagina,
            String aPartirDe) throws Exception {
        String url = "https://api.themoviedb.org/3/discover/movie"
                + "?api_key=" + URLEncoder.encode(apiKey, StandardCharsets.UTF_8)
                + "&language=pt-BR"
                + "&region=BR"
                + "&include_adult=false"
                + "&sort_by=primary_release_date.asc"
                + "&with_release_type=2%7C3"
                + "&primary_release_date.gte=" + aPartirDe
                + "&page=" + pagina;

        HttpRequest requisicao = HttpRequest.newBuilder(URI.create(url))
                .timeout(Duration.ofSeconds(12))
                .header("Accept", "application/json")
                .GET()
                .build();

        HttpResponse<String> resposta = cliente.send(requisicao, HttpResponse.BodyHandlers.ofString());
        if (resposta.statusCode() < 200 || resposta.statusCode() >= 300) {
            throw new IllegalStateException("TMDB respondeu HTTP " + resposta.statusCode());
        }
        return objectMapper.readTree(resposta.body());
    }

    private Map<Integer, String> consultarGeneros(
            HttpClient cliente,
            ObjectMapper objectMapper,
            String apiKey) {
        try {
            String url = "https://api.themoviedb.org/3/genre/movie/list"
                    + "?api_key=" + URLEncoder.encode(apiKey, StandardCharsets.UTF_8)
                    + "&language=pt-BR";
            HttpRequest requisicao = HttpRequest.newBuilder(URI.create(url))
                    .timeout(Duration.ofSeconds(12))
                    .header("Accept", "application/json")
                    .GET()
                    .build();
            HttpResponse<String> resposta = cliente.send(requisicao, HttpResponse.BodyHandlers.ofString());
            if (resposta.statusCode() < 200 || resposta.statusCode() >= 300) {
                return Map.of();
            }

            Map<Integer, String> generos = new LinkedHashMap<>();
            for (JsonNode genero : objectMapper.readTree(resposta.body()).path("genres")) {
                int id = genero.path("id").asInt(0);
                String nome = texto(genero, "name");
                if (id > 0 && nome != null) {
                    generos.put(id, nome);
                }
            }
            return generos;
        } catch (Exception erro) {
            System.err.printf("Nao foi possivel carregar os generos do TMDB. Motivo: %s%n", erro.getMessage());
            return Map.of();
        }
    }

    private int atualizarGenerosExistentes(
            HttpClient cliente,
            ObjectMapper objectMapper,
            String apiKey,
            FilmeRepositorio repositorio,
            Set<String> idsExistentes,
            Map<Integer, String> generosTmdb,
            int paginas) {
        int atualizados = 0;
        for (int pagina = 1; pagina <= paginas; pagina++) {
            try {
                JsonNode resposta = consultarPagina(cliente, objectMapper, apiKey, pagina);
                for (JsonNode item : resposta.path("results")) {
                    int tmdbId = item.path("id").asInt(0);
                    FilmeId filmeId = new FilmeId(String.valueOf(PREFIXO_ID_TMDB + tmdbId));
                    if (!idsExistentes.contains(filmeId.getCodigo())) {
                        continue;
                    }
                    Filme filme = repositorio.obter(filmeId);
                    if (filme != null && filme.getGeneros().isEmpty()) {
                        filme.setGeneros(generosDoItem(item, generosTmdb));
                        repositorio.salvar(filme);
                        atualizados++;
                    }
                }
            } catch (Exception erro) {
                break;
            }
        }
        return atualizados;
    }

    private boolean importarFilme(
            JsonNode item,
            FilmeRepositorio repositorio,
            Set<String> idsExistentes,
            Set<String> titulosExistentes,
            Map<Integer, String> generosTmdb) {
        int tmdbId = item.path("id").asInt(0);
        String titulo = texto(item, "title");
        String poster = texto(item, "poster_path");
        LocalDate dataEstreia = data(texto(item, "release_date"));
        Integer ano = dataEstreia != null ? dataEstreia.getYear() : null;

        if (tmdbId <= 0 || titulo == null || poster == null || ano == null) {
            return false;
        }

        FilmeId filmeId = new FilmeId(String.valueOf(PREFIXO_ID_TMDB + tmdbId));
        String tituloNormalizado = normalizarTitulo(titulo);
        if (idsExistentes.contains(filmeId.getCodigo()) || titulosExistentes.contains(tituloNormalizado)) {
            return false;
        }

        String sinopse = limitar(texto(item, "overview"), 250);
        Filme filme = new Filme(
                filmeId,
                titulo,
                sinopse,
                ano,
                List.of());
        filme.setImagemUrl(IMAGEM_BASE + poster);
        filme.setDataEstreia(dataEstreia);
        filme.setGeneros(generosDoItem(item, generosTmdb));
        repositorio.salvar(filme);
        idsExistentes.add(filmeId.getCodigo());
        titulosExistentes.add(tituloNormalizado);
        return true;
    }

    private List<String> generosDoItem(JsonNode item, Map<Integer, String> generosTmdb) {
        if (!item.path("genre_ids").isArray()) {
            return List.of();
        }
        List<String> generos = new java.util.ArrayList<>();
        for (JsonNode generoId : item.path("genre_ids")) {
            String nome = generosTmdb.get(generoId.asInt(0));
            if (nome != null && !nome.isBlank() && !generos.contains(nome)) {
                generos.add(nome);
            }
        }
        return generos;
    }

    private String texto(JsonNode item, String campo) {
        String valor = item.path(campo).asText("").trim();
        return valor.isEmpty() ? null : valor;
    }

    private LocalDate data(String data) {
        try {
            return data == null ? null : LocalDate.parse(data);
        } catch (Exception ignorado) {
            return null;
        }
    }

    private int numeroDePaginas() {
        try {
            int valor = Integer.parseInt(variavel("TMDB_CATALOG_PAGES", String.valueOf(PAGINAS_PADRAO)));
            return Math.max(1, Math.min(valor, MAXIMO_PAGINAS));
        } catch (NumberFormatException ignorado) {
            return PAGINAS_PADRAO;
        }
    }

    private int converterId(String id) {
        try {
            return Integer.parseInt(id);
        } catch (NumberFormatException ignorado) {
            return 0;
        }
    }

    private String normalizarTitulo(String titulo) {
        return titulo.trim().toLowerCase(Locale.ROOT);
    }

    private String limitar(String texto, int tamanhoMaximo) {
        if (texto == null || texto.length() <= tamanhoMaximo) {
            return texto;
        }
        return texto.substring(0, tamanhoMaximo - 1).trim() + "…";
    }

    private String variavel(String nome, String padrao) {
        String valor = System.getenv(nome);
        return valor == null || valor.isBlank() ? padrao : valor.trim();
    }
}
