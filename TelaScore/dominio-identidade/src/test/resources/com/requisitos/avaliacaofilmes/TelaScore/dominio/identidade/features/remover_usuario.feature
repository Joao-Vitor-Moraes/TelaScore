# language: pt
Funcionalidade: Remover usuario
    Como sistema
    Quero remover um usuario
    Para manter apenas contas validas no repositorio

    Cenario: Remover usuario existente
        Dado que existe um usuario cadastrado com ID 1
        Quando solicito a remocao do usuario com ID 1 no dominio
        Entao o usuario deve ser removido com sucesso

    Cenario: Nao deve remover usuario inexistente
        Dado que nao existe usuario cadastrado com ID 99
        Quando solicito a remocao do usuario com ID 99 no dominio
        Entao o sistema deve bloquear a remocao com erro "O usuario informado nao existe"
