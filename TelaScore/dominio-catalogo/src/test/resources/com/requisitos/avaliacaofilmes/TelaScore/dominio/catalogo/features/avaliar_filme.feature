# language: pt
Funcionalidade: Sistema de review de filmes
  Como usuário autenticado
  Quero escrever avaliações sobre filmes
  Para compartilhar minha opinião com outros usuários

  Cenário: Registrar uma avaliação com nota mínima válida
    Dado que eu quero avaliar o filme "FILME-001" como o usuário 10
    Quando eu tento criar uma avaliação com a nota 1 e a resenha "Não gostei, mas assisti até o fim."
    Então a avaliação deve ser criada com sucesso
    E a nota registrada deve ser 1
    E a resenha registrada deve ser "Não gostei, mas assisti até o fim."
    E a data da avaliação deve ser a data de hoje

  Cenário: Registrar uma avaliação com nota máxima válida
    Dado que eu quero avaliar o filme "FILME-001" como o usuário 10
    Quando eu tento criar uma avaliação com a nota 5 e a resenha "Obra-prima absoluta!"
    Então a avaliação deve ser criada com sucesso
    E a nota registrada deve ser 5

  Cenário: Registrar uma avaliação sem resenha
    Dado que eu quero avaliar o filme "FILME-002" como o usuário 20
    Quando eu tento criar uma avaliação com a nota 3 e sem resenha
    Então a avaliação deve ser criada com sucesso
    E a nota registrada deve ser 3
    E a resenha deve estar vazia

  Cenário: Não deve ser possível registrar uma avaliação com nota abaixo do mínimo
    Dado que eu quero avaliar o filme "FILME-001" como o usuário 10
    Quando eu tento criar uma avaliação com a nota 0 e a resenha "Nota inválida."
    Então o sistema deve bloquear a criação informando que a nota é inválida

  Cenário: Não deve ser possível registrar uma avaliação com nota acima do máximo
    Dado que eu quero avaliar o filme "FILME-001" como o usuário 10
    Quando eu tento criar uma avaliação com a nota 6 e a resenha "Nota inválida."
    Então o sistema deve bloquear a criação informando que a nota é inválida

  Cenário: Atualizar a nota de uma avaliação já existente
    Dado que existe uma avaliação criada com a nota 3 e a resenha "Achei mediano."
    Quando eu atualizo a nota para 5
    Então a nota registrada deve ser 5
    E a resenha registrada deve ser "Achei mediano."

  Cenário: Atualizar a resenha de uma avaliação já existente
    Dado que existe uma avaliação criada com a nota 4 e a resenha "Muito bom."
    Quando eu atualizo a resenha para "Revi e achei ainda melhor do que lembrava."
    Então a resenha registrada deve ser "Revi e achei ainda melhor do que lembrava."
    E a nota registrada deve ser 4

  Cenário: Não deve ser possível atualizar uma avaliação com nota inválida
    Dado que existe uma avaliação criada com a nota 3 e a resenha "Mediano."
    Quando eu atualizo a nota para 0
    Então o sistema deve bloquear a atualização informando que a nota é inválida