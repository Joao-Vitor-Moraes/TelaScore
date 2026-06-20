import { useEffect, useMemo, useState } from 'react';
import { FiAtSign, FiEdit2, FiMail, FiSave, FiSearch, FiShield, FiTrash2, FiUser, FiX } from 'react-icons/fi';
import Navbar from '../../components/Navbar';
import NavbarAdmin from '../../components/NavbarAdmin';
import { useAuth } from '../../context/AuthContext';
import { usuarioService } from '../../services/api';
import '../admin/admin.css';

const formInicial = {
    nome: '',
    email: '',
    papel: 'CINEFILO',
    apelido: '',
    biografia: '',
    avatarUrl: '',
};

export default function AdminUsuarios() {
    const { sessao } = useAuth();
    const [usuarios, setUsuarios] = useState([]);
    const [carregando, setCarregando] = useState(true);
    const [erro, setErro] = useState(null);
    const [erroAcao, setErroAcao] = useState(null);
    const [busca, setBusca] = useState('');
    const [editandoId, setEditandoId] = useState(null);
    const [form, setForm] = useState(formInicial);
    const [salvando, setSalvando] = useState(false);
    const [usuarioParaRemover, setUsuarioParaRemover] = useState(null);
    const [removendo, setRemovendo] = useState(false);

    useEffect(() => {
        carregarUsuarios();
    }, []);

    async function carregarUsuarios() {
        setCarregando(true);
        setErro(null);
        try {
            const dados = await usuarioService.listar();
            setUsuarios(Array.isArray(dados) ? dados : []);
        } catch {
            setUsuarios([]);
            setErro('Erro ao carregar usuários.');
        } finally {
            setCarregando(false);
        }
    }

    const listagem = useMemo(() => {
        const termo = busca.trim().toLowerCase();
        if (!termo) return usuarios;

        return usuarios.filter(usuario => [
            usuario.nome,
            usuario.email,
            usuario.apelido,
            usuario.papel,
            String(usuario.id),
        ].some(valor => String(valor ?? '').toLowerCase().includes(termo)));
    }, [busca, usuarios]);

    function abrirEdicao(usuario) {
        if (editandoId === usuario.id) {
            fecharEdicao();
            return;
        }

        setErroAcao(null);
        setEditandoId(usuario.id);
        setForm({
            nome: usuario.nome ?? '',
            email: usuario.email ?? '',
            papel: usuario.papel ?? 'CINEFILO',
            apelido: usuario.apelido ?? '',
            biografia: usuario.biografia ?? '',
            avatarUrl: usuario.avatarUrl ?? '',
        });
    }

    function fecharEdicao() {
        setEditandoId(null);
        setForm(formInicial);
        setErroAcao(null);
    }

    function abrirModalRemocao(usuario) {
        setErroAcao(null);
        setUsuarioParaRemover(usuario);
    }

    function fecharModalRemocao() {
        if (removendo) return;
        setUsuarioParaRemover(null);
    }

    function atualizarCampo(campo, valor) {
        setForm(atual => ({ ...atual, [campo]: valor }));
    }

    async function salvarEdicao(usuarioId) {
        setSalvando(true);
        setErroAcao(null);
        try {
            const usuarioAtualizado = await usuarioService.editar(usuarioId, form);
            setUsuarios(atuais => atuais.map(usuario =>
                usuario.id === usuarioId ? usuarioAtualizado : usuario
            ));
            fecharEdicao();
        } catch {
            setErroAcao('Erro ao editar usuário.');
        } finally {
            setSalvando(false);
        }
    }

    async function alternarPapelAutor(usuario) {
        setErroAcao(null);
        const proximoPapel = usuario.papel === 'AUTOR' ? 'CINEFILO' : 'AUTOR';
        const dadosDinamicos = {
            nome: usuario.nome ?? '',
            email: usuario.email ?? '',
            papel: proximoPapel,
            apelido: usuario.apelido ?? '',
            biografia: usuario.biografia ?? '',
            avatarUrl: usuario.avatarUrl ?? '',
        };
        try {
            const usuarioAtualizado = await usuarioService.editar(usuario.id, dadosDinamicos);
            setUsuarios(atuais => atuais.map(u =>
                u.id === usuario.id ? usuarioAtualizado : u
            ));
        } catch {
            setErroAcao('Erro ao modificar papel do usuário.');
        }
    }

    async function confirmarRemocao() {
        if (!usuarioParaRemover) return;

        setRemovendo(true);
        setErroAcao(null);
        try {
            await usuarioService.remover(usuarioParaRemover.id);
            setUsuarios(atuais => atuais.filter(item => item.id !== usuarioParaRemover.id));
            if (editandoId === usuarioParaRemover.id) fecharEdicao();
            setUsuarioParaRemover(null);
        } catch {
            setErroAcao('Erro ao remover usuário.');
        } finally {
            setRemovendo(false);
        }
    }

    if (sessao?.papel !== 'ADMIN') {
        return (
            <div className="cinema-page admin-page">
                <Navbar />
                <NavbarAdmin />
                <main className="cinema-container admin-content">
                    <div className="empty-state admin-error">Acesso restrito a administradores.</div>
                </main>
            </div>
        );
    }

    return (
        <div className="cinema-page admin-page">
            <Navbar />
            <NavbarAdmin />

            <main className="cinema-container admin-content">
                <div className="catalog-toolbar admin-toolbar">
                    <div>
                        <p className="page-eyebrow">Administração</p>
                        <h2 className="page-title">Usuários</h2>
                        <p className="page-description">{listagem.length} de {usuarios.length} usuários cadastrados</p>
                    </div>

                    <div className="catalog-toolbar__actions">
                        <label className="catalog-search">
                            <FiSearch />
                            <input
                                value={busca}
                                onChange={e => setBusca(e.target.value)}
                                placeholder="Buscar usuário..."
                            />
                        </label>
                    </div>
                </div>

                {erroAcao && <div className="admin-alert">{erroAcao}</div>}
                {carregando && <div className="empty-state">Carregando...</div>}
                {erro && !carregando && <div className="empty-state admin-error">{erro}</div>}
                {!erro && !carregando && listagem.length === 0 && (
                    <div className="empty-state">
                        <FiUser size={32} />
                        <p>Nenhum usuário encontrado.</p>
                    </div>
                )}

                {!carregando && !erro && listagem.length > 0 && (
                    <div className="admin-list">
                        {listagem.map(usuario => (
                            <article key={usuario.id} className={`admin-card admin-user-card ${editandoId === usuario.id ? 'is-editing' : ''}`}>
                                <div className="admin-card__main">
                                    <div className="admin-avatar">
                                        {usuario.avatarUrl
                                            ? <img src={usuario.avatarUrl} alt="" />
                                            : <span>{inicialUsuario(usuario)}</span>}
                                    </div>

                                    <div className="admin-card__body">
                                        <div className="admin-card__title-row">
                                            <h3>{usuario.nome || 'Usuário sem nome'}</h3>
                                            <span className={`admin-badge ${usuario.papel === 'ADMIN' ? 'is-admin' : usuario.papel === 'AUTOR' ? 'is-autor' : 'is-user'}`}>
                                                <FiShield />
                                                {usuario.papel}
                                            </span>
                                        </div>

                                        <div className="admin-meta">
                                            <span><FiUser /> ID {usuario.id}</span>
                                            <span><FiMail /> {usuario.email}</span>
                                            <span><FiAtSign /> {usuario.apelido || '-'}</span>
                                        </div>

                                        {usuario.biografia && (
                                            <p className="admin-description">{usuario.biografia}</p>
                                        )}
                                    </div>

                                    <div className="admin-actions">
                                        {usuario.papel !== 'ADMIN' && (
                                            <button className="btn-secondary" onClick={() => alternarPapelAutor(usuario)}>
                                                <FiShield />
                                                {usuario.papel === 'AUTOR' ? 'Remover Autor' : 'Definir Autor'}
                                            </button>
                                        )}
                                        <button className="btn-secondary" onClick={() => abrirEdicao(usuario)}>
                                            {editandoId === usuario.id ? <FiX /> : <FiEdit2 />}
                                            {editandoId === usuario.id ? 'Fechar' : 'Editar'}
                                        </button>
                                        <button className="btn-danger" onClick={() => abrirModalRemocao(usuario)}>
                                            <FiTrash2 />
                                            Remover
                                        </button>
                                    </div>
                                </div>

                                {editandoId === usuario.id && (
                                    <div className="admin-edit-panel">
                                        <div className="admin-form-grid">
                                            <label className="admin-field">
                                                Nome
                                                <input value={form.nome} onChange={e => atualizarCampo('nome', e.target.value)} />
                                            </label>
                                            <label className="admin-field">
                                                E-mail
                                                <input value={form.email} onChange={e => atualizarCampo('email', e.target.value)} />
                                            </label>
                                            <label className="admin-field">
                                                Apelido
                                                <input value={form.apelido} onChange={e => atualizarCampo('apelido', e.target.value)} />
                                            </label>
                                            <label className="admin-field">
                                                Papel
                                                <select value={form.papel} onChange={e => atualizarCampo('papel', e.target.value)}>
                                                    <option value="CINEFILO">CINEFILO</option>
                                                    <option value="AUTOR">AUTOR</option>
                                                    <option value="ADMIN">ADMIN</option>
                                                </select>
                                            </label>
                                            <label className="admin-field is-wide">
                                                Avatar URL
                                                <input value={form.avatarUrl} onChange={e => atualizarCampo('avatarUrl', e.target.value)} />
                                            </label>
                                            <label className="admin-field is-wide">
                                                Biografia
                                                <textarea
                                                    value={form.biografia}
                                                    onChange={e => atualizarCampo('biografia', e.target.value)}
                                                />
                                            </label>
                                        </div>

                                        <div className="admin-form-actions">
                                            <button className="btn-secondary" onClick={fecharEdicao} disabled={salvando}>
                                                <FiX />
                                                Cancelar
                                            </button>
                                            <button className="btn-primary" onClick={() => salvarEdicao(usuario.id)} disabled={salvando}>
                                                <FiSave />
                                                {salvando ? 'Salvando...' : 'Salvar'}
                                            </button>
                                        </div>
                                    </div>
                                )}
                            </article>
                        ))}
                    </div>
                )}
            </main>

            {usuarioParaRemover && (
                <div className="admin-modal-overlay">
                    <div className="admin-modal">
                        <h3>Remover usuário</h3>
                        <p>
                            Tem certeza que deseja remover {usuarioParaRemover.nome || usuarioParaRemover.email}?
                        </p>
                        <div className="admin-modal__actions">
                            <button className="btn-secondary" onClick={fecharModalRemocao} disabled={removendo}>
                                <FiX />
                                Cancelar
                            </button>
                            <button className="btn-danger" onClick={confirmarRemocao} disabled={removendo}>
                                <FiTrash2 />
                                {removendo ? 'Removendo...' : 'Remover'}
                            </button>
                        </div>
                    </div>
                </div>
            )}
        </div>
    );
}

function inicialUsuario(usuario) {
    const valor = usuario.apelido || usuario.nome || usuario.email || '?';
    return valor.trim().charAt(0).toUpperCase() || '?';
}
