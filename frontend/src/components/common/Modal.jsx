
import { createPortal } from 'react-dom';
import "./Modal.css"; // We can go back to using your clean CSS!

function Modal({ isOpen, onClose, children }) {
  if (!isOpen) return null;

  // createPortal takes two arguments: (1) The JSX to render, (2) Where to render it
  return createPortal(
    <div className="modal-overlay" onClick={onClose}>
      <div
        className="modal-container"
        onClick={(e) => e.stopPropagation()} 
      >
        <button className="modal-close" onClick={onClose} aria-label="Close modal">
          ✕
        </button>

        {children}
      </div>
    </div>,
    document.body // Teleports the modal completely outside of your app structure!
  );
}

export default Modal;