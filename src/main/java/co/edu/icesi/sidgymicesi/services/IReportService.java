package co.edu.icesi.sidgymicesi.services;

import java.util.List;

public interface IReportService {
    List<Object> generateConsistencyReport(Long user);
    List<Object> getMonthlyInstructorStats(Long trainer);
}
