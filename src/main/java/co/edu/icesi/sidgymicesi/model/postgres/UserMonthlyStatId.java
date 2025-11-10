package co.edu.icesi.sidgymicesi.model.postgres;

import jakarta.persistence.Column;
import jakarta.persistence.Embeddable;
import lombok.*;

import java.io.Serializable;

@Embeddable
@Getter @Setter @NoArgsConstructor @AllArgsConstructor @EqualsAndHashCode
public class UserMonthlyStatId implements Serializable {
    @Column(name = "user_username", length = 100, nullable = false)
    private String userUsername;

    @Column(name = "period", length = 7, nullable = false) // YYYY-MM
    private String period;
}
