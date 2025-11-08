package co.edu.icesi.sidgymicesi.model.mongo;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.index.CompoundIndex;
import org.springframework.data.mongodb.core.index.CompoundIndexes;
import org.springframework.data.mongodb.core.index.Indexed;
import org.springframework.data.mongodb.core.mapping.Document;
import org.springframework.data.mongodb.core.mapping.Field;

import java.time.Instant;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

@Document("progress_logs")
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
// Índices compuestos para búsquedas rápidas
@CompoundIndexes({
        // índice único por rutina y día
        @CompoundIndex(name="routine_day", def="{'routine_id':1,'date':1}", unique=true),
        // índice para búsquedas por usuario y día
        @CompoundIndex(name="owner_day",   def="{'username':1,'date':1}")
})
public class ProgressLog {
    @Id
    private String id;
    @Indexed
    private String username;

    @Field("routine_id")
    private String routineId;

    private LocalDate createdAt;   // un log por día/rutina

    @Builder.Default
    private List<Entry> entries = new ArrayList<>();

    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    @Builder
    public static class Entry {

        @Field("exercise_id")
        private String exerciseId;

        @Field("is_completed")
        private Boolean completed;

        private Integer sets;
        private Integer reps;

        @Field("weight_kg")
        private Double weightKg;

        @Field("effort_level")
        private Integer effortLevel; // RPE

        @Field("notes_user")
        private String notesUser;
    }

    @Field("trainer_feedback")
    @Builder.Default
    private List<TrainerFeedback> trainerFeedback = new ArrayList<>();

    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    @Builder
    public static class TrainerFeedback {
        @Field("trainer_id")
        private String trainerId;

        private String message;

        @Field("created_at")
        private Instant createdAt;
    }
}
