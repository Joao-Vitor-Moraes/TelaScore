# 🎬 TelaScore
Sistema de gerenciamento de avaliações de filmes inspirado em plataformas como Letterboxd, permitindo que cinéfilos registrem, organizem e compartilhem suas experiências cinematográficas.

## 🌎 Descrição do Domínio (Linguagem Onipresente)
O domínio está relacionado às atividades de cinéfilos e amantes de cinema que buscam registrar, organizar e compartilhar suas experiências ao assistir filmes. O consumo de obras cinematográficas é uma prática comum entre pessoas interessadas na arte do cinema, que frequentemente desejam lembrar quais filmes assistiram, expressar suas opiniões sobre essas obras e descobrir novos conteúdos que possam ser de seu interesse.

Nesse contexto, cinéfilos assistem a filmes, que são obras cinematográficas com informações como título, data de lançamento, gênero, elenco, direção e outras informações relevantes. O grande volume de produções existentes faz com que seja difícil acompanhar tudo o que já foi assistido ou identificar facilmente novos filmes que possam despertar interesse.

Após assistir a um filme, muitas pessoas sentem vontade de registrar suas opiniões e impressões sobre a obra, para trocar experiências com outros fãs. Isso pode ocorrer por meio de avaliações escritas, nas quais o espectador expressa sua satisfação e, em alguns casos, descreve sua percepção crítica sobre o filme. Essas opiniões individuais contribuem para formar uma percepção coletiva sobre as obras, ajudando outros cinéfilos a identificar filmes bem avaliados ou que geram discussões relevantes.

Além de registrar opiniões, cinéfilos frequentemente buscam organizar filmes de acordo com seus próprios critérios, como preferências pessoais, gêneros favoritos ou temas específicos. Essa organização pode ocorrer por meio de coleções ou lista pessoais, que representam grupos de filmes associados a determinados pontos de vista, como reunir obras favoritas, registrar recomendações ou planejar filmes que se deseja assistir futuramente.

Outro aspecto comum neste domínio é a busca pela descoberta de novos filmes. Diante da grande quantidade de produções cinematográficas disponíveis, muitos cinéfilos enfrentam dificuldades para encontrar obras que correspondam aos seus maiores gostos pessoais. Então, recomendações baseadas em preferências, avaliações e interesses individuais tornam-se um elemento importante para facilitar a descoberta de novos conteúdos.

A experiência relacionada ao cinema também possui um forte componente social. Cinéfilos costumam compartilhar opiniões, discutir interpretações e trocar experiências e ideias com outras pessoas que possuem interesses semelhantes. Essas interações contribuem para a formação de grupos de discussão, nas quais diferentes perspectivas sobre filmes podem ser apresentadas e debatidas.

Além de registrar e organizar filmes, muitos cinéfilos buscam acompanhar o lançamento de novas obras cinematográficas. O cinema é um campo em constante renovação, com produções sendo anunciadas, lançadas e discutidas ao longo do tempo. Para pessoas interessadas nesse universo, é importante acompanhar datas de estreia, anúncios de novos filmes e outras informações relacionadas a futuros lançamentos, permitindo que planejem quais obras desejam assistir quando estiverem disponíveis.

Além disso, muitos amantes de cinema demonstram interesse em testar e expandir seus conhecimentos sobre filmes, diretores, atores e momentos marcantes da história do cinema. Esse interesse frequentemente se manifesta em desafios que permitem aos participantes avaliar o quanto sabem sobre algum universo cinematográfico e aprender novos fatos sobre essa área.

## 🗺️ Mapa de Histórias do Usuário
<img width="3782" height="1121" alt="Mapa_de_historias_do_usuario" src="https://github.com/user-attachments/assets/747b329e-ab8a-48be-bec4-59d4eb23d782" />

## 🎨 Protótipos
[Link dos Protótipos digitalizados](https://drive.google.com/drive/folders/1E7BqRoQI8SytqiydP5ptddj06PrGCgtM?usp=sharing)

## ▶️ Como Rodar

### Pré-requisitos

- Java 17
- Maven 3.8+
- Node.js 18+
- MySQL 8+ rodando em `localhost:3306`

### Banco de dados

Crie o banco antes de subir o backend:

```sql
CREATE DATABASE telascore_db;
```

As tabelas são criadas automaticamente pelo Hibernate na primeira execução.

> **Atenção (banco já existente):** Se o banco foi criado em uma versão anterior do projeto, o Hibernate pode falhar ao tentar alterar tabelas com mudanças de chave primária (ex: `membro_comunidade`). Nesse caso, drop e recrie o banco:
> ```sql
> DROP DATABASE telascore_db;
> CREATE DATABASE telascore_db;
> ```
> Depois suba o backend normalmente — ele recria tudo.

> **Atenção:** Nunca rode `DROP DATABASE` para reiniciar o MySQL — isso apaga todos os dados. Para reiniciar o serviço sem perder nada, use `net stop MySQL80` / `net start MySQL80` ou reinicie pelo painel de serviços do Windows (`services.msc`).

### Backend

**Compilar** (rodar dentro da pasta `TelaScore/TelaScore`):

```powershell
cd "C:\Users\Joao Vitor\Downloads\TelaScore\TelaScore"
mvn install "-Dmaven.test.skip=true"
```

> Use `-Dmaven.test.skip=true` entre aspas no PowerShell. Não use `-DskipTests` — há um erro de compilação pré-existente em testes de integração.

**Subir** (em um terminal separado do frontend — os dois ficam rodando ao mesmo tempo):

```powershell
$env:DB_USER = "root"
$env:DB_PASSWORD = "sua_senha"
$env:DB_URL = "jdbc:mysql://localhost:3306/telascore_db?useSSL=false&allowPublicKeyRetrieval=true&serverTimezone=UTC"

mvn spring-boot:run -pl apresentacao-backend "-Dspring-devtools.restart.enabled=false" "-Dmaven.test.skip=true"
```

Backend sobe em `http://localhost:8080`.

### Frontend

Abra um **novo terminal** (o terminal do backend precisa continuar aberto):

```powershell
cd TelaScore\apresentacao-frontend\telascore-frontend
npm install   # apenas na primeira vez
npm start
```

Frontend sobe em `http://localhost:3000`.

### Parar o projeto

- **Backend:** `Ctrl + C` no terminal onde o `mvn spring-boot:run` está rodando
- **Frontend:** `Ctrl + C` no terminal onde o `npm start` está rodando
- **MySQL:** não precisa parar — fica rodando em segundo plano como serviço do Windows

### IDs de usuário hardcoded no frontend

O frontend não tem tela de login. Os IDs estão fixos no código:

| Constante | Valor | Papel |
|-----------|-------|-------|
| `USUARIO_ID` | 3 | Cinéfilo (páginas de listas e solicitações) |
| `ADM_ID` | 2 | Admin (página de avaliação de solicitações) |

O admin (ID 2, `admin@admin.com` / `admin123`) é criado automaticamente pelo backend a cada startup.

O cinéfilo (ID 3) precisa ser criado manualmente se o banco for resetado:

```bash
curl -X POST http://localhost:8080/api/identidade/usuario/registrar \
  -H "Content-Type: application/json" \
  -d '{"nome":"Usuario Teste","email":"usuario@teste.com","senha":"teste123"}'
```
