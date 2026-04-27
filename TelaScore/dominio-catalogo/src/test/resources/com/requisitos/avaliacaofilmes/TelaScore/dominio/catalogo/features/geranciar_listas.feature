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

    Cenário: Adicionar um filme à lista com sucesso
    Dado que a usuária "ana_lima" com ID 1 tem uma lista criada
    Quando ela adiciona o filme com ID 5 à lista
    Então a lista deve conter 1 item

  Cenário: Tentar adicionar um filme repetido na lista
    Dado que a usuária "ana_lima" com ID 1 tem uma lista criada
    E o filme com ID 5 já está na lista
    Quando ela tenta adicionar o filme com ID 5 novamente
    Então o sistema rejeita a operação
    E retorna o erro "O filme já existe nesta lista"

Cenário: Reordenar filmes numa lista ranqueada
    Dado que a usuária "ana_lima" com ID 1 tem uma lista ranqueada criada
    E ela adicionou o filme com ID 10 na lista
    E ela adicionou o filme com ID 20 na lista
    E ela adicionou o filme com ID 30 na lista
    Quando ela move o filme com ID 30 para a posição 1
    Então o filme com ID 30 deve estar na posição 1
    E o filme com ID 10 deve estar na posição 2

  Cenário: Tentar reordenar numa lista não ranqueada
    Dado que a usuária "ana_lima" com ID 1 tem uma lista criada
    E ela adicionou o filme com ID 10 na lista
    Quando ela tenta mover o filme com ID 10 para a posição 1
    Então o sistema rejeita a operação
    E retorna o erro "Apenas listas ranqueadas permitem reordenação manual"

  Cenário: Lista Colaborativa permite que outro usuário adicione filmes
    Dado que a usuária "ana_lima" com ID 1 tem uma lista criada
    E que a usuária "ana_lima" tornou a lista colaborativa
    E adicionou o usuário com ID 2 como colaborador
    Quando o usuário com ID 2 adiciona o filme com ID 99 à lista
    Então a lista deve conter 1 item

  Cenário: Lista normal não aceita filmes não assistidos
    Dado que a usuária "ana_lima" com ID 1 tem uma lista criada
    Quando ela tenta adicionar o filme com ID 99 à lista informando que não assistiu
    Então o sistema rejeita a operação
    E retorna o erro "Listas normais só podem conter filmes que você já assistiu"

  Cenário: Watchlist auto-remove filme ao ser assistido
    Dado que a usuária "ana_lima" com ID 1 tem uma watchlist criada
    E ela adicionou o filme com ID 50 à watchlist
    Quando ela registra que assistiu ao filme com ID 50
    Então a lista deve conter 0 item