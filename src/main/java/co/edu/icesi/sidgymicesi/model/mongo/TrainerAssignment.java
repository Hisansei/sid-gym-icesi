package co.edu.icesi.sidgymicesi.model.mongo;

import lombok.*;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.index.Indexed;
import org.springframework.data.mongodb.core.mapping.Document;
import org.springframework.data.mongodb.core.mapping.Field;

import java.time.Instant;

@Data @NoArgsConstructor @AllArgsConstructor @Builder
@Document("trainer_assignments")
public class TrainerAssignment {
    @Id
    private String id;

    @Field("trainer_id") @Indexed
    private String trainerId;

    @Field("user_username") @Indexed
    private String userUsername;

    @Field("assigned_at")
    private Instant assignedAt;
}
