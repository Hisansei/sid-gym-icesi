package co.edu.icesi.sidgymicesi.model.postgres;

import jakarta.persistence.*;
import lombok.*;

@Entity
@Table(name = "groups")
@Getter @Setter @NoArgsConstructor @AllArgsConstructor @Builder
@EqualsAndHashCode(of = "nrc")
public class CourseGroup {

    @Id
    @Column(name = "nrc", length = 10, nullable = false)
    private String nrc;

    @Column(name = "number", nullable = false)
    private Integer number;

    @Column(name = "semester", length = 6, nullable = false)
    private String semester;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "subject_code", nullable = false)
    private Subject subject;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "professor_id", nullable = false)
    private Employee professor;
}
