package co.edu.icesi.sidgymicesi.report;

import java.time.LocalDate;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data @NoArgsConstructor @AllArgsConstructor @Builder
public class WeeklyConsistencyStat {
    private String periodLabel;
    private LocalDate weekStartDate;  // Para ordenar la serie
    
    private int sessionsCompleted;    // Conteo de ProgressLog en esa semana
    private double consistencyRate;   // % de consistencia (si tiene meta)
}
