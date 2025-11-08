package co.edu.icesi.sidgymicesi.model.mongo;

import org.springframework.data.annotation.Id;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.mongodb.core.index.Indexed;
import org.springframework.data.mongodb.core.mapping.Document;
import org.springframework.data.mongodb.core.mapping.Field;

import java.time.Instant;
import java.util.ArrayList;
import java.util.List;

@Document("routine_templates")
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class RoutineTemplate {
    @Id
    private String id;
    private String name;
    private String description;
    private String level; // BEGINNER|INTERMEDIATE|ADVANCED

    @Indexed
    @Field("trainer_id")
    private String trainerId;

    @Field("last_updated_at")
    private Instant lastUpdatedAt;

    private boolean status;

    @Builder.Default
    private List<TemplateItem> exercises = new ArrayList<>();

    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    @Builder
    public static class TemplateItem {
        private int order;

        @Field("exercise_id")
        private String exerciseId;

        private int sets;

        private int reps;

        @Field("rest_seconds")
        private int restSeconds;
    }
}
