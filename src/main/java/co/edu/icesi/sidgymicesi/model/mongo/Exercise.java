package co.edu.icesi.sidgymicesi.model.mongo;

import lombok.*;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.index.Indexed;
import org.springframework.data.mongodb.core.mapping.Document;
import org.springframework.data.mongodb.core.mapping.Field;

import java.util.List;

@Data @NoArgsConstructor @AllArgsConstructor @Builder
@Document("exercises")
public class Exercise {
    @Id
    private String id;

    @Indexed
    private String name;

    private String type;

    private String description;

    @Field("duration_seconds")
    private Integer durationSeconds;

    private String difficulty;

    private boolean status;

    @Field("demo_videos")
    private List<String> demoVideos;
}
