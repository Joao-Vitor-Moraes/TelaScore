# language: pt
Funcionalidade: Gerenciar metas de consumo de filmes

  Cenário: Usuário cria uma meta com sucesso
    Dado que o usuário "joao_vitor" com ID 1 quer criar uma meta
    Quando ele cria uma meta chamada "Maratona Pós-Provas" com alvo 10 para o futuro
    Então a meta é criada com sucesso
    E pertence ao usuário 1
    E o status inicial deve ser "EM_ANDAMENTO"

  Cenário: Usuário tenta criar meta com data no passado
    Dado que o usuário "joao_vitor" com ID 1 quer criar uma meta
    Quando ele tenta criar uma meta com alvo 5 para 5 dias atrás
    Então o sistema rejeita a operação
    E retorna o erro "O prazo deve ser uma data futura ou o dia de hoje"

  Cenário: Adicionar progresso à meta com sucesso
    Dado que o usuário "joao_vitor" com ID 1 tem a meta "Ver Documentários" criada em andamento com alvo 10 e progresso 0
    Quando ele adiciona 3 de progresso à meta
    Então a meta deve registrar 3 de quantidade atual
    E o status da meta deve continuar "EM_ANDAMENTO"

  Cenário: Tentar adicionar progresso em uma meta cancelada
    Dado que o usuário "joao_vitor" com ID 1 tem a meta "Ver Documentários" que está com status "CANCELADA"
    Quando ele tenta adicionar 1 de progresso à meta
    Então o sistema rejeita a operação
    E retorna o erro "Apenas metas em andamento podem receber progresso."

  Cenário: Concluir e reabrir meta ajustando progresso acidental
    Dado que o usuário "joao_vitor" com ID 1 tem a meta "Filmes de Terror" criada com alvo 5 e progresso 4
    E ele adicionou 1 de progresso à meta
    E o status da meta mudou para "CONCLUIDA"
    Quando ele remove 1 de progresso da meta por ter registrado errado
    Então a quantidade atual deve ser 4
    E a meta deve voltar para o status "EM_ANDAMENTO"

  Cenário: Tentar estender prazo de uma meta já encerrada
    Dado que o usuário "joao_vitor" com ID 1 tem a meta "Desafio 30 Dias" que está com status "FALHADA"
    Quando ele tenta estender o prazo da meta para a próxima semana
    Então o sistema rejeita a operação
    E retorna o erro "Apenas metas em andamento podem ter o prazo estendido."
    
  Cenário: Estender o prazo de uma meta em andamento com sucesso
    Dado que o usuário "joao_vitor" com ID 1 tem a meta "Ver Documentários" criada em andamento com alvo 10 e progresso 0
    Quando ele tenta estender o prazo da meta para a próxima semana
    Então a meta é atualizada com sucesso

  Cenário: Tentar estender prazo para uma data anterior ao prazo atual
    Dado que o usuário "joao_vitor" com ID 1 tem a meta "Ver Documentários" criada em andamento com alvo 10 e progresso 0
    Quando ele tenta estender o prazo da meta para ontem
    Então o sistema rejeita a operação
    E retorna o erro "O novo prazo deve ser posterior ao prazo atual"

  Cenário: Tentar adicionar uma quantidade de progresso inválida
    Dado que o usuário "joao_vitor" com ID 1 tem a meta "Ver Documentários" criada em andamento com alvo 10 e progresso 0
    Quando ele tenta adicionar 0 de progresso à meta
    Então o sistema rejeita a operação
    E retorna o erro "A quantidade de progresso a adicionar deve ser maior que zero"