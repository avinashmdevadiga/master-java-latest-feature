# Spring Security — Complete Guide (Basics to Advanced)
### For Experienced Java Developers Preparing for Interviews

---

## Table of Contents
1. [Introduction to Spring Security](#1-introduction-to-spring-security)
2. [Authentication vs Authorization](#2-authentication-vs-authorization)
3. [Security Filters and Configuration](#3-security-filters-and-configuration)
4. [Role-Based Access Control](#4-role-based-access-control)
5. [JWT Integration](#5-jwt-integration)
6. [OAuth2 and OpenID Connect](#6-oauth2-and-openid-connect)
7. [CSRF Protection](#7-csrf-protection)
8. [Real-World Example: Securing a REST API with JWT](#8-real-world-example-securing-a-rest-api-with-jwt)
9. [Interview Questions](#9-interview-questions)

---

## 1. Introduction to Spring Security

**What is Spring Security?**
Spring Security is a framework providing authentication, authorization, and protection against common web application security vulnerabilities (CSRF, session fixation, clickjacking), integrated deeply with the rest of the Spring ecosystem via a **filter chain** and AOP-based method security.

**Why not just write custom security logic?**
```java
// The naive, custom approach - looks simple but is genuinely dangerous:
public boolean login(String username, String password) {
    User user = userRepository.findByUsername(username);
    return user != null && user.getPassword().equals(password); // plaintext comparison?! Timing attack risk!
}
```
This kind of hand-rolled security code is a frequent source of real vulnerabilities: plaintext or weakly-hashed password storage, no protection against timing attacks, no CSRF protection, no session fixation protection, and no consistent authorization enforcement across endpoints. Spring Security provides battle-tested, widely-audited implementations of these concerns instead.

**High-level architecture:**
```
HTTP Request
     │
     ▼
┌─────────────────────────────────────────────────────┐
│              Security Filter Chain                    │
│  ┌────────────────┐  ┌──────────────────┐            │
│  │ Authentication   │  │  Authorization     │           │
│  │ Filters          │→ │  Filters           │           │
│  │ (verify identity)│  │  (check permissions)│          │
│  └────────────────┘  └──────────────────┘            │
└──────────────────────────┬──────────────────────────┘
                            │
                            ▼
                  SecurityContextHolder
              (holds the current Authentication
               for the duration of the request)
                            │
                            ▼
                    Your Controller/Service
                  (can check authorities via
                   @PreAuthorize, SecurityContext, etc.)
```

**Q&A**
- **Q: Why is Spring Security implemented as a chain of servlet filters rather than being built into the DispatcherServlet?**
  A: Filters run *before* the request reaches Spring MVC's `DispatcherServlet`, allowing security checks (authentication, CSRF validation, CORS) to happen at the earliest possible point in request processing — rejecting unauthorized or malicious requests before they consume any application-layer resources, and keeping security concerns cleanly separated from business/web-layer code.

---

## 2. Authentication vs Authorization

| Aspect | Authentication | Authorization |
|---|---|---|
| Question answered | "Who are you?" | "What are you allowed to do?" |
| When it happens | First — establishes identity | Second — happens after identity is known |
| Spring Security abstraction | `AuthenticationManager`, `Authentication` object | `AccessDecisionManager` / `AuthorizationManager`, `GrantedAuthority` |
| Failure result | `401 Unauthorized` | `403 Forbidden` |
| Example | Verifying a username/password or JWT signature | Checking if the authenticated user has the `ADMIN` role to access `/admin/**` |

```java
// Authentication - establishing WHO the caller is
Authentication auth = authenticationManager.authenticate(
    new UsernamePasswordAuthenticationToken(username, password));
SecurityContextHolder.getContext().setAuthentication(auth);

// Authorization - checking WHAT the authenticated caller can do
@PreAuthorize("hasRole('ADMIN')")
public void deleteUser(Long userId) { /* ... */ }
```

**A critical distinction interviewers probe:** A `401 Unauthorized` response actually means "you are not authenticated" (despite the confusing name) — the server doesn't know who you are, or your credentials are invalid. A `403 Forbidden` means "you ARE authenticated, but you don't have permission for this specific action" — the server knows exactly who you are and has decided you're not allowed.

**Real-world example:** In a banking application, authentication verifies a customer's login credentials (or JWT token) to establish "this is customer ID 12345." Authorization then determines whether customer 12345 is allowed to view *account 67890* specifically — even a fully authenticated, legitimate customer should get a `403` if they try to access someone else's account, which is an authorization failure, not an authentication one.

**Q&A**
- **Q: Can a user be authenticated but not authorized for anything?**
  A: Yes — a valid, authenticated user might have zero granted authorities/roles, in which case every authorization check fails (`403`) even though authentication succeeded (`200`-eligible identity). This is a normal, expected state, e.g., a newly registered user pending admin approval before being granted any functional roles.

---

## 3. Security Filters and Configuration

### 3.1 The Filter Chain

Spring Security's core mechanism is a chain of `Filter` implementations, each handling one concern, executed in a specific order:

| Filter (simplified, common ones) | Responsibility |
|---|---|
| `SecurityContextPersistenceFilter` / `SecurityContextHolderFilter` | Loads/saves the `SecurityContext` for the request |
| `CsrfFilter` | Validates CSRF tokens on state-changing requests |
| `UsernamePasswordAuthenticationFilter` | Handles form-login authentication |
| `BasicAuthenticationFilter` | Handles HTTP Basic authentication |
| Custom JWT filter (not built-in, commonly added) | Validates a JWT from the `Authorization` header |
| `ExceptionTranslationFilter` | Catches security exceptions, triggers entry points (redirect to login, or 401/403 response) |
| `FilterSecurityInterceptor` / `AuthorizationFilter` | Makes the final authorization decision for the requested resource |

### 3.2 Modern Configuration (Spring Security 6.x, Lambda DSL)

```java
@Configuration
@EnableWebSecurity
@EnableMethodSecurity // enables @PreAuthorize/@PostAuthorize/@Secured
public class SecurityConfig {

    @Bean
    public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
        http
            .csrf(csrf -> csrf.disable()) // typically disabled for stateless REST APIs using tokens (see Section 7)
            .sessionManagement(session -> session.sessionCreationPolicy(SessionCreationPolicy.STATELESS))
            .authorizeHttpRequests(auth -> auth
                .requestMatchers("/api/v1/auth/**", "/api/v1/public/**").permitAll()
                .requestMatchers("/api/v1/admin/**").hasRole("ADMIN")
                .requestMatchers(HttpMethod.GET, "/api/v1/products/**").permitAll()
                .requestMatchers(HttpMethod.POST, "/api/v1/products/**").hasAnyRole("ADMIN", "MANAGER")
                .anyRequest().authenticated()
            )
            .exceptionHandling(ex -> ex
                .authenticationEntryPoint((request, response, authException) ->
                    response.sendError(HttpServletResponse.SC_UNAUTHORIZED, "Unauthorized"))
                .accessDeniedHandler((request, response, accessDeniedException) ->
                    response.sendError(HttpServletResponse.SC_FORBIDDEN, "Forbidden"))
            )
            .addFilterBefore(jwtAuthFilter(), UsernamePasswordAuthenticationFilter.class);

        return http.build();
    }

    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder(); // NEVER use plaintext or weak hashing (MD5/SHA1) for passwords
    }

    @Bean
    public AuthenticationManager authenticationManager(AuthenticationConfiguration config) throws Exception {
        return config.getAuthenticationManager();
    }
}
```

**Key points about `authorizeHttpRequests` matching:**
- **Order matters** — rules are evaluated top to bottom; the first matching rule wins. More specific patterns must come before more general ones (`anyRequest()` should always be last).
- **`permitAll()`, `authenticated()`, `hasRole()`, `hasAuthority()`, `hasAnyRole()`** are the most common terminal matchers.

**Q&A**
- **Q: What happens if you accidentally place `anyRequest().authenticated()` before a more specific `permitAll()` rule?**
  A: The `anyRequest()` rule would match first and take effect — the later `permitAll()` rule would never be reached for any URL, effectively requiring authentication for every endpoint including ones you intended to be public. This exact ordering mistake is a common real-world Spring Security misconfiguration.

---

## 4. Role-Based Access Control

### 4.1 URL-Based Authorization

```java
.authorizeHttpRequests(auth -> auth
    .requestMatchers("/api/v1/admin/**").hasRole("ADMIN")          // requires ROLE_ADMIN authority
    .requestMatchers("/api/v1/reports/**").hasAnyRole("ADMIN", "AUDITOR")
    .requestMatchers("/api/v1/users/me").authenticated()            // any authenticated user
    .anyRequest().denyAll()                                          // deny by default - fail closed
)
```
**Important gotcha:** `hasRole("ADMIN")` implicitly expects the authority to be stored as `"ROLE_ADMIN"` — Spring Security automatically prepends `"ROLE_"`. Using `hasAuthority("ADMIN")` instead requires the exact string `"ADMIN"` with no prefix. Mixing these up is a very common source of "why is my authorization rule not working" bugs.

### 4.2 Method-Level Security

```java
@Service
public class AccountService {

    @PreAuthorize("hasRole('ADMIN')")
    public void deleteAccount(Long accountId) { /* ... */ }

    @PreAuthorize("hasRole('ADMIN') or #accountId == authentication.principal.id")
    public Account getAccount(Long accountId) {
        // ADMIN can view any account; a regular user can only view their OWN account
        return accountRepository.findById(accountId).orElseThrow();
    }

    @PostAuthorize("returnObject.ownerId == authentication.principal.id")
    public Account getAccountDetails(Long accountId) {
        // authorization decision made AFTER the method runs, based on the RETURNED object
        return accountRepository.findById(accountId).orElseThrow();
    }

    @PreFilter("filterObject.ownerId == authentication.principal.id")
    public void processAccounts(List<Account> accounts) {
        // filters the INPUT collection before the method body executes
    }
}
```

### 4.3 Custom `UserDetailsService` and Roles/Authorities Model

```java
@Entity
public class User {
    @Id @GeneratedValue
    private Long id;
    private String username;
    private String password; // BCrypt-hashed, never plaintext

    @ManyToMany(fetch = FetchType.EAGER)
    private Set<Role> roles;
}

@Entity
public class Role {
    @Id @GeneratedValue
    private Long id;
    private String name; // e.g., "ROLE_ADMIN", "ROLE_USER"
}

@Service
public class CustomUserDetailsService implements UserDetailsService {
    private final UserRepository userRepository;

    public CustomUserDetailsService(UserRepository userRepository) { this.userRepository = userRepository; }

    @Override
    public UserDetails loadUserByUsername(String username) {
        User user = userRepository.findByUsername(username)
                .orElseThrow(() -> new UsernameNotFoundException(username));

        Set<GrantedAuthority> authorities = user.getRoles().stream()
                .map(role -> new SimpleGrantedAuthority(role.getName()))
                .collect(Collectors.toSet());

        return new org.springframework.security.core.userdetails.User(
                user.getUsername(), user.getPassword(), authorities);
    }
}
```

**Real-world example:** In a banking application, `ROLE_CUSTOMER` can view/manage only their own accounts (`#accountId == authentication.principal.id`), `ROLE_TELLER` can view accounts within their branch, and `ROLE_ADMIN` can access any account for support/audit purposes — modeling this cleanly with method security expressions keeps authorization logic declarative and close to the business method it protects, rather than scattered as manual `if` checks inside service methods.

**Q&A**
- **Q: Difference between `hasRole()` and `hasAuthority()`?**
  A: `hasRole("X")` checks for the authority `"ROLE_X"` (Spring Security auto-prepends the `ROLE_` prefix). `hasAuthority("X")` checks for the exact authority string `"X"` with no prefix added. Functionally they can achieve the same result if your authorities are named consistently, but mixing conventions (some code checking `hasRole`, authorities stored without the `ROLE_` prefix) is a classic source of authorization bugs.
- **Q: What's the difference between `@PreAuthorize` and `@PostAuthorize`?**
  A: `@PreAuthorize` evaluates its expression **before** the method executes — if it fails, the method body never runs at all. `@PostAuthorize` evaluates **after** the method executes, with access to the method's return value (`returnObject`) — useful when the authorization decision genuinely depends on data only known after fetching it (e.g., "is the caller the owner of the record that was just retrieved").

---

## 5. JWT Integration

**JWT (JSON Web Token)** is a compact, self-contained token format commonly used for stateless authentication in REST APIs — the server doesn't need to store session state; the token itself carries the claims (user identity, roles, expiration) and is cryptographically signed.

**JWT structure:** `header.payload.signature` (Base64URL-encoded, dot-separated)

### 5.1 Generating a JWT

```java
@Component
public class JwtService {
    @Value("${jwt.secret}")
    private String secretKey; // in production, load from a secrets manager, never hardcode

    @Value("${jwt.expiration-ms}")
    private long expirationMs;

    public String generateToken(UserDetails userDetails) {
        Map<String, Object> claims = new HashMap<>();
        claims.put("roles", userDetails.getAuthorities().stream()
                .map(GrantedAuthority::getAuthority).toList());

        return Jwts.builder()
                .claims(claims)
                .subject(userDetails.getUsername())
                .issuedAt(new Date())
                .expiration(new Date(System.currentTimeMillis() + expirationMs))
                .signWith(getSigningKey())
                .compact();
    }

    public boolean isTokenValid(String token, UserDetails userDetails) {
        String username = extractUsername(token);
        return username.equals(userDetails.getUsername()) && !isTokenExpired(token);
    }

    public String extractUsername(String token) {
        return extractClaims(token).getSubject();
    }

    private boolean isTokenExpired(String token) {
        return extractClaims(token).getExpiration().before(new Date());
    }

    private Claims extractClaims(String token) {
        return Jwts.parser().verifyWith(getSigningKey()).build()
                .parseSignedClaims(token).getPayload();
    }

    private SecretKey getSigningKey() {
        return Keys.hmacShaKeyFor(Decoders.BASE64.decode(secretKey));
    }
}
```

### 5.2 Custom JWT Authentication Filter

```java
@Component
public class JwtAuthenticationFilter extends OncePerRequestFilter {

    private final JwtService jwtService;
    private final UserDetailsService userDetailsService;

    public JwtAuthenticationFilter(JwtService jwtService, UserDetailsService userDetailsService) {
        this.jwtService = jwtService;
        this.userDetailsService = userDetailsService;
    }

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response,
                                       FilterChain filterChain) throws ServletException, IOException {
        String authHeader = request.getHeader("Authorization");
        if (authHeader == null || !authHeader.startsWith("Bearer ")) {
            filterChain.doFilter(request, response);
            return;
        }

        String jwt = authHeader.substring(7);
        String username = jwtService.extractUsername(jwt);

        if (username != null && SecurityContextHolder.getContext().getAuthentication() == null) {
            UserDetails userDetails = userDetailsService.loadUserByUsername(username);
            if (jwtService.isTokenValid(jwt, userDetails)) {
                UsernamePasswordAuthenticationToken authToken =
                        new UsernamePasswordAuthenticationToken(userDetails, null, userDetails.getAuthorities());
                authToken.setDetails(new WebAuthenticationDetailsSource().buildDetails(request));
                SecurityContextHolder.getContext().setAuthentication(authToken);
            }
        }
        filterChain.doFilter(request, response);
    }
}
```

**Wiring the filter into the chain (before the built-in username/password filter):**
```java
http.addFilterBefore(jwtAuthenticationFilter, UsernamePasswordAuthenticationFilter.class);
```

**Why JWT pairs naturally with `SessionCreationPolicy.STATELESS`:** Since the token itself carries all necessary identity/claims information and is verified via signature on every request, there's no need for the server to maintain server-side session state — this is what enables horizontal scaling without sticky sessions or a shared session store.

**Q&A**
- **Q: What are the security risks of JWTs, and how do you mitigate them?**
  A: Once issued, a JWT **cannot be revoked** before its expiration (unlike a server-side session you can simply delete) — mitigated by using short expiration times combined with a refresh-token mechanism, or maintaining a server-side blocklist for genuinely compromised tokens (which reintroduces some statefulness). Also: never store sensitive data in the payload (it's Base64-encoded, not encrypted — anyone can decode and read it, though not forge it without the signing key), always validate the signature and expiration server-side, and use a strong, securely-stored signing key (or asymmetric keys, `RS256`, when multiple services need to verify tokens without all holding the same shared secret).

---

## 6. OAuth2 and OpenID Connect

### 6.1 Core Concepts

- **OAuth2** is an **authorization** framework — it lets a third-party application obtain limited access to a resource on a user's behalf, without ever handling the user's actual credentials.
- **OpenID Connect (OIDC)** is an **authentication** layer built on top of OAuth2 — it adds an `ID Token` (a JWT containing identity claims) to OAuth2's access-token-focused flow, standardizing "who is this user" on top of OAuth2's "what can this app access."

**Key roles in OAuth2:**
| Role | Description |
|---|---|
| Resource Owner | The user who owns the data/account |
| Client | The application requesting access (e.g., a mobile app, another microservice) |
| Authorization Server | Issues access tokens after authenticating the resource owner and obtaining consent |
| Resource Server | Hosts the protected resources, validates access tokens on incoming requests |

### 6.2 Authorization Code Flow (Most Common, Most Secure for Web Apps)

```
User → Client App → Authorization Server (login + consent) → redirect back with auth code
Client App → Authorization Server: exchange auth code + client secret for an access token
Client App → Resource Server: call API with access token in Authorization header
```

### 6.3 Spring Boot as an OAuth2 Client

```xml
<dependency>
    <groupId>org.springframework.boot</groupId>
    <artifactId>spring-boot-starter-oauth2-client</artifactId>
</dependency>
```

```yaml
spring:
  security:
    oauth2:
      client:
        registration:
          google:
            client-id: ${GOOGLE_CLIENT_ID}
            client-secret: ${GOOGLE_CLIENT_SECRET}
            scope: openid, profile, email
```

```java
@Configuration
@EnableWebSecurity
public class OAuth2SecurityConfig {
    @Bean
    public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
        http
            .authorizeHttpRequests(auth -> auth
                .requestMatchers("/", "/login/**").permitAll()
                .anyRequest().authenticated())
            .oauth2Login(Customizer.withDefaults()); // delegates login to the configured provider (Google, etc.)
        return http.build();
    }
}
```

### 6.4 Spring Boot as an OAuth2 Resource Server (Validating Tokens from an External Auth Server)

```xml
<dependency>
    <groupId>org.springframework.boot</groupId>
    <artifactId>spring-boot-starter-oauth2-resource-server</artifactId>
</dependency>
```

```yaml
spring:
  security:
    oauth2:
      resourceserver:
        jwt:
          issuer-uri: https://auth.company.com/realms/company # e.g., a Keycloak or Auth0 realm
```

```java
@Bean
public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
    http
        .authorizeHttpRequests(auth -> auth
            .requestMatchers("/api/v1/public/**").permitAll()
            .anyRequest().authenticated())
        .oauth2ResourceServer(oauth2 -> oauth2.jwt(Customizer.withDefaults()));
    return http.build();
}
```
With this configuration, Spring Security automatically fetches the authorization server's public keys (via its JWKS endpoint, discovered from `issuer-uri`), validates incoming JWT signatures, and populates the `SecurityContext` — no custom JWT filter needed, unlike the self-issued JWT setup in Section 5.

**Real-world example:** A microservices architecture where a central Identity Provider (Keycloak, Okta, or Auth0) issues tokens after a user logs in once (Single Sign-On), and each downstream microservice is configured purely as an **OAuth2 Resource Server** — validating the token's signature against the shared identity provider's public keys, with no microservice needing to handle passwords, sessions, or its own login flow at all.

**Q&A**
- **Q: What's the practical difference between building your own JWT auth (Section 5) and using Spring's OAuth2 Resource Server support?**
  A: Self-issued JWTs (Section 5) mean your own application is *both* the identity provider and the resource server — you generate, sign, and validate the tokens yourself, suitable for a single application or tightly-coupled system. OAuth2 Resource Server delegates *identity and token issuance* to a dedicated, external Authorization Server (Keycloak, Auth0, Okta, Azure AD) — your application only *validates* tokens issued elsewhere, which is the standard pattern for multi-service/enterprise architectures needing centralized identity management, SSO, and standardized token formats across many independently-developed services.

---

## 7. CSRF Protection

**What is CSRF (Cross-Site Request Forgery)?** An attack where a malicious site tricks a logged-in user's browser into submitting an unintended, state-changing request to a different site where the user is authenticated (relying on the browser automatically attaching session cookies to that request).

**How Spring Security's CSRF protection works by default:**
- For session-based (cookie-authenticated) applications, Spring Security requires a **CSRF token** to be included in state-changing requests (`POST`, `PUT`, `DELETE`, `PATCH`) — a token the attacker's malicious site has no way to know or forge, since it's tied to the user's session and delivered only to legitimate pages of your own application.
- **GET requests are exempt** by default, since they should never cause side effects (a REST design principle, not just a security one).

```java
// Enabled by default for session-based auth - typically no code needed, but here's what it looks like explicitly:
http.csrf(csrf -> csrf
    .csrfTokenRepository(CookieCsrfTokenRepository.withHttpOnlyFalse()) // token exposed via a readable cookie for JS/SPA frontends
);
```

**Why CSRF protection is typically DISABLED for stateless REST APIs using JWT:**
```java
http.csrf(csrf -> csrf.disable()); // common in JWT-based, session-less REST APIs
```
CSRF attacks fundamentally exploit **automatic cookie attachment** by browsers. A stateless API authenticated via a JWT sent explicitly in an `Authorization: Bearer <token>` header (not a cookie) is **not vulnerable to CSRF** in the same way — there's no ambient credential the browser attaches automatically that an attacker's page could piggyback on. This is why disabling CSRF protection is standard and safe specifically for token-header-authenticated stateless APIs, but would be a serious mistake for a traditional session-cookie-based web application.

**Comparison:**
| Authentication Style | CSRF Risk | Recommended CSRF Setting |
|---|---|---|
| Session cookie (traditional web app, form login) | High — cookies attach automatically | Enabled (Spring Security default) |
| JWT in `Authorization` header (stateless REST API) | Low — no ambient credential | Typically disabled |
| JWT stored in a cookie (some SPA architectures) | High again — back to cookie-based risk | Enabled, or use `SameSite=Strict` cookies as a complementary defense |

**Q&A**
- **Q: If a team decides to store a JWT in an HttpOnly cookie instead of local storage/an Authorization header (a common XSS-mitigation choice), does CSRF protection need to be re-enabled?**
  A: Yes — once the token is stored in a cookie, the browser will attach it automatically to requests again, reintroducing the exact ambient-credential problem CSRF protection exists to prevent. This exact tradeoff (JWT in localStorage is vulnerable to XSS token theft; JWT in a cookie is vulnerable to CSRF unless mitigated) is a classic, nuanced interview discussion point — the typical resolution is a cookie with `HttpOnly`, `Secure`, and `SameSite=Strict` attributes, combined with re-enabling CSRF protection or using the `SameSite` attribute as the primary CSRF defense instead.

---

## 8. Real-World Example: Securing a REST API with JWT

**Scenario:** A complete login + protected-endpoint flow for an `Employee Management` REST API.

```java
// ---------- Authentication Endpoint ----------
@RestController
@RequestMapping("/api/v1/auth")
public class AuthController {
    private final AuthenticationManager authenticationManager;
    private final JwtService jwtService;
    private final UserDetailsService userDetailsService;

    public AuthController(AuthenticationManager authenticationManager, JwtService jwtService,
                            UserDetailsService userDetailsService) {
        this.authenticationManager = authenticationManager;
        this.jwtService = jwtService;
        this.userDetailsService = userDetailsService;
    }

    @PostMapping("/login")
    public ResponseEntity<AuthResponse> login(@Valid @RequestBody LoginRequest request) {
        authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(request.getUsername(), request.getPassword()));
        // if authenticate() doesn't throw, credentials are valid

        UserDetails userDetails = userDetailsService.loadUserByUsername(request.getUsername());
        String token = jwtService.generateToken(userDetails);
        return ResponseEntity.ok(new AuthResponse(token));
    }
}

// ---------- Protected Endpoint ----------
@RestController
@RequestMapping("/api/v1/employees")
public class EmployeeController {
    private final EmployeeService employeeService;
    public EmployeeController(EmployeeService employeeService) { this.employeeService = employeeService; }

    @GetMapping
    @PreAuthorize("hasAnyRole('ADMIN', 'MANAGER')")
    public ResponseEntity<List<EmployeeDto>> getAllEmployees() {
        return ResponseEntity.ok(employeeService.getAll());
    }

    @DeleteMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<Void> deleteEmployee(@PathVariable Long id) {
        employeeService.delete(id);
        return ResponseEntity.noContent().build();
    }
}

// ---------- Full Security Configuration ----------
@Configuration
@EnableWebSecurity
@EnableMethodSecurity
public class SecurityConfig {

    private final JwtAuthenticationFilter jwtAuthFilter;
    private final CustomUserDetailsService userDetailsService;

    public SecurityConfig(JwtAuthenticationFilter jwtAuthFilter, CustomUserDetailsService userDetailsService) {
        this.jwtAuthFilter = jwtAuthFilter;
        this.userDetailsService = userDetailsService;
    }

    @Bean
    public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
        http
            .csrf(csrf -> csrf.disable()) // stateless, token-header-authenticated API
            .sessionManagement(session -> session.sessionCreationPolicy(SessionCreationPolicy.STATELESS))
            .authorizeHttpRequests(auth -> auth
                .requestMatchers("/api/v1/auth/**").permitAll()
                .anyRequest().authenticated())
            .authenticationProvider(authenticationProvider())
            .addFilterBefore(jwtAuthFilter, UsernamePasswordAuthenticationFilter.class)
            .exceptionHandling(ex -> ex
                .authenticationEntryPoint((req, res, e) -> res.sendError(401, "Unauthorized"))
                .accessDeniedHandler((req, res, e) -> res.sendError(403, "Forbidden")));
        return http.build();
    }

    @Bean
    public AuthenticationProvider authenticationProvider() {
        DaoAuthenticationProvider provider = new DaoAuthenticationProvider();
        provider.setUserDetailsService(userDetailsService);
        provider.setPasswordEncoder(passwordEncoder());
        return provider;
    }

    @Bean
    public PasswordEncoder passwordEncoder() { return new BCryptPasswordEncoder(); }

    @Bean
    public AuthenticationManager authenticationManager(AuthenticationConfiguration config) throws Exception {
        return config.getAuthenticationManager();
    }
}
```

**Request flow demonstrated end-to-end:**
1. `POST /api/v1/auth/login` with username/password → `AuthenticationManager` verifies credentials against `CustomUserDetailsService` (BCrypt comparison) → JWT issued.
2. Client stores the JWT and sends it as `Authorization: Bearer <token>` on subsequent requests.
3. `JwtAuthenticationFilter` intercepts each request, validates the token, and populates `SecurityContextHolder` before the request reaches the controller.
4. `@PreAuthorize` annotations on controller methods enforce role-based authorization using the now-populated `SecurityContext`.
5. Unauthenticated requests get `401`; authenticated-but-unauthorized requests get `403`.

---

## 9. Interview Questions

### Q1: Difference between authentication and authorization?
**A:** Authentication verifies **who** the caller is (validating credentials, a token's signature, etc.) and results in a `401` on failure. Authorization determines **what** an already-authenticated caller is permitted to do, and results in a `403` on failure. Authentication always happens first; authorization decisions are made based on the identity/roles established during authentication.

### Q2: How does Spring Security handle CSRF?
**A:** By default (for session/cookie-based authentication), Spring Security requires a CSRF token to accompany state-changing requests (`POST`/`PUT`/`DELETE`/`PATCH`), rejecting requests that don't include a valid token matching the user's session — this defeats CSRF attacks because a malicious third-party site has no way to obtain or forge that token. For stateless REST APIs authenticated via a token sent in the `Authorization` header (not a cookie), CSRF protection is typically disabled, since there's no ambient, browser-auto-attached credential for an attacker to exploit in the first place. If a JWT is stored in a cookie instead, CSRF protection should remain enabled (or `SameSite` cookie attributes used) since the vulnerability returns.

### Q3: Explain JWT integration with Spring Security.
**A:** JWT integration typically involves: (1) a service to generate signed tokens containing user identity/claims upon successful login, (2) a custom `OncePerRequestFilter` inserted into the security filter chain (before `UsernamePasswordAuthenticationFilter`) that extracts the token from the `Authorization` header, validates its signature and expiration, and — if valid — populates `SecurityContextHolder` with an `Authentication` object built from the token's claims, and (3) configuring `SessionCreationPolicy.STATELESS` since the token itself carries all needed state, eliminating the need for server-side sessions. Downstream, `@PreAuthorize` and URL-based `authorizeHttpRequests` rules work exactly as they would with any other authentication mechanism, since they operate on the populated `SecurityContext` regardless of how it was populated.

### Q4: What is the OAuth2 flow in Spring Security?
**A:** The most common and secure flow is the **Authorization Code flow**: the user is redirected to an Authorization Server to authenticate and grant consent; the Authorization Server redirects back to the client application with a short-lived authorization code; the client (server-side, holding a client secret) exchanges that code for an access token (and, with OpenID Connect, an ID token) directly with the Authorization Server. Spring Security supports two complementary roles: **OAuth2 Client** (`spring-boot-starter-oauth2-client`, for an application that needs to log users in via an external provider like Google) and **OAuth2 Resource Server** (`spring-boot-starter-oauth2-resource-server`, for an API that validates tokens issued by an external Authorization Server, typically via JWT signature verification against the issuer's published public keys).

### Q5: What's the difference between `hasRole()` and `hasAuthority()`, and why does this trip people up?
**A:** `hasRole("ADMIN")` checks for the authority string `"ROLE_ADMIN"` — Spring Security silently prepends `"ROLE_"`. `hasAuthority("ADMIN")` checks for the literal string `"ADMIN"` with no prefix added. This trips people up because it's an invisible convention: if authorities are stored in the database or JWT claims as plain `"ADMIN"` (without the `ROLE_` prefix) but the security config uses `hasRole("ADMIN")`, the check silently fails (looking for `"ROLE_ADMIN"` which doesn't exist) rather than throwing an obvious error — a very common real-world debugging session.

### Q6: How would you implement token refresh in a JWT-based system, given that JWTs can't be revoked?
**A:** Issue two tokens at login: a short-lived **access token** (minutes to an hour) used for actual API calls, and a longer-lived **refresh token** (days) stored more securely (often HttpOnly cookie) and used *only* to request a new access token from a dedicated `/refresh` endpoint once the access token expires. This limits the exposure window of a compromised access token while avoiding forcing the user to re-authenticate frequently. Refresh tokens themselves should be revocable (tracked server-side, e.g., in a database or Redis), since unlike access tokens they're used infrequently enough that a server-side lookup isn't a scalability concern.

### Q7: Why is `BCryptPasswordEncoder` preferred over plain hashing algorithms like MD5 or SHA-256 for storing passwords?
**A:** BCrypt is a deliberately **slow**, adaptive hashing algorithm with a built-in configurable work factor and automatic per-password salting — properties specifically designed to resist brute-force and rainbow-table attacks, since an attacker must spend meaningful computation per guess. MD5/SHA-256 are designed to be *fast*, which is exactly the wrong property for password hashing (fast hashing means an attacker can attempt billions of guesses per second on stolen hash data) — they're appropriate for data integrity checks, not credential storage.

### Q8: Real-time scenario — how would you secure a microservices architecture where multiple services need to validate the same user's identity?
**A:** Centralize identity issuance in a single Authorization Server (Keycloak, Okta, Auth0, or a custom auth service) using OAuth2/OIDC, issuing signed JWTs (ideally with asymmetric signing, `RS256`, so services only need the public key to verify — never a shared secret every service would otherwise need). Each downstream microservice is configured purely as an **OAuth2 Resource Server**, independently validating incoming tokens' signatures against the Authorization Server's published JWKS endpoint — no service needs to call back to the auth server synchronously per request, and no service needs to handle passwords or sessions directly. This also naturally supports Single Sign-On, since a user authenticates once against the central Authorization Server and the resulting token is accepted by every downstream service.

---