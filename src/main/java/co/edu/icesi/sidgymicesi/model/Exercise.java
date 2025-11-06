package co.edu.icesi.sidgymicesi.model;

import java.util.List;

import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;

import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Document(collection = "exercises") // Colección sujeta a cambios!
public class Exercise {

    @Id
    private String id;
    
    private String name;
    
    private String type;
    
    private String description;
    
    private Integer durationSeconds;

    private String difficulty;
    
    private List<String> demoVideos;
}