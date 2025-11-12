package co.edu.icesi.sidgymicesi.config;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.bson.Document;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
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
import java.util.stream.Collectors;

@Component
@Profile("mongo")
public class MongoDataInitializer implements CommandLineRunner {

    private static final Logger log = LoggerFactory.getLogger(MongoDataInitializer.class);

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
    public void run(String... args) throws Exception { // <-- firma correcta
        PathMatchingResourcePatternResolver resolver = new PathMatchingResourcePatternResolver();
        Resource[] resources = resolver.getResources(seedLocationPattern);

        for (Resource resource : resources) {
            String filename = resource.getFilename();
            if (filename == null || !filename.endsWith(".json")) continue;

            String collection = filename.substring(0, filename.length() - ".json".length());

            if (dropCollections && mongoTemplate.collectionExists(collection)) {
                log.info("Dropping collection '{}'", collection);
                mongoTemplate.dropCollection(collection);
            }

            try (InputStream is = resource.getInputStream()) {
                List<Map<String, Object>> docs = objectMapper.readValue(
                        is, new TypeReference<List<Map<String, Object>>>() {}
                );
                if (!docs.isEmpty()) {
                    List<Document> documents = docs.stream()
                            .map(Document::new)
                            .collect(Collectors.toList());
                    log.info("Seeding {} docs into '{}'", documents.size(), collection);
                    mongoTemplate.getCollection(collection).insertMany(documents);
                } else {
                    log.info("Seed file '{}' vacío; no se insertan docs.", filename);
                }
            }
        }
        log.info("Mongo seed DONE (drop={}, pattern={})", dropCollections, seedLocationPattern);
    }
}
