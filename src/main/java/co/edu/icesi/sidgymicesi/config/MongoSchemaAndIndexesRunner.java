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
        // exercises (igual)
        ensureIndex("exercises", Indexes.ascending("name"), new IndexOptions().name("idx_ex_name").unique(true));
        ensureIndex("exercises", Indexes.ascending("type"), new IndexOptions().name("idx_ex_type"));
        ensureIndex("exercises", Indexes.ascending("difficulty"), new IndexOptions().name("idx_ex_diff"));

        // user_routines (ANTES decía "routines")
        ensureIndex("user_routines", Indexes.ascending("ownerUsername"), new IndexOptions().name("idx_rt_owner"));
        ensureIndex("user_routines", Indexes.descending("created_at"), new IndexOptions().name("idx_rt_createdAt"));

        // routine_templates (ANTES camelCase)
        ensureIndex("routine_templates", Indexes.ascending("status"), new IndexOptions().name("idx_rtt_status"));
        ensureIndex("routine_templates", Indexes.ascending("trainer_id"), new IndexOptions().name("idx_rtt_trainer"));

        // progress_logs (ANTES camelCase)
        ensureIndex("progress_logs",
                Indexes.compoundIndex(Indexes.ascending("routine_id"), Indexes.ascending("date")),
                new IndexOptions().name("idx_pl_routine_date"));
        ensureIndex("progress_logs",
                Indexes.compoundIndex(Indexes.ascending("ownerUsername"), Indexes.descending("date")),
                new IndexOptions().name("idx_pl_owner_date"));
        ensureIndex("progress_logs", Indexes.ascending("date"), new IndexOptions().name("idx_pl_date"));

        // trainer_assignments (ANTES camelCase)
        ensureIndex("trainer_assignments", Indexes.ascending("trainer_id"), new IndexOptions().name("idx_ta_trainer"));
        ensureIndex("trainer_assignments", Indexes.ascending("user_username"), new IndexOptions().name("idx_ta_user"));
        ensureIndex("trainer_assignments",
                Indexes.compoundIndex(Indexes.ascending("user_username"), Indexes.ascending("active")),
                new IndexOptions().name("idx_ta_user_active"));

    }

    private void ensureIndex(String collectionName, org.bson.conversions.Bson index, IndexOptions options) {
        MongoCollection<Document> col = mongoTemplate.getCollection(collectionName);
        col.createIndex(index, options);
    }
}
