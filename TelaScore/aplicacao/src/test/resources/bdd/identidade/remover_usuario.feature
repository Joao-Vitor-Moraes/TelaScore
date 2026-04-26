# language: pt
Funcionalidade: Remoção de usuário
    Como administrador da plataforma
    Quero remover uma conta de usuário
    Para manter o controle dos usuários cadastrados

    Cenário: Administrador remove usuário existente
        Dado que existe um administrador logado para remoção com ID 10
        E existe um usuário alvo cadastrado para remoção com ID 1
        Quando solicito remover o usuário com ID 1
        Então a remoção deve ser realizada com sucesso
        E o perfil do usuário removido também deve ser removido

    Cenário: Não deve remover usuário inexistente
        Dado que existe um administrador logado para remoção com ID 10
        E não existe usuário alvo cadastrado para remoção com ID 99
        Quando solicito remover o usuário com ID 99
        Então a remoção deve ser rejeitada
        E deve retornar o erro da remoção "O usuário informado não existe"

    Cenário: Usuário comum não pode remover usuário
        Dado que existe um usuário comum logado para remoção com ID 20
        E existe um usuário alvo cadastrado para remoção com ID 1
        Quando solicito remover o usuário com ID 1
        Então a remoção deve ser rejeitada
        E deve retornar o erro da remoção "Apenas administradores podem remover usuários"

    Cenário: Não deve remover quando não há usuário logado
        Dado que não há usuário logado para remoção
        E existe um usuário alvo cadastrado para remoção com ID 1
        Quando solicito remover o usuário com ID 1
        Então a remoção deve ser rejeitada
        E deve retornar o erro da remoção "Usuário não está logado"
