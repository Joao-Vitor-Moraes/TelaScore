# language: pt
Funcionalidade: Gerenciar eventos do sistema
    Como usuário
    Quero agendar e cancelar meus eventos
    Para gerenciar minhas atividades dentro da plataforma

    Cenário: Agendar um novo evento
        Dado que o usuário deseja agendar um novo evento com ID 1
        Quando o usuário agenda o evento
        Então o evento deve ser salvo com sucesso

    Cenário: Cancelar um evento existente
        Dado que existe um evento agendado com ID 1
        Quando o usuário cancela o evento agendado
        Então o evento deve ser removido com sucesso
