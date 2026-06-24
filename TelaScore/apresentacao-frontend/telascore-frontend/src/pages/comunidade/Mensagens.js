import { useEffect, useMemo, useState } from 'react';
import { FiEdit2, FiMessageCircle, FiSend, FiSmile, FiTrash2, FiUser } from 'react-icons/fi';
import { useAuth } from '../../context/AuthContext';
import Navbar from '../../components/Navbar';
import { amigoService, mensagemPrivadaService } from '../../services/api';
import './Mensagens.css';

const FIGURINHAS_RAPIDAS = ['🎬', '🍿', '⭐', '🔥', '😭', '👏', '🤯', '❤️'];

function formatarHorario(mensagem) {
    const valor = mensagem?.dataEnvio || mensagem?.data;
    if (!valor) return '';
    const data = new Date(valor);
    if (Number.isNaN(data.getTime())) return valor;
    return data.toLocaleString('pt-BR', {
        day: '2-digit',
        month: '2-digit',
        hour: '2-digit',
        minute: '2-digit'
    });
}

export default function Mensagens() {
    const { sessao } = useAuth();
    const [mensagens, setMensagens] = useState([]);
    const [contatos, setContatos] = useState([]);
    const [chatAtivo, setChatAtivo] = useState(null);
    const [mensagemDigitada, setMensagemDigitada] = useState('');
    const [mensagemEditando, setMensagemEditando] = useState(null);
    const [mostrarFigurinhas, setMostrarFigurinhas] = useState(false);
    const [confirmarExclusao, setConfirmarExclusao] = useState(null);
    const [carregandoContatos, setCarregandoContatos] = useState(true);
    const [carregandoMensagens, setCarregandoMensagens] = useState(false);
    const [erro, setErro] = useState('');

    const usuarioAtual = useMemo(
        () => contatos.find((contato) => contato.id === chatAtivo),
        [contatos, chatAtivo]
    );

    useEffect(() => {
        async function carregarAmigos() {
            if (!sessao?.id) return;
            setCarregandoContatos(true);
            setErro('');
            try {
                const amigos = await amigoService.listarAmigos(sessao.id);
                const lista = Array.isArray(amigos) ? amigos : [];
                setContatos(lista);
                setChatAtivo((atual) => {
                    if (atual && lista.some((amigo) => amigo.id === atual)) return atual;
                    return lista[0]?.id || null;
                });
            } catch (error) {
                console.error('Erro ao carregar amigos:', error);
                setErro(error.message || 'Nao foi possivel carregar seus amigos.');
                setContatos([]);
                setChatAtivo(null);
            } finally {
                setCarregandoContatos(false);
            }
        }

        carregarAmigos();
    }, [sessao?.id]);

    useEffect(() => {
        async function carregarConversa() {
            if (!sessao?.id || !chatAtivo) {
                setMensagens([]);
                return;
            }
            setCarregandoMensagens(true);
            setErro('');
            try {
                const historico = await mensagemPrivadaService.listarConversa(sessao.id, chatAtivo);
                setMensagens(Array.isArray(historico) ? historico : []);
            } catch (error) {
                console.error('Erro ao carregar conversa:', error);
                setErro(error.message || 'Nao foi possivel carregar esta conversa.');
                setMensagens([]);
            } finally {
                setCarregandoMensagens(false);
            }
        }

        carregarConversa();
    }, [sessao?.id, chatAtivo]);

    const enviarMensagem = async (texto, figurinha = null) => {
        if (!sessao?.id || !chatAtivo) return;
        const conteudo = texto.trim();
        if (!conteudo && !figurinha) return;

        try {
            setErro('');
            if (mensagemEditando) {
                const atualizada = await mensagemPrivadaService.editar(mensagemEditando, {
                    usuarioId: sessao.id,
                    texto: conteudo
                });
                setMensagens((atuais) =>
                    atuais.map((mensagem) => mensagem.id === mensagemEditando ? atualizada : mensagem)
                );
                setMensagemEditando(null);
            } else {
                const criada = await mensagemPrivadaService.enviar({
                    remetenteId: sessao.id,
                    destinatarioId: chatAtivo,
                    texto: conteudo,
                    figurinha
                });
                setMensagens((atuais) => [...atuais, criada]);
            }
            setMensagemDigitada('');
            setMostrarFigurinhas(false);
        } catch (error) {
            console.error('Erro ao salvar mensagem:', error);
            setErro(error.message || 'Nao foi possivel salvar a mensagem.');
        }
    };

    const deletarMensagem = async () => {
        if (!confirmarExclusao || !sessao?.id) return;
        try {
            setErro('');
            await mensagemPrivadaService.remover(confirmarExclusao.id, sessao.id);
            setMensagens((atuais) => atuais.filter((mensagem) => mensagem.id !== confirmarExclusao.id));
            setConfirmarExclusao(null);
        } catch (error) {
            console.error('Erro ao apagar mensagem:', error);
            setErro(error.message || 'Nao foi possivel apagar a mensagem.');
        }
    };

    const iniciarEdicao = (mensagem) => {
        setMensagemEditando(mensagem.id);
        setMensagemDigitada(mensagem.texto || '');
        setMostrarFigurinhas(false);
    };

    const cancelarEdicao = () => {
        setMensagemEditando(null);
        setMensagemDigitada('');
    };

    return (
        <div className="comunidades-page">
            <Navbar />
            <main className="comunidades-container mensagens-container">
                <div className="chat-painel mensagens-painel">
                    <div className="mensagens-cabecalho">
                        <div className="mensagens-titulo">
                            <div className="chat-header-icon">
                                <FiMessageCircle size={20} />
                            </div>
                            <div>
                                <h3>{usuarioAtual?.nome || usuarioAtual?.apelido || 'Mensagens privadas'}</h3>
                                <small>{usuarioAtual ? '@' + (usuarioAtual.apelido || usuarioAtual.nome) : 'Converse com seus amigos'}</small>
                            </div>
                        </div>
                    </div>

                    {erro && <div className="mensagens-alerta">{erro}</div>}

                    <div className="mensagens-layout">
                        <section className="chat-zona-mensagens mensagens-lista">
                            {carregandoMensagens && <div className="mensagens-vazio">Carregando conversa...</div>}

                            {!carregandoMensagens && !chatAtivo && (
                                <div className="mensagens-vazio">
                                    <FiUser size={28} />
                                    <strong>Nenhum amigo selecionado</strong>
                                    <span>Adicione amigos para abrir conversas privadas.</span>
                                </div>
                            )}

                            {!carregandoMensagens && chatAtivo && mensagens.length === 0 && (
                                <div className="mensagens-vazio">
                                    <FiMessageCircle size={28} />
                                    <strong>Comece a conversa</strong>
                                    <span>As mensagens aparecem aqui assim que forem enviadas.</span>
                                </div>
                            )}

                            {!carregandoMensagens && mensagens.map((mensagem) => {
                                const minha = mensagem.remetenteId === sessao?.id;
                                return (
                                    <article key={mensagem.id} className={`chat-balao ${minha ? 'meu' : 'outro'}`}>
                                        <div className="mensagem-meta">
                                            <strong>{minha ? 'Voce' : (usuarioAtual?.nome || usuarioAtual?.apelido)}</strong>
                                            <span>{formatarHorario(mensagem)}</span>
                                        </div>
                                        <p>{mensagem.texto}</p>
                                        {minha && (
                                            <div className="mensagem-acoes">
                                                <button type="button" onClick={() => iniciarEdicao(mensagem)}>
                                                    <FiEdit2 size={12} /> Editar
                                                </button>
                                                <button type="button" onClick={() => setConfirmarExclusao(mensagem)}>
                                                    <FiTrash2 size={12} /> Apagar
                                                </button>
                                            </div>
                                        )}
                                    </article>
                                );
                            })}
                        </section>

                        <aside className="chat-zona-membros mensagens-contatos">
                            <small>Amigos</small>
                            {carregandoContatos && <span className="mensagens-contato-vazio">Carregando...</span>}
                            {!carregandoContatos && contatos.length === 0 && (
                                <span className="mensagens-contato-vazio">Nenhum amigo encontrado.</span>
                            )}
                            {contatos.map((contato) => (
                                <button
                                    key={contato.id}
                                    type="button"
                                    className={`membro-linha mensagens-contato ${chatAtivo === contato.id ? 'ativo' : ''}`}
                                    onClick={() => {
                                        setChatAtivo(contato.id);
                                        setMensagemEditando(null);
                                        setMensagemDigitada('');
                                    }}
                                >
                                    <span>{contato.nome || contato.apelido}</span>
                                    <small>@{contato.apelido || contato.nome}</small>
                                </button>
                            ))}
                        </aside>
                    </div>

                    <div className="mensagens-compositor">
                        {mostrarFigurinhas && (
                            <div className="mensagens-figurinhas">
                                {FIGURINHAS_RAPIDAS.map((figurinha) => (
                                    <button key={figurinha} type="button" onClick={() => enviarMensagem('', figurinha)}>
                                        {figurinha}
                                    </button>
                                ))}
                            </div>
                        )}

                        <button
                            type="button"
                            className="mensagens-icon-button"
                            disabled={!chatAtivo || Boolean(mensagemEditando)}
                            onClick={() => setMostrarFigurinhas((atual) => !atual)}
                            title="Figurinhas rapidas"
                        >
                            <FiSmile size={22} />
                        </button>

                        <input
                            type="text"
                            placeholder={mensagemEditando ? 'Editando mensagem...' : 'Enviar mensagem...'}
                            value={mensagemDigitada}
                            disabled={!chatAtivo}
                            onChange={(event) => setMensagemDigitada(event.target.value)}
                            onKeyDown={(event) => {
                                if (event.key === 'Enter') enviarMensagem(mensagemDigitada);
                                if (event.key === 'Escape' && mensagemEditando) cancelarEdicao();
                            }}
                        />

                        {mensagemEditando && (
                            <button type="button" className="mensagens-cancelar" onClick={cancelarEdicao}>
                                Cancelar
                            </button>
                        )}

                        <button
                            type="button"
                            className="mensagens-enviar"
                            disabled={!chatAtivo || mensagemDigitada.trim() === ''}
                            onClick={() => enviarMensagem(mensagemDigitada)}
                            title="Enviar mensagem"
                        >
                            <FiSend />
                        </button>
                    </div>
                </div>
            </main>

            {confirmarExclusao && (
                <div className="mensagens-modal-backdrop" role="presentation">
                    <div className="mensagens-modal" role="dialog" aria-modal="true">
                        <h3>Apagar mensagem?</h3>
                        <p>Essa mensagem sera removida da conversa.</p>
                        <div>
                            <button type="button" onClick={() => setConfirmarExclusao(null)}>Cancelar</button>
                            <button type="button" className="danger" onClick={deletarMensagem}>Apagar</button>
                        </div>
                    </div>
                </div>
            )}
        </div>
    );
}
