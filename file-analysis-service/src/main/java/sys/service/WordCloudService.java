package sys.service;

import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.Map;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestClient;

@Slf4j
@Service
public class WordCloudService {
    private final RestClient restClient = RestClient.create();

    public byte[] generate(String filePath) {
        try {
            String content = Files.readString(Paths.get(filePath));
            Map<String, Object> body = Map.of(
                    "text", content,
                    "format", "png",
                    "width", 200,
                    "height", 200,
                    "maxNumWords", 15,
                    "removeStopwords", true
            );

            return restClient.post()
                    .uri("https://quickchart.io/wordcloud")
                    .body(body)
                    .retrieve()
                    .body(byte[].class);
        } catch (Exception e) {
            log.error("Ошибка при создании облака слов: {}", e.getMessage());
            return null;
        }
    }
}
