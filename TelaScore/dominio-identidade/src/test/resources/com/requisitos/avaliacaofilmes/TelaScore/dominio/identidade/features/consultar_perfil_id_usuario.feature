# language: pt
Funcionalidade: Consultar perfil pelo usuário
    Como sistema
    Quero consultar o perfil pelo ID do usuário
    Para obter o perfil associado ao usuário

    Cenário: Consultar perfil existente pelo ID do usuário
        Dado que existe um perfil cadastrado para o usuário 10
        Quando pesquiso o perfil do usuário 10
        Então o perfil deve ser encontrado com sucesso
        E o ID do usuário dono do perfil encontrado deve ser 10

    Cenário: Consultar perfil inexistente pelo ID do usuário
        Dado que não existe perfil cadastrado para o usuário 99
        Quando pesquiso o perfil do usuário 99
        Então o sistema deve informar que o perfil não foi encontrado
