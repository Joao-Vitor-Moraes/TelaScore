package com.requisitos.avaliacaofilmes.TelaScore.apresentacao.catalogo;

import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import org.springframework.boot.ApplicationRunner;
import org.springframework.core.annotation.Order;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import com.requisitos.avaliacaofilmes.TelaScore.dominio.catalogo.diretor.Diretor;
import com.requisitos.avaliacaofilmes.TelaScore.dominio.catalogo.diretor.DiretorId;
import com.requisitos.avaliacaofilmes.TelaScore.dominio.catalogo.diretor.DiretorRepositorio;
import com.requisitos.avaliacaofilmes.TelaScore.dominio.catalogo.filme.Filme;
import com.requisitos.avaliacaofilmes.TelaScore.dominio.catalogo.filme.FilmeId;
import com.requisitos.avaliacaofilmes.TelaScore.dominio.catalogo.filme.FilmeRepositorio;

@Configuration
public class CatalogoInicialConfig {

    @Bean
    @Order(1)
    public ApplicationRunner catalogoInicial(
            FilmeRepositorio filmeRepositorio,
            DiretorRepositorio diretorRepositorio) {
        return args -> {
            Map<String, DiretorId> diretores = prepararDiretores(diretorRepositorio);
            int id = 10000;

            for (FilmeInicial item : filmes()) {
                int filmeId = id++;
                if (filmeRepositorio.existeComTitulo(item.titulo())) {
                    continue;
                }

                List<DiretorId> idsDiretores = item.diretores().stream()
                        .map(diretores::get)
                        .toList();

                Filme filme = new Filme(
                        new FilmeId(String.valueOf(filmeId)),
                        item.titulo(),
                        item.sinopse(),
                        item.ano(),
                        idsDiretores);
                filme.setImagemUrl(item.imagemUrl());
                filmeRepositorio.salvar(filme);
            }
        };
    }

    private Map<String, DiretorId> prepararDiretores(DiretorRepositorio repositorio) {
        Map<String, DiretorId> resultado = new LinkedHashMap<>();
        int id = 1000;

        for (FilmeInicial filme : filmes()) {
            for (String nome : filme.diretores()) {
                if (resultado.containsKey(nome)) {
                    continue;
                }

                DiretorId diretorId = new DiretorId(id++);
                resultado.put(nome, diretorId);
                if (repositorio.obter(diretorId) == null) {
                    repositorio.salvar(new Diretor(diretorId, nome));
                }
            }
        }
        return resultado;
    }

    private List<FilmeInicial> filmes() {
        return List.of(
                // Studio Ghibli
                filme("O Castelo no Céu", 1986, "Hayao Miyazaki",
                        "Sheeta e Pazu partem em busca da lendária cidade flutuante de Laputa enquanto protegem um cristal de forças militares e piratas do céu.",
                        "https://image.tmdb.org/t/p/w600_and_h900_bestv2/npOnzAbLh6VOIu3naU5QaEcTepo.jpg"),
                filme("Túmulo dos Vagalumes", 1988, "Isao Takahata",
                        "Durante os últimos meses da Segunda Guerra, dois irmãos órfãos tentam sobreviver em um Japão devastado pelos bombardeios.",
                        "https://image.tmdb.org/t/p/w600_and_h900_bestv2/qG3RYlIVpTYclR9TYIsy8p7m7AT.jpg"),
                filme("Meu Amigo Totoro", 1988, "Hayao Miyazaki",
                        "Duas irmãs se mudam para o interior e descobrem espíritos mágicos da floresta, entre eles o gentil e inesquecível Totoro.",
                        "https://image.tmdb.org/t/p/w600_and_h900_bestv2/rtGDOeG9LzoerkDGZF9dnVeLppL.jpg"),
                filme("O Serviço de Entregas da Kiki", 1989, "Hayao Miyazaki",
                        "Uma jovem bruxa inicia seu ano de independência em uma cidade costeira e cria um serviço de entregas voadoras.",
                        "https://image.tmdb.org/t/p/w600_and_h900_bestv2/7nO5DUMnGUuXrA4r2h6ESOKQRrx.jpg"),
                filme("Memórias de Ontem", 1991, "Isao Takahata",
                        "Ao viajar para o campo, uma mulher de 27 anos revisita lembranças da infância e reconsidera os caminhos de sua vida.",
                        "https://image.tmdb.org/t/p/w600_and_h900_bestv2/xjJU6rwzLX7Jk8HFQfVW6H5guMC.jpg"),
                filme("Porco Rosso: O Último Herói Romântico", 1992, "Hayao Miyazaki",
                        "Um ás da aviação transformado em porco caça piratas aéreos sobre o Adriático entre duelos, humor e antigas paixões.",
                        "https://image.tmdb.org/t/p/w600_and_h900_bestv2/byKAndF6KQSDpGxp1mTr23jPbYp.jpg"),
                filme("Pom Poko: A Grande Batalha dos Guaxinins", 1994, "Isao Takahata",
                        "Guaxinins com poderes de transformação lutam para proteger sua floresta do avanço urbano de Tóquio.",
                        "https://image.tmdb.org/t/p/w600_and_h900_bestv2/kowo9E1e1JcWLXj9cCvAOFZcy5n.jpg"),
                filme("Sussurros do Coração", 1995, "Yoshifumi Kondō",
                        "Uma estudante apaixonada por livros conhece um jovem artesão e encontra coragem para perseguir seu sonho de escrever.",
                        "https://image.tmdb.org/t/p/w600_and_h900_bestv2/5E3Hvbu0bg38ouYf6chGftVGqZ7.jpg"),
                filme("Princesa Mononoke", 1997, "Hayao Miyazaki",
                        "Um príncipe amaldiçoado se vê no centro do conflito entre uma comunidade industrial e os antigos deuses da floresta.",
                        "https://image.tmdb.org/t/p/w600_and_h900_bestv2/jHWmNr7m544fJ8eItsfNk8fs2Ed.jpg"),
                filme("Meus Vizinhos, os Yamadas", 1999, "Isao Takahata",
                        "Pequenas histórias acompanham o cotidiano divertido, caótico e afetuoso de uma família japonesa.",
                        "https://image.tmdb.org/t/p/w600_and_h900_bestv2/wTGuHmMIBBgKakY80J1D52VvQKI.jpg"),
                filme("A Viagem de Chihiro", 2001, "Hayao Miyazaki",
                        "Chihiro entra em um mundo de espíritos e precisa trabalhar em uma casa de banhos para salvar seus pais e voltar para casa.",
                        "https://image.tmdb.org/t/p/w600_and_h900_bestv2/39wmItIWsg5sZMyRUHLkWBcuVCM.jpg"),
                filme("O Reino dos Gatos", 2002, "Hiroyuki Morita",
                        "Após salvar um gato, Haru é levada a um reino fantástico onde precisa recuperar sua liberdade e sua própria identidade.",
                        "https://image.tmdb.org/t/p/w600_and_h900_bestv2/avPMO5cnaGHgLaNiAIhy33WoQLm.jpg"),
                filme("O Castelo Animado", 2004, "Hayao Miyazaki",
                        "Amaldiçoada com a aparência de uma idosa, Sophie encontra abrigo no castelo ambulante do misterioso mago Howl.",
                        "https://image.tmdb.org/t/p/w600_and_h900_bestv2/TkTPELv4kC3u1lkloush8skOjE.jpg"),
                filme("Contos de Terramar", 2006, "Gorō Miyazaki",
                        "Em uma terra desequilibrada por dragões e magia, um jovem príncipe acompanha um sábio mago em uma jornada sombria.",
                        "https://image.tmdb.org/t/p/w600_and_h900_bestv2/67yYwCPq7NbxSF6BIIXCMD34sY0.jpg"),
                filme("Ponyo: Uma Amizade que Veio do Mar", 2008, "Hayao Miyazaki",
                        "Um menino encontra uma peixinha mágica que deseja se tornar humana, provocando uma aventura capaz de alterar o equilíbrio do mar.",
                        "https://image.tmdb.org/t/p/w600_and_h900_bestv2/mikKSEdk5kLhflWXbp4S5mmHsDo.jpg"),
                filme("O Mundo dos Pequeninos", 2010, "Hiromasa Yonebayashi",
                        "A pequena Arrietty vive escondida sob uma casa humana até ser descoberta por um garoto solitário.",
                        "https://image.tmdb.org/t/p/w600_and_h900_bestv2/oc2OB2KDmSRDMelKEAA1n4YRQL0.jpg"),
                filme("Da Colina Kokuriko", 2011, "Gorō Miyazaki",
                        "Dois estudantes se aproximam enquanto lutam para preservar um antigo clube escolar na Yokohama de 1963.",
                        "https://image.tmdb.org/t/p/w600_and_h900_bestv2/rRLYX4RZIyloHSJwvZKAhphAjiB.jpg"),
                filme("Vidas ao Vento", 2013, "Hayao Miyazaki",
                        "A paixão pelo voo guia a vida do engenheiro Jiro Horikoshi em meio a sonhos, amor e às turbulências do Japão do século XX.",
                        "https://image.tmdb.org/t/p/w600_and_h900_bestv2/jfwSexzlIzaOgxP9A8bTA6t8YYb.jpg"),
                filme("O Conto da Princesa Kaguya", 2013, "Isao Takahata",
                        "Uma menina encontrada dentro de um bambu cresce rapidamente e é levada a uma vida de nobreza que ameaça sua liberdade.",
                        "https://image.tmdb.org/t/p/w600_and_h900_bestv2/mWRQNlWXYYfd2z4FRm99MsgHgiA.jpg"),
                filme("As Memórias de Marnie", 2014, "Hiromasa Yonebayashi",
                        "Em uma cidade litorânea, Anna conhece a misteriosa Marnie e desvenda segredos ligados à sua própria família.",
                        "https://image.tmdb.org/t/p/w600_and_h900_bestv2/vug1dvDI1tSa60Z8qjCuUE7ntkO.jpg"),
                filme("A Tartaruga Vermelha", 2016, "Michaël Dudok de Wit",
                        "Um náufrago tenta escapar de uma ilha deserta, mas o encontro com uma tartaruga transforma para sempre sua existência.",
                        "https://image.tmdb.org/t/p/w600_and_h900_bestv2/wOBU3SLjQ9358Km9YWYasPZyebp.jpg"),
                filme("Aya e a Bruxa", 2021, "Gorō Miyazaki",
                        "Uma órfã muito esperta é adotada por uma bruxa e usa sua inteligência para enfrentar uma casa cheia de magia e mistérios.",
                        "https://image.tmdb.org/t/p/w600_and_h900_bestv2/sJhFtY3eHuvvACaPpxpzdCLQqpQ.jpg"),

                // Homem-Aranha
                filme("Homem-Aranha", 2002, "Sam Raimi",
                        "Após ser picado por uma aranha geneticamente modificada, Peter Parker aprende que grandes poderes trazem grandes responsabilidades.",
                        "https://upload.wikimedia.org/wikipedia/en/6/6c/Spider-Man_%282002_film%29_poster.jpg"),
                filme("Homem-Aranha 2", 2004, "Sam Raimi",
                        "Peter tenta equilibrar sua vida pessoal com o heroísmo enquanto enfrenta o brilhante e trágico Doutor Octopus.",
                        "https://upload.wikimedia.org/wikipedia/en/4/4e/Spider-Man_2_USA_poster.jpg"),
                filme("Homem-Aranha 3", 2007, "Sam Raimi",
                        "Um simbionte alienígena intensifica o lado sombrio de Peter enquanto novos inimigos surgem em Nova York.",
                        "https://upload.wikimedia.org/wikipedia/en/7/7a/Spider-Man_3%2C_International_Poster.jpg"),
                filme("O Espetacular Homem-Aranha", 2012, "Marc Webb",
                        "Peter Parker investiga o desaparecimento dos pais e descobre seus poderes enquanto enfrenta o Lagarto.",
                        "https://upload.wikimedia.org/wikipedia/en/e/e0/The_Amazing_Spider-Man_%28film%29_poster.jpg"),
                filme("O Espetacular Homem-Aranha 2", 2014, "Marc Webb",
                        "A vida de Peter se complica quando Electro surge e antigas revelações sobre a Oscorp vêm à tona.",
                        "https://upload.wikimedia.org/wikipedia/en/2/24/The_Amazing_Spider-Man_2_poster.jpg"),
                filme("Homem-Aranha: De Volta ao Lar", 2017, "Jon Watts",
                        "De volta à rotina escolar, Peter quer provar que merece ser um Vingador enquanto enfrenta o Abutre.",
                        "https://upload.wikimedia.org/wikipedia/en/f/f9/Spider-Man_Homecoming_poster.jpg"),
                filme("Homem-Aranha: Longe de Casa", 2019, "Jon Watts",
                        "Durante uma viagem escolar pela Europa, Peter é recrutado para enfrentar ameaças elementais e o enigmático Mysterio.",
                        "https://upload.wikimedia.org/wikipedia/en/b/bd/Spider-Man_Far_From_Home_poster.jpg"),
                filme("Homem-Aranha: Sem Volta para Casa", 2021, "Jon Watts",
                        "Com sua identidade revelada, Peter pede ajuda ao Doutor Estranho e abre as portas para visitantes de outros universos.",
                        "https://upload.wikimedia.org/wikipedia/en/0/00/Spider-Man_No_Way_Home_poster.jpg"),
                filme("Homem-Aranha no Aranhaverso", 2018, List.of("Bob Persichetti", "Peter Ramsey", "Rodney Rothman"),
                        "Miles Morales descobre que não é o único Homem-Aranha quando heróis de diferentes dimensões chegam ao seu mundo.",
                        "https://upload.wikimedia.org/wikipedia/en/f/fa/Spider-Man_Into_the_Spider-Verse_poster.png"),
                filme("Homem-Aranha: Através do Aranhaverso", 2023, List.of("Joaquim Dos Santos", "Kemp Powers", "Justin K. Thompson"),
                        "Miles atravessa o multiverso e entra em conflito com uma sociedade de Pessoas-Aranha sobre o significado de ser um herói.",
                        "https://upload.wikimedia.org/wikipedia/en/b/b4/Spider-Man-_Across_the_Spider-Verse_poster.jpg"),

                // Fantasia, ficção científica e cultura nerd
                filme("O Senhor dos Anéis: A Sociedade do Anel", 2001, "Peter Jackson",
                        "Frodo deixa o Condado com a missão de destruir o Um Anel antes que ele caia nas mãos de Sauron.",
                        "https://upload.wikimedia.org/wikipedia/en/f/fb/Lord_Rings_Fellowship_Ring.jpg"),
                filme("O Senhor dos Anéis: As Duas Torres", 2002, "Peter Jackson",
                        "A Sociedade está dividida, enquanto a guerra se aproxima e Frodo continua sua jornada até Mordor.",
                        "https://upload.wikimedia.org/wikipedia/en/a/a1/Lord_Rings_Two_Towers.jpg"),
                filme("O Senhor dos Anéis: O Retorno do Rei", 2003, "Peter Jackson",
                        "A batalha final pela Terra-média começa enquanto Frodo e Sam se aproximam da Montanha da Perdição.",
                        "https://upload.wikimedia.org/wikipedia/en/4/48/Lord_Rings_Return_King.jpg"),
                filme("Matrix", 1999, List.of("Lana Wachowski", "Lilly Wachowski"),
                        "Um hacker descobre que a realidade é uma simulação controlada por máquinas e se une à rebelião humana.",
                        "https://upload.wikimedia.org/wikipedia/en/d/db/The_Matrix.png"),
                filme("Interestelar", 2014, "Christopher Nolan",
                        "Exploradores atravessam um buraco de minhoca em busca de um novo lar para a humanidade enquanto o tempo cobra seu preço.",
                        "https://upload.wikimedia.org/wikipedia/en/b/bc/Interstellar_film_poster.jpg"),
                filme("Duna", 2021, "Denis Villeneuve",
                        "Paul Atreides viaja ao planeta Arrakis e se envolve em uma disputa por poder, profecia e a substância mais valiosa do universo.",
                        "https://upload.wikimedia.org/wikipedia/en/8/8e/Dune_%282021_film%29.jpg"),
                filme("Star Wars: Uma Nova Esperança", 1977, "George Lucas",
                        "Luke Skywalker deixa seu planeta natal e se une à Rebelião para enfrentar o Império e resgatar a princesa Leia.",
                        "https://upload.wikimedia.org/wikipedia/en/8/87/StarWarsMoviePoster1977.jpg"),
                filme("Star Wars: O Império Contra-Ataca", 1980, "Irvin Kershner",
                        "Enquanto o Império persegue os rebeldes, Luke busca treinamento Jedi e descobre uma verdade devastadora.",
                        "https://upload.wikimedia.org/wikipedia/en/3/3f/The_Empire_Strikes_Back_%281980_film%29.jpg"),
                filme("Harry Potter e a Pedra Filosofal", 2001, "Chris Columbus",
                        "Um garoto órfão descobre que é bruxo e inicia seus estudos em Hogwarts, onde encontra amizade e antigos perigos.",
                        "https://upload.wikimedia.org/wikipedia/en/thumb/7/7a/Harry_Potter_and_the_Philosopher%27s_Stone_banner.jpg/330px-Harry_Potter_and_the_Philosopher%27s_Stone_banner.jpg"),
                filme("De Volta para o Futuro", 1985, "Robert Zemeckis",
                        "Marty McFly viaja acidentalmente para 1955 e precisa garantir que seus pais se apaixonem para salvar seu próprio futuro.",
                        "https://upload.wikimedia.org/wikipedia/en/d/d2/Back_to_the_Future.jpg"),
                filme("Blade Runner 2049", 2017, "Denis Villeneuve",
                        "Um novo blade runner encontra um segredo que pode alterar o equilíbrio entre humanos e replicantes.",
                        "https://upload.wikimedia.org/wikipedia/en/9/9b/Blade_Runner_2049_poster.png"),
                filme("Homem de Ferro", 2008, "Jon Favreau",
                        "O inventor Tony Stark constrói uma armadura de alta tecnologia e decide usá-la para enfrentar as consequências de suas armas.",
                        "https://upload.wikimedia.org/wikipedia/en/0/02/Iron_Man_%282008_film%29_poster.jpg"),
                filme("Guardiões da Galáxia", 2014, "James Gunn",
                        "Um grupo improvável de foras da lei precisa trabalhar junto para impedir que um artefato destrua a galáxia.",
                        "https://upload.wikimedia.org/wikipedia/en/3/33/Guardians_of_the_Galaxy_%28film%29_poster.jpg"),

                // Romance
                filme("Diário de uma Paixão", 2004, "Nick Cassavetes",
                        "Em diferentes fases da vida, Noah e Allie enfrentam diferenças sociais e escolhas que desafiam um amor inesquecível.",
                        "https://upload.wikimedia.org/wikipedia/en/8/86/Posternotebook.jpg"),
                filme("Orgulho e Preconceito", 2005, "Joe Wright",
                        "Elizabeth Bennet e o reservado Sr. Darcy confrontam orgulho, expectativas sociais e sentimentos que insistem em crescer.",
                        "https://upload.wikimedia.org/wikipedia/en/0/03/Prideandprejudiceposter.jpg"),
                filme("La La Land: Cantando Estações", 2016, "Damien Chazelle",
                        "Uma atriz e um pianista se apaixonam em Los Angeles enquanto perseguem sonhos que podem levá-los por caminhos diferentes.",
                        "https://upload.wikimedia.org/wikipedia/en/a/ab/La_La_Land_%28film%29.png"),
                filme("Antes do Amanhecer", 1995, "Richard Linklater",
                        "Dois jovens se conhecem em um trem e passam uma noite caminhando por Viena, conversando sobre a vida e o amor.",
                        "https://upload.wikimedia.org/wikipedia/en/d/da/Before_Sunrise_poster.jpg"),
                filme("Questão de Tempo", 2013, "Richard Curtis",
                        "Um jovem capaz de viajar no tempo aprende que nenhum poder substitui a beleza dos momentos comuns ao lado de quem se ama.",
                        "https://upload.wikimedia.org/wikipedia/en/7/7c/About_Time_%282013_film%29_Poster.jpg"),
                filme("Brilho Eterno de uma Mente sem Lembranças", 2004, "Michel Gondry",
                        "Após apagar as lembranças de um relacionamento, Joel percebe durante o processo que ainda não quer esquecer Clementine.",
                        "https://upload.wikimedia.org/wikipedia/en/a/a4/Eternal_Sunshine_of_the_Spotless_Mind.png"),
                filme("Titanic", 1997, "James Cameron",
                        "A bordo do Titanic, uma jovem da alta sociedade e um artista sem dinheiro vivem um romance em meio a uma tragédia histórica.",
                        "https://upload.wikimedia.org/wikipedia/en/1/18/Titanic_%281997_film%29_poster.png"),
                filme("Your Name", 2016, "Makoto Shinkai",
                        "Dois adolescentes começam a trocar de corpo sem explicação e criam um vínculo capaz de atravessar tempo e distância.",
                        "https://upload.wikimedia.org/wikipedia/en/0/0b/Your_Name_poster.png"),

                // Grandes histórias e variedade
                filme("O Poderoso Chefão", 1972, "Francis Ford Coppola",
                        "A família Corleone atravessa uma transição de poder quando Michael é lentamente atraído para o império criminoso do pai.",
                        "https://upload.wikimedia.org/wikipedia/en/1/1c/Godfather_ver1.jpg"),
                filme("Pulp Fiction: Tempo de Violência", 1994, "Quentin Tarantino",
                        "Histórias de criminosos, boxeadores e figuras excêntricas se cruzam em uma Los Angeles imprevisível.",
                        "https://upload.wikimedia.org/wikipedia/en/3/3b/Pulp_Fiction_%281994%29_poster.jpg"),
                filme("Um Sonho de Liberdade", 1994, "Frank Darabont",
                        "Condenado injustamente, Andy Dufresne constrói uma amizade duradoura e preserva a esperança dentro da prisão de Shawshank.",
                        "https://upload.wikimedia.org/wikipedia/en/8/81/ShawshankRedemptionMoviePoster.jpg"),
                filme("Parasita", 2019, "Bong Joon Ho",
                        "Uma família com dificuldades financeiras se infiltra gradualmente na vida de uma família rica, com consequências inesperadas.",
                        "https://upload.wikimedia.org/wikipedia/en/5/53/Parasite_%282019_film%29.png"),
                filme("Whiplash: Em Busca da Perfeição", 2014, "Damien Chazelle",
                        "Um jovem baterista enfrenta os métodos extremos de um professor obcecado em extrair dele uma performance extraordinária.",
                        "https://upload.wikimedia.org/wikipedia/en/0/01/Whiplash_poster.jpg"),
                filme("Tudo em Todo o Lugar ao Mesmo Tempo", 2022, List.of("Daniel Kwan", "Daniel Scheinert"),
                        "Uma mulher comum precisa atravessar universos alternativos e acessar outras versões de si mesma para salvar o multiverso.",
                        "https://upload.wikimedia.org/wikipedia/en/1/1e/Everything_Everywhere_All_at_Once.jpg"),
                filme("O Show de Truman", 1998, "Peter Weir",
                        "Sem saber, Truman vive desde o nascimento dentro de um gigantesco reality show e começa a desconfiar de sua realidade.",
                        "https://upload.wikimedia.org/wikipedia/en/c/cd/Trumanshow.jpg"),

                // Novidades de 2025 e 2026
                filme("Billie Eilish - Hit Me Hard and Soft: The Tour (Live in 3D)", 2026,
                        List.of("Billie Eilish", "James Cameron"),
                        "Billie Eilish leva ao cinema a energia de sua turnê mundial em uma experiência musical imersiva filmada em 3D.",
                        "/billie-eilish-hit-me-hard-and-soft-live-3d.png"),
                filme("KPop Demon Hunters", 2025, List.of("Maggie Kang", "Chris Appelhans"),
                        "As estrelas do grupo Huntrix equilibram a fama com uma vida secreta como caçadoras de demônios em uma aventura musical vibrante.",
                        "https://upload.wikimedia.org/wikipedia/en/thumb/9/93/KPDHposter.jpeg/500px-KPDHposter.jpeg"),
                filme("Project Hail Mary", 2026, List.of("Phil Lord", "Christopher Miller"),
                        "Um professor acorda sozinho em uma nave sem memória e descobre que sua missão pode ser a última esperança para salvar a Terra.",
                        "https://upload.wikimedia.org/wikipedia/en/thumb/3/3b/Project_Hail_Mary_poster.jpg/500px-Project_Hail_Mary_poster.jpg"),
                filme("O Diabo Veste Prada 2", 2026, "David Frankel",
                        "Andy Sachs retorna ao universo da Runway para ajudar Miranda Priestly a enfrentar uma indústria da moda completamente transformada.",
                        "https://upload.wikimedia.org/wikipedia/en/thumb/9/97/The_Devil_Wears_Prada_2_%28film_poster%29.png/500px-The_Devil_Wears_Prada_2_%28film_poster%29.png"),
                filme("Hoppers", 2026, "Daniel Chong",
                        "Uma jovem transfere sua mente para um castor robótico e mergulha no mundo animal para proteger um habitat ameaçado.",
                        "https://upload.wikimedia.org/wikipedia/en/thumb/6/6c/Hoppers_film_poster.jpg/500px-Hoppers_film_poster.jpg"),
                filme("Pecadores", 2025, "Ryan Coogler",
                        "Dois irmãos retornam ao Mississippi dos anos 1930 para recomeçar a vida, mas encontram uma ameaça sobrenatural à sua espera.",
                        "https://upload.wikimedia.org/wikipedia/en/thumb/5/5f/Sinners_%282025_film%29_poster.jpg/500px-Sinners_%282025_film%29_poster.jpg"),
                filme("F1", 2025, "Joseph Kosinski",
                        "Um piloto veterano volta às pistas para ajudar uma equipe em crise e orientar um jovem talento da Fórmula 1.",
                        "https://upload.wikimedia.org/wikipedia/en/thumb/3/38/F1_%282025_film%29.png/500px-F1_%282025_film%29.png"),
                filme("Wicked: For Good", 2025, "Jon M. Chu",
                        "Elphaba e Glinda seguem caminhos opostos enquanto as consequências de suas escolhas transformam para sempre a Terra de Oz.",
                        "https://upload.wikimedia.org/wikipedia/en/thumb/b/bf/Wicked_For_Good_poster.jpg/500px-Wicked_For_Good_poster.jpg"),
                filme("Zootopia 2", 2025, List.of("Jared Bush", "Byron Howard"),
                        "Judy Hopps e Nick Wilde perseguem uma nova pista pela metrópole animal enquanto tentam limpar seus próprios nomes.",
                        "https://upload.wikimedia.org/wikipedia/en/thumb/6/6a/Zootopia_2_%282025_film%29.jpg/500px-Zootopia_2_%282025_film%29.jpg"),
                filme("The Super Mario Galaxy Movie", 2026, List.of("Aaron Horvath", "Michael Jelenic"),
                        "Mario, Luigi e seus amigos partem para o espaço em uma aventura colorida para proteger Rosalina e enfrentar Bowser novamente.",
                        "https://upload.wikimedia.org/wikipedia/en/thumb/b/bf/The_Super_Mario_Galaxy_Movie_poster.jpeg/500px-The_Super_Mario_Galaxy_Movie_poster.jpeg"),
                filme("Star Wars: The Mandalorian e Grogu", 2026, "Jon Favreau",
                        "Din Djarin e Grogu são recrutados pela Nova República para uma nova missão em uma galáxia ainda ameaçada por forças imperiais.",
                        "https://upload.wikimedia.org/wikipedia/en/thumb/4/4c/The_Mandalorian_and_Grogu_poster.jpg/250px-The_Mandalorian_and_Grogu_poster.jpg"),
                filme("Mortal Kombat II", 2026, "Simon McQuoid",
                        "Johnny Cage se junta aos campeões do Plano Terreno para enfrentar os guerreiros da Exoterra no torneio Mortal Kombat.",
                        "https://upload.wikimedia.org/wikipedia/en/thumb/9/9a/Mortal_Kombat_II_%28film%29_poster.jpg/250px-Mortal_Kombat_II_%28film%29_poster.jpg"),
                filme("Toy Story 5", 2026, "Andrew Stanton",
                        "Jessie, Woody e Buzz encaram um novo desafio quando uma tela inteligente se torna o brinquedo favorito de Bonnie.",
                        "https://upload.wikimedia.org/wikipedia/en/thumb/0/08/Toy_Story_5_poster.jpg/250px-Toy_Story_5_poster.jpg"));
    }

    private FilmeInicial filme(String titulo, int ano, String diretor, String sinopse, String imagemUrl) {
        return filme(titulo, ano, List.of(diretor), sinopse, imagemUrl);
    }

    private FilmeInicial filme(
            String titulo,
            int ano,
            List<String> diretores,
            String sinopse,
            String imagemUrl) {
        return new FilmeInicial(titulo, ano, diretores, sinopse, imagemUrl);
    }

    private record FilmeInicial(
            String titulo,
            int ano,
            List<String> diretores,
            String sinopse,
            String imagemUrl) {
    }
}
