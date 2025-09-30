package anxiety.detector.api.service.framework.adapters.ports.output;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.ByteArrayResource;
import org.springframework.http.HttpEntity;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.client.HttpStatusCodeException;
import org.springframework.web.client.RestClientException;
import org.springframework.web.client.RestTemplate;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;

import anxiety.detector.api.service.application.ports.output.SendVideoOutputPort;
import anxiety.detector.api.service.domain.model.AnxietyResult;
import anxiety.detector.api.service.domain.model.ValidationError;
import anxiety.detector.api.service.framework.exception.ErrorResponse;
import anxiety.detector.api.service.framework.exception.ValidationException;

@Component
public class PrototypeServiceAdapter implements SendVideoOutputPort {

    private static final Logger LOG = LoggerFactory.getLogger(PrototypeServiceAdapter.class);

    private final RestTemplate restTemplate;
    private final String prototypeUrl;
    private final ObjectMapper mapper = new ObjectMapper();

    public PrototypeServiceAdapter(RestTemplate restTemplate, @Value("${prototype.url}") String prototypeUrl) {
        this.restTemplate = restTemplate;
        this.prototypeUrl = prototypeUrl; 
    }

    @Override
    public AnxietyResult sendVideoToPrototype(byte[] data, String fileName) {

        ByteArrayResource fileRes = new ByteArrayResource(data) {
            @Override 
            public String getFilename(){ 
                return fileName; 
            }
        };

        MultiValueMap<String, Object> body = new LinkedMultiValueMap<>();
        body.add("video", fileRes);   

        HttpEntity<MultiValueMap<String, Object>> request = new HttpEntity<>(body);
        ResponseEntity<AnxietyResult> resp;
        
        try {
            resp = restTemplate.postForEntity(prototypeUrl, request, AnxietyResult.class);
        } catch (HttpStatusCodeException ex) {      
            handleProtoError(ex);
            return null; 
        }

        if (!resp.getStatusCode().is2xxSuccessful() || resp.getBody() == null) {
            LOG.error("Python service status {} sin body", resp.getStatusCode());
            throw new RestClientException("Invalid response from Python service");
        }

        AnxietyResult ar = resp.getBody();
        if (ar.isError()) {
            ValidationError ve = new ValidationError("Prototype Error", ar.getError() != null ? ar.getError().getDetail() : "Unexpected Error", 3000);
            throw new ValidationException(ErrorResponse.of(ve));
        }

        LOG.info("Python returned result overall={} (frames={}), (totalWindows{})", ar.getOverall(), ar.getFramesAnalyzed(), ar.getWindows().size());
        return ar;
    }

    private void handleProtoError(HttpStatusCodeException ex) {
        String body = ex.getResponseBodyAsString();
        String detail = "Unexpected Error";
        try {
            JsonNode node = mapper.readTree(body);
            detail = node.has("detail") ? node.get("detail").asText() : "Unexpected Error";
        } catch (Exception ignore) { }
    
        int code;
        switch (detail) {
            case "UNSUPPORTED_FORMAT" -> code = 2001;
            case "NO_VALID_WINDOWS" -> code = 2002;
            case "WINDOW_TIMEOUT" -> code = 2003;
            case "CANNOT_OPEN_VIDEO" -> code = 2004;   
            case "NON_MONOTONIC_TIMESTAMPS"     -> code = 2005;
            case "PREPROCESSING_ERROR"          -> code = 2006; 
            default -> code = 2999;
        }
    
        ValidationError ve = new ValidationError("Prototype Error", detail, code);
        throw new ValidationException(ErrorResponse.of(ve));
    }
}
