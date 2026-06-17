# language: pt
Funcionalidade: Cadastrar usuario
    Como visitante da plataforma
    Quero cadastrar meu usuario com dados de acesso e dados publicos
    Para que eu possa ter minha conta no sistema

    Cenario: Cadastrar usuario com dados completos
        Dado que desejo cadastrar um usuario chamado "Gabriel Reis"
        E informo o e-mail "grmp@cesar.school"
        E informo a senha "123456"
        E informo o apelido "Gabs"
        E informo a biografia "Gosto de filmes de suspense"
        E informo a URL da imagem "https://avatar.test/gabs.png"
        Quando crio o usuario no dominio
        Entao o usuario deve ser criado com sucesso
        E o usuario deve receber o papel "CINEFILO"
        E o usuario deve ter o apelido "Gabs"
        E o usuario deve ter a biografia "Gosto de filmes de suspense"
        E o usuario deve ter a URL da imagem "https://avatar.test/gabs.png"

    Cenario: Cadastrar usuario sem dados publicos opcionais
        Dado que desejo cadastrar um usuario chamado "Gabriel Reis"
        E informo o e-mail "gabriel@cesar.school"
        E informo a senha "123456"
        Quando crio o usuario no dominio
        Entao o usuario deve ser criado com sucesso
        E o usuario deve receber o papel "CINEFILO"
        E o usuario deve ter o apelido "Gabriel Reis"
        E o usuario nao deve ter biografia
        E o usuario nao deve ter URL da imagem

    Cenario: Nao deve cadastrar usuario com e-mail invalido
        Dado que desejo cadastrar um usuario chamado "Gabriel Reis"
        E informo o e-mail "grmp-email-invalido"
        E informo a senha "123456"
        Quando crio o usuario no dominio
        Entao o sistema deve bloquear o cadastro com erro "O formato do e-mail e invalido"

    Cenario: Nao deve cadastrar usuario sem nome
        Dado que desejo cadastrar um usuario sem nome
        E informo o e-mail "grmp@email.com"
        E informo a senha "123456"
        Quando crio o usuario no dominio
        Entao o sistema deve bloquear o cadastro com erro "O nome nao pode estar em branco"

    Cenario: Nao deve cadastrar usuario sem senha
        Dado que desejo cadastrar um usuario chamado "Gabriel Reis"
        E informo o e-mail "grmp@email.com"
        E informo a senha ""
        Quando crio o usuario no dominio
        Entao o sistema deve bloquear o cadastro com erro "A senha nao pode estar em branco"
