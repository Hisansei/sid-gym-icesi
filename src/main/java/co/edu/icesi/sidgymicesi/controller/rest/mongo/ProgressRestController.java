package co.edu.icesi.sidgymicesi.controller.rest.mongo;

import co.edu.icesi.sidgymicesi.model.mongo.ProgressLog;
import co.edu.icesi.sidgymicesi.services.mongo.IProgressLogService;
import co.edu.icesi.sidgymicesi.services.mongo.IRoutineService;
import lombok.RequiredArgsConstructor;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/progress")
@RequiredArgsConstructor
public class ProgressRestController {

    private final IProgressLogService progressService;
    private final IRoutineService routineService;

    // Crear (o idempotente si ya existe para routineId+date del mismo owner)
    @PostMapping("/{routineId}")
    @PreAuthorize("@authz.isOwnerOfRoutine(#routineId, authentication)")
    public ProgressLog create(
            @PathVariable String routineId,
            @RequestBody ProgressLog body,
            Authentication auth
    ) {
        // Forzamos ownership y rutina (sin DTOs)
        body.setOwnerUsername(auth.getName());
        body.setRoutineId(routineId);
        return progressService.addLog(body);
    }

    // Mi histórico por rango
    @GetMapping("/my")
    public List<ProgressLog> myHistory(
            Authentication auth,
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate from,
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate to
    ) {
        return progressService.listByOwnerBetween(auth.getName(), from, to);
    }

    // Resumen (conteo + RPE promedio semanal) sin DTO (Map simple)
    @GetMapping("/my/summary")
    public Map<String, Object> mySummary(
            Authentication auth,
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate from,
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate to
    ) {
        return progressService.summarizeOwnerBetween(auth.getName(), from, to);
    }

    // Histórico por rutina y rango (protegido por ownership de la rutina)
    @GetMapping("/routine/{routineId}")
    @PreAuthorize("@authz.isOwnerOfRoutine(#routineId, authentication)")
    public List<ProgressLog> byRoutine(
            @PathVariable String routineId,
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate from,
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate to
    ) {
        return progressService.listByRoutineBetween(routineId, from, to);
    }

    // Borrar (propietario del log)
    @DeleteMapping("/{logId}")
    @PreAuthorize("@authz.isOwnerOfProgressLog(#logId, authentication)")
    public void delete(@PathVariable String logId) {
        progressService.deleteLog(logId);
    }
}
