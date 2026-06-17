import { useEffect, useMemo, useState } from 'react';
import { FiAtSign, FiEdit2, FiFileText, FiImage, FiMail, FiSave, FiShield, FiUser, FiX } from 'react-icons/fi';
import Navbar from '../../components/Navbar';
import { usuarioService } from '../../services/api';

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
            setErro('Erro ao carregar seu usuario.');
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

    async function salvarUsuario() {
        setSalvando(true);
        setErroAcao(null);

        try {
            const usuarioAtualizado = await usuarioService.editarMeuUsuario(form);
            setUsuario(usuarioAtualizado);
            setForm(formDeUsuario(usuarioAtualizado));
            setEditando(false);
        } catch {
            setErroAcao('Erro ao salvar seu usuario.');
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

    return (
        <div style={styles.pagina}>
            <Navbar />

            <main style={styles.conteudo}>
                <div style={styles.cabecalho}>
                    <div>
                        <h2 style={styles.titulo}>MEU USUARIO</h2>
                        <span style={styles.subtitulo}>Dados da sua conta</span>
                    </div>

                    {usuario && !editando && (
                        <button style={styles.btnEditar} onClick={iniciarEdicao} disabled={carregando}>
                            <FiEdit2 size={14} />
                            Editar
                        </button>
                    )}
                </div>

                {carregando && <p style={styles.msg}>Carregando...</p>}
                {erro && !carregando && <p style={styles.msgErro}>{erro}</p>}
                {erroAcao && <p style={styles.msgErroAcao}>{erroAcao}</p>}

                {usuario && !carregando && (
                    <section style={styles.usuarioBox}>
                        <div style={styles.topoUsuario}>
                            <div style={styles.avatar}>
                                {usuario.avatarUrl
                                    ? <img src={usuario.avatarUrl} alt="" style={styles.avatarImg} />
                                    : <span>{inicialUsuario(usuario)}</span>}
                            </div>

                            <div style={styles.identidade}>
                                <div style={styles.nomeLinha}>
                                    <h1 style={styles.nome}>{usuario.nome || 'Usuario sem nome'}</h1>
                                    {usuario.papel && (
                                        <span style={{
                                            ...styles.badge,
                                            ...(usuario.papel === 'ADMIN' ? styles.badgeAdmin : styles.badgeUsuario),
                                        }}>
                                            <FiShield size={12} />
                                            {usuario.papel}
                                        </span>
                                    )}
                                </div>
                                <span style={styles.email}>{usuario.email || 'E-mail nao informado'}</span>
                            </div>
                        </div>

                        {editando ? (
                            <div style={styles.formEdicao}>
                                <div style={styles.gridForm}>
                                    <label style={styles.campo}>
                                        Nome
                                        <input
                                            style={styles.input}
                                            value={form.nome}
                                            onChange={e => atualizarCampo('nome', e.target.value)}
                                        />
                                    </label>

                                    <label style={styles.campo}>
                                        E-mail
                                        <input style={{ ...styles.input, ...styles.inputBloqueado }} value={usuario.email || ''} disabled />
                                    </label>

                                    <label style={styles.campo}>
                                        Tipo
                                        <input style={{ ...styles.input, ...styles.inputBloqueado }} value={usuario.papel || ''} disabled />
                                    </label>

                                    <label style={styles.campo}>
                                        Apelido
                                        <input
                                            style={styles.input}
                                            value={form.apelido}
                                            onChange={e => atualizarCampo('apelido', e.target.value)}
                                        />
                                    </label>

                                    <label style={styles.campoLargo}>
                                        URL da imagem
                                        <div style={styles.inputIconeBox}>
                                            <FiImage size={15} color="#aaa" />
                                            <input
                                                style={styles.inputComIcone}
                                                value={form.avatarUrl}
                                                onChange={e => atualizarCampo('avatarUrl', e.target.value)}
                                                placeholder="https://exemplo.com/minha-foto.jpg"
                                            />
                                        </div>
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
                                    <button style={styles.btnCancelar} onClick={cancelarEdicao} disabled={salvando}>
                                        <FiX size={14} />
                                        Cancelar
                                    </button>
                                    <button style={styles.btnSalvar} onClick={salvarUsuario} disabled={salvando}>
                                        <FiSave size={14} />
                                        {salvando ? 'Salvando...' : 'Salvar'}
                                    </button>
                                </div>
                            </div>
                        ) : (
                            <div style={styles.grade}>
                                {campos.map(campo => (
                                    <div
                                        key={campo.chave}
                                        style={campo.chave === 'biografia' ? { ...styles.cardCampo, ...styles.cardLargo } : styles.cardCampo}
                                    >
                                        <span style={styles.campoRotulo}>
                                            {campo.icone}
                                            {campo.rotulo}
                                        </span>
                                        <span style={styles.campoValor}>{campo.valor}</span>
                                    </div>
                                ))}
                            </div>
                        )}
                    </section>
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
    subtitulo: {
        display: 'block',
        color: '#aaa',
        fontSize: '12px',
        marginTop: '6px',
    },
    btnEditar: {
        display: 'inline-flex',
        alignItems: 'center',
        justifyContent: 'center',
        gap: '6px',
        padding: '8px 12px',
        borderRadius: '6px',
        border: '1px solid #e94560',
        backgroundColor: 'transparent',
        color: '#e94560',
        cursor: 'pointer',
        fontSize: '12px',
        fontWeight: 'bold',
    },
    usuarioBox: {
        backgroundColor: '#16213e',
        borderRadius: '10px',
        border: '1px solid #22304f',
        padding: '18px',
    },
    topoUsuario: {
        display: 'flex',
        alignItems: 'center',
        gap: '16px',
        paddingBottom: '18px',
        borderBottom: '1px solid #2a2a4a',
    },
    avatar: {
        width: '76px',
        height: '76px',
        borderRadius: '8px',
        backgroundColor: '#0f3460',
        border: '1px solid #2a2a4a',
        display: 'flex',
        alignItems: 'center',
        justifyContent: 'center',
        color: '#e94560',
        fontWeight: 'bold',
        fontSize: '28px',
        flexShrink: 0,
        overflow: 'hidden',
    },
    avatarImg: {
        width: '100%',
        height: '100%',
        objectFit: 'cover',
    },
    identidade: {
        display: 'flex',
        flexDirection: 'column',
        gap: '6px',
        minWidth: 0,
    },
    nomeLinha: {
        display: 'flex',
        alignItems: 'center',
        gap: '10px',
        flexWrap: 'wrap',
    },
    nome: {
        margin: 0,
        fontSize: '22px',
        lineHeight: 1.2,
    },
    email: {
        color: '#aaa',
        fontSize: '13px',
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
    grade: {
        display: 'grid',
        gridTemplateColumns: 'repeat(auto-fit, minmax(220px, 1fr))',
        gap: '12px',
        marginTop: '18px',
    },
    cardCampo: {
        backgroundColor: '#0f3460',
        border: '1px solid #2a2a4a',
        borderRadius: '8px',
        padding: '14px',
        display: 'flex',
        flexDirection: 'column',
        gap: '8px',
        minWidth: 0,
    },
    cardLargo: {
        gridColumn: '1 / -1',
    },
    campoRotulo: {
        display: 'inline-flex',
        alignItems: 'center',
        gap: '8px',
        color: '#aaa',
        fontSize: '11px',
        fontWeight: 'bold',
        letterSpacing: '0.5px',
    },
    campoValor: {
        color: 'white',
        fontSize: '14px',
        lineHeight: 1.5,
        overflowWrap: 'anywhere',
        whiteSpace: 'pre-wrap',
    },
    formEdicao: {
        marginTop: '18px',
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
    inputBloqueado: {
        color: '#aaa',
        cursor: 'not-allowed',
        opacity: 0.8,
    },
    inputIconeBox: {
        display: 'flex',
        alignItems: 'center',
        gap: '8px',
        padding: '0 12px',
        borderRadius: '6px',
        border: '1px solid #2a2a4a',
        backgroundColor: '#0f3460',
    },
    inputComIcone: {
        width: '100%',
        padding: '10px 0',
        border: 'none',
        backgroundColor: 'transparent',
        color: 'white',
        outline: 'none',
        fontSize: '13px',
        fontFamily: 'inherit',
    },
    textarea: {
        minHeight: '90px',
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
        backgroundColor: '#e94560',
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
};
