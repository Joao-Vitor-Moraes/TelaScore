# language: pt
Funcionalidade: Login de usuario
    Como usuario cadastrado
    Quero validar minhas credenciais
    Para acessar minha conta conforme meu papel

    Cenario: Login de usuario cinefilo com credenciais validas
        Dado que existe um usuario "CINEFILO" cadastrado com e-mail "grmp@cesar.school" e senha "123456"
        Quando valido login com e-mail "grmp@cesar.school" e senha "123456" no dominio
        Entao o login deve ser validado com sucesso
        E o usuario autenticado deve ter ID 1
        E o usuario autenticado deve ter papel "CINEFILO"

    Cenario: Login de administrador com credenciais validas
        Dado que existe um usuario "ADMIN" cadastrado com e-mail "admin@admin.com" e senha "admin123"
        Quando valido login com e-mail "admin@admin.com" e senha "admin123" no dominio
        Entao o login deve ser validado com sucesso
        E o usuario autenticado deve ter ID 1
        E o usuario autenticado deve ter papel "ADMIN"

    Cenario: Nao deve validar login de usuario inexistente
        Dado que nao existe usuario cadastrado com e-mail "naoexiste@cesar.school"
        Quando valido login com e-mail "naoexiste@cesar.school" e senha "123456" no dominio
        Entao o login deve ser rejeitado com erro "Erro ao fazer login"

    Cenario: Nao deve validar login com senha incorreta
        Dado que existe um usuario "CINEFILO" cadastrado com e-mail "grmp@cesar.school" e senha "123456"
        Quando valido login com e-mail "grmp@cesar.school" e senha "senhaerrada" no dominio
        Entao o login deve ser rejeitado com erro "Erro ao fazer login"
