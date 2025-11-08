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

@Document("trainer_assignments")
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
@CompoundIndex(name="unique_active", def="{'trainer_id':1,'user_username':1,'active':1}")
public class TrainerAssignment {
    @Id
    private String id;

    @Field("trainer_id")
    @Indexed
    private String trainerId;

    @Field("user_username")
    @Indexed
    private String userUsername;

    @Field("assigned_at")
    private Instant assignedAt;

    // falta en el modelo
    private boolean active;
}
