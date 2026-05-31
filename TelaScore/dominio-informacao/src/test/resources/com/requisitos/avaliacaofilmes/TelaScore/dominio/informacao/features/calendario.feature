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

    Cenário: Remover filme do calendário
        Dado que existe um calendário cadastrado para o usuário 10
        E o usuário registra o filme 101 no calendário para a data "2026-12-01"
        Quando o usuário remove o filme 101 do calendário
        Então o calendário do usuário não deve conter o filme 101

    Cenário: Notificar o observador quando a estreia chega
        Dado que existe um calendário cadastrado para o usuário 10
        E o usuário registra o filme 101 no calendário para a data "2026-12-01"
        E um observador de lembretes está inscrito no calendário do usuário
        Quando o sistema dispara os lembretes do dia "2026-12-01"
        Então o observador deve ser notificado sobre o filme 101

    Cenário: Não notificar quando o lembrete está desativado
        Dado que existe um calendário cadastrado para o usuário 10
        E o usuário registra o filme 101 no calendário para a data "2026-12-01"
        E o lembrete do filme 101 está desativado
        E um observador de lembretes está inscrito no calendário do usuário
        Quando o sistema dispara os lembretes do dia "2026-12-01"
        Então o observador não deve ser notificado sobre o filme 101
