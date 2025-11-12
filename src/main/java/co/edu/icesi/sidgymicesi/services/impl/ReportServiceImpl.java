package co.edu.icesi.sidgymicesi.services.impl;

import java.util.Map;
import java.util.List;
import java.util.Locale;
import java.util.HashMap;
import java.util.ArrayList;
import java.time.LocalDate;
import java.util.Collections;
import java.util.stream.Collectors;
import java.time.temporal.WeekFields;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import co.edu.icesi.sidgymicesi.model.mongo.ProgressLog;
import co.edu.icesi.sidgymicesi.model.mongo.Routine;
import co.edu.icesi.sidgymicesi.services.IReportService;
import co.edu.icesi.sidgymicesi.services.impl.mongo.ProgressLogServiceImpl;
import co.edu.icesi.sidgymicesi.services.impl.mongo.RoutineServiceImpl;

@Service
@RequiredArgsConstructor
public class ReportServiceImpl implements IReportService {

        private RoutineServiceImpl routineService;
        private ProgressLogServiceImpl progressService;

        @Override
        public List<Map<String, Object>> generateConsistencyReport(String username, int numberOfWeeks) {
        LocalDate endDate = LocalDate.now();
        LocalDate startDate = endDate.minusWeeks(numberOfWeeks);

        List<ProgressLog> allLogs = progressService.listByOwner(username);

        WeekFields weekFields = WeekFields.of(Locale.getDefault());
        Map<LocalDate, Long> weeklyCounts = allLogs.stream()
                .filter(log -> !log.getDate().isBefore(startDate))
                .collect(Collectors.groupingBy(
                        log -> log.getDate().with(weekFields.dayOfWeek(), 1),
                        Collectors.counting()
                ));

        List<Map<String, Object>> result = new ArrayList<>();

        weeklyCounts.forEach((weekStartDate, count) -> {
                int sessionsCompleted = count.intValue();
                double consistencyRate = (double) sessionsCompleted / 7 * 100;

                Map<String, Object> weekReport = new HashMap<>();
                weekReport.put("weekStartDate", weekStartDate);
                weekReport.put("sessionsCompleted", sessionsCompleted);
                weekReport.put("consistencyRate", consistencyRate);

                result.add(weekReport);
        });

        return result.stream()
                .sorted((entry1, entry2) -> ((LocalDate) entry2.get("weekStartDate")).compareTo((LocalDate) entry1.get("weekStartDate")))
                .collect(Collectors.toList());
        }

        @Override
        public Map<String, Long> generateExerciseTypeReport(String username) {

        if (routineService == null) {
            throw new NullPointerException("RoutineService no ha sido inyectado correctamente");
        }

        List<Routine> userRoutines = routineService.listByOwner(username);
        
        if (userRoutines.isEmpty()) {
                return Collections.emptyMap();
        }
        
        Map<String, String> exerciseTypes = userRoutines.stream()
                .flatMap(routine -> routine.getExercises().stream())
                .collect(Collectors.toMap(
                Routine.RoutineExercise::getExerciseId,
                Routine.RoutineExercise::getType
                ));

        List<ProgressLog> allLogs = progressService.listByOwner(username);

        return allLogs.stream()
                .flatMap(log -> log.getEntries().stream())
                .map(entry -> exerciseTypes.get(entry.getExerciseId()))
                .filter(type -> type != null)
                .collect(Collectors.groupingBy(
                type -> type,
                Collectors.counting()
                ));
        }

}