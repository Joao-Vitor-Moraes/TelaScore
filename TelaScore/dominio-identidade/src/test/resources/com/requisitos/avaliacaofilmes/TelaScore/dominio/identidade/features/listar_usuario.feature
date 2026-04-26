# language: pt
Funcionalidade: Listar usuários
    Como sistema
    Quero listar usuários cadastrados
    Para consultar as contas existentes

    Cenário: Listar usuários cadastrados
        Dado que existem usuários cadastrados no repositório
        Quando solicito a listagem de usuários
        Então devem ser retornados 2 usuários cadastrados

    Cenário: Listar usuários quando não há cadastros
        Dado que não existem usuários cadastrados no repositório
        Quando solicito a listagem de usuários
        Então deve ser retornada uma lista vazia
