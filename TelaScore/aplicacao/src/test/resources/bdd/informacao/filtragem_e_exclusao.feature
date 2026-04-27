# language: pt
Funcionalidade: Gerenciamento e Filtros de Notícias
  Como um cinéfilo ou administrador
  Quero gerenciar a visibilidade das notícias no feed

  Cenário: Filtrar feed por categoria específica
    Dado que existem notícias das categorias "LANCAMENTO" e "EVENTO" no sistema
    Quando o usuário solicita visualizar apenas a categoria "EVENTO"
    Então o resultado deve conter apenas notícias de "EVENTO"
    E o contador de notícias deve refletir apenas essa seleção

  Cenário: Remover notícia do sistema
    Dado que existe uma notícia cadastrada com ID 50
    Quando o administrador executa o caso de uso de remoção para o ID 50
    Então a notícia não deve mais ser retornada pelo repositório