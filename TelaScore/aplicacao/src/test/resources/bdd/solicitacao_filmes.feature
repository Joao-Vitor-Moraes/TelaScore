Feature: Solicitação de filmes

  Scenario: Usuário solicita um filme com sucesso
    Given que o usuário "ana_lima" está autenticado
    When ela solicita o filme "Interestelar"
    Then a solicitação é registrada com sucesso

  Scenario: Usuário solicita um filme já existente
    Given que o filme "Interestelar" já existe no catálogo
    When "ana_lima" tenta solicitá-lo
    Then o sistema rejeita a operação
    And retorna o erro "Filme já cadastrado"
