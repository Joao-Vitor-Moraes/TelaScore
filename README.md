# TelaScore

Sistema web para avaliação, organização e descoberta de filmes, com recursos sociais inspirados em plataformas como Letterboxd. A aplicação permite consultar filmes, criar listas, registrar avaliações, acompanhar metas, responder quizzes, receber recomendações, publicar notícias e interagir com outros usuários.

## Visão Geral

O TelaScore está dividido em cinco contextos principais:

- **Identidade:** cadastro, login, perfil, autenticação por token e permissões de administrador.
- **Catálogo:** filmes, avaliações, listas, watchlist, solicitações de novos filmes e importação via TMDB.
- **Análise:** metas, recomendações, quizzes, níveis, pontuação, recompensas e notificações.
- **Social:** amizades/conexões, comunidades, denúncias e mensagens privadas.
- **Informação:** notícias, eventos e calendário de estreias.

## Tecnologias

- Java 17
- Spring Boot 4
- Maven
- React
- MySQL 8
- Docker Compose
- Nginx para servir o frontend em container

## Estrutura

```text
TelaScore/
|-- aplicacao/                 # Casos de uso
|-- apresentacao-backend/      # Controllers, segurança e configuração da API
|-- apresentacao-frontend/     # Aplicação React
|-- dominio-analise/           # Metas, quizzes, recomendações e recompensas
|-- dominio-catalogo/          # Filmes, listas, avaliações e solicitações
|-- dominio-compartilhado/     # Tipos compartilhados
|-- dominio-identidade/        # Usuários e autenticação
|-- dominio-informacao/        # Notícias, eventos e calendário
|-- dominio-social/            # Conexões, comunidades, mensagens e denúncias
`-- infraestrutura/            # Persistência JPA, repositórios e integrações
```

## Como Executar

### Docker

Pré-requisito: Docker Desktop instalado e em execução.

Na pasta onde está o `docker-compose.yml`, rode:

```powershell
docker compose up --build
```

Acesse:

- Aplicação: `http://localhost:3000`
- API: `http://localhost:8080`
- MySQL externo: `localhost:3307`

Para parar:

```powershell
docker compose down
```

Para recriar o banco do zero:

```powershell
docker compose down -v
docker compose up --build
```

Os scripts de inicialização do banco ficam em `docker/mysql/init`.

### Execução Local

Pré-requisitos: Java 17, Maven, Node.js e MySQL 8.

1. Crie o banco:

```sql
CREATE DATABASE IF NOT EXISTS telascore_db;
```

2. Suba o backend:

```powershell
cd TelaScore
$env:DB_URL = "jdbc:mysql://localhost:3306/telascore_db?useSSL=false&serverTimezone=UTC&allowPublicKeyRetrieval=true"
$env:DB_USER = "root"
$env:DB_PASSWORD = "sua_senha_aqui"
$env:TELASCORE_TOKEN_SECRET = "TelaScore-token-dev-secret"
mvn clean install "-Dmaven.test.skip=true"
cd apresentacao-backend
mvn spring-boot:run "-Dmaven.test.skip=true"
```

3. Suba o frontend:

```powershell
cd TelaScore\apresentacao-frontend\telascore-frontend
npm install
npm start
```

## Acesso Inicial

É possível criar uma conta pela tela de login. Também existe um administrador inicial:

```text
E-mail: admin@admin.com
Senha: admin123
```

## Variáveis de Ambiente

| Variável | Uso | Valor comum em desenvolvimento |
|---|---|---|
| `DB_URL` | URL JDBC do MySQL | `jdbc:mysql://localhost:3307/telascore_db?...` |
| `DB_USER` | Usuário do banco | `telascore` ou `root` |
| `DB_PASSWORD` | Senha do banco | definida no `.env` |
| `TELASCORE_TOKEN_SECRET` | Assinatura dos tokens | segredo local de desenvolvimento |
| `REACT_APP_API_URL` | URL da API quando o React roda fora do Docker | `http://localhost:8080` |
| `TMDB_API_KEY` | Importação de catálogo pela TMDB | chave pessoal da API |

## Funcionalidades

- Login, cadastro, sessão persistida e perfis de usuário.
- Catálogo de filmes com busca, detalhes, avaliações e solicitação de inclusão.
- Listas, watchlist e gerenciamento de filmes assistidos.
- Notícias com menção opcional a filmes e visualização do filme relacionado.
- Quizzes conectados ao backend, com respostas, histórico e pontuação.
- Recomendações baseadas em avaliações, watchlist, quizzes e preferências.
- Sistema de nível com pontos por atividades concluídas.
- Metas pessoais e metas de sistema, com pontuação controlada conforme o tipo da meta.
- Amizades/conexões, DMs baseadas em amigos e notificações sociais.
- Comunidades, denúncias e moderação por administrador.
- Notificações no sino para eventos do sistema, social, metas, recomendações e mensagens.
- Navbar compacta por hubs para reduzir poluição visual sem remover funcionalidades.
- Diálogos próprios da aplicação no lugar de `alert`, `confirm` e `prompt` nativos do navegador.

## Padrões de Projeto

| Padrão | Módulo | Uso principal | Classes principais |
|---|---|---|---|
| Iterator | Catálogo | Percorrer itens de listas normais e ranqueadas sem expor a estrutura interna | `IteradorDeItens`, `IteradorSequencialDeItens`, `IteradorRanqueadoDeItens`, `Lista`, `ConsultarItensListaCasoDeUso` |
| Iterator | Social | Percorrer denúncias antes de montar os resumos da aplicação | `ColecaoDenuncias`, `IteradorDeDenuncias`, `IteradorSequencialDeDenuncias`, `MapeadorDenunciasComIterador` |
| Template Method | Análise | Centralizar o fluxo de atualização de metas e variar apenas efeitos como notificação e pontuação | `AtualizadorMetaTemplate`, `AtualizadorMetaComNotificacao`, `AtualizadorMetaSilencioso`, `AdicionarProgressoMetaCasoDeUso` |
| Decorator | Análise | Adicionar restrições ao quiz sem alterar o componente base | `QuizComponent`, `QuizBase`, `QuizDecorator`, `QuizComRestricao`, `ResponderQuizCasoDeUso` |
| Decorator | Social/Infraestrutura | Adicionar logging ao repositório de mensagens sem alterar a implementação persistente | `MensagemRepositorioLoggingDecorator`, `MensagemRepositorioImpl` |
| Proxy | Social | Controlar regras de entrada em comunidades antes de chamar o caso de uso real | `EntrarComunidade`, `EntrarComunidadeProxy`, `ErroProxyHandler` |
| Observer | Catálogo | Reagir a mudanças de filmes e avaliações, como remoção de avaliações associadas e atualização de média | `FilmeObserver`, `FilmeServico`, `RemoverAvaliacoesDoFilmeObserver`, `AvaliacaoObserver`, `MediaNotasObserver`, `AvaliacaoLogObserver` |
| Observer | Informação | Notificar interessados quando uma estreia entra no calendário | `ObservadorEstreia`, `CalendarioEstreia`, `CalendarioServico`, `NotificadorPush` |
| Strategy + Factory | Análise | Calcular pontos por tipo de atividade concluída | `EstrategiaPontuacaoFactory`, estratégias de pontuação e `PontuacaoServico` |
| Singleton | Infraestrutura | Manter uma fábrica compartilhada de `EntityManager` para persistência JPA legada | `ConexaoBanco` |

## Validação e Testes

Backend:

```powershell
cd TelaScore
mvn test
```

Build do backend usado pelo Docker:

```powershell
cd TelaScore
mvn -pl apresentacao-backend -am package "-Dmaven.test.skip=true"
```

Frontend:

```powershell
cd TelaScore\apresentacao-frontend\telascore-frontend
npm run build
```

Validações rápidas úteis depois de subir com Docker:

```powershell
docker compose ps
Invoke-RestMethod http://localhost:3000/api/quizzes
Invoke-RestMethod http://localhost:3000/api/filmes
```


## Protótipos e Material de Apoio

- [Protótipos digitalizados](https://drive.google.com/drive/folders/1E7BqRoQI8SytqiydP5ptddj06PrGCgtM?usp=sharing)
- [Mapa de histórias do usuário](https://github.com/user-attachments/assets/747b329e-ab8a-48be-bec4-59d4eb23d782)
