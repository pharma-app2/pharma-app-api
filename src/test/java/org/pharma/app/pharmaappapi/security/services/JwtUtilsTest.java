package org.pharma.app.pharmaappapi.security.services;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.InvalidClaimException;
import io.jsonwebtoken.Jwts;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.MockedStatic;
import org.mockito.Spy;
import org.mockito.junit.jupiter.MockitoExtension;
import org.pharma.app.pharmaappapi.security.DTOs.JwtPayloadPatientDTO;
import org.pharma.app.pharmaappapi.security.jwt.JwtUtils;
import org.pharma.app.pharmaappapi.security.models.RoleName;
import org.springframework.http.ResponseCookie;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.web.util.WebUtils;

import javax.crypto.SecretKey;
import java.lang.reflect.Field;
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

    @Spy // instead of using a mocked value (@Mock), use a real object but the capacity of mocking some of its methods
    // Usamos @Spy em JwtUtils porque queremos executar a lógica real de generateJwtCookieFromUserDetails, mas queremos
    // interceptar e controlar a chamada para buildJwt dentro da mesma classe. Um @Mock substituiria todas as
    // implementações de metodo.
    private JwtUtils jwtUtils;

    @Mock
    private HttpServletRequest mockRequest;

    // TODO: move to secure place
    private String jwtCookieName = "jwt-cookie";
    private Long expTime = 5 * 60 * 60 * 1000L;
    private SecretKey testKey;;
    private String testIssuer = "test-issuer";
    private String testAudience = "test-audience";
    private String testKeyId = "test-key-id-123";
    private long testExpTimeMs = 5 * 3600 * 1000;
    private String testSubject = "user@test.com";

    @BeforeEach
    void setUp() {
        // Injeta os valores de configuração no nosso objeto espião antes de cada teste.
        // Isso é feito usando reflexão para este exemplo, mas em um app real seria com @Value.
        try {
            Field cookieNameField = JwtUtils.class.getDeclaredField("jwtCookieName");
            cookieNameField.setAccessible(true);
            cookieNameField.set(jwtUtils, jwtCookieName);

            Field expTimeField = JwtUtils.class.getDeclaredField("expTime");
            expTimeField.setAccessible(true);
            expTimeField.set(jwtUtils, expTime);

            // Gera uma chave segura para cada teste
            testKey = Jwts.SIG.HS256.key().build();

            // Injeta os valores de teste no nosso objeto espião.
            // Em um app real, isso seria feito com @Value e um construtor.
            // Aqui usamos reflexão para simular essa injeção para o teste.
            setField(jwtUtils, "key", testKey);
            setField(jwtUtils, "issuer", testIssuer);
            setField(jwtUtils, "aud", testAudience);
            setField(jwtUtils, "keyId", testKeyId);
            setField(jwtUtils, "expTime", testExpTimeMs);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    // Método auxiliar para injetar valores nos campos privados para o teste
    private void setField(Object target, String fieldName, Object value) throws Exception {
        java.lang.reflect.Field field = target.getClass().getDeclaredField(fieldName);
        field.setAccessible(true);
        field.set(target, value);
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