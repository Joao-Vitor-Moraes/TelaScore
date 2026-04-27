# language: pt
Funcionalidade: Validação de Dados da Comunidade
  Como o domínio social
  Quero impedir a criação de comunidades com dados inválidos

  Cenário: Impedir nome em branco
    Dado que eu informo o ID 1 e uma descrição válida
    Mas o nome da comunidade é "   "
    Quando eu tento instanciar a comunidade
    Então o sistema deve lançar um erro de validação de campo obrigatório

  Cenário: Criar comunidade com dados válidos
    Dado que eu informo o nome "Clube do Terror" e descrição "Debates sobre filmes de horror"
    Quando a comunidade é criada
    Então a data de criação deve ser registrada automaticamente com o horário atual