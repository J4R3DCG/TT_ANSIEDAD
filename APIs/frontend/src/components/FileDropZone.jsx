// src/components/FileDropZone.jsx
import { useRef, useState } from "react";

export default function FileDropZone({ onFile, file }) {
  const inputRef = useRef();
  const [dragging, setDragging] = useState(false);

  const pick = () => inputRef.current?.click();
  const stop = e => e.preventDefault();
  const drop = e => {
    e.preventDefault();
    setDragging(false);
    const f = e.dataTransfer.files[0];
    if (f) onFile(f);
  };

  const style = {
    border: "2px dashed var(--pico-primary)",
    borderRadius: "0.75rem",
    padding: "2rem",
    textAlign: "center",
    cursor: "pointer",
    background: dragging ? "var(--pico-primary-low)" : "transparent",
    transition: "background 0.15s",
    position: "relative",
  };

  return (
    <article
      style={style}
      onClick={pick}
      onDragOver={e => { stop(e); setDragging(true); }}
      onDragLeave={() => setDragging(false)}
      onDrop={drop}
    >
      {!file && (
        <>
          <svg width="48" height="48" fill="none" stroke="var(--pico-primary)"
               strokeWidth="1.5" viewBox="0 0 24 24">
            <path strokeLinecap="round" strokeLinejoin="round"
                  d="M12 16v-4m0 0V8m0 4h4m-4 0H8M20 16v1.8A2.2 2.2 0 0 1 17.8 20H6.2A2.2 2.2 0 0 1 4 17.8V16" />
          </svg>
          <p style={{ margin: "0.5rem 0" }}>
            Arrastra tu video MP4 aquí<br /><em>o haz clic para seleccionarlo</em>
          </p>
        </>
      )}

      <input
        ref={inputRef}
        type="file"
        accept="video/mp4"
        hidden
        onChange={e => onFile(e.target.files[0])}
      />

      {file && (
        <div style={{
          display: "flex",
          alignItems: "center",
          justifyContent: "center",
          gap: "0.5rem",
          fontSize: "1rem",
          color: "var(--pico-success)"
        }}>
          {/* Checkmark icon */}
          <svg width="24" height="24" fill="none" stroke="var(--pico-success)"
               strokeWidth="2" viewBox="0 0 24 24">
            <path strokeLinecap="round" strokeLinejoin="round"
                  d="M5 13l4 4L19 7" />
          </svg>
          <span>{file.name}</span>

          {/* Optional remove button */}
          <button
            onClick={e => { e.stopPropagation(); onFile(null); }}
            style={{
              background: "transparent",
              border: "none",
              fontSize: "1.2rem",
              cursor: "pointer",
              color: "var(--pico-error)",
            }}
            aria-label="Quitar vídeo"
          >
            ×
          </button>
        </div>
      )}
    </article>
  );
}
