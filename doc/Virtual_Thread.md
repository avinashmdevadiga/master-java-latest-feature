# Virtual Threads & Reactive Programming — Architect-Level Mastery Guide

> Audience: Senior/Staff/Principal Engineers, Solution & Java Architects.
> Assumes strong Spring Boot, JPA, Kafka, multithreading, cloud-native background.
> JDK baseline: **Java 21+ (Virtual Threads GA, JEP 444)**, with notes up to Java 25.

---

## Table of Contents

1. [Concurrency Evolution](#1-concurrency-evolution)
2. [Virtual Threads Deep Dive](#2-virtual-threads-deep-dive)
3. [Reactive Programming Deep Dive](#3-reactive-programming-deep-dive)
4. [Virtual Threads vs Reactive](#4-virtual-threads-vs-reactive)
5. [Database Architecture](#5-database-architecture)
6. [Spring Boot 3.x](#6-spring-boot-3x)
7. [System Design](#7-system-design)
8. [Performance Engineering](#8-performance-engineering)
9. [Production Failures](#9-production-failures)
10. [Source Code Internals](#10-source-code-internals)
11. [Architecture Decision Framework](#11-architecture-decision-framework)
12. [Interview Preparation](#12-interview-preparation)
13. [Cheat Sheets & Checklists](#13-cheat-sheets--checklists)

---

## 1. Concurrency Evolution

**Thread-per-request → Platform Threads → Executors → ForkJoinPool → CompletableFuture → Reactive Streams → Project Loom**

### 1.1 The core tension

Server concurrency is a fight between two scarce resources:

- **Memory** — each OS thread reserves a stack (default ~1 MB on HotSpot/Linux, reserved virtual address space, committed lazily).
- **CPU scheduling** — the OS scheduler time-slices runnable threads; more threads → more context switches → more cache pollution and TLB churn.

The whole history below is the industry repeatedly trading *programming model simplicity* against *hardware efficiency*.

### 1.2 Stage-by-stage

| Stage | Model | Scalability ceiling | Memory/req | Context switch | Kernel interaction | Scheduling |
|-------|-------|--------------------|-----------|----------------|--------------------|-----------|
| Thread-per-request | 1 platform thread per request, blocking I/O | ~ thousands (stack memory + scheduler) | ~1 MB stack | Kernel-level, expensive | Every blocking syscall parks the OS thread | OS preemptive, time-sliced |
| Thread Pools (`Executors`) | Bounded pool, queue | Pool size bounds concurrency; queue hides latency | ~1 MB × pool | Same as above but amortized thread creation | Blocking still ties up a pooled thread | OS + pool queueing |
| ForkJoinPool | Work-stealing, divide & conquer | Great for CPU-bound recursive tasks | Shared deques | Low for CPU-bound | `ManagedBlocker` needed for blocking | Work-stealing, LIFO own-deque / FIFO steal |
| CompletableFuture | Async composition over executors | No blocked threads if fully async | Small per-stage closures | Low (callbacks) | Non-blocking only if underlying I/O is async | Executor-driven callbacks |
| Reactive Streams (Reactor/RxJava) | Push-based, backpressure | 100K+ conns on few event-loop threads | ~KB per subscription | Minimal (few event-loop threads) | Non-blocking NIO selectors/epoll | Event loop + Schedulers |
| Project Loom (Virtual Threads) | 1 virtual thread per request, blocking *looks* synchronous | Millions | ~few hundred bytes to KB (heap stack chunks) | Cheap user-mode mount/unmount | JDK intercepts blocking, unmounts carrier | FJP-based cooperative + OS for carriers |

### 1.3 Why thread-per-request hit a wall

- **Little's Law**: `concurrency = throughput × latency`. 10K req/s × 100 ms = 1,000 in-flight. With 500 ms downstream latency → 5,000 threads. At ~1 MB stacks that's ~5 GB of reserved stack and a scheduler managing 5,000 runnables — most of them **parked on I/O**, doing nothing but holding a thread.
- The waste is structural: a platform thread blocked on a socket read is a ~1 MB OS object idling. You scale the *cost* of waiting, not the *work*.

### 1.4 Why async/reactive solved it — and what it cost

Reactive frees the thread during I/O: a handful of event-loop threads multiplex thousands of connections via `epoll`/`kqueue`. But it imposes:

- **Loss of the thread-per-request mental model** — no thread-local context flows naturally; stack traces fragment across operators.
- **Coloring**: every function in the call chain must be async (`Mono`/`Flux`) or you block an event loop and collapse throughput.
- **Debugging/profiling tax** — the call stack no longer represents the logical operation.

### 1.5 Why Loom is the synthesis

Virtual threads give you the **thread-per-request programming model** (blocking, synchronous, thread-locals, clean stack traces, try/catch) with the **scalability of async**. The JDK runtime does the multiplexing that reactive made *you* do by hand.

**Interview discussion points**
- Explain Little's Law applied to connection pools and thread pools.
- Why does adding threads eventually *reduce* throughput? (context-switch + cache + lock contention — the "knee" of the USL / Universal Scalability Law).
- Reactive doesn't give more CPU; it gives better *utilization* by not blocking. Loom gives the same utilization benefit without function coloring.

---

## 2. Virtual Threads Deep Dive

### 2.1 Problem solved

Decouple the **unit of concurrency** (a thread) from the **OS resource** (kernel thread). A virtual thread is a lightweight, JVM-managed, user-mode thread that is *mounted* onto a platform "carrier" thread only while actually executing CPU work, and *unmounted* while blocked.

### 2.2 The building block: Continuations

Virtual threads are built on a (non-public) **delimited continuation** primitive: `jdk.internal.vm.Continuation` + `ContinuationScope`.

- A continuation captures the *rest of a computation* — the Java call stack — so it can be **suspended (yield)** and **resumed (run)** later, possibly on a different carrier.
- `Continuation.yield(scope)` freezes the current stack into the heap; `continuation.run()` thaws it back onto a carrier's stack.

```
VirtualThread.run()
  └─ runContinuation()            // mount: thaw stack onto carrier
       └─ user code ... blocking op
            └─ Continuation.yield(VTHREAD_SCOPE)  // unmount: freeze stack to heap
```

### 2.3 Stack chunking (how stacks live on the heap)

- A platform thread's stack is a fixed, contiguous, kernel-reserved region.
- A virtual thread's stack is stored as **stack chunk objects** (`jdk.internal.vm.StackChunk`) on the **Java heap**. When unmounted, only the *used* frames are retained — often a few hundred bytes to a few KB, **growing on demand**.
- This is why **millions** of virtual threads fit: idle (blocked) virtual threads cost roughly their heap stack chunk, not 1 MB of reserved OS stack. GC can relocate/manage them like any object.
- **Freeze/thaw**: unmount copies live frames from carrier stack → heap chunk (freeze); mount copies back (thaw). Lazy copying minimizes work.

### 2.4 Carrier threads & the scheduler

- Virtual threads run on a dedicated **ForkJoinPool** in **FIFO (asyncMode) mode**, the "virtual thread scheduler".
- Default parallelism = `Runtime.availableProcessors()` (tunable via `jdk.virtualThreadScheduler.parallelism`, `...maxPoolSize`, `...minRunnable`).
- Carrier threads are regular platform threads. A carrier runs one virtual thread at a time; when that VT yields, the carrier picks up another runnable VT.

```
Request
  → Virtual Thread   (millions, cheap, heap stacks)
    → Carrier Thread (ForkJoinPool worker, ~= #cores)
      → OS Thread    (1:1 with carrier)
        → CPU core
```

### 2.5 Mount / Unmount lifecycle

- **Mount**: scheduler assigns a runnable VT to a carrier; thaw stack; execute.
- **Unmount**: VT hits a blocking point the JDK has instrumented (socket I/O, `LockSupport.park`, `BlockingQueue`, `Thread.sleep`, `Future.get`, NIO channels, etc.). The JDK:
  1. Registers a continuation/completion callback,
  2. `yield`s the continuation (freezes stack to heap),
  3. Frees the carrier for other VTs.
- **Unpark/Resume**: when I/O completes (via the JDK's internal poller / `Poller` using epoll/kqueue) or a lock is released, the VT is submitted back to the scheduler and eventually re-mounted on *any* free carrier.

### 2.6 Parking / Unparking

- The entire primitive is `LockSupport.park/unpark`, but **overridden** for virtual threads: instead of parking the OS thread, it `yield`s the continuation.
- `java.util.concurrent` locks (`ReentrantLock`, `Semaphore`, `CountDownLatch`, `ArrayBlockingQueue`, etc.) are built on `LockSupport` → they are **virtual-thread-friendly** and unmount correctly.

### 2.7 Pinning — the critical gotcha

A virtual thread **cannot unmount** (it "pins" the carrier) in two cases:

1. **Inside a `synchronized` block/method** while it blocks — the monitor is tied to the carrier OS thread.
2. When executing a **native frame / foreign function (JNI)** that blocks.

Effect: the carrier is stuck; if many VTs pin simultaneously, you **exhaust carriers** and throughput collapses — effectively back to platform-thread limits.

- **Detect**: `-Djdk.tracePinnedThreads=full|short`, or JFR event `jdk.VirtualThreadPinned`.
- **Fix**: replace `synchronized` with `ReentrantLock` around blocking sections. **Java 24 (JEP 491)** largely removes synchronized pinning — monitors no longer pin — but legacy JDKs (21–23) and native frames still do.

```java
// BAD: pins carrier on blocking I/O inside synchronized
synchronized (lock) {
    byte[] b = socket.getInputStream().readAllBytes(); // blocks while pinned
}

// GOOD: ReentrantLock unmounts cleanly (pre-Java 24)
lock.lock();
try {
    byte[] b = socket.getInputStream().readAllBytes();
} finally {
    lock.unlock();
}
```

### 2.8 JVM internals & memory model

- VTs are **not** pooled and **not** reused — create one per task. Creation is cheap (heap allocation, no syscall).
- **ThreadLocals** work but can be a memory footgun at millions of threads; prefer **`ScopedValue` (JEP 446/481)** for immutable, inheritable, bounded context.
- Memory model (JLS/JMM) is unchanged — happens-before, volatile, locks behave identically. A mount/unmount introduces no reordering surprises; the continuation resume re-establishes the stack state.
- GC: stack chunks are heap objects → more live objects, but tiny; blocked VTs add GC-reachable graph but minimal pressure vs. 1 MB stacks.

### 2.9 Thread scheduling behavior

- VTs are **cooperatively scheduled at blocking/yield points**, not preemptively time-sliced. A CPU-bound VT that never blocks will hold its carrier until done (no fairness preemption among VTs) — so **virtual threads are for I/O-bound, not CPU-bound** workloads.
- The carriers themselves are preemptively scheduled by the OS.

### 2.10 Performance implications

- Massive win for **high-concurrency, I/O-bound** workloads: thousands of concurrent blocking calls, few carriers.
- Little benefit (sometimes slight overhead) for **CPU-bound** work — use a bounded platform-thread pool or ForkJoinPool there.
- Throughput ceiling shifts from "thread count" to **downstream capacity** (DB connections, remote APIs) — now you must size those explicitly (see §5, §9).

### 2.11 Good / Bad / Production-ready

```java
// ❌ BAD: pooling virtual threads defeats the purpose + caps concurrency
ExecutorService pool = Executors.newFixedThreadPool(200, Thread.ofVirtual().factory());

// ❌ BAD: CPU-bound work on virtual threads — no gain, hides real parallelism
Executors.newVirtualThreadPerTaskExecutor().submit(() -> heavyMatrixMultiply());

// ✅ GOOD: one VT per task, structured lifecycle
try (var executor = Executors.newVirtualThreadPerTaskExecutor()) {
    List<Future<Resp>> futures = requests.stream()
        .map(r -> executor.submit(() -> callBlockingService(r)))
        .toList();
    // auto-joins on close()
}

// ✅ PRODUCTION-READY: StructuredTaskScope — fan-out with cancellation & deadline
Response handle(Req req) throws Exception {
    try (var scope = StructuredTaskScope.open(Joiner.allSuccessfulOrThrow())) { // Java 25 API
        var user   = scope.fork(() -> userService.find(req.userId()));
        var orders = scope.fork(() -> orderService.list(req.userId()));
        scope.join();                 // waits all; propagates failure; cancels siblings
        return new Response(user.get(), orders.get());
    }
}
```

> Note: `StructuredTaskScope` API evolved: preview in 21–23 (`ShutdownOnFailure`), reworked in Java 25 (`StructuredTaskScope.open(Joiner...)`). Adjust to your JDK.

**Common mistakes**: pooling VTs; using them for CPU-bound work; `synchronized` around blocking calls; unbounded fan-out hammering a downstream without a semaphore; relying on ThreadLocal at scale.

**Best practices**: one VT per request/task; `ScopedValue` over `ThreadLocal`; bound downstream concurrency with `Semaphore`; prefer `ReentrantLock`; keep `synchronized` blocks tiny and non-blocking; enable pinning diagnostics in staging.

**Interview points**: Explain freeze/thaw and where the stack lives. Why aren't VTs preemptive among themselves? Why does pinning matter and how do you detect it? Why not pool them?

---

## 3. Reactive Programming Deep Dive

### 3.1 Problem solved

Achieve high concurrency with **bounded threads** by never blocking: work is expressed as an **asynchronous, push-based pipeline** with **backpressure** so a fast producer cannot overwhelm a slow consumer.

### 3.2 Reactive Streams — the SPI contract

Four interfaces (JDK `java.util.concurrent.Flow` / `org.reactivestreams`):

```java
interface Publisher<T>     { void subscribe(Subscriber<? super T> s); }
interface Subscriber<T>    { void onSubscribe(Subscription s); void onNext(T t);
                             void onError(Throwable t); void onComplete(); }
interface Subscription     { void request(long n); void cancel(); }
interface Processor<T,R>   extends Subscriber<T>, Publisher<R> {}
```

- **Pull-push hybrid**: the subscriber *pulls* by calling `request(n)` (demand), the publisher *pushes* up to `n` items. This is **backpressure** — demand flows upstream, data flows downstream.
- Strict protocol: `onSubscribe` once; then 0..N `onNext` bounded by total requested; terminated by exactly one `onError`/`onComplete`; serial (no concurrent `onNext`).

### 3.3 Flux & Mono internals (Reactor)

- `Mono<T>` = 0..1 element; `Flux<T>` = 0..N.
- **Assembly time vs subscription time**: building the pipeline (`map`, `filter`, `flatMap`) only *assembles* a chain of `Publisher` decorators. **Nothing runs until `subscribe()`** — then a chain of `Subscriber`s is instantiated upward (the "subscribe" signal travels up), and `request(n)` demand flows up, data flows down.
- Each operator is a `Publisher` that wraps its source and, on subscribe, creates an inner `Subscriber`. This is why stack traces are deep and operator-named (`FluxMap.MapSubscriber`, etc.).
- **Operator fusion**: Reactor optimizes adjacent operators (macro-fusion e.g. combining filters; micro-fusion via `QueueSubscription`) to avoid queue/request overhead.

```
Flux.fromIterable(src)      // source Publisher
    .map(f)                 // FluxMap wraps source
    .filter(p)              // FluxFilter wraps FluxMap
    .flatMap(this::call)    // async boundary, concurrency control
    .subscribe(sub);        // <-- triggers: subscribers chain upward, demand flows up
```

### 3.4 Schedulers (where work runs)

Reactive does **not** imply multithreading; by default everything runs on the subscribing thread. `Scheduler`s move work:

| Scheduler | Backing | Use for |
|-----------|---------|---------|
| `Schedulers.parallel()` | Fixed N = #cores, non-blocking | CPU-bound operators |
| `Schedulers.boundedElastic()` | Capped elastic pool (default 10×cores) | **Wrapping blocking/legacy calls** |
| `Schedulers.single()` | 1 thread | Serial work |
| `Schedulers.immediate()` | caller thread | no-op |
| Netty event loop | `LoopResources`, N = #cores | I/O (WebFlux/WebClient) |

- **`publishOn`** switches the thread for *downstream* operators. **`subscribeOn`** affects where the *source* subscription (and upstream) runs, regardless of position.
- **Golden rule**: never run blocking code on `parallel()` or the Netty event loop — offload to `boundedElastic()`.

### 3.5 Event loop & the network path

WebFlux runs on **Reactor Netty**. A small number of event-loop threads (default = #cores) each own a `Selector`/`epoll` instance multiplexing thousands of sockets.

```
WebClient.get()...retrieve()
  → Reactor Netty channel (connection pool)
    → Netty EventLoop thread
      → Selector/epoll_wait (readiness on thousands of FDs)
        → NIO SocketChannel read (non-blocking)
          → bytes → HTTP decode → onNext up the reactive pipeline
            → your map/flatMap operators (same loop unless publishOn)
              → onComplete / backpressure request(n)
```

### 3.6 Backpressure strategies

When producer > consumer: `onBackpressureBuffer` (queue, risk OOM), `onBackpressureDrop`, `onBackpressureLatest`, `onBackpressureError`. `request(n)` is the native mechanism; operators like `limitRate(n)` shape demand. Hot vs cold publishers differ: cold replays per-subscriber; hot (e.g., `Sinks`) multicasts live.

### 3.7 How WebFlux handles 100K+ connections

- Few event-loop threads + non-blocking sockets ⇒ memory per connection is ~KB (channel + buffers), not ~1 MB/thread. No per-connection thread ⇒ no scheduler/context-switch explosion.
- The constraint becomes **heap for buffers/queues** and **downstream capacity**, plus the absolute rule that **nothing on the event loop may block**.

### 3.8 JVM internals, scheduling, memory

- Reactor objects (operators, subscriptions, queues) are heap-allocated and short-lived ⇒ pressure on the **young gen**; high-throughput pipelines generate garbage (each `onNext` can allocate). Tune allocation, consider operator fusion, avoid boxing.
- Thread scheduling is **cooperative via the event loop**; a single blocking call starves *all* connections on that loop thread (tail-latency cliff).

### 3.9 Common mistakes / best practices

- **Mistakes**: blocking on the event loop (JDBC, `Thread.sleep`, `block()`); forgetting to `subscribe` (nothing happens); losing context (`ThreadLocal` doesn't flow — use Reactor `Context`/`ContextView`); unbounded `flatMap` concurrency; swallowing `onError`.
- **Best practices**: keep the loop non-blocking; `boundedElastic()` for unavoidable blocking; bound `flatMap(…, concurrency)`; propagate context via `contextWrite`; use `checkpoint()`/`Hooks.onOperatorDebug()`/`ReactorDebugAgent` for traceability.

**Interview points**: Explain assembly vs subscription time. What exactly is backpressure and how does `request(n)` implement it? `publishOn` vs `subscribeOn`. Why does one blocking call on the event loop kill throughput?

---

## 4. Virtual Threads vs Reactive

### 4.1 Head-to-head

| Dimension | Virtual Threads | Reactive (WebFlux/Reactor) |
|-----------|-----------------|----------------------------|
| **Programming model** | Imperative, blocking, synchronous — familiar | Declarative, async pipelines — steep curve |
| **CPU** | Same CPU work; cheap mount/unmount in user mode | Efficient; event-loop driven; fusion reduces overhead |
| **Memory** | Heap stack chunks, grows on demand (~hundreds B–KB idle) | ~KB per subscription/connection; GC churn on young gen from operator allocs |
| **Throughput** | Excellent for I/O-bound; ceiling = downstream capacity | Excellent; proven at 100K+ conns on few threads |
| **Latency** | Low; no fairness preemption among VTs; pinning can spike tail | Low until event loop blocks → catastrophic tail |
| **Context switching** | Cheap user-mode unmount; OS switches only among carriers | Minimal — few event-loop threads |
| **Scalability** | Millions of threads | Millions of connections, bounded threads |
| **Backpressure** | **Not built-in** — must bound with Semaphore/queues/pool limits | **First-class** via `request(n)` end-to-end |
| **Debugging** | Normal stack traces, breakpoints, thread dumps work | Fragmented stacks; needs checkpoints/debug agent |
| **Monitoring** | Standard thread/JFR tooling; VT JFR events | Reactor metrics, needs operator-level instrumentation |
| **Profiling** | async-profiler, JFR — stacks are meaningful | Harder — logical op spread across operators/threads |
| **Ecosystem** | Works with existing blocking libs (JDBC, Kafka client) | Needs non-blocking drivers (R2DBC, reactive Kafka) end-to-end |

### 4.2 Internal reasons behind the numbers

- **Why VTs match reactive throughput**: both avoid parking an OS thread on I/O. Reactive uses callbacks + event loop; Loom uses continuation unmount. Same end effect — the OS thread is freed during the wait — achieved at different layers (library vs runtime).
- **Why reactive still wins at backpressure**: Loom has no intrinsic demand signaling. A million VTs each calling a slow service = a million concurrent calls unless *you* add a `Semaphore`. Reactive's `request(n)` propagates demand automatically across the whole chain.
- **Why VTs win on maintainability**: no function coloring, no `.map/.flatMap` gymnastics, thread-per-request debugging, drop-in for blocking libraries.
- **Pinning vs event-loop blocking** are the symmetrical failure modes: a `synchronized` blocking call pins a carrier; a blocking call on an event loop stalls a loop. Both collapse throughput; both are the #1 production incident cause for their model.

### 4.3 Decision summary

- **Choose Virtual Threads** when: workload is request/response + I/O-bound; team is imperative; you rely on JDBC/blocking libraries; you want simplicity and easy debugging. (Most CRUD microservices.)
- **Choose Reactive** when: you need **end-to-end backpressure** (streaming, SSE, high-fanout gateways), fully non-blocking stack already exists, extreme connection counts with strict memory budgets, or composable stream operators are core to the domain.
- **Both can hit the same throughput** for typical microservices; the deciding factors are **backpressure needs, team expertise, and existing drivers** — not raw speed.

**Interview points**: "Are virtual threads faster than reactive?" → Usually comparable throughput for I/O-bound; VTs win on simplicity/debuggability, reactive wins on backpressure and streaming. Explain *why* mechanistically (freeing the OS thread at runtime vs library layer).

---

## 5. Database Architecture

### 5.1 The three models

| Model | Driver | Thread behavior | Best for |
|-------|--------|-----------------|----------|
| Virtual Threads + JDBC | Blocking JDBC (HikariCP) | VT unmounts on socket wait (if not pinned) | Mainstream — keep existing JPA/JDBC code |
| Virtual Threads + Hibernate/JPA | Blocking, same | Same; watch transaction/session scope | Rich domain models, existing JPA investment |
| Reactive + R2DBC | Non-blocking reactive driver | Event loop, no blocked thread | Fully reactive stacks, streaming reads |

### 5.2 Why JDBC scales *better* with Virtual Threads than with platform threads

- With platform threads, concurrency = Tomcat thread pool size (e.g., 200). Each blocking JDBC call ties up one of those 200 OS threads while waiting on the DB socket.
- With virtual threads, 10,000 requests → 10,000 VTs issuing blocking JDBC calls, but each VT **unmounts** while waiting for the DB — carriers are freed. The *application* no longer bottlenecks on thread count.
- **BUT** the real bottleneck moves to the **connection pool**. Blocking JDBC requires one connection held for the duration of the call. 10,000 concurrent queries but a 50-connection Hikari pool ⇒ 9,950 VTs **park on `getConnection()`** (which correctly unmounts) and queue. So VTs remove the *thread* bottleneck but expose the *connection* bottleneck.

```
10K requests → 10K virtual threads
                  │
                  ▼ dataSource.getConnection()  (blocks → VT unmounts, queues)
          HikariCP pool (e.g. 50 connections)  ← the real limit
                  │
                  ▼
             Database (CPU, IO, locks, max_connections)
```

### 5.3 Connection pool behavior & sizing

- **Do not** scale Hikari pool to match VT count. DB connections are expensive (DB memory, backend processes, lock contention). Oversizing the pool overloads the DB.
- Size with the classic formula (PostgreSQL guidance): `connections ≈ ((core_count × 2) + effective_spindle_count)`. Typically 10–50 per instance. Let VTs *queue* on the pool — that's healthy backpressure.
- Set a sane `connectionTimeout`; under VT load, many threads will wait — ensure they fail fast rather than pile up unbounded.
- **Pinning note**: HikariCP historically used `synchronized` internally; on JDK 21–23 this could pin carriers. Use recent Hikari versions and/or JDK 24+ (JEP 491) which removes monitor pinning.

### 5.4 Blocking I/O impact

- JDBC is inherently blocking (the JDBC spec is synchronous). On VTs this is fine: the blocking socket read unmounts the carrier. The cost is only the held *connection*, not a held *thread*.
- Hibernate lazy loading, `@Transactional` boundaries, and session-per-request still apply — keep transactions short so connections return to the pool quickly (critical under high VT concurrency).

### 5.5 When R2DBC is preferable

- You have a **fully non-blocking stack** (WebFlux) and want end-to-end backpressure from DB → HTTP (e.g., streaming large result sets to SSE clients while respecting demand).
- Extremely high connection concurrency where even connection *acquisition* should be non-blocking and demand-driven.
- **Caveats**: R2DBC ecosystem is less mature than JDBC; no rich ORM (Spring Data R2DBC ≠ full JPA — no lazy loading, no dirty checking, limited relationships); complex transactional/joins logic is harder. For most teams, **Virtual Threads + JDBC/JPA** delivers reactive-like scalability with far less complexity.

**Common mistakes**: oversizing the connection pool under VTs; long transactions holding connections; using R2DBC "for performance" without needing backpressure; mixing blocking JDBC into a reactive chain.

**Best practices**: keep pool modest and transactions short; monitor pool wait time & active connections; prefer VT+JDBC unless you need streaming backpressure; load-test to find the DB's real ceiling.

**Interview points**: "If virtual threads remove the thread limit, what's the new bottleneck?" → connection pool + DB capacity. "Why not R2DBC everywhere?" → ecosystem/ORM maturity, complexity vs. marginal benefit when VT+JDBC already frees threads.

---

## 6. Spring Boot 3.x

### 6.1 Three deployment models

| Model | Stack | Thread per request | Blocking allowed? |
|-------|-------|--------------------|--------------------|
| MVC + Platform Threads | Servlet (Tomcat) | 1 pooled OS thread | Yes, bounded by pool |
| MVC + Virtual Threads | Servlet (Tomcat) | 1 virtual thread | Yes, scales massively |
| WebFlux | Reactor Netty | Shared event loop | **No blocking on loop** |

### 6.2 MVC + Platform Threads (classic)

```
HTTP Request
 → Tomcat acceptor → worker thread pool (server.tomcat.threads.max, default 200)
   → DispatcherServlet → @Controller → @Service → @Repository (JDBC blocks worker)
     → DB → response on same worker thread
```
- Concurrency ceiling ≈ `server.tomcat.threads.max`. Beyond that, requests queue in `accept-count`. Blocking downstream latency directly consumes the pool.

### 6.3 MVC + Virtual Threads (Spring Boot 3.2+)

Enable:
```yaml
spring:
  threads:
    virtual:
      enabled: true          # Spring Boot 3.2+ ; requires Java 21+
```
- Spring configures Tomcat to run each request on a **new virtual thread** (`VirtualThreadExecutor`) instead of a pooled worker. `@Async`, `@Scheduled`, and task executors also switch to VTs.
- Same imperative code, same blocking JDBC/JPA — but now ~unlimited concurrent requests, each VT unmounting on I/O.
- `server.tomcat.threads.max` becomes largely irrelevant for request handling (acceptor still exists; the executor is VT-per-task).
- **This is the single biggest "free" scalability win** for existing MVC apps: flip one flag, keep all code, audit for pinning (`synchronized`), size the connection pool.

```
HTTP Request
 → Tomcat acceptor → NEW virtual thread per request
   → @Controller → @Service → @Repository (JDBC blocks → VT unmounts carrier)
     → DB → response ; carrier free for other VTs during the wait
```

### 6.4 WebFlux

```
HTTP Request
 → Netty event loop (N = #cores)
   → @Controller returns Mono/Flux → @Service (reactive) → R2DBC/WebClient (non-blocking)
     → data flows back through reactive pipeline with backpressure → response
```
- No per-request thread. Must be non-blocking end to end. Blocking anywhere on the loop ⇒ throughput collapse (offload to `boundedElastic`).

### 6.5 Key properties

| Property | Meaning |
|----------|---------|
| `server.tomcat.threads.max` / `min-spare` | Platform worker pool size (MVC without VT) |
| `server.tomcat.accept-count` | OS accept queue when pool saturated |
| `spring.threads.virtual.enabled` | Switch MVC + executors to virtual threads (3.2+) |
| `spring.datasource.hikari.maximum-pool-size` | The *real* concurrency limit under VTs |
| `spring.webflux.*` / Netty `reactor.netty.ioWorkerCount` | Event-loop sizing for WebFlux |

### 6.6 Trade-offs & recommendation

- **MVC + Virtual Threads** is the pragmatic default for new and existing Spring Boot apps on Java 21+: near-reactive scalability, imperative simplicity, full JPA/JDBC/observability.
- **WebFlux** when you genuinely need backpressure/streaming or already own a reactive stack.
- Don't forget: **enabling VTs doesn't remove the need to size downstream** (DB pool, HTTP client pools, rate limiters).

**Common mistakes**: enabling VTs but leaving `synchronized` hot paths (pinning); assuming WebFlux is "faster" for CRUD; not re-sizing Hikari; blocking inside WebFlux controllers.

**Interview points**: Trace request→thread for all three models. What does `spring.threads.virtual.enabled` actually change? Why can MVC+VT replace WebFlux for most services? What stays the bottleneck after enabling VTs?

---

## 7. System Design

For each system: **Architecture → Throughput/Latency profile → Bottlenecks → Recommended approach.**

### 7.1 Payment Gateway

- **Profile**: moderate RPS, high value per txn, **strict correctness & idempotency**, synchronous call-outs to banks/card networks (high latency, 100ms–2s), must not double-charge.
- **Architecture**: API → orchestration service (fan-out to fraud, ledger, acquirer) → outbox → event bus. Idempotency keys, saga/compensation, exactly-once semantics on ledger.
- **Bottlenecks**: downstream bank latency; DB row locks on accounts; distributed txn consistency.
- **Recommended**: **MVC + Virtual Threads** — blocking calls to banks unmount carriers; imperative saga code is auditable. Bound each downstream with a `Semaphore`/circuit breaker. Reactive adds complexity without payoff here (low-to-moderate concurrency, correctness-dominated).

### 7.2 Banking / Core Ledger

- **Profile**: ACID-critical, consistency over throughput, heavy RDBMS, long transactions avoided.
- **Architecture**: strong consistency DB, pessimistic/optimistic locking, double-entry ledger, event sourcing for audit.
- **Bottlenecks**: DB lock contention, connection pool, transaction duration.
- **Recommended**: **MVC + Virtual Threads + JDBC/JPA**. Keep transactions short; modest, well-tuned Hikari pool. Backpressure = pool queueing. Avoid reactive (no streaming need, ORM richness needed).

### 7.3 Trading Platform (low-latency)

- **Profile**: ultra-low, predictable **tail latency**; market data fan-out; order matching is CPU/lock sensitive.
- **Architecture**: in-memory matching engine (single-writer, LMAX Disruptor style), separate market-data distribution, kernel-bypass/NIO for feeds.
- **Bottlenecks**: GC pauses, context switches, lock contention, jitter.
- **Recommended**: **Not vanilla VTs or reactive** on the hot path — use **dedicated pinned platform threads**, lock-free ring buffers (Disruptor), mechanical-sympathy design, low-pause GC (ZGC/Shenandoah). VTs fine for the *non-latency-critical* edges (admin, reporting). Market-data push to many clients → reactive/Netty with backpressure.

### 7.4 E-commerce

- **Profile**: spiky traffic (flash sales), read-heavy catalog, write-sensitive inventory/checkout, many downstream calls per request (pricing, inventory, recommendations).
- **Architecture**: CQRS — cached read path (Redis/CDN) + transactional write path; async inventory reservation; event-driven order pipeline.
- **Bottlenecks**: inventory hotspot rows, cache stampede, downstream fan-out latency.
- **Recommended**: **MVC + Virtual Threads** for product/checkout services (parallel fan-out via `StructuredTaskScope`). Reactive/SSE for live stock/price streams if needed. Rate-limit + circuit-break downstreams.

### 7.5 Notification Service (email/SMS/push)

- **Profile**: very high fan-out, I/O-bound to external providers, bursty, backpressure-sensitive (provider rate limits).
- **Architecture**: Kafka ingest → worker consumers → provider adapters with rate limiting & retries → DLQ.
- **Bottlenecks**: provider throttling, retry storms.
- **Recommended**: **Virtual Threads** per message for massive concurrent provider calls, **bounded by per-provider `Semaphore`/rate limiter**. Reactive is also a strong fit due to native backpressure if streaming from Kafka reactively. Either works; VTs are simpler.

### 7.6 Chat Application

- **Profile**: **100K+ persistent connections** (WebSocket/SSE), long-lived, mostly idle, push-heavy, backpressure per slow client.
- **Architecture**: stateful gateway holding connections, pub/sub (Redis/Kafka) fan-out, presence service.
- **Bottlenecks**: connection memory, slow-consumer backpressure, fan-out amplification.
- **Recommended**: **Reactive / Netty (WebFlux + WebSocket)** — purpose-built for massive idle connections with per-subscriber backpressure (`onBackpressureDrop`/buffer). VTs can hold many connections too, but reactive's demand signaling handles slow consumers more naturally for streaming push.

### 7.7 API Gateway

- **Profile**: pure I/O proxy, huge fan-in/out, minimal CPU, backpressure & connection multiplexing critical.
- **Architecture**: non-blocking proxy, connection pooling to upstreams, rate limiting, circuit breaking.
- **Bottlenecks**: upstream latency, connection pool exhaustion, head-of-line blocking.
- **Recommended**: **Reactive (Spring Cloud Gateway on WebFlux/Netty)** — the canonical reactive use case; end-to-end non-blocking streaming with backpressure. VTs viable but reactive is the ecosystem standard here.

**Summary heuristic**: Correctness/transaction-heavy + moderate concurrency → **VTs**. Massive persistent connections / pure streaming proxy / backpressure-critical → **Reactive**. Ultra-low-latency hot path → **specialized platform-thread/lock-free design**.

---

## 8. Performance Engineering

### 8.1 Scaling comparison (I/O-bound request, ~100 ms downstream)

| Users (concurrent) | Platform Threads (pool=200) | Virtual Threads | Reactive |
|--------------------|-----------------------------|-----------------|----------|
| **10K** | Pool saturates; requests queue; latency climbs | Handles; carriers ≈ cores; low heap | Handles easily; low heap |
| **50K** | Severe queueing or OOM if pool grown; thrashing | Handles; heap = Σ stack chunks (tens–hundreds MB) | Handles; few event-loop threads |
| **100K** | Not feasible (200K+ threads → GB stacks, context-switch storm) | Handles if downstream/pool sized; watch pinning | Handles; memory ~KB/conn |
| **500K** | Impossible | Feasible on big heap; GC & downstream become limits | Feasible; most memory-efficient |

### 8.2 What to watch at each layer

- **Heap usage**: Platform threads → off-heap-ish 1 MB stacks (not Java heap but RSS). VTs → on-heap stack chunks grow with concurrency; a request with deep stacks × 500K = real heap. Reactive → operator/queue garbage, lowest steady-state footprint per connection.
- **CPU usage**: comparable for the same work; platform threads add **context-switch overhead** at high counts. VTs mount/unmount in user space (cheap). Reactive minimal switching.
- **GC pressure**: Reactive generates young-gen garbage per `onNext`. VTs add stack-chunk objects + per-task allocation (one Thread object per request). Platform threads: least per-request allocation but huge RSS.
- **Thread dumps**: Platform → readable but thousands of entries. VTs → `jcmd <pid> Thread.dump_to_file -format=json` gives all virtual threads with clean stacks (huge debugging win vs reactive). Reactive → few threads, stacks don't show logical flow.
- **Context switches**: measure with `vmstat`, `pidstat -w`. Platform threads spike; VTs/reactive stay low.
- **Tail latency (p99/p999)**: Platform → degrades as pool queues. VTs → stable unless **pinning** spikes tail. Reactive → stable unless **event-loop blocking** causes cliff.

### 8.3 Tooling

- **JFR** (`-XX:StartFlightRecording`) — `jdk.VirtualThreadStart/End/Pinned/SubmitFailed` events.
- **async-profiler** — flame graphs; works well with VTs (meaningful stacks).
- **Micrometer** — `executor` metrics, Hikari pool metrics (`hikaricp.connections.pending`), Reactor metrics (`.metrics()` + `Micrometer` tap).
- **`jcmd Thread.dump_to_file`** — dump millions of virtual threads.

### 8.4 Methodology

1. Load-test to find the **knee** (where latency rises non-linearly) — that's your real capacity.
2. Identify the **binding constraint**: thread pool? connection pool? downstream? CPU? GC?
3. For VTs: confirm no pinning; size connection/HTTP pools; add bulkheads.
4. For reactive: confirm no blocking on loop; tune `flatMap` concurrency & buffer sizes.
5. Re-test; validate p99/p999 and GC pause distribution, not just averages.

**Interview points**: "At 100K concurrent, compare heap for platform vs virtual vs reactive." "Why can tail latency be worse with VTs than reactive?" (pinning). "What's the binding constraint after you remove the thread limit?"

---

## 9. Production Failures

For each: **Symptoms → Root Cause → Diagnosis → Solution.**

### 9.1 Thread Starvation (pool exhaustion)
- **Symptoms**: requests queue, latency spikes, timeouts, healthy CPU but no throughput.
- **Root cause**: bounded pool (Tomcat/FJP/custom) all threads blocked on slow downstream; no threads to accept work.
- **Diagnosis**: thread dump shows all workers `BLOCKED`/`WAITING` on same resource; pool `active==max`, large queue.
- **Solution**: virtual threads to remove app-thread limit; bulkhead per downstream; timeouts + circuit breakers; separate pools for independent dependencies.

### 9.2 Carrier Thread Exhaustion (VT pinning)
- **Symptoms**: VT app suddenly behaves like a tiny fixed pool; throughput caps at ~#cores; latency balloons under load.
- **Root cause**: VTs **pinned** inside `synchronized` blocks (or native/JNI) during blocking I/O → carriers consumed, scheduler starved.
- **Diagnosis**: `-Djdk.tracePinnedThreads=full`; JFR `jdk.VirtualThreadPinned`; carriers busy while most VTs runnable-but-unscheduled.
- **Solution**: replace `synchronized` with `ReentrantLock` around blocking; upgrade to JDK 24+ (JEP 491); audit libraries (old Hikari, logging, drivers); increase `jdk.virtualThreadScheduler.maxPoolSize` as a stopgap.

### 9.3 Reactive Blocking Calls
- **Symptoms**: WebFlux throughput collapses under load; a few requests stall everything; p99 cliff.
- **Root cause**: blocking call (JDBC, `block()`, `Thread.sleep`, sync HTTP) executed on a Netty event-loop thread → that loop (and all its connections) stalls.
- **Diagnosis**: BlockHound flags it; thread dump shows event-loop thread in blocking call; few `reactor-http-nio-*` threads stuck.
- **Solution**: offload blocking to `Schedulers.boundedElastic()`; use non-blocking drivers (R2DBC, WebClient); add BlockHound in tests to fail fast.

### 9.4 Scheduler Misuse
- **Symptoms**: unexpected thread, `boundedElastic` saturation, OOM from unbounded elastic, or CPU-bound work on event loop.
- **Root cause**: wrong `publishOn`/`subscribeOn`; running CPU work on `boundedElastic`; blocking on `parallel()`.
- **Diagnosis**: inspect scheduler thread names in dumps; `boundedElastic` queue growth metric.
- **Solution**: `parallel()` for CPU, `boundedElastic()` for blocking only; understand `publishOn` (downstream) vs `subscribeOn` (source); cap elastic size.

### 9.5 Connection Pool Exhaustion
- **Symptoms**: `getConnection` timeouts (`SQLTransientConnectionException`), latency spikes, cascading failures.
- **Root cause**: more concurrent queries than pool size (classic under VTs!), long transactions, connection leaks (unclosed), N+1 queries.
- **Diagnosis**: `hikaricp.connections.pending` > 0, `active == max`; leak detection (`leakDetectionThreshold`); slow-query logs.
- **Solution**: size pool to DB capacity (not request count); shorten transactions; fix leaks; add per-endpoint concurrency limits so VTs queue gracefully; read replicas/caching.

### 9.6 Deadlocks
- **Symptoms**: subset of requests hang forever; threads stuck pairwise.
- **Root cause**: lock-ordering violations; VTs + limited carriers + inter-VT dependency where a pinned VT holds a resource a carrier needs; bounded executor where tasks wait on sibling tasks in same pool.
- **Diagnosis**: thread dump deadlock detector; look for circular `WAITING`; check for `synchronized` + VT pinning combos.
- **Solution**: consistent lock ordering; `tryLock` with timeout; avoid parent/child tasks sharing a bounded pool; structured concurrency to bound lifecycles.

### 9.7 Memory Leaks
- **Symptoms**: steady heap growth, rising GC, eventual OOM.
- **Root cause**: `ThreadLocal` not cleared (worse historically with pooled threads; with per-task VTs the Thread dies so TL is collected — but caching VTs reintroduces it); unbounded reactive buffers (`onBackpressureBuffer()`); unbounded queues; retained subscriptions; `Sinks` without cleanup.
- **Diagnosis**: heap dump + dominator tree (MAT); watch `onBackpressureBuffer` sizes; monitor live `Thread` count.
- **Solution**: bound all buffers/queues; prefer `ScopedValue`; don't pool/cache VTs; cancel/dispose subscriptions; set backpressure to drop/error where appropriate.

**Interview points**: Given symptoms, name the root cause and the exact diagnostic command. Why does pinning masquerade as a tiny thread pool? Why does enabling VTs expose connection-pool exhaustion?

---

## 10. Source Code Internals

### 10.1 `java.lang.VirtualThread`

- Package-private subclass of `Thread`. Key fields: a `Continuation`, a reference to the **scheduler** (default FJP), a volatile `state` (NEW, STARTED, RUNNING, PARKING, PARKED, PINNED, YIELDING, TERMINATED), and the current `carrierThread` (null when unmounted).
- `run()` wraps the task in a `Runnable` passed to the continuation. `runContinuation()` is the scheduled task the carrier executes (mount).
- `park()`/`unpark()` override `Thread`/`LockSupport` semantics: parking calls `yieldContinuation()` (unmount) instead of blocking the OS thread; unpark re-submits the VT to its scheduler.
- `yieldContinuation()` → `Continuation.yield(VTHREAD_SCOPE)`; on success the carrier is released. If yield fails (pinned), it **blocks on the carrier** (the pinning fallback).
- Mount/unmount update `carrierThread` and the carrier's `jdk.internal.vm.Continuation` link; `Thread.currentThread()` always returns the *virtual* thread to user code.

### 10.2 `StructuredTaskScope`

- Treats a group of concurrent subtasks as a single unit (structured concurrency): all forks must complete before the scope closes; failure/cancellation propagates.
- `fork(Callable)` → starts a child **virtual thread**, returns a `Subtask`/handle. `join()` blocks (unmounting) until the joining policy is satisfied.
- **Joiners/policies**: fail-fast (`allSuccessfulOrThrow` / old `ShutdownOnFailure`) cancels siblings on first error; `anySuccessfulResultOrThrow` / `ShutdownOnSuccess` cancels rest on first success (racing).
- Cancellation interrupts the sibling virtual threads; the tree-structured lifecycle guarantees no leaked threads. Pairs with `ScopedValue` for context inheritance.

### 10.3 `ForkJoinPool`

- Work-stealing pool; each worker has a **double-ended queue (deque)**. Own tasks pushed/popped **LIFO** (cache-hot); idle workers **steal FIFO** from others' tails.
- The VT scheduler is an FJP created in **asyncMode=true (FIFO)** so VTs are processed fairly in submission order.
- `ManagedBlocker` lets a blocking operation tell the pool to **compensate** by starting/activating another worker, preserving target parallelism — this is how the carrier pool grows when VTs block in uninstrumented ways.
- Key internals: `workQueues[]`, `ctl` (packed control field: active/total worker counts + versioning), `signalWork`, `scan`/`awaitWork`.

### 10.4 Reactor Scheduler

- `Scheduler` abstracts a thread provider: `Worker` instances schedule `Runnable`s. `boundedElastic` wraps capped, reusable `ScheduledExecutorService`-backed workers with a task queue cap and TTL eviction; `parallel` wraps N pinned single-thread workers (`ParallelScheduler`).
- `publishOn` inserts a `FluxPublishOn` operator that queues items and drains them on a `Worker` (changes downstream thread). `subscribeOn` wraps subscription in a scheduled task (changes upstream/source thread).
- Backpressure handled via `request(n)` propagation and internal `Queue` (often `SpscLinkedArrayQueue`) with drain loops guarded by `WIP` (work-in-progress) atomic counters — classic lock-free serialization.

### 10.5 Netty Event Loop

- `EventLoopGroup` = N `EventLoop`s (default 2×cores for `NioEventLoopGroup`, but Reactor Netty defaults to #cores). Each `EventLoop` = **one thread + one `Selector`** owning a set of `Channel`s.
- Core loop: `select()` (epoll_wait) for ready I/O → process I/O events → run queued tasks → repeat. `ioRatio` tunes time split between I/O and task processing.
- A `Channel` is bound to exactly one `EventLoop` **for its lifetime** → all handlers for that channel run on the same thread → no synchronization needed in the pipeline, but **any blocking on that thread stalls every channel it owns**.
- `ChannelPipeline` = ordered `ChannelHandler`s (codec, business). Writes go through `ChannelOutboundBuffer` with high/low watermarks = Netty-level backpressure.

**Interview points**: Explain FJP work-stealing (LIFO own / FIFO steal) and `ManagedBlocker` compensation. What does `Continuation.yield` do and when does it fail? Why is a Netty channel pinned to one event-loop thread? How does Reactor serialize concurrent `request`/`onNext` without locks (WIP drain loop)?

---

## 11. Architecture Decision Framework

### 11.1 Decision tree

```
START
 │
 ├─ Do you need end-to-end BACKPRESSURE or STREAMING (SSE/WebSocket, huge
 │   persistent connections, reactive Kafka, large result streaming)?
 │        │
 │        ├─ YES ──► Is the WHOLE stack non-blocking (R2DBC / reactive clients)
 │        │           AND team fluent in reactive?
 │        │                ├─ YES ──► SPRING WEBFLUX (Reactive)
 │        │                └─ NO  ──► Invest in reactive skills OR isolate the
 │        │                            streaming part as a dedicated reactive service
 │        │                            (HYBRID)
 │        │
 │        └─ NO ──► Is the workload I/O-bound request/response (CRUD, orchestration,
 │                   fan-out to DB/REST)?
 │                        ├─ YES ──► SPRING MVC + VIRTUAL THREADS   ◄── default
 │                        └─ NO  ──► CPU-bound / ultra-low-latency hot path?
 │                                       └─► Dedicated platform-thread pool /
 │                                            lock-free (Disruptor) + tuned GC
 │
 └─ Legacy MVC app on Java 21+ wanting more scale with minimal change?
          └─► Flip spring.threads.virtual.enabled=true, audit pinning, size pools
```

### 11.2 Factor weighting

| Factor | Pushes toward Virtual Threads | Pushes toward Reactive |
|--------|-------------------------------|------------------------|
| Team expertise | Imperative/Spring MVC background | Existing reactive fluency |
| Traffic profile | Request/response, bursty I/O | Massive persistent conns, streaming |
| Data layer | JDBC/JPA, rich ORM needed | R2DBC/non-blocking available |
| External APIs | Blocking SDKs/clients | Non-blocking clients available |
| Backpressure | Can bound with Semaphore | Must have native demand propagation |
| Complexity budget | Low — simple, debuggable | Willing to pay for operator model |
| Maintainability | Standard tooling, stack traces | Needs discipline & tooling |
| Debuggability/Observability | First-class (dumps, breakpoints) | Harder, needs instrumentation |

### 11.3 Hybrid architecture

- Common real-world pattern: **MVC + Virtual Threads** for the bulk of services (CRUD, orchestration), **WebFlux/Netty** only for the components that truly need it (API gateway, chat/streaming, SSE push, high-fanout notification streaming).
- Within a VT service, use `StructuredTaskScope` for parallel fan-out and `Semaphore`/Resilience4j for backpressure & bulkheads — this gives most of reactive's benefits without the model cost.
- Avoid mixing blocking and reactive **inside one call chain**; isolate at service boundaries.

**Interview points**: Walk the decision tree aloud for a given scenario. When is "just enable virtual threads" the right answer, and when is it a trap (CPU-bound, streaming backpressure)? Justify a hybrid split.

---

## 12. Interview Preparation

50 advanced Q&A. Format: **Q — ideal answer — ⚠ common mistake — ↪ follow-up.**

### Virtual Threads fundamentals (1–12)

1. **What is a virtual thread and how does it differ from a platform thread?**
   A JVM-managed, user-mode thread multiplexed onto a small pool of OS carrier threads; its stack lives on the heap as stack chunks and is mounted onto a carrier only while running. Platform thread = thin 1:1 wrapper over an OS thread with a ~1MB native stack.
   ⚠ Saying "lighter OS thread" — it's *not* an OS thread. ↪ Where does its stack live?

2. **How are millions of virtual threads possible?** Idle/blocked VTs cost only a heap stack chunk (hundreds of bytes–KB) and unmount their carrier; no 1MB OS stacks, no OS scheduler entries. ⚠ Confusing with a huge thread pool. ↪ What's the new bottleneck then? (downstream/pool)

3. **Explain mount/unmount.** Mount = thaw heap stack onto a carrier and run; unmount = at a blocking point, freeze the stack to the heap and release the carrier. ↪ Which operations trigger unmount?

4. **What is a continuation?** A delimited continuation (`jdk.internal.vm.Continuation`) capturing the call stack so it can yield (suspend) and resume later, possibly on another carrier. ↪ Relation to `yield`?

5. **What is pinning and why does it matter?** A VT that can't unmount (inside `synchronized` during blocking, or native/JNI) holds its carrier; mass pinning exhausts carriers → throughput collapses to ~#cores. ⚠ Thinking `ReentrantLock` also pins (it doesn't). ↪ How to detect/fix?

6. **How do you detect pinning?** `-Djdk.tracePinnedThreads=full`, JFR `jdk.VirtualThreadPinned`. ↪ Did JDK 24 change this? (JEP 491 removes monitor pinning)

7. **Should you pool virtual threads?** No — they're cheap and disposable; pooling caps concurrency and defeats the purpose. Use `newVirtualThreadPerTaskExecutor()`. ⚠ `newFixedThreadPool` with VT factory. 

8. **Are VTs good for CPU-bound work?** No — no fairness preemption among VTs, no parallelism gain; use a bounded platform pool/FJP. ↪ Why no benefit?

9. **ThreadLocal vs ScopedValue with VTs?** ThreadLocal works but risks memory bloat at scale and mutable sharing; `ScopedValue` is immutable, bounded to a dynamic scope, cheaper, inheritable by structured forks. 

10. **Does the Java Memory Model change?** No — happens-before, volatile, locks identical. Mount/unmount preserve correctness. 

11. **What scheduler runs VTs?** A dedicated `ForkJoinPool` in FIFO async mode, parallelism = #cores by default. ↪ Tunables? (`jdk.virtualThreadScheduler.*`)

12. **What happens on `Thread.sleep` in a VT?** It unmounts (timed park via the JDK poller), freeing the carrier; no OS thread blocked. ⚠ Thinking it blocks a carrier.

### Reactive fundamentals (13–24)

13. **What problem do Reactive Streams solve?** Async, non-blocking data flow with **backpressure** so consumers control producer rate; high concurrency on few threads. 

14. **Explain the 4 interfaces.** Publisher/Subscriber/Subscription/Processor; demand via `request(n)` upstream, data via `onNext` downstream, terminated by one `onComplete`/`onError`. 

15. **Assembly vs subscription time?** Operators only build a Publisher chain at assembly; execution starts at `subscribe()` when subscribers chain upward and demand flows. ⚠ Expecting side effects before subscribe. 

16. **publishOn vs subscribeOn?** `publishOn` changes the thread for downstream operators; `subscribeOn` changes where the source subscription runs regardless of position. ↪ Multiple `subscribeOn`? (first one wins)

17. **boundedElastic vs parallel?** `parallel` = fixed #cores for CPU, non-blocking; `boundedElastic` = capped elastic pool for wrapping blocking calls. ⚠ Blocking on `parallel`. 

18. **What is backpressure, mechanically?** The subscriber's `request(n)` bounds how many items the publisher may emit; operators propagate/transform demand end to end. 

19. **Backpressure strategies?** buffer / drop / latest / error; plus `limitRate`. Trade memory vs loss vs failure. 

20. **Hot vs cold publisher?** Cold = per-subscriber fresh execution (replays); hot = shared live stream (multicast via `Sinks`). 

21. **Why does one blocking call kill WebFlux?** It stalls a Netty event-loop thread owning many channels → all those connections freeze; few loops → systemic collapse. 

22. **How does context flow (no ThreadLocal)?** Reactor `Context`/`ContextView` via `contextWrite`, read with `deferContextual`. 

23. **How does WebFlux serve 100K connections?** Non-blocking NIO + few event loops; ~KB/connection, no per-connection thread. 

24. **How to debug reactive stack traces?** `checkpoint()`, `Hooks.onOperatorDebug()` / `ReactorDebugAgent`, `log()` operator, Micrometer tracing. 

### Comparison & architecture (25–36)

25. **Are VTs faster than reactive?** For I/O-bound, comparable throughput; VTs win simplicity/debuggability, reactive wins backpressure/streaming. ⚠ Claiming one is universally faster. 

26. **Why does Loom NOT give backpressure for free?** No demand signaling; a million VTs = a million concurrent calls unless bounded by Semaphore/pool. 

27. **VT equivalent of event-loop blocking failure?** Carrier exhaustion via pinning. 

28. **When pick WebFlux over MVC+VT?** True streaming/backpressure, massive persistent connections, existing non-blocking stack, API gateway. 

29. **When is MVC+VT the better default?** CRUD/orchestration, JDBC/JPA, blocking SDKs, imperative team, debuggability. 

30. **Hybrid architecture example?** VT services for business logic + reactive gateway/chat/SSE edge; isolate at boundaries. 

31. **Why does enabling VTs expose connection-pool exhaustion?** App thread limit removed → true concurrency hits a small Hikari pool; VTs queue on `getConnection`. 

32. **How to size the DB pool under VTs?** To DB capacity (~(cores×2)+spindles), not request count; let VTs queue. 

33. **R2DBC vs JDBC on VTs?** JDBC+VT: simple, mature, frees threads, held connection is the cost. R2DBC: non-blocking acquisition, end-to-end backpressure, but immature ORM/complexity. 

34. **Structured concurrency — what and why?** Scopes tie child task lifecycles to a parent; fail-fast cancellation, no leaks, clear error propagation. 

35. **How to bound fan-out in VTs?** `Semaphore`, bounded queues, Resilience4j bulkhead/rate limiter, `flatMap(concurrency)` in reactive. 

36. **Tail latency: VT vs reactive causes?** VT: pinning spikes p99; reactive: event-loop blocking cliff. Both otherwise stable. 

### JVM/internals & performance (37–46)

37. **Where do VT stacks live and how do they grow?** Heap `StackChunk` objects, grown on demand via freeze/thaw. 

38. **FJP work-stealing details?** Per-worker deque; own tasks LIFO (cache-hot), steals FIFO; VT scheduler uses async FIFO mode. 

39. **ManagedBlocker?** Lets a blocking op ask FJP to compensate by activating/starting a worker to keep target parallelism. 

40. **GC impact of each model?** Reactive: young-gen garbage per onNext; VTs: per-task Thread + stack chunks; platform: least allocation but huge RSS. 

41. **How to profile VT apps?** `jcmd Thread.dump_to_file -format=json`, JFR VT events, async-profiler (meaningful stacks). 

42. **Context switches — how measured, who wins?** `pidstat -w`, `vmstat`; VTs/reactive low, platform threads high at scale. 

43. **What stays on the carrier when a VT unmounts?** Nothing of the VT — carrier is free; VT's frames are frozen to heap. 

44. **Netty channel threading model?** Each channel bound to one event loop for life → no handler sync needed, but blocking stalls all its channels. 

45. **How does Reactor avoid locks in drain loops?** WIP atomic counter serializes concurrent request/onNext (lock-free drain). 

46. **GC choice for low-latency + many VTs?** ZGC/Shenandoah for low pause; watch heap for stack chunks at high concurrency. 

### Failures & production (47–50)

47. **All requests hang, CPU low, pool active==max — diagnosis?** Thread starvation: workers blocked on slow downstream. Fix: VTs/bulkheads/timeouts/circuit breakers. 

48. **VT app throughput caps at ~#cores — why?** Carrier exhaustion from pinning; trace pinned threads, replace `synchronized`, upgrade JDK. 

49. **WebFlux p99 cliff under load — why?** Blocking on event loop; add BlockHound, offload to boundedElastic, use non-blocking drivers. 

50. **Heap grows until OOM in reactive — causes?** Unbounded `onBackpressureBuffer`/queues/retained subscriptions; bound buffers, choose drop/error, dispose. 

**Architect-level discussion threads**: migration strategy MVC→VT (audit pinning, pool sizing, observability); when reactive complexity is justified by SLA; cost of function coloring; organizational skill/maintainability as a first-class architecture input; testing strategies (BlockHound, load tests to the knee, chaos on downstreams).

---

## 13. Cheat Sheets & Checklists

### 13.1 Architecture Cheat Sheet

| Workload | Recommended | Why |
|----------|-------------|-----|
| CRUD / REST microservice | MVC + Virtual Threads | Simple, scalable, JDBC-friendly |
| Orchestration / fan-out | MVC + VT + StructuredTaskScope | Parallel blocking calls, clean cancel |
| API Gateway | Reactive (Spring Cloud Gateway) | Non-blocking proxy, backpressure |
| Chat / streaming / SSE | Reactive (WebFlux + WebSocket) | 100K+ idle conns, demand control |
| Notifications / high fan-out | VT (bounded) or Reactive | I/O-bound; need rate limiting |
| Trading / ultra-low-latency | Platform threads + Disruptor + ZGC | Jitter-sensitive, lock-free |
| Banking / ledger | MVC + VT + JPA | ACID, short txns, ORM richness |

### 13.2 Virtual Threads Cheat Sheet

```java
// Create
Thread.ofVirtual().start(runnable);
Thread.startVirtualThread(runnable);
var exec = Executors.newVirtualThreadPerTaskExecutor();   // one VT per task

// Structured concurrency (Java 25)
try (var scope = StructuredTaskScope.open(Joiner.allSuccessfulOrThrow())) {
    var a = scope.fork(taskA);  var b = scope.fork(taskB);
    scope.join();  use(a.get(), b.get());
}

// Bound a downstream
Semaphore limit = new Semaphore(50);

// Context
ScopedValue<User> USER = ScopedValue.newInstance();
ScopedValue.where(USER, user).run(() -> handle());
```
- DO: one VT per task; `ReentrantLock` over `synchronized`; size DB/HTTP pools; `ScopedValue`.
- DON'T: pool/cache VTs; CPU-bound on VTs; blocking inside `synchronized`; unbounded fan-out.
- DIAGNOSE: `-Djdk.tracePinnedThreads=full`; JFR `jdk.VirtualThreadPinned`; `jcmd <pid> Thread.dump_to_file -format=json <file>`.
- TUNE: `jdk.virtualThreadScheduler.parallelism`, `...maxPoolSize`, `...minRunnable`.

### 13.3 Reactive Programming Cheat Sheet

```java
Mono<User> user = userClient.get(id);            // 0..1
Flux<Order> orders = orderClient.stream(id);     // 0..N

mono.flatMap(this::call)                          // async, ordered-ish
    .flatMapMany(x -> Flux.fromIterable(x.items()))
    .subscribeOn(Schedulers.boundedElastic())     // where source runs
    .publishOn(Schedulers.parallel())             // downstream thread
    .onBackpressureBuffer(1000)
    .contextWrite(ctx -> ctx.put("traceId", id))
    .checkpoint("after-call")
    .subscribe();                                  // nothing runs until here
```
- DO: `boundedElastic()` for blocking; bound `flatMap(fn, concurrency)`; propagate `Context`; `checkpoint()`.
- DON'T: block on event loop/`parallel()`; call `.block()` in a reactive path; forget to subscribe; unbounded buffers.
- Schedulers: `parallel()`=CPU, `boundedElastic()`=blocking, `single()`, `immediate()`.
- Test: **BlockHound** to catch blocking on non-blocking threads.

### 13.4 Spring Boot Cheat Sheet

```yaml
# MVC + Virtual Threads (Spring Boot 3.2+, Java 21+)
spring:
  threads:
    virtual:
      enabled: true
  datasource:
    hikari:
      maximum-pool-size: 30          # size to DB, NOT request count
      connection-timeout: 3000
      leak-detection-threshold: 20000

server:
  tomcat:
    threads:
      max: 200                        # ~irrelevant once VTs enabled
    accept-count: 200
```
- MVC+Platform: bounded by `tomcat.threads.max`.
- MVC+VT: VT per request; audit `synchronized`; size Hikari; keep txns short.
- WebFlux: non-blocking end to end; `reactor.netty.ioWorkerCount` for loops; never block.

### 13.5 Decision Matrix

| Criterion | MVC + Platform | MVC + Virtual Threads | WebFlux (Reactive) |
|-----------|----------------|------------------------|--------------------|
| Concurrency ceiling | ~pool size (100s) | Millions (downstream-bound) | Millions (conn-bound) |
| Programming model | Imperative | Imperative | Declarative/async |
| Blocking allowed | Yes | Yes | No (offload only) |
| Backpressure | No | Manual (Semaphore) | Native |
| DB | JDBC/JPA | JDBC/JPA | R2DBC |
| Debuggability | High | High | Low–Medium |
| Learning curve | Low | Low | High |
| Best for | Legacy/low scale | Default modern choice | Streaming/gateway/100K conns |
| Memory/conn | ~1 MB/thread | ~KB stack chunk | ~KB/subscription |

### 13.6 Performance Tuning Checklist

- [ ] Load-tested to the **knee**; know real capacity and binding constraint.
- [ ] DB/HTTP connection pools sized to **downstream capacity**, with metrics (`hikaricp.connections.pending`).
- [ ] Transactions short; no connection leaks (leak detection on).
- [ ] VT: no pinning (`-Djdk.tracePinnedThreads`); `ReentrantLock` on hot paths; JDK 24+ if possible.
- [ ] Reactive: no blocking on loop (BlockHound in CI); bounded `flatMap` concurrency; bounded buffers.
- [ ] GC tuned (G1/ZGC/Shenandoah) for pause SLA; monitor allocation rate & p99 pauses.
- [ ] Bulkheads + timeouts + circuit breakers (Resilience4j) on every remote dependency.
- [ ] Rate limiters bounding fan-out to protect downstreams.
- [ ] Metrics on p50/p99/p999, not just averages; context switches (`pidstat -w`).

### 13.7 Production Readiness Checklist

- [ ] Observability: distributed tracing propagates context (ScopedValue / Reactor Context); structured logs with traceId.
- [ ] Thread/heap dump runbook; JFR enabled or on-demand; async-profiler available.
- [ ] Failure modes rehearsed: pinning, pool exhaustion, event-loop blocking, retry storms.
- [ ] Timeouts on ALL I/O (DB, HTTP, cache, Kafka); no unbounded waits.
- [ ] Circuit breakers + bulkheads + rate limiters configured and alerted.
- [ ] Idempotency & retries safe (no duplicate side effects).
- [ ] Backpressure/overload behavior defined (shed load, 429, queue limits) — not silent OOM.
- [ ] Capacity validated via load test; autoscaling signals chosen (latency/queue depth, not just CPU).
- [ ] Dependency versions audited for VT-friendliness (drivers, pools, logging) and CVEs.
- [ ] Graceful shutdown: drain in-flight requests, close scopes/executors, release connections.
- [ ] Rollback plan for architecture switch (feature-flag `spring.threads.virtual.enabled`).

---

### Final mental model

> **Virtual Threads** move the async multiplexing *down into the JVM runtime* so you keep writing simple blocking code and still scale. **Reactive** keeps multiplexing *in the library* but gives you end-to-end backpressure and streaming composition. Both free the OS thread during I/O — the choice is about **backpressure needs, team expertise, existing drivers, and maintainability**, not raw speed. After adopting either, the bottleneck becomes your **downstream capacity** — size and protect it deliberately.





