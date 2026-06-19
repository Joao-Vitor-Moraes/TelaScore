import { useEffect, useMemo, useState } from 'react';
import { FiAtSign, FiEdit2, FiFileText, FiImage, FiMail, FiSave, FiShield, FiUpload, FiUser, FiX } from 'react-icons/fi';
import Navbar from '../../components/Navbar';
import { usuarioService } from '../../services/api';
import './usuario.css';

const camposOcultos = new Set(['id', 'senha', 'token', 'tipoToken', 'expiraEmSegundos']);

const rotulos = {
    nome: 'Nome',
    email: 'E-mail',
    papel: 'Tipo',
    apelido: 'Apelido',
    biografia: 'Biografia',
    avatarUrl: 'Avatar URL',
};

const icones = {
    nome: <FiUser size={16} />,
    email: <FiMail size={16} />,
    papel: <FiShield size={16} />,
    apelido: <FiAtSign size={16} />,
    biografia: <FiFileText size={16} />,
};

const formInicial = {
    nome: '',
    apelido: '',
    biografia: '',
    avatarUrl: '',
};

export default function MeuUsuario() {
    const [usuario, setUsuario] = useState(null);
    const [carregando, setCarregando] = useState(true);
    const [erro, setErro] = useState(null);
    const [erroAcao, setErroAcao] = useState(null);
    const [editando, setEditando] = useState(false);
    const [salvando, setSalvando] = useState(false);
    const [enviandoFoto, setEnviandoFoto] = useState(false);
    const [form, setForm] = useState(formInicial);

    useEffect(() => {
        carregarUsuario();
    }, []);

    async function carregarUsuario() {
        setCarregando(true);
        setErro(null);

        try {
            const dados = await usuarioService.meuUsuario();
            setUsuario(dados);
            setForm(formDeUsuario(dados));
        } catch {
            setUsuario(null);
            setErro('Erro ao carregar seu usuário.');
        } finally {
            setCarregando(false);
        }
    }

    function iniciarEdicao() {
        setErroAcao(null);
        setForm(formDeUsuario(usuario));
        setEditando(true);
    }

    function cancelarEdicao() {
        setErroAcao(null);
        setForm(formDeUsuario(usuario));
        setEditando(false);
    }

    function atualizarCampo(campo, valor) {
        setForm(atual => ({ ...atual, [campo]: valor }));
    }

    async function selecionarFoto(event) {
        const arquivo = event.target.files?.[0];
        event.target.value = '';
        if (!arquivo) return;

        if (!arquivo.type.startsWith('image/')) {
            setErroAcao('Escolha um arquivo de imagem.');
            return;
        }
        if (arquivo.size > 5 * 1024 * 1024) {
            setErroAcao('A imagem deve ter no máximo 5 MB.');
            return;
        }

        setEnviandoFoto(true);
        setErroAcao(null);
        try {
            const resultado = await usuarioService.enviarAvatar(arquivo);
            atualizarCampo('avatarUrl', resultado.url);
        } catch (e) {
            setErroAcao(e.message || 'Erro ao enviar a imagem.');
        } finally {
            setEnviandoFoto(false);
        }
    }

    async function salvarUsuario(event) {
        event?.preventDefault();
        setSalvando(true);
        setErroAcao(null);

        try {
            const usuarioAtualizado = await usuarioService.editarMeuUsuario(form);
            setUsuario(usuarioAtualizado);
            setForm(formDeUsuario(usuarioAtualizado));
            setEditando(false);
        } catch {
            setErroAcao('Erro ao salvar seu usuário.');
        } finally {
            setSalvando(false);
        }
    }

    const campos = useMemo(() => {
        if (!usuario) return [];

        return Object.entries(usuario)
            .filter(([chave]) => !camposOcultos.has(chave))
            .filter(([chave]) => chave !== 'avatarUrl')
            .map(([chave, valor]) => ({
                chave,
                rotulo: rotulos[chave] || formatarRotulo(chave),
                valor: valor || '-',
                icone: icones[chave] || <FiFileText size={16} />,
            }));
    }, [usuario]);

    const avatarPreview = editando ? form.avatarUrl : usuario?.avatarUrl;

    return (
        <div className="cinema-page user-page">
            <Navbar />

            <main className="cinema-container user-content">
                <div className="page-heading">
                    <div>
                        <p className="page-eyebrow">Perfil</p>
                        <h1 className="page-title">Meu usuário</h1>
                        <p className="page-description">Consulte e atualize os dados públicos da sua conta.</p>
                    </div>

                    {usuario && !editando && (
                        <button className="btn-secondary" onClick={iniciarEdicao} disabled={carregando}>
                            <FiEdit2 />
                            Editar perfil
                        </button>
                    )}
                </div>

                {carregando && <div className="empty-state">Carregando...</div>}
                {erro && !carregando && <div className="empty-state user-error">{erro}</div>}
                {erroAcao && <div className="user-alert">{erroAcao}</div>}

                {usuario && !carregando && (
                    <>
                        <section className="user-summary glass-panel">
                            <div className="user-avatar">
                                {avatarPreview
                                    ? <img src={avatarPreview} alt="" />
                                    : <span>{inicialUsuario(usuario)}</span>}
                            </div>

                            <div className="user-identity">
                                <div className="user-title-row">
                                    <h2>{usuario.nome || 'Usuário sem nome'}</h2>
                                    {usuario.papel && (
                                        <span className={`user-badge ${usuario.papel === 'ADMIN' ? 'is-admin' : 'is-user'}`}>
                                            <FiShield />
                                            {usuario.papel}
                                        </span>
                                    )}
                                </div>
                                <p>
                                    <FiMail />
                                    {usuario.email || 'E-mail não informado'}
                                </p>
                                {usuario.apelido && (
                                    <span className="user-nickname">
                                        <FiAtSign />
                                        {usuario.apelido}
                                    </span>
                                )}
                            </div>
                        </section>

                        {editando ? (
                            <form className="user-edit-panel glass-panel" onSubmit={salvarUsuario}>
                                <div className="user-form-grid">
                                    <label className="user-field">
                                        Nome
                                        <input
                                            value={form.nome}
                                            onChange={e => atualizarCampo('nome', e.target.value)}
                                            required
                                        />
                                    </label>

                                    <label className="user-field">
                                        E-mail
                                        <input className="is-locked" value={usuario.email || ''} disabled />
                                    </label>

                                    <label className="user-field">
                                        Tipo
                                        <input className="is-locked" value={usuario.papel || ''} disabled />
                                    </label>

                                    <label className="user-field">
                                        Apelido
                                        <input
                                            value={form.apelido}
                                            onChange={e => atualizarCampo('apelido', e.target.value)}
                                        />
                                    </label>

                                    <label className="user-field is-wide">
                                        Foto do perfil
                                        <span className="user-avatar-options">
                                            <label className={`user-upload-button ${enviandoFoto ? 'is-disabled' : ''}`}>
                                                <FiUpload />
                                                {enviandoFoto ? 'Enviando imagem...' : 'Escolher do computador'}
                                                <input
                                                    type="file"
                                                    accept="image/jpeg,image/png,image/webp,image/gif"
                                                    onChange={selecionarFoto}
                                                    disabled={enviandoFoto}
                                                />
                                            </label>
                                            <span className="user-avatar-divider">ou use um link</span>
                                        </span>
                                        <span className="user-input-with-icon">
                                            <FiImage />
                                            <input
                                                type="text"
                                                value={form.avatarUrl}
                                                onChange={e => atualizarCampo('avatarUrl', e.target.value)}
                                                placeholder="https://exemplo.com/minha-foto.jpg"
                                            />
                                        </span>
                                    </label>

                                    <label className="user-field is-wide">
                                        Biografia
                                        <textarea
                                            value={form.biografia}
                                            onChange={e => atualizarCampo('biografia', e.target.value)}
                                        />
                                    </label>
                                </div>

                                <div className="user-form-actions">
                                    <button type="button" className="btn-secondary" onClick={cancelarEdicao} disabled={salvando}>
                                        <FiX />
                                        Cancelar
                                    </button>
                                    <button type="submit" className="btn-primary" disabled={salvando || enviandoFoto}>
                                        <FiSave />
                                        {salvando ? 'Salvando...' : 'Salvar alterações'}
                                    </button>
                                </div>
                            </form>
                        ) : (
                            <section className="user-info-grid">
                                {campos.map(campo => (
                                    <article
                                        key={campo.chave}
                                        className={`user-info-card ${campo.chave === 'biografia' ? 'is-wide' : ''}`}
                                    >
                                        <span className="user-info-label">
                                            {campo.icone}
                                            {campo.rotulo}
                                        </span>
                                        <span className="user-info-value">{campo.valor}</span>
                                    </article>
                                ))}
                            </section>
                        )}
                    </>
                )}
            </main>
        </div>
    );
}

function inicialUsuario(usuario) {
    const valor = usuario.apelido || usuario.nome || usuario.email || '?';
    return valor.trim().charAt(0).toUpperCase() || '?';
}

function formatarRotulo(chave) {
    return chave
        .replace(/([A-Z])/g, ' $1')
        .replace(/^./, letra => letra.toUpperCase());
}

function formDeUsuario(usuario) {
    return {
        nome: usuario?.nome ?? '',
        apelido: usuario?.apelido ?? '',
        biografia: usuario?.biografia ?? '',
        avatarUrl: usuario?.avatarUrl ?? '',
    };
}
