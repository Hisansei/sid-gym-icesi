package co.edu.icesi.sidgymicesi.model;

import lombok.*;
import java.time.YearMonth;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor @Builder
public class TrainerMonthlyStats {
    private String trainerUsername;
    private YearMonth period;
    private int newAssignments;
}
