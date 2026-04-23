# language: pt
Funcionalidade: Gerenciamento de filmes
  Como usuário do sistema
  Quero cadastrar e gerenciar filmes
  Para manter o catálogo atualizado

  Cenário: Cadastrar um filme com dados válidos
    Dado que eu quero cadastrar um filme com o título "Matrix" lançado em 1999
    E que o filme tem o diretor de id 1
    Quando eu tento criar o filme
    Então o filme deve ser criado com sucesso
    E o filme deve ter o título "Matrix"

  Cenário: Não deve ser possível cadastrar um filme sem título
    Dado que eu quero cadastrar um filme com o título "" lançado em 2000
    E que o filme tem o diretor de id 1
    Quando eu tento criar o filme
    Então o sistema deve bloquear a criação do filme informando que o título é inválido