package anxiety.detector.api.service.framework.adapters.ports.input;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import anxiety.detector.api.service.application.ports.input.ProcessVideoInputPort;
import anxiety.detector.api.service.domain.model.SuccessResponse;
import anxiety.detector.api.service.domain.model.UserRequest;
import anxiety.detector.api.service.framework.exception.ValidationException;

@RestController
@RequestMapping("/api/video")
public class VideoController {

    private static final Logger LOG = LoggerFactory.getLogger(VideoController.class);
    private final ProcessVideoInputPort processVideo;

    public VideoController(ProcessVideoInputPort processVideo) {
        this.processVideo = processVideo;
    }

    @PostMapping("/process")
    public ResponseEntity<?> process(@RequestParam String email, @RequestParam MultipartFile video) {

        try {
            UserRequest request = new UserRequest(email, video.getBytes(), video.getOriginalFilename());
            processVideo.execute(request);
            String msg = String.format("Tus datos fueron validados y están siendo procesados. Recibirás los resultados al correo: %s en unos segundos", email);
            return ResponseEntity.ok(new SuccessResponse(0, msg));        
        } catch (ValidationException ex) {
            LOG.error("Validation error", ex);
            return ResponseEntity.badRequest().body(ex.getErrorResponse());
        } catch (Exception ex) {
            LOG.error("Unexpected error", ex);
            return ResponseEntity.internalServerError().body("Unexpected error");
        }
    }
}
