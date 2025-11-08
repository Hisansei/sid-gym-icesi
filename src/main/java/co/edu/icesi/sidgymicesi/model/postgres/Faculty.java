package co.edu.icesi.sidgymicesi.model.postgres;

import jakarta.persistence.*;
import lombok.*;

import java.awt.geom.Area;
import java.util.LinkedHashSet;
import java.util.Set;

@Entity
@Table(name = "faculties")
@Getter @Setter @NoArgsConstructor @AllArgsConstructor @Builder
@EqualsAndHashCode(of = "code")
public class Faculty {

    @Id
    @Column(name = "code", nullable = false)
    private Integer code;

    @Column(name = "name", nullable = false, length = 40)
    private String name;

    @Column(name = "location", nullable = false, length = 15)
    private String location;

    @Column(name = "phone_number", nullable = false, length = 15)
    private String phoneNumber;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "dean_id")
    private Employee dean;

    @Builder.Default
    @OneToMany(mappedBy = "faculty", fetch = FetchType.LAZY)
    private Set<Area> areas = new LinkedHashSet<>();
}
