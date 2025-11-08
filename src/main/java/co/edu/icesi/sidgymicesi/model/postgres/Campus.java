package co.edu.icesi.sidgymicesi.model.postgres;

import jakarta.persistence.*;
import lombok.*;

@Entity
@Table(name = "campuses")
@Getter @Setter @NoArgsConstructor @AllArgsConstructor @Builder
@EqualsAndHashCode(of = "code")
public class Campus {

    @Id
    @Column(name = "code", nullable = false)
    private Integer code;

    @Column(name = "name", length = 20)
    private String name;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "city_code", nullable = false)
    private City city;
}
