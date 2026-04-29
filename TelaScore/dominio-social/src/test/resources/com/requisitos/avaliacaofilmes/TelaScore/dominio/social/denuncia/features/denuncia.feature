# language: pt
Funcionalidade: Denúncia de conteúdo inadequado

  Cenário: Registrar denúncia válida de mensagem
    Dado que o usuário 1 deseja denunciar a mensagem 10
    Quando ele registra uma denúncia pelo motivo "OFENSIVO" com a descrição "Mensagem ofensiva"
    Então a denúncia deve ser criada com status "PENDENTE"

  Cenário: Impedir denúncia sem descrição
    Dado que o usuário 1 deseja denunciar a mensagem 10
    Quando ele tenta registrar uma denúncia sem descrição
    Então o sistema deve rejeitar a denúncia informando que a descrição não pode estar em branco

  Cenário: Impedir denúncia duplicada do mesmo usuário para o mesmo alvo
    Dado que o usuário 1 já denunciou a mensagem 10
    Quando ele registra uma denúncia pelo motivo "SPAM" com a descrição "Repetida"
    Então o sistema deve rejeitar a denúncia informando que o usuário já denunciou este alvo

  Cenário: Aceitar denúncia pendente
    Dado que existe uma denúncia pendente
    Quando a denúncia é aceita pela moderação
    Então a denúncia deve ficar com status "ACEITA"

  Cenário: Rejeitar denúncia pendente
    Dado que existe uma denúncia pendente
    Quando a denúncia é rejeitada pela moderação
    Então a denúncia deve ficar com status "REJEITADA"
