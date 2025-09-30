import io, base64, matplotlib
matplotlib.use("Agg")
import matplotlib.pyplot as plt
from typing import List
from app.schemas import WindowResult

MAX_LEVEL = 5                       
LABEL_TO_DESC = {
    0: "Mucha felicidad",
    1: "Felicidad media",
    2: "Felicidad leve",
    3: "Neutro",
    4: "Ansiedad leve o ligera",
    5: "Ansiedad alta",
}

class PostProcessor:
    FPS = 30   

    def aggregate(self, windows: List[WindowResult]) -> float:
        total = sum(w.end - w.start + 1 for w in windows)
        score = sum(w.anxiety * (w.end - w.start + 1) for w in windows) / total
        return round(score, 2)

    def sparkline_b64(self, windows: List[WindowResult]) -> str:
        if not windows:
            return ""   

        xs, ys = [], []
        for w in windows:
            xs += [w.start, w.end]         
            ys += [w.anxiety]*2   

        fig, ax = plt.subplots(figsize=(7, 2.3), dpi=140, facecolor="white")
        ax.plot(xs, ys, color="#0066ff", linewidth=2)

        ax.set_title("Evolución de ansiedad por frames", fontsize=9, pad=6)
        ax.set_ylabel(f"Ansiedad (0-{MAX_LEVEL})", fontsize=8)
        ax.set_ylim(-0.2, MAX_LEVEL + 0.2)
        ax.set_yticks(range(0, MAX_LEVEL + 1)) 
        ax.grid(alpha=.25, axis="y", linestyle="--")

        start, end = xs[0], xs[-1]
        mid = (start + end) // 2
        ax.set_xticks([start, mid, end],
                      [str(start), str(mid), str(end)],
                      rotation=0, fontsize=7)
        ax.set_xlabel("Frames", fontsize=8)

        fig.tight_layout(pad=0.4)
        buf = io.BytesIO()
        fig.savefig(buf, format="jpg")
        plt.close(fig)
        return base64.b64encode(buf.getvalue()).decode("ascii")
