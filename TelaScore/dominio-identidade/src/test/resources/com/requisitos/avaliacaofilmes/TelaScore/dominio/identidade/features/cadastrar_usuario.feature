# language: pt
Funcionalidade: Cadastrar um usuário
    Como visitante da plataforma
    Quero cadastrar um usuário
    Para que eu possa ter minha conta

    Cenário:  Cadastrar usuário com e-mail válido
        Dado que desejo cadastrar um usuário chamado "Gabriel Reis"
        Quando informo o e-mail "grmp@cesar.school"
        Então o usuário deve ser criado com sucesso

    Cenário: Não deve ser possível cadastrar usuário com e-mail inválido
        Dado que desejo cadastrar um usuário chamado "Gabriel Reis"
        Quando informo o e-mail "grmp-email-invalido"
        Então o sistema deve bloquear o cadastro informando que o e-mail é inválido

    Cenário: Não deve ser possível cadastrar usuário sem nome
        Dado que desejo cadastrar um usuário sem nome
        Quando informo o e-mail "grmp@email.com"
        Então o sistema deve bloquear o cadastro informando que o nome não pode estar em branco

