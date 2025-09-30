import { ANXIETY_SCALE } from "../constants/anxietyScale";

export default function AnxietyScaleTable() {
  return (
    <table style={{ width: "100%", fontSize: "0.9rem" }}>
      <thead>
        <tr>
          <th>Nivel</th><th>Descripción</th><th>Rango</th>
        </tr>
      </thead>
      <tbody>
        {ANXIETY_SCALE.map(({ id, label, range, color }) => (
          <tr key={id}>
            <td style={{ borderLeft: `4px solid ${color}`, paddingLeft: "0.5rem" }}>
              <strong>{id}</strong>
            </td>
            <td>{label}</td>
            <td>{range}</td>
          </tr>
        ))}
      </tbody>
    </table>
  );
}
