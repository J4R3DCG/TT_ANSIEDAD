package anxiety.detector.api.service.framework.util;

import java.util.Arrays;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;
import org.springframework.web.filter.CorsFilter;

@Configuration
public class CORSConfig {

    @Value("${cors.allow-credentials}")
    private boolean allowCredentials;

    @Value("${cors.allowed-origins}")
    private String allowedOrigins;  

    @Value("${cors.allowed-methods}")
    private String allowedMethods;    

    @Value("${cors.allowed-headers}")
    private String allowedHeaders;

    @Bean
    public CorsFilter corsFilter() {
        CorsConfiguration config = new CorsConfiguration();
        config.setAllowCredentials(allowCredentials);
        config.setAllowedOrigins(Arrays.asList(allowedOrigins.split("\\s*,\\s*")));
        config.setAllowedMethods(Arrays.asList(allowedMethods.split("\\s*,\\s*")));
        config.setAllowedHeaders(Arrays.asList(allowedHeaders.split("\\s*,\\s*")));

        UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
        source.registerCorsConfiguration("/api/**", config);
        return new CorsFilter(source);
    }
}
