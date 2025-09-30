import logging
from pathlib import Path
import numpy as np
from tensorflow import keras
from app.config import settings

logger = logging.getLogger("app.services.model_service")

LABELS_ORDER = [0, 1, 2, 3, 4, 5]

LABEL_TO_DESC = {
    0: "Mucha felicidad",
    1: "Felicidad media",
    2: "Felicidad leve",
    3: "Neutro",
    4: "Ansiedad leve o ligera",
    5: "Ansiedad alta",
}

class ModelService:
    def __init__(self, model_path: str = settings.MODEL_PATH) -> None:
        model_path = Path(model_path).expanduser().resolve()
        try:
            self.model = keras.models.load_model(model_path)
            logger.info("Modelo cargado correctamente: %s", model_path)
        except Exception:
            logger.exception("No se pudo cargar el modelo en %s", model_path)
            raise

        self.num_classes = self.model.output_shape[-1]
        assert self.num_classes == len(LABELS_ORDER), (
            f"Modelo devuelve {self.num_classes} clases pero LABELS_ORDER tiene "
            f"{len(LABELS_ORDER)} elementos"
        )
        logger.info("Modelo espera %d clases; orden de labels: %s",
                    self.num_classes, LABELS_ORDER)

    def predict(self, tensor: np.ndarray) -> float:
        if tensor.shape != (150, settings.IMG_SIZE, settings.IMG_SIZE, 3):
            logger.warning("Shape inesperada %s; se esperaba (150,%d,%d,3)",
                           tensor.shape, settings.IMG_SIZE, settings.IMG_SIZE)

        probs = self.model.predict(np.expand_dims(tensor, axis=0), verbose=0)[0]

        class_idx = int(np.argmax(probs))      
        label = LABELS_ORDER[class_idx]       
        confidence = float(probs[class_idx])

        logger.debug("Predicción ventana: class_idx=%d label=%d prob=%.4f desc=%s", class_idx, label, confidence, LABEL_TO_DESC[label])
        return float(label)
    
    def predict_batch(self, batch: np.ndarray, *, batch_size: int = 32) -> np.ndarray:
        probs = self.model.predict(batch,
                                   batch_size=min(batch_size, len(batch)),
                                   verbose=0)
        return probs.argmax(axis=1).astype(float)
