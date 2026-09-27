package zw.ac.uz.dpdms.auth.security;

import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import org.springframework.stereotype.Service;
import zw.ac.uz.dpdms.auth.domain.User;

import java.util.Date;
import java.util.Map;

@Service
public class JwtService {

    private final long TTL_MS = 60L * 60 * 1000;

    public String issue(User u) {
        return Jwts.builder()
            .subject(u.getUsername())
            .claims(Map.of(
                "role", u.getRole().name(),
                "hazard", u.getRole().hazard() == null ? "" : u.getRole().hazard(),
                "ward", u.getWard() == null ? "" : u.getWard()))
            .issuedAt(new Date())
            .expiration(new Date(System.currentTimeMillis() + TTL_MS))
            .signWith(DevKeyPair.privateKey(), SignatureAlgorithm.RS256)
            .compact();
    }
}