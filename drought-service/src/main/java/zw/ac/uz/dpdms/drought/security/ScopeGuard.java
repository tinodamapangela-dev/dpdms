package zw.ac.uz.dpdms.drought.security;

import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.stereotype.Component;
import zw.ac.uz.dpdms.drought.common.ApiException;

@Component
public class ScopeGuard {

    public Jwt jwt() {
        Object p = SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        if (p instanceof Jwt j) return j;
        throw new ApiException(401, "Not authenticated");
    }

    public String username() { return jwt().getSubject(); }
    public String role()     { return jwt().getClaimAsString("role"); }
    public String ward()     { return jwt().getClaimAsString("ward"); }

    public void requireNotNationalForWrite() {
        if ("NATIONAL_USER".equals(role())) throw new ApiException(403, "National users have read-only access");
    }

    public void requireRecorderOwnsWard(String targetWard) {
        if (!"DROUGHT_RECORDER".equals(role()))
            throw new ApiException(403, "Only DROUGHT_RECORDER may create/update drought incidents");
        if (targetWard == null || !targetWard.equals(ward()))
            throw new ApiException(403, "You can only operate on incidents in " + ward());
    }

    public void requireCanReadWard(String targetWard) {
        switch (role()) {
            case "DROUGHT_RECORDER" -> {
                if (!targetWard.equals(ward())) throw new ApiException(403, "Outside your ward");
            }
            case "DROUGHT_SUPERVISOR", "PROVINCIAL_ADMIN", "NATIONAL_USER" -> { }
            default -> throw new ApiException(403, "You cannot access drought incidents");
        }
    }

    public void requireHazardRead() {
        String r = role();
        if (!(r.equals("DROUGHT_RECORDER") || r.equals("DROUGHT_SUPERVISOR")
           || r.equals("PROVINCIAL_ADMIN") || r.equals("NATIONAL_USER")))
            throw new ApiException(403, "You are not authorized to access this hazard");
    }

    public void requireSupervisor() {
        if (!"DROUGHT_SUPERVISOR".equals(role()))
            throw new ApiException(403, "Only DROUGHT_SUPERVISOR may perform approval actions");
    }
}