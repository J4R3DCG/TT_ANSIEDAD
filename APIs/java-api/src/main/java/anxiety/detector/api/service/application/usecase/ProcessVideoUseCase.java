package anxiety.detector.api.service.application.usecase;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import anxiety.detector.api.service.application.ports.input.ProcessVideoInputPort;
import anxiety.detector.api.service.application.ports.output.SendVideoOutputPort;
import anxiety.detector.api.service.application.service.EmailAsyncService;
import anxiety.detector.api.service.domain.model.AnxietyResult;
import anxiety.detector.api.service.domain.model.EmailTemplate;
import anxiety.detector.api.service.domain.model.UserRequest;
import anxiety.detector.api.service.framework.util.EmailValidator;
import anxiety.detector.api.service.framework.util.VideoDurationCalculator;
import anxiety.detector.api.service.framework.util.VideoValidator;

@Service
public class ProcessVideoUseCase implements ProcessVideoInputPort {

    private static final Logger LOG = LoggerFactory.getLogger(ProcessVideoUseCase.class);
    private final SendVideoOutputPort prototypePort;
    private final EmailAsyncService   mailAsync;
    private final SimulateAnxietyUseCase templateSelector;

    public ProcessVideoUseCase(SendVideoOutputPort prototypePort, EmailAsyncService mailAsync, SimulateAnxietyUseCase templateSelector) {
        this.prototypePort = prototypePort;
        this.mailAsync = mailAsync;
        this.templateSelector = templateSelector;
    }

    @Override
    public void execute(UserRequest req) {
        
        validate(req);
        long startTime = System.currentTimeMillis();
        LOG.info("Validación superada → se enviará el video '{}' del usuario '{}' al microservicio Prototype.",req.getFileName(), req.getEmail());
        AnxietyResult result = prototypePort.sendVideoToPrototype(req.getVideoData(), req.getFileName());
        long endTime = System.currentTimeMillis();
        long elapsedMillis = endTime - startTime;
        LOG.info("Resultado recibido del microservicio Prototype para el usuario '{}': nivel de ansiedad={}, tiempo de respuesta={} ms", req.getEmail(), result.getOverall(), elapsedMillis);
        EmailTemplate tpl = templateSelector.buildMail(result);
        mailAsync.send(req.getEmail(), tpl); 
    }

    private void validate(UserRequest r) {
        EmailValidator.validate(r.getEmail());
        VideoValidator.validateExtension(r.getFileName());
        VideoValidator.validateSize(r.getVideoData().length);
        VideoValidator.validateFFmpeg();
        long duration = VideoDurationCalculator.calculateDuration(r.getVideoData());
        VideoValidator.validateDurationMax(duration);
        VideoValidator.validateDurationMin(duration);

    }
}
