# language: pt
Funcionalidade: Regras de Avaliação de Filmes
  Como um usuário cinéfilo
  Eu quero avaliar os filmes que assisti
  Para dar uma nota de 1 a 5 estrelas

  Cenário: Avaliar um filme com uma nota válida
    Dado que eu quero avaliar o filme "10" como o usuário 5
    Quando eu tento criar uma avaliação com a nota 4
    Então a avaliação deve ser criada com sucesso

  Cenário: Tentar avaliar um filme com uma nota fora do limite
    Dado que eu quero avaliar o filme "10" como o usuário 5
    Quando eu tento criar uma avaliação com a nota 6
    Então o sistema deve bloquear a criação informando que a nota é inválida