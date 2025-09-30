/*package anxiety.detector.api.service.framework.adapters.ports.output;

import java.util.List;
import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;
import org.springframework.web.client.HttpStatusCodeException;
import org.springframework.web.client.RestTemplate;

import com.fasterxml.jackson.databind.ObjectMapper;

import anxiety.detector.api.service.application.ports.output.CopyWriterPort;
import anxiety.detector.api.service.domain.model.AnxietyResult;

@Component
public class DeepSeekChatAdapter implements CopyWriterPort {

    private static final Logger LOG = LoggerFactory.getLogger(DeepSeekChatAdapter.class);

    private final RestTemplate rt;
    private final String url;
    private final String hfToken;
    private final ObjectMapper mapper = new ObjectMapper();

    public DeepSeekChatAdapter(RestTemplate rt,
                               @Value("${llm.url:https://api-inference.huggingface.co/models/deepseek-ai/deepseek-llm-7b-chat}") String url,
                               @Value("${hf.token:}") String hfToken) {
        this.rt = rt;
        this.url = url;
        this.hfToken = hfToken;
    }

    @Override
    public String buildBody(AnxietyResult ar) {

        String json;
        try {
            json = mapper.writerWithDefaultPrettyPrinter().writeValueAsString(ar);
        } catch (Exception e) {
            LOG.warn("No se pudo serializar AnxietyResult", e);
            json = "{}";
        }

        String prompt = """
            Te llamas “TTAnxiety”, eres coach certificado en bienestar emocional y analista de datos profesional.  
            Tu objetivo es transformar un json de mediciones numéricas de ansiedad por ventanas de un video en texto plano entendible para una persona con empatía.

            Para ponerte en contexto, cada window del json dura 5 segundos, de la lista de windows, el id muestra el número de la ventana, el start muestra el frame del que empieza y el end es el frame en el que termina, y anxiety es el nivel de ansiedad que tuvo esa window. El campo overall es el promedio de ansiedad de todas las ventanas.
            La Escala de ansiedad que se maneja es de (0-4), donde:
            0 = Muy Bajo  (felicidad clara)
            1 = Bajo      (felicidad leve)
            2 = Medio     (neutro)
            3 = Alto      (ansiedad leve)
            4 = Muy Alto  (ansiedad clara)

            Explica el JSON a tu paciente, como si le explicaras lo que pasó en su video, usa menos de 300 palabras y usa emojis obligatoriamente.
            Este es el JSON del video del paciente:
            %s
        
            Responde solo con el análisis. Comienza siempre con: "Respuesta:" no olvides saludar y presentarte como TTAnxiety, explicar el json y finalmente despedirte.
            """.formatted(json);

        Map<String, Object> payload = Map.of("inputs", prompt,"parameters", Map.of("temperature", 0.7, "top_p", 0.9, "max_new_tokens", 500));

        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        if (!hfToken.isBlank()) {
            headers.setBearerAuth(hfToken);
            LOG.debug("Usando token HF ******{}", hfToken.substring(Math.max(0, hfToken.length() - 4)));
        } else {
            LOG.warn("hf.token vacío: la API puede responder 403 o 429 (rate-limit)");
        }

        try {
            ResponseEntity<List> resp = rt.postForEntity(url, new HttpEntity<>(payload, headers), List.class);
            LOG.debug("LLM status={} bytes={}", resp.getStatusCode(), resp.toString().length());

            String txt = (String) ((Map<?, ?>) resp.getBody().get(0)).get("generated_text");
            LOG.info("LLM OK, longitud {} caracteres", txt.length());
            
            int firstIdx = txt.indexOf("Respuesta:");
            int secondIdx = (firstIdx != -1) ? txt.indexOf("Respuesta:", firstIdx + 1) : -1;

            if (secondIdx != -1) {
                txt = txt.substring(secondIdx + "Respuesta:".length()).stripLeading();
            } else if (firstIdx != -1) {
                txt = txt.substring(firstIdx + "Respuesta:".length()).stripLeading();
            } else {
                txt = txt.stripLeading();
            }

            return txt;

        } catch (HttpStatusCodeException ex) {
            LOG.error("LLM HTTP {} body={}", ex.getStatusCode(), ex.getResponseBodyAsString());
        } catch (Exception ex) {
            LOG.error("LLM ERROR genérico", ex);
        }
        return "Tú resultado global del video es de: " + ar.getOverall();
    }
}
*/