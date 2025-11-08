package co.edu.icesi.sidgymicesi.model.postgres;

import jakarta.persistence.*;
import lombok.*;

@Entity
@Table(name = "employee_types")
@Getter @Setter @NoArgsConstructor @AllArgsConstructor @Builder
@EqualsAndHashCode(of = "name")
public class EmployeeType {

    @Id
    @Column(name = "name", length = 30, nullable = false)
    private String name;
}
