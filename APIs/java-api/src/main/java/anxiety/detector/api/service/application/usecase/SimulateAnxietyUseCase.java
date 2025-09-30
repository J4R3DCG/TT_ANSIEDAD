package anxiety.detector.api.service.application.usecase;

import java.util.Locale;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.Resource;
import org.springframework.stereotype.Service;

import anxiety.detector.api.service.domain.model.AnxietyLevel;
import anxiety.detector.api.service.domain.model.AnxietyResult;
import anxiety.detector.api.service.domain.model.EmailTemplate;

@Service
public class SimulateAnxietyUseCase {

    @Value("classpath:infographics/2.jpg")
    private Resource medImg;
    @Value("classpath:infographics/3.jpg")
    private Resource highImg;

    //private final CopyWriterPort copyWriter;

    public SimulateAnxietyUseCase(/*CopyWriterPort copyWriter*/) {
        //this.copyWriter = copyWriter;
    }

    public EmailTemplate buildMail(AnxietyResult r) {

        AnxietyLevel lvl = AnxietyLevel.fromScore(r.getOverall());
        String avgStr = String.format(Locale.US, "%.2f", r.getOverall());  
        EmailTemplate t = new EmailTemplate();
        t.setSubject(lvl.mailSubject);

        switch (lvl) {
            case VERY_HAPPY, MEDIUM_HAPPY, SLIGHT_HAPPY -> t.setBody("""
                Hola,
                Gracias por utilizar TTAnxiety. Hemos analizado tu video y detectamos un estado emocional positivo. Promedio: %s

                Nos alegra saber que estás atravesando un momento de bienestar. Te invitamos a seguir cuidando tu salud emocional con hábitos que te funcionen, como mantener rutinas placenteras, dormir bien y compartir tiempo con personas cercanas.
                Recuerda que monitorear tu estado emocional con regularidad puede ayudarte a fortalecer tu estabilidad emocional a largo plazo.

                ¡Sigue así!
                """.formatted(avgStr));
            case NEUTRAL -> t.setBody("""
                Hola,   
                Gracias por confiar en TTAnxiety. El análisis de tu video indica un estado emocional neutro. Promedio: %s  

                Esto puede reflejar estabilidad o simplemente un momento de calma. Aunque no se detectan señales de ansiedad, te sugerimos mantener prácticas saludables como descanso adecuado, ejercicio ligero y momentos de desconexión.
                Prevenir es parte de cuidar tu salud mental.

                Estamos para apoyarte.
                """.formatted(avgStr));
            case LIGHT_ANXIETY -> {
                t.setInfographic(toBytes(medImg));
                t.setBody("""
                    Hola,
                    Gracias por utilizar TTAnxiety. Nuestro análisis indica señales de ansiedad leve. Promedio: %s

                    Este estado no es alarmante, pero sí una señal para que prestes atención. Algunos hábitos que podrían ayudarte:
                    • Técnicas de respiración consciente (como la 4-7-8).
                    • Micro descansos cada hora.
                    • Limitar cafeína y pantallas por la noche.

                    Consulta la infografía adjunta para más recomendaciones.

                    Tu bienestar emocional importa.
                    """.formatted(avgStr));
            }
            case HIGH_ANXIETY -> {
                t.setInfographic(toBytes(highImg));
                t.setBody("""
                    Hola,
                    Gracias por utilizar TTAnxiety. El análisis de tu video muestra indicadores de ansiedad elevada. Promedio: %s

                    Sabemos que estos momentos pueden ser difíciles. Es importante que tomes medidas activas como:
                    • Realizar actividad física moderada diariamente.
                    • Usar técnicas de respiración o meditación.
                    • Buscar apoyo emocional en tu red cercana.
                    • Consultar a un profesional si los síntomas persisten.

                    Revisa la infografía anexa para sugerencias prácticas. Estamos contigo en este proceso.
                    """.formatted(avgStr));
            }
        }

        if (r.getSparklineB64() != null && !r.getSparklineB64().isBlank())
            t.setSparkline(java.util.Base64.getDecoder().decode(r.getSparklineB64()));
        
        return t;
    }

    private byte[] toBytes(Resource res) {
        try (var in = res.getInputStream()) { return in.readAllBytes(); }
        catch (Exception e) { return null; }
    }
}
