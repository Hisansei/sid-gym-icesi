package co.edu.icesi.sidgymicesi.model;

import lombok.*;
import java.time.LocalDate;
import java.util.*;

@Getter @Setter @NoArgsConstructor @AllArgsConstructor @Builder
public class ProgressLog {
    private String id;
    private String ownerUsername;
    private String routineId;
    private LocalDate date;
    @Builder.Default
    private List<ProgressEntry> entries = new ArrayList<>();
}
