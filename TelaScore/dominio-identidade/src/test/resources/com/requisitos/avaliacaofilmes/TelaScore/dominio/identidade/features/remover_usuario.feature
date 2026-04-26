# language: pt
Funcionalidade: Remover usuário
    Como administrador da plataforma
    Quero remover um usuário
    Para manter apenas contas válidas no sistema

    Cenário: Remover usuário existente
        Dado que existe um usuário cadastrado com ID 1
        Quando solicito a remoção do usuário com ID 1
        Então o usuário deve ser removido com sucesso

    Cenário: Não deve remover usuário inexistente
        Dado que não existe usuário cadastrado com ID 99
        Quando solicito a remoção do usuário com ID 99
        Então o sistema deve bloquear a remoção informando que o usuário não existe
