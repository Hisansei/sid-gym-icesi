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

    private final MongoTemplate mongo;

    @Override
    public void run(String... args) {
        ensureExercisesValidator();
        ensureRoutineTemplatesValidator();
        ensureUserRoutinesValidatorAndIndexes();
        ensureProgressLogsValidatorAndIndexes();
    }

    private void ensureExercisesValidator() {
        Document validator = Document.parse("""
        {
          "$jsonSchema": {
            "bsonType": "object",
            "required": ["name","type"],
            "properties": {
              "name":  { "bsonType": "string", "minLength": 1 },
              "type":  { "enum": ["cardio","fuerza","movilidad"] },
              "description": { "bsonType": ["string","null"] },
              "duration_seconds": { "bsonType": ["int","long","null"], "minimum": 0 },
              "difficulty": { "bsonType": ["string","null"] },
              "demo_videos": {
                "bsonType": ["array","null"],
                "items": { "bsonType": "string" }
              }
            }
          }
        }
        """);
        applyValidator("exercises", validator);
    }

    private void ensureRoutineTemplatesValidator() {
        Document validator = Document.parse("""
        {
          "$jsonSchema": {
            "bsonType": "object",
            "required": ["name","trainer_id","exercises","status"],
            "properties": {
              "name": { "bsonType": "string", "minLength": 1 },
              "description": { "bsonType": ["string","null"] },
              "level": { "bsonType": ["string","null"] },
              "trainer_id": { "bsonType": "string", "minLength": 1 },
              "last_updated_at": { "bsonType": ["date","string","null"] },
              "status": { "bsonType": "bool" },
              "exercises": {
                "bsonType": "array",
                "items": {
                  "bsonType": "object",
                  "required": ["order","exercise_id","sets","reps","rest_seconds"],
                  "properties": {
                    "order": { "bsonType": ["int","long"], "minimum": 1 },
                    "exercise_id": { "bsonType": "string", "minLength": 1 },
                    "sets": { "bsonType": ["int","long"], "minimum": 0 },
                    "reps": { "bsonType": ["int","long"], "minimum": 0 },
                    "rest_seconds": { "bsonType": ["int","long"], "minimum": 0 }
                  }
                }
              }
            }
          }
        }
        """);
        applyValidator("routine_templates", validator);
    }

    private void ensureUserRoutinesValidatorAndIndexes() {
        Document validator = Document.parse("""
        {
          "$jsonSchema": {
            "bsonType": "object",
            "required": ["ownerUsername","name","status"],
            "properties": {
              "ownerUsername": { "bsonType": "string", "minLength": 1 },
              "origin_template_id": { "bsonType": ["string","null"] },
              "name": { "bsonType": "string", "minLength": 1 },
              "description": { "bsonType": ["string","null"] },
              "level": { "bsonType": ["string","null"] },
              "created_at": { "bsonType": ["date","null"] },
              "status": { "bsonType": "bool" },
              "exercises": {
                "bsonType": ["array","null"],
                "items": {
                  "bsonType": "object",
                  "required": ["id","order"],
                  "properties": {
                    "id": { "bsonType": "string", "minLength": 1 },
                    "order": { "bsonType": ["int","long"], "minimum": 1 },
                    "exercise_id": { "bsonType": ["string","null"] },
                    "name": { "bsonType": ["string","null"] },
                    "type": { "enum": ["cardio","fuerza","movilidad", null] },
                    "description": { "bsonType": ["string","null"] },
                    "duration_seconds": { "bsonType": ["int","long","null"], "minimum": 0 },
                    "difficulty": { "bsonType": ["string","null"] },
                    "demo_videos": {
                      "bsonType": ["array","null"],
                      "items": { "bsonType": "string" }
                    },
                    "sets": { "bsonType": ["int","long","null"], "minimum": 0 },
                    "reps": { "bsonType": ["int","long","null"], "minimum": 0 },
                    "rest_seconds": { "bsonType": ["int","long","null"], "minimum": 0 }
                  }
                }
              }
            }
          }
        }
        """);
        applyValidator("user_routines", validator);

        MongoCollection<Document> col = mongo.getCollection("user_routines");
        col.createIndex(Indexes.compoundIndex(
                Indexes.ascending("ownerUsername"),
                Indexes.ascending("status")
        ), new IndexOptions().name("owner_status_idx"));
        col.createIndex(Indexes.ascending("exercises.exercise_id"),
                new IndexOptions().name("exercises_exercise_id_idx"));
    }

    private void ensureProgressLogsValidatorAndIndexes() {
        Document validator = Document.parse("""
        {
          "$jsonSchema": {
            "bsonType": "object",
            "required": ["ownerUsername","routine_id","date"],
            "properties": {
              "ownerUsername": { "bsonType": "string", "minLength": 1 },
              "routine_id": { "bsonType": "string", "minLength": 1 },
              "date": { "bsonType": "date" },
              "entries": {
                "bsonType": ["array","null"],
                "items": {
                  "bsonType": "object",
                  "required": ["exercise_id","is_completed"],
                  "properties": {
                    "exercise_id": { "bsonType": "string", "minLength": 1 },
                    "is_completed": { "bsonType": "bool" },
                    "sets": {
                      "bsonType": ["array","null"],
                      "items": { "bsonType": ["int","long"], "minimum": 0 }
                    },
                    "reps": {
                      "bsonType": ["array","null"],
                      "items": { "bsonType": ["int","long"], "minimum": 0 }
                    },
                    "weight_kg": {
                      "bsonType": ["array","null"],
                      "items": { "bsonType": ["double","int","long"], "minimum": 0 }
                    },
                    "effort_level": {
                      "bsonType": ["string","null"],
                      "pattern": "^(10|[1-9])$"
                    },
                    "notes_user": { "bsonType": ["string","null"] }
                  }
                }
              },
              "trainer_feedback": {
                "bsonType": ["array","null"],
                "items": {
                  "bsonType": "object",
                  "required": ["trainer_id","message"],
                  "properties": {
                    "trainer_id": { "bsonType": "string", "minLength": 1 },
                    "message": { "bsonType": "string", "minLength": 1 },
                    "created_at": { "bsonType": ["date","null"] }
                  }
                }
              },
              "created_at": { "bsonType": ["date","null"] }
            }
          }
        }
        """);
        applyValidator("progress_logs", validator);

        MongoCollection<Document> col = mongo.getCollection("progress_logs");
        col.createIndex(Indexes.compoundIndex(
                Indexes.ascending("ownerUsername"),
                Indexes.descending("date")
        ), new IndexOptions().name("owner_date_idx"));

        col.createIndex(Indexes.compoundIndex(
                Indexes.ascending("routine_id"),
                Indexes.descending("date")
        ), new IndexOptions().name("routine_date_idx"));

    }

    private void applyValidator(String collection, Document validator) {
        boolean exists = mongo.collectionExists(collection);
        if (!exists) {
            Document options = new Document("validator", validator);
            mongo.getDb().runCommand(new Document("create", collection).append("validator", validator));
        } else {
            mongo.getDb().runCommand(new Document("collMod", collection).append("validator", validator));
        }
    }
}
