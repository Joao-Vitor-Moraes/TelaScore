# language: pt
Funcionalidade: Listar usuários
    Como administrador da plataforma
    Quero listar todos os usuários cadastrados
    Para visualizar as contas existentes

    Cenário: Administrador lista todos os usuários
        Dado que o usuário com ID 10 é um administrador
        E existem usuários cadastrados na plataforma
        Quando o administrador com ID 10 solicita a listagem de usuários
        Então a listagem deve ser realizada com sucesso
        E devem ser retornados 2 usuários

    Cenário: Usuário comum não pode listar usuários
        Dado que o usuário com ID 20 é um cinefilo
        E existem usuários cadastrados na plataforma
        Quando o usuário com ID 20 solicita a listagem de usuários
        Então a listagem deve ser rejeitada
        E deve retornar o erro da listagem "Apenas administradores podem listar usuários"

    Cenário: Não deve listar quando o administrador não existe
        Dado que não existe usuário cadastrado com ID 10
        Quando o administrador com ID 10 solicita a listagem de usuários
        Então a listagem deve ser rejeitada
        E deve retornar o erro da listagem "O usuário administrador não existe"
