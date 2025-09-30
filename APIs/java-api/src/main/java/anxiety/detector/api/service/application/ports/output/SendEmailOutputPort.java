package anxiety.detector.api.service.application.ports.output;

import anxiety.detector.api.service.domain.model.EmailTemplate;

public interface SendEmailOutputPort {
    void sendEmailWithTemplate(String email, EmailTemplate template);
}