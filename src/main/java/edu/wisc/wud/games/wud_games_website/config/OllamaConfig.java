import org.springframework.ai.embedding.EmbeddingModel;
import org.springframework.ai.ollama.OllamaEmbeddingModel;
import org.springframework.ai.ollama.api.OllamaApi;
import org.springframework.ai.ollama.api.OllamaEmbeddingOptions;
import org.springframework.ai.ollama.management.ModelManagementOptions;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import io.micrometer.observation.ObservationRegistry;

@Configuration
public class OllamaConfig {

    private final OllamaApi ollamaApi = OllamaApi.builder().build();

    @Value("${spring.ai.ollama.embedding.options.model}")
    private String ollamaEmbeddingModel;

    @Bean(name = "ollamaEmbeddingModel")
    public EmbeddingModel getOllamaEmbeddingModel() {
        return OllamaEmbeddingModel.builder()
                .ollamaApi(ollamaApi)
                .options(OllamaEmbeddingOptions.builder()
                        .model(ollamaEmbeddingModel)
                        .build())
                //.defaultOptions(OllamaOptions.builder()
                //        .model(ollamaEmbeddingModel)
                //        .build())
                .observationRegistry(ObservationRegistry.create())
                .modelManagementOptions(ModelManagementOptions.defaults())
                .build();
    }
}