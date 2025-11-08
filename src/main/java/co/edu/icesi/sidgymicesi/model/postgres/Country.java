package co.edu.icesi.sidgymicesi.model.postgres;

import jakarta.persistence.*;
import lombok.*;

import java.util.LinkedHashSet;
import java.util.Set;

@Entity
@Table(name = "countries")
@Getter @Setter @NoArgsConstructor @AllArgsConstructor @Builder
@EqualsAndHashCode(of = "code")
public class Country {

    @Id
    @Column(name = "code", nullable = false)
    private Integer code;

    @Column(name = "name", nullable = false, length = 20)
    private String name;

    @Builder.Default
    @OneToMany(mappedBy = "country", fetch = FetchType.LAZY)
    private Set<Department> departments = new LinkedHashSet<>();
}
