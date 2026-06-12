import { BrowserRouter, Routes, Route } from 'react-router-dom';
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

function App() {
  return (
    <BrowserRouter>
      <Routes>
        {/* Filmes */}
        <Route path="/filmes" element={<Filmes />} />
        <Route path="/filmes/novo" element={<CadastrarFilme />} />
        <Route path="/filmes/:id" element={<FilmeDetalhe />} />
        <Route path="/filmes/:id/editar" element={<EditarFilme />} />

        {/* Listas */}
        <Route path="/listas" element={<MinhasListas />} />
        <Route path="/listas/nova" element={<CriarLista />} />
        <Route path="/listas/:id" element={<ListaAberta />} />
        <Route path="/listas/:id/editar" element={<EditarLista />} />
        <Route path="/listas/:id/adicionar" element={<AdicionarFilme />} />

        {/* Solicitações */}
        <Route path="/solicitacoes" element={<SolicitacoesUsuario />} />
        <Route path="/solicitacoes/nova" element={<SolicitarFilme />} />
        <Route path="/solicitacoes/:id/editar" element={<EditarSolicitacao />} />
        <Route path="/admin/solicitacoes" element={<AdminSolicitacoes />} />

        {/* Watchlist */}
        <Route path="/watchlist" element={<Watchlist />} />

        {/* Raiz */}
        <Route path="/" element={<Filmes />} />
      </Routes>
    </BrowserRouter>
  );
}

export default App;