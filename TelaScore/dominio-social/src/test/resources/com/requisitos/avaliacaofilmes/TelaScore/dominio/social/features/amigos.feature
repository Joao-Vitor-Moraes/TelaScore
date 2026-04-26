# language: pt
Funcionalidade: Sistema de amigos
  Como usuário da plataforma
  Quero seguir e deixar de seguir outros usuários
  Para acompanhar as atividades cinematográficas de pessoas que me interessam

  Cenário: Usuário adiciona amigo
    Dado que "ana_lima" está autenticada com ID 1
    Quando ela segue o usuário com ID 2
    Então a conexão deve ser criada com sucesso

  Cenário: Usuário adiciona a si mesmo
    Dado que "ana_lima" está autenticada com ID 1
    Quando ela tenta seguir a si mesma com ID 1
    Então o sistema rejeita a operação
    E retorna o erro "Um utilizador não pode seguir a si próprio"

  Cenário: Usuário tenta seguir alguém que já segue
    Dado que "ana_lima" está autenticada com ID 1
    E já existe uma conexão entre o usuário 1 e o usuário 2
    Quando ela tenta seguir novamente o usuário com ID 2
    Então o sistema rejeita a operação
    E retorna o erro de conexão "O utilizador já segue este perfil."

  Cenário: Usuário deixa de seguir outro usuário
    Dado que "ana_lima" está autenticada com ID 1
    E já existe uma conexão entre o usuário 1 e o usuário 2
    Quando ela deixa de seguir o usuário com ID 2
    Então a conexão deve ser removida com sucesso