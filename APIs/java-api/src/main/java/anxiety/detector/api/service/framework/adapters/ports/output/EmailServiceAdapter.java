package anxiety.detector.api.service.framework.adapters.ports.output;

import java.io.UnsupportedEncodingException;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.ByteArrayResource;
import org.springframework.mail.MailException;
import org.springframework.mail.MailSendException;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.stereotype.Component;

import anxiety.detector.api.service.application.ports.output.SendEmailOutputPort;
import anxiety.detector.api.service.domain.model.EmailTemplate;
import jakarta.mail.MessagingException;
import jakarta.mail.internet.MimeMessage;

@Component
public class EmailServiceAdapter implements SendEmailOutputPort {

    private static final Logger LOG = LoggerFactory.getLogger(EmailServiceAdapter.class);

    private final JavaMailSender mailSender;
    private final String fromAddress;

    public EmailServiceAdapter(JavaMailSender mailSender,
                               @Value("${mail.sender.username}") String fromAddress) {
        this.mailSender = mailSender;
        this.fromAddress = fromAddress;
    }

    @Override
    public void sendEmailWithTemplate(String to, EmailTemplate tpl) {

        try {
            MimeMessage mime = mailSender.createMimeMessage();
            MimeMessageHelper helper = new MimeMessageHelper(mime, true, "UTF-8");

            helper.setFrom(fromAddress, "Detector de Ansiedad");
            helper.setTo(to);
            helper.setSubject(tpl.getSubject());

            String plain = tpl.getBody();
            StringBuilder html = new StringBuilder();

            html.append("""
                <div style="font-family:Arial,sans-serif;max-width:700px;margin:auto;
                            padding:20px;border:1px solid #ddd;border-radius:10px;">
                  <h2 style="color:#2c3e50;">Reporte de Análisis Emocional</h2>
                  <p style="font-size:16px;color:#333;">
                """)
                .append(plain.replace("\n", "<br/>"))
                .append("</p>");
    
            if (tpl.getInfographic() != null && tpl.getInfographic().length > 0) {
                html.append("""
                      <hr style="margin:20px 0;"/>
                      <h3 style="color:#2c3e50;">Infografía de recomendaciones</h3>
                      <img src='cid:info'
                           style='width:400px;max-width:100%;border:1px solid #ccc;border-radius:5px;'/>
                    """);
            }
    
            if (tpl.getSparkline() != null && tpl.getSparkline().length > 0) {
                html.append("""
                      <h3 style="color:#2c3e50;">Gráfica de evolución de ansiedad</h3>
                      <img src='cid:chart'
                           style='width:400px;max-width:100%;border:1px solid #ccc;border-radius:5px;'/>
                    """);
            }
    
            html.append("""
                  <p style="font-size:12px;color:#888;margin-top:30px;">
                    Este correo fue generado automáticamente por TTAnxiety.
                  </p>
                </div>
                """);
    
            helper.setText(plain, html.toString());

            if (tpl.getInfographic() != null && tpl.getInfographic().length > 0) {
                helper.addInline("info", new ByteArrayResource(tpl.getInfographic()) {
                        @Override public String getFilename() { return "infografia.jpg"; }
                    }, "image/jpeg");
            }
    
            if (tpl.getSparkline() != null && tpl.getSparkline().length > 0) {
                helper.addInline("chart", new ByteArrayResource(tpl.getSparkline()) {
                        @Override public String getFilename() { return "sparkline.jpg"; }
                    }, "image/jpeg");
            }

            mailSender.send(mime);
            LOG.info("Email sent successfully to {}", to);

        } catch (MessagingException | UnsupportedEncodingException | MailException ex) {
            LOG.error("Failed to send email to {}", to, ex);
            throw new MailSendException("Email send failed", ex);
        }
    }
}
