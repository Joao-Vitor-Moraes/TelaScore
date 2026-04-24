# language: pt
Funcionalidade: Envio de recomendações sociais
  Como usuário da plataforma
  Quero recomendar filmes para meus amigos
  Para compartilhar obras que eu gostei

  Cenário: Enviar uma recomendação válida para um amigo
    Dado que o usuário remetente com ID 1 quer recomendar o filme "FILME-001"
    Quando ele envia a recomendação para o destinatário com ID 2 com a mensagem "Você vai adorar!"
    Então a recomendação deve ser criada com sucesso
    E o status inicial da recomendação deve ser "PENDENTE"

  Cenário: Não deve ser possível enviar uma recomendação para si mesmo
    Dado que o usuário remetente com ID 1 quer recomendar o filme "FILME-001"
    Quando ele tenta enviar a recomendação para o destinatário com ID 1
    Então o sistema deve bloquear a criação informando que a operação é inválida
    E a recomendação retorna o erro "O remetente não pode ser o mesmo que o destinatário"