import { useState } from 'react';
import { FiSend, FiSmile, FiUser, FiMoreVertical } from 'react-icons/fi';
import Navbar from '../../components/Navbar';
import './Mensagens.css'; // <-- Puxando o seu CSS exclusivo!

const FIGURINHAS_MOCK = [
    { id: 'f1', emoji: '🍿', nome: 'Pipoca' },
    { id: 'f2', emoji: '🎬', nome: 'Ação' },
    { id: 'f3', emoji: '🏆', nome: 'Oscar' },
    { id: 'f4', emoji: '😎', nome: 'Cinéfilo' },
    { id: 'f5', emoji: '😱', nome: 'Terror' },
    { id: 'f6', emoji: '🚀', nome: 'Ficção' }
];

export default function Mensagens() {
    const [mensagens, setMensagens] = useState([
        { id: 1, usuario: "Carlos_Filmes", texto: "Alguém já assistiu ao novo filme do Dune?", figurinha: null, data: "16:40", isMeu: false },
        { id: 2, usuario: "Lu", texto: "Sim! Achei incrível a fotografia!", figurinha: "🍿", data: "16:42", isMeu: true }
    ]);
    const [novoTexto, setNovoTexto] = useState('');
    const [mostrarFigurinhas, setMostrarFigurinhas] = useState(false);

    const enviarMensagem = (texto, figurinha = null) => {
        if (!texto.trim() && !figurinha) return;

        const novaMsg = {
            id: mensagens.length + 1,
            usuario: "Lu", // O seu usuário
            texto: texto,
            figurinha: figurinha,
            data: new Date().toLocaleTimeString('pt-BR', { hour: '2-digit', minute: '2-digit' }),
            isMeu: true // Define que o balão fica na direita
        };

        setMensagens([...mensagens, novaMsg]);
        setNovoTexto('');
        setMostrarFigurinhas(false);
    };

    return (
        <div className="comunidades-page"> {/* Aplicando a classe do fundo radial-gradient */}
            <Navbar />
            
            <main className="comunidades-container" style={{ margin: '0 auto' }}>
                
                {/* O grid que você definiu no CSS */}
                <div className="comunidades-grid" style={{ gridTemplateColumns: '1fr' }}>
                    
                    {/* O Painel do Chat */}
                    <div className="chat-painel">
                        
                        {/* Linha 1: Cabeçalho */}
                        <div style={{ display: 'flex', alignItems: 'center', gap: '15px', borderBottom: '1px solid var(--line)', paddingBottom: '15px' }}>
                            <div className="chat-header-icon">
                                <FiUser size={20} />
                            </div>
                            <div>
                                <h3 style={{ margin: 0, fontSize: '1.2rem' }}>Clube do Cinema</h3>
                                <small style={{ opacity: 0.6 }}>Discussão Geral</small>
                            </div>
                        </div>

                        {/* Linha 2: Área Principal (Mensagens + Membros) */}
                        <div style={{ display: 'grid', gridTemplateColumns: '1fr 220px', overflow: 'hidden' }}>
                            
                            {/* Zona de Mensagens */}
                            <div className="chat-zona-mensagens">
                                {mensagens.map(msg => (
                                    <div 
                                        key={msg.id} 
                                        className={`chat-balao ${msg.isMeu ? 'meu' : 'outro'}`}
                                    >
                                        <div style={{ display: 'flex', justifyContent: 'space-between', marginBottom: '4px', opacity: 0.7, fontSize: '11px' }}>
                                            <strong>{msg.isMeu ? 'Você' : msg.usuario}</strong>
                                            <span>{msg.data}</span>
                                        </div>
                                        {msg.texto && <div>{msg.texto}</div>}
                                        {msg.figurinha && (
                                            <div style={{ fontSize: '2.5rem', marginTop: '5px' }}>
                                                {msg.figurinha}
                                            </div>
                                        )}
                                    </div>
                                ))}
                            </div>

                            {/* Zona de Membros (que está no seu CSS!) */}
                            <div className="chat-zona-membros">
                                <small style={{ opacity: 0.5, marginBottom: '10px', display: 'block' }}>Membros (3)</small>
                                
                                <div className="membro-linha">
                                    <span style={{ fontSize: '14px' }}>Lu (Você)</span>
                                    <button className="btn-moderacao-acao"><FiMoreVertical size={14}/></button>
                                </div>
                                <div className="membro-linha">
                                    <span style={{ fontSize: '14px' }}>Carlos_Filmes</span>
                                    <button className="btn-moderacao-acao"><FiMoreVertical size={14}/></button>
                                </div>
                                <div className="membro-linha">
                                    <span style={{ fontSize: '14px' }}>Ana_Cinema</span>
                                    <button className="btn-moderacao-acao"><FiMoreVertical size={14}/></button>
                                </div>
                            </div>
                        </div>

                        {/* Linha 3: Input de Texto */}
                        <div style={{ position: 'relative', display: 'flex', gap: '10px', alignItems: 'center' }}>
                            
                            {/* Janela de Figurinhas */}
                            {mostrarFigurinhas && (
                                <div style={{ position: 'absolute', bottom: '60px', left: '0', background: '#202025', border: '1px solid var(--line)', borderRadius: '12px', padding: '10px', display: 'grid', gridTemplateColumns: 'repeat(3, 1fr)', gap: '10px', zIndex: 10 }}>
                                    {FIGURINHAS_MOCK.map(fig => (
                                        <button 
                                            key={fig.id} 
                                            style={{ fontSize: '1.8rem', background: 'transparent', border: 'none', cursor: 'pointer', transition: 'transform 0.2s' }}
                                            onClick={() => enviarMensagem('', fig.emoji)}
                                            onMouseOver={(e) => e.target.style.transform = 'scale(1.2)'}
                                            onMouseOut={(e) => e.target.style.transform = 'scale(1)'}
                                        >
                                            {fig.emoji}
                                        </button>
                                    ))}
                                </div>
                            )}

                            <button 
                                style={{ background: 'transparent', border: 'none', color: 'white', cursor: 'pointer', padding: '10px' }} 
                                onClick={() => setMostrarFigurinhas(!mostrarFigurinhas)}
                            >
                                <FiSmile size={24} />
                            </button>
                            
                            <input 
                                type="text"
                                className="chat-input-area"
                                value={novoTexto}
                                onChange={e => setNovoTexto(e.target.value)}
                                onKeyDown={e => e.key === 'Enter' && enviarMensagem(novoTexto)}
                                placeholder="Digite sua mensagem..."
                            />
                            
                            <button 
                                style={{ background: 'rgba(229, 9, 20, 0.8)', border: 'none', color: 'white', cursor: 'pointer', padding: '12px', borderRadius: '10px' }} 
                                onClick={() => enviarMensagem(novoTexto)}
                            >
                                <FiSend size={18} />
                            </button>
                        </div>
                        
                    </div>
                </div>
            </main>
        </div>
    );
}