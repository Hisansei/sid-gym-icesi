package co.edu.icesi.sidgymicesi.model.postgres;

import jakarta.persistence.*;
import lombok.*;

@Entity
@Table(name = "user_monthly_stats")
@Getter @Setter @NoArgsConstructor @AllArgsConstructor @Builder
public class UserMonthlyStat {

    @EmbeddedId
    private UserMonthlyStatId id;

    @Column(name = "routines_started", nullable = false)
    private int routinesStarted;

    @Column(name = "followups_made", nullable = false)
    private int followupsMade;
}
