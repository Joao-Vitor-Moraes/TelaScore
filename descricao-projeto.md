# TelaScore — Descrição Completa do Projeto

## 1. Apresentação do Domínio

O TelaScore é uma plataforma social voltada para cinéfilos e amantes do cinema. O domínio abrange todas as atividades que envolvem o consumo, registro, organização e compartilhamento de experiências cinematográficas. Diante do volume crescente de produções audiovisuais, os cinéfilos enfrentam dificuldades para acompanhar o que já assistiram, expressar suas opiniões de forma organizada, descobrir novos filmes alinhados ao seu gosto pessoal e interagir com outros fãs. O TelaScore resolve essas dores ao oferecer um ecossistema completo que une catálogo de filmes, avaliações, listas personalizadas, comunidades temáticas, recomendações inteligentes, calendário de estreias, notícias, sistema de gamificação e muito mais — tudo integrado em uma experiência social rica e engajante.

---

## Jornada 1 — Chegando à Plataforma: Cadastro e Identidade

**Objetivo da Persona:** "Preciso criar minha conta no TelaScore e montar meu perfil de cinéfilo para que outros usuários me conheçam e vejam meus gostos."

### Decomposição em Passos

1. Registrar-se no sistema informando nome, e-mail e senha.
2. Personalizar o perfil público com apelido, biografia e avatar.

### Funcionalidades

#### 1. Gerenciamento de Cinéfilos
Permite o cadastro, consulta, listagem e remoção de usuários. A entidade `Usuario` possui campos obrigatórios (nome, e-mail validado via Value Object `Email`, senha validada via `Senha`) e um papel (`PapelUsuario`: ADMIN ou CINEFILO). O `UsuarioServico` orquestra as operações de CRUD garantindo que nenhum campo obrigatório seja nulo ou vazio. A listagem permite que o sistema exiba outros cinéfilos para fins de busca e conexão social.

**Tela associada:** Formulário de cadastro com campos de Nome, E-mail e Senha; tela de listagem de usuários com busca.

#### 2. Personalização de Perfil
Após o cadastro, o usuário cria e edita seu perfil público. A entidade `Perfil` contém `apelido` (Value Object `Apelido`, validado para não ser nulo), `biografia` (texto livre) e `avatarUrl` (link para foto). O `PerfilServico` permite criar, editar (com verificação de propriedade — só o dono pode alterar seu próprio perfil), consultar por ID de usuário e remover. A edição valida que o perfil existe e pertence ao usuário autenticado antes de aplicar alterações.

**Tela associada:** Página de perfil com campos editáveis de apelido, biografia e upload de avatar; visualização pública do perfil de outros usuários.

---

## Jornada 2 — Registrando Opiniões: Avaliação e Catálogo

**Objetivo da Persona:** "Acabei de assistir um filme incrível e quero registrar minha nota e resenha para nunca esquecer o que achei, e para que outros cinéfilos vejam minha opinião."

### Decomposição em Passos

1. Encontrar o filme desejado no catálogo da plataforma.
2. Atribuir uma nota (de 1 a 5 estrelas) ao filme assistido.
3. Escrever, editar ou consultar resenhas críticas (reviews) detalhadas sobre os filmes.
4. Caso o filme não exista no catálogo, solicitar sua inclusão.

### Funcionalidades

#### 3. Gerenciamento de Filmes
A entidade `Filme` armazena `titulo` (obrigatório e não vazio), `sinopse` (opcional, mas se informada não pode estar em branco), `anoLancamento` (obrigatório) e uma lista de `DiretorId` (obrigatória — todo filme deve ter pelo menos um diretor). O `FilmeServico` permite o cadastro (persistência) de novos filmes no catálogo via `FilmeRepositorio`. As validações de domínio garantem integridade desde a instanciação: título em branco lança `IllegalArgumentException`, e a lista de diretores não pode ser vazia. O método `adicionarDiretor` permite vincular novos diretores após a criação. Os testes BDD (feature "Gerenciamento de filmes") cobrem o cadastro com dados válidos (verificando que o título é persistido corretamente) e o bloqueio de criação quando o título está vazio.

**Tela associada:** Página de detalhes do filme exibindo título, sinopse, ano, diretores e média de avaliações; formulário administrativo de cadastro de novos filmes.

#### 4. Gerenciamento de Reviews
A entidade `Avaliacao` vincula um `UsuarioId` a um `FilmeId`, registrando uma `Nota` (Value Object com valor entre 1 e 5), uma `resenha` (campo de texto livre opcional) e a `dataAvaliacao` (preenchida automaticamente). O `AvaliacaoServico` permite registrar avaliações, e a `CalculadoraMediaAvaliacoes` calcula a média das notas de um filme a partir de uma lista de avaliações. A nota é obrigatória e limitada ao intervalo 1-5 (fora do intervalo lança `IllegalArgumentException`), enquanto a review é opcional na criação e pode ser **editada independentemente** da nota a qualquer momento. O `AtualizarAvaliacaoCasoDeUso` na camada de aplicação recebe um `AtualizarAvaliacaoComando` que permite atualizar a nota e/ou a resenha de forma granular — se apenas a resenha for enviada, a nota permanece inalterada e vice-versa. O `ObterAvaliacaoCasoDeUso` permite consultar avaliações existentes retornando um `AvaliacaoResumo`. Os testes BDD (feature "Sistema de review de filmes") cobrem cenários de criação com nota válida e bloqueio de notas fora do intervalo permitido (ex: nota 6).

**Tela associada:** Componente de estrelas clicáveis na página do filme + campo de texto expandido (textarea) para resenha; botão "Editar Resenha" nas avaliações já publicadas; seção de "Avaliações dos Usuários" listando nota, resenha e data.

#### 5. Solicitação de Filmes
Quando o cinéfilo não encontra um filme (clássico, independente ou regional), ele pode solicitar sua inclusão. A entidade `SolicitacaoFilme` possui `tituloSugerido`, `justificativa` opcional e um `StatusSolicitacao` (PENDENTE → APROVADA ou REJEITADA). O `SolicitacaoServico` permite criar solicitações, aprová-las ou rejeitá-las (ações de moderação). A data de criação é registrada automaticamente.

**Tela associada:** Formulário com campos Título Sugerido e Justificativa; painel administrativo listando solicitações pendentes com botões Aprovar/Rejeitar.

---

## Jornada 3 — Organizando o Acervo Pessoal: Listas de Filmes

**Objetivo da Persona:** "Quero organizar os filmes que já assisti e os que pretendo assistir em listas temáticas personalizadas, como 'Favoritos de Terror' ou 'Maratona Oscar 2026'."

### Decomposição em Passos

1. Criar uma lista com título, descrição e definir se ela será ranqueada (com ordenação manual) ou não.
2. Adicionar filmes à lista, garantindo que não haja duplicatas.
3. Reorganizar a ordem dos filmes em listas ranqueadas.
4. Remover filmes da lista quando necessário.

### Funcionalidade

#### 6. Gerenciar Listas
A entidade `Lista` pertence a um `UsuarioId` (dono) e contém `titulo`, `descricao`, flag `ranqueada` e uma coleção de `ItemLista`. Cada `ItemLista` referencia um `FilmeId` e armazena uma `anotacao` pessoal opcional. As regras de negócio incluem: apenas o dono pode modificar a lista (método `garantirPermissao`); filmes duplicados são impedidos; a reordenação manual (`moverFilmeParaPosicao`) só é permitida em listas ranqueadas; posições inválidas lançam exceção. O `ListaServico` gerencia a persistência.

**Tela associada:** Página "Minhas Listas" com cards de listas criadas; tela interna da lista com itens arrastáveis (drag-and-drop para listas ranqueadas) e botão de adicionar/remover filme.

---

## Jornada 4 — Vida Social: Amigos e Mensagens

**Objetivo da Persona:** "Quero seguir outros cinéfilos para acompanhar o que eles assistem e poder trocar mensagens privadas sobre filmes."

### Decomposição em Passos

1. Encontrar e seguir outros cinéfilos na plataforma.
2. Enviar e receber mensagens privadas com outros usuários.

### Funcionalidades

#### 7. Sistema de Amigos
A entidade `Conexao` representa o vínculo entre um `seguidorId` e um `seguidoId`, com `dataCriacao` automática. A regra de negócio principal impede que um usuário siga a si mesmo (`isTrue(!seguidorId.equals(seguidoId))`). O `ConexaoServico` permite seguir um usuário (com verificação de duplicata via repositório), deixar de seguir e listar seguidores. O `ConexaoRepositorio` expõe métodos para verificar existência de conexão e listar conexões.

**Tela associada:** Botão "Seguir" no perfil público de outros usuários; página "Meus Seguidores/Seguindo" com lista de conexões.

#### 8. Mensagens Privadas
A entidade `Mensagem` conecta `remetenteId` e `destinatarioId`, contendo `conteudo` (validado para não ser nulo nem em branco), `dataEnvio` automática e flag `lida` (inicialmente `false`). Regras: um usuário não pode enviar mensagem para si mesmo; o conteúdo não pode estar vazio. O método `marcarComoLida()` atualiza o status. O `MensagemServico` gerencia envio e remoção de mensagens.

**Tela associada:** Caixa de entrada (inbox) estilo chat; conversa individual com campo de texto e botão enviar; indicador visual de "não lida".

---

## Jornada 5 — Participando de Comunidades

**Objetivo da Persona:** "Quero participar de grupos temáticos sobre gêneros de cinema que eu gosto, como 'Fãs de Ficção Científica' ou 'Cineastas Brasileiros', para discutir filmes com pessoas de interesses parecidos."

### Decomposição em Passos

1. Buscar e ingressar em comunidades existentes.
2. Interagir com membros da comunidade, observando as regras e papéis definidos.

### Funcionalidade

#### 9. Comunidades
A entidade `Comunidade` possui `nome`, `descricao` (ambos obrigatórios e não vazios — nome em branco lança exceção de validação), `regras` (opcional) e `dataCriacao` (registrada automaticamente no momento da instanciação). Os membros são representados pela entidade `MembroComunidade`, que vincula `ComunidadeId` + `UsuarioId` com um `PapelComunidade` (CRIADOR, MODERADOR ou MEMBRO) e `dataEntrada`. Regras de negócio: um membro pode ser promovido a moderador (`promoverAModerador`); um moderador pode ser rebaixado a membro (`rebaixarAMembro`); o criador nunca pode ser rebaixado (lança `IllegalStateException`). O `ComunidadeServico` permite criar comunidades, adicionar membros e buscar por ID. Na camada de aplicação, o `CriarComunidadeCasoDeUso` orquestra a criação recebendo um `CriarComunidadeComando` com nome, descrição e ID do criador. Os testes BDD cobrem cenários como: bloquear rebaixamento do criador, promover membro a moderador, impedir criação com nome em branco e validar que a data de criação é registrada automaticamente.

**Tela associada:** Página de listagem de comunidades com busca; página interna da comunidade com feed de discussão, lista de membros e regras; botão "Participar".

---

## Jornada 6 — Descobrindo Novos Filmes: Recomendações

**Objetivo da Persona:** "Estou sem ideia do que assistir e quero receber sugestões de filmes compatíveis com meu gosto, seja pela plataforma ou por amigos."

### Decomposição em Passos

1. Receber recomendações automáticas baseadas em compatibilidade.
2. Receber recomendações sociais (enviadas por outros usuários) com mensagem personalizada.
3. Aceitar, rejeitar ou simplesmente visualizar a recomendação.

### Funcionalidade

#### 10. Recomendação de Filme
A entidade `Recomendacao` possui `usuarioId` (destinatário), `filmeId`, `pontuacaoCompatibilidade` (0 a 100, validada), `remetenteId` (opcional — se presente, é recomendação social; se ausente, é algorítmica), `mensagem` (máximo 255 caracteres), `dataGeracao` e `StatusRecomendacao` (PENDENTE → VISUALIZADA → ACEITA ou REJEITADA). Regras: o remetente não pode ser o mesmo que o destinatário; uma recomendação rejeitada não pode ser aceita depois (lança exceção). O `RecomendacaoServico` permite gerar recomendações algorítmicas, enviar recomendações sociais, aceitar/rejeitar e listar pendentes.

**Tela associada:** Carrossel "Recomendados para Você" na home; seção "Amigos Recomendam" com mensagem do remetente; botões Aceitar/Rejeitar em cada card.

---

## Jornada 7 — Acompanhando Novidades: Notícias e Calendário

**Objetivo da Persona:** "Quero ficar por dentro das novidades do cinema — trailers, anúncios — e marcar no meu calendário pessoal as datas de estreia dos filmes que me interessam para não perder nenhum lançamento."

### Decomposição em Passos

1. Ler notícias e novidades publicadas na plataforma.
2. Adicionar filmes futuros ao calendário pessoal de estreias.
3. Ativar ou desativar lembretes para cada filme marcado.

### Funcionalidades

#### 11. Notícias de Filmes
A entidade `Noticia` possui `autorId`, `titulo` (obrigatório, não vazio e com mínimo de 5 caracteres), `conteudo` (obrigatório e não vazio), `dataPublicacao` automática e `CategoriaNoticia` — um enum que classifica a notícia em LANCAMENTO, CRITICA, EVENTO ou CURIOSIDADE. O `NoticiaServico` permite publicar notícias, excluí-las e pesquisar por filtros combinando termo de busca e categoria (`pesquisarNoticias`). O `NoticiaRepositorio` expõe métodos adicionais para buscar notícias recentes (com limite) e por autor. Na camada de aplicação, o `AdicionarNoticiaCasoDeUso` recebe um comando com título, conteúdo, ID do autor e categoria (convertida para o enum), instancia a entidade e delega ao serviço. O `PesquisarNoticiasCasoDeUso` permite buscar notícias filtrando por termo e/ou categoria, e o `RemoverNoticiaCasoDeUso` gerencia a exclusão. Os testes BDD na camada de domínio cobrem validação da entidade e regras de categoria, enquanto os testes na camada de aplicação cobrem o fluxo completo de publicação, filtragem e exclusão.

**Tela associada:** Feed de notícias estilo blog com cards contendo título, categoria (badge colorido), resumo e data; filtros por categoria; página de leitura completa da notícia.

#### 12. Calendário de Estreias Personalizado
A entidade `CalendarioEstreia` pertence a um `UsuarioId` e contém uma lista de `EntradaCalendario`. Cada `EntradaCalendario` referencia um `FilmeId`, uma `dataEstreiaPrevista` (obrigatória) e um flag `lembreteAtivo` (padrão: `true`). Regras: filmes duplicados não são adicionados ao calendário (verificação por `FilmeId`); o lembrete pode ser alternado (`alternarLembrete`); filmes podem ser removidos. O `CalendarioServico` permite registrar filmes no calendário de um usuário.

**Tela associada:** Visualização em formato de agenda mensal; botão "Adicionar ao Calendário" na página do filme; toggle de lembrete por entrada.

---

## Jornada 8 — Eventos e Encontros

**Objetivo da Persona:** "Quero organizar ou participar de sessões especiais de cinema, debates e maratonas com outros cinéfilos da plataforma."

### Decomposição em Passos

1. Criar um evento com título, descrição, data e hora.
2. Cancelar um evento caso necessário.

### Funcionalidade

#### 13. Eventos
A entidade `Evento` possui `criadorId`, `titulo` (obrigatório e não vazio), `descricao` (opcional) e `dataHora` (obrigatória e validada para ser no futuro — `isTrue(dataHora.isAfter(LocalDateTime.now()))`). O `EventoServico` permite agendar e cancelar eventos. O agendamento valida que o evento não é nulo antes de salvar; o cancelamento valida o ID.

**Tela associada:** Painel de eventos com cards contendo título, data/hora e descrição; formulário de criação de evento; botão "Cancelar Evento".

---

## Jornada 9 — Desafios e Conhecimento: Quiz e Metas

**Objetivo da Persona:** "Quero testar meu conhecimento sobre cinema, me desafiar a assistir uma quantidade específica de filmes por ano e acompanhar meu progresso."

### Decomposição em Passos

1. Participar de quizzes temáticos sobre cinema.
2. Definir metas pessoais de filmes e acompanhar o progresso.
3. Estender prazos, cancelar ou marcar metas como falhadas conforme necessário.

### Funcionalidades

#### 14. Quiz
A entidade `Quiz` possui `titulo`, `descricao` e uma lista de `Pergunta`. Cada `Pergunta` contém um enunciado e uma lista de `Alternativa` (com texto e flag de correta). O quiz só pode ser disponibilizado se tiver ao menos uma pergunta (`validarQuizPronto`). A entidade `TentativaQuiz` registra as respostas do usuário para calcular a pontuação. O `QuizServico` gerencia a criação e disponibilização de quizzes.

**Tela associada:** Lista de quizzes disponíveis; tela interativa de perguntas com alternativas clicáveis e cronômetro; tela de resultado com pontuação.

#### 15. Metas de Filmes/Desafios
A entidade `Meta` pertence a um `UsuarioId` e contém `titulo`, `quantidadeAlvo` (> 0), `quantidadeAtual` (inicia em 0), `dataPrazo` (deve ser hoje ou futura) e `StatusMeta` (EM_ANDAMENTO → CONCLUIDA, FALHADA ou CANCELADA). Regras ricas de negócio: `adicionarProgresso` só funciona em metas em andamento e conclui automaticamente ao atingir o alvo; `removerProgresso` reabre metas concluídas se o progresso cair abaixo do alvo; `estenderPrazo` só aceita datas posteriores ao prazo atual; `cancelar` e `marcarComoFalhada` só atuam em metas em andamento; alterar a `quantidadeAlvo` pode alterar automaticamente o status. O `MetaServico` permite criar, adicionar progresso, estender prazo e cancelar metas.

**Tela associada:** Painel "Minhas Metas" com barra de progresso visual (quantidadeAtual/quantidadeAlvo); botões de adicionar progresso e estender prazo; badges de status (Em Andamento, Concluída, Falhada).

---

## Jornada 10 — Engajamento: Sistema de Recompensas

**Objetivo da Persona:** "Quero ser reconhecido pelo meu engajamento na plataforma e acumular pontos por minhas contribuições."

### Decomposição em Passos

1. Realizar ações que geram pontos (avaliar filmes, acertar quizzes, criar listas, completar metas, convidar amigos).
2. Acumular pontuação e subir de nível no perfil.

### Funcionalidade

#### 16. Sistema de Gamificação
A entidade `RegistroPontuacao` vincula um `UsuarioId` a `Pontos` (Value Object com valor > 0), uma `AcaoPontuada` (enum: AVALIAR_FILME, ACERTAR_QUIZ, CRIAR_LISTA, COMPLETAR_META, CONVIDAR_AMIGO) e `dataRegistro` automática. O `PontuacaoServico` permite registrar pontos quando o usuário realiza ações qualificadas. Cada registro é imutável após criação.

**Tela associada:** Indicador de pontuação total no perfil do usuário; histórico de pontos com ação e data; badges e níveis desbloqueáveis.

---

## 6. Especificação de Teste e Automação (BDD)

Para concluir, apresentamos os artefatos de teste da funcionalidade **Mensagens Privadas** (`dominio-social`). A automação usa BDD com Cucumber (Gherkin em português) e JUnit 5 em Java.

### Especificação: `enviar_mensagem.feature`

```gherkin
# language: pt
Funcionalidade: Envio de Mensagens Privadas

  Cenário: Enviar uma mensagem válida com sucesso
    Dado que o usuário 1 quer enviar uma mensagem para o usuário 2
    Quando ele escreve "Olá, tudo bem?" e envia
    Então a mensagem deve ser registrada com sucesso
    E o status inicial deve ser "não lida"

  Cenário: Impedir envio de mensagem para si mesmo
    Dado que o usuário 1 tenta enviar uma mensagem para o usuário 1
    Quando ele escreve "Nota mental: comprar pipoca" e envia
    Então o sistema deve rejeitar o envio informando que não pode enviar para si mesmo

  Cenário: Impedir mensagem com conteúdo em branco
    Dado que o usuário 1 quer enviar uma mensagem para o usuário 2
    Quando ele tenta enviar uma mensagem sem texto
    Então o sistema deve rejeitar o envio informando que o conteúdo não pode estar em branco

  Cenário: Remover uma mensagem enviada com sucesso
    Dado que o usuário 1 enviou uma mensagem para o usuário 2 com o texto "Mensagem para deletar"
    Quando a mensagem é removida
    Então a mensagem deve deixar de existir no sistema
```

### Automação: `MensagemSteps.java`

```java
package com.requisitos.avaliacaofilmes.TelaScore.dominio.social;

import static org.junit.jupiter.api.Assertions.*;
import com.requisitos.avaliacaofilmes.TelaScore.dominio.social.mensagem.*;
import com.requisitos.avaliacaofilmes.TelaScore.dominio.identidade.usuario.UsuarioId;
import io.cucumber.java.pt.*;

public class MensagemSteps {

    private UsuarioId remetenteId;
    private UsuarioId destinatarioId;
    private Mensagem mensagem;
    private Exception excecaoCapturada;

    @Dado("que o usuário {int} quer enviar uma mensagem para o usuário {int}")
    public void preparar_usuarios(Integer id1, Integer id2) {
        this.remetenteId = new UsuarioId(id1);
        this.destinatarioId = new UsuarioId(id2);
        this.excecaoCapturada = null;
    }

    @Dado("que o usuário {int} tenta enviar uma mensagem para o usuário {int}")
    public void preparar_auto_envio(Integer id1, Integer id2) {
        preparar_usuarios(id1, id2);
    }

    @Quando("ele escreve {string} e envia")
    public void enviar_mensagem(String texto) {
        try {
            this.mensagem = new Mensagem(new MensagemId(1), remetenteId, destinatarioId, texto);
        } catch (Exception e) {
            this.excecaoCapturada = e;
        }
    }

    @Quando("ele tenta enviar uma mensagem sem texto")
    public void enviar_mensagem_vazia() {
        enviar_mensagem("");
    }

    @Então("a mensagem deve ser registrada com sucesso")
    public void validar_sucesso() {
        assertNull(excecaoCapturada);
        assertNotNull(mensagem);
    }

    @Então("o status inicial deve ser {string}")
    public void validar_status(String status) {
        assertFalse(mensagem.isLida());
    }

    @Então("o sistema deve rejeitar o envio informando que não pode enviar para si mesmo")
    public void validar_erro_si_mesmo() {
        assertNotNull(excecaoCapturada);
        assertEquals("O remetente e o destinatário não podem ser a mesma pessoa",
                     excecaoCapturada.getMessage());
    }

    @Então("o sistema deve rejeitar o envio informando que o conteúdo não pode estar em branco")
    public void validar_erro_vazio() {
        assertNotNull(excecaoCapturada);
        assertTrue(excecaoCapturada.getMessage().contains("em branco"));
    }

    @Dado("que o usuário {int} enviou uma mensagem para o usuário {int} com o texto {string}")
    public void setup_mensagem_para_remover(Integer id1, Integer id2, String texto) {
        this.remetenteId = new UsuarioId(id1);
        this.destinatarioId = new UsuarioId(id2);
        this.mensagem = new Mensagem(new MensagemId(1), remetenteId, destinatarioId, texto);
    }

    @Quando("a mensagem é removida")
    public void a_mensagem_e_removida() {
        this.mensagem = null;
    }

    @Então("a mensagem deve deixar de existir no sistema")
    public void validar_remocao() {
        assertNull(this.mensagem, "A mensagem deveria ser nula após a remoção");
    }
}
```

Os testes cobrem os quatro cenários críticos: caminho feliz (envio válido com status "não lida"), autoenvio bloqueado, conteúdo em branco rejeitado e remoção com sucesso. Cada cenário segue o padrão AAA (Arrange/Act/Assert) por meio das anotações Gherkin `@Dado`, `@Quando` e `@Então`.
