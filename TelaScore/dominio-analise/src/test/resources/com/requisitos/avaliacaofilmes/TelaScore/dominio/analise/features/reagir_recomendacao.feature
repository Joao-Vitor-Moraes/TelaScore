# language: pt
Funcionalidade: Reagir a recomendações recebidas
  Como usuário que recebe indicações
  Quero poder aceitar ou rejeitar recomendações
  Para organizar os filmes que pretendo assistir

  Cenário: Aceitar uma recomendação pendente com sucesso
    Dado que o usuário com ID 2 possui uma recomendação pendente do filme "FILME-001"
    Quando ele reage aceitando a recomendação
    Então a recomendação deve ser atualizada com sucesso
    E o status da recomendação deve mudar para "ACEITA"

  Cenário: Não deve ser possível aceitar uma recomendação já rejeitada
    Dado que o usuário com ID 2 possui uma recomendação do filme "FILME-001" que está com status "REJEITADA"
    Quando ele tenta reagir aceitando a recomendação
    Então o sistema rejeita a operação
    E retorna o erro "Não é possível aceitar uma recomendação que já foi rejeitada."