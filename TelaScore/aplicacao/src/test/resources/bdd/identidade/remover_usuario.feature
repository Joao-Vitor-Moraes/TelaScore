# language: pt
Funcionalidade: Remoção de usuário
    Como administrador da plataforma
    Quero remover uma conta de usuário
    Para manter o controle dos usuários cadastrados

    Cenário: Administrador remove usuário existente
        Dado que o administrador da remoção tem ID 10
        E existe um usuário alvo cadastrado com ID 1
        Quando o administrador da remoção com ID 10 solicita remover o usuário com ID 1
        Então a remoção deve ser realizada com sucesso

    Cenário: Não deve remover usuário inexistente
        Dado que o administrador da remoção tem ID 10
        E não existe usuário alvo cadastrado com ID 99
        Quando o administrador da remoção com ID 10 solicita remover o usuário com ID 99
        Então a remoção deve ser rejeitada
        E deve retornar o erro da remoção "O usuário informado não existe"

    Cenário: Usuário comum não pode remover usuário
        Dado que o usuário comum da remoção tem ID 20
        E existe um usuário alvo cadastrado com ID 1
        Quando o usuário da remoção com ID 20 solicita remover o usuário com ID 1
        Então a remoção deve ser rejeitada
        E deve retornar o erro da remoção "Apenas administradores podem remover usuários"

    Cenário: Não deve remover quando o administrador não existe
        Dado que não existe administrador da remoção com ID 10
        E existe um usuário alvo cadastrado com ID 1
        Quando o administrador da remoção com ID 10 solicita remover o usuário com ID 1
        Então a remoção deve ser rejeitada
        E deve retornar o erro da remoção "O usuário administrador não existe"
