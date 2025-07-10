package org.pharma.app.pharmaappapi.security.services;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.InvalidClaimException;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.io.Decoders;
import io.jsonwebtoken.security.Keys;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.MockedStatic;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;
import org.pharma.app.pharmaappapi.security.DTOs.users.JwtPayloadPatientDTO;
import org.pharma.app.pharmaappapi.security.jwt.JwtUtils;
import org.pharma.app.pharmaappapi.security.models.users.RoleName;
import org.springframework.http.ResponseCookie;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.web.util.WebUtils;

import javax.crypto.SecretKey;
import java.time.temporal.ChronoUnit;
import java.util.Collections;
import java.util.Date;
import java.util.List;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.within;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.doReturn;
import static org.mockito.Mockito.mockStatic;

@ExtendWith(MockitoExtension.class)
class JwtUtilsTest {
    private JwtUtils jwtUtils;

    @Mock
    private HttpServletRequest mockRequest;

    private final String jwtCookieName = "jwt-cookie";
    private final Long expTime = 5 * 60 * 60 * 1000L;
    private SecretKey testKey;
    private final String testIssuer = "test-issuer";
    private final String testAudience = "test-audience";
    private final String testKeyId = "test-key-id-123";
    private final long testExpTimeMs = 5 * 3600 * 1000;
    private final String testSubject = "user@test.com";

    @BeforeEach
    void setUp() {
        // 1. A NOSSA "FONTE DA VERDADE" EM FORMATO STRING (BASE64)
        // Esta é a string que simula o que está no seu application.properties
        String validBase64Secret = "B3k/v+g8L2pW8yZ4xT6qR7sU9vA+cE1dF0hG2iJ3kL4="; // Exemplo!

        // 2. CRIAMOS O OBJETO SecretKey A PARTIR DA STRING
        // Este objeto 'testKey' será usado para assinar nossos tokens de teste.
        byte[] keyBytes = Decoders.BASE64.decode(validBase64Secret);
        this.testKey = Keys.hmacShaKeyFor(keyBytes);

        // 2. Crie uma instância REAL da sua classe usando o construtor
        // que recebe os valores de configuração. Isso simula o que o Spring faz.
        JwtUtils realJwtUtils = new JwtUtils(
                testKeyId,
                testIssuer,
                testAudience,
                jwtCookieName,
                expTime,
                validBase64Secret
        );

        // 3. Crie o SPY a partir do objeto real que você acabou de criar.
        this.jwtUtils = Mockito.spy(realJwtUtils);
    }

    // ********** METHOD: validateAndParseClaims **********

    @Test
    void validateAndParseClaimsSuccess() {
        String validToken = Jwts.builder()
                .subject(testSubject)
                .issuer(testIssuer)
                .audience().add(testAudience).and()
                .expiration(new Date(System.currentTimeMillis() + 60000)) // Expira em 1 minuto
                .signWith(testKey)
                .compact();

        Claims claims = jwtUtils.validateAndParseClaims(validToken);

        assertThat(claims).isNotNull();
        assertThat(claims.getSubject()).isEqualTo(testSubject);
        assertThat(claims.getIssuer()).isEqualTo(testIssuer);
    }

    @Test
    void validateAndParseClaimsExpiredTokenFail() {
        // Arrange (Arrumar)
        // Gera um token que JÁ EXPIROU (data de expiração no passado)
        String expiredToken = Jwts.builder()
                .subject(testSubject)
                .issuer(testIssuer)
                .audience().add(testAudience).and()
                .expiration(new Date(System.currentTimeMillis() - 10000)) // Expirou há 10 segundos
                .signWith(testKey)
                .compact();

        // Act & Assert (Agir e Verificar)
        RuntimeException exception = assertThrows(RuntimeException.class, () -> {
            jwtUtils.validateAndParseClaims(expiredToken);
        });

        assertThat(exception.getMessage()).contains("JWT Token has expired");
    }

    @Test
    void validateAndParseClaimsInvalidSignatureFail() {
        // Arrange (Arrumar)
        // Gera um token com uma chave...
        SecretKey originalKey = Jwts.SIG.HS256.key().build();
        String token = Jwts.builder().subject(testSubject).issuer(testIssuer).audience().add(testAudience).and().signWith(originalKey).compact();

        // ...mas o nosso validador está configurado com uma chave DIFERENTE (a testKey)

        // Act & Assert (Agir e Verificar)
        RuntimeException exception = assertThrows(RuntimeException.class, () -> {
            jwtUtils.validateAndParseClaims(token);
        });

        assertThat(exception.getMessage()).contains("Invalid JWT signature");
    }

    @Test
    void validateAndParseClaimsIncorrectIssuerFail() {
        // Arrange (Arrumar)
        // Gera um token com um 'issuer' diferente do esperado
        String tokenWithWrongIssuer = Jwts.builder()
                .subject(testSubject)
                .issuer("wrong-issuer") // Issuer incorreto
                .audience().add(testAudience).and()
                .signWith(testKey)
                .compact();

        // Act & Assert (Agir e Verificar)
        // A biblioteca jjwt lança InvalidClaimException para claims erradas como issuer e audience.
        // O seu método a encapsula em uma RuntimeException, mas a causa raiz é o importante.
        Exception exception = assertThrows(Exception.class, () -> {
            jwtUtils.validateAndParseClaims(tokenWithWrongIssuer);
        });

        // Verificamos a causa raiz para sermos mais específicos
        assertThat(exception.getCause()).isInstanceOf(InvalidClaimException.class);
    }

    @Test
    void validateAndParseClaimsMalformedTokenFail() {
        // Arrange (Arrumar)
        String malformedToken = "this.is.not.a.valid.jwt";

        // Act & Assert (Agir e Verificar)
        RuntimeException exception = assertThrows(RuntimeException.class, () -> {
            jwtUtils.validateAndParseClaims(malformedToken);
        });

        assertThat(exception.getMessage()).contains("Invalid JWT Token");
    }

    // ********** METHOD: buildJwt **********

    @Test
    void buildJwtSuccess() {
        JwtPayloadPatientDTO payload = new JwtPayloadPatientDTO(
                UUID.randomUUID(),
                "john@doe.com",
                RoleName.ROLE_PATIENT
        );

        String generatedToken = jwtUtils.buildJwt(payload);

        assertThat(generatedToken).isNotNull();

        Claims claims = Jwts.parser()
                .verifyWith(testKey)
                .build()
                .parseSignedClaims(generatedToken)
                .getPayload();

        assertThat(claims.getSubject()).isEqualTo(payload.email());
        assertThat(claims.getIssuer()).isEqualTo(testIssuer);
        assertThat(claims.getAudience()).contains(testAudience);

        // Verifica os claims customizados
        assertThat(claims.get("id", String.class)).isEqualTo(payload.id().toString());
        assertThat(claims.get("role", String.class)).isEqualTo(payload.role().name());

        Date expectedExpiration = new Date(System.currentTimeMillis() + testExpTimeMs);
        assertThat(claims.getExpiration().toInstant())
                .isCloseTo(
                        expectedExpiration.toInstant(),
                        within(2, ChronoUnit.SECONDS)
                );
    }

//    // ********** METHOD: generateJwtCookieFromUserDetails **********

    @Test
    void generateJwtCookieFromUserDetailsSuccess() {
        SimpleGrantedAuthority authority = new SimpleGrantedAuthority(RoleName.ROLE_PATIENT.name());
        UserDetailsImpl userDetails = new UserDetailsImpl(
                UUID.randomUUID(),
                "john@doe.com",
                "password",
                List.of(authority)
        );

        String fakeJwt = "fake.jwt.token";

        // 3. Instrua o nosso "espiao": Quando o metodo buildJwt for chamado com QUALQUER payload,
        // nao execute o metodo real, apenas retorne o nosso token falso.
        // Usamos doReturn().when() que é a sintaxe segura para espiões (spies).
        doReturn(fakeJwt).when(jwtUtils).buildJwt(any(JwtPayloadPatientDTO.class));

        ResponseCookie resultCookie = jwtUtils.generateJwtCookieFromUserDetails(userDetails);

        assertThat(resultCookie).isNotNull();
        assertThat(resultCookie.getName()).isEqualTo(jwtCookieName);
        assertThat(resultCookie.getValue()).isEqualTo(fakeJwt);
        assertThat(resultCookie.getPath()).isEqualTo("/api");
        assertThat(resultCookie.getMaxAge().getSeconds()).isEqualTo(expTime);
        assertThat(resultCookie.isHttpOnly()).isFalse();
    }

    @Test
    void generateJwtCookieFromUserDetailsFail() {
        UserDetailsImpl userDetailsWithoutRoles = new UserDetailsImpl(
                UUID.randomUUID(),
                "norole@test.com",
                "password",
                Collections.emptyList() // Lista de autoridades vazia
        );

        RuntimeException exception = assertThrows(RuntimeException.class, () -> {
            jwtUtils.generateJwtCookieFromUserDetails(userDetailsWithoutRoles);
        });

        assertThat(exception.getMessage()).isEqualTo("No role provided");
    }

    // ********** METHOD: getJwtFromCookies **********

    @Test
    void getJwtFromCookiesSuccess() {
        String expectedToken = "eyJhbGciOiJIUzI1NiJ9.eyJzdWIiOiJ...";
        Cookie jwtCookie = new Cookie(jwtCookieName, expectedToken);

        try (MockedStatic<WebUtils> webUtilsMockedStatic = mockStatic(WebUtils.class)) {
            webUtilsMockedStatic.when(() -> WebUtils.getCookie(mockRequest, jwtCookieName))
                    .thenReturn(jwtCookie);

            String actualToken = jwtUtils.getJwtFromCookies(mockRequest);

            assertThat(actualToken).isNotNull();
            assertThat(actualToken).isEqualTo(expectedToken);
        }
    }

    @Test
    void getJwtFromCookiesFail() {
        try (MockedStatic<WebUtils> webUtilsMockedStatic = mockStatic(WebUtils.class)) {
            webUtilsMockedStatic.when(() -> WebUtils.getCookie(mockRequest, jwtCookieName))
                    .thenReturn(null);

            String actualToken = jwtUtils.getJwtFromCookies(mockRequest);

            assertThat(actualToken).isNull();
        }
    }
}