# Spring Framework — Complete Guide (Basics to Advanced)
### For Experienced Java Developers Preparing for Interviews

---

## Table of Contents
1. [Introduction to Spring and IoC](#1-introduction-to-spring-and-ioc-inversion-of-control)
2. [Dependency Injection: Constructor vs Setter](#2-dependency-injection-constructor-vs-setter)
3. [Spring Bean Lifecycle](#3-spring-bean-lifecycle)
4. [ApplicationContext vs BeanFactory](#4-applicationcontext-vs-beanfactory)
5. [Autowiring and Qualifiers](#5-autowiring-and-qualifiers)
6. [Spring Modules](#6-spring-modules)
7. [Real-World Example: Layered Architecture with Spring](#7-real-world-example-layered-architecture-with-spring)
8. [Interview Questions](#8-interview-questions)

---

## 1. Introduction to Spring and IoC (Inversion of Control)

**What is Spring?**
Spring is a comprehensive, modular framework for building Java applications, built around one central idea: **Inversion of Control (IoC)**. Everything else in Spring — dependency injection, AOP, transaction management, MVC — is built on top of or alongside this core principle.

**What is Inversion of Control?**
In traditional programming, an object is responsible for creating and managing its own dependencies:
```java
public class OrderService {
    private PaymentGateway paymentGateway = new StripePaymentGateway(); // OrderService controls creation
}
```
This tightly couples `OrderService` to a specific `PaymentGateway` implementation, making it hard to swap implementations or write isolated unit tests.

With **Inversion of Control**, that responsibility is *inverted* — an external container creates the dependency and hands (injects) it to the object that needs it:
```java
public class OrderService {
    private final PaymentGateway paymentGateway;
    public OrderService(PaymentGateway paymentGateway) { // dependency is INJECTED, not created here
        this.paymentGateway = paymentGateway;
    }
}
```
The object no longer controls *how* its dependency is constructed — that control has been "inverted" to an external party (the Spring **IoC Container**).

**How Spring implements IoC — the container:**
```java
@Configuration
public class AppConfig {
    @Bean
    public PaymentGateway paymentGateway() { return new StripePaymentGateway(); }

    @Bean
    public OrderService orderService(PaymentGateway paymentGateway) {
        return new OrderService(paymentGateway);
    }
}

// The Spring container (ApplicationContext) creates and wires everything together
ApplicationContext context = new AnnotationConfigApplicationContext(AppConfig.class);
OrderService orderService = context.getBean(OrderService.class);
```

**Why this matters — the core benefits:**
- **Loose coupling** — `OrderService` depends on the `PaymentGateway` *interface*, not a concrete class; implementations are swappable without changing `OrderService`.
- **Testability** — unit tests can inject a mock `PaymentGateway` without touching Spring at all.
- **Centralized configuration** — object wiring lives in one place (`@Configuration` classes or component scanning), not scattered across `new` calls throughout the codebase.

**Real-world example:** In a regulatory reporting system, a `ReportGenerator` might depend on a `ReportRepository` interface. In production, Spring injects a JPA-backed implementation; in tests, a mock or in-memory implementation is injected instead — the `ReportGenerator`'s code never changes.

**Q&A**
- **Q: Is Dependency Injection the only way to implement Inversion of Control?**
  A: No — DI is the most common form, but IoC also covers patterns like the Service Locator pattern, template methods, and event-driven callback registration (where a framework calls *your* code rather than you calling the framework). Spring primarily uses DI, but its event system and lifecycle callbacks are also IoC in a broader sense.

---

## 2. Dependency Injection: Constructor vs Setter

### 2.1 Constructor Injection (Recommended)

```java
@Service
public class OrderService {
    private final PaymentGateway paymentGateway; // final - guarantees immutability
    private final InventoryService inventoryService;

    // Since Spring 4.3+, @Autowired is optional on a single constructor
    public OrderService(PaymentGateway paymentGateway, InventoryService inventoryService) {
        this.paymentGateway = paymentGateway;
        this.inventoryService = inventoryService;
    }
}
```

### 2.2 Setter Injection

```java
@Service
public class OrderService {
    private PaymentGateway paymentGateway; // not final - can be reassigned

    @Autowired
    public void setPaymentGateway(PaymentGateway paymentGateway) {
        this.paymentGateway = paymentGateway;
    }
}
```

### 2.3 Field Injection (Discouraged, but Common in Legacy Code)

```java
@Service
public class OrderService {
    @Autowired
    private PaymentGateway paymentGateway; // works, but has real downsides
}
```

**Comparison:**
| Aspect | Constructor Injection | Setter Injection | Field Injection |
|---|---|---|---|
| Immutability | Yes (`final` fields) | No | No |
| Mandatory dependencies | Enforced (won't compile/instantiate without them) | Optional by nature | Not enforced |
| Testability without Spring | Excellent — plain `new` in unit tests | Good | Poor — requires reflection or a Spring context to inject |
| Circular dependency detection | Fails fast at startup | Can silently work (sometimes masking a design smell) | Can silently work |
| Verbosity | More boilerplate for many dependencies | Moderate | Least boilerplate |

**Why constructor injection is the recommended default (Spring team's own guidance):**
```java
// Testable WITHOUT any Spring context or mocking framework magic:
@Test
void testOrderService() {
    PaymentGateway mockGateway = mock(PaymentGateway.class);
    InventoryService mockInventory = mock(InventoryService.class);
    OrderService service = new OrderService(mockGateway, mockInventory); // plain constructor call
    // ... test
}
```

**Real-world example:** A common production bug pattern with field injection — a class instantiated manually somewhere (e.g., in a test, a static factory, or legacy code) outside Spring's container ends up with `null` dependencies, causing `NullPointerException`s that only surface at runtime under specific code paths, rather than failing immediately at application startup the way constructor injection would.

**Q&A**
- **Q: When, if ever, is setter injection appropriate?**
  A: For genuinely optional dependencies that have a sensible default and may be reconfigured after construction (rare in typical business logic, more common in framework/library code) — but for typical service/repository dependencies, constructor injection remains the default recommendation even then.

---

## 3. Spring Bean Lifecycle

```
┌──────────────────┐
│ Instantiation      │  (constructor called)
└────────┬──────────┘
         │
┌────────▼──────────┐
│ Populate Properties │  (dependency injection - setters/fields)
└────────┬──────────┘
         │
┌────────▼──────────┐
│ BeanNameAware,     │  (Aware interfaces called if implemented)
│ BeanFactoryAware,  │
│ ApplicationContext │
│ Aware, etc.        │
└────────┬──────────┘
         │
┌────────▼──────────┐
│ BeanPostProcessor  │  (postProcessBeforeInitialization)
│ (before init)      │
└────────┬──────────┘
         │
┌────────▼──────────┐
│ @PostConstruct /   │  (custom init logic)
│ InitializingBean /  │
│ init-method         │
└────────┬──────────┘
         │
┌────────▼──────────┐
│ BeanPostProcessor  │  (postProcessAfterInitialization - AOP proxies often created here)
│ (after init)        │
└────────┬──────────┘
         │
┌────────▼──────────┐
│   Bean is READY     │  (available for use via getBean() / injection)
└────────┬──────────┘
         │
    ... application runs ...
         │
┌────────▼──────────┐
│ @PreDestroy /       │  (cleanup logic, only for singleton beans on container shutdown)
│ DisposableBean /    │
│ destroy-method       │
└────────────────────┘
```

**Code demonstrating the key hooks:**
```java
@Component
public class DatabaseConnectionManager implements InitializingBean, DisposableBean {

    @Autowired
    private DataSourceProperties properties;

    // Option 1: @PostConstruct (most common, JSR-250 standard annotation)
    @PostConstruct
    public void init() {
        System.out.println("Initializing connection pool with: " + properties.getUrl());
    }

    // Option 2: InitializingBean interface
    @Override
    public void afterPropertiesSet() {
        System.out.println("afterPropertiesSet called");
    }

    // Option 3: destroy-method / @PreDestroy for cleanup
    @PreDestroy
    public void cleanup() {
        System.out.println("Closing connection pool");
    }

    @Override
    public void destroy() {
        System.out.println("destroy() called");
    }
}
```

**Custom `BeanPostProcessor` example (used internally by Spring for things like `@Autowired` processing and AOP proxy creation):**
```java
@Component
public class LoggingBeanPostProcessor implements BeanPostProcessor {
    @Override
    public Object postProcessBeforeInitialization(Object bean, String beanName) {
        System.out.println("Before init: " + beanName);
        return bean;
    }
    @Override
    public Object postProcessAfterInitialization(Object bean, String beanName) {
        System.out.println("After init: " + beanName);
        return bean; // could return a proxy wrapping the original bean here
    }
}
```

**Real-world example:** A `KafkaConsumerManager` bean uses `@PostConstruct` to start consuming from a topic once all its dependencies (deserializer config, topic names from `@Value`) are injected, and `@PreDestroy` to gracefully close consumers and commit offsets on application shutdown — critical for avoiding message loss or duplicate processing during deployments.

**Q&A**
- **Q: Why does Spring call `Aware` interfaces and `BeanPostProcessor`s before `@PostConstruct`?**
  A: `Aware` interfaces (like `ApplicationContextAware`) inject framework-level context the bean might need to complete its own initialization; `BeanPostProcessor.postProcessBeforeInitialization` runs custom logic (like `@Autowired`/`@Value` resolution) that must complete before your own `@PostConstruct` method runs, since your init logic often depends on those injected values already being set.
- **Q: Does `@PreDestroy` get called for prototype-scoped beans?**
  A: No — Spring does not manage the complete lifecycle of prototype beans after creation; it hands them off to the client and does not track them for destruction callbacks. If cleanup is needed for prototype beans, the client code must handle it manually.

---

## 4. ApplicationContext vs BeanFactory

| Aspect | BeanFactory | ApplicationContext |
|---|---|---|
| Bean instantiation | Lazy by default (created only when requested) | Eager by default for singletons (created at startup) |
| Feature set | Basic DI container only | DI + AOP integration, event publishing, internationalization (i18n), environment abstraction, annotation processing |
| Typical usage | Rarely used directly today | The standard choice for virtually all real applications |
| Enterprise features | Minimal | Full — `@Transactional`, `@Scheduled`, `@EventListener`, etc. all depend on `ApplicationContext`-level infrastructure |

```java
// BeanFactory - low-level, rarely used directly in modern applications
BeanFactory factory = new XmlBeanFactory(new ClassPathResource("beans.xml"));
MyBean bean = factory.getBean(MyBean.class); // lazy - bean created HERE, on first request

// ApplicationContext - what virtually every real Spring application uses
ApplicationContext context = new AnnotationConfigApplicationContext(AppConfig.class);
// ALL singleton beans are already created and wired by this point (eager initialization)
MyBean bean2 = context.getBean(MyBean.class); // just retrieves the already-created instance
```

**Why `ApplicationContext`'s eager initialization is usually preferred:**
Configuration errors, missing beans, and circular dependency issues **surface immediately at application startup** rather than lazily at some arbitrary point during runtime — "fail fast" is a deliberate design principle here, especially valuable in production systems where you want to know immediately on deployment if something is misconfigured, not three hours later when a rarely-used code path finally requests that bean.

**Real-world example:** In a Spring Boot microservice, `ApplicationContext` is what's running behind the scenes the entire time — `SpringApplication.run()` creates and refreshes an `ApplicationContext` (typically an `AnnotationConfigServletWebServerApplicationContext` for a web app), which is why a misconfigured `@Bean` method or missing required property causes the application to fail immediately on startup rather than at request time.

**Q&A**
- **Q: If ApplicationContext extends BeanFactory, why would you ever use BeanFactory directly?**
  A: In practice, almost never in modern applications — `BeanFactory` is largely of historical/architectural interest (it's the base interface `ApplicationContext` extends) or used in extremely memory-constrained environments where eager initialization of every singleton isn't affordable. Virtually all Spring Boot and enterprise Spring applications use `ApplicationContext`.

---

## 5. Autowiring and Qualifiers

### 5.1 Basic Autowiring

```java
@Service
public class NotificationService {
    private final EmailSender emailSender;

    @Autowired // resolves by TYPE first
    public NotificationService(EmailSender emailSender) {
        this.emailSender = emailSender;
    }
}
```

### 5.2 The Multiple-Implementations Problem

```java
public interface PaymentGateway { void charge(double amount); }

@Component
public class StripeGateway implements PaymentGateway { /* ... */ }

@Component
public class PayPalGateway implements PaymentGateway { /* ... */ }

@Service
public class OrderService {
    // AMBIGUOUS - Spring can't decide which PaymentGateway bean to inject -> NoUniqueBeanDefinitionException
    @Autowired
    private PaymentGateway paymentGateway;
}
```

### 5.3 Resolving Ambiguity with `@Qualifier`

```java
@Component
@Qualifier("stripe")
public class StripeGateway implements PaymentGateway { /* ... */ }

@Component
@Qualifier("paypal")
public class PayPalGateway implements PaymentGateway { /* ... */ }

@Service
public class OrderService {
    private final PaymentGateway paymentGateway;

    public OrderService(@Qualifier("stripe") PaymentGateway paymentGateway) {
        this.paymentGateway = paymentGateway; // explicitly wires the Stripe implementation
    }
}
```

### 5.4 `@Primary` (Default Choice Among Multiple Candidates)

```java
@Component
@Primary // used whenever no explicit @Qualifier is given
public class StripeGateway implements PaymentGateway { /* ... */ }

@Component
public class PayPalGateway implements PaymentGateway { /* ... */ }

@Service
public class OrderService {
    @Autowired // resolves to StripeGateway automatically due to @Primary
    private PaymentGateway paymentGateway;
}
```

### 5.5 Injecting All Implementations

```java
@Service
public class PaymentGatewayRegistry {
    private final Map<String, PaymentGateway> gateways;

    // Spring injects ALL PaymentGateway beans, keyed by their bean name
    public PaymentGatewayRegistry(Map<String, PaymentGateway> gateways) {
        this.gateways = gateways;
    }

    public PaymentGateway getGateway(String type) {
        return gateways.get(type);
    }
}
```

**Real-world example:** A regulatory reporting system with multiple report generators (`CSSFReportGenerator`, `MiFIDReportGenerator`, `EMIRReportGenerator`) all implementing a common `ReportGenerator` interface — injecting a `Map<String, ReportGenerator>` (keyed by bean name) into a factory/dispatcher class lets you select the correct generator dynamically at runtime based on the report type requested, without a large `if-else`/`switch` chain (a clean application of the Strategy pattern via Spring's DI).

**Q&A**
- **Q: What's the resolution order Spring uses when multiple beans of the same type exist?**
  A: First it checks for an exact `@Qualifier` match if one is specified; if not, it checks for a single `@Primary` bean; if neither resolves the ambiguity (multiple candidates, no primary, no qualifier), Spring throws `NoUniqueBeanDefinitionException` at startup.
- **Q: Difference between `@Autowired` and `@Resource`?**
  A: `@Autowired` (Spring-specific) resolves by type first, then by qualifier/name for disambiguation. `@Resource` (JSR-250, Java standard) resolves by *name* first, then falls back to type — a subtle but occasionally important difference when migrating between DI frameworks or working in JSR-250-compliant environments.

---

## 6. Spring Modules

Spring is not one library but a collection of modules, each addressing a different concern:

| Module | Purpose |
|---|---|
| **Spring Core** | The IoC container itself — `BeanFactory`, `ApplicationContext`, dependency injection machinery |
| **Spring Context** | Builds on Core — adds `ApplicationContext`, internationalization, event propagation, resource loading |
| **Spring AOP** | Aspect-oriented programming — cross-cutting concerns like logging, transactions, security via proxies |
| **Spring DAO** | Consistent exception hierarchy and templates (`JdbcTemplate`) for data access, abstracting away checked `SQLException` |
| **Spring ORM** | Integration with ORM frameworks — Hibernate, JPA — providing session/transaction management integration |
| **Spring Web (MVC)** | Web application framework — `DispatcherServlet`, `@Controller`, `@RestController`, view resolution |
| **Spring Web Reactive (WebFlux)** | Non-blocking, reactive web framework built on Project Reactor, for high-concurrency I/O-bound workloads |
| **Spring Security** | Authentication, authorization, and common web application security concerns |
| **Spring Batch** | Batch processing framework for large-volume, non-interactive data processing jobs |
| **Spring Data** | Simplifies data access across relational, NoSQL, and other data stores via repository abstractions |
| **Spring Cloud** | Tools for building distributed systems/microservices — service discovery, config server, circuit breakers |

**Simplified module dependency diagram:**
```
                    ┌──────────────┐
                    │ Spring Core   │
                    └───────┬──────┘
                            │
                    ┌───────▼──────┐
                    │ Spring       │
                    │ Context       │
                    └───────┬──────┘
          ┌─────────────────┼─────────────────┐
     ┌────▼────┐      ┌─────▼─────┐      ┌────▼─────┐
     │ Spring   │      │ Spring     │      │ Spring    │
     │ AOP      │      │ DAO/ORM    │      │ Web MVC   │
     └──────────┘      └───────────┘      └───────────┘
```

**Q&A**
- **Q: How do Spring Boot "starters" relate to these modules?**
  A: A starter (e.g., `spring-boot-starter-web`, `spring-boot-starter-data-jpa`) is a curated dependency bundle that pulls in the relevant Spring modules plus compatible third-party libraries (like an embedded Tomcat for `-web`, or Hibernate for `-data-jpa`), all at tested-compatible versions — starters are a packaging/dependency-management convenience layered on top of the underlying modules, not a replacement for them.

---

## 7. Real-World Example: Layered Architecture with Spring

**Scenario:** A typical enterprise application structured into Controller → Service → Repository layers, demonstrating IoC/DI, bean scopes, and cross-cutting concerns together.

```java
// ---------- Repository Layer ----------
public interface EmployeeRepository extends JpaRepository<Employee, Long> {
    List<Employee> findByDepartment(String department);
}

// ---------- Service Layer ----------
public interface EmployeeService {
    EmployeeDto getEmployee(Long id);
    EmployeeDto createEmployee(CreateEmployeeRequest request);
}

@Service
@Transactional
public class EmployeeServiceImpl implements EmployeeService {

    private final EmployeeRepository employeeRepository;
    private final NotificationService notificationService; // cross-cutting collaborator

    // Constructor injection - the recommended pattern
    public EmployeeServiceImpl(EmployeeRepository employeeRepository,
                                 NotificationService notificationService) {
        this.employeeRepository = employeeRepository;
        this.notificationService = notificationService;
    }

    @Override
    @Transactional(readOnly = true)
    public EmployeeDto getEmployee(Long id) {
        Employee emp = employeeRepository.findById(id)
                .orElseThrow(() -> new EmployeeNotFoundException(id));
        return EmployeeDto.from(emp);
    }

    @Override
    public EmployeeDto createEmployee(CreateEmployeeRequest request) {
        Employee emp = new Employee(request.getName(), request.getDepartment());
        Employee saved = employeeRepository.save(emp);
        notificationService.notifyNewHire(saved); // decoupled collaborator, injected not constructed
        return EmployeeDto.from(saved);
    }
}

// ---------- Controller Layer ----------
@RestController
@RequestMapping("/api/v1/employees")
public class EmployeeController {

    private final EmployeeService employeeService; // depends on the INTERFACE, not the impl

    public EmployeeController(EmployeeService employeeService) {
        this.employeeService = employeeService;
    }

    @GetMapping("/{id}")
    public ResponseEntity<EmployeeDto> getEmployee(@PathVariable Long id) {
        return ResponseEntity.ok(employeeService.getEmployee(id));
    }

    @PostMapping
    public ResponseEntity<EmployeeDto> createEmployee(@Valid @RequestBody CreateEmployeeRequest request) {
        EmployeeDto created = employeeService.createEmployee(request);
        return ResponseEntity.status(HttpStatus.CREATED).body(created);
    }
}

// ---------- Configuration ----------
@Configuration
@ComponentScan(basePackages = "com.company.employee")
public class AppConfig {
    @Bean
    public NotificationService notificationService(EmailSender emailSender) {
        return new NotificationServiceImpl(emailSender);
    }
}
```

**What this demonstrates:**
- **Each layer depends on abstractions** (`EmployeeService` interface, not `EmployeeServiceImpl` directly) in the controller — this is IoC/DI in action, enabling each layer to be tested and evolved independently.
- **Constructor injection throughout** — every dependency is explicit, immutable, and testable without a Spring context.
- **`@Transactional` at the service layer**, not the controller or repository — the service layer is where business transaction boundaries naturally belong.
- **Cross-cutting concerns** (`NotificationService`) are injected as collaborators rather than instantiated inline, keeping `EmployeeServiceImpl` focused on its core responsibility (Single Responsibility Principle).

**Testing this architecture in isolation:**
```java
@ExtendWith(MockitoExtension.class)
class EmployeeServiceImplTest {
    @Mock private EmployeeRepository employeeRepository;
    @Mock private NotificationService notificationService;
    @InjectMocks private EmployeeServiceImpl employeeService;

    @Test
    void shouldNotifyOnNewHire() {
        CreateEmployeeRequest request = new CreateEmployeeRequest("Avinash", "Engineering");
        Employee saved = new Employee(1L, "Avinash", "Engineering");
        when(employeeRepository.save(any())).thenReturn(saved);

        employeeService.createEmployee(request);

        verify(notificationService).notifyNewHire(saved);
    }
}
```
Notice this test requires **no Spring context at all** — constructor injection means the class can be instantiated directly with mocks, which is exactly why constructor injection is the recommended default (Section 2).

---

## 8. Interview Questions

### Q1: What is IoC and how does Spring implement it?
**A:** Inversion of Control is a design principle where the responsibility for creating and wiring an object's dependencies is moved from the object itself to an external container. Spring implements IoC through its **IoC Container** (`ApplicationContext`), which reads configuration (annotations, Java config, or XML), instantiates beans, resolves their dependencies, and injects them — primarily via **Dependency Injection** (constructor, setter, or field injection).

### Q2: Difference between ApplicationContext and BeanFactory?
**A:** `BeanFactory` is the root interface providing basic DI container functionality with lazy bean instantiation. `ApplicationContext` extends `BeanFactory` and adds enterprise features: eager singleton initialization (fail-fast at startup), AOP integration, event publishing (`ApplicationEventPublisher`), internationalization support, and environment abstraction (profiles, property sources). Virtually all real-world applications use `ApplicationContext`; `BeanFactory` is rarely used directly.

### Q3: Explain bean scopes in Spring.
**A:**
| Scope | Description |
|---|---|
| `singleton` (default) | One instance per Spring container, shared everywhere it's injected |
| `prototype` | A new instance created every time the bean is requested/injected |
| `request` | One instance per HTTP request (web-aware contexts only) |
| `session` | One instance per HTTP session (web-aware contexts only) |
| `application` | One instance per `ServletContext` (web-aware contexts only) |
| `websocket` | One instance per WebSocket session |

```java
@Component
@Scope("prototype")
public class ShoppingCart { /* a new cart instance per injection point */ }
```
A common interview follow-up: **injecting a prototype bean into a singleton bean** only resolves the dependency *once*, at the singleton's creation time — the "prototype" behavior is lost unless you use a `ObjectProvider<T>`/`Provider<T>` or scoped proxy to get a fresh instance on each access.
```java
@Component
public class CartHolder {
    private final ObjectProvider<ShoppingCart> cartProvider;
    public CartHolder(ObjectProvider<ShoppingCart> cartProvider) { this.cartProvider = cartProvider; }
    public ShoppingCart newCart() { return cartProvider.getObject(); } // fresh prototype instance each call
}
```

### Q4: How does Spring manage transactions?
**A:** Spring provides **declarative transaction management** via the `@Transactional` annotation, implemented internally using **AOP proxies**. When a `@Transactional` method is called, the call goes through a proxy that:
1. Starts a transaction (or joins an existing one, based on propagation settings) before the method executes.
2. Invokes the actual method.
3. Commits the transaction if the method completes normally, or rolls back if a `RuntimeException` (or a configured checked exception) is thrown.

```java
@Transactional(propagation = Propagation.REQUIRED, isolation = Isolation.READ_COMMITTED, rollbackFor = Exception.class)
public void transferFunds(Long fromId, Long toId, BigDecimal amount) { /* ... */ }
```
Because this is proxy-based, **self-invocation bypasses it** — calling a `@Transactional` method from another method *within the same class* does not go through the proxy, so the annotation is silently ignored. This is one of the most common real-world Spring transaction bugs.

**Propagation types:**
| Propagation | Behavior |
|---|---|
| `REQUIRED` (default) | Joins existing transaction, or creates a new one if none exists |
| `REQUIRES_NEW` | Always starts a new transaction, suspending any existing one |
| `SUPPORTS` | Joins existing transaction if present, otherwise runs non-transactionally |
| `MANDATORY` | Requires an existing transaction; throws an exception if none exists |
| `NESTED` | Executes within a nested transaction (savepoint) if a transaction exists |
| `NOT_SUPPORTED` | Suspends any existing transaction and runs non-transactionally |
| `NEVER` | Throws an exception if a transaction exists |

### Q5: What's the difference between `@Component`, `@Service`, `@Repository`, and `@Controller`?
**A:** All four are specializations of `@Component` and are functionally identical for bean registration purposes — the distinction is primarily **semantic/documentation-based**, with a few practical differences:
- `@Repository` additionally enables **automatic exception translation** — Hibernate/JDBC-specific exceptions get wrapped into Spring's unchecked `DataAccessException` hierarchy.
- `@Controller`/`@RestController` are recognized by Spring MVC's request-mapping infrastructure specifically.
- `@Service` has no additional technical behavior beyond `@Component` — it's purely a semantic marker for the service layer.

### Q6: What causes a circular dependency in Spring, and how do you resolve it?
**A:** A circular dependency occurs when Bean A depends on Bean B, and Bean B (directly or transitively) depends on Bean A. With **constructor injection**, this fails immediately at startup with a `BeanCurrentlyInCreationException`, since neither bean can be fully constructed first. With **setter/field injection**, Spring can sometimes resolve it by injecting a partially-initialized bean reference — but this is a **design smell** more than a solution. The real fix is almost always to **refactor** — extract the shared responsibility into a third bean that both depend on, or use an event-based/lazy (`@Lazy`) approach only as a last resort.

### Q7: What is the difference between `@Bean` and `@Component`?
**A:** `@Component` (and its specializations) is a class-level annotation used with **component scanning** — Spring discovers and registers the class as a bean automatically. `@Bean` is a method-level annotation used inside a `@Configuration` class to **explicitly** define a bean, typically used when you need fine-grained control over instantiation (e.g., wiring third-party classes you don't own/can't annotate, or when the bean's creation logic is non-trivial).

### Q8: How does `@Value` work, and what are its limitations?
**A:** `@Value("${property.name}")` injects a value from Spring's `Environment` (property files, environment variables, command-line args) directly into a field or constructor parameter. Limitations: it's a compile-time-unchecked string expression (typos in the property key fail silently unless combined with `:defaultValue` syntax or `@Value` validation), and it doesn't group related properties into a cohesive, type-safe object the way `@ConfigurationProperties` does for larger configuration blocks.

---