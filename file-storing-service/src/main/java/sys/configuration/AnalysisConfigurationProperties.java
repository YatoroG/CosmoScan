package sys.configuration;

import org.springframework.boot.context.properties.ConfigurationProperties;

@ConfigurationProperties("app.file-analysis-service")
public record AnalysisConfigurationProperties(String url) {
}
