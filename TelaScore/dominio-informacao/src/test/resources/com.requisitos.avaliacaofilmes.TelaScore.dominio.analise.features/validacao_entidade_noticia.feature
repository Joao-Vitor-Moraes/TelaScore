# language: pt
Funcionalidade: Validação da Entidade Notícia
  Como o sistema de informação cinematográfica
  Quero garantir que os dados básicos da notícia estejam corretos
  Para evitar informações incompletas no sistema

  Cenário: Criar notícia com categoria válida
    Dado que eu informo o título "Batman 2 confirmado para 2026"
    E a categoria "LANCAMENTO"
    Quando eu tento criar a entidade de notícia
    Então a notícia deve ser instanciada com a categoria "LANCAMENTO"
    E a data de publicação deve ser registrada

  Cenário: Impedir título muito curto
    Dado que eu tento criar uma notícia com o título "Oi"
    Quando eu tento processar a criação
    Então o sistema deve lançar um erro de domínio informando "Título muito curto"