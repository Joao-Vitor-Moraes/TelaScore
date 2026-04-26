# language: pt
Funcionalidade: Envio de Mensagens Privadas

  Cenário: Enviar uma mensagem válida com sucesso
    Dado que o usuário 1 quer enviar uma mensagem para o usuário 2
    Quando ele escreve "Olá, tudo bem?" e envia
    Então a mensagem deve ser registrada com sucesso
    E o status inicial deve ser "não lida"

  Cenário: Impedir envio de mensagem para si mesmo
    Dado que o usuário 1 tenta enviar uma mensagem para o usuário 1
    Quando ele escreve "Nota mental: comprar pipoca" e envia
    Então o sistema deve rejeitar o envio informando que não pode enviar para si mesmo

  Cenário: Impedir mensagem com conteúdo em branco
    Dado que o usuário 1 quer enviar uma mensagem para o usuário 2
    Quando ele tenta enviar uma mensagem sem texto
    Então o sistema deve rejeitar o envio informando que o conteúdo não pode estar em branco
  
  Cenário: Remover uma mensagem enviada com sucesso
    Dado que o usuário 1 enviou uma mensagem para o usuário 2 com o texto "Mensagem para deletar"
    Quando a mensagem é removida
    Então a mensagem deve deixar de existir no sistema