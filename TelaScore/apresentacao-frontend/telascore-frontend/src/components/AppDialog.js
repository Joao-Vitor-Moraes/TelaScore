import { useCallback, useState } from 'react';
import { FiAlertTriangle, FiCheckCircle, FiX } from 'react-icons/fi';

export function useAppDialog() {
  const [dialog, setDialog] = useState(null);

  const fechar = useCallback((resultado = false) => {
    setDialog(atual => {
      atual?.resolver(resultado);
      return null;
    });
  }, []);

  const confirmar = useCallback((opcoes) => new Promise(resolve => {
    setDialog({
      tipo: 'confirmar',
      titulo: opcoes?.titulo || 'Confirmar ação',
      mensagem: opcoes?.mensagem || 'Deseja continuar?',
      textoConfirmar: opcoes?.textoConfirmar || 'Confirmar',
      textoCancelar: opcoes?.textoCancelar || 'Cancelar',
      variante: opcoes?.variante || 'perigo',
      resolver: resolve,
    });
  }), []);

  const avisar = useCallback((opcoes) => new Promise(resolve => {
    setDialog({
      tipo: 'aviso',
      titulo: opcoes?.titulo || 'Aviso',
      mensagem: opcoes?.mensagem || '',
      textoConfirmar: opcoes?.textoConfirmar || 'Entendi',
      variante: opcoes?.variante || 'info',
      resolver: resolve,
    });
  }), []);

  const Dialog = dialog ? (
    <div className="app-dialog-backdrop" role="presentation" onMouseDown={() => fechar(false)}>
      <section
        className="app-dialog"
        role="dialog"
        aria-modal="true"
        aria-labelledby="app-dialog-title"
        onMouseDown={event => event.stopPropagation()}
      >
        <button className="app-dialog__close" onClick={() => fechar(false)} aria-label="Fechar">
          <FiX />
        </button>
        <span className={`app-dialog__icon is-${dialog.variante}`}>
          {dialog.variante === 'info' ? <FiCheckCircle /> : <FiAlertTriangle />}
        </span>
        <h2 id="app-dialog-title">{dialog.titulo}</h2>
        {dialog.mensagem && <p>{dialog.mensagem}</p>}
        <div className="app-dialog__actions">
          {dialog.tipo === 'confirmar' && (
            <button className="app-dialog__button is-secondary" onClick={() => fechar(false)}>
              {dialog.textoCancelar}
            </button>
          )}
          <button className={`app-dialog__button is-${dialog.variante}`} onClick={() => fechar(true)}>
            {dialog.textoConfirmar}
          </button>
        </div>
      </section>
    </div>
  ) : null;

  return { confirmar, avisar, Dialog };
}
