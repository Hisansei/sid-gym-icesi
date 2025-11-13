package co.edu.icesi.sidgymicesi.model.postgres;

import jakarta.persistence.*;
import lombok.*;

import java.io.Serializable;

@Embeddable
@Getter @Setter @NoArgsConstructor @AllArgsConstructor @EqualsAndHashCode
public class TrainerMonthlyStatId implements Serializable {
    @Column(name = "trainer_username", length = 100, nullable = false)
    private String trainerUsername;

    @Column(name = "period", length = 7, nullable = false)
    private String period; // YYYY-MM
}