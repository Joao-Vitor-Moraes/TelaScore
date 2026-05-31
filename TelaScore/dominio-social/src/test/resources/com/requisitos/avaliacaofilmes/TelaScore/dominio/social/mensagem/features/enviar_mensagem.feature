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

# --- NOVOS CENÁRIOS: GERENCIAMENTO DE FIGURINHAS ---

  Cenário: Adicionar figurinha no sistema com sucesso
    Dado que o administrador deseja adicionar uma figurinha com o nome "Pipoca" e imagem "pipoca.png"
    Quando a figurinha é cadastrada no sistema
    Então a figurinha deve ser registrada com sucesso

  Cenário: Enviar uma figurinha com sucesso na mensagem privada
    Dado que existe uma figurinha cadastrada com o nome "Cérebro Explodindo"
    E que o usuário 1 quer enviar uma mensagem para o usuário 2
    Quando ele seleciona e envia a figurinha "Cérebro Explodindo"
    Então a mensagem contendo a figurinha deve ser enviada com sucesso

  Cenário: Excluir uma figurinha do sistema com sucesso
    Dado que existe uma figurinha cadastrada com o nome "Choro Filmes"
    Quando o administrador exclui a figurinha "Choro Filmes"
    Então a figurinha deve deixar de existir no sistema