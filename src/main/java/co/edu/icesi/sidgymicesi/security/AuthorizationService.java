package co.edu.icesi.sidgymicesi.security;

import co.edu.icesi.sidgymicesi.model.mongo.Routine;
import co.edu.icesi.sidgymicesi.services.mongo.IProgressLogService;
import co.edu.icesi.sidgymicesi.services.mongo.IRoutineService;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.stereotype.Component;

@Component("authz")
public class AuthorizationService {

    private final IRoutineService routineService;
    private final IProgressLogService progressLogService;

    public AuthorizationService(IRoutineService routineService, IProgressLogService progressLogService) {
        this.routineService = routineService;
        this.progressLogService = progressLogService;
    }

    public boolean isOwnerOfRoutine(String routineId, Authentication authentication) {
        if (routineId == null || authentication == null) return false;

        // Admin siempre permitido
        boolean isAdmin = authentication.getAuthorities().stream()
                .map(GrantedAuthority::getAuthority)
                .anyMatch(a -> a.equals("ROLE_ADMIN"));

        if (isAdmin) return true;

        return routineService.findById(routineId)
                .map(Routine::getOwnerUsername)
                .map(owner -> owner.equals(authentication.getName()))
                .orElse(false);
    }

    public boolean isOwnerOfProgressLog(String logId, Authentication authentication) {
        String username = authentication.getName();
        return progressLogService.findById(logId)
                .map(l -> l.getOwnerUsername().equalsIgnoreCase(username))
                .orElse(false);
    }

}
