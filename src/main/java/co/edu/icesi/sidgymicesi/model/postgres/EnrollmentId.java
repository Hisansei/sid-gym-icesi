package co.edu.icesi.sidgymicesi.model.postgres;

import jakarta.persistence.*;
import lombok.*;

import java.io.Serializable;

@Embeddable
@Getter @Setter @NoArgsConstructor @AllArgsConstructor @EqualsAndHashCode
public class EnrollmentId implements Serializable {

    @Column(name = "student_id", length = 15, nullable = false)
    private String studentId;

    @Column(name = "nrc", length = 10, nullable = false)
    private String nrc;
}
