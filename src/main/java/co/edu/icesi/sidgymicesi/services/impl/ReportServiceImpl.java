package co.edu.icesi.sidgymicesi.services.impl;

import java.time.LocalDate;
import java.time.temporal.WeekFields;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.stream.Collectors;

import lombok.RequiredArgsConstructor;

import org.springframework.stereotype.Service;

import co.edu.icesi.sidgymicesi.model.mongo.ProgressLog;
import co.edu.icesi.sidgymicesi.report.WeeklyConsistencyStat;
import co.edu.icesi.sidgymicesi.services.impl.mongo.ProgressLogServiceImpl;

@Service
@RequiredArgsConstructor
public class ReportServiceImpl {
    private ProgressLogServiceImpl progressService;

    public List<WeeklyConsistencyStat> generateConsistencyReport(String username) {
        
        // 1. OBTENER DATOS (Últimas 4 semanas)
        LocalDate endDate = LocalDate.now();
        LocalDate startDate = endDate.minusWeeks(4);
        
        List<ProgressLog> allLogs = progressService.listByOwner(username);
        
        // 2. FILTRAR Y AGRUPAR POR SEMANA
        // Usamos WeekFields.ISO para un estándar de semana (Lunes a Domingo)
        WeekFields weekFields = WeekFields.of(Locale.getDefault()); 

        Map<LocalDate, Long> weeklyCounts = allLogs.stream()
                .filter(log -> !log.getDate().isBefore(startDate))
                .collect(Collectors.groupingBy(
                        log -> log.getDate().with(weekFields.dayOfWeek(), 1), // Agrupa al inicio de la semana
                        Collectors.counting() // Cuenta los logs por semana
                ));
        
        // 3. MAPEAR A LA ESTRUCTURA DE REPORTE
        return weeklyCounts.entrySet().stream()
                .map(entry -> WeeklyConsistencyStat.builder()
                        .weekStartDate(entry.getKey())
                        .sessionsCompleted(entry.getValue().intValue())
                        .periodLabel("Semana " + entry.getKey().get(weekFields.weekOfWeekBasedYear()))
                        // Ejemplo simple: No tenemos la meta, asumimos una meta de 3
                        .consistencyRate(Math.min(1.0, entry.getValue().doubleValue() / 3.0) * 100) 
                        .build())
                .sorted(Comparator.comparing(WeeklyConsistencyStat::getWeekStartDate))
                .collect(Collectors.toList());
    }
}
