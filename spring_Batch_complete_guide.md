# Spring Batch — Complete Guide (Basics to Advanced)
### For Experienced Java Developers Preparing for Interviews

---

## Table of Contents
1. [Introduction to Batch Processing](#1-introduction-to-batch-processing)
2. [Spring Batch Architecture](#2-spring-batch-architecture)
3. [Chunk-Oriented Processing](#3-chunk-oriented-processing)
4. [Transaction Management in Batch Jobs](#4-transaction-management-in-batch-jobs)
5. [Error Handling and Retry Mechanisms](#5-error-handling-and-retry-mechanisms)
6. [Scheduling Batch Jobs](#6-scheduling-batch-jobs)
7. [Real-World Example: Processing Large CSV Files into a Database](#7-real-world-example-processing-large-csv-files-into-a-database)
8. [Interview Questions](#8-interview-questions)

---

## 1. Introduction to Batch Processing

**What is batch processing?**
Batch processing refers to running a set of programs (jobs) to process large volumes of data **without user interaction**, typically on a schedule (e.g., nightly) or triggered by an event, as opposed to request-response (online/transactional) processing.

**Why not just write a plain Java loop for this?**
```java
// The naive approach - looks simple, but has serious gaps at scale:
public void processFile(String filePath) {
    List<String> lines = Files.readAllLines(Paths.get(filePath)); // loads EVERYTHING into memory
    for (String line : lines) {
        Record record = parse(line);
        repository.save(record); // one DB round-trip per record - very slow at scale
        // What happens if this crashes at record 800,000 of 1,000,000?
        // Do we reprocess everything? How do we know where we left off?
    }
}
```

**What Spring Batch adds that a hand-rolled loop doesn't give you for free:**
- **Chunk-based processing** — read/process/write in configurable batches, not one record (or the whole file) at a time.
- **Restart-ability** — if a job fails partway through, Spring Batch tracks exactly which chunks succeeded via its **JobRepository**, so a restarted job can resume from the point of failure instead of reprocessing everything.
- **Transaction boundaries per chunk** — a failure in one chunk rolls back just that chunk, not the entire job's already-committed work.
- **Skip/retry policies** — declaratively skip bad records or retry transient failures (e.g., a momentary DB connection blip) without custom retry loop code.
- **Built-in listeners and metrics** — job/step execution status, read/write/skip counts, and timing, all tracked automatically in the batch metadata tables.
- **Parallelization/scaling options** — multi-threaded steps, partitioning, and remote chunking for very large workloads.

**Real-world example:** A nightly job ingesting 2 million trade records from an upstream file feed into a regulatory reporting database. Without Spring Batch, a failure at record 1.5 million means either reprocessing everything (risking duplicate records) or building custom checkpointing logic by hand. With Spring Batch, the `JobRepository` already knows exactly which chunks completed, so restarting the job picks up cleanly from the last successful chunk.

**Q&A**
- **Q: When would you NOT use Spring Batch?**
  A: For very small, simple one-off scripts where the operational overhead (metadata tables, job/step configuration) isn't justified, or for real-time/streaming use cases (better suited to Kafka Streams, Spring Cloud Stream, or a simple message-driven consumer) — Spring Batch is specifically designed for large-volume, scheduled/triggered, non-interactive bulk processing.

---

## 2. Spring Batch Architecture

```
┌─────────────────────────────────────────────────────────────────┐
│                             Job                                    │
│  (the whole batch process, e.g., "Nightly Trade Import")           │
│                                                                      │
│  ┌──────────────┐    ┌──────────────┐    ┌──────────────┐        │
│  │    Step 1      │ →  │    Step 2      │ →  │    Step 3      │  →...  │
│  │ (Validate File)│    │ (Import Data) │    │ (Generate      │        │
│  │                │    │                │    │  Summary Report)│        │
│  └──────────────┘    └──────────────┘    └──────────────┘        │
└─────────────────────────────────────────────────────────────────┘
                              │
                    Each Step typically follows
                  the Chunk-Oriented processing model:
                              │
        ┌─────────────────────┼─────────────────────┐
        ▼                     ▼                     ▼
┌───────────────┐   ┌───────────────┐   ┌───────────────┐
│  ItemReader    │ → │ ItemProcessor  │ → │  ItemWriter    │
│ (reads one     │   │ (transforms/   │   │ (writes a full │
│  item at a     │   │  validates one │   │  CHUNK of items │
│  time)         │   │  item at a     │   │  at once)      │
│                │   │  time)         │   │                │
└───────────────┘   └───────────────┘   └───────────────┘

              All of this is tracked by:
┌─────────────────────────────────────────────────────────────────┐
│                        JobRepository                              │
│  (persists JobInstance, JobExecution, StepExecution metadata      │
│   to database tables — enables restart, monitoring, auditing)     │
└─────────────────────────────────────────────────────────────────┘
```

**Core concepts:**

- **Job:** The entire batch process, composed of one or more **Steps** executed in a defined order (sequentially, conditionally, or in parallel).
- **JobInstance:** A logical run of a Job, identified by the Job's name plus its **JobParameters** (e.g., "Nightly Trade Import" run for `2026-07-29`). Re-running with the *same* parameters refers to the same `JobInstance`.
- **JobExecution:** One physical attempt to run a `JobInstance`. If a job fails and is restarted, you get a *new* `JobExecution` for the *same* `JobInstance`.
- **Step:** A single, independently-executable phase of a Job — most commonly implemented via the chunk-oriented `ItemReader → ItemProcessor → ItemWriter` pattern, but can also be a simple `Tasklet` for non-chunked logic.
- **ItemReader<T>:** Reads one item at a time from a data source (file, database, queue) — returns `null` when there's nothing left to read.
- **ItemProcessor<I, O>:** Transforms, validates, or filters a single item (returning `null` from `process()` filters that item out entirely, excluding it from the write step).
- **ItemWriter<T>:** Writes a **chunk** (list) of items at once — this batched write is what makes bulk operations efficient.
- **JobRepository:** The persistence mechanism (backed by relational database tables like `BATCH_JOB_EXECUTION`, `BATCH_STEP_EXECUTION`) tracking every job/step execution's state, enabling restart-ability and monitoring.
- **JobLauncher:** The entry point used to actually start a Job execution (with a given set of `JobParameters`).

**Basic Job/Step definition (Spring Batch 5.x style, using `JobRepository`/`PlatformTransactionManager` beans directly):**
```java
@Configuration
public class TradeImportJobConfig {

    @Bean
    public Job tradeImportJob(JobRepository jobRepository, Step validateFileStep, Step importDataStep) {
        return new JobBuilder("tradeImportJob", jobRepository)
                .start(validateFileStep)
                .next(importDataStep)
                .build();
    }

    @Bean
    public Step importDataStep(JobRepository jobRepository, PlatformTransactionManager transactionManager,
                                 ItemReader<TradeRecord> reader, ItemProcessor<TradeRecord, Trade> processor,
                                 ItemWriter<Trade> writer) {
        return new StepBuilder("importDataStep", jobRepository)
                .<TradeRecord, Trade>chunk(100, transactionManager) // chunk size = 100
                .reader(reader)
                .processor(processor)
                .writer(writer)
                .build();
    }
}
```

**Q&A**
- **Q: What's the difference between a JobInstance and a JobExecution?**
  A: A `JobInstance` represents the logical concept of "this job, run with these specific parameters" (e.g., processing the July 29 file). A `JobExecution` represents one physical attempt at running that instance — if the first attempt fails and you restart it, you get a second `JobExecution` tied to the *same* `JobInstance`, which is precisely what enables Spring Batch to know it should resume rather than start fresh.

---

## 3. Chunk-Oriented Processing

**The core idea:** Instead of processing the entire dataset in one giant transaction (risky — one bad record fails the whole job) or one record per transaction (extremely slow — a database round-trip per record), chunk-oriented processing reads and processes items **one at a time**, but commits them to the database in **configurable-sized batches (chunks)**.

```java
@Bean
public Step processEmployeesStep(JobRepository jobRepository, PlatformTransactionManager txManager,
                                    ItemReader<EmployeeCsvRow> reader,
                                    ItemProcessor<EmployeeCsvRow, Employee> processor,
                                    ItemWriter<Employee> writer) {
    return new StepBuilder("processEmployeesStep", jobRepository)
            .<EmployeeCsvRow, Employee>chunk(500, txManager) // read/process 500 items, THEN write+commit as one unit
            .reader(reader)
            .processor(processor)
            .writer(writer)
            .faultTolerant()
            .skipLimit(50)
            .skip(FlatFileParseException.class)
            .build();
}
```

**What actually happens under the hood, per chunk of size 500:**
1. `ItemReader.read()` is called 500 times, one item at a time.
2. Each item is passed individually to `ItemProcessor.process()`.
3. Once 500 (processed, non-null) items have accumulated, they're all passed **together** to `ItemWriter.write()` as a single `List`.
4. The transaction for that chunk commits.
5. Repeat for the next chunk, until the reader returns `null` (no more data).

**Example implementations:**
```java
// ItemReader - reads from a CSV file, one row at a time
@Bean
@StepScope // new reader instance per step execution, allows late-binding of job parameters
public FlatFileItemReader<EmployeeCsvRow> employeeReader(
        @Value("#{jobParameters['inputFile']}") String inputFile) {
    return new FlatFileItemReaderBuilder<EmployeeCsvRow>()
            .name("employeeReader")
            .resource(new FileSystemResource(inputFile))
            .delimited()
            .names("name", "email", "department", "salary")
            .linesToSkip(1) // skip CSV header
            .fieldSetMapper(new BeanWrapperFieldSetMapper<>() {{
                setTargetType(EmployeeCsvRow.class);
            }})
            .build();
}

// ItemProcessor - validates and transforms each row individually
@Bean
public ItemProcessor<EmployeeCsvRow, Employee> employeeProcessor() {
    return csvRow -> {
        if (csvRow.getSalary() == null || csvRow.getSalary() < 0) {
            throw new InvalidRecordException("Invalid salary for: " + csvRow.getEmail());
        }
        if (isBlacklistedDomain(csvRow.getEmail())) {
            return null; // returning null FILTERS this record out - it won't reach the writer
        }
        Employee emp = new Employee();
        emp.setName(csvRow.getName());
        emp.setEmail(csvRow.getEmail());
        emp.setDepartment(csvRow.getDepartment());
        emp.setSalary(csvRow.getSalary());
        return emp;
    };
}

// ItemWriter - writes an entire chunk (List<Employee>) in one batch operation
@Bean
public JpaItemWriter<Employee> employeeWriter(EntityManagerFactory entityManagerFactory) {
    return new JpaItemWriterBuilder<Employee>()
            .entityManagerFactory(entityManagerFactory)
            .build();
}
```

**Why chunk size matters — a real tuning tradeoff:**
| Chunk Size | Effect |
|---|---|
| Too small (e.g., 1-10) | Many small transactions — high commit overhead, slow overall throughput |
| Too large (e.g., 100,000) | Long-running transactions holding locks, large memory footprint per chunk, coarse-grained restart points (more work lost/reprocessed on a mid-chunk failure) |
| Well-tuned (typically 100-1000, workload-dependent) | Balances throughput against transaction overhead and failure-recovery granularity |

**Q&A**
- **Q: Why does returning `null` from an ItemProcessor "filter" a record instead of causing an error?**
  A: This is a deliberate Spring Batch design choice — it distinguishes between "this record is invalid/should be skipped silently" (return `null`) and "this record caused an unexpected processing error" (throw an exception, subject to skip/retry policies). It's the standard, idiomatic way to implement business-rule-based filtering within a chunk-oriented step.

---

## 4. Transaction Management in Batch Jobs

**Transaction boundary = one chunk**, not the whole step or job. Each chunk's read+process+write cycle is wrapped in a single transaction, managed by the `PlatformTransactionManager` supplied to the step.

```java
@Bean
public Step importStep(JobRepository jobRepository, PlatformTransactionManager transactionManager, ...) {
    return new StepBuilder("importStep", jobRepository)
            .<Input, Output>chunk(200, transactionManager) // transactionManager governs commit/rollback per chunk
            .reader(reader).processor(processor).writer(writer)
            .build();
}
```

**What happens on a failure within a chunk:**
- If an exception occurs during processing or writing of items **within a chunk**, that entire chunk's transaction **rolls back** — none of the items in that chunk are committed.
- **Chunks already committed before the failure remain committed** — this is exactly why chunking (rather than one giant job-wide transaction) enables restart-from-failure-point behavior.
- On restart (same `JobInstance`, new `JobExecution`), Spring Batch's `JobRepository` metadata tells it which step to resume and, for readers that support it (e.g., `FlatFileItemReader` tracking line numbers, database cursor readers tracking a bookmark), where within that step to resume reading from.

**Custom transaction attributes per step:**
```java
@Bean
public Step importStep(JobRepository jobRepository, PlatformTransactionManager transactionManager, ...) {
    return new StepBuilder("importStep", jobRepository)
            .<Input, Output>chunk(200, transactionManager)
            .reader(reader).processor(processor).writer(writer)
            .transactionAttribute(new DefaultTransactionAttribute() {{
                setIsolationLevel(Isolation.READ_COMMITTED.value());
                setTimeout(30); // seconds - per-chunk transaction timeout
            }})
            .build();
}
```

**Real-world example:** A 5-million-row nightly import configured with a chunk size of 500 fails at row 3.2 million due to a transient network blip talking to a downstream validation service. Because each 500-row chunk is its own transaction, roughly 6,400 chunks had already committed successfully before the failure — restarting the job resumes from approximately row 3.2 million rather than reprocessing 3.2 million already-successful rows, which would both waste hours of processing time and risk creating duplicate records.

**Q&A**
- **Q: Why is committing per-chunk (rather than one transaction for the entire job) essential for large batch jobs?**
  A: A single job-wide transaction for millions of records would hold database locks and transaction log resources for the job's entire duration (potentially hours), risking transaction log overflow, severe lock contention with other concurrent processes, and — critically — a failure anywhere in the job would roll back *everything*, forcing a complete restart from scratch with no partial progress preserved.

---

## 5. Error Handling and Retry Mechanisms

### 5.1 Skip Policy (Ignore Bad Records, Continue Processing)

```java
@Bean
public Step importStep(JobRepository jobRepository, PlatformTransactionManager txManager, ...) {
    return new StepBuilder("importStep", jobRepository)
            .<TradeCsvRow, Trade>chunk(200, txManager)
            .reader(reader).processor(processor).writer(writer)
            .faultTolerant()
            .skip(FlatFileParseException.class)     // skip malformed CSV rows
            .skip(InvalidRecordException.class)     // skip records failing business validation
            .noSkip(NullPointerException.class)      // NEVER skip NPEs - these indicate a real bug, not bad data
            .skipLimit(100)                          // abort the ENTIRE job if more than 100 records are skipped
            .listener(new SkipListener<TradeCsvRow, Trade>() {
                @Override
                public void onSkipInProcess(TradeCsvRow item, Throwable t) {
                    log.warn("Skipped record {}: {}", item, t.getMessage());
                    // could write skipped records to a dead-letter table here for manual review
                }
            })
            .build();
}
```

### 5.2 Retry Policy (Retry Transient Failures)

```java
@Bean
public Step importStep(JobRepository jobRepository, PlatformTransactionManager txManager, ...) {
    return new StepBuilder("importStep", jobRepository)
            .<TradeCsvRow, Trade>chunk(200, txManager)
            .reader(reader).processor(processor).writer(writer)
            .faultTolerant()
            .retry(TransientDataAccessException.class) // retry on transient DB issues (deadlock, timeout)
            .retryLimit(3)
            .build();
}
```

**Skip vs. Retry — the key distinction:**
| Mechanism | Use Case | Effect |
|---|---|---|
| **Skip** | The data itself is bad/invalid (malformed row, business rule violation) | The record is permanently excluded from this run; processing continues with the next record |
| **Retry** | The failure is transient/environmental (network blip, DB deadlock, momentary service unavailability) | The **same** record's processing is attempted again, up to `retryLimit` times, before giving up |

### 5.3 Job-Level Failure Handling

```java
@Component
public class TradeImportJobListener implements JobExecutionListener {
    @Override
    public void afterJob(JobExecution jobExecution) {
        if (jobExecution.getStatus() == BatchStatus.FAILED) {
            alertingService.sendAlert("Trade import job FAILED: " + jobExecution.getExitStatus());
        }
        log.info("Job finished with status: {}, read: {}, written: {}, skipped: {}",
                jobExecution.getStatus(),
                jobExecution.getStepExecutions().stream().mapToLong(StepExecution::getReadCount).sum(),
                jobExecution.getStepExecutions().stream().mapToLong(StepExecution::getWriteCount).sum(),
                jobExecution.getStepExecutions().stream().mapToLong(StepExecution::getSkipCount).sum());
    }
}
```

**Real-world example:** A regulatory reporting import processes 200,000 trade records nightly. Roughly 0.1% of rows have minor data quality issues (a malformed date format from an upstream source) — these are configured as skippable (up to a sensible `skipLimit`), logged to a dead-letter table for the operations team to review the next morning, while the other 99.9% of valid records are imported successfully. A `retryLimit(3)` on the database writer handles occasional transient connection pool exhaustion under load without failing the entire job over a momentary blip.

**Q&A**
- **Q: Why set an explicit `skipLimit` rather than allowing unlimited skips?**
  A: An unbounded skip policy could mask a systemic problem (e.g., an entire upstream file format change causing 90% of records to fail) by silently "succeeding" while actually importing almost nothing useful — a `skipLimit` ensures the job fails loudly and visibly once the skip count exceeds what's a reasonable/expected level of bad data, forcing investigation rather than silent data loss.

---

## 6. Scheduling Batch Jobs

### 6.1 Spring's `@Scheduled` (Simple, Single-Instance Scheduling)

```java
@Component
public class TradeImportScheduler {
    private final JobLauncher jobLauncher;
    private final Job tradeImportJob;

    public TradeImportScheduler(JobLauncher jobLauncher, Job tradeImportJob) {
        this.jobLauncher = jobLauncher;
        this.tradeImportJob = tradeImportJob;
    }

    @Scheduled(cron = "0 0 2 * * *") // every day at 2:00 AM
    public void runNightlyImport() throws Exception {
        JobParameters params = new JobParametersBuilder()
                .addString("inputFile", "/data/trades/" + LocalDate.now() + ".csv")
                .addLong("timestamp", System.currentTimeMillis()) // ensures a UNIQUE JobInstance per run
                .toJobParameters();
        jobLauncher.run(tradeImportJob, params);
    }
}
```
```java
@Configuration
@EnableScheduling
public class SchedulingConfig { }
```

**Why add a `timestamp` parameter?** Spring Batch identifies a `JobInstance` by job name + parameters — running the *exact same* job with identical parameters twice is treated as "restart the same instance," which fails if that instance already completed successfully (`JobInstanceAlreadyCompleteException`). Adding a changing parameter (timestamp, or better, the actual business date being processed) ensures each scheduled run is treated as a distinct instance where appropriate.

### 6.2 Production-Grade Scheduling (Multi-Instance Safe)

`@Scheduled` alone doesn't coordinate across multiple application instances (a real concern in a horizontally-scaled/Kubernetes deployment) — without coordination, **every pod** would try to trigger the same job simultaneously.

**Options for production-safe scheduling:**
- **ShedLock** — a lightweight library providing distributed locking specifically for scheduled tasks, ensuring only one instance actually executes a given scheduled job even when multiple application instances are running.
  ```java
  @Scheduled(cron = "0 0 2 * * *")
  @SchedulerLock(name = "tradeImportJob", lockAtMostFor = "PT2H", lockAtLeastFor = "PT5M")
  public void runNightlyImport() { /* ... */ }
  ```
- **External orchestrators** — Kubernetes `CronJob`, Quartz Scheduler (with a JDBC-backed `JobStore` for clustering), or an enterprise scheduler (Control-M, Autosys) triggering the batch job via a REST endpoint or command-line invocation.

**Real-world example:** A regulatory reporting platform runs across 3 Kubernetes pods for availability, but the nightly trade import must run exactly once, not three times. Using ShedLock (backed by a database lock table) ensures only one pod actually executes the scheduled job each night, while the other two simply skip it — a simpler operational setup than standing up a full Quartz cluster for a single daily job.

**Q&A**
- **Q: What real production problem occurs if you use `@Scheduled` alone across multiple horizontally-scaled instances?**
  A: Every instance's scheduler fires independently at the same cron time, all attempting to launch the same batch job simultaneously — this can cause duplicate processing (if `JobParameters` happen to differ per instance, e.g., due to including a per-instance identifier) or repeated `JobInstanceAlreadyCompleteException` failures (if parameters are identical) as multiple instances race to claim the same `JobInstance`. Distributed locking (ShedLock) or a dedicated external scheduler is required to ensure single execution.

---

## 7. Real-World Example: Processing Large CSV Files into a Database

**Scenario:** Import a large CSV file of employee records into a database, validating each row, skipping malformed rows (up to a limit), and writing valid records in efficient batches.

**`employees.csv`:**
```
name,email,department,salary
Avinash Kumar,avinash@company.com,Engineering,150000
Priya Sharma,priya@company.com,Engineering,140000
INVALID_ROW_MISSING_FIELDS,,
Ravi Patel,ravi@company.com,Finance,120000
```

```java
// Domain objects
public class EmployeeCsvRow {
    private String name;
    private String email;
    private String department;
    private Double salary;
    // getters/setters
}

@Entity
@Table(name = "employee")
public class Employee {
    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    private String name;
    private String email;
    private String department;
    private Double salary;
    // getters/setters
}
```

```java
@Configuration
public class EmployeeImportJobConfig {

    @Bean
    public Job employeeImportJob(JobRepository jobRepository, Step importEmployeesStep,
                                    JobCompletionListener listener) {
        return new JobBuilder("employeeImportJob", jobRepository)
                .listener(listener)
                .start(importEmployeesStep)
                .build();
    }

    @Bean
    public Step importEmployeesStep(JobRepository jobRepository, PlatformTransactionManager txManager,
                                       ItemReader<EmployeeCsvRow> reader,
                                       ItemProcessor<EmployeeCsvRow, Employee> processor,
                                       ItemWriter<Employee> writer) {
        return new StepBuilder("importEmployeesStep", jobRepository)
                .<EmployeeCsvRow, Employee>chunk(100, txManager)
                .reader(reader)
                .processor(processor)
                .writer(writer)
                .faultTolerant()
                .skip(FlatFileParseException.class)
                .skip(ValidationException.class)
                .skipLimit(20)
                .build();
    }

    @Bean
    @StepScope
    public FlatFileItemReader<EmployeeCsvRow> reader(
            @Value("#{jobParameters['inputFile']}") String inputFile) {
        return new FlatFileItemReaderBuilder<EmployeeCsvRow>()
                .name("employeeCsvReader")
                .resource(new FileSystemResource(inputFile))
                .delimited()
                .names("name", "email", "department", "salary")
                .linesToSkip(1)
                .fieldSetMapper(fieldSet -> {
                    EmployeeCsvRow row = new EmployeeCsvRow();
                    row.setName(fieldSet.readString("name"));
                    row.setEmail(fieldSet.readString("email"));
                    row.setDepartment(fieldSet.readString("department"));
                    row.setSalary(fieldSet.readDouble("salary")); // throws parse exception on bad data -> skippable
                    return row;
                })
                .build();
    }

    @Bean
    public ItemProcessor<EmployeeCsvRow, Employee> processor() {
        return csvRow -> {
            if (csvRow.getName() == null || csvRow.getName().isBlank()) {
                throw new ValidationException("Missing name for row with email: " + csvRow.getEmail());
            }
            if (csvRow.getSalary() == null || csvRow.getSalary() < 0) {
                throw new ValidationException("Invalid salary for: " + csvRow.getEmail());
            }
            Employee emp = new Employee();
            emp.setName(csvRow.getName());
            emp.setEmail(csvRow.getEmail());
            emp.setDepartment(csvRow.getDepartment());
            emp.setSalary(csvRow.getSalary());
            return emp;
        };
    }

    @Bean
    public JpaItemWriter<Employee> writer(EntityManagerFactory entityManagerFactory) {
        return new JpaItemWriterBuilder<Employee>()
                .entityManagerFactory(entityManagerFactory)
                .build();
    }
}

@Component
public class JobCompletionListener implements JobExecutionListener {
    private static final Logger log = LoggerFactory.getLogger(JobCompletionListener.class);

    @Override
    public void afterJob(JobExecution jobExecution) {
        StepExecution step = jobExecution.getStepExecutions().iterator().next();
        log.info("Import complete - Read: {}, Written: {}, Skipped: {}",
                step.getReadCount(), step.getWriteCount(), step.getSkipCount());
    }
}
```

**Triggering the job (e.g., via a REST endpoint for on-demand imports, alongside the scheduled nightly run):**
```java
@RestController
@RequestMapping("/api/v1/batch")
public class BatchTriggerController {
    private final JobLauncher jobLauncher;
    private final Job employeeImportJob;

    public BatchTriggerController(JobLauncher jobLauncher, Job employeeImportJob) {
        this.jobLauncher = jobLauncher;
        this.employeeImportJob = employeeImportJob;
    }

    @PostMapping("/import-employees")
    public ResponseEntity<String> triggerImport(@RequestParam String filePath) throws Exception {
        JobParameters params = new JobParametersBuilder()
                .addString("inputFile", filePath)
                .addLong("timestamp", System.currentTimeMillis())
                .toJobParameters();
        JobExecution execution = jobLauncher.run(employeeImportJob, params);
        return ResponseEntity.ok("Job started with status: " + execution.getStatus());
    }
}
```

**What this demonstrates end-to-end:** File-based `ItemReader` with `@StepScope` late-binding of a job parameter (the file path), business validation in the `ItemProcessor` that triggers skippable exceptions, batched JPA writes via `JpaItemWriter`, a `skipLimit` guarding against systemic data problems, and a `JobExecutionListener` reporting final read/write/skip counts — plus both scheduled and on-demand (REST-triggered) job launching.

---

## 8. Interview Questions

### Q1: What is Spring Batch and why is it used?
**A:** Spring Batch is a framework for building robust, high-volume, non-interactive batch processing applications — providing reusable functionality for chunk-based reading/processing/writing, transaction management scoped to chunks, restart-ability after failure (via persisted job/step metadata), skip/retry policies for error handling, and monitoring/auditing. It's used instead of hand-rolled batch loops because those cross-cutting concerns (restart safety, transaction boundaries, error handling, scalability) are genuinely hard to get right manually and are exactly what causes production incidents in home-grown batch code.

### Q2: Explain chunk-oriented processing.
**A:** Chunk-oriented processing reads items one at a time via an `ItemReader`, passes each individually through an `ItemProcessor` for transformation/validation/filtering, and accumulates the results until a configured chunk size is reached — at which point the entire chunk is passed to the `ItemWriter` in one batch write, and the transaction for that chunk commits. This balances efficiency (fewer, larger database operations than record-by-record) against risk (a failure only rolls back the current chunk, not the whole job, and already-committed chunks provide a natural restart point).

### Q3: How do you handle errors in Spring Batch?
**A:** Through **skip** and **retry** policies configured via `.faultTolerant()` on the step builder. **Skip** (`.skip(ExceptionClass.class).skipLimit(n)`) permanently excludes a bad record from the current run and continues processing, useful for genuinely invalid data — with a `skipLimit` to prevent silently ignoring a systemic problem. **Retry** (`.retry(ExceptionClass.class).retryLimit(n)`) re-attempts processing of the *same* record, useful for transient failures like momentary database connectivity issues. `SkipListener` and `RetryListener` hooks allow logging or dead-lettering skipped/retried records for visibility.

### Q4: Difference between ItemReader and ItemWriter?
**A:** `ItemReader<T>` reads data **one item at a time** from a source (file, database cursor, queue) — its `read()` method returns a single item, or `null` to signal there's no more data, driving the chunk loop's termination. `ItemWriter<T>` writes an **entire chunk (a `List<T>`) at once** — it's called only after the configured number of items have been read and processed, enabling efficient batched writes (e.g., a single JDBC batch insert or JPA batch flush) instead of one write operation per record.

### Q5: What's the difference between a `JobInstance` and a `JobExecution`, and why does it matter for restart-ability?
**A:** A `JobInstance` is the logical identity of a job run, determined by job name + `JobParameters`. A `JobExecution` is one physical attempt at running that instance. If a `JobExecution` fails, restarting with the **same** `JobParameters` creates a new `JobExecution` tied to the **same** `JobInstance` — Spring Batch's `JobRepository` uses this to know exactly which steps already completed successfully and can be skipped on restart, and (for supporting readers/writers) where within a partially-completed step to resume.

### Q6: How would you scale a Spring Batch job to process very large volumes faster?
**A:** Several options, chosen based on the bottleneck: **multi-threaded steps** (`taskExecutor()` on the step builder) for CPU-bound processing within a single JVM; **partitioning** (splitting the input data range across multiple parallel worker steps, e.g., by date range or ID range) for larger-scale parallelism, including across multiple JVMs with remote partitioning; and **parallel steps** (running independent steps of a job concurrently rather than sequentially) when steps don't depend on each other's output.

### Q7: How do you ensure a scheduled batch job runs exactly once across multiple horizontally-scaled application instances?
**A:** Plain `@Scheduled` has no built-in cross-instance coordination — every instance's scheduler fires independently. Production-safe options include a distributed locking library like **ShedLock** (simplest for straightforward cron-based jobs), or delegating scheduling entirely to an external, coordination-aware system (Kubernetes `CronJob` triggering a single job execution, or an enterprise scheduler like Control-M/Autosys calling a REST trigger endpoint).

### Q8: Real-time scenario — how would you design a Spring Batch job to import a 10 GB CSV file without running out of memory?
**A:** Never load the whole file into memory — use a streaming `FlatFileItemReader`, which reads line-by-line rather than loading the entire file upfront. Choose a moderate chunk size (e.g., 500-1000) so only that many parsed objects are held in memory at once, not the full dataset. Periodically-flushed writers (like `JpaItemWriter`, which flushes per chunk) avoid unbounded persistence-context growth (see the Hibernate batch processing pitfalls this shares). For very large files, consider partitioning the file into ranges processed by parallel worker steps to also improve throughput, and ensure the reader supports restart (tracking its position) so a mid-file failure doesn't require reprocessing gigabytes of already-handled data.

---