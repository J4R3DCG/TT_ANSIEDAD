import { useState } from "react";
import FileDropZone from "./components/FileDropZone";
import ErrorToast, { fireErrors } from "./components/ErrorToast";
import AnxietyScaleTable from "./components/AnxietyScaleTable";

export default function App() {
  const [file, setFile]     = useState(null);
  const [email, setEmail]   = useState("");
  const [status, setStatus] = useState("");
  const [loading, setLoading] = useState(false);

  const upload = async e => {
    e.preventDefault();

    if (!file) {
      return fireErrors([{ title: "Archivo requerido", details: "Selecciona un vídeo MP4", code: 400 }]);
    }

    const fd = new FormData();
    fd.append("email", email);
    fd.append("video", file);

    setLoading(true);
    setStatus("Procesando, esto puede tardar unos minutos …");
    ///api/video/process
    try {
    const resp = await fetch("http://localhost:8080/api/video/process", {
        method: "POST",
        body: fd
    });
    const contentTyp = resp.headers.get("content-type") || "";
      let payload;

      if (contentTyp.includes("application/json")) {
        payload = await resp.json();
      } else {
        payload = await resp.text();           
      }

      if (!resp.ok) {
        if (typeof payload === "string") {
          fireErrors([{ title: "Error", details: payload, code: resp.status }]);
        } else {
          fireErrors(payload.errors ?? [{ title: "Error", details: JSON.stringify(payload), code: resp.status }]);
        }
        setStatus("No se pudo procesar.");
      } else {
        setStatus(payload.message ??
          "El vídeo pasó las validaciones y está en proceso. Revisa tu correo.");
      }

    } catch (err) {
      fireErrors([{ title: "Red", details: err.message, code: 0 }]);
      setStatus("Fallo de red.");
    } finally {
      setLoading(false);
    }
  };

  return (
    <>
      <ErrorToast />

      <section className="container">
        <hgroup>
          <h1>Detector de Ansiedad</h1>
          <h2 style={{ fontWeight: 400 }}>Sube tu video y recibe el resultado por correo</h2>
        </hgroup>

        <form onSubmit={upload}>
          <label>
            Correo electrónico
            <input required type="email" value={email} onChange={e => setEmail(e.target.value)} />
          </label>

          <FileDropZone onFile={setFile} file={file} />

          <button type="submit" aria-busy={loading} disabled={loading || !file}>
            {loading ? "Procesando…" : "Enviar"}
          </button>
        </form>

        {status && <p><small>{status}</small></p>}
      </section>
      <section className="container">
        <AnxietyScaleTable />     
      </section>
    </>
  );
}
