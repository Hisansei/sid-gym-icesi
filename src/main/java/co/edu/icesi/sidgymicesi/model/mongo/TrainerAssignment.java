package co.edu.icesi.sidgymicesi.model.mongo;

import lombok.*;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.index.CompoundIndex;
import org.springframework.data.mongodb.core.index.Indexed;
import org.springframework.data.mongodb.core.mapping.Document;
import org.springframework.data.mongodb.core.mapping.Field;

import java.time.LocalDateTime;

@Data @NoArgsConstructor @AllArgsConstructor @Builder
@Document("trainer_assignments")
@CompoundIndex(name = "active_by_user", def = "{'user_username':1,'active':1}")
public class TrainerAssignment {
    @Id
    private String id;

    @Field("trainer_id")
    private String trainerId;

    @Indexed
    @Field("user_username")
    private String userUsername;

    @Field("assigned_at")
    private LocalDateTime assignedAt;

    private boolean active;

    @Field("ended_at")
    private LocalDateTime endedAt;
}
