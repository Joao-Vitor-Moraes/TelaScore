# language: pt
Funcionalidade: Editar perfil
    Como usuário da plataforma
    Quero editar meu perfil
    Para atualizar minhas informações

    Cenário: Usuário edita o próprio perfil
        Dado que existe um perfil com ID 1 do usuário 10 com apelido "Gabriel Reis"
        Quando o usuário 10 solicita a edição do perfil 1 com apelido "Reis", biografia "Gosto de filmes de ação" e avatar "https://foto.com/reis.png"
        Então a edição do perfil deve ser realizada com sucesso
        E o apelido editado do perfil deve ser "Reis"
        E a biografia editada do perfil deve ser "Gosto de filmes de ação"
        E o avatar editado do perfil deve ser "https://foto.com/reis.png"

    Cenário: Usuário edita o próprio perfil sem informar apelido
        Dado que existe um perfil com ID 1 do usuário 10 com apelido "Gabriel Reis"
        Quando o usuário 10 solicita a edição do perfil 1 sem apelido, com biografia "Nova bio" e avatar "https://foto.com/novo.png"
        Então a edição do perfil deve ser realizada com sucesso
        E o apelido editado do perfil deve ser "Gabriel Reis"
        E a biografia editada do perfil deve ser "Nova bio"
        E o avatar editado do perfil deve ser "https://foto.com/novo.png"

    Cenário: Usuário não pode editar perfil de outro usuário
        Dado que existe um perfil com ID 1 do usuário 10 com apelido "Gabriel Reis"
        Quando o usuário 20 solicita a edição do perfil 1 com apelido "Outro", biografia "Bio indevida" e avatar "https://foto.com/outro.png"
        Então a edição do perfil deve ser rejeitada
        E deve retornar o erro da edição de perfil "O perfil não pertence ao usuário"
