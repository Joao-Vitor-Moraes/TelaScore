# language: pt
Funcionalidade: Listar usuários
    Como administrador da plataforma
    Quero listar todos os usuários cadastrados
    Para visualizar as contas existentes

    Cenário: Administrador lista todos os usuários
        Dado que existe um administrador logado para listagem com ID 10
        E existem usuários cadastrados na plataforma para listagem
        Quando solicito a listagem de usuários
        Então a listagem deve ser realizada com sucesso
        E devem ser retornados 2 usuários

    Cenário: Usuário comum não pode listar usuários
        Dado que existe um usuário comum logado para listagem com ID 20
        E existem usuários cadastrados na plataforma para listagem
        Quando solicito a listagem de usuários
        Então a listagem deve ser rejeitada
        E deve retornar o erro da listagem "Apenas administradores podem listar usuários"

    Cenário: Não deve listar quando não há usuário logado
        Dado que não há usuário logado para listagem
        Quando solicito a listagem de usuários
        Então a listagem deve ser rejeitada
        E deve retornar o erro da listagem "Usuário não está logado"
