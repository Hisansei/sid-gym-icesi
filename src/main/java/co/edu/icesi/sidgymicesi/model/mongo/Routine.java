package co.edu.icesi.sidgymicesi.model.mongo;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.index.CompoundIndex;
import org.springframework.data.mongodb.core.index.Indexed;
import org.springframework.data.mongodb.core.mapping.Document;
import org.springframework.data.mongodb.core.mapping.Field;

import java.time.Instant;
import java.util.ArrayList;
import java.util.List;

@Document("user_routines")
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
// Índice compuesto para búsquedas rápidas por usuario y nombre de rutina
@CompoundIndex(name = "owner_name", def = "{'username':1, 'name':1}")
public class Routine {
    @Id
    private String id;

    // Consultas rápidas por usuario
    @Indexed
    private String username;

    @Field("origin_template_id")
    private String originTemplateId;

    private String name;
    private String description;
    private String level;

    @Field("created_at")
    private Instant createdAt;

    private boolean status;

    @Builder.Default
    private List<RoutineItem> exercises = new ArrayList<>();

    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    @Builder
    // tiene otro nombre en el modelo
    public static class RoutineItem {
        private String id;
        private int order;

        // Faltan unas pero no sé si agregarlas aquí
        // porque ya están en Exercise
        @Field("exercise_id")
        private String exerciseId;

        private Integer sets;
        private Integer reps;

        @Field("rest_seconds")
        private Integer restSeconds;
    }
}
