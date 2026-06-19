# 🎬 TelaScore

Sistema de gerenciamento de avaliações de filmes inspirado em plataformas como
Letterboxd, permitindo que cinéfilos registrem, organizem e compartilhem suas
experiências cinematográficas.

## 🌎 Descrição do domínio

O TelaScore é uma plataforma social voltada para cinéfilos e amantes do cinema.
Seu domínio envolve o registro, a organização e o compartilhamento de
experiências cinematográficas.

Os usuários podem consultar filmes, registrar avaliações e resenhas, organizar
obras em listas personalizadas e manter uma watchlist com os títulos que
pretendem assistir.

A plataforma também permite solicitar a inclusão de filmes que ainda não
estejam no catálogo. Essas solicitações são analisadas pelos administradores,
que podem aprová-las, rejeitá-las ou solicitar ajustes.

O aspecto social inclui recomendações de filmes entre usuários e comunidades
temáticas para interação entre pessoas com interesses semelhantes.

Para incentivar o engajamento, os usuários podem definir metas pessoais, como
assistir determinada quantidade de filmes dentro de um prazo, acompanhando o
progresso até sua conclusão.

O projeto está organizado em diferentes contextos de domínio:

- **Identidade:** cadastro, autenticação e gerenciamento de usuários.
- **Catálogo:** filmes, avaliações, listas, watchlist e solicitações.
- **Análise:** metas, recomendações, quizzes e recompensas.
- **Social:** comunidades, conexões e mensagens.
- **Informação:** notícias, eventos e calendário de estreias.

## 🗺️ Mapa de histórias do usuário

![Mapa de histórias do usuário](https://github.com/user-attachments/assets/747b329e-ab8a-48be-bec4-59d4eb23d782)

## 🎨 Protótipos

[Acessar os protótipos digitalizados](https://drive.google.com/drive/folders/1E7BqRoQI8SytqiydP5ptddj06PrGCgtM?usp=sharing)

## Tecnologias

- Java 17, Spring Boot e Maven
- React
- MySQL 8
- Docker Compose

## ▶️ Como executar

### Pré-requisito

Tenha o Docker Desktop instalado e em execução.

Na pasta onde está o arquivo `docker-compose.yml`, rode:

```powershell
docker compose up --build
```

Acesse:

- Aplicação: `http://localhost:3000`
- API: `http://localhost:8080`
- MySQL externo: `localhost:3307`

O Docker inicia automaticamente frontend, backend e MySQL. O banco
`telascore_db` e suas tabelas são criados na primeira execução.

### Parar

```powershell
docker compose down
```

### Recriar o banco

O comando abaixo remove todos os dados e executa novamente os scripts SQL:

```powershell
docker compose down -v
docker compose up --build
```

Os scripts de inicialização ficam em:

```text
docker/mysql/init
```

## Acesso inicial

É possível criar uma conta pela tela de login. Também existe o administrador:

```text
E-mail: admin@admin.com
Senha: admin123
```
