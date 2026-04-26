# language: pt
Funcionalidade: Consultar perfil pelo usuário
    Como sistema
    Quero consultar o perfil pelo ID do usuário
    Para obter o perfil associado ao usuário

    Cenário: Consultar perfil existente pelo ID do usuário
        Dado que existe um perfil cadastrado para o usuário 10
        Quando solicito a consulta do perfil do usuário 10
        Então a consulta do perfil deve ser realizada com sucesso
        E o perfil retornado deve pertencer ao usuário 10

    Cenário: Consultar perfil inexistente pelo ID do usuário
        Dado que não existe perfil cadastrado para o usuário 99
        Quando solicito a consulta do perfil do usuário 99
        Então a consulta do perfil deve retornar nenhum resultado
