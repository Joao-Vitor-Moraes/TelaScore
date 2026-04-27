# language: pt
Funcionalidade: Solicitação de filmes

  Cenário: Usuário solicita um filme com sucesso
    Dado que o usuário "ana_lima" está autenticado
    Quando ela solicita o filme "Interestelar"
    Então a solicitação é registrada com sucesso

  Cenário: Usuário solicita um filme já existente
    Dado que o filme "Interestelar" já existe no catálogo
    Quando "ana_lima" tenta solicitá-lo
    Então a solicitação do filme é rejeitada
    E exibe a mensagem "Filme já cadastrado"

    Cenário: Administrador aprova uma solicitação pendente
    Dado que existe uma solicitação pendente para o filme "Inception"
    E o usuário "carlos_admin" é um "administrador"
    Quando "carlos_admin" avalia a solicitação como aprovada
    Então a solicitação deve constar como aprovada

  Cenário: Usuário comum tenta avaliar uma solicitação
    Dado que existe uma solicitação pendente para o filme "Inception"
    E o usuário "pedro_comum" é um "cinefilo"
    Quando "pedro_comum" tenta avaliar a solicitação como aprovada
    Então a solicitação do filme é rejeitada
    E exibe a mensagem "Apenas administradores podem avaliar solicitações de filmes."

  Cenário: Usuário cancela sua própria solicitação pendente
    Dado que existe uma solicitação pendente para o filme "Avatar"
    Quando o próprio solicitante cancela a solicitação
    Então a solicitação deve constar como cancelada

  Cenário: Usuário tenta cancelar solicitação de outra pessoa
    Dado que existe uma solicitação pendente para o filme "Avatar"
    Quando um usuário diferente tenta cancelar a solicitação
    Então a solicitação do filme é rejeitada
    E exibe a mensagem "Apenas o criador pode cancelar esta solicitação"

  Cenário: Administrador solicita ajustes na solicitação
    Dado que existe uma solicitação pendente para o filme "Avatar"
    E o usuário "admin" é um "administrador"
    Quando "admin" solicita ajustes com o feedback "Faltou o ano de lançamento"
    Então a solicitação deve constar como aguardando ajustes
    E o feedback salvo na solicitação deve ser "Faltou o ano de lançamento"