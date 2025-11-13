package co.edu.icesi.sidgymicesi.model.postgres;

import jakarta.persistence.*;
import lombok.*;

@Entity
@Table(name = "trainer_monthly_stats")
@Getter @Setter @NoArgsConstructor @AllArgsConstructor @Builder
public class TrainerMonthlyStat {

    @EmbeddedId
    private TrainerMonthlyStatId id;

    @Column(name = "new_assignments", nullable = false)
    private int newAssignments;

    @Column(name = "followups_made", nullable = false)
    private int followupsMade;

}