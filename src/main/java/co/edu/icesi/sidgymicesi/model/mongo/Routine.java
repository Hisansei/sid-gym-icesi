package co.edu.icesi.sidgymicesi.model.mongo;

import lombok.*;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.index.Indexed;
import org.springframework.data.mongodb.core.mapping.Document;
import org.springframework.data.mongodb.core.mapping.Field;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Data @NoArgsConstructor @AllArgsConstructor @Builder
@Document("user_routines")
public class Routine {
    @Id
    private String id;

    @Indexed
    private String ownerUsername;

    @Field("origin_template_id")
    private String sourceTemplateId;

    private String name;

    @Field("created_at")
    private LocalDateTime createdAt;

    private String status;

    @Builder.Default
    private List<RoutineItem> items = new ArrayList<>();

    @Data @NoArgsConstructor @AllArgsConstructor @Builder
    public static class RoutineItem {
        private String id;
        private Integer orderIndex;
        @Field("exercise_id")
        private String exerciseId;

        private String customName;
        private String type;
        private String description;
        private Integer targetReps;
        private Integer targetTimeSeconds;
        private Integer targetIntensity;
    }
}
