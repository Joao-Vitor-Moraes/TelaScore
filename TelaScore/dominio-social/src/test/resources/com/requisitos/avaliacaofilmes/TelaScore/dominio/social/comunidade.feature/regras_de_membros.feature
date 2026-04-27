# language: pt
Funcionalidade: Regras de Membros da Comunidade
  Como o sistema social do TelaScore
  Quero garantir que a hierarquia de papéis seja respeitada
  Para manter a integridade da moderação

  Cenário: Bloquear rebaixamento do criador
    Dado que o usuário 10 é o "CRIADOR" de uma comunidade
    Quando o sistema tenta rebaixar o papel do usuário 10 para "MEMBRO"
    Então uma exceção de estado inválido deve ser lançada
    E o papel do usuário 10 deve permanecer como "CRIADOR"

  Cenário: Promover membro a moderador
    Dado que o usuário 20 é um "MEMBRO" comum
    Quando o usuário 20 é promovido a moderador
    Então o papel atualizado deve ser "MODERADOR"