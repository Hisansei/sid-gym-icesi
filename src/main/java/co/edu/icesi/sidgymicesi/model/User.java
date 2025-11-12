package co.edu.icesi.sidgymicesi.model;

import java.time.LocalDateTime;

import co.edu.icesi.sidgymicesi.model.postgres.Employee;
import jakarta.persistence.*;
import lombok.*;

@Entity
@Table(name = "users")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class User {

    @Id
    @Column(name = "username", nullable = false, length = 100)
    private String username;

    @Column(name = "password_hash", nullable = false, length = 255)
    private String passwordHash;

    @Enumerated(EnumType.STRING)
    @Column(name = "role", nullable = false, length = 50)
    private Role role;

    @Column(name = "student_id", length = 15)
    private String studentId;

    @Column(name = "employee_id", length = 15)
    private String employeeId;

    @Column(name = "is_active", nullable = false, insertable = false)
    private boolean active;

    @Column(name = "created_at", nullable = false, insertable = true, updatable = false)
    private LocalDateTime createdAt;

    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "employee_id", referencedColumnName = "id", insertable = false, updatable = false)
    private Employee employee;

    @PrePersist
    void prePersist() {
        if (createdAt == null) {
            createdAt = LocalDateTime.now();
        }
    }

    public String getEmployeeType() {
        if (employee != null) {
            return employee.getEmployeeType().getName();
        }
        return null;
    }
}