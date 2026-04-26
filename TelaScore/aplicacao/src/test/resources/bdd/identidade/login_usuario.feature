# language: pt
Funcionalidade: Login de usuário
    Como usuário cadastrado
    Quero fazer login na plataforma
    Para acessar minha conta conforme meu papel

    Cenário: Cinéfilo faz login com credenciais válidas
        Dado que existe um usuário "CINEFILO" cadastrado com e-mail "grmp@cesar.school" e senha "123456"
        Quando solicito login com e-mail "grmp@cesar.school" e senha "123456"
        Então o login deve ser realizado com sucesso
        E o usuário logado deve ter ID 1
        E o usuário logado deve ter papel "CINEFILO"

    Cenário: Administrador faz login com credenciais válidas
        Dado que existe um usuário "ADMIN" cadastrado com e-mail "admin@admin.com" e senha "admin123"
        Quando solicito login com e-mail "admin@admin.com" e senha "admin123"
        Então o login deve ser realizado com sucesso
        E o usuário logado deve ter ID 1
        E o usuário logado deve ter papel "ADMIN"

    Cenário: Não deve fazer login com e-mail não cadastrado
        Dado que não existe usuário cadastrado com e-mail "naoexiste@gmail.com"
        Quando solicito login com e-mail "naoexiste@gmail.com" e senha "123456"
        Então o login deve ser rejeitado
        E deve retornar o erro do login "Erro ao fazer login"

    Cenário: Não deve fazer login com senha incorreta
        Dado que existe um usuário "CINEFILO" cadastrado com e-mail "grmp@cesar.school" e senha "123456"
        Quando solicito login com e-mail "grmp@cesar.school" e senha "senhaerrada"
        Então o login deve ser rejeitado
        E deve retornar o erro do login "Erro ao fazer login"
