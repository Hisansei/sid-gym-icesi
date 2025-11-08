package co.edu.icesi.sidgymicesi.model;

import lombok.*;

@Data @NoArgsConstructor @AllArgsConstructor @Builder
public class ProgressEntry {
    private String itemId;
    private String exerciseId;
    private Integer repsDone;
    private Integer timeSeconds;
    private Integer effortRpe;
    private String notes;
}
