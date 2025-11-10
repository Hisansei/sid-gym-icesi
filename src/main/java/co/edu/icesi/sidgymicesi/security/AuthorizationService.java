package co.edu.icesi.sidgymicesi.security;

import co.edu.icesi.sidgymicesi.model.mongo.Routine;
import co.edu.icesi.sidgymicesi.services.mongo.IRoutineService;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Component;

@Component("authz")
public class AuthorizationService {

    private final IRoutineService routineService;

    public AuthorizationService(IRoutineService routineService) {
        this.routineService = routineService;
    }

    public boolean isOwnerOfRoutine(String routineId, Authentication authentication) {
        if (routineId == null || authentication == null || authentication.getName() == null) {
            return false;
        }
        return routineService.findById(routineId)
                .map(Routine::getOwnerUsername)
                .map(owner -> owner != null && owner.equals(authentication.getName()))
                .orElse(false);
    }
}
