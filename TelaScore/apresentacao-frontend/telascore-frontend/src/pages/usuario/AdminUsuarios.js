import { useEffect, useMemo, useState } from 'react';
import { FiAtSign, FiEdit2, FiMail, FiSave, FiSearch, FiShield, FiTrash2, FiUser, FiX } from 'react-icons/fi';
import NavbarAdmin from '../../components/NavbarAdmin';
import { useAuth } from '../../context/AuthContext';
import { usuarioService } from '../../services/api';
import Navbar from '../../components/Navbar';

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
            setErro('Erro ao carregar usuarios.');
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
            setErroAcao('Erro ao editar usuario.');
        } finally {
            setSalvando(false);
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
            setErroAcao('Erro ao remover usuario.');
        } finally {
            setRemovendo(false);
        }
    }

    if (sessao?.papel !== 'ADMIN') {
        return (
            <div style={styles.pagina}>
                <Navbar />
                <NavbarAdmin />
                <p style={styles.msgErro}>Acesso restrito a administradores.</p>
            </div>
        );
    }

    return (
        <div style={styles.pagina}>
            <Navbar />
            <NavbarAdmin />
            <div style={styles.conteudo}>
                <div style={styles.cabecalho}>
                    <div>
                        <h2 style={styles.titulo}>ADM. USUARIOS</h2>
                        <span style={styles.total}>{listagem.length} de {usuarios.length} usuarios</span>
                    </div>

                    <div style={styles.buscaBox}>
                        <FiSearch size={16} color="#aaa" />
                        <input
                            style={styles.inputBusca}
                            value={busca}
                            onChange={e => setBusca(e.target.value)}
                            placeholder="Buscar usuario..."
                        />
                    </div>
                </div>

                {erroAcao && <p style={styles.msgErroAcao}>{erroAcao}</p>}
                {carregando && <p style={styles.msg}>Carregando...</p>}
                {erro && !carregando && <p style={styles.msgErro}>{erro}</p>}
                {!erro && !carregando && listagem.length === 0 && (
                    <p style={styles.msg}>Nenhum usuario encontrado.</p>
                )}

                <div style={styles.lista}>
                    {listagem.map(usuario => (
                        <div key={usuario.id} style={styles.card}>
                            <div style={styles.cardTopo}>
                                <div style={styles.avatar}>
                                    {usuario.avatarUrl
                                        ? <img src={usuario.avatarUrl} alt="" style={styles.avatarImg} />
                                        : <span>{inicialUsuario(usuario)}</span>}
                                </div>

                                <div style={styles.cardInfo}>
                                    <div style={styles.linhaPrincipal}>
                                        <span style={styles.cardTitulo}>{usuario.nome || 'Usuario sem nome'}</span>
                                        <span style={{
                                            ...styles.badge,
                                            ...(usuario.papel === 'ADMIN' ? styles.badgeAdmin : styles.badgeUsuario),
                                        }}>
                                            <FiShield size={12} />
                                            {usuario.papel}
                                        </span>
                                    </div>

                                    <div style={styles.detalhes}>
                                        <span style={styles.detalhe}><FiUser size={13} /> ID {usuario.id}</span>
                                        <span style={styles.detalhe}><FiMail size={13} /> {usuario.email}</span>
                                        <span style={styles.detalhe}><FiAtSign size={13} /> {usuario.apelido || '-'}</span>
                                    </div>

                                    {usuario.biografia && (
                                        <p style={styles.bio}>{usuario.biografia}</p>
                                    )}
                                </div>

                                <div style={styles.acoes}>
                                    <button style={styles.btnEditar} onClick={() => abrirEdicao(usuario)}>
                                        {editandoId === usuario.id ? <FiX size={14} /> : <FiEdit2 size={14} />}
                                        {editandoId === usuario.id ? 'Fechar' : 'Editar'}
                                    </button>
                                    <button style={styles.btnRemover} onClick={() => abrirModalRemocao(usuario)}>
                                        <FiTrash2 size={14} />
                                        Remover
                                    </button>
                                </div>
                            </div>

                            {editandoId === usuario.id && (
                                <div style={styles.formEdicao}>
                                    <div style={styles.gridForm}>
                                        <label style={styles.campo}>
                                            Nome
                                            <input style={styles.input} value={form.nome} onChange={e => atualizarCampo('nome', e.target.value)} />
                                        </label>
                                        <label style={styles.campo}>
                                            E-mail
                                            <input style={styles.input} value={form.email} onChange={e => atualizarCampo('email', e.target.value)} />
                                        </label>
                                        <label style={styles.campo}>
                                            Apelido
                                            <input style={styles.input} value={form.apelido} onChange={e => atualizarCampo('apelido', e.target.value)} />
                                        </label>
                                        <label style={styles.campo}>
                                            Papel
                                            <select style={styles.input} value={form.papel} onChange={e => atualizarCampo('papel', e.target.value)}>
                                                <option value="CINEFILO">CINEFILO</option>
                                                <option value="ADMIN">ADMIN</option>
                                            </select>
                                        </label>
                                        <label style={styles.campoLargo}>
                                            Avatar URL
                                            <input style={styles.input} value={form.avatarUrl} onChange={e => atualizarCampo('avatarUrl', e.target.value)} />
                                        </label>
                                        <label style={styles.campoLargo}>
                                            Biografia
                                            <textarea
                                                style={{ ...styles.input, ...styles.textarea }}
                                                value={form.biografia}
                                                onChange={e => atualizarCampo('biografia', e.target.value)}
                                            />
                                        </label>
                                    </div>

                                    <div style={styles.formAcoes}>
                                        <button style={styles.btnCancelar} onClick={fecharEdicao} disabled={salvando}>
                                            <FiX size={14} />
                                            Cancelar
                                        </button>
                                        <button style={styles.btnSalvar} onClick={() => salvarEdicao(usuario.id)} disabled={salvando}>
                                            <FiSave size={14} />
                                            {salvando ? 'Salvando...' : 'Salvar'}
                                        </button>
                                    </div>
                                </div>
                            )}
                        </div>
                    ))}
                </div>
            </div>

            {usuarioParaRemover && (
                <div style={styles.modalOverlay}>
                    <div style={styles.modal}>
                        <h3 style={styles.modalTitulo}>Remover usuario</h3>
                        <p style={styles.modalTexto}>
                            Tem certeza que deseja remover {usuarioParaRemover.nome || usuarioParaRemover.email}?
                        </p>
                        <div style={styles.modalAcoes}>
                            <button style={styles.btnCancelar} onClick={fecharModalRemocao} disabled={removendo}>
                                <FiX size={14} />
                                Cancelar
                            </button>
                            <button style={styles.btnRemoverConfirmar} onClick={confirmarRemocao} disabled={removendo}>
                                <FiTrash2 size={14} />
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

const styles = {
    pagina: {
        minHeight: '100vh',
        backgroundColor: '#0f3460',
        color: 'white',
    },
    conteudo: {
        maxWidth: '920px',
        margin: '0 auto',
        padding: '24px 32px',
    },
    cabecalho: {
        display: 'flex',
        alignItems: 'center',
        justifyContent: 'space-between',
        gap: '16px',
        marginBottom: '24px',
        flexWrap: 'wrap',
    },
    titulo: {
        fontSize: '16px',
        letterSpacing: '1px',
        margin: 0,
    },
    total: {
        display: 'block',
        color: '#aaa',
        fontSize: '12px',
        marginTop: '6px',
    },
    buscaBox: {
        display: 'flex',
        alignItems: 'center',
        gap: '8px',
        backgroundColor: '#16213e',
        border: '1px solid #2a2a4a',
        borderRadius: '8px',
        padding: '8px 12px',
        minWidth: '240px',
    },
    inputBusca: {
        border: 'none',
        backgroundColor: 'transparent',
        color: 'white',
        outline: 'none',
        fontSize: '13px',
        width: '100%',
    },
    lista: {
        display: 'flex',
        flexDirection: 'column',
        gap: '12px',
    },
    card: {
        backgroundColor: '#16213e',
        borderRadius: '10px',
        padding: '16px',
        border: '1px solid #22304f',
    },
    cardTopo: {
        display: 'flex',
        alignItems: 'center',
        gap: '16px',
    },
    avatar: {
        width: '56px',
        height: '56px',
        borderRadius: '8px',
        backgroundColor: '#0f3460',
        border: '1px solid #2a2a4a',
        display: 'flex',
        alignItems: 'center',
        justifyContent: 'center',
        color: '#e94560',
        fontWeight: 'bold',
        fontSize: '20px',
        flexShrink: 0,
        overflow: 'hidden',
    },
    avatarImg: {
        width: '100%',
        height: '100%',
        objectFit: 'cover',
    },
    cardInfo: {
        flex: 1,
        display: 'flex',
        flexDirection: 'column',
        gap: '8px',
        minWidth: 0,
    },
    linhaPrincipal: {
        display: 'flex',
        alignItems: 'center',
        justifyContent: 'flex-start',
        gap: '12px',
        flexWrap: 'wrap',
    },
    cardTitulo: {
        fontSize: '15px',
        fontWeight: 'bold',
    },
    badge: {
        display: 'inline-flex',
        alignItems: 'center',
        gap: '6px',
        padding: '4px 8px',
        borderRadius: '6px',
        fontSize: '11px',
        fontWeight: 'bold',
        letterSpacing: '0.5px',
    },
    badgeAdmin: {
        color: '#f97316',
        border: '1px solid #f97316',
    },
    badgeUsuario: {
        color: '#10b981',
        border: '1px solid #10b981',
    },
    detalhes: {
        display: 'flex',
        flexWrap: 'wrap',
        gap: '8px 14px',
    },
    detalhe: {
        display: 'inline-flex',
        alignItems: 'center',
        gap: '6px',
        color: '#aaa',
        fontSize: '12px',
        minWidth: 0,
    },
    bio: {
        color: '#888',
        fontSize: '12px',
        margin: 0,
        lineHeight: 1.4,
    },
    acoes: {
        display: 'flex',
        flexDirection: 'column',
        gap: '8px',
        flexShrink: 0,
    },
    btnEditar: {
        display: 'inline-flex',
        alignItems: 'center',
        justifyContent: 'center',
        gap: '6px',
        padding: '7px 12px',
        borderRadius: '6px',
        border: '1px solid #f97316',
        backgroundColor: 'transparent',
        color: '#f97316',
        cursor: 'pointer',
        fontSize: '12px',
        fontWeight: 'bold',
    },
    btnRemover: {
        display: 'inline-flex',
        alignItems: 'center',
        justifyContent: 'center',
        gap: '6px',
        padding: '7px 12px',
        borderRadius: '6px',
        border: '1px solid #e94560',
        backgroundColor: 'transparent',
        color: '#e94560',
        cursor: 'pointer',
        fontSize: '12px',
        fontWeight: 'bold',
    },
    formEdicao: {
        marginTop: '14px',
        paddingTop: '14px',
        borderTop: '1px solid #2a2a4a',
    },
    gridForm: {
        display: 'grid',
        gridTemplateColumns: 'repeat(auto-fit, minmax(220px, 1fr))',
        gap: '12px',
    },
    campo: {
        display: 'flex',
        flexDirection: 'column',
        gap: '6px',
        color: '#aaa',
        fontSize: '11px',
        fontWeight: 'bold',
        letterSpacing: '0.5px',
    },
    campoLargo: {
        display: 'flex',
        flexDirection: 'column',
        gap: '6px',
        color: '#aaa',
        fontSize: '11px',
        fontWeight: 'bold',
        letterSpacing: '0.5px',
        gridColumn: '1 / -1',
    },
    input: {
        padding: '10px 12px',
        borderRadius: '6px',
        border: '1px solid #2a2a4a',
        backgroundColor: '#0f3460',
        color: 'white',
        outline: 'none',
        fontSize: '13px',
        fontFamily: 'inherit',
    },
    textarea: {
        minHeight: '74px',
        resize: 'vertical',
    },
    formAcoes: {
        display: 'flex',
        justifyContent: 'flex-end',
        gap: '8px',
        marginTop: '12px',
    },
    btnCancelar: {
        display: 'inline-flex',
        alignItems: 'center',
        gap: '6px',
        padding: '8px 14px',
        borderRadius: '6px',
        border: '1px solid #aaa',
        backgroundColor: 'transparent',
        color: '#aaa',
        cursor: 'pointer',
        fontSize: '13px',
    },
    btnSalvar: {
        display: 'inline-flex',
        alignItems: 'center',
        gap: '6px',
        padding: '8px 14px',
        borderRadius: '6px',
        border: 'none',
        backgroundColor: '#f97316',
        color: 'white',
        cursor: 'pointer',
        fontSize: '13px',
        fontWeight: 'bold',
    },
    msg: {
        color: '#aaa',
        textAlign: 'center',
        marginTop: '60px',
    },
    msgErro: {
        color: '#e94560',
        textAlign: 'center',
        marginTop: '60px',
    },
    msgErroAcao: {
        color: '#e94560',
        backgroundColor: 'rgba(233, 69, 96, 0.1)',
        border: '1px solid rgba(233, 69, 96, 0.35)',
        borderRadius: '8px',
        padding: '10px 12px',
        fontSize: '13px',
        marginBottom: '12px',
    },
    modalOverlay: {
        position: 'fixed',
        inset: 0,
        backgroundColor: 'rgba(0, 0, 0, 0.55)',
        display: 'flex',
        alignItems: 'center',
        justifyContent: 'center',
        padding: '20px',
        zIndex: 1000,
    },
    modal: {
        width: '100%',
        maxWidth: '420px',
        backgroundColor: '#16213e',
        border: '1px solid #2a2a4a',
        borderRadius: '8px',
        padding: '20px',
        boxShadow: '0 20px 45px rgba(0, 0, 0, 0.35)',
    },
    modalTitulo: {
        margin: 0,
        fontSize: '16px',
        letterSpacing: '0.5px',
    },
    modalTexto: {
        color: '#aaa',
        fontSize: '13px',
        lineHeight: 1.5,
        margin: '12px 0 18px',
    },
    modalAcoes: {
        display: 'flex',
        justifyContent: 'flex-end',
        gap: '8px',
    },
    btnRemoverConfirmar: {
        display: 'inline-flex',
        alignItems: 'center',
        gap: '6px',
        padding: '8px 14px',
        borderRadius: '6px',
        border: 'none',
        backgroundColor: '#e94560',
        color: 'white',
        cursor: 'pointer',
        fontSize: '13px',
        fontWeight: 'bold',
    },
};
