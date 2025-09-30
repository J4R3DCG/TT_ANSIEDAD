package anxiety.detector.api.service.application.service;

import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;

import anxiety.detector.api.service.application.ports.output.SendEmailOutputPort;
import anxiety.detector.api.service.domain.model.EmailTemplate;

@Service
public class EmailAsyncService {

    private final SendEmailOutputPort sender;

    public EmailAsyncService(SendEmailOutputPort sender) {
        this.sender = sender;
    }

    @Async("mailExecutor")     
    public void send(String to, EmailTemplate tpl) {
        sender.sendEmailWithTemplate(to, tpl);
    }
}
