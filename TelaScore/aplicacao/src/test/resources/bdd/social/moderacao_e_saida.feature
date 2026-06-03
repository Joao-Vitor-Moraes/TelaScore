# language: pt
Funcionalidade: Moderação e Fluxo de Saída
  Como um gestor de comunidade
  Quero gerenciar a lista de membros ativos

  Cenário: Remover membro da comunidade
    Dado que o usuário 30 é membro da comunidade 10
    Quando o comando de remoção é executado para o usuário 30 na comunidade 10
    Então o vínculo do usuário 30 com a comunidade 10 deve ser excluído do repositório

    Cenário: Usuário bloqueado tenta entrar na comunidade (Validação do Proxy)
      Dado que existe a comunidade "Marvel Vs DC" com ID 10
      Quando o usuário 99 solicita entrar na comunidade 10
      Então o sistema deve barrar a entrada informando uma violação de segurança