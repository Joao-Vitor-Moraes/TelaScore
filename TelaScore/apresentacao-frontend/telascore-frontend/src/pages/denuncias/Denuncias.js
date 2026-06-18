import { useEffect, useMemo, useState } from 'react';
import {
  FiCalendar,
  FiExternalLink,
  FiFlag,
  FiHash,
  FiPlus,
  FiSearch,
  FiTag,
  FiTarget,
} from 'react-icons/fi';
import DenunciaFormModal from '../../components/DenunciaFormModal';
import Navbar from '../../components/Navbar';
import { useAuth } from '../../context/AuthContext';
import { denunciaService } from '../../services/api';
import './denuncias.css';

export default function Denuncias() {
  const { sessao } = useAuth();
  const [denuncias, setDenuncias] = useState([]);
  const [carregando, setCarregando] = useState(true);
  const [erro, setErro] = useState(null);
  const [erroAcao, setErroAcao] = useState(null);
  const [busca, setBusca] = useState('');
  const [modalAberto, setModalAberto] = useState(false);

  useEffect(() => {
    carregar();
  }, []);

  async function carregar() {
    setCarregando(true);
    setErro(null);
    try {
      const dados = await denunciaService.listarMinhas();
      setDenuncias(Array.isArray(dados) ? dados : []);
    } catch {
      setDenuncias([]);
      setErro('Erro ao carregar denúncias.');
    } finally {
      setCarregando(false);
    }
  }

  const listagem = useMemo(() => {
    const termo = busca.trim().toLowerCase();
    if (!termo) return denuncias;

    return denuncias.filter(denuncia => [
      denuncia.id,
      denuncia.alvoId,
      denuncia.tipoAlvo,
      denuncia.motivo,
      denuncia.status,
      denuncia.descricao,
      denuncia.linkOcorrencia,
      formatarData(denuncia.dataCriacao),
    ].some(valor => String(valor ?? '').toLowerCase().includes(termo)));
  }, [busca, denuncias]);

  function registrarNaLista(denuncia) {
    setErroAcao(null);
    if (denuncia) {
      setDenuncias(atuais => [denuncia, ...atuais]);
    } else {
      carregar();
    }
  }

  return (
    <div className="cinema-page reports-page">
      <Navbar />

      <main className="cinema-container reports-content">
        <div className="catalog-toolbar">
          <div>
            <p className="page-eyebrow">Central de denúncias</p>
            <h2 className="page-title">Minhas denúncias</h2>
            <p className="page-description">
              {listagem.length} de {denuncias.length} denúncias cadastradas
            </p>
          </div>

          <div className="catalog-toolbar__actions">
            <label className="catalog-search">
              <FiSearch />
              <input
                value={busca}
                onChange={e => setBusca(e.target.value)}
                placeholder="Buscar denúncia..."
              />
            </label>

            <button className="btn-primary" onClick={() => setModalAberto(true)}>
              <FiPlus />
              Nova denúncia
            </button>
          </div>
        </div>

        {erroAcao && <div className="report-alert">{erroAcao}</div>}
        {carregando && <div className="empty-state">Carregando...</div>}
        {erro && !carregando && <div className="empty-state report-error">{erro}</div>}
        {!erro && !carregando && listagem.length === 0 && (
          <div className="empty-state">
            <FiFlag size={32} />
            <p>Nenhuma denúncia encontrada.</p>
          </div>
        )}

        {!carregando && !erro && listagem.length > 0 && (
          <div className="report-list">
            {listagem.map(denuncia => (
              <article key={denuncia.id} className="report-card">
                <div className="report-card__mark">
                  <FiFlag size={24} />
                </div>

                <div className="report-card__body">
                  <div className="report-card__title-row">
                    <h3>Denúncia #{denuncia.id}</h3>
                    <span className={`report-status ${classeStatus(denuncia.status)}`}>
                      {rotuloStatus(denuncia.status)}
                    </span>
                  </div>

                  <div className="report-meta">
                    <span><FiTarget /> {rotuloTipo(denuncia.tipoAlvo)}</span>
                    <span><FiHash /> Alvo {denuncia.alvoId}</span>
                    <span><FiTag /> {rotuloMotivo(denuncia.motivo)}</span>
                    <span><FiCalendar /> {formatarData(denuncia.dataCriacao)}</span>
                  </div>

                  {denuncia.descricao && (
                    <p className="report-description">{denuncia.descricao}</p>
                  )}
                </div>

                <div className="report-card__action">
                  {denuncia.linkOcorrencia ? (
                    <a className="btn-secondary" href={denuncia.linkOcorrencia} target="_blank" rel="noreferrer">
                      <FiExternalLink />
                      Abrir link
                    </a>
                  ) : (
                    <span>Sem link</span>
                  )}
                </div>
              </article>
            ))}
          </div>
        )}
      </main>

      <DenunciaFormModal
        aberto={modalAberto}
        denuncianteId={sessao?.id}
        onFechar={() => setModalAberto(false)}
        onRegistrada={registrarNaLista}
      />
    </div>
  );
}

function formatarData(valor) {
  if (!valor) return '-';
  return new Date(valor).toLocaleString('pt-BR');
}

function rotuloTipo(tipo) {
  const rotulos = {
    USUARIO: 'Usuário',
    MENSAGEM: 'Mensagem',
    COMUNIDADE: 'Comunidade',
    AVALIACAO: 'Avaliação',
  };
  return rotulos[tipo] || tipo;
}

function rotuloMotivo(motivo) {
  const rotulos = {
    OFENSIVO: 'Ofensivo',
    SPAM: 'Spam',
    ASSEDIO: 'Assédio',
    CONTEUDO_INADEQUADO: 'Conteúdo inadequado',
    OUTRO: 'Outro',
  };
  return rotulos[motivo] || motivo;
}

function rotuloStatus(status) {
  const rotulos = {
    PENDENTE: 'Pendente',
    EM_ANALISE: 'Em análise',
    ACEITA: 'Aceita',
    REJEITADA: 'Rejeitada',
  };
  return rotulos[status] || status;
}

function classeStatus(status) {
  if (status === 'ACEITA') return 'is-accepted';
  if (status === 'REJEITADA') return 'is-rejected';
  if (status === 'EM_ANALISE') return 'is-reviewing';
  return 'is-pending';
}
