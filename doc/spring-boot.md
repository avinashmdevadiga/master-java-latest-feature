# Spring Boot — Complete Guide (Basics to Advanced)
### For Experienced Java Developers Preparing for Interviews

---

## Table of Contents
1. [Why Spring Boot? Advantages over Spring](#1-why-spring-boot-advantages-over-spring)
2. [Auto-Configuration and Starters](#2-auto-configuration-and-starters)
3. [Spring Boot Annotations](#3-spring-boot-annotations)
4. [Profiles and Configuration Management](#4-profiles-and-configuration-management)
5. [Embedded Servers](#5-embedded-servers-tomcat-jetty)
6. [Actuator for Monitoring](#6-actuator-for-monitoring)
7. [Real-World Example: RESTful Microservice](#7-real-world-example-restful-microservice-with-spring-boot)
8. [Interview Questions](#8-interview-questions)

---

## 1. Why Spring Boot? Advantages over Spring

**The problem with plain Spring:** Setting up a Spring application traditionally required substantial manual configuration — `web.xml`, `DispatcherServlet` setup, manually declaring dozens of beans, choosing and configuring a compatible version of every dependency, and deploying to an external application server (Tomcat/JBoss/WebSphere).

**What Spring Boot adds on top of Spring:**

| Aspect | Plain Spring | Spring Boot |
|---|---|---|
| Configuration | Extensive manual XML/Java config | Auto-configuration based on classpath contents |
| Dependency management | Manually align compatible versions | Curated "starter" BOMs with tested version combinations |
| Server | Requires external deployment (WAR to Tomcat/JBoss) | Embedded server (Tomcat/Jetty/Undertow) — runs as a standalone JAR |
| Boilerplate | Significant | Minimal — sensible defaults everywhere |
| Production readiness | Must build monitoring/health checks yourself | Actuator provides health, metrics, and monitoring out of the box |
| Getting started | Non-trivial setup effort | `@SpringBootApplication` + `main()` method — running in minutes |

**A minimal Spring Boot application:**
```java
@SpringBootApplication
public class OrderServiceApplication {
    public static void main(String[] args) {
        SpringApplication.run(OrderServiceApplication.class, args);
    }
}
```
This single class, with its embedded server and auto-configuration, replaces what would traditionally be several XML configuration files, a `web.xml`, and a separately-deployed application server.

**Important nuance:** Spring Boot is **not a replacement for Spring** — it's built entirely on top of the Spring Framework. Every core concept (IoC, DI, AOP, `ApplicationContext`) still applies; Spring Boot's value-add is convention-over-configuration, dependency curation, and operational tooling.

**Q&A**
- **Q: Is Spring Boot a separate framework from Spring?**
  A: No — Spring Boot uses the Spring Framework's IoC container underneath. It's an opinionated layer that auto-configures Spring beans based on what's on the classpath and provides packaging/operational conveniences (embedded servers, Actuator, starter dependencies) — it doesn't replace or compete with core Spring concepts.

---

## 2. Auto-Configuration and Starters

### 2.1 Starters

A **starter** is a curated dependency descriptor that pulls in everything typically needed for a specific type of functionality, at mutually-compatible versions.

```xml
<dependency>
    <groupId>org.springframework.boot</groupId>
    <artifactId>spring-boot-starter-web</artifactId>
</dependency>
<!-- Pulls in: Spring MVC, embedded Tomcat, Jackson (JSON), validation, and more -->

<dependency>
    <groupId>org.springframework.boot</groupId>
    <artifactId>spring-boot-starter-data-jpa</artifactId>
</dependency>
<!-- Pulls in: Spring Data JPA, Hibernate, HikariCP connection pool -->
```

Common starters: `spring-boot-starter-web`, `spring-boot-starter-data-jpa`, `spring-boot-starter-security`, `spring-boot-starter-test`, `spring-boot-starter-actuator`, `spring-boot-starter-validation`.

### 2.2 Auto-Configuration — How It Actually Works

Spring Boot's `@EnableAutoConfiguration` (bundled inside `@SpringBootApplication`) scans the classpath and **conditionally** registers beans based on what it finds — using `@Conditional`-family annotations.

```java
// Simplified version of what Spring Boot's actual DataSourceAutoConfiguration does internally
@Configuration
@ConditionalOnClass(DataSource.class)          // only activates if a DataSource class is on the classpath
@ConditionalOnMissingBean(DataSource.class)    // only activates if the user hasn't already defined their own DataSource bean
@EnableConfigurationProperties(DataSourceProperties.class)
public class DataSourceAutoConfiguration {
    @Bean
    public DataSource dataSource(DataSourceProperties properties) {
        return properties.initializeDataSourceBuilder().build();
    }
}
```

**Key `@Conditional` annotations used throughout Spring Boot's auto-configuration classes:**
| Annotation | Activates When... |
|---|---|
| `@ConditionalOnClass` | A specific class is present on the classpath |
| `@ConditionalOnMissingBean` | No bean of that type has already been defined by the user |
| `@ConditionalOnProperty` | A specific property is set (optionally to a specific value) |
| `@ConditionalOnWebApplication` | The application is a web application |
| `@ConditionalOnMissingClass` | A specific class is absent from the classpath |

**This is why adding `spring-boot-starter-web` to the classpath is enough** to get a fully configured `DispatcherServlet`, embedded Tomcat, and Jackson message converters — Spring Boot detects these dependencies are present and auto-configures the beans they need, with sensible defaults, **unless you've already defined your own** (in which case `@ConditionalOnMissingBean` backs off and respects your explicit configuration).

**Overriding auto-configuration:**
```java
@Configuration
public class CustomDataSourceConfig {
    @Bean
    public DataSource dataSource() {
        // Your custom DataSource bean here - Spring Boot's DataSourceAutoConfiguration
        // will see this already exists (@ConditionalOnMissingBean) and step aside
        return DataSourceBuilder.create()
                .url("jdbc:postgresql://localhost:5432/mydb")
                .build();
    }
}
```

**Real-world example:** Adding `spring-boot-starter-data-redis` to a project's dependencies is often enough on its own to get a fully wired `RedisTemplate` bean — no manual `RedisConnectionFactory` or `RedisTemplate` bean definition needed for the default case, letting a developer go from "add dependency" to "inject and use `RedisTemplate`" in minutes.

**Q&A**
- **Q: How would you debug why a particular auto-configuration isn't being applied?**
  A: Run the application with `--debug` (or set `debug=true` in properties) — Spring Boot logs a detailed **auto-configuration report** at startup showing every auto-configuration class considered, whether it was applied ("Positive matches") or skipped ("Negative matches"), and exactly which condition caused it to be skipped.

---

## 3. Spring Boot Annotations

```java
@SpringBootApplication // combines three annotations:
// = @Configuration + @EnableAutoConfiguration + @ComponentScan
public class MyApplication {
    public static void main(String[] args) {
        SpringApplication.run(MyApplication.class, args);
    }
}
```

| Annotation | Purpose |
|---|---|
| `@SpringBootApplication` | Entry point marker — combines `@Configuration`, `@EnableAutoConfiguration`, `@ComponentScan` |
| `@RestController` | `@Controller` + `@ResponseBody` — every method's return value is written directly to the HTTP response body (typically as JSON), not resolved to a view |
| `@RequestMapping` / `@GetMapping` / `@PostMapping` / `@PutMapping` / `@DeleteMapping` | Map HTTP requests to handler methods |
| `@RequestBody` | Deserializes the HTTP request body into a Java object |
| `@PathVariable` | Binds a URI template variable to a method parameter |
| `@RequestParam` | Binds a query string parameter to a method parameter |
| `@Valid` / `@Validated` | Triggers Bean Validation on the annotated argument |
| `@ConfigurationProperties` | Binds a group of related external properties to a strongly-typed Java object |
| `@ConditionalOnProperty` | Used in custom auto-configuration/beans, conditional on a property's presence/value |
| `@Profile` | Restricts a bean's registration to a specific active profile |
| `@ComponentScan` | Configures which packages to scan for `@Component`-annotated classes |

```java
@RestController
@RequestMapping("/api/v1/employees")
public class EmployeeController {

    private final EmployeeService employeeService;

    public EmployeeController(EmployeeService employeeService) {
        this.employeeService = employeeService;
    }

    @GetMapping("/{id}")
    public ResponseEntity<EmployeeDto> getEmployee(@PathVariable Long id) {
        return ResponseEntity.ok(employeeService.getEmployee(id));
    }

    @GetMapping
    public ResponseEntity<List<EmployeeDto>> searchEmployees(
            @RequestParam(required = false) String department,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "20") int size) {
        return ResponseEntity.ok(employeeService.search(department, page, size));
    }

    @PostMapping
    public ResponseEntity<EmployeeDto> createEmployee(@Valid @RequestBody CreateEmployeeRequest request) {
        EmployeeDto created = employeeService.createEmployee(request);
        return ResponseEntity.status(HttpStatus.CREATED).body(created);
    }
}
```

**`@ConfigurationProperties` — the recommended alternative to scattered `@Value` annotations:**
```java
@Component
@ConfigurationProperties(prefix = "app.mail")
public class MailProperties {
    private String host;
    private int port;
    private String username;
    // getters/setters - Spring binds app.mail.host, app.mail.port, app.mail.username automatically
}
```
```yaml
app:
  mail:
    host: smtp.company.com
    port: 587
    username: notifications@company.com
```

**Q&A**
- **Q: When would you use `@ConfigurationProperties` over `@Value`?**
  A: `@ConfigurationProperties` is preferred for groups of related settings — it's type-safe (validated at binding time, supports nested objects and collections), refactor-safe (IDE support for renaming), and testable in isolation, whereas `@Value` is better suited for a single, standalone property injected directly where it's used.

---

## 4. Profiles and Configuration Management

**Profiles** let you maintain environment-specific configuration (dev, staging, production) and activate only the relevant one at runtime.

```yaml
# application.yml (common/default config)
spring:
  application:
    name: order-service
---
# application-dev.yml
spring:
  config:
    activate:
      on-profile: dev
  datasource:
    url: jdbc:h2:mem:testdb
logging:
  level:
    root: DEBUG
---
# application-prod.yml
spring:
  config:
    activate:
      on-profile: prod
  datasource:
    url: jdbc:postgresql://prod-db:5432/orders
logging:
  level:
    root: WARN
```

**Activating a profile:**
```bash
# Command line
java -jar order-service.jar --spring.profiles.active=prod

# Environment variable
export SPRING_PROFILES_ACTIVE=prod

# In application.yml itself (less common, usually overridden by CI/CD)
spring:
  profiles:
    active: dev
```

**Profile-specific beans:**
```java
@Configuration
public class PaymentGatewayConfig {

    @Bean
    @Profile("prod")
    public PaymentGateway prodPaymentGateway() {
        return new StripeLivePaymentGateway();
    }

    @Bean
    @Profile("dev")
    public PaymentGateway devPaymentGateway() {
        return new MockPaymentGateway(); // never charges real money in dev
    }
}
```

**Configuration property precedence (highest to lowest, simplified):**
1. Command-line arguments
2. `SPRING_APPLICATION_JSON` environment property
3. `ServletConfig`/`ServletContext` init parameters
4. OS environment variables
5. Profile-specific `application-{profile}.yml`
6. Default `application.yml`
7. `@PropertySource` annotated classes
8. Default properties (`SpringApplication.setDefaultProperties`)

**Real-world example:** A microservice needs different database connections, log levels, and external API endpoints in local development, CI/CD pipeline testing, staging, and production — profiles let the same JAR artifact be promoted through every environment unchanged, with only the active profile (typically set via a Kubernetes ConfigMap or environment variable) differing per deployment, which is the correct production pattern versus rebuilding for each environment.

**Q&A**
- **Q: Why should secrets (passwords, API keys) not be committed directly into `application-prod.yml`?**
  A: Committing secrets to source control is a security risk regardless of environment separation — production secrets should come from external, access-controlled sources (environment variables injected by the deployment platform, a secrets manager like HashiCorp Vault or AWS Secrets Manager, or Kubernetes Secrets), referenced in configuration via placeholders (`${DB_PASSWORD}`) rather than hardcoded values.

---

## 5. Embedded Servers (Tomcat, Jetty)

Spring Boot applications are typically packaged as **executable JARs** with an embedded servlet container, rather than as WAR files deployed to an external server.

```xml
<!-- Tomcat is the DEFAULT embedded server when using spring-boot-starter-web -->
<dependency>
    <groupId>org.springframework.boot</groupId>
    <artifactId>spring-boot-starter-web</artifactId>
</dependency>

<!-- To switch to Jetty instead, exclude Tomcat and add the Jetty starter -->
<dependency>
    <groupId>org.springframework.boot</groupId>
    <artifactId>spring-boot-starter-web</artifactId>
    <exclusions>
        <exclusion>
            <groupId>org.springframework.boot</groupId>
            <artifactId>spring-boot-starter-tomcat</artifactId>
        </exclusion>
    </exclusions>
</dependency>
<dependency>
    <groupId>org.springframework.boot</groupId>
    <artifactId>spring-boot-starter-jetty</artifactId>
</dependency>
```

**Comparison:**
| Server | Characteristics |
|---|---|
| **Tomcat** (default) | Most widely used, mature, good general-purpose performance, thread-per-request model |
| **Jetty** | Lighter-weight, often favored for embedded/low-memory-footprint scenarios |
| **Undertow** | Non-blocking I/O, typically the best throughput/memory profile of the three, used heavily with WebFlux |

**Configuring the embedded server:**
```yaml
server:
  port: 8443
  ssl:
    key-store: classpath:keystore.p12
    key-store-password: ${SSL_PASSWORD}
    key-store-type: PKCS12
  tomcat:
    threads:
      max: 200
      min-spare: 10
    connection-timeout: 5000
  compression:
    enabled: true
```

**Why this matters over WAR deployment:**
- **Self-contained deployment** — the JAR includes everything needed to run; no separately-installed/versioned application server to manage across environments.
- **Consistent server version** — the embedded server version is locked to what's declared in your build file, eliminating "works on my Tomcat 9 but not the prod Tomcat 8.5" class of issues.
- **Simpler containerization** — a Docker image just needs a JRE and the JAR; no application server installation/configuration inside the image.

**Q&A**
- **Q: Can a Spring Boot application still be deployed as a traditional WAR to an external server?**
  A: Yes — by extending `SpringBootServletInitializer` and changing the packaging to `war` in the build file, a Spring Boot app can still be deployed to an external servlet container if organizational constraints require it, though this is increasingly uncommon given the operational benefits of the embedded-server/executable-JAR model, especially in containerized deployments.

---

## 6. Actuator for Monitoring

**Spring Boot Actuator** exposes production-ready operational endpoints (health, metrics, environment info) with minimal setup.

```xml
<dependency>
    <groupId>org.springframework.boot</groupId>
    <artifactId>spring-boot-starter-actuator</artifactId>
</dependency>
```

```yaml
management:
  endpoints:
    web:
      exposure:
        include: health, info, metrics, prometheus  # only expose what's needed - never "*" in production
  endpoint:
    health:
      show-details: when-authorized
  metrics:
    tags:
      application: order-service
```

**Key built-in endpoints:**
| Endpoint | Purpose |
|---|---|
| `/actuator/health` | Application health status — used by load balancers/Kubernetes for liveness/readiness probes |
| `/actuator/info` | Arbitrary build/application metadata |
| `/actuator/metrics` | Detailed metrics (JVM memory, HTTP request counts/latencies, custom metrics) |
| `/actuator/env` | Current environment properties (sensitive — restrict access) |
| `/actuator/loggers` | View/modify logging levels **at runtime**, without a redeploy |
| `/actuator/threaddump` | JVM thread dump — invaluable for diagnosing deadlocks/hangs in production |
| `/actuator/prometheus` | Metrics in Prometheus scrape format (requires `micrometer-registry-prometheus`) |

**Custom health indicator:**
```java
@Component
public class ExternalPaymentGatewayHealthIndicator implements HealthIndicator {
    private final PaymentGatewayClient client;

    public ExternalPaymentGatewayHealthIndicator(PaymentGatewayClient client) { this.client = client; }

    @Override
    public Health health() {
        try {
            client.ping();
            return Health.up().withDetail("gateway", "reachable").build();
        } catch (Exception e) {
            return Health.down(e).withDetail("gateway", "unreachable").build();
        }
    }
}
```

**Custom metrics with Micrometer:**
```java
@Service
public class OrderService {
    private final Counter orderCounter;
    private final Timer orderProcessingTimer;

    public OrderService(MeterRegistry registry) {
        this.orderCounter = Counter.builder("orders.created").register(registry);
        this.orderProcessingTimer = Timer.builder("orders.processing.time").register(registry);
    }

    public void placeOrder(Order order) {
        orderProcessingTimer.record(() -> {
            // ... order placement logic
            orderCounter.increment();
        });
    }
}
```

**Real-world example:** In a Kubernetes deployment, `/actuator/health/liveness` and `/actuator/health/readiness` are wired directly into the pod's liveness and readiness probes — if the readiness probe fails (e.g., the database connection pool is exhausted), Kubernetes stops routing traffic to that pod without killing it, giving it a chance to recover; if the liveness probe fails repeatedly, Kubernetes restarts the pod entirely.

**Q&A**
- **Q: Why should you never expose all Actuator endpoints (`include: "*"`) in a production environment?**
  A: Several endpoints (`/env`, `/heapdump`, `/threaddump`, `/beans`) expose sensitive internal application details (environment variables potentially containing secrets, memory contents, full bean graph) that could aid an attacker or leak confidential configuration — production should explicitly whitelist only the endpoints actually needed (typically `health`, `info`, `metrics`, `prometheus`) and secure them behind authentication/network restrictions.

---

## 7. Real-World Example: RESTful Microservice with Spring Boot

**Scenario:** A complete, production-shaped `Order` microservice demonstrating the concepts above together.

```java
@SpringBootApplication
public class OrderServiceApplication {
    public static void main(String[] args) { SpringApplication.run(OrderServiceApplication.class, args); }
}

@Entity
@Table(name = "orders")
public class Order {
    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    private String customerEmail;
    @Enumerated(EnumType.STRING)
    private OrderStatus status;
    private BigDecimal totalAmount;
    private LocalDateTime createdAt;
    // getters/setters
}

public interface OrderRepository extends JpaRepository<Order, Long> {
    List<Order> findByCustomerEmail(String email);
}

@Service
@Transactional
public class OrderService {
    private final OrderRepository orderRepository;
    private final PaymentGatewayClient paymentGatewayClient;
    private final MeterRegistry meterRegistry;

    public OrderService(OrderRepository orderRepository, PaymentGatewayClient paymentGatewayClient,
                         MeterRegistry meterRegistry) {
        this.orderRepository = orderRepository;
        this.paymentGatewayClient = paymentGatewayClient;
        this.meterRegistry = meterRegistry;
    }

    public Order placeOrder(CreateOrderRequest request) {
        PaymentResult result = paymentGatewayClient.charge(request.getAmount(), request.getPaymentToken());
        if (!result.isSuccess()) throw new PaymentFailedException(result.getFailureReason());

        Order order = new Order();
        order.setCustomerEmail(request.getCustomerEmail());
        order.setTotalAmount(request.getAmount());
        order.setStatus(OrderStatus.CONFIRMED);
        order.setCreatedAt(LocalDateTime.now());

        Order saved = orderRepository.save(order);
        meterRegistry.counter("orders.placed", "status", "confirmed").increment();
        return saved;
    }
}

@RestController
@RequestMapping("/api/v1/orders")
@Validated
public class OrderController {
    private final OrderService orderService;
    public OrderController(OrderService orderService) { this.orderService = orderService; }

    @PostMapping
    public ResponseEntity<OrderDto> placeOrder(@Valid @RequestBody CreateOrderRequest request) {
        Order order = orderService.placeOrder(request);
        return ResponseEntity.status(HttpStatus.CREATED).body(OrderDto.from(order));
    }

    @GetMapping("/{id}")
    public ResponseEntity<OrderDto> getOrder(@PathVariable Long id) {
        return orderService.findById(id)
                .map(order -> ResponseEntity.ok(OrderDto.from(order)))
                .orElse(ResponseEntity.notFound().build());
    }
}

@RestControllerAdvice // global exception handling across all @RestController classes
public class GlobalExceptionHandler {
    @ExceptionHandler(PaymentFailedException.class)
    public ResponseEntity<ErrorResponse> handlePaymentFailure(PaymentFailedException ex) {
        return ResponseEntity.status(HttpStatus.PAYMENT_REQUIRED)
                .body(new ErrorResponse("PAYMENT_FAILED", ex.getMessage()));
    }

    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<ErrorResponse> handleValidation(MethodArgumentNotValidException ex) {
        return ResponseEntity.badRequest()
                .body(new ErrorResponse("VALIDATION_ERROR", ex.getMessage()));
    }
}

@Component
public class PaymentGatewayHealthIndicator implements HealthIndicator {
    private final PaymentGatewayClient client;
    public PaymentGatewayHealthIndicator(PaymentGatewayClient client) { this.client = client; }
    @Override
    public Health health() {
        return client.isReachable() ? Health.up().build() : Health.down().build();
    }
}
```

```yaml
# application.yml
spring:
  application:
    name: order-service
  datasource:
    url: jdbc:postgresql://${DB_HOST:localhost}:5432/orders
    username: ${DB_USER}
    password: ${DB_PASSWORD}
  jpa:
    hibernate:
      ddl-auto: validate
management:
  endpoints:
    web:
      exposure:
        include: health, info, metrics, prometheus
  endpoint:
    health:
      probes:
        enabled: true # enables /actuator/health/liveness and /readiness for Kubernetes
```

This example ties together: `@SpringBootApplication` bootstrapping, auto-configured JPA/DataSource, `@RestController` + `@Valid` request handling, `@RestControllerAdvice` global error handling, custom Actuator health checks, custom Micrometer metrics, and externalized, environment-variable-driven configuration — the shape of a real production microservice.

---

## 8. Interview Questions

### Q1: Difference between Spring and Spring Boot?
**A:** Spring is the underlying framework providing IoC, DI, AOP, and the broader ecosystem of modules (MVC, Data, Security, etc.) — using it traditionally requires substantial manual configuration. Spring Boot is built **on top of** Spring and provides: (1) **auto-configuration** that wires beans automatically based on classpath contents, (2) **starter dependencies** bundling compatible library versions, (3) an **embedded server** for standalone, deployable JARs instead of WAR-to-external-server deployment, and (4) **Actuator** for production monitoring out of the box. Spring Boot doesn't replace Spring concepts — every Spring Framework fundamental (beans, DI, `@Transactional`, AOP) still applies underneath.

### Q2: What is auto-configuration in Spring Boot?
**A:** Auto-configuration is Spring Boot's mechanism for automatically registering beans based on what's present on the application's classpath and what the developer hasn't already explicitly configured. It's implemented via `@Conditional`-family annotations (`@ConditionalOnClass`, `@ConditionalOnMissingBean`, `@ConditionalOnProperty`, etc.) on a large set of `@Configuration` classes bundled inside Spring Boot's auto-configure JAR, activated through `@EnableAutoConfiguration` (part of `@SpringBootApplication`). If you define your own bean of a given type, Spring Boot's `@ConditionalOnMissingBean` backs off and respects your configuration instead.

### Q3: How do you manage multiple environments in Spring Boot?
**A:** Through **Spring Profiles** — maintaining environment-specific configuration files (`application-dev.yml`, `application-prod.yml`) and activating the appropriate one via `spring.profiles.active` (set through command-line args, environment variables, or a deployment platform's configuration). Beans can also be conditionally registered per profile using `@Profile("prod")`. Secrets should never be hardcoded in these files — they should come from environment variables or a secrets manager, referenced via placeholder syntax (`${DB_PASSWORD}`).

### Q4: Explain Spring Boot Actuator.
**A:** Actuator is a Spring Boot module that exposes production-ready operational endpoints over HTTP (or JMX) — health checks, metrics, environment info, thread dumps, and runtime-adjustable logging levels — with minimal setup (just adding the `spring-boot-starter-actuator` dependency). It integrates with Micrometer for metrics (which can be exported to Prometheus, Datadog, etc.) and is commonly wired into Kubernetes liveness/readiness probes via `/actuator/health/liveness` and `/actuator/health/readiness`. In production, only necessary endpoints should be exposed and access should be restricted, since some endpoints reveal sensitive internal details.

### Q5: What's the difference between `@Controller` and `@RestController`?
**A:** `@Controller` is used in traditional Spring MVC applications where methods typically return a **view name** to be resolved (e.g., a Thymeleaf template). `@RestController` is `@Controller` + `@ResponseBody` combined — every method's return value is serialized directly into the HTTP response body (typically as JSON via Jackson), which is the standard pattern for building REST APIs.

### Q6: How does Spring Boot decide which embedded server to use?
**A:** By default, `spring-boot-starter-web` includes `spring-boot-starter-tomcat`, making Tomcat the default embedded server. To use Jetty or Undertow instead, you exclude the Tomcat starter dependency and add the corresponding starter (`spring-boot-starter-jetty` or `spring-boot-starter-undertow`) — Spring Boot's auto-configuration then detects which server library is on the classpath and configures accordingly.

### Q7: What is `spring-boot-starter-parent`, and what does it provide?
**A:** It's a special Maven parent POM that provides default configuration for compiler settings, resource filtering, plugin management, and — most importantly — **dependency version management** (a curated Bill of Materials) so that individual starter dependencies don't need explicit version numbers; they inherit tested-compatible versions from the parent. Projects that can't use it directly as a parent (e.g., already have a different corporate parent POM) can instead import `spring-boot-dependencies` as a BOM in `<dependencyManagement>`.

### Q8: How would you add custom auto-configuration for a shared internal library used across multiple microservices?
**A:** Create a separate module containing a `@Configuration` class with the desired bean definitions guarded by appropriate `@Conditional` annotations (e.g., `@ConditionalOnClass`, `@ConditionalOnMissingBean`), then register it in `META-INF/spring/org.springframework.boot.autoconfigure.AutoConfiguration.imports` (Spring Boot 2.7+) or the legacy `spring.factories` file (older versions) so that any application including this library as a dependency automatically picks up the auto-configuration — this is exactly the mechanism Spring Boot itself uses internally, and is the standard pattern for building internal "starter" libraries at a company.

---