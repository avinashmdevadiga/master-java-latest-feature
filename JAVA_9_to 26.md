# Java 9 to Java 26 Features

## 📖 Introduction

Java has moved from the old "wait 2–3 years for a big release" model to a **predictable six-month release cadence** starting with Java 9 (September 2017). Every six months a new version ships, and every few years one of those versions is marked **LTS (Long-Term Support)** — meaning vendors support it for many years in production. The LTS releases so far are **Java 8, 11, 17, 21, and 25**.

This shift changed how the language itself evolves too. Instead of monolithic features arriving all at once, big ideas like pattern matching, records, and virtual threads are introduced as **preview features**, refined release after release based on real developer feedback, and only "finalized" once the design is proven. Knowing this JEP (JDK Enhancement Proposal) lifecycle — Incubator → Preview → Final — helps you understand *why* a feature took 3–4 versions to become production-ready.

**Why these versions matter for developers and interviews:**

- **Modernization pressure**: Most companies are migrating from Java 8 to Java 17/21, and now planning 21 → 25. Interviewers routinely ask "what changed since Java 8?" to gauge whether you've kept up.
- **Performance & cost**: Features like Virtual Threads, ZGC, and AOT caching directly cut cloud infra costs — a talking point that stands out in interviews for backend/microservices roles.
- **Cleaner code**: Records, pattern matching, sealed classes, and text blocks remove enormous amounts of boilerplate — expect live-coding rounds to expect this syntax now.
- **Concurrency redesign**: Virtual Threads and Structured Concurrency are reshaping how the JVM handles scale, replacing older reactive/thread-pool tricks — a hot topic in senior-level system design interviews.

Below, every major feature from Java 9 through Java 26 is grouped by version, explained in plain language, with the problem it solves, the benefit it brings, a code example, and interview Q&A.

---

## Java 9 (September 2017)

### 1. Java Platform Module System (JPMS / Project Jigsaw) — JEP 200/261/275
**What it is:** Introduces `module-info.java`, letting you split code into modules with explicit `requires` and `exports` declarations, instead of one giant classpath.

**Problem it solves:** Before Java 9, the JDK itself was one huge monolithic `rt.jar`, and the classpath had no real encapsulation — any public class was accessible from anywhere ("JAR hell"). Applications also had to ship a full JRE even if they used a tiny part of it.

**Benefit:** Strong encapsulation, smaller custom runtimes (via `jlink`), and a modularized JDK that starts faster and is more secure.

```java
module com.example.app {
    requires java.sql;
    exports com.example.app.api;
}
```

### 2. JShell (REPL) — JEP 222
**What it is:** An interactive command-line tool to run Java snippets without writing a full class.

**Problem it solves:** Previously you needed a whole project just to test one line of logic.

**Benefit:** Fast experimentation, great for teaching and quick prototyping.

### 3. Other notable Java 9 changes
- **Collection factory methods**: `List.of()`, `Set.of()`, `Map.of()` — create immutable collections concisely.
- **Try-with-resources improvement**: effectively-final variables can be used directly.
- **Private interface methods**: interfaces can now have `private` helper methods to share code between default methods.
- **Stream API additions**: `takeWhile()`, `dropWhile()`, `iterate()` (with predicate), `ofNullable()`.

```java
List<String> names = List.of("A", "B", "C"); // immutable
```

---

## Java 10 (March 2018)

### Local-Variable Type Inference (`var`) — JEP 286
**What it is:** Lets the compiler infer the type of a local variable from the right-hand side.

**Problem it solves:** Verbose declarations like `Map<String, List<Customer>> map = new HashMap<String, List<Customer>>();` cluttered code with repeated type information.

**Benefit:** Less boilerplate, more readable code — but type safety is preserved because `var` is inferred at **compile time**, not dynamically typed like JavaScript's `var`.

```java
var list = new ArrayList<String>(); // inferred as ArrayList<String>
var name = "Avinash";               // inferred as String
```

**Interview tip:** `var` cannot be used for fields, method parameters, or return types — only local variables with an initializer.

---

## Java 11 (September 2018) — LTS

### 1. New `HttpClient` API — JEP 321
**Problem it solves:** The old `HttpURLConnection` was clunky, blocking-only, and hard to use correctly.

**Benefit:** A modern, fluent API with both synchronous and asynchronous (via `CompletableFuture`) support, plus native HTTP/2.

```java
HttpClient client = HttpClient.newHttpClient();
HttpRequest request = HttpRequest.newBuilder()
        .uri(URI.create("https://api.example.com/data"))
        .build();
HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());
```

### 2. String and File API additions
- `String.isBlank()`, `strip()`, `lines()`, `repeat()`
- `Files.readString()`, `Files.writeString()`

### 3. Single-file source-code launcher — JEP 330
Run a `.java` file directly without compiling: `java HelloWorld.java`.

### 4. Removed: Java EE & CORBA modules
Trimmed legacy modules (`java.corba`, `java.xml.ws`, etc.) to reduce JDK bloat.

**Why Java 11 matters most for interviews:** It's the LTS that most legacy Java 8 shops are migrating to (or through, toward 17/21) — expect "migration gotchas" questions (removed modules, module system impact).

---

## Java 12 & 13 (2019)

### Switch Expressions (Preview) — JEP 325 / 354
**Problem it solves:** Traditional `switch` statements were verbose, needed `break` on every branch, and were error-prone (fall-through bugs).

**Benefit:** `switch` can now be an **expression** that returns a value, using arrow syntax.

```java
int numLetters = switch (day) {
    case MONDAY, FRIDAY, SUNDAY -> 6;
    case TUESDAY                -> 7;
    default                     -> 9;
};
```

### Text Blocks (Preview, Java 13) — JEP 355
**Problem it solves:** Embedding multi-line strings (JSON, SQL, HTML) required ugly `+` concatenation and escaped quotes.

**Benefit:** Clean multi-line literals using triple quotes.

```java
String json = """
    {
      "name": "Avinash",
      "role": "Java Full Stack Developer"
    }
    """;
```

---

## Java 14 (March 2020)

### 1. Switch Expressions (Standard) — JEP 361
Finalized from the Java 12/13 preview, with `yield` for multi-statement branches.

### 2. Helpful NullPointerExceptions — JEP 358
**Problem it solves:** A classic `NullPointerException` gave you a stack trace but not *which* variable was null in a chained call like `a.getB().getC().getD()`.

**Benefit:** The JVM now tells you exactly which reference was null.

```
Exception in thread "main" java.lang.NullPointerException:
    Cannot invoke "C.getD()" because the return value of "B.getC()" is null
```

### 3. Records (Preview) — JEP 359
**Problem it solves:** POJOs/DTOs needed hand-written (or Lombok-generated) constructors, `equals()`, `hashCode()`, `toString()`, and getters — dozens of lines for a simple data holder.

**Benefit:** One line gives you an immutable data class with all of that generated automatically.

```java
record Point(int x, int y) {}
```

---

## Java 15 (September 2020)

### 1. Sealed Classes (Preview) — JEP 360
**Problem it solves:** `abstract` classes/interfaces let *any* class extend them — you couldn't restrict the exact hierarchy, which limited exhaustive pattern matching and API control.

**Benefit:** You explicitly declare which classes are allowed to extend/implement a type.

```java
public sealed interface Shape permits Circle, Square, Triangle {}
```

### 2. Text Blocks (Standard) — JEP 378
### 3. Hidden Classes — JEP 371
Used mainly by frameworks (Spring, Hibernate) for dynamically generated classes at runtime, without exposing them in the classloader lookups.

---

## Java 16 (March 2021)

### 1. Records (Standard) — JEP 395
Records became a stable, production-ready feature.

### 2. Pattern Matching for `instanceof` — JEP 394
**Problem it solves:** The classic cast-after-check pattern was repetitive:
```java
if (obj instanceof String) {
    String s = (String) obj; // repetitive re-cast
}
```
**Benefit:** The compiler binds the variable for you.
```java
if (obj instanceof String s) {
    System.out.println(s.length());
}
```

### 3. Vector API (Incubator) — JEP 338
Groundwork for SIMD (Single Instruction, Multiple Data) style vectorized computation for performance-heavy numeric workloads (AI/ML, data processing).

---

## Java 17 (September 2021) — LTS

### 1. Sealed Classes (Standard) — JEP 409
Now stable and heavily used with pattern matching for exhaustive `switch`.

### 2. Pattern Matching for `switch` (Preview) — JEP 406
```java
static String describe(Object obj) {
    return switch (obj) {
        case Integer i -> "int " + i;
        case String s  -> "string " + s;
        default        -> "unknown";
    };
}
```

### 3. Enhanced Pseudo-Random Number Generators — JEP 356
New interfaces (`RandomGenerator`) and algorithms for better statistical quality and jumpable/splittable streams — useful for simulations and parallel random streams.

### 4. Deprecated the Security Manager for removal — JEP 411
Signaled the eventual death of a rarely-used, hard-to-configure sandboxing mechanism.

**Why Java 17 matters most for interviews:** It's the **current dominant enterprise LTS baseline** (many companies, including regulatory/banking systems, run on 17). Expect deep questions on sealed classes + pattern matching combos, and "why skip straight from 8 to 17?" migration reasoning.

---

## Java 18 (March 2022)

### 1. UTF-8 by Default — JEP 400
**Problem it solves:** Default charset used to depend on OS/locale, causing "works on my machine" encoding bugs across environments.

**Benefit:** Predictable behavior everywhere — `Charset.defaultCharset()` is now always UTF-8.

### 2. Simple Web Server — JEP 408
A command-line static file server (`jwebserver`) for quick prototyping/demos — no need to spin up a full app server for static assets.

### 3. `@Snippet` in Javadoc (Preview) — JEP 413

---

## Java 19 (September 2022)

### 1. Virtual Threads (Preview) — JEP 425
**Problem it solves:** Traditional platform threads are OS threads — expensive (~1MB stack each), so you can only run a few thousand concurrently. This forced developers into complex async/reactive programming (`CompletableFuture` chains, reactive streams) just to scale I/O-bound workloads.

**Benefit:** Virtual threads are lightweight, JVM-managed threads — you can spin up **millions** of them. You write simple blocking-style code, and the JVM handles scheduling onto a small pool of OS threads underneath.

```java
try (var executor = Executors.newVirtualThreadPerTaskExecutor()) {
    for (int i = 0; i < 100_000; i++) {
        executor.submit(() -> {
            // looks like a normal blocking call — but scales massively
            Thread.sleep(Duration.ofMillis(100));
            return i;
        });
    }
}
```

### 2. Structured Concurrency (Incubator) — JEP 428
**Problem it solves:** When you fork multiple subtasks onto separate threads, error handling and cancellation across them is manual and error-prone (a failure in one child thread doesn't automatically cancel siblings).

**Benefit:** Treats a group of related tasks as a single unit of work — one scope, one lifecycle, coordinated cancellation and error propagation.

### 3. Record Patterns (Preview) — JEP 405
Destructure a record directly in a pattern:
```java
if (obj instanceof Point(int x, int y)) {
    System.out.println(x + y);
}
```

---

## Java 20 (March 2023)

Mostly a "refinement" release — Virtual Threads (2nd preview), Structured Concurrency (2nd preview), Record Patterns (2nd preview), and Scoped Values (incubator, an immutable alternative to `ThreadLocal` designed to work cleanly with virtual threads).

---

## Java 21 (September 2023) — LTS 🌟

This is one of the most important LTS releases since Java 8 — it **finalizes** the concurrency revolution started in Java 19.

### 1. Virtual Threads (Standard) — JEP 444
Now production-ready. This is a genuine paradigm shift for Java backend scalability — Spring Boot, Tomcat, and other frameworks quickly added virtual-thread support so existing blocking code scales like reactive code, with none of the complexity.

### 2. Pattern Matching for `switch` (Standard) — JEP 441
### 3. Record Patterns (Standard) — JEP 440
```java
record Point(int x, int y) {}

static String classify(Object obj) {
    return switch (obj) {
        case Point(int x, int y) when x == y -> "on the diagonal";
        case Point(int x, int y)             -> "point (" + x + "," + y + ")";
        default                              -> "not a point";
    };
}
```

### 4. Sequenced Collections — JEP 431
**Problem it solves:** There was no common interface to say "this collection has a defined encounter order" — getting the first/last element differed by collection type (`list.get(0)` vs `deque.peekFirst()`...).

**Benefit:** New `SequencedCollection`, `SequencedSet`, `SequencedMap` interfaces provide uniform `getFirst()`, `getLast()`, `reversed()` methods.

### 5. Generational ZGC — JEP 439
Improves the low-latency Z Garbage Collector by adding generational awareness (young vs old objects), drastically cutting GC pause times for large heaps.

### 6. Key Encapsulation Mechanism API (KEM) & Sequenced updates round out this LTS as a major milestone.

**Why Java 21 matters most for interviews:** It's the version companies are actively migrating *to* right now (2025–2026), and Virtual Threads is a top system-design/concurrency interview topic. Be ready to explain **when NOT to use virtual threads** too (CPU-bound work, synchronized blocks pinning carrier threads).

---

## Java 22 (March 2024)

### 1. Unnamed Variables & Patterns — JEP 456
Use `_` when a variable is required syntactically but never used — improves readability of pattern matching and lambda parameters.
```java
if (obj instanceof Point(int x, _)) { // y ignored
    System.out.println(x);
}
```

### 2. Stream Gatherers (Preview) — JEP 461
Lets you define custom intermediate stream operations beyond the built-in `map`/`filter`/`reduce`.

### 3. Foreign Function & Memory API (Standard) — JEP 454
**Problem it solves:** Calling native (C/C++) code previously required JNI — verbose, unsafe, and hard to maintain.

**Benefit:** A safe, pure-Java API to call native libraries and access off-heap memory directly, without JNI boilerplate.

---

## Java 23 (September 2024)

### 1. Primitive Types in Patterns (Preview) — JEP 455
Groundwork for allowing primitives (`int`, `double`, etc.) directly in `instanceof`/`switch` patterns, not just reference types.

### 2. Markdown in Javadoc — JEP 467
Write Javadoc comments using Markdown syntax instead of verbose HTML tags — much easier to write and read.

### 3. Structured Concurrency (3rd Preview), Stream Gatherers (2nd Preview) continue maturing.

---

## Java 24 (March 2025)

### 1. Quantum-Resistant Cryptography — JEP 496 & JEP 497
Introduces Module-Lattice-Based Key Encapsulation (ML-KEM) and Digital Signature (ML-DSA) algorithms — Java's first step toward post-quantum security, anticipating future quantum-computing threats to current encryption.

### 2. Ahead-of-Time Class Loading & Linking (Project Leyden groundwork) — JEP 483
**Problem it solves:** JVM startup and warm-up time is a real cost for short-lived cloud functions/microservices — every JVM start re-does class loading and JIT warm-up from scratch.

**Benefit:** Cache class-loading/linking work from a previous run and reuse it, cutting startup time significantly.

### 3. Stream Gatherers (Standard) — JEP 485
### 4. Generational Shenandoah & Compact Object Headers — reduce memory footprint per object, letting more objects fit in cache and cutting GC overhead.

---

## Java 25 (September 2025) — LTS 🌟

The newest LTS, succeeding Java 21, with **18 JEPs** — 7 of them finalized permanent features.

### 1. Flexible Constructor Bodies (Standard) — JEP 513
**Problem it solves:** You couldn't run any statements before calling `super()`/`this()` in a constructor, forcing awkward static-helper-method workarounds for validation/setup logic.

**Benefit:** Statements (like validation or computing a derived value) can now appear before the `super()`/`this()` call.

```java
class PositiveInt {
    int value;
    PositiveInt(int value) {
        if (value < 0) throw new IllegalArgumentException("must be positive");
        this.value = value; // allowed to set fields before super() runs, under rules
        super();
    }
}
```

### 2. Compact Source Files & Instance Main Methods (Standard) — JEP 512
**Problem it solves:** Even a "Hello World" required `public class`, `static void main(String[] args)` — heavy ceremony for beginners and quick scripts.

**Benefit:** A file can now skip the class declaration entirely.

```java
void main() {
    System.out.println("Hello from Java 25!");
}
```

### 3. Module Import Declarations (Standard) — JEP 511
```java
import module java.base;
import module java.sql;
```
Import an entire module's exported packages in one line instead of listing every package.

### 4. Scoped Values (Standard) — JEP 506
A safer, immutable replacement for `ThreadLocal`, designed to work well with virtual threads and structured concurrency — values are only visible within a well-defined lexical scope, not leaked across thread reuse.

### 5. PEM Encodings of Cryptographic Objects (Preview) — JEP 470
Standard API for encoding/decoding keys and certificates to/from PEM format — previously required third-party libraries like Bouncy Castle for simple PEM handling.

### 6. Generational Shenandoah (Standard) & Compact Object Headers (Standard)
Both promoted from experimental to production-ready, reducing GC pause inconsistency and per-object memory overhead.

### 7. Structured Concurrency (5th Preview), Primitive Types in Patterns (3rd Preview), Stable Values (Preview — JEP 502) continue evolving.

**Why Java 25 matters most for interviews (2026 context):** As the newest LTS, this is *the* version job postings are starting to require. Expect questions on Scoped Values vs ThreadLocal, and how Flexible Constructor Bodies/Compact Source Files reduce boilerplate compared to Java 8/11 style code.

---

## Java 26 (March 2026)

The first non-LTS release after Java 25 — 10 JEPs, 5 finalized, focused on **polishing** rather than big new syntax.

### 1. AOT Cache for Any GC (Standard) — JEP 516
Extends the Ahead-of-Time object cache (from Project Leyden) to work with **any** garbage collector, including ZGC, by storing cached objects in a GC-neutral format — further shrinking cold-start times for cloud-native apps.

### 2. G1 GC Throughput Improvements — JEP 522
Reduces synchronization overhead between application threads and GC threads, giving up to ~15% throughput gains for workloads with heavy object-reference mutation.

### 3. Removal of the Applet API — JEP (cleanup)
Applets have been deprecated since Java 9 and unsupported by any modern browser for years; Java 26 finally removes the API entirely.

### 4. Final Field Mutation Warnings
**Problem it solves:** Reflection could always bypass `final` and mutate a field that was supposed to be immutable — a long-standing loophole that broke a core language guarantee.

**Benefit:** The JVM now emits a warning when this happens, paving the way for it to be blocked entirely in a future release — tightening real immutability guarantees.

### 5. HTTP/3 Support
Adds HTTP/3 (QUIC-based) support to the `HttpClient` API for lower-latency networking.

### 6. Lazy Constants (formerly "Stable Values") (2nd Preview) — renamed & simplified API
**Problem it solves:** `final` fields must be eagerly initialized (slows startup for large apps); mutable fields allow laziness but lose JVM constant-folding optimizations.

**Benefit:** A `LazyConstant` is initialized on first use but still gets full JVM constant-folding once set — best of both worlds.

```java
private static final LazyConstant<Logger> LOGGER =
        LazyConstant.of(() -> Logger.getLogger("com.app"));
```

### 7. PEM Encodings (2nd Preview), Structured Concurrency (6th Preview), Vector API (11th Incubator) continue their long refinement journeys.

**Note:** As a short-term (non-LTS) release, Java 26 is mainly for teams tracking bleeding-edge features; most production shops will wait for **Java 27 (September 2026)** or stay on the **Java 25 LTS** baseline.

---

## 🎯 Interview Questions & Answers

**Q1. What's the real difference between an LTS and a non-LTS Java release?**
A: Every Java version gets the same JEPs merged on the same 6-month cadence, but LTS releases (8, 11, 17, 21, 25) receive years of vendor patches and security updates, while non-LTS releases (like 26) get support only until the next release ships. Enterprises build production baselines on LTS versions and only adopt non-LTS versions for experimentation.

**Q2. Explain Virtual Threads and why they matter.**
A: Virtual threads (JEP 444, finalized in Java 21) are lightweight threads managed by the JVM rather than the OS. Thousands of platform threads would exhaust memory and context-switching capacity, so before virtual threads, high-concurrency I/O-bound servers had to use reactive/async programming models to scale. Virtual threads let you write simple, blocking-style code while the JVM multiplexes many virtual threads onto a small number of OS "carrier" threads — you get reactive-level scalability with imperative-level simplicity. Caveat: they don't help CPU-bound work, and code with `synchronized` blocks can pin a virtual thread to its carrier thread, hurting scalability — use `ReentrantLock` instead in hot paths.

**Q3. What problem do Records solve, and what do you get for free?**
A: Records (finalized in Java 16) eliminate the boilerplate of writing constructors, accessors, `equals()`, `hashCode()`, and `toString()` for simple immutable data carriers. Declaring `record Point(int x, int y) {}` auto-generates all of that. They're ideal for DTOs, and in Java 21+ they also support **record patterns** for destructuring in `switch`/`instanceof`.

**Q4. How does Pattern Matching for `switch` improve on traditional switch statements?**
A: Traditional `switch` only matched on constants (int, enum, String) and required manual casting after `instanceof` checks. Pattern matching (finalized Java 21) lets `switch` match on a value's *type* and destructure it in the same expression, with `when` clauses for extra guard conditions, and the compiler enforces **exhaustiveness** when combined with sealed types — catching missing cases at compile time instead of runtime.

**Q5. What is Structured Concurrency, and how is it different from a plain `ExecutorService`?**
A: A raw thread pool lets you fire off tasks independently, but if one subtask fails, the others aren't automatically cancelled, and error handling across threads is manual and error-prone. Structured Concurrency treats a set of related subtasks as a single unit ("fork many, join once") — a scope owns the child threads, cancels siblings automatically on failure, and propagates errors coherently, which prevents thread leaks and makes concurrent stack traces easier to read.

**Q6. Sealed classes — what problem do they actually solve?**
A: Before sealed classes (Java 17), any class could extend a public `abstract` class or interface from any module, so a library author couldn't guarantee the *complete* set of subtypes — which also blocked the compiler from proving a `switch` over those subtypes was exhaustive. `sealed ... permits A, B, C` restricts the hierarchy to a known, closed set, enabling safe exhaustive pattern matching and clearer API contracts.

**Q7. What's the difference between Scoped Values (Java 25) and ThreadLocal?**
A: `ThreadLocal` is mutable and can live for the entire life of a thread — a real risk with thread-pool reuse (stale data leaking into the next task) and virtual threads (millions of them would multiply that risk). `ScopedValue` is immutable, bound only within a well-defined lexical scope, and automatically cleaned up when that scope exits — safer and cheaper to use at virtual-thread scale.

**Q8. Why did Java move to a 6-month release cadence instead of big multi-year releases?**
A: The old model (e.g., the 3-year gap before Java 8, 3 more years to Java 9) meant huge, risky releases and long waits for small useful features. The 6-month cadence (since Java 9) lets small, well-tested increments ship continuously, while big/risky features go through multiple **preview** cycles (sometimes 4–6 releases) to gather real feedback before being finalized — reducing the risk of shipping a flawed API permanently.

**Q9. What are Compact Object Headers and why do they matter for performance?**
A: Every Java object carries a header (mark word + class pointer) taking up memory even before any real data. Compact Object Headers (finalized in Java 25) shrink this overhead, which reduces per-object memory footprint across the whole heap — meaning more objects fit in CPU cache and garbage collection has less data to scan, improving throughput especially for object-heavy applications.

**Q10. If migrating a Java 8 codebase today, what should you prioritize learning first?**
A: (1) Module system basics (JPMS) since it affects classpath/build tooling even if you don't fully modularize; (2) `var`, text blocks, records, and pattern matching for day-to-day code modernization; (3) Virtual Threads and Structured Concurrency if the app is I/O-heavy (typical microservices/REST backends); (4) sealed classes if you're modeling closed domain hierarchies. This mirrors the real Java 8 → 17/21 → 25 migration path most enterprises (including regulatory/banking systems) are executing right now.

---

## 📚 Further Reading & Resources

- [OpenJDK JEP Index](https://openjdk.org/jeps/0) — the authoritative list of every JEP by number
- [Oracle: JDK Release Notes](https://www.oracle.com/java/technologies/javase/jdk-relnotes-index.html)
- [Baeldung — New Features in Java 25](https://www.baeldung.com/java-25-features)
- [InfoQ — Java 25 Release Coverage](https://www.infoq.com/news/2025/09/java25-released/)
- [InfoQ — Java 26 Release Coverage](https://www.infoq.com/news/2026/03/java26-released/)
- [Oracle Java Blog — The Arrival of Java 26](https://blogs.oracle.com/java/the-arrival-of-java-26)
- [JEP 444 — Virtual Threads](https://openjdk.org/jeps/444)
- [JEP 440/441 — Record Patterns & Pattern Matching for switch](https://openjdk.org/jeps/440)
- [Inside Java (Oracle's official YouTube channel)](https://www.youtube.com/@InsideJava) — "Road to Java XX" video series for every release

---

*Document generated for interview and upskilling preparation — pairs well with a hands-on project (e.g., migrating a small Spring Boot service from Java 11/17 idioms to Java 21/25 idioms: records, sealed classes, pattern matching, and virtual-thread-based controllers).*