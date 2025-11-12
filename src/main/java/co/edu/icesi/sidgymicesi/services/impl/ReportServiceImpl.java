package co.edu.icesi.sidgymicesi.services.impl;

import co.edu.icesi.sidgymicesi.model.mongo.ProgressLog;
import co.edu.icesi.sidgymicesi.model.mongo.Routine;
import co.edu.icesi.sidgymicesi.services.IReportService;
import co.edu.icesi.sidgymicesi.services.mongo.IProgressLogService;
import co.edu.icesi.sidgymicesi.services.mongo.IRoutineService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.time.temporal.WeekFields;
import java.util.*;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class ReportServiceImpl implements IReportService {

        private final IRoutineService routineService;
        private final IProgressLogService progressService;

        @Override
        public List<Map<String, Object>> generateConsistencyReport(String username, int numberOfWeeks) {
                LocalDate endDate = LocalDate.now();
                LocalDate startDate = endDate.minusWeeks(numberOfWeeks);

                List<ProgressLog> allLogs = progressService.listByOwner(username);

                WeekFields weekFields = WeekFields.of(Locale.getDefault());
                Map<LocalDate, Long> weeklyCounts = allLogs.stream()
                        .filter(log -> log.getDate() != null && !log.getDate().isBefore(startDate))
                        .collect(Collectors.groupingBy(
                                log -> log.getDate().with(weekFields.dayOfWeek(), 1),
                                Collectors.counting()
                        ));

                return weeklyCounts.entrySet().stream()
                        .sorted(Map.Entry.<LocalDate, Long>comparingByKey().reversed())
                        .map(e -> {
                                int sessionsCompleted = e.getValue().intValue();
                                double consistencyRate = (sessionsCompleted / 7.0) * 100.0;

                                Map<String, Object> weekReport = new HashMap<>();
                                weekReport.put("weekStartDate", e.getKey());
                                weekReport.put("sessionsCompleted", sessionsCompleted);
                                weekReport.put("consistencyRate", consistencyRate);
                                return weekReport;
                        })
                        .collect(Collectors.toList());
        }

        @Override
        public Map<String, Long> generateExerciseTypeReport(String username) {
                List<Routine> userRoutines = routineService.listByOwner(username);
                if (userRoutines.isEmpty()) {
                        return Collections.emptyMap();
                }

                Map<String, String> exerciseTypes = userRoutines.stream()
                        .filter(r -> r.getExercises() != null)
                        .flatMap(r -> r.getExercises().stream())
                        .collect(Collectors.toMap(
                                Routine.RoutineExercise::getExerciseId,
                                Routine.RoutineExercise::getType,
                                (a, b) -> a
                        ));

                List<ProgressLog> allLogs = progressService.listByOwner(username);

                return allLogs.stream()
                        .filter(l -> l.getEntries() != null)
                        .flatMap(l -> l.getEntries().stream())
                        .map(entry -> exerciseTypes.get(entry.getExerciseId()))
                        .filter(Objects::nonNull)
                        .collect(Collectors.groupingBy(t -> t, Collectors.counting()));
        }
}