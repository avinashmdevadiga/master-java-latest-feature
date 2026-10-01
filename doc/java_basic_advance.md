# Java Developer Interview Guide (8 Years Experience)
### Core to Advanced — Concepts, Real-Time Examples & Interview Q&A

---

## Table of Contents
1. [Core Java Basics](#1-core-java-basics)
2. [Advanced OOP & Design Patterns](#2-advanced-oop--design-patterns)
3. [Collections Framework](#3-collections-framework)
4. [Concurrency & Multithreading](#4-concurrency--multithreading)
5. [Java 8+ Features](#5-java-8-features)
6. [Exception Handling](#6-exception-handling)
7. [Memory Management & Garbage Collection](#7-memory-management--garbage-collection)
8. [Serialization & Deserialization](#8-serialization--deserialization)
9. [JDBC & Persistence](#9-jdbc--persistence)
10. [Spring & Spring Boot](#10-spring--spring-boot)
11. [Testing](#11-testing)
12. [Performance & Optimization](#12-performance--optimization)
13. [Security](#13-security)
14. [Interview Preparation — Scenario & Problem Solving](#14-interview-preparation--scenario--problem-solving)

---

## 1. Core Java Basics

### 1.1 JVM, JDK, JRE

| Component | Description |
|---|---|
| **JVM** (Java Virtual Machine) | The runtime engine that executes bytecode. Platform-specific implementation, but bytecode is platform-independent. Contains ClassLoader, Runtime Data Areas (Heap, Stack, Metaspace), and Execution Engine (Interpreter + JIT Compiler). |
| **JRE** (Java Runtime Environment) | JVM + core libraries needed to **run** Java applications. No compiler. |
| **JDK** (Java Development Kit) | JRE + development tools (`javac`, `javadoc`, `jar`, `jdb`, `jshell`). Needed to **write and compile** Java code. |

**Flow:** `.java` → `javac` (compiler) → `.class` (bytecode) → JVM (Class Loader → Bytecode Verifier → Interpreter/JIT) → Machine Code.

**Real-time example:** In production, you ship only a JRE (or a minimal custom runtime via `jlink`) inside Docker images to reduce image size — the JDK's dev tools aren't needed at runtime.

**Q&A**
- **Q: Why is Java called "platform independent" but JVM is not?**
  A: Bytecode is the same on every platform, but each OS/architecture needs its own JVM implementation to translate that bytecode into native machine instructions. "Write once, run anywhere" applies to the code, not the JVM binary.
- **Q: What is JIT compilation and why does it matter for performance?**
  A: The Just-In-Time compiler converts frequently executed bytecode ("hot" methods) into native machine code at runtime, using runtime profiling (HotSpot). This is why long-running Java apps (like Spring Boot services) get faster after warm-up — a key reason load tests should include a warm-up phase.
- **Q: Difference between `jlink` custom runtime and traditional JRE?**
  A: `jlink` creates a minimal, application-specific runtime image containing only the modules the app needs (Java 9+ Modules), reducing footprint — useful for containerized microservices.

---

### 1.2 Data Types, Operators, Control Statements

- **Primitive types:** `byte, short, int, long, float, double, char, boolean` — stored on stack (or inline in objects), fixed size, not objects.
- **Reference types:** Objects, arrays, interfaces — stored on heap, variable holds a reference.
- **Autoboxing/unboxing:** `Integer i = 5;` (boxing) and `int j = i;` (unboxing) — watch for `NullPointerException` when unboxing a `null` wrapper.
- **Operators:** Arithmetic, relational, logical, bitwise, ternary, `instanceof`.
- **Control statements:** `if-else`, `switch` (including Java 14+ switch expressions with arrow syntax and `yield`), loops (`for`, `while`, `do-while`, enhanced `for`).

```java
// Modern switch expression (Java 14+)
String dayType = switch (day) {
    case SATURDAY, SUNDAY -> "Weekend";
    default -> "Weekday";
};
```

**Real-time example:** Using `Integer.valueOf()` caching (-128 to 127) is a classic gotcha — `Integer a = 100; Integer b = 100;` → `a == b` is `true` (cache), but `Integer a = 200; Integer b = 200;` → `a == b` is `false` (different objects). Production bugs from this exact issue are common in comparison logic.

**Q&A**
- **Q: Why does `0.1 + 0.2 != 0.3` in Java?**
  A: `float`/`double` use IEEE 754 binary floating point, which cannot exactly represent most decimal fractions. For monetary calculations (a real production concern in banking/regulatory systems), always use `BigDecimal` with explicit scale and `RoundingMode`.
- **Q: What's the pitfall with unboxing in comparisons?**
  A: `if (someInteger == null)` is safe, but `int x = someInteger;` when `someInteger` is `null` throws `NullPointerException` at unboxing — a common cause of hard-to-trace NPEs in DTOs mapped from database `NULL` columns.

---

### 1.3 OOP Concepts

**Encapsulation** — bundling data and methods, restricting direct access via private fields + public getters/setters.
```java
public class Account {
    private double balance; // hidden state
    public void deposit(double amt) {
        if (amt > 0) balance += amt; // controlled mutation
    }
    public double getBalance() { return balance; }
}
```
*Real-time use:* In a banking/regulatory reporting system (e.g., EMIR trade objects), encapsulation prevents external modules from directly mutating a trade's notional or status — all changes go through validated setters/business methods.

**Inheritance** — code reuse via `extends`. Favor composition over inheritance for flexibility (a common enterprise design review point).
```java
class Employee { protected String name; void work() { System.out.println(name + " works"); } }
class Manager extends Employee { void approveLeave() { /* extra behavior */ } }
```

**Polymorphism**
- *Compile-time (overloading):* Same method name, different signatures.
- *Runtime (overriding):* Subclass provides specific implementation; resolved via dynamic dispatch (vtable lookup).
```java
class Shape { double area() { return 0; } }
class Circle extends Shape { double area() { return Math.PI * r * r; } }
Shape s = new Circle(); s.area(); // runtime polymorphism
```
*Real-time use:* A `ReportGenerator` interface with `CSSFReportGenerator`, `MiFIDReportGenerator`, `EMIRReportGenerator` implementations — the calling service just invokes `generator.generate()` without knowing the concrete type.

**Abstraction** — exposing only essential behavior via abstract classes or interfaces, hiding implementation.
```java
interface PaymentProcessor { void process(Payment p); }
```

**Q&A**
- **Q: Abstract class vs Interface — when do you choose which?**
  A: Use an **interface** for a contract across unrelated classes and for multiple inheritance of type (Java 8+ interfaces can have `default`/`static` methods). Use an **abstract class** when you want to share common state/fields and partial implementation among closely related classes. In enterprise systems, interfaces dominate for service contracts (`Repository`, `Processor`), abstract classes for template-method style base logic.
- **Q: Can you achieve polymorphism without inheritance?**
  A: Yes — via interfaces (interface-based polymorphism) which is the more common pattern in modern Spring apps (`@Service` classes implementing an interface, injected by type).
- **Q: What is the diamond problem and how does Java 8 handle it for interfaces?**
  A: If two interfaces provide the same `default` method, Java forces the implementing class to explicitly override and resolve the conflict — the compiler won't guess.

---

### 1.4 Access Modifiers

| Modifier | Same Class | Same Package | Subclass (diff package) | World |
|---|---|---|---|---|
| `private` | ✅ | ❌ | ❌ | ❌ |
| default (no modifier) | ✅ | ✅ | ❌ | ❌ |
| `protected` | ✅ | ✅ | ✅ | ❌ |
| `public` | ✅ | ✅ | ✅ | ✅ |

**Real-time example:** In layered enterprise apps, repository implementation classes are often package-private, exposed only via a public interface, enforcing dependency inversion and preventing other layers from bypassing the service layer.

---

## 2. Advanced OOP & Design Patterns

### 2.1 SOLID Principles

- **S — Single Responsibility:** A class should have one reason to change. *E.g.,* separate `TradeValidator` from `TradeRepository` — validation logic changing shouldn't force persistence code changes.
- **O — Open/Closed:** Open for extension, closed for modification. *E.g.,* adding a new `ReportFormat` via a new `strategy` implementation instead of editing a giant `if-else`.
- **L — Liskov Substitution:** Subtypes must be substitutable for base types without breaking behavior. *E.g.,* a `ReadOnlyRepository` shouldn't extend a `Repository` interface that declares `save()` if it can't support it — violates LSP.
- **I — Interface Segregation:** Prefer many small, specific interfaces over one large interface. *E.g.,* split a bloated `TradeService` into `TradeReader` and `TradeWriter`.
- **D — Dependency Inversion:** Depend on abstractions, not concrete classes. *E.g.,* `@Autowired` a `PaymentGateway` interface, not a concrete `RazorpayGatewayImpl`, so implementations are swappable.

**Q&A**
- **Q: How do SOLID principles show up in a Spring Boot codebase specifically?**
  A: Constructor injection with interfaces (DIP), `@Service` classes narrowly scoped to one concern (SRP), Strategy beans selected via `@Qualifier`/factory instead of switch statements (OCP), and thin, role-specific interfaces (ISP) like `Pageable`, `Sortable` in Spring Data.
- **Q: Give a real production example where violating OCP caused pain.**
  A: A regulatory report generator with one giant `if (reportType.equals("EMIR")) {...} else if (...)` block — every new regulation (MiFID II amendments, CSSF changes) required editing and re-testing the entire method, increasing regression risk. Refactoring to a `Map<ReportType, ReportGenerator>` strategy registry isolated changes.

### 2.2 Design Patterns

**Creational**

*Singleton* — one instance globally.
```java
public class ConfigManager {
    private static volatile ConfigManager instance;
    private ConfigManager() {}
    public static ConfigManager getInstance() {
        if (instance == null) {
            synchronized (ConfigManager.class) {
                if (instance == null) instance = new ConfigManager();
            }
        }
        return instance;
    }
}
```
*Real-time use:* Spring beans are singleton-scoped by default (managed by the IoC container) — you rarely hand-roll this anymore, but connection pools/config caches still use it.

*Factory* — delegate object creation.
```java
interface Notification { void notifyUser(); }
class EmailNotification implements Notification { public void notifyUser() { /*...*/ } }
class NotificationFactory {
    static Notification create(String type) {
        return switch (type) {
            case "EMAIL" -> new EmailNotification();
            case "SMS" -> new SmsNotification();
            default -> throw new IllegalArgumentException("Unknown type");
        };
    }
}
```

*Builder* — construct complex objects step by step.
```java
Employee emp = Employee.builder().name("Avinash").role("Lead Dev").salary(150000).build();
```
*Real-time use:* Building complex report request DTOs with many optional fields (CSSF/MiFID payloads) — avoids telescoping constructors.

**Structural**

*Adapter* — bridge incompatible interfaces. *E.g.,* wrapping a legacy SOAP client behind a modern REST-facing interface during a microservices migration.

*Decorator* — add behavior dynamically. *E.g.,* wrapping a `PaymentService` with `LoggingPaymentService` and `RetryPaymentService` without modifying the core class.

**Behavioral**

*Observer* — publish/subscribe. *E.g.,* Spring's `ApplicationEventPublisher` — when a trade is booked, publish `TradeBookedEvent`, and multiple listeners (audit, notification, reporting) react independently.
```java
@EventListener
public void onTradeBooked(TradeBookedEvent event) { auditService.log(event); }
```

*Strategy* — interchangeable algorithms. *E.g.,* different fee-calculation strategies per product type, injected at runtime.

**Q&A**
- **Q: Why is double-checked locking with `volatile` needed in Singleton?**
  A: Without `volatile`, due to JVM instruction reordering, another thread could see a partially constructed object (reference assigned before constructor finishes), causing subtle bugs under high concurrency.
- **Q: Singleton vs Spring-managed singleton bean — any difference?**
  A: Spring's "singleton" is per-**ApplicationContext**, not per-JVM — if you have multiple contexts (e.g., in tests or multi-module apps), you can get multiple instances. True JVM-wide singleton pattern is per-classloader.
- **Q: When would you choose Strategy over a simple if-else chain?**
  A: When the number of variants grows or changes frequently (adding new regulatory report types), or when each variant has substantial, testable logic — isolating each as its own class/bean improves testability and adheres to OCP.

---

## 3. Collections Framework

### 3.1 Core Hierarchy

- **List** (ordered, duplicates allowed): `ArrayList` (fast random access, resizable array), `LinkedList` (fast insert/delete, doubly linked list).
- **Set** (no duplicates): `HashSet` (unordered, O(1) avg), `LinkedHashSet` (insertion order), `TreeSet` (sorted, O(log n), backed by Red-Black tree).
- **Map** (key-value): `HashMap`, `LinkedHashMap`, `TreeMap`, `ConcurrentHashMap`.
- **Queue/Deque:** `ArrayDeque`, `PriorityQueue`, `LinkedList` (as Queue).

**Real-time use:** `TreeMap` for maintaining a sorted book of trades by settlement date; `PriorityQueue` for processing highest-priority regulatory alerts first.

### 3.2 HashMap vs ConcurrentHashMap

| Aspect | HashMap | ConcurrentHashMap |
|---|---|---|
| Thread safety | Not thread-safe | Thread-safe |
| Locking | None | Segment/bucket-level locking (Java 8+: CAS + synchronized on bins, no full-map lock) |
| Null keys/values | 1 null key, multiple null values allowed | No null keys or values allowed |
| Iterator | Fail-fast | Weakly consistent (doesn't throw `ConcurrentModificationException`) |
| Performance under concurrency | Degrades / breaks | Scales well |

```java
Map<String, Integer> counts = new ConcurrentHashMap<>();
counts.merge("EMIR", 1, Integer::sum); // atomic increment
```
**Real-time use:** Caching reference data (currency codes, LEI lookups) shared across multiple threads processing regulatory reports concurrently — `ConcurrentHashMap` avoids external synchronization overhead.

### 3.3 Fail-Fast vs Fail-Safe Iterators

- **Fail-fast** (`ArrayList`, `HashMap`): Uses a `modCount` field; throws `ConcurrentModificationException` if the collection is structurally modified during iteration.
- **Fail-safe** (`ConcurrentHashMap`, `CopyOnWriteArrayList`): Iterates over a snapshot or uses safe traversal; doesn't throw, but may not reflect the very latest updates.

```java
List<String> list = new ArrayList<>(List.of("A", "B", "C"));
for (String s : list) {
    if (s.equals("B")) list.remove(s); // throws ConcurrentModificationException
}
// Fix: use Iterator.remove() or removeIf()
list.removeIf(s -> s.equals("B"));
```

### 3.4 Comparable vs Comparator

- **`Comparable<T>`**: Natural ordering, implemented by the class itself (`compareTo`), only one ordering per class.
- **`Comparator<T>`**: External, multiple orderings, ideal with lambdas.

```java
class Employee implements Comparable<Employee> {
    int salary;
    public int compareTo(Employee o) { return Integer.compare(this.salary, o.salary); }
}
employees.sort(Comparator.comparing(Employee::getSalary).reversed().thenComparing(Employee::getName));
```

**Q&A**
- **Q: Why does `ConcurrentHashMap` disallow null keys/values?**
  A: In a concurrent context, `map.get(key) == null` is ambiguous — it could mean "key absent" or "key present with null value," and there's no reliable way to distinguish that atomically across threads, so Doug Lea's design disallows nulls entirely to avoid this ambiguity.
- **Q: How does `HashMap` resolve collisions internally (Java 8+)?**
  A: Buckets store a linked list of entries; if a bucket exceeds 8 entries (and table size ≥ 64), it converts to a red-black tree for O(log n) worst-case lookup instead of O(n).
- **Q: Real scenario — you see intermittent `ConcurrentModificationException` in production logs. How do you diagnose and fix?**
  A: Identify the collection being iterated and modified in the same thread (classic case) or discover a shared mutable `ArrayList` accessed by multiple threads without synchronization (racier case). Fix: use `removeIf`/`Iterator.remove()` for same-thread modification, or replace with `CopyOnWriteArrayList`/`ConcurrentHashMap`-backed structures, or add proper synchronization/immutability for shared state.

---

## 4. Concurrency & Multithreading

### 4.1 Thread Lifecycle

`NEW → RUNNABLE → (BLOCKED / WAITING / TIMED_WAITING) → TERMINATED`

- **NEW:** Thread object created, not started.
- **RUNNABLE:** After `start()`, eligible to run (may be actually running or waiting for CPU).
- **BLOCKED:** Waiting for a monitor lock (`synchronized`).
- **WAITING/TIMED_WAITING:** `wait()`, `join()`, `sleep()`.
- **TERMINATED:** `run()` completed or thread died.

### 4.2 Synchronization, Locks, Executors

```java
// synchronized block - intrinsic lock
public synchronized void increment() { count++; }

// ReentrantLock - explicit, more flexible (tryLock, fairness, interruptible)
private final ReentrantLock lock = new ReentrantLock();
public void increment() {
    lock.lock();
    try { count++; } finally { lock.unlock(); }
}

// ExecutorService - managed thread pools instead of raw Thread creation
ExecutorService executor = Executors.newFixedThreadPool(10);
executor.submit(() -> processTradeBatch(batch));
executor.shutdown();
```

**ForkJoinPool** — for divide-and-conquer parallel tasks (used internally by parallel streams).
```java
ForkJoinPool pool = new ForkJoinPool();
pool.invoke(new RecursiveSumTask(array, 0, array.length));
```

**Real-time use:** In regulatory reporting batch jobs, an `ExecutorService` with a bounded thread pool processes thousands of trade records in parallel while respecting DB connection pool limits — unbounded thread creation (`new Thread()` per record) would exhaust resources.

### 4.3 Java Memory Model (JMM)

- Defines how threads interact through memory — visibility, ordering, atomicity.
- **`volatile`:** guarantees visibility (writes visible to all threads immediately) and prevents instruction reordering around it, but **not** atomicity for compound operations (`count++` is still not atomic).
- **`synchronized`:** guarantees both visibility and atomicity for the guarded block, via entering/exiting a monitor (happens-before relationship).
- **Atomic classes** (`AtomicInteger`, `AtomicLong`): lock-free, CAS-based (Compare-And-Swap) atomic operations.

```java
AtomicInteger processedCount = new AtomicInteger(0);
processedCount.incrementAndGet(); // thread-safe, no explicit lock
```

### 4.4 Real-Time Example: Producer-Consumer

```java
BlockingQueue<Trade> queue = new LinkedBlockingQueue<>(1000);

// Producer
Runnable producer = () -> {
    while (true) {
        Trade trade = fetchNextTrade();
        queue.put(trade); // blocks if full
    }
};

// Consumer
Runnable consumer = () -> {
    while (true) {
        Trade trade = queue.take(); // blocks if empty
        process(trade);
    }
};

ExecutorService pool = Executors.newFixedThreadPool(4);
pool.submit(producer);
pool.submit(consumer);
```
**Real-time use:** File-watcher threads producing incoming EMIR trade messages onto a `BlockingQueue`, while a pool of consumer threads validate and persist them — decouples ingestion rate from processing rate and naturally applies backpressure.

**Q&A**
- **Q: Why is `count++` not thread-safe even though it looks like one operation?**
  A: It's actually three steps — read, increment, write — which can interleave across threads (a lost-update race condition). Use `AtomicInteger` or `synchronized`.
- **Q: When would you choose `ReentrantLock` over `synchronized`?**
  A: When you need `tryLock()` with timeout, interruptible lock acquisition, fairness policies, or multiple condition variables (`Condition` objects) — `synchronized` doesn't support any of these.
- **Q: Explain a deadlock scenario you've debugged and how you resolved it.**
  A: A common real case: Thread A locks `Resource1` then waits for `Resource2`; Thread B locks `Resource2` then waits for `Resource1`. Diagnosed via a thread dump (`jstack`) showing both threads `BLOCKED` on each other's monitors. Fix: always acquire locks in a consistent global order, or use `tryLock()` with timeout and back off/retry instead of blocking indefinitely.
- **Q: What's the difference between `ForkJoinPool` and a regular `ExecutorService` thread pool?**
  A: `ForkJoinPool` uses work-stealing — idle threads "steal" tasks from busy threads' queues, ideal for many small, recursively-splittable tasks (parallel streams, recursive algorithms). Regular executors use a single shared task queue, better for independent, coarser-grained tasks like handling incoming requests.

---

## 5. Java 8+ Features

### 5.1 Streams API, Lambdas, Functional Interfaces

```java
List<Employee> employees = getEmployees();

double avgSalary = employees.stream()
        .filter(e -> e.getDept().equals("Engineering"))
        .mapToDouble(Employee::getSalary)
        .average()
        .orElse(0.0);

Map<String, List<Employee>> byDept = employees.stream()
        .collect(Collectors.groupingBy(Employee::getDept));
```

- **Functional interfaces:** `Function<T,R>`, `Predicate<T>`, `Consumer<T>`, `Supplier<T>`, `BiFunction<T,U,R>` — each has exactly one abstract method (SAM), enabling lambda assignment.
- **Method references:** `Employee::getSalary` as shorthand for a lambda.
- **Parallel streams:** `.parallelStream()` uses `ForkJoinPool.commonPool()` — good for CPU-bound, large datasets; risky for I/O-bound or order-sensitive operations.

### 5.2 Optional

```java
Optional<Employee> emp = repository.findById(id);
String name = emp.map(Employee::getName).orElse("Unknown");
emp.ifPresentOrElse(
    e -> log.info("Found: {}", e.getName()),
    () -> log.warn("Employee not found")
);
```
*Best practice:* Never use `Optional` as a field type or method parameter — it's designed as a **return type** to force callers to handle absence explicitly.

### 5.3 Date & Time API (`java.time`)

```java
LocalDate settlementDate = LocalDate.now().plusDays(2); // T+2 settlement
LocalDateTime timestamp = LocalDateTime.now();
ZonedDateTime cetTime = ZonedDateTime.now(ZoneId.of("Europe/Luxembourg")); // for CSSF reporting
Duration processingTime = Duration.between(start, end);
```
*Real-time use:* CSSF/EMIR reports require precise timezone-aware timestamps (CET/UTC) — `java.time` (immutable, thread-safe) replaced the old mutable, non-thread-safe `Date`/`Calendar`, eliminating a whole class of production bugs from shared mutable date objects.

### 5.4 Real-Time Example: Processing Employee Records with Streams

```java
record Employee(String name, String dept, double salary, int experience) {}

List<Employee> employees = List.of(
    new Employee("Avinash", "Engineering", 1800000, 8),
    new Employee("Priya", "Engineering", 1500000, 5),
    new Employee("Ravi", "Finance", 1200000, 6)
);

Map<String, Double> avgSalaryByDept = employees.stream()
    .collect(Collectors.groupingBy(Employee::dept, Collectors.averagingDouble(Employee::salary)));

List<String> seniorEngineers = employees.stream()
    .filter(e -> e.dept().equals("Engineering") && e.experience() >= 7)
    .map(Employee::name)
    .toList(); // Java 16+ shorthand for collect(toList())
```

**Q&A**
- **Q: Difference between `map()` and `flatMap()`?**
  A: `map()` transforms each element 1:1. `flatMap()` transforms each element into a stream and flattens all resulting streams into one — needed when each input maps to *multiple* outputs, e.g., flattening a `List<List<Trade>>` (trades per client) into a single `Stream<Trade>`.
- **Q: Are Java streams lazily evaluated?**
  A: Yes — intermediate operations (`filter`, `map`) build a pipeline but don't execute until a terminal operation (`collect`, `forEach`, `reduce`) is invoked, enabling short-circuiting (`findFirst`, `anyMatch`) and efficient single-pass processing.
- **Q: When should you avoid parallel streams?**
  A: For small datasets (overhead of splitting/merging outweighs gains), I/O-bound operations (DB calls, REST calls — better handled with async/reactive), or when order matters and you haven't accounted for it, or when the common pool is shared with other latency-sensitive work (can cause thread starvation in a web app).
- **Q: Why was `Optional` not made `Serializable` and why shouldn't it be a field?**
  A: It's intended purely as a method return-type wrapper to express "may be absent" at the API boundary. Using it as a field or parameter adds unnecessary indirection/overhead and isn't idiomatic — a nullable field with clear documentation, or the Null Object pattern, is preferred internally.

---

## 6. Exception Handling

### 6.1 Checked vs Unchecked

- **Checked exceptions** (extend `Exception`, not `RuntimeException`): Must be declared (`throws`) or caught at compile time. E.g., `IOException`, `SQLException`. Represent recoverable conditions the caller should anticipate.
- **Unchecked exceptions** (extend `RuntimeException`): Not enforced by compiler. E.g., `NullPointerException`, `IllegalArgumentException`. Represent programming errors.
- **`Error`** (e.g., `OutOfMemoryError`, `StackOverflowError`): Serious JVM-level issues, generally not meant to be caught/recovered from.

### 6.2 Custom Exceptions

```java
public class TradeValidationException extends RuntimeException {
    private final String tradeId;
    public TradeValidationException(String tradeId, String message) {
        super(message);
        this.tradeId = tradeId;
    }
    public String getTradeId() { return tradeId; }
}

// Usage
if (trade.getNotional() <= 0) {
    throw new TradeValidationException(trade.getId(), "Notional must be positive");
}
```
*Real-time use:* In a regulatory reporting pipeline, custom checked exceptions like `ReportSubmissionException` force calling code to explicitly handle CSSF/EMIR submission failures (retry, dead-letter queue) rather than silently ignoring them.

### 6.3 Best Practices

- Catch specific exceptions, not blanket `catch (Exception e)`.
- Never swallow exceptions silently — at minimum, log with context.
- Use try-with-resources for anything `Closeable` (connections, streams).
- Don't use exceptions for normal control flow (expensive stack trace generation).
- Wrap and rethrow with context, preserving the original cause (`throw new ServiceException("...", e)`).
- Define exception hierarchies per layer (e.g., `RepositoryException`, `ServiceException`) rather than leaking `SQLException` up to controllers.

```java
try (Connection conn = dataSource.getConnection();
     PreparedStatement ps = conn.prepareStatement(SQL)) {
    // auto-closed even on exception
} catch (SQLException e) {
    throw new DataAccessException("Failed to fetch employee", e);
}
```

**Q&A**
- **Q: Why prefer unchecked exceptions in modern Spring applications?**
  A: Checked exceptions force every layer up the call stack to declare or handle them, leading to boilerplate and leaking implementation details across layers. Spring's own exception hierarchy (`DataAccessException`, etc.) wraps checked JDBC/SQL exceptions into unchecked ones, letting you handle them only where meaningful (e.g., a global `@ExceptionHandler`).
- **Q: What happens if an exception is thrown in a `finally` block that itself has a `return`?**
  A: The `finally` block's `return`/exception **suppresses** any exception or return value from the `try`/`catch` — a classic gotcha; avoid `return` or throwing inside `finally`.
- **Q: Scenario — a batch job silently drops failed records with an empty catch block. How do you fix the production issue?**
  A: Replace the empty catch with structured logging (record ID, exception, stack trace), push failed records to a dead-letter table/queue for reprocessing, add metrics/alerting (e.g., failure count per batch), and consider whether the exception should actually be checked to force explicit handling at the call site.

---

## 7. Memory Management & Garbage Collection

### 7.1 Memory Areas

- **Heap:** Object storage. Divided into **Young Generation** (Eden + Survivor spaces S0/S1) and **Old Generation (Tenured)**.
- **Stack:** Per-thread; stores method frames, local variables, partial results. `StackOverflowError` on deep/infinite recursion.
- **Metaspace** (Java 8+, replaced PermGen): Stores class metadata, method info; can grow dynamically (native memory), unlike fixed-size PermGen.

### 7.2 GC Algorithms

| GC | Best For | Characteristics |
|---|---|---|
| **Serial GC** | Small apps, single-core | Single-threaded, stop-the-world |
| **Parallel GC** | Throughput-focused batch jobs | Multi-threaded, still stop-the-world for major GC |
| **G1 (Garbage First)** | Default since Java 9; balanced latency/throughput | Region-based heap, incremental, predictable pause targets |
| **ZGC** | Very large heaps, ultra-low latency (sub-millisecond pauses) | Concurrent, colored pointers, minimal stop-the-world |
| **Shenandoah** | Low-latency, similar goals to ZGC | Concurrent compaction |

```
-XX:+UseG1GC -Xms2g -Xmx4g -XX:MaxGCPauseMillis=200
```

### 7.3 Memory Leaks & Profiling

Common leak causes: unbounded static collections (caches without eviction), unclosed resources, listener/callback references never removed, `ThreadLocal` not cleaned up in thread-pool-based servers (very common in Spring Boot apps using `ThreadLocal` for request context).

**Profiling tools:** VisualVM, JConsole, `jmap`/`jhat`, Eclipse MAT (heap dump analysis), async-profiler, and APM tools (New Relic, Dynatrace) in production.

### 7.4 Real-Time Example: OutOfMemoryError Handling

```java
// Diagnosing OOM in production
// 1. Enable heap dump on OOM at JVM startup:
// -XX:+HeapDumpOnOutOfMemoryError -XX:HeapDumpPath=/var/log/app/heapdump.hprof

// 2. Analyze with Eclipse MAT - look for "dominator tree", suspect leak reports.

// 3. Example root cause: an unbounded in-memory cache
private static final Map<String, ReportPayload> cache = new HashMap<>(); // never evicted!

// Fix: bounded cache with eviction policy
private static final Map<String, ReportPayload> cache =
    Caffeine.newBuilder().maximumSize(10_000).expireAfterWrite(30, TimeUnit.MINUTES).build();
```
**Real-time use:** A production ADER/Priplay batch job was intermittently crashing with `OutOfMemoryError: Java heap space` after long uptimes; heap dump analysis revealed a static `HashMap` accumulating report payloads across runs without eviction. Fix: bounded cache (Caffeine) + monitoring heap usage via Prometheus/Grafana.

**Q&A**
- **Q: Why is Metaspace less likely to cause `OutOfMemoryError: PermGen space` compared to old PermGen?**
  A: Metaspace uses native (off-heap) memory and can grow dynamically by default, whereas PermGen had a small fixed size — though Metaspace can still OOM if class loading is unbounded (e.g., dynamic proxy class generation leak, classloader leaks in app-server redeployments).
- **Q: What's a minor GC vs major/full GC?**
  A: Minor GC cleans the Young Generation (fast, frequent). Major/Full GC cleans the Old Generation (and often the whole heap), typically slower and causes longer pauses — frequent full GCs are a red flag for tuning or leak investigation.
- **Q: How would you tune GC for a low-latency trade-processing service vs a high-throughput nightly batch job?**
  A: Low-latency service → G1 or ZGC with a tight `MaxGCPauseMillis` target, sized heap to avoid promotion pressure. High-throughput batch → Parallel GC, larger heap, since occasional longer pauses are acceptable if overall throughput is maximized.

---

## 8. Serialization & Deserialization

### 8.1 `Serializable`

```java
public class UserSession implements Serializable {
    private static final long serialVersionUID = 1L; // version control for compatibility
    private String userId;
    private transient String tempAuthToken; // excluded from serialization
}
```
- `serialVersionUID` should always be explicit — without it, JVM computes one at runtime based on class structure, and any class change (even a harmless one) breaks deserialization of previously-serialized objects (`InvalidClassException`).
- `transient` fields are skipped during serialization.

### 8.2 `Externalizable`

Gives full manual control over the serialization format (`writeExternal`/`readExternal`), used for performance-critical or custom binary formats where default reflection-based serialization is too slow.

```java
public class FastTrade implements Externalizable {
    public void writeExternal(ObjectOutput out) throws IOException {
        out.writeUTF(tradeId); out.writeDouble(notional);
    }
    public void readExternal(ObjectInput in) throws IOException {
        tradeId = in.readUTF(); notional = in.readDouble();
    }
}
```

### 8.3 JSON/XML Serialization (Jackson, Gson)

```java
ObjectMapper mapper = new ObjectMapper();
mapper.registerModule(new JavaTimeModule()); // for LocalDate/LocalDateTime support
String json = mapper.writeValueAsString(employee);
Employee emp = mapper.readValue(json, Employee.class);

// Common annotations
public class Employee {
    @JsonProperty("emp_name") private String name;
    @JsonIgnore private String internalNotes;
    @JsonFormat(pattern = "yyyy-MM-dd") private LocalDate joinDate;
}
```

### 8.4 Real-Time Example: Saving User Session Data

```java
// Storing session data in Redis as JSON (common in Spring Boot microservices)
@Service
public class SessionService {
    private final RedisTemplate<String, String> redisTemplate;
    private final ObjectMapper mapper = new ObjectMapper();

    public void saveSession(String sessionId, UserSession session) throws JsonProcessingException {
        redisTemplate.opsForValue().set(
            "session:" + sessionId, mapper.writeValueAsString(session), Duration.ofMinutes(30));
    }
}
```
**Real-time use:** For regulatory report payloads exchanged with external CSSF/trade-repository endpoints, JSON (via Jackson) is used for REST APIs, while native Java `Serializable` is largely avoided in modern systems due to security risks (deserialization vulnerabilities) and cross-service/language incompatibility.

**Q&A**
- **Q: Why is native Java serialization considered risky/legacy in modern systems?**
  A: Deserialization of untrusted data can lead to remote code execution (a well-known class of vulnerabilities exploiting `readObject()` gadget chains), it's JVM-language-specific (breaks interoperability with non-Java services), and it's more brittle across versions than JSON. Modern systems prefer JSON/Protobuf/Avro over REST/messaging.
- **Q: Jackson vs Gson — how do you decide?**
  A: Jackson is the de facto standard in the Spring ecosystem (auto-configured in Spring Boot, richer feature set — streaming API, JSON Schema, extensive annotation support, better performance at scale). Gson is simpler and sometimes preferred for lightweight Android/standalone use. In enterprise Spring Boot work, Jackson is almost always the default choice.
- **Q: What breaks if you change a field type in a class that implements `Serializable` without updating `serialVersionUID`?**
  A: Deserializing old serialized data with the new class definition throws `InvalidClassException` due to a UID mismatch (if auto-generated) — explicitly versioning and writing custom `readObject`/backward-compatible logic mitigates this.

---

## 9. JDBC & Persistence

### 9.1 JDBC Basics

```java
try (Connection conn = DriverManager.getConnection(url, user, pass);
     PreparedStatement ps = conn.prepareStatement(
         "SELECT id, name, salary FROM employee WHERE dept = ?")) {
    ps.setString(1, "Engineering");
    try (ResultSet rs = ps.executeQuery()) {
        while (rs.next()) {
            System.out.println(rs.getString("name") + " - " + rs.getDouble("salary"));
        }
    }
}
```
- Always use `PreparedStatement` over `Statement` — prevents SQL injection and enables query plan caching.

### 9.2 Connection Pooling

Raw JDBC connections are expensive to create per request. Pools (HikariCP — default in Spring Boot, Apache DBCP, C3P0) maintain a reusable set of open connections.

```yaml
spring:
  datasource:
    hikari:
      maximum-pool-size: 20
      minimum-idle: 5
      connection-timeout: 30000
      idle-timeout: 600000
```

**Real-time use:** A regulatory batch job processing 100k+ trades hitting the DB per-record without pooling exhausted DB connections under load; switching to a tuned HikariCP pool (sized to DB max_connections and app concurrency) resolved intermittent `Connection is not available` errors.

### 9.3 ORM (Hibernate/JPA)

```java
@Entity
@Table(name = "employee")
public class Employee {
    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    private String name;
    @OneToMany(mappedBy = "employee", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    private List<Payslip> payslips;
}

public interface EmployeeRepository extends JpaRepository<Employee, Long> {
    List<Employee> findByDept(String dept);
    @Query("SELECT e FROM Employee e WHERE e.salary > :min")
    List<Employee> findHighEarners(@Param("min") double min);
}
```
- **Lazy vs Eager fetching:** Lazy loads related entities on-demand (avoids over-fetching, but risks `LazyInitializationException` outside a session/transaction). Eager loads immediately (simpler but can cause N+1 or over-fetch issues).
- **N+1 problem:** Fetching a list of parents then lazily fetching children in a loop triggers N additional queries — fixed via `JOIN FETCH`, `@EntityGraph`, or batch fetching.

### 9.4 Real-Time Example: Employee Database CRUD

```java
@Service
@Transactional
public class EmployeeService {
    private final EmployeeRepository repo;

    public Employee create(Employee e) { return repo.save(e); }

    public Employee update(Long id, EmployeeDto dto) {
        Employee e = repo.findById(id).orElseThrow(() -> new EmployeeNotFoundException(id));
        e.setSalary(dto.getSalary());
        return e; // dirty checking auto-flushes on transaction commit, no explicit save() needed
    }

    public void delete(Long id) { repo.deleteById(id); }
}
```

**Q&A**
- **Q: How does Hibernate's dirty checking work, and why does it matter for the `update()` method above?**
  A: Within an active transaction/session, Hibernate tracks the loaded entity's original state snapshot; on commit/flush, it compares current field values to the snapshot and issues `UPDATE` only for changed fields — no explicit `save()` call is needed for managed entities, which is a common interview trap for people unfamiliar with JPA.
- **Q: How do you solve the N+1 select problem in a real Hibernate/JPA app?**
  A: Use `JOIN FETCH` in JPQL, `@EntityGraph` annotations on repository methods, or batch fetching (`hibernate.default_batch_fetch_size`) — verified via enabling SQL logging (`show-sql`, or better, a tool like p6spy) during code review/load testing.
- **Q: Difference between `save()` and `saveAndFlush()` in Spring Data JPA?**
  A: `save()` may defer the actual SQL execution until the transaction commits or a flush is triggered (bulk operation optimization). `saveAndFlush()` forces an immediate synchronization with the DB — useful when you need the generated ID or DB-side triggers/constraints validated immediately within the same method.
- **Q: How would you diagnose a connection pool exhaustion issue in production?**
  A: Check pool metrics (HikariCP exposes via Micrometer/Actuator — active vs idle vs pending connections), look for long-running transactions or connections not being closed (leak detection: `leak-detection-threshold` in HikariCP config), and correlate with slow queries or external service calls held open inside a transaction.

---

## 10. Spring & Spring Boot

### 10.1 Dependency Injection & AOP

```java
@Service
public class PaymentService {
    private final PaymentGateway gateway; // interface, not concrete class - DIP
    public PaymentService(PaymentGateway gateway) { this.gateway = gateway; } // constructor injection
}
```
- **Constructor injection** is preferred over field injection (`@Autowired` on field) — enables immutability (`final` fields), easier unit testing (no reflection needed to inject mocks), and fails fast if a dependency is missing.
- **Bean scopes:** `singleton` (default), `prototype`, `request`, `session`.

**AOP (Aspect-Oriented Programming)** — cross-cutting concerns (logging, transactions, security) separated from business logic via proxies.
```java
@Aspect
@Component
public class LoggingAspect {
    @Around("@annotation(Loggable)")
    public Object logExecutionTime(ProceedingJoinPoint pjp) throws Throwable {
        long start = System.currentTimeMillis();
        Object result = pjp.proceed();
        log.info("{} executed in {}ms", pjp.getSignature(), System.currentTimeMillis() - start);
        return result;
    }
}
```
*Real-time use:* `@Transactional` itself is implemented via AOP proxies — Spring wraps your bean, starting/committing/rolling back transactions around method boundaries without you writing that logic manually.

### 10.2 REST APIs with Spring Boot

```java
@RestController
@RequestMapping("/api/v1/payments")
public class PaymentController {

    private final PaymentService paymentService;

    @PostMapping
    public ResponseEntity<PaymentResponse> createPayment(@Valid @RequestBody PaymentRequest request) {
        PaymentResponse response = paymentService.process(request);
        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }

    @ExceptionHandler(PaymentValidationException.class)
    public ResponseEntity<ErrorResponse> handleValidation(PaymentValidationException ex) {
        return ResponseEntity.badRequest().body(new ErrorResponse(ex.getMessage()));
    }
}
```

### 10.3 Microservices Concepts

- **Service discovery** (Eureka/Consul), **API Gateway** (Spring Cloud Gateway), **Circuit Breaker** (Resilience4j), **Config Server** (centralized externalized config), **distributed tracing** (Sleuth/Zipkin, or OpenTelemetry).
- **Communication:** synchronous (REST/gRPC) vs asynchronous (Kafka/RabbitMQ events) — asynchronous preferred for decoupling and resilience in regulatory pipelines where downstream systems may be temporarily unavailable.

```java
@CircuitBreaker(name = "paymentGateway", fallbackMethod = "fallbackPayment")
public PaymentResponse callExternalGateway(PaymentRequest req) {
    return restTemplate.postForObject(gatewayUrl, req, PaymentResponse.class);
}
public PaymentResponse fallbackPayment(PaymentRequest req, Throwable t) {
    return PaymentResponse.pending("Gateway unavailable, queued for retry");
}
```

### 10.4 Real-Time Example: Building a Payment Service

```java
@Service
public class PaymentService {
    private final PaymentRepository repository;
    private final PaymentGateway gateway;
    private final ApplicationEventPublisher eventPublisher;

    @Transactional
    public PaymentResponse process(PaymentRequest request) {
        Payment payment = Payment.builder()
            .amount(request.getAmount())
            .status(PaymentStatus.PENDING)
            .build();
        repository.save(payment);

        GatewayResult result = gateway.charge(request); // Strategy pattern: gateway impl injected

        payment.setStatus(result.isSuccess() ? PaymentStatus.SUCCESS : PaymentStatus.FAILED);
        repository.save(payment);

        eventPublisher.publishEvent(new PaymentProcessedEvent(payment.getId(), payment.getStatus()));
        return PaymentResponse.from(payment);
    }
}
```

**Q&A**
- **Q: Why does Spring recommend constructor injection over field injection?**
  A: It makes dependencies explicit and immutable (`final`), allows the class to be instantiated in plain unit tests without a Spring context (just call `new PaymentService(mockGateway)`), and causes bean-creation failures to surface immediately at startup rather than a lazy `NullPointerException` later.
- **Q: How does `@Transactional` actually work under the hood?**
  A: Spring creates a proxy (CGLIB or JDK dynamic proxy) around the bean; calls to `@Transactional` methods go through the proxy, which starts a transaction before the method executes and commits/rolls back after, based on whether an exception (matching configured rollback rules) was thrown. This is why calling a `@Transactional` method from *within the same class* (self-invocation) bypasses the proxy and the annotation is silently ignored — a very common interview trap and real bug source.
- **Q: How would you make a payment call resilient to a flaky downstream gateway?**
  A: Wrap the call with a Circuit Breaker (Resilience4j) to fail fast when the gateway is unhealthy, add a retry policy with exponential backoff for transient failures, define a fallback (queue for later reprocessing), and expose health/metrics via Actuator for alerting.
- **Q: Synchronous REST vs asynchronous messaging for microservices — how do you decide for a payment flow?**
  A: If the caller needs an immediate result (e.g., UI waiting on payment confirmation), synchronous REST with timeouts/circuit breakers fits. If the flow can tolerate eventual processing (e.g., downstream regulatory reporting triggered by a payment event), asynchronous messaging (Kafka) decouples services, improves resilience, and allows independent scaling/retry without blocking the caller.

---

## 11. Testing

### 11.1 JUnit & Mockito

```java
@ExtendWith(MockitoExtension.class)
class PaymentServiceTest {

    @Mock private PaymentRepository repository;
    @Mock private PaymentGateway gateway;
    @InjectMocks private PaymentService paymentService;

    @Test
    void shouldMarkPaymentSuccess_whenGatewayChargeSucceeds() {
        // Arrange
        PaymentRequest request = new PaymentRequest(BigDecimal.valueOf(500));
        when(gateway.charge(request)).thenReturn(GatewayResult.success());

        // Act
        PaymentResponse response = paymentService.process(request);

        // Assert
        assertThat(response.getStatus()).isEqualTo(PaymentStatus.SUCCESS);
        verify(repository, times(2)).save(any(Payment.class)); // saved before & after status update
    }

    @Test
    void shouldThrow_whenAmountIsNegative() {
        PaymentRequest invalid = new PaymentRequest(BigDecimal.valueOf(-10));
        assertThrows(PaymentValidationException.class, () -> paymentService.process(invalid));
    }
}
```

### 11.2 Integration Testing

```java
@SpringBootTest
@AutoConfigureMockMvc
@Testcontainers
class PaymentControllerIntegrationTest {

    @Container
    static PostgreSQLContainer<?> postgres = new PostgreSQLContainer<>("postgres:15");

    @Autowired private MockMvc mockMvc;

    @Test
    void shouldCreatePayment() throws Exception {
        mockMvc.perform(post("/api/v1/payments")
                .contentType(MediaType.APPLICATION_JSON)
                .content("{\"amount\":500}"))
            .andExpect(status().isCreated())
            .andExpect(jsonPath("$.status").value("SUCCESS"));
    }
}
```
*Real-time use:* Testcontainers spins up a real, ephemeral PostgreSQL/Kafka instance in Docker for integration tests, catching issues (SQL dialect quirks, actual constraint violations) that pure mocking would miss — standard practice for regulatory-data services where correctness matters more than test speed.

### 11.3 Best Practices

- Follow AAA pattern: Arrange, Act, Assert.
- Test behavior, not implementation details (avoid over-mocking internal calls).
- Use `@ParameterizedTest` for data-driven test cases instead of copy-pasted test methods.
- Keep unit tests fast/isolated (mocked dependencies); reserve real DB/network calls for integration tests.
- Aim for meaningful coverage of edge cases (nulls, boundary values, exception paths) over chasing a raw coverage percentage.

**Q&A**
- **Q: `@Mock` vs `@Spy` in Mockito — when to use which?**
  A: `@Mock` creates a fully fake object where all methods return defaults unless stubbed. `@Spy` wraps a real object — real methods execute unless explicitly stubbed, useful when you want to verify interactions but still exercise real logic on most methods (e.g., testing a partial override).
- **Q: How do you test a method that depends on `LocalDate.now()` deterministically?**
  A: Inject a `Clock` (Java 8+ `java.time.Clock`) as a dependency instead of calling `LocalDate.now()` directly, and supply a fixed `Clock` in tests (`Clock.fixed(instant, zone)`) — makes date-dependent logic (like T+2 settlement calculations) fully deterministic and testable.
- **Q: Why use Testcontainers instead of an in-memory DB like H2 for integration tests?**
  A: H2 doesn't perfectly replicate production DB (e.g., Postgres/Oracle) SQL dialect, constraints, and functions — subtle bugs (date handling, case sensitivity, specific functions used in regulatory queries) can pass on H2 but fail in production. Testcontainers runs the actual database engine in Docker, closing that gap at the cost of slightly slower test runs.

---

## 12. Performance & Optimization

### 12.1 Caching Strategies

- **Local/in-process cache:** Caffeine, Guava Cache — fast, but not shared across instances.
- **Distributed cache:** Redis, Hazelcast — shared across microservice instances, supports TTL/eviction, essential in horizontally scaled deployments.
- **Cache patterns:** Cache-aside (app checks cache, falls back to DB on miss, populates cache), write-through, write-behind.

```java
@Cacheable(value = "employeeCache", key = "#id")
public Employee findById(Long id) { return repository.findById(id).orElseThrow(); }

@CacheEvict(value = "employeeCache", key = "#employee.id")
public Employee update(Employee employee) { return repository.save(employee); }
```

### 12.2 JVM Tuning

```
-Xms4g -Xmx4g                     # equal min/max heap avoids resizing pauses
-XX:+UseG1GC -XX:MaxGCPauseMillis=200
-XX:+HeapDumpOnOutOfMemoryError
-XX:MetaspaceSize=256m
```
- Always load-test with production-like data volume before tuning blindly.
- Watch GC logs (`-Xlog:gc*`) for pause frequency/duration trends over time.

### 12.3 Profiling Tools

- **VisualVM/JConsole:** Live heap, thread, CPU monitoring — good for local/dev diagnosis.
- **async-profiler:** Low-overhead CPU/allocation flame graphs, safe for production sampling.
- **APM (Application Performance Monitoring):** New Relic, Dynatrace, Elastic APM — distributed tracing across microservices in production.

**Real-time example:** A slow regulatory report generation endpoint was traced via async-profiler flame graph to excessive `String` concatenation inside a loop (`+=` creating many intermediate `String` objects) — replaced with `StringBuilder`, reducing response time significantly under load; separately, adding a Caffeine cache for frequently looked-up static reference data (LEI/currency master data) cut DB round-trips substantially.

**Q&A**
- **Q: Why is setting `-Xms` equal to `-Xmx` often recommended for production services?**
  A: It prevents the JVM from repeatedly resizing the heap at runtime (a costly operation involving OS-level memory allocation), giving more predictable, stable performance — especially important for containerized services with fixed memory limits.
- **Q: How do you decide between a local cache and a distributed cache for a given piece of data?**
  A: If data is small, read-heavy, and can tolerate slight staleness/inconsistency across instances (e.g., static reference/config data), a local cache (Caffeine) is simpler and faster. If data must be consistent across all instances (session data, rate-limit counters) or the dataset is too large for per-instance memory, use a distributed cache (Redis).
- **Q: A REST endpoint is fast in isolation but slow under load — what's your investigation approach?**
  A: Check for connection pool exhaustion (DB/HTTP client pools), thread pool saturation, lock contention (thread dumps under load), N+1 queries only surfacing at scale, and GC pause frequency increasing with heap pressure — load testing (JMeter/Gatling) combined with APM traces usually pinpoints the bottleneck layer.

---

## 13. Security

### 13.1 Authentication & Authorization

- **Authentication:** Verifying identity (who are you) — e.g., username/password, OAuth2/OIDC, JWT.
- **Authorization:** Verifying permission (what can you do) — e.g., role-based access control (RBAC), attribute-based access control (ABAC).

```java
@Configuration
@EnableWebSecurity
public class SecurityConfig {
    @Bean
    public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
        return http
            .authorizeHttpRequests(auth -> auth
                .requestMatchers("/api/v1/admin/**").hasRole("ADMIN")
                .requestMatchers("/api/v1/payments/**").hasAnyRole("USER", "ADMIN")
                .anyRequest().authenticated())
            .oauth2ResourceServer(oauth2 -> oauth2.jwt(Customizer.withDefaults()))
            .build();
    }
}
```

### 13.2 Secure Coding Practices

- Always use `PreparedStatement`/parameterized queries — never string-concatenate SQL.
- Validate and sanitize all external input (`@Valid` + Bean Validation annotations).
- Never log sensitive data (PII, tokens, card numbers) — mask/redact in logging config.
- Store secrets (DB passwords, API keys) in a vault (HashiCorp Vault, AWS Secrets Manager), never hardcoded or in plain application.properties committed to Git.
- Use `BCrypt`/`Argon2` for password hashing — never MD5/SHA1 for passwords, and never plain-text.

```java
@Bean
public PasswordEncoder passwordEncoder() { return new BCryptPasswordEncoder(); }
```

### 13.3 OWASP Guidelines (Top Risks Relevant to Java Backends)

- **Injection** (SQL/NoSQL/LDAP) — mitigated by parameterized queries/ORM.
- **Broken Authentication** — enforce strong session management, MFA where applicable, secure JWT signing (`RS256` over `HS256` for multi-service verification).
- **Sensitive Data Exposure** — encrypt data at rest and in transit (TLS everywhere), avoid over-exposing fields in API responses (DTOs, not raw entities).
- **Broken Access Control** — always enforce authorization server-side, never trust client-side role checks alone.
- **Security Misconfiguration** — disable verbose stack traces/debug endpoints in production, keep dependencies patched (Dependabot/Snyk scanning for CVEs in libraries — very relevant given frequent Log4j-style vulnerabilities).

**Real-time use:** In a regulatory reporting platform handling client trade/financial data, field-level encryption for sensitive attributes, strict RBAC (only authorized roles can trigger CSSF/EMIR submissions), and audit logging of every access/change to reportable data are standard compliance requirements, not optional extras.

**Q&A**
- **Q: Why is exposing JPA entities directly as REST responses considered a security anti-pattern?**
  A: Entities often contain internal fields (audit columns, relationships, sensitive attributes) not meant for external consumption, and Jackson will happily serialize the entire object graph (risking accidental exposure and lazy-loading exceptions). Always map to a dedicated DTO that explicitly whitelists exposed fields.
- **Q: How would you securely store and rotate a third-party API key used by a Spring Boot service?**
  A: Store it in a secrets manager (Vault/AWS Secrets Manager) rather than `application.yml`, inject it at runtime via environment variables or a Spring Cloud Config Vault backend, and design the integration to support key rotation without a redeploy (e.g., reading from a refreshable `@ConfigurationProperties` bean or polling the secrets manager).
- **Q: What's the risk of using `HS256` JWTs across multiple microservices, and how does `RS256` help?**
  A: `HS256` uses a single shared secret for signing and verification — every service that can verify a token can also forge one, which is dangerous once you have many services. `RS256` uses a private/public key pair — only the issuing auth service holds the private (signing) key, while all other services only need the public key to verify, eliminating the shared-secret risk.

---

## 14. Interview Preparation — Scenario & Problem Solving

### 14.1 Frequently Asked Core Questions (Rapid Fire)

| Question | Concise Answer |
|---|---|
| `==` vs `.equals()`? | `==` compares references (or primitive values); `.equals()` compares logical equality (override for custom objects). |
| Why override `hashCode()` when overriding `equals()`? | Contract: equal objects must have equal hash codes, or hash-based collections (`HashMap`, `HashSet`) break (lost entries, duplicate "unique" elements). |
| `String` vs `StringBuilder` vs `StringBuffer`? | `String` is immutable; `StringBuilder` is mutable, not thread-safe, faster; `StringBuffer` is mutable and thread-safe (synchronized methods), slower. |
| Why is `String` immutable in Java? | Security (safe to share as keys/across trust boundaries), thread-safety (safe to share across threads without synchronization), enables the string pool for memory efficiency, and safe hashcode caching. |
| final vs finally vs finalize? | `final` = constant/no-override/no-subclass; `finally` = block always executed after try/catch; `finalize()` = deprecated GC hook, don't rely on it. |
| `this` vs `super`? | `this` refers to current instance; `super` refers to immediate parent class (constructor/method/field access). |
| Static vs instance methods? | Static belongs to the class (no object needed, can't access instance state); instance methods operate on object state, support polymorphism (static methods can't be overridden, only hidden). |
| Can you overload `main()`? | Yes, but JVM only ever calls the exact `public static void main(String[] args)` signature as the entry point. |

### 14.2 Scenario-Based Questions

**Q: Design a thread-safe Singleton without using `synchronized` on every call.**
A: Use the **Bill Pugh / Initialization-on-demand holder** idiom — thread-safe by leveraging the JVM's class-loading guarantees, with no explicit locking overhead after the first access:
```java
public class ConfigManager {
    private ConfigManager() {}
    private static class Holder {
        private static final ConfigManager INSTANCE = new ConfigManager();
    }
    public static ConfigManager getInstance() { return Holder.INSTANCE; }
}
```
The `Holder` class is only loaded (and `INSTANCE` created) on first call to `getInstance()`, and the JVM guarantees class initialization is thread-safe — giving lazy loading without synchronized blocks on every call. (Enum-based singletons are another common, arguably simpler, alternative.)

**Q: You need to process 10 million records from a file without running out of memory. How do you approach it?**
A: Avoid loading the entire file into memory (`Files.readAllLines()` is a trap here). Stream it line-by-line (`Files.lines()` or `BufferedReader`), process and write output incrementally, use batch inserts to the DB (e.g., batches of 1000) rather than one-by-one, and consider a producer-consumer pipeline with bounded queues if processing is CPU-heavy, to parallelize without unbounded memory growth.

**Q: Two microservices need to stay eventually consistent after a payment is processed — how do you design this?**
A: Use the **Outbox pattern** — write the payment state change and an "event to be published" row to the same local DB transaction, then a separate relay process (or Debezium CDC) publishes the event to Kafka reliably, avoiding the dual-write problem (DB commit succeeds but message publish fails, or vice versa). Downstream services consume the event idempotently (dedup by event ID) to handle at-least-once delivery semantics.

**Q: A production API response time degraded from 200ms to 3s over a week, gradually. What's your triage process?**
A:
1. Check recent deployments/config changes correlating with the timeline.
2. Check DB metrics — slow query log, index usage, table growth (a growing table without proper indexing is a classic "gradual degradation" cause).
3. Check GC logs/heap usage trend — a slow memory leak causing increasing GC frequency matches a "gradual" pattern well.
4. Check connection pool saturation as traffic/data grew.
5. Use APM traces to pinpoint which downstream call/DB query is contributing the added latency, rather than guessing.

**Q: How would you migrate a monolithic Java application to microservices with minimal risk?**
A: Use the **Strangler Fig pattern** — incrementally extract bounded-context modules (e.g., start with the least risky, most independent domain like reporting) behind an API gateway/facade, route traffic to the new service gradually, keep the monolith as the source of truth until the new service is proven, and use contract testing (Pact) to ensure compatibility during the transition, rather than a risky "big bang" rewrite.

### 14.3 Real-Time Problem-Solving Example

**Problem:** Detect duplicate trade submissions in a regulatory reporting pipeline (same trade submitted twice due to a retry bug), where each trade has a unique business key (`tradeId + reportingDate`).

```java
public class DuplicateTradeDetector {
    private final Set<String> seenKeys = ConcurrentHashMap.newKeySet(); // thread-safe Set

    public boolean isDuplicate(Trade trade) {
        String key = trade.getTradeId() + "_" + trade.getReportingDate();
        return !seenKeys.add(key); // add() returns false if already present
    }
}
```
*Discussion point for interview:* For a single-instance, in-memory case this works, but in a distributed, horizontally-scaled service, `seenKeys` must be backed by a shared store (Redis `SETNX` / DB unique constraint) so duplicate detection works across instances — a good opportunity to demonstrate awareness of distributed-systems tradeoffs, not just a single-JVM answer.

---

## Final Interview Day Checklist

- Be ready to **whiteboard/live-code** basics: Singleton variants, stream pipelines, a simple producer-consumer.
- Prepare **2–3 real production incident stories** (a memory leak, a deadlock, a performance regression) with a clear structure: symptom → investigation → root cause → fix → prevention.
- Know the **"why" behind defaults** (why constructor injection, why G1 is default, why `PreparedStatement` over `Statement`) — interviewers at the 7–8 YOE level probe reasoning, not just definitions.
- Be ready to discuss **trade-offs**, not just "the right answer" — e.g., consistency vs availability, latency vs throughput GC tuning, checked vs unchecked exceptions.
- Practice explaining **one design pattern end-to-end** with a real project example rather than a textbook definition — depth beats breadth at senior level.

---

*Good luck with your interviews!*