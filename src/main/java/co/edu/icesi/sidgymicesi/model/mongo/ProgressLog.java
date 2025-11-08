package co.edu.icesi.sidgymicesi.model.mongo;

import lombok.*;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.index.CompoundIndex;
import org.springframework.data.mongodb.core.index.Indexed;
import org.springframework.data.mongodb.core.mapping.Document;
import org.springframework.data.mongodb.core.mapping.Field;

import java.time.Instant;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

@Data @NoArgsConstructor @AllArgsConstructor @Builder
@Document("progress_logs")
@CompoundIndex(name = "routine_date_unique", def = "{'routine_id':1,'date':1}", unique = true)
public class ProgressLog {
    @Id
    private String id;

    @Indexed
    private String ownerUsername;

    @Field("routine_id")
    private String routineId;

    private LocalDate date;

    @Builder.Default
    private List<Entry> entries = new ArrayList<>();

    @Builder.Default
    @Field("trainer_feedback")
    private List<TrainerFeedback> trainerFeedback = new ArrayList<>();

    @Field("created_at")
    private Instant createdAt;

    @Data @NoArgsConstructor @AllArgsConstructor @Builder
    public static class Entry {
        @Field("exercise_id")
        private String exerciseId;

        @Field("is_completed")
        private boolean completed;

        private List<Integer> sets;
        private List<Integer> reps;

        @Field("weight_kg")
        private List<Double> weightKg;

        @Field("effort_level")
        private String effortLevel;

        @Field("notes_user")
        private String notesUser;
    }

    @Data @NoArgsConstructor @AllArgsConstructor @Builder
    public static class TrainerFeedback {
        @Field("trainer_id")
        private String trainerId;

        private String message;

        @Field("created_at")
        private Instant createdAt;
    }
}
