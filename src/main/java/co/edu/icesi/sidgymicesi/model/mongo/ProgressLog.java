package co.edu.icesi.sidgymicesi.model.mongo;

import co.edu.icesi.sidgymicesi.model.ProgressEntry;
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
    private List<ProgressEntry> entries = new ArrayList<>();

    @Field("created_at")
    private Instant createdAt;
}
