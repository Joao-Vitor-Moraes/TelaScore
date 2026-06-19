import { BrowserRouter, Routes, Route } from 'react-router-dom';
import { AuthProvider } from './context/AuthContext';
import RotaProtegida from './components/RotaProtegida';
import Login from './pages/auth/Login';
import MinhasListas from './pages/listas/MinhasListas';
import CriarLista from './pages/listas/CriarLista';
import ListaAberta from './pages/listas/ListaAberta';
import EditarLista from './pages/listas/EditarLista';
import Watchlist from './pages/listas/Watchlist';
import AdicionarFilme from './pages/listas/AdicionarFilme';
import SolicitacoesUsuario from './pages/solicitacoes/SolicitacoesUsuario';
import SolicitarFilme from './pages/solicitacoes/SolicitarFilme';
import EditarSolicitacao from './pages/solicitacoes/EditarSolicitacao';
import AdminSolicitacoes from './pages/solicitacoes/AdminSolicitacoes';
import Filmes from './pages/filmes/Filmes';
import CadastrarFilme from './pages/filmes/CadastrarFilme';
import FilmeDetalhe from './pages/filmes/FilmeDetalhe';
import EditarFilme from './pages/filmes/EditarFilme';
import AdminUsuarios from './pages/usuario/AdminUsuarios';
import MeuUsuario from './pages/usuario/MeuUsuario';
import Metas from './pages/analise/Metas';
import Recomendacoes from './pages/analise/Recomendacoes';
import './App.css';
import Denuncias from './pages/denuncias/Denuncias';
import AdminDenuncias from './pages/denuncias/AdminDenuncias';
import Comunidades from './pages/comunidade/Comunidades';
import Noticias from './pages/noticias/Noticias';

function App() {
  return (
      <AuthProvider>
        <BrowserRouter>
          <Routes>
            <Route path="/login" element={<Login />} />

            {/* Filmes */}
            <Route path="/filmes" element={<RotaProtegida><Filmes /></RotaProtegida>} />
            <Route path="/filmes/novo" element={<RotaProtegida><CadastrarFilme /></RotaProtegida>} />
            <Route path="/filmes/:id" element={<RotaProtegida><FilmeDetalhe /></RotaProtegida>} />
            <Route path="/filmes/:id/editar" element={<RotaProtegida><EditarFilme /></RotaProtegida>} />

            {/* Listas */}
            <Route path="/listas" element={<RotaProtegida><MinhasListas /></RotaProtegida>} />
            <Route path="/listas/nova" element={<RotaProtegida><CriarLista /></RotaProtegida>} />
            <Route path="/listas/:id" element={<RotaProtegida><ListaAberta /></RotaProtegida>} />
            <Route path="/listas/:id/editar" element={<RotaProtegida><EditarLista /></RotaProtegida>} />
            <Route path="/listas/:id/adicionar" element={<RotaProtegida><AdicionarFilme /></RotaProtegida>} />

            {/* Solicitações */}
            <Route path="/solicitacoes" element={<RotaProtegida><SolicitacoesUsuario /></RotaProtegida>} />
            <Route path="/solicitacoes/nova" element={<RotaProtegida><SolicitarFilme /></RotaProtegida>} />
            <Route path="/solicitacoes/:id/editar" element={<RotaProtegida><EditarSolicitacao /></RotaProtegida>} />
            <Route path="/admin/solicitacoes" element={<RotaProtegida><AdminSolicitacoes /></RotaProtegida>} />

            {/* Denuncias */}
            <Route path="/denuncias" element={<RotaProtegida><Denuncias /></RotaProtegida>} />
            <Route path="/admin/denuncias" element={<RotaProtegida><AdminDenuncias /></RotaProtegida>} />

            {/* usuario*/}
            <Route path="/meuusuario" element={<RotaProtegida><MeuUsuario /></RotaProtegida>} />
            <Route path="/admin/usuarios" element={<RotaProtegida><AdminUsuarios /></RotaProtegida>} />

            {/* Watchlist */}
            <Route path="/watchlist" element={<RotaProtegida><Watchlist /></RotaProtegida>} />

            {/* Comunidades */}
            <Route path="/comunidades" element={<RotaProtegida><Comunidades /></RotaProtegida>} />

            {/* Notícias */}
            <Route path="/noticias" element={<RotaProtegida><Noticias /></RotaProtegida>} />

            {/* Análise */}
            <Route path="/metas" element={<RotaProtegida><Metas /></RotaProtegida>} />
            <Route path="/recomendacoes" element={<RotaProtegida><Recomendacoes /></RotaProtegida>} />

            {/* Raiz */}
            <Route path="/" element={<RotaProtegida><Filmes /></RotaProtegida>} />
          </Routes>
        </BrowserRouter>
      </AuthProvider>
  );
}

export default App;