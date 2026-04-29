# language: pt
Funcionalidade: Gerenciamento de filmes
  Como usuário do sistema
  Quero cadastrar e gerenciar filmes
  Para manter o catálogo atualizado

  Cenário: Cadastrar um filme com todos os dados válidos
    Dado que eu quero cadastrar um filme com o título "Matrix" lançado em 1999
    E que o filme tem o diretor de id 1
    Quando eu tento criar o filme
    Então o filme deve ser criado com sucesso
    E o filme deve ter o título "Matrix"
    E o filme deve ter o ano de lançamento 1999
    E o filme deve ter pelo menos um diretor

  Cenário: Cadastrar um filme com sinopse
    Dado que eu quero cadastrar um filme com o título "Interestelar" lançado em 2014
    E que o filme tem o diretor de id 2
    E que o filme tem a sinopse "Um grupo de astronautas viaja pelo universo."
    Quando eu tento criar o filme
    Então o filme deve ser criado com sucesso
    E a sinopse do filme deve ser "Um grupo de astronautas viaja pelo universo."

  Cenário: Não deve ser possível cadastrar um filme sem título
    Dado que eu quero cadastrar um filme com o título "" lançado em 2000
    E que o filme tem o diretor de id 1
    Quando eu tento criar o filme
    Então o sistema deve bloquear a criação do filme informando que o título é inválido

  Cenário: Não deve ser possível cadastrar um filme com sinopse em branco
    Dado que eu quero cadastrar um filme com o título "Duna" lançado em 2021
    E que o filme tem o diretor de id 3
    E que o filme tem a sinopse ""
    Quando eu tento criar o filme
    Então o sistema deve bloquear a criação do filme informando que a sinopse é inválida

  Cenário: Atualizar o título de um filme já cadastrado
    Dado que existe um filme cadastrado com o título "Matrixx" lançado em 1999 com o diretor de id 1
    Quando eu atualizo o título do filme para "Matrix"
    Então o filme deve ter o título "Matrix"

  Cenário: Atualizar o ano de lançamento de um filme já cadastrado
    Dado que existe um filme cadastrado com o título "Matrix" lançado em 1998 com o diretor de id 1
    Quando eu atualizo o ano de lançamento para 1999
    Então o filme deve ter o ano de lançamento 1999

  Cenário: Não deve ser possível atualizar o título de um filme para vazio
    Dado que existe um filme cadastrado com o título "Matrix" lançado em 1999 com o diretor de id 1
    Quando eu atualizo o título do filme para ""
    Então o sistema deve bloquear a atualização do filme informando que o título é inválido

  Cenário: Adicionar um segundo diretor ao filme
    Dado que existe um filme cadastrado com o título "Matrix" lançado em 1999 com o diretor de id 1
    Quando eu adiciono o diretor de id 2 ao filme
    Então o filme deve ter pelo menos um diretor