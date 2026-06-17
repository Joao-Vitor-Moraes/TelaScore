# language: pt
Funcionalidade: Listar usuarios
    Como sistema
    Quero listar usuarios cadastrados
    Para consultar as contas existentes

    Cenario: Listar usuarios cadastrados
        Dado que existem 2 usuarios cadastrados no repositorio
        Quando solicito a listagem de usuarios no dominio
        Entao devem ser retornados 2 usuarios cadastrados

    Cenario: Listar usuarios quando nao ha cadastros
        Dado que nao existem usuarios cadastrados no repositorio
        Quando solicito a listagem de usuarios no dominio
        Entao deve ser retornada uma lista vazia
