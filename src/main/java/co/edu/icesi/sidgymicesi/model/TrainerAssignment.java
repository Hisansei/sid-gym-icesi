package co.edu.icesi.sidgymicesi.model;

import lombok.*;
import java.time.LocalDateTime;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class TrainerAssignment {
    private String id;

    private String userUsername;
    private String trainerUsername;

    private boolean active;
    private LocalDateTime assignedAt;
    private LocalDateTime endedAt;
}
