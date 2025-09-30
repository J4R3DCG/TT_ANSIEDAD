package anxiety.detector.api.service.domain.model;

import java.util.Arrays;

public enum AnxietyLevel {

    VERY_HAPPY (0, "Mucha felicidad", 0.0, 0.99, "Estado de Felicidad 🎉"),
    MEDIUM_HAPPY (1, "Felicidad media", 1.0, 1.99,  "Estado de Felicidad 😊"),
    SLIGHT_HAPPY (2, "Felicidad leve", 2.0, 2.99,  "Estado Positivo 🙂"),
    NEUTRAL (3, "Neutro", 3.0, 3.49,  "Estado Emocional Neutro"),
    LIGHT_ANXIETY (4, "Ansiedad leve o ligera",  3.5, 4.49, "Ansiedad Leve — Recomendaciones"),
    HIGH_ANXIETY (5, "Ansiedad alta", 4.5, 5.00, "Ansiedad Alta — Acciones Inmediatas");

    public final int id;
    public final String desc;
    public final double min;
    public final double max;
    public final String mailSubject;

    AnxietyLevel(int id, String desc, double min, double max, String mailSubject) {
        this.id = id;
        this.desc = desc;
        this.min = min;
        this.max = max;
        this.mailSubject = mailSubject;
    }

    public static AnxietyLevel fromScore(double s) {
        return Arrays.stream(values())
                     .filter(l -> s >= l.min && s <= l.max)
                     .findFirst()
                     .orElseThrow();
    }
}
