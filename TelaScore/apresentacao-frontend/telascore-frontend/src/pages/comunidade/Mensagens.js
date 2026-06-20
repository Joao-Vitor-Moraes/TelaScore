import { useState, useEffect } from 'react';
import { FiSend, FiSmile, FiUser, FiMoreVertical, FiEdit2, FiTrash2 } from 'react-icons/fi';
import { useAuth } from '../../context/AuthContext'; 
import Navbar from '../../components/Navbar';
import { mensagemPrivadaService, figurinhaService, usuarioService } from '../../services/api'; // <-- Importando a API!
import './Mensagens.css';

export default function Mensagens() {
    const { sessao } = useAuth(); 
    
    // Estados conectados com o Banco de Dados
    const [mensagens, setMensagens] = useState([]);
    const [figurinhas, setFigurinhas] = useState([]);
    const [contatos, setContatos] = useState([]); 
    const [chatAtivo, setChatAtivo] = useState(null);
    
    // Estados de Interface
    const [modalAdminAberto, setModalAdminAberto] = useState(false);
    const [novaFigurinha, setNovaFigurinha] = useState({ nome: '', emoji: '' });
    const [mensagemDigitada, setMensagemDigitada] = useState(''); 
    const [mostrarFigurinhas, setMostrarFigurinhas] = useState(false);
    const [mensagemEditando, setMensagemEditando] = useState(null); 

    // 1. CARREGAR CONTATOS E FIGURINHAS AO ABRIR A TELA
    useEffect(() => {
        async function carregarDadosIniciais() {
            try {
                // Busca as figurinhas do Java
                const figs = await figurinhaService.listar();
                if (figs) setFigurinhas(figs);

                // Busca todos os usuários do sistema para montar a lista de contatos
                const usuarios = await usuarioService.listar();
                if (usuarios) {
                    const listaFiltrada = usuarios.filter(u => u.id !== sessao?.id); // Esconde você mesmo
                    setContatos(listaFiltrada);
                    if (listaFiltrada.length > 0) setChatAtivo(listaFiltrada[0].id); // Seleciona o 1º contato
                }
            } catch (error) {
                console.error("Erro ao carregar dados iniciais:", error);
            }
        }
        if (sessao) carregarDadosIniciais();
    }, [sessao]);

    // 2. CARREGAR MENSAGENS QUANDO CLICAR EM UM CONTATO
    useEffect(() => {
        async function carregarChat() {
            if (!chatAtivo) return;
            try {
                const historico = await mensagemPrivadaService.listarPorDestinatario(chatAtivo);
                setMensagens(historico || []);
            } catch (error) {
                console.error("Erro ao carregar o chat:", error);
                setMensagens([]); // Se o backend não tiver a rota pronta, não quebra a tela
            }
        }
        carregarChat();
    }, [chatAtivo]);

    // 3. ENVIAR OU EDITAR MENSAGEM (JAVA)
    const enviarMensagem = async (texto, figurinha = null) => {
        if (!texto.trim() && !figurinha) return; 

        try {
            if (mensagemEditando !== null) {
                // EDITAR NO BANCO
                await mensagemPrivadaService.editar(mensagemEditando, { texto });
                setMensagens(mensagens.map(msg => msg.id === mensagemEditando ? { ...msg, texto: texto } : msg));
                setMensagemEditando(null); 
            } else {
                // ENVIAR NOVA PRO BANCO
                const payload = {
                    destinatarioId: chatAtivo,
                    texto: texto,
                    figurinha: figurinha // Mandamos a string do emoji/nome
                };
                
                const msgSalva = await mensagemPrivadaService.enviar(payload);
                
                // Se o Java não retornar a mensagem salva, criamos uma temporária para aparecer na tela rápido
                const novaMsgLocal = msgSalva || {
                    id: Date.now(), 
                    remetenteId: sessao?.id,
                    usuario: sessao?.nome || "Você",
                    texto: texto,
                    figurinha: figurinha,
                    data: new Date().toLocaleTimeString('pt-BR', { hour: '2-digit', minute: '2-digit' })
                };
                setMensagens([...mensagens, novaMsgLocal]);
            }
            setMensagemDigitada(''); 
            setMostrarFigurinhas(false);
        } catch (error) {
            console.error("Erro ao enviar mensagem:", error);
            alert("A conexão com o servidor falhou.");
        }
    };

    // 4. DELETAR MENSAGEM (JAVA)
    const deletarMensagem = async (id) => {
        if (window.confirm("Tem certeza que deseja apagar esta mensagem?")) {
            try {
                await mensagemPrivadaService.remover(id);
                setMensagens(mensagens.filter(msg => msg.id !== id));
            } catch (error) {
                console.error("Erro ao deletar:", error);
                alert("Não foi possível apagar a mensagem.");
            }
        }
    };

    const iniciarEdicao = (msg) => {
        setMensagemDigitada(msg.texto);
        setMensagemEditando(msg.id);
    };

    // 5. FUNÇÕES DO ADMINISTRADOR (FIGURINHAS NO JAVA)
    const adicionarFigurinha = async () => {
        if (!novaFigurinha.nome || !novaFigurinha.emoji) return;
        try {
            const figSalva = await figurinhaService.criar(novaFigurinha);
            setFigurinhas([...figurinhas, figSalva || { id: Date.now(), ...novaFigurinha }]);
            setNovaFigurinha({ nome: '', emoji: '' }); 
        } catch (error) {
            console.error("Erro ao criar figurinha", error);
            alert("Não foi possível salvar a figurinha no servidor.");
        }
    };

    const deletarFigurinha = async (id) => {
        if (window.confirm("Excluir figurinha permanentemente?")) {
            try {
                await figurinhaService.remover(id);
                setFigurinhas(figurinhas.filter(f => f.id !== id));
            } catch (error) {
                console.error("Erro ao excluir figurinha", error);
            }
        }
    };

    const usuarioAtual = contatos.find(m => m.id === chatAtivo);

    return (
        <div className="comunidades-page">
            <Navbar />
            <main className="comunidades-container" style={{ margin: '0 auto' }}>
                <div className="comunidades-grid" style={{ gridTemplateColumns: '1fr' }}>
                    <div className="chat-painel" style={{ height: 'calc(100vh - 180px)', minHeight: '500px', position: 'relative' }}>
                        
                        {/* Linha 1: Cabeçalho */}
                        <div style={{ display: 'flex', alignItems: 'center', justifyContent: 'space-between', borderBottom: '1px solid var(--line)', paddingBottom: '15px' }}>
                            <div style={{ display: 'flex', alignItems: 'center', gap: '15px' }}>
                                <div className="chat-header-icon">
                                    <FiUser size={20} />
                                </div>
                                <div>
                                    <h3 style={{ margin: 0, fontSize: '1.2rem' }}>{usuarioAtual?.nome || "Carregando..."}</h3>
                                    <small style={{ opacity: 0.6 }}>Mensagem Privada</small>
                                </div>
                            </div>

                            {sessao?.papel === 'ADMIN' && (
                                <button className="goal-deadline-button" style={{ background: 'transparent', border: '1px solid var(--brand)', color: 'white', padding: '8px 15px', fontSize: '12px' }} onClick={() => setModalAdminAberto(!modalAdminAberto)}>
                                    Gerenciar Figurinhas
                                </button>
                            )}
                        </div>

                        {/* MODAL DO ADMINISTRADOR */}
                        {modalAdminAberto && sessao?.papel === 'ADMIN' && (
                            <div style={{ position: 'absolute', top: '70px', right: '20px', width: '300px', background: '#1c1c21', border: '1px solid var(--line)', borderRadius: '12px', padding: '20px', zIndex: 100, boxShadow: '0 10px 30px rgba(0,0,0,0.5)' }}>
                                <div style={{ display: 'flex', justifyContent: 'space-between', marginBottom: '15px' }}>
                                    <h4 style={{ margin: 0 }}>Figurinhas</h4>
                                    <button onClick={() => setModalAdminAberto(false)} style={{ background: 'none', border: 'none', color: 'white', cursor: 'pointer' }}>X</button>
                                </div>
                                <div style={{ display: 'flex', gap: '5px', marginBottom: '15px' }}>
                                    <input type="text" placeholder="Nome" value={novaFigurinha.nome} onChange={(e) => setNovaFigurinha({...novaFigurinha, nome: e.target.value})} style={{ flex: 1, padding: '8px', borderRadius: '4px', border: '1px solid rgba(255,255,255,0.1)', background: 'transparent', color: 'white' }} />
                                    <input type="text" placeholder="Emoji" value={novaFigurinha.emoji} onChange={(e) => setNovaFigurinha({...novaFigurinha, emoji: e.target.value})} style={{ width: '60px', padding: '8px', borderRadius: '4px', border: '1px solid rgba(255,255,255,0.1)', background: 'transparent', color: 'white' }} />
                                </div>
                                <button onClick={adicionarFigurinha} style={{ width: '100%', background: 'var(--brand)', color: 'white', border: 'none', padding: '8px', borderRadius: '4px', cursor: 'pointer', marginBottom: '15px' }}>Adicionar</button>
                                
                                <div style={{ maxHeight: '150px', overflowY: 'auto' }}>
                                    {figurinhas.map(fig => (
                                        <div key={fig.id} style={{ display: 'flex', justifyContent: 'space-between', padding: '8px 0', borderTop: '1px solid rgba(255,255,255,0.05)' }}>
                                            <span>{fig.nome} ({fig.emoji})</span>
                                            <button onClick={() => deletarFigurinha(fig.id)} style={{ background: 'none', border: 'none', color: '#ff6975', cursor: 'pointer' }}><FiTrash2 /></button>
                                        </div>
                                    ))}
                                </div>
                            </div>
                        )}

                        {/* Linha 2: Área Principal */}
                        <div style={{ display: 'grid', gridTemplateColumns: '1fr 220px', overflow: 'hidden' }}>
                            
                            {/* Zona de Mensagens */}
                            <div className="chat-zona-mensagens">
                                {mensagens.map(msg => {
                                    // Compara o ID de quem mandou com o seu ID para colocar na Direita ou Esquerda
                                    const isMeu = msg.remetenteId === sessao?.id;
                                    return (
                                        <div key={msg.id} className={`chat-balao ${isMeu ? 'meu' : 'outro'}`}>
                                            <div style={{ display: 'flex', justifyContent: 'space-between', marginBottom: '4px', opacity: 0.7, fontSize: '11px' }}>
                                                <strong>{isMeu ? 'Você' : (msg.usuario || usuarioAtual?.nome)}</strong>
                                                <span>{msg.data}</span>
                                            </div>
                                            {msg.texto && <div>{msg.texto}</div>}
                                            {msg.figurinha && <div style={{ fontSize: '2.5rem', marginTop: '5px' }}>{msg.figurinha}</div>}

                                            {isMeu && (
                                                <div style={{ display: 'flex', justifyContent: 'flex-end', gap: '10px', marginTop: '8px', borderTop: '1px solid rgba(255,255,255,0.1)', paddingTop: '5px' }}>
                                                    <button onClick={() => iniciarEdicao(msg)} style={{ background: 'none', border: 'none', color: '#f6c969', fontSize: '11px', cursor: 'pointer', display: 'flex', alignItems: 'center', gap: '3px' }}><FiEdit2 size={10} /> Editar</button>
                                                    <button onClick={() => deletarMensagem(msg.id)} style={{ background: 'none', border: 'none', color: '#ff6975', fontSize: '11px', cursor: 'pointer', display: 'flex', alignItems: 'center', gap: '3px' }}><FiTrash2 size={10} /> Apagar</button>
                                                </div>
                                            )}
                                        </div>
                                    )
                                })}
                            </div>

                            {/* Zona de Membros (Vem do Banco de Dados) */}
                            <div className="chat-zona-membros">
                                <small style={{ opacity: 0.5, marginBottom: '10px', display: 'block' }}>Contatos</small>
                                {contatos.length === 0 && <small style={{opacity: 0.6}}>Nenhum usuário encontrado.</small>}
                                {contatos.map(membro => (
                                    <div 
                                        key={membro.id} 
                                        className="membro-linha"
                                        style={{ 
                                            background: chatAtivo === membro.id ? 'rgba(229, 9, 20, 0.1)' : 'transparent',
                                            borderLeft: chatAtivo === membro.id ? '3px solid var(--brand)' : '3px solid transparent',
                                            cursor: 'pointer', padding: '8px'
                                        }}
                                        onClick={() => setChatAtivo(membro.id)}
                                    >
                                        <span style={{ fontSize: '14px', fontWeight: chatAtivo === membro.id ? 'bold' : 'normal' }}>
                                            {membro.nome || membro.apelido}
                                        </span>
                                    </div>
                                ))}
                            </div>
                        </div>

                        {/* Linha 3: Input de Texto */}
                        <div style={{ position: 'relative', display: 'flex', gap: '10px', alignItems: 'center', marginTop: '15px' }}>
                            {mostrarFigurinhas && (
                                <div style={{ position: 'absolute', bottom: '60px', left: '0', background: '#202025', border: '1px solid var(--line)', borderRadius: '12px', padding: '10px', display: 'grid', gridTemplateColumns: 'repeat(3, 1fr)', gap: '10px', zIndex: 10 }}>
                                    {figurinhas.length === 0 && <small>Vazio</small>}
                                    {figurinhas.map(fig => (
                                        <button key={fig.id} style={{ fontSize: '1.8rem', background: 'transparent', border: 'none', cursor: 'pointer', transition: 'transform 0.2s' }} onClick={() => enviarMensagem('', fig.emoji)}>{fig.emoji}</button>
                                    ))}
                                </div>
                            )}

                            <button style={{ background: 'transparent', border: 'none', color: 'white', cursor: 'pointer', padding: '10px' }} onClick={() => setMostrarFigurinhas(!mostrarFigurinhas)}>
                                <FiSmile size={24} />
                            </button>
                            
                            <input 
                                type="text" 
                                placeholder={mensagemEditando ? "Editando mensagem..." : `Enviar mensagem...`}
                                value={mensagemDigitada}
                                onChange={(e) => setMensagemDigitada(e.target.value)}
                                style={{ flex: 1, padding: '12px', borderRadius: '8px', border: mensagemEditando ? '1px solid #f6c969' : '1px solid rgba(255,255,255,0.1)', background: 'rgba(255,255,255,0.05)', color: 'white' }}
                                onKeyDown={(e) => { if(e.key === 'Enter') enviarMensagem(mensagemDigitada); }}
                            />

                            <button 
                                disabled={mensagemDigitada.trim() === ''} 
                                onClick={() => enviarMensagem(mensagemDigitada)}
                                style={{ background: mensagemEditando ? '#f6c969' : 'var(--brand)', color: mensagemEditando ? 'black' : 'white', padding: '12px 20px', borderRadius: '8px', border: 'none', opacity: mensagemDigitada.trim() === '' ? 0.5 : 1, cursor: mensagemDigitada.trim() === '' ? 'not-allowed' : 'pointer' }}
                            >
                                <FiSend />
                            </button>
                        </div>
                    </div>
                </div>
            </main>
        </div>
    );
}