package co.edu.icesi.sidgymicesi.model.mongo;

import lombok.*;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.index.Indexed;
import org.springframework.data.mongodb.core.mapping.Document;
import org.springframework.data.mongodb.core.mapping.Field;

import java.time.Instant;
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
    private String description;
    private String level;

    @Field("created_at")
    private Instant createdAt;

    private boolean status;

    @Builder.Default
    private List<RoutineExercise> exercises = new ArrayList<>();

    @Data @NoArgsConstructor @AllArgsConstructor @Builder
    public static class RoutineExercise {
        private String id;
        private int order;

        @Field("exercise_id")
        private String exerciseId;

        private String name;
        private String type;
        private String description;

        @Field("duration_seconds")
        private Integer durationSeconds;

        private String difficulty;

        @Field("demo_videos")
        private List<String> demoVideos;

        private int sets;
        private int reps;

        @Field("rest_seconds")
        private int restSeconds;
    }
}
