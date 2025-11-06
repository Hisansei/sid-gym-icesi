package co.edu.icesi.sidgymicesi.model;

import lombok.*;
import java.time.LocalDateTime;
import java.util.*;

@Getter @Setter @NoArgsConstructor @AllArgsConstructor @Builder
public class Routine {
    private String id;
    private String ownerUsername;
    private String name;
    private LocalDateTime createdAt;
    private String sourceTemplateId;
    private String status;
    @Builder.Default
    private List<RoutineItem> items = new ArrayList<>();
}
