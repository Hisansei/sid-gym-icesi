package co.edu.icesi.sidgymicesi.config;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Profile;
import org.springframework.core.io.Resource;
import org.springframework.core.io.support.PathMatchingResourcePatternResolver;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.stereotype.Component;

import java.io.InputStream;
import java.util.List;
import java.util.Map;

@Component
@Profile("mongo")
public class MongoDataInitializer implements CommandLineRunner {

    private final MongoTemplate mongoTemplate;
    private final ObjectMapper objectMapper = new ObjectMapper();

    @Value("${app.mongo.init.drop:true}")
    private boolean dropCollections;

    @Value("${app.mongo.init.location:classpath:db/mongo/*.json}")
    private String seedLocationPattern;

    public MongoDataInitializer(MongoTemplate mongoTemplate) {
        this.mongoTemplate = mongoTemplate;
    }

    @Override
    public void run(String... args) throws Exception {
        PathMatchingResourcePatternResolver resolver = new PathMatchingResourcePatternResolver();
        Resource[] resources = resolver.getResources(seedLocationPattern);

        for (Resource resource : resources) {
            String filename = resource.getFilename();
            if (filename == null || !filename.endsWith(".json")) continue;

            String collection = filename.substring(0, filename.length() - ".json".length());

            if (dropCollections && mongoTemplate.collectionExists(collection)) {
                mongoTemplate.dropCollection(collection);
            }

            try (InputStream is = resource.getInputStream()) {
                List<Map<String, Object>> docs = objectMapper.readValue(
                        is, new TypeReference<List<Map<String, Object>>>() {});
                if (!docs.isEmpty()) {
                    mongoTemplate.getCollection(collection).insertMany(
                            docs.stream().map(org.bson.Document::new).toList()
                    );
                }
            }
        }
    }
}
