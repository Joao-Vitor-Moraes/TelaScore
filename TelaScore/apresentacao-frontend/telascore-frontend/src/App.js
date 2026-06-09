import { BrowserRouter, Routes, Route } from 'react-router-dom';
import MinhasListas from './pages/listas/MinhasListas';
import CriarLista from './pages/listas/CriarLista';
import ListaAberta from './pages/listas/ListaAberta';
import EditarLista from './pages/listas/EditarLista';
import Watchlist from './pages/listas/Watchlist';
import AdicionarFilme from './pages/listas/AdicionarFilme';

function App() {
  return (
    <BrowserRouter>
      <Routes>
        {/* Listas */}
        <Route path="/listas" element={<MinhasListas />} />
        <Route path="/listas/nova" element={<CriarLista />} />
        <Route path="/listas/:id" element={<ListaAberta />} />
        <Route path="/listas/:id/editar" element={<EditarLista />} />
        <Route path="/listas/:id/adicionar" element={<AdicionarFilme />} />

        {/* Solicitações */}
        <Route path="/solicitacoes" element={<div>Minhas Solicitações</div>} />
        <Route path="/solicitacoes/nova" element={<div>Solicitar Filme</div>} />
        <Route path="/admin/solicitacoes" element={<div>ADM Solicitações</div>} />

        {/* Watchlist */}
        <Route path="/watchlist" element={<Watchlist />} />

        {/* Raiz */}
        <Route path="/" element={<div>Home</div>} />
      </Routes>
    </BrowserRouter>
  );
}

export default App;
