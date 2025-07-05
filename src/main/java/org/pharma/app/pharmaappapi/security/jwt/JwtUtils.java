package org.pharma.app.pharmaappapi.security.jwt;

import io.jsonwebtoken.*;
import io.jsonwebtoken.security.SignatureException;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import org.pharma.app.pharmaappapi.security.DTOs.JwtPayloadPatientDTO;
import org.pharma.app.pharmaappapi.security.models.RoleName;
import org.pharma.app.pharmaappapi.security.services.UserDetailsImpl;
import org.springframework.http.ResponseCookie;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.stereotype.Component;
import org.springframework.web.util.WebUtils;

import javax.crypto.SecretKey;
import java.util.Date;

@Component
public class JwtUtils {
    // TODO: change to a secure place for production
    private static SecretKey key = Jwts.SIG.HS256.key().build();
    private static String keyId = "pharma-app-key-1";
    private static String issuer = "pharma-app-api";
    private static String aud = "pharma-app-aud";
    private static String jwtCookieName = "jwt-cookie";
    private static Long expTime = 5 * 60 * 60 * 1000L;

    public Claims validateAndParseClaims(String token) {
        try {
            JwtParser parser = Jwts.parser()
                    .verifyWith(key)
                    .requireIssuer(issuer)
                    .requireAudience(aud)
                    .build();

            return parser.parseSignedClaims(token).getPayload();

        } catch (ExpiredJwtException e) {
            throw new RuntimeException(String.format("JWT Token has expired: %s", e.getMessage()), e);

        } catch (UnsupportedJwtException e) {
            throw new RuntimeException(String.format("JWT Token is unsupported: %s", e.getMessage()), e);

        } catch (MalformedJwtException e) {
            throw new RuntimeException(String.format("Invalid JWT Token: %s", e.getMessage()), e);

        } catch (SignatureException e) {
            throw new RuntimeException(String.format("Invalid JWT signature: %s", e.getMessage()), e);

        } catch (IllegalArgumentException e) {
            throw new RuntimeException(String.format("JWT claims string is empty: %s", e.getMessage()), e);

        } catch (InvalidClaimException e) {
            throw new RuntimeException(String.format("Invalid JWT claim: %s", e.getMessage()), e);
        }
    }

    public String buildJwt(JwtPayloadPatientDTO payload) {
        long nowMillis = System.currentTimeMillis();
        Date now = new Date(nowMillis);
        // TODO: change exp to a secure place
        long expMillis = nowMillis + expTime;
        Date expirationDate = new Date(expMillis);

        return Jwts.builder()
                .header()
                .keyId(keyId)
                .and()
                .subject(payload.email())
                .issuer(issuer) // (Opcional) Quem emitiu o token
                .issuedAt(now) // Data de emissão
                .audience().add(aud)
                .and()
                .expiration(expirationDate)

                .claim("id", payload.id())
                .claim("role", payload.role())

                .signWith(key)

                .compact();
    }

    public ResponseCookie generateJwtCookieFromUserDetails(UserDetailsImpl userDetails) {
        GrantedAuthority authority = userDetails
                .getAuthorities().stream()
                .findFirst()
                .orElseThrow(() -> new RuntimeException("No role provided"));

        String roleStr = authority.getAuthority();
        RoleName role = roleStr.equals("ROLE_ADMIN")
                ? RoleName.ROLE_ADMIN
                : roleStr.equals("ROLE_PHARMACIST") ? RoleName.ROLE_PHARMACIST
                : RoleName.ROLE_PATIENT;

        JwtPayloadPatientDTO payload = new JwtPayloadPatientDTO(userDetails.getId(), userDetails.getUsername(), role);
        String jwtToken = buildJwt(payload);

        return ResponseCookie.from(jwtCookieName, jwtToken)
                .path("/api")
                .maxAge(expTime)
                .httpOnly(false) // allow js access
                .build();
    }

    public String getJwtFromCookies(HttpServletRequest request) {
        Cookie cookie = WebUtils.getCookie(request, jwtCookieName);

        return cookie == null ? null : cookie.getValue();
    }
}
