# language: pt
Funcionalidade: Cadastro de usuário
    Como visitante da plataforma
    Quero cadastrar um usuário
    Para que eu possa ter minha conta

    Cenário: Cadastrar usuário com dados válidos
        Dado que desejo criar uma conta com o nome "Gabriel Reis"
        E informo o e-mail "grmp@cesar.school"
        E informo a senha "123456"
        Quando solicito o cadastro do usuário
        Então o cadastro deve ser realizado com sucesso
        E o usuário deve receber o papel "CINEFILO"
        E um perfil deve ser criado para o usuário
        E o apelido do perfil deve ser "Gabriel Reis"

    Cenário: Não deve cadastrar usuário com e-mail inválido
        Dado que desejo criar uma conta com o nome "Gabriel Reis"
        E informo o e-mail "grmp-email-invalido"
        E informo a senha "123456"
        Quando solicito o cadastro do usuário
        Então o cadastro deve ser rejeitado
        E deve retornar o erro "O formato do e-mail é inválido"

    Cenário: Não deve cadastrar usuário sem nome
        Dado que desejo criar uma conta sem informar o nome
        E informo o e-mail "grmp@cesar.school"
        E informo a senha "123456"
        Quando solicito o cadastro do usuário
        Então o cadastro deve ser rejeitado
        E deve retornar o erro "O nome não pode estar em branco"

    Cenário: Não deve cadastrar usuário sem senha
        Dado que desejo criar uma conta com o nome "Gabriel Reis"
        E informo o e-mail "grmp@cesar.school"
        E informo a senha ""
        Quando solicito o cadastro do usuário
        Então o cadastro deve ser rejeitado
        E deve retornar o erro "A senha não pode estar em branco"

    Cenário: Não deve cadastrar usuário com e-mail já existente
        Dado que já existe um usuário cadastrado com o e-mail "grmp@cesar.school"
        E desejo criar uma conta com o nome "Gabriel Reis"
        E informo o e-mail "grmp@cesar.school"
        E informo a senha "123456"
        Quando solicito o cadastro do usuário
        Então o cadastro deve ser rejeitado
        E deve retornar o erro "Já existe um usuário cadastrado com este e-mail"
