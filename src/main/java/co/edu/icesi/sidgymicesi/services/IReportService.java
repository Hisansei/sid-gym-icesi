package co.edu.icesi.sidgymicesi.services;

import java.util.Map;
import java.util.List;

public interface IReportService {
    List<Map<String, Object>> generateConsistencyReport(String username, int numberOfWeeks);
    Map<String, Long> generateExerciseTypeReport(String username);
}