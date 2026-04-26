# language: pt
Funcionalidade: Sistema de gamificação
  Como usuário da plataforma
  Quero ser recompensado por minhas atividades
  Para me manter engajado na plataforma

  Cenário: Usuário ganha pontos ao realizar uma avaliação
    Dado que o usuário "ana_lima" realizou uma avaliação
    Quando o sistema calcula pontos
    Então pontos são adicionados ao perfil

  Cenário: Usuário ganha pontos ao acertar um quiz
    Dado que o usuário "ana_lima" acertou um quiz
    Quando o sistema calcula pontos
    Então pontos são adicionados ao perfil

  Cenário: Usuário ganha pontos ao criar uma lista
    Dado que o usuário "ana_lima" criou uma lista
    Quando o sistema calcula pontos
    Então pontos são adicionados ao perfil

  Cenário: Usuário ganha pontos ao completar uma meta
    Dado que o usuário "ana_lima" completou uma meta
    Quando o sistema calcula pontos
    Então pontos são adicionados ao perfil

  Cenário: Usuário ganha pontos ao convidar um amigo
    Dado que o usuário "ana_lima" convidou um amigo
    Quando o sistema calcula pontos
    Então pontos são adicionados ao perfil

  Cenário: Ação inválida não gera pontos
    Dado que uma ação inválida foi realizada
    Quando o sistema calcula pontos
    Então nenhum ponto é atribuído