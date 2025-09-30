package anxiety.detector.api.service.framework.config;

import java.util.Properties;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.JavaMailSenderImpl;

@Configuration
public class MailConfig {

    @Value("${mail.sender.host}")      private String host;
    @Value("${mail.sender.port}")      private int    port;
    @Value("${mail.sender.username}")  private String username;
    @Value("${mail.sender.password}")  private String password;

    @Value("${mail.sender.timeout-ms:5000}")      private int  timeout;
    @Value("${mail.sender.pool.enabled:true}")    private boolean poolEnabled;
    @Value("${mail.sender.pool.size:5}")          private int  poolSize;
    @Value("${mail.sender.pool.ttl-ms:600000}")   private int  poolTtl;
    @Value("${mail.debug:false}")                 private boolean debug;

    @Bean
    public JavaMailSender javaMailSender() {
        JavaMailSenderImpl sender = new JavaMailSenderImpl();
        sender.setHost(host);
        sender.setPort(port);
        sender.setUsername(username);
        sender.setPassword(password);

        Properties p = sender.getJavaMailProperties();
        p.put("mail.transport.protocol", "smtp");
        p.put("mail.smtp.auth", "true");
        p.put("mail.smtp.starttls.enable", "true");

        p.put("mail.smtp.connectiontimeout", timeout);
        p.put("mail.smtp.timeout", timeout);
        p.put("mail.smtp.writetimeout", timeout);

        p.put("mail.smtp.pool", poolEnabled);
        p.put("mail.smtp.poolsize", poolSize);
        p.put("mail.smtp.pooltimeout", poolTtl);

        p.put("mail.smtp.quitwait", "false");
        p.put("mail.debug", debug);
        p.put("mail.smtp.from", username);
        return sender;
    }
}
