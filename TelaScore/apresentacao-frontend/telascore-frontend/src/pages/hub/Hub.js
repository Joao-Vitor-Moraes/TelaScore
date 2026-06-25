import { useMemo } from 'react';
import { useNavigate, useParams } from 'react-router-dom';
import {
  FiAward, FiCalendar, FiCompass, FiEdit3, FiFilm, FiFlag, FiGrid, FiHeart,
  FiInbox, FiList, FiMessageCircle, FiPlus, FiShield, FiStar, FiTarget,
  FiTrendingUp, FiUser, FiUsers
} from 'react-icons/fi';
import Navbar from '../../components/Navbar';
import { useAuth } from '../../context/AuthContext';

const hubsBase = {
  filmes: {
    titulo: 'Filmes',
    subtitulo: 'Catálogo, listas e solicitações ficam juntos aqui.',
    icone: FiFilm,
    itens: [
      { titulo: 'Catálogo', descricao: 'Explorar filmes e abrir detalhes.', rota: '/filmes', icone: FiFilm },
      { titulo: 'Minhas listas', descricao: 'Organizar coleções pessoais.', rota: '/listas', icone: FiList },
      { titulo: 'Watchlist', descricao: 'Acompanhar o que pretende assistir.', rota: '/watchlist', icone: FiHeart },
      { titulo: 'Solicitações', descricao: 'Pedir filmes ou acompanhar pedidos.', rota: '/solicitacoes', rotaAdmin: '/admin/solicitacoes', icone: FiInbox },
      { titulo: 'Solicitar filme', descricao: 'Enviar uma nova sugestão ao catálogo.', rota: '/solicitacoes/nova', apenasUsuario: true, icone: FiPlus },
      { titulo: 'Novo filme', descricao: 'Cadastrar um título diretamente.', rota: '/filmes/novo', apenasAdmin: true, icone: FiEdit3 },
    ],
  },
  social: {
    titulo: 'Social',
    subtitulo: 'Amigos, conversas, comunidades e moderação social.',
    icone: FiUsers,
    itens: [
      { titulo: 'Amigos', descricao: 'Gerenciar conexões e abrir perfis.', rota: '/amigos', icone: FiUsers },
      { titulo: 'Mensagens', descricao: 'Conversar com seus amigos.', rota: '/mensagens', icone: FiMessageCircle },
      { titulo: 'Comunidades', descricao: 'Participar de grupos e debates.', rota: '/comunidades', icone: FiGrid },
      { titulo: 'Denúncias', descricao: 'Acompanhar denúncias enviadas.', rota: '/denuncias', icone: FiFlag },
      { titulo: 'Moderação', descricao: 'Avaliar denúncias pendentes.', rota: '/admin/denuncias', apenasAdmin: true, icone: FiShield },
    ],
  },
  descobrir: {
    titulo: 'Descobrir',
    subtitulo: 'Conteúdo, recomendações, quizzes e agenda.',
    icone: FiCompass,
    itens: [
      { titulo: 'Notícias', descricao: 'Publicações e críticas do portal.', rota: '/noticias', icone: FiEdit3 },
      { titulo: 'Quizzes', descricao: 'Responder e criar desafios.', rota: '/quiz', icone: FiStar },
      { titulo: 'Recomendações', descricao: 'Indicações recebidas e enviadas.', rota: '/recomendacoes', icone: FiCompass },
      { titulo: 'Eventos', descricao: 'Sessões, encontros e estreias.', rota: '/eventos', icone: FiCalendar },
      { titulo: 'Criar evento', descricao: 'Agendar um novo evento.', rota: '/eventos/novo', icone: FiPlus },
      { titulo: 'Calendário', descricao: 'Visualizar datas importantes.', rota: '/calendario', icone: FiCalendar },
    ],
  },
  progresso: {
    titulo: 'Progresso',
    subtitulo: 'Metas, nível e recompensas do sistema.',
    icone: FiTrendingUp,
    itens: [
      { titulo: 'Metas', descricao: 'Acompanhar objetivos e progresso.', rota: '/metas', icone: FiTarget },
      { titulo: 'Nível', descricao: 'Ver pontos, níveis e conquistas.', rota: '/recompensas', icone: FiAward },
    ],
  },
  perfil: {
    titulo: 'Perfil',
    subtitulo: 'Conta, perfil público e ferramentas administrativas.',
    icone: FiUser,
    itens: [
      { titulo: 'Meu perfil', descricao: 'Editar dados, avatar e visualizar nível.', rota: '/meuusuario', icone: FiUser },
      { titulo: 'Usuários', descricao: 'Administrar contas do sistema.', rota: '/admin/usuarios', apenasAdmin: true, icone: FiUsers },
      { titulo: 'Solicitações admin', descricao: 'Moderar pedidos de catálogo.', rota: '/admin/solicitacoes', apenasAdmin: true, icone: FiShield },
      { titulo: 'Denúncias admin', descricao: 'Revisar ocorrências reportadas.', rota: '/admin/denuncias', apenasAdmin: true, icone: FiFlag },
    ],
  },
};

export default function Hub() {
  const { area = 'filmes' } = useParams();
  const navigate = useNavigate();
  const { sessao } = useAuth();
  const isAdmin = sessao?.papel === 'ADMIN';

  const hub = hubsBase[area] || hubsBase.filmes;
  const itens = useMemo(() => hub.itens
    .filter(item => !item.apenasAdmin || isAdmin)
    .filter(item => !item.apenasUsuario || !isAdmin)
    .map(item => ({
      ...item,
      rotaFinal: item.rotaAdmin && isAdmin ? item.rotaAdmin : item.rota,
    })), [hub, isAdmin]);

  const IconeHub = hub.icone;

  return (
    <div className="hub-page">
      <Navbar />
      <main className="hub-shell">
        <section className="hub-hero">
          <span className="hub-hero__icon"><IconeHub /></span>
          <div>
            <h1>{hub.titulo}</h1>
            <p>{hub.subtitulo}</p>
          </div>
        </section>

        <section className="hub-grid">
          {itens.map(item => {
            const Icone = item.icone;
            return (
              <button key={`${item.titulo}-${item.rotaFinal}`} className="hub-card" onClick={() => navigate(item.rotaFinal)}>
                <span className="hub-card__icon"><Icone /></span>
                <span>
                  <strong>{item.titulo}</strong>
                  <small>{item.descricao}</small>
                </span>
              </button>
            );
          })}
        </section>
      </main>
    </div>
  );
}
