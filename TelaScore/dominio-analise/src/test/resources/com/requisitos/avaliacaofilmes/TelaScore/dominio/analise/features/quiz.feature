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