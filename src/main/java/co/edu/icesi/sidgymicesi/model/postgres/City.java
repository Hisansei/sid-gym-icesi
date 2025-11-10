package co.edu.icesi.sidgymicesi.model.postgres;

import jakarta.persistence.*;
import lombok.*;

import java.util.LinkedHashSet;
import java.util.Set;

@Entity
@Table(name = "cities")
@Getter @Setter @NoArgsConstructor @AllArgsConstructor @Builder
@EqualsAndHashCode(of = "code")
public class City {

    @Id
    @Column(name = "code", nullable = false)
    private Integer code;

    @Column(name = "name", nullable = false, length = 20)
    private String name;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "dept_code", nullable = false)
    private Department department;

    @Builder.Default
    @OneToMany(mappedBy = "birthPlace", fetch = FetchType.LAZY)
    private Set<Employee> employeesBornHere = new LinkedHashSet<>();

    @Builder.Default
    @OneToMany(mappedBy = "birthPlace", fetch = FetchType.LAZY)
    private Set<Student> studentsBornHere = new LinkedHashSet<>();

    @Builder.Default
    @OneToMany(mappedBy = "city", fetch = FetchType.LAZY)
    private Set<Campus> campuses = new LinkedHashSet<>();
}
