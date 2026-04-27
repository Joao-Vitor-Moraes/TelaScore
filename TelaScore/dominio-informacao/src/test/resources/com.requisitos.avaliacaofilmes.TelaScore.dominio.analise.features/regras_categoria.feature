# language: pt
Funcionalidade: Regras de Categorização
  Como um sistema de curadoria
  Quero garantir que as categorias sejam atribuídas corretamente

  Cenário: Notícia de crítica deve ser informativa
    Dado que eu defino a categoria como "CRITICA"
    Quando a entidade é criada
    Então o sistema deve permitir que o conteúdo contenha a opinião do autor