package co.edu.icesi.sidgymicesi.config;

import com.mongodb.client.MongoCollection;
import com.mongodb.client.model.IndexOptions;
import com.mongodb.client.model.Indexes;
import lombok.RequiredArgsConstructor;
import org.bson.Document;
import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Profile;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.stereotype.Component;

@Component
@Profile("mongo")
@RequiredArgsConstructor
public class MongoSchemaAndIndexesRunner implements CommandLineRunner {

    private final MongoTemplate mongoTemplate;

    @Override
    public void run(String... args) {
        // exercises
        ensureIndex("exercises", Indexes.ascending("name"),
                new IndexOptions().name("idx_ex_name").unique(true));
        ensureIndex("exercises", Indexes.ascending("type"), new IndexOptions().name("idx_ex_type"));
        ensureIndex("exercises", Indexes.ascending("difficulty"), new IndexOptions().name("idx_ex_diff"));

        // routines
        ensureIndex("routines", Indexes.ascending("ownerUsername"),
                new IndexOptions().name("idx_rt_owner"));
        ensureIndex("routines", Indexes.descending("createdAt"),
                new IndexOptions().name("idx_rt_createdAt"));

        // routineTemplates
        ensureIndex("routineTemplates", Indexes.ascending("status"),
                new IndexOptions().name("idx_rtt_status"));
        ensureIndex("routineTemplates", Indexes.ascending("trainerId"),
                new IndexOptions().name("idx_rtt_trainer"));

        // progressLogs
        ensureIndex("progressLogs", Indexes.compoundIndex(Indexes.ascending("routineId"), Indexes.ascending("date")),
                new IndexOptions().name("idx_pl_routine_date"));
        ensureIndex("progressLogs", Indexes.compoundIndex(Indexes.ascending("ownerUsername"), Indexes.descending("date")),
                new IndexOptions().name("idx_pl_owner_date"));
        ensureIndex("progressLogs", Indexes.ascending("date"),
                new IndexOptions().name("idx_pl_date"));

        // trainerAssignments
        ensureIndex("trainerAssignments", Indexes.ascending("trainerId"),
                new IndexOptions().name("idx_ta_trainer"));
        ensureIndex("trainerAssignments", Indexes.ascending("user.username"),
                new IndexOptions().name("idx_ta_user"));
        ensureIndex("trainerAssignments", Indexes.compoundIndex(Indexes.ascending("user.username"),
                        Indexes.ascending("active")),
                new IndexOptions().name("idx_ta_user_active"));
    }

    private void ensureIndex(String collectionName, org.bson.conversions.Bson index, IndexOptions options) {
        MongoCollection<Document> col = mongoTemplate.getCollection(collectionName);
        col.createIndex(index, options);
    }
}
