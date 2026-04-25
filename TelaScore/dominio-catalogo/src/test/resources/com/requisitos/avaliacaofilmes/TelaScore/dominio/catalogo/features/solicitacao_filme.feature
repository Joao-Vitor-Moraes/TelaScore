# language: pt
Funcionalidade: Solicitação de filmes

  Cenário: Usuário solicita um filme com sucesso
    Dado que o usuário "ana_lima" está autenticado
    Quando ela solicita o filme "Interestelar"
    Então a solicitação é registrada com sucesso

  Cenário: Usuário solicita um filme já existente
    Dado que o filme "Interestelar" já existe no catálogo
    Quando "ana_lima" tenta solicitá-lo
    Então o sistema rejeita a operação
    E retorna o erro "Filme já cadastrado"