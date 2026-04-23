# language: pt
Funcionalidade: Gerenciar listas

  Cenário: Usuário cria uma lista com sucesso
    Dado que o usuário "ana_lima" com ID 1 quer criar uma lista
    Quando ela cria uma lista chamada "Favoritos"
    Então a lista é criada com sucesso
    E pertence ao usuário 1

  Cenário: Usuário tenta criar lista sem nome
    Dado que o usuário "ana_lima" com ID 1 quer criar uma lista
    Quando ela tenta criar uma lista sem nome
    Então o sistema rejeita a operação
    E retorna o erro "O título não pode estar em branco"