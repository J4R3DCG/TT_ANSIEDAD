package anxiety.detector.api.service.framework.config;

import java.time.Duration;

import org.apache.hc.client5.http.impl.classic.CloseableHttpClient;
import org.apache.hc.client5.http.impl.classic.HttpClients;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.web.client.RestTemplateBuilder;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.client.HttpComponentsClientHttpRequestFactory;
import org.springframework.web.client.RestTemplate;

@Configuration
public class RestClientConfig {

    @Value("${rest.connect-timeout-ms:5000}")
    private int connectMs;

    @Value("${rest.read-timeout-ms:30000}")
    private int readMs;

    @Bean
    public RestTemplate restTemplate(RestTemplateBuilder builder) {

        CloseableHttpClient httpClient = HttpClients.custom().build();
        HttpComponentsClientHttpRequestFactory factory = new HttpComponentsClientHttpRequestFactory(httpClient);
        factory.setConnectTimeout(connectMs);
        factory.setReadTimeout(readMs);

        return builder
                .requestFactory(() -> factory)
                .setConnectTimeout(Duration.ofMillis(connectMs))
                .setReadTimeout(Duration.ofMillis(readMs))
                .build();
    }
}
