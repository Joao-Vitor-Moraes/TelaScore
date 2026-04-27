# language: pt
Funcionalidade: Fluxo de Publicação de Notícias
  Como um autor de conteúdo cinematográfico
  Quero publicar novas informações
  Para que elas fiquem salvas no repositório oficial

  Cenário: Publicar notícia com sucesso via Caso de Uso
    Dado que o autor com ID 1 está autenticado
    Quando ele executa o comando de adicionar notícia com título "Novo filme do Aranha" e categoria "LANCAMENTO"
    Então o repositório deve confirmar o salvamento da nova notícia
    E a notícia deve receber um ID único gerado pelo sistema