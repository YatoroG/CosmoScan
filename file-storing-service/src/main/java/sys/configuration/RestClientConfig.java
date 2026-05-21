package sys.configuration;

import lombok.RequiredArgsConstructor;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.client.RestClient;

@RequiredArgsConstructor
@Configuration
@EnableConfigurationProperties(AnalysisConfigurationProperties.class)
public class RestClientConfig {
    private final AnalysisConfigurationProperties properties;

    @Bean
    public RestClient analysisRestClient() {
        return RestClient.builder()
                .baseUrl(properties.url())
                .build();
    }
}
