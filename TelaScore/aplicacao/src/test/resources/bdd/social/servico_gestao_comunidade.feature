# language: pt
Funcionalidade: Serviço de Gestão de Comunidades
  Como um usuário interessado em grupos
  Quero utilizar os serviços de criação e adesão
  Para interagir com outros cinéfilos

  Cenário: Criar comunidade e vincular criador automaticamente
    Dado que o usuário 1 deseja criar a comunidade "Nolan Fans"
    Quando o serviço de criação é acionado para o usuário 1
    Então a comunidade deve ser persistida no repositório
    E o usuário 1 deve constar na lista de membros com o papel "CRIADOR"

  Cenário: Novo usuário entrando na comunidade
    Dado que existe a comunidade "Cinema Cult" com ID 50
    Quando o usuário 5 solicita entrar na comunidade 50
    Então o serviço deve registrar o usuário 5 como um novo "MEMBRO"
    E o repositório deve confirmar a existência de 1 novo vínculo social