package co.edu.icesi.sidgymicesi.model;

import lombok.*;

@Getter @Setter @NoArgsConstructor @AllArgsConstructor @Builder
public class RoutineItem {
    private String id;
    private Integer orderIndex;

    //
    private String exerciseId;
    private String customName;

    private String type;
    private String description;
    private Integer targetReps;
    private Integer targetTimeSeconds;
    private Integer targetIntensity;
}
