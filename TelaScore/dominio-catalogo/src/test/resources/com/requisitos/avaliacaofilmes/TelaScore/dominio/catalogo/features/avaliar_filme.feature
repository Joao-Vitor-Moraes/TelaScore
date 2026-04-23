# language: pt
Funcionalidade: Sistema de review de filmes
  Como usuário autenticado
  Quero escrever avaliações sobre filmes
  Para compartilhar minha opinião com outros usuários

  Cenário: Registrar uma avaliação com nota válida
    Dado que eu quero avaliar o filme "FILME-001" como o usuário 10
    Quando eu tento criar uma avaliação com a nota 4
    Então a avaliação deve ser criada com sucesso

  Cenário: Não deve ser possível registrar uma avaliação com nota fora do intervalo
    Dado que eu quero avaliar o filme "FILME-001" como o usuário 10
    Quando eu tento criar uma avaliação com a nota 6
    Então o sistema deve bloquear a criação informando que a nota é inválida