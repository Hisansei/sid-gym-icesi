package co.edu.icesi.sidgymicesi.model.postgres;

import jakarta.persistence.*;
import lombok.*;

import javax.security.auth.Subject;
import java.util.LinkedHashSet;
import java.util.Set;

@Entity
@Table(name = "programs")
@Getter @Setter @NoArgsConstructor @AllArgsConstructor @Builder
@EqualsAndHashCode(of = "code")
public class Program {

    @Id
    @Column(name = "code", nullable = false)
    private Integer code;

    @Column(name = "name", nullable = false, length = 40)
    private String name;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "area_code", nullable = false)
    private Area area;

    @Builder.Default
    @OneToMany(mappedBy = "program", fetch = FetchType.LAZY)
    private Set<Subject> subjects = new LinkedHashSet<>();
}
