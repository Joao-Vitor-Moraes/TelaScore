# language: pt
Funcionalidade: Reagir a recomendações recebidas
  Como usuário que recebe indicações
  Quero poder aceitar ou rejeitar recomendações de filmes e quizzes
  Para organizar os conteúdos que pretendo acessar

  Cenário: Aceitar uma recomendação pendente com sucesso
    Dado que o usuário com ID 2 possui uma recomendação pendente do filme "FILME-001"
    Quando ele reage aceitando a recomendação
    Então a recomendação deve ser atualizada com sucesso
    E o status da recomendação deve mudar para "ACEITA"

  Cenário: Não deve ser possível aceitar uma recomendação já rejeitada
    Dado que o usuário com ID 2 possui uma recomendação do filme "FILME-001" que está com status "REJEITADA"
    Quando ele tenta reagir aceitando a recomendação
    Então o sistema rejeita a recomendação
    E a recomendação retorna o erro "Não é possível aceitar uma recomendação que já foi rejeitada."

  Cenário: Aceitar uma recomendação pendente de um Quiz
    Dado que o usuário com ID 2 possui uma recomendação pendente do quiz "QUIZ-005"
    Quando ele reage aceitando a recomendação
    Então a recomendação deve ser atualizada com sucesso
    E o status da recomendação deve mudar para "ACEITA"