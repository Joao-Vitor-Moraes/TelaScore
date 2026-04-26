# language: pt
Funcionalidade: Gerenciar calendário de estreias
    Como usuário
    Quero gerenciar meu calendário de filmes
    Para acompanhar as datas de estreia que me interessam

    Cenário: Registrar filme em um calendário existente
        Dado que existe um calendário cadastrado para o usuário 10
        Quando o usuário registra o filme 101 no calendário para a data "2026-12-01"
        Então o filme 101 deve estar no calendário do usuário

    Cenário: Tentar registrar filme sem calendário existente
        Dado que não existe calendário cadastrado para o usuário 99
        Quando o usuário registra o filme 102 no calendário para a data "2026-12-01"
        Então o calendário do usuário não deve conter o filme 102