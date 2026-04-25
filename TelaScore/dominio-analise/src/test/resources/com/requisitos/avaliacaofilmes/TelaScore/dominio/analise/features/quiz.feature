# language: pt
Funcionalidade: Gestão de Quizzes

  Cenário: Criar um quiz com perguntas com sucesso
    Dado que eu estou criando um quiz com o título "Cinema 101"
    E que este quiz possui uma pergunta válida
    Quando eu tento finalizar a criação do quiz
    Então o quiz deve ser salvo com sucesso

  Cenário: Bloquear criação de quiz sem perguntas
    Dado que eu estou criando um quiz com o título "Quiz Vazio"
    Quando eu tento finalizar a criação do quiz
    Então o sistema deve impedir a criação informando que o quiz não tem perguntas

  Cenário: Responder a um quiz com sucesso
    Dado que existe um quiz salvo com o título "Marvel Quiz" e a pergunta "Qual o nome do Thor?" com a resposta correta "Odinson"
    Quando o usuário 10 responde "Odinson" para a pergunta "Qual o nome do Thor?"
    Então a tentativa deve ser registrada com 1 acerto de 1 pergunta
  
  Cenário: Remover um quiz com sucesso
    Dado que existe um quiz salvo com o título "Quiz para Deletar" e a pergunta "Pergunta" com a resposta correta "Resposta"
    Quando o quiz é removido
    Então o quiz deve deixar de existir