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

### Opção 1 — Docker (recomendado)

Pré-requisito: Docker Desktop instalado e em execução.

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

#### Parar

```powershell
docker compose down
```

#### Recriar o banco

```powershell
docker compose down -v
docker compose up --build
```

Os scripts de inicialização ficam em:

```text
docker/mysql/init
```

---

### Opção 2 — Sem Docker (local)

Pré-requisitos: Java 17, Maven, Node.js e MySQL 8 instalados localmente.

#### 1. Banco de dados

Crie o banco e as tabelas via MySQL (ou MySQL Workbench):

```sql
CREATE DATABASE IF NOT EXISTS telascore_db;
```

> As tabelas são criadas automaticamente pelo Hibernate na primeira
> inicialização do backend (`hibernate.hbm2ddl.auto=update`).

#### 2. Backend

Abra um terminal **na pasta raiz do projeto** (onde está o `docker-compose.yml`) e rode tudo no mesmo terminal:

```powershell
# 1. Variáveis de ambiente (substitua a senha)
$env:DB_URL = "jdbc:mysql://localhost:3306/telascore_db?useSSL=false&serverTimezone=UTC&allowPublicKeyRetrieval=true"
$env:DB_USER = "root"
$env:DB_PASSWORD = "sua_senha_aqui"
$env:TELASCORE_TOKEN_SECRET = "TelaScore-token-dev-secret"

# 2. Build completo (apenas na primeira vez ou após mudanças nos módulos)
cd TelaScore
mvn clean install "-Dmaven.test.skip=true"

# 3. Subir o backend
cd apresentacao-backend
mvn spring-boot:run "-Dmaven.test.skip=true"
```

A API estará disponível em `http://localhost:8080`.

#### 3. Frontend

Em outro terminal:

```powershell
cd TelaScore\apresentacao-frontend\telascore-frontend
npm install   # apenas na primeira vez
npm start
```

A aplicação abrirá em `http://localhost:3000`.

## Acesso inicial

É possível criar uma conta pela tela de login. Também existe o administrador:

```text
E-mail: admin@admin.com
Senha: admin123
```

## Padrões de Projeto

| Integrante | Padrão | Funcionalidade | Classes |
|---|---|---|---|
| João Vitor Moraes | Iterator | Lista | `IteradorDeItens`, `IteradorSequencialDeItens`, `IteradorRanqueadoDeItens`, `Lista` (dominio-catalogo) · `ConsultarItensListaCasoDeUso` (aplicacao) |
| Ester Carvalho | — | — | — |
| Célio Pereira | — | — | — |
| Guilherme Vinicius | — | — | — |
| Fátima Beatriz Moraes | — | — | — |
| Caio Almeida | — | — | — |
| Gabriel Reis | — | — | — |
| Rodrigo Souza | — | — | — |
