# SQL Features Explained — Basic to Advanced (with Examples & Outputs)

## 📖 Introduction

SQL spans a much wider surface than just `SELECT`. This guide walks through everything from core CRUD statements to indexing internals, stored procedures, window functions, normalization theory, and — critically for senior/team-lead interviews — **real-world performance debugging**. Every concept uses the same small set of sample tables so you can see how they connect.

All five command families are covered, plus the "advanced" layer most guides skip:

| Category | Covers |
|---|---|
| DDL | `CREATE`, `ALTER`, `DROP`, `TRUNCATE`, constraints |
| DML | `INSERT`, `UPDATE`, `DELETE`, `MERGE` |
| DQL | `SELECT`, joins, unions, subqueries, CTEs, window functions |
| DCL | `GRANT`, `REVOKE` |
| TCL | `COMMIT`, `ROLLBACK`, `SAVEPOINT`, isolation levels |
| Objects | Views, Indexes, Stored Procedures, Functions, Triggers |
| Theory | Normalization, ACID, query optimization |

---

## 🧩 Sample Tables (used throughout this document)

```sql
CREATE TABLE departments (
    dept_id     INT PRIMARY KEY,
    dept_name   VARCHAR(50) NOT NULL,
    location    VARCHAR(50)
);

CREATE TABLE employees (
    emp_id      INT PRIMARY KEY,
    emp_name    VARCHAR(50) NOT NULL,
    salary      DECIMAL(10,2) CHECK (salary > 0),
    dept_id     INT,
    manager_id  INT,
    join_date   DATE,
    email       VARCHAR(100) UNIQUE,
    FOREIGN KEY (dept_id) REFERENCES departments(dept_id),
    FOREIGN KEY (manager_id) REFERENCES employees(emp_id)
);

CREATE TABLE projects (
    project_id   INT PRIMARY KEY,
    project_name VARCHAR(50),
    emp_id       INT,
    hours_logged INT,
    FOREIGN KEY (emp_id) REFERENCES employees(emp_id)
);
```

**Data:**

departments

| dept_id | dept_name | location |
|---|---|---|
| 1 | Engineering | Bangalore |
| 2 | HR | Mumbai |
| 3 | Finance | Delhi |

employees

| emp_id | emp_name | salary | dept_id | manager_id | join_date |
|---|---|---|---|---|---|
| 101 | Avinash | 104500 | 1 | NULL | 2023-04-10 |
| 102 | Rahul | 68200 | 1 | 101 | 2023-06-15 |
| 103 | Priya | 58000 | 2 | NULL | 2022-11-01 |
| 104 | Kavya | 71000 | 3 | NULL | 2024-01-20 |
| 105 | Sanjay | 59400 | 1 | 101 | 2024-03-05 |

projects

| project_id | project_name | emp_id | hours_logged |
|---|---|---|---|
| 1 | ADER Reporting | 101 | 120 |
| 2 | ADER Reporting | 102 | 90 |
| 3 | EMIR Compliance | 101 | 150 |
| 4 | Payroll System | 103 | 60 |

---

## 1️⃣ Constraints (foundation for DDL)

| Constraint | Purpose | Example |
|---|---|---|
| `PRIMARY KEY` | Unique row identifier, not null | `emp_id INT PRIMARY KEY` |
| `FOREIGN KEY` | Enforces referential integrity | `FOREIGN KEY (dept_id) REFERENCES departments(dept_id)` |
| `UNIQUE` | No duplicate values allowed | `email VARCHAR(100) UNIQUE` |
| `NOT NULL` | Column must always have a value | `emp_name VARCHAR(50) NOT NULL` |
| `CHECK` | Validates a condition | `CHECK (salary > 0)` |
| `DEFAULT` | Auto-fills a value if none given | `join_date DATE DEFAULT CURRENT_DATE` |

**Why it matters:** Constraints push data-quality rules down into the database itself, so bad data can never get in even if application code has a bug.

---

## 2️⃣ DDL — Data Definition Language

```sql
ALTER TABLE employees ADD COLUMN phone VARCHAR(15);   -- add a column
ALTER TABLE employees DROP COLUMN phone;               -- remove a column
TRUNCATE TABLE projects;                               -- wipe all rows, keep structure
DROP TABLE projects;                                   -- delete table entirely
```
**Output:** `Table altered.` / `Table truncated.` / `Table dropped.`

**`DROP` vs `TRUNCATE` vs `DELETE`:**

| | Removes rows | Removes structure | `WHERE` allowed | Rollback-able |
|---|---|---|---|---|
| `DELETE` | Yes | No | Yes | Yes |
| `TRUNCATE` | Yes (all) | No | No | No (most RDBMS) |
| `DROP` | Yes (all) | Yes | No | No |

---

## 3️⃣ DML — Data Manipulation Language

```sql
INSERT INTO employees VALUES (106, 'Meera', 60000, 2, 103, '2024-07-01', 'meera@co.com');

UPDATE employees SET salary = salary * 1.10 WHERE dept_id = 1;

DELETE FROM employees WHERE emp_id = 106;
```
**Output:**
```
1 row inserted.
3 rows updated.
1 row deleted.
```

### `MERGE` (a.k.a. "upsert")
```sql
MERGE INTO employees e
USING (SELECT 106 AS emp_id, 'Meera' AS emp_name, 60000 AS salary) src
ON (e.emp_id = src.emp_id)
WHEN MATCHED THEN UPDATE SET e.salary = src.salary
WHEN NOT MATCHED THEN INSERT (emp_id, emp_name, salary) VALUES (src.emp_id, src.emp_name, src.salary);
```
**Why it matters:** Instead of writing separate "does it exist? update, else insert" logic in application code, `MERGE` does it atomically in one statement — common in ETL/data-sync jobs.

---

## 4️⃣ DQL — Basic `SELECT`

```sql
SELECT emp_name, salary FROM employees WHERE salary > 60000 ORDER BY salary DESC;
```
**Output:**
```
emp_name | salary
Avinash  | 104500
Kavya    | 71000
Rahul    | 68200
```

### Pattern matching & wildcards
```sql
SELECT emp_name FROM employees WHERE emp_name LIKE 'A%';   -- starts with A
```
**Output:** `Avinash`

### `DISTINCT`, `LIMIT`/`OFFSET`
```sql
SELECT DISTINCT dept_id FROM employees;              -- 1, 2, 3
SELECT * FROM employees ORDER BY salary DESC LIMIT 2 OFFSET 1;  -- skip 1, take 2 (pagination)
```

---

## 5️⃣ Aggregate Functions, `GROUP BY`, `HAVING`

```sql
SELECT dept_id, COUNT(*) AS total, AVG(salary) AS avg_salary, MAX(salary) AS max_salary
FROM employees
GROUP BY dept_id
HAVING AVG(salary) > 60000;
```
**Output:**
```
dept_id | total | avg_salary | max_salary
1       | 3     | 77366.67   | 104500
3       | 1     | 71000.00   | 71000
```
**`WHERE` vs `HAVING`:** `WHERE` filters rows before grouping; `HAVING` filters groups after aggregation (and is the only place you can filter on `AVG()`, `SUM()`, etc.).

---

## 6️⃣ Joins

```sql
-- INNER JOIN
SELECT e.emp_name, d.dept_name FROM employees e
INNER JOIN departments d ON e.dept_id = d.dept_id;
```
**Output:**
```
emp_name | dept_name
Avinash  | Engineering
Rahul    | Engineering
Priya    | HR
Kavya    | Finance
Sanjay   | Engineering
```

```sql
-- LEFT JOIN (all employees, dept_name NULL if no match)
-- RIGHT JOIN (all departments, even those with zero employees)
-- FULL OUTER JOIN (everything from both sides)
-- SELF JOIN — employee to manager
SELECT e.emp_name AS employee, m.emp_name AS manager
FROM employees e LEFT JOIN employees m ON e.manager_id = m.emp_id;
```
**Output:**
```
employee | manager
Avinash  | NULL
Rahul    | Avinash
Priya    | NULL
Kavya    | NULL
Sanjay   | Avinash
```

```sql
-- CROSS JOIN — every combination (rarely used directly, good for generating test data)
SELECT e.emp_name, d.dept_name FROM employees e CROSS JOIN departments d;
-- Output: 5 employees x 3 departments = 15 rows
```

---

## 7️⃣ `UNION`, `UNION ALL`, `INTERSECT`, `EXCEPT`

```sql
SELECT emp_name FROM employees WHERE dept_id = 1
UNION
SELECT emp_name FROM employees WHERE salary > 70000;
```
**Output (duplicates removed):**
```
emp_name
Avinash
Rahul
Sanjay
Kavya
```

```sql
SELECT emp_name FROM employees WHERE dept_id = 1
UNION ALL
SELECT emp_name FROM employees WHERE salary > 70000;
```
**Output:** Same as above but **Avinash appears twice** (once from each query) since `UNION ALL` keeps duplicates and skips the de-dup step — faster when you know there's no overlap or duplicates are fine.

```sql
-- INTERSECT: rows common to both queries
SELECT emp_id FROM employees WHERE dept_id = 1
INTERSECT
SELECT emp_id FROM employees WHERE salary > 70000;
-- Output: 101 (Avinash - the only one in both sets)

-- EXCEPT / MINUS: rows in first query but not second
SELECT emp_id FROM employees WHERE dept_id = 1
EXCEPT
SELECT emp_id FROM employees WHERE salary > 70000;
-- Output: 102, 105
```
**Rule:** All `UNION`/`INTERSECT`/`EXCEPT` queries must have the same number of columns with compatible data types.

---

## 8️⃣ Subqueries

```sql
-- Scalar subquery
SELECT emp_name, salary FROM employees
WHERE salary > (SELECT AVG(salary) FROM employees);

-- Subquery with IN
SELECT emp_name FROM employees
WHERE dept_id IN (SELECT dept_id FROM departments WHERE location = 'Bangalore');

-- Correlated subquery (re-evaluated per outer row)
SELECT emp_name, salary FROM employees e
WHERE salary > (SELECT AVG(salary) FROM employees WHERE dept_id = e.dept_id);

-- EXISTS
SELECT emp_name FROM employees e
WHERE EXISTS (SELECT 1 FROM projects p WHERE p.emp_id = e.emp_id);
```
**Output (last query — employees with at least one logged project):**
```
emp_name
Avinash
Rahul
Priya
```
**`IN` vs `EXISTS`:** `EXISTS` stops as soon as it finds one matching row (often faster on large correlated checks); `IN` materializes the full subquery result first — for large subquery result sets, `EXISTS` is usually preferred.

---

## 9️⃣ Common Table Expressions (CTE) & Recursive CTEs

```sql
WITH dept_avg AS (
    SELECT dept_id, AVG(salary) AS avg_sal
    FROM employees
    GROUP BY dept_id
)
SELECT e.emp_name, e.salary, d.avg_sal
FROM employees e
JOIN dept_avg d ON e.dept_id = d.dept_id
WHERE e.salary > d.avg_sal;
```
**Output:**
```
emp_name | salary | avg_sal
Avinash  | 104500 | 77366.67
```
**Why it matters:** CTEs let you name a subquery and reuse/reference it cleanly — far more readable than deeply nested subqueries, especially with multiple steps.

**Recursive CTE (org-chart traversal):**
```sql
WITH RECURSIVE org_chart AS (
    SELECT emp_id, emp_name, manager_id, 1 AS level
    FROM employees WHERE manager_id IS NULL
    UNION ALL
    SELECT e.emp_id, e.emp_name, e.manager_id, oc.level + 1
    FROM employees e
    JOIN org_chart oc ON e.manager_id = oc.emp_id
)
SELECT * FROM org_chart ORDER BY level;
```
**Output:**
```
emp_id | emp_name | manager_id | level
101    | Avinash  | NULL       | 1
103    | Priya    | NULL       | 1
104    | Kavya    | NULL       | 1
102    | Rahul    | 101        | 2
105    | Sanjay   | 101        | 2
```

---

## 🔟 Views

```sql
CREATE VIEW high_earners AS
SELECT emp_name, salary, dept_id FROM employees WHERE salary > 65000;

SELECT * FROM high_earners;
```
**Output:**
```
emp_name | salary | dept_id
Avinash  | 104500 | 1
Rahul    | 68200  | 1
Kavya    | 71000  | 3
```
**Why it matters:** A view is a saved, named query — it doesn't store data itself (unless it's a *materialized* view), but simplifies repeated complex queries and can restrict which columns/rows a user is exposed to (a form of security).

```sql
CREATE MATERIALIZED VIEW dept_salary_summary AS
SELECT dept_id, SUM(salary) AS total_salary FROM employees GROUP BY dept_id;
```
**Materialized vs normal view:** A materialized view **physically stores** the result set and must be manually/periodically refreshed — much faster to read, at the cost of possibly-stale data. A normal view re-runs the underlying query every time it's selected from.

---

## 1️⃣1️⃣ Indexing

```sql
CREATE INDEX idx_emp_dept ON employees(dept_id);
CREATE UNIQUE INDEX idx_emp_email ON employees(email);
```
**What it does:** An index is a separate data structure (typically a **B-Tree**) that lets the database jump directly to matching rows instead of scanning the entire table.

**Without an index:** `SELECT * FROM employees WHERE dept_id = 1;` on a 10-million-row table means a **full table scan** — reading every row.
**With an index on `dept_id`:** the engine does an **index seek**, jumping straight to the matching entries — orders of magnitude faster on large tables.

**Types of indexes:**
| Type | Use case |
|---|---|
| B-Tree (default) | General-purpose range/equality lookups |
| Unique Index | Enforces uniqueness + speeds lookups (e.g., email) |
| Composite (multi-column) | Queries filtering on multiple columns together, e.g., `(dept_id, salary)` |
| Covering Index | Includes all columns a query needs, so the engine never touches the actual table row |
| Full-Text Index | Fast text search inside large text columns |

**Trade-off (important interview point):** Indexes speed up `SELECT` but slow down `INSERT`/`UPDATE`/`DELETE`, because every write must also update the index structure. Over-indexing a write-heavy table is a real performance anti-pattern.

---

## 1️⃣2️⃣ Stored Procedures

```sql
CREATE PROCEDURE give_raise (IN emp_id_param INT, IN raise_pct DECIMAL)
BEGIN
    UPDATE employees
    SET salary = salary + (salary * raise_pct / 100)
    WHERE emp_id = emp_id_param;
END;

CALL give_raise(102, 10);
```
**Output:** `Rahul's salary updated from 68200 to 75020.`

**Why it matters:** Business logic that involves multiple steps (validation, multiple updates, conditional branching) can live in the database itself — reducing round-trips between application and database, and centralizing logic that many applications/scripts might need to call.

---

## 1️⃣3️⃣ Functions (User-Defined)

```sql
CREATE FUNCTION annual_salary(monthly_salary DECIMAL) RETURNS DECIMAL
BEGIN
    RETURN monthly_salary * 12;
END;

SELECT emp_name, annual_salary(salary) AS yearly_ctc FROM employees;
```
**Output:**
```
emp_name | yearly_ctc
Avinash  | 1254000
Rahul    | 818400
```
**Function vs Stored Procedure:** A **function** must return a value and can be used directly inside a `SELECT` statement; it generally can't modify data (no `INSERT`/`UPDATE`/`DELETE`). A **stored procedure** may or may not return a value, is called with `CALL`/`EXEC`, and is commonly used specifically *because* it performs data modifications or multi-step operations.

---

## 1️⃣4️⃣ Triggers

```sql
CREATE TRIGGER trg_salary_audit
AFTER UPDATE ON employees
FOR EACH ROW
BEGIN
    INSERT INTO salary_audit_log (emp_id, old_salary, new_salary, changed_at)
    VALUES (OLD.emp_id, OLD.salary, NEW.salary, NOW());
END;
```
**What happens:** Every time an `UPDATE` runs on `employees`, this trigger fires automatically and logs the change — no application code needs to remember to do it.

**Why it matters:** Triggers enforce rules or side effects (auditing, cascading updates, validation) that must **always** happen regardless of which application or script touches the table. **Caution (real-world lesson):** overusing triggers makes systems hard to debug, since "invisible" logic fires on simple-looking statements — a common root cause of "why did this UPDATE take 10x longer than expected?" incidents.

---

## 1️⃣5️⃣ Transactions & TCL

```sql
BEGIN TRANSACTION;
UPDATE employees SET salary = salary - 5000 WHERE emp_id = 101;
UPDATE employees SET salary = salary + 5000 WHERE emp_id = 102;
COMMIT;
```
**Output:** `Transaction committed` — both updates saved together.

```sql
BEGIN TRANSACTION;
UPDATE employees SET salary = salary - 100000 WHERE emp_id = 105;
ROLLBACK;
```
**Output:** `Transaction rolled back` — change fully undone.

**ACID:** Atomicity, Consistency, Isolation, Durability (see Q&A section for detail).

**Isolation levels (important for team-lead-level discussions):**
| Level | Prevents | Risk |
|---|---|---|
| Read Uncommitted | Nothing | Dirty reads |
| Read Committed | Dirty reads | Non-repeatable reads |
| Repeatable Read | Dirty + non-repeatable reads | Phantom reads |
| Serializable | Everything | Highest — but most locking/lowest concurrency |

---

## 1️⃣6️⃣ DCL

```sql
GRANT SELECT, INSERT ON employees TO 'analyst_user';
REVOKE INSERT ON employees FROM 'analyst_user';
```
**Output:** `Grant succeeded.` / `Revoke succeeded.`

---

## 1️⃣7️⃣ Window Functions

Window functions calculate a value **across a set of rows related to the current row**, without collapsing rows the way `GROUP BY` does.

```sql
SELECT emp_name, dept_id, salary,
       RANK() OVER (PARTITION BY dept_id ORDER BY salary DESC) AS dept_rank
FROM employees;
```
**Output:**
```
emp_name | dept_id | salary | dept_rank
Avinash  | 1       | 104500 | 1
Rahul    | 1       | 68200  | 2
Sanjay   | 1       | 59400  | 3
Kavya    | 3       | 71000  | 1
Priya    | 2       | 58000  | 1
```

```sql
-- Running total
SELECT emp_name, salary,
       SUM(salary) OVER (ORDER BY emp_id) AS running_total
FROM employees;

-- Compare to previous row
SELECT emp_name, salary,
       LAG(salary) OVER (ORDER BY emp_id) AS prev_emp_salary
FROM employees;
```
**Why it matters:** Before window functions, "rank within group" or "running total" required self-joins or correlated subqueries — slow and hard to read. `ROW_NUMBER()`, `RANK()`, `DENSE_RANK()`, `LAG()`, `LEAD()`, `SUM() OVER()` do this in one clean pass, and are heavily used in reporting/dashboard queries.

---

## 1️⃣8️⃣ Normalization & Denormalization

**Normalization** organizes tables to reduce data redundancy and avoid update anomalies.

- **1NF:** Each column holds atomic (indivisible) values, no repeating groups (e.g., don't store `"Java,Spring,SQL"` in one column — use a separate skills table).
- **2NF:** 1NF + every non-key column depends on the *whole* primary key (relevant for composite keys).
- **3NF:** 2NF + no column depends on another *non-key* column (no transitive dependency) — e.g., don't store `dept_location` inside `employees` when it already lives in `departments`; that's exactly why our sample schema splits them.
- **BCNF:** A stricter version of 3NF for edge cases with overlapping candidate keys.

**Denormalization** intentionally reintroduces redundancy (e.g., storing `dept_name` directly on `employees`) to avoid expensive joins in read-heavy reporting systems — a deliberate trade-off of write complexity/storage for read speed.

**Real-world framing:** OLTP systems (transactional, e.g., order processing) are usually normalized; OLAP/reporting/data-warehouse systems are often deliberately denormalized (star schema) for query speed.

---

## 1️⃣9️⃣ Query Optimization & Performance Tuning

```sql
EXPLAIN ANALYZE
SELECT e.emp_name, d.dept_name
FROM employees e
JOIN departments d ON e.dept_id = d.dept_id
WHERE e.salary > 60000;
```
**Output (simplified execution plan):**
```
Hash Join (cost=12.50..45.30 rows=3)
  -> Seq Scan on employees (cost=0.00..30.00 rows=3) [Filter: salary > 60000]
  -> Hash on departments (cost=0.00..5.00 rows=3)
```
`Seq Scan` here signals a full table scan — on a small table that's fine, but on a large one it's the first thing to investigate (e.g., add an index on `salary` if this filter runs constantly).

**Common optimization techniques:**
- Add indexes on columns used in `WHERE`, `JOIN`, and `ORDER BY` — but don't over-index write-heavy tables.
- Avoid `SELECT *` — fetch only needed columns to reduce I/O.
- Avoid functions on indexed columns in `WHERE` (e.g., `WHERE YEAR(join_date) = 2024` disables the index — rewrite as a range: `WHERE join_date >= '2024-01-01' AND join_date < '2025-01-01'`).
- Use `EXPLAIN`/`EXPLAIN ANALYZE` to see the actual execution plan before guessing.
- Batch large `UPDATE`/`DELETE` operations instead of one massive transaction (reduces lock duration and log growth).
- Use pagination (`LIMIT`/`OFFSET` or keyset pagination) instead of pulling entire large result sets.
- Denormalize selectively for read-heavy reporting paths.
- Watch for implicit type conversions in `WHERE` clauses — comparing a `VARCHAR` column to an `INT` literal can silently prevent index usage.

---

## 📊 Summary Table

| Category/Object | Key Commands | Purpose |
|---|---|---|
| DDL | `CREATE`, `ALTER`, `DROP`, `TRUNCATE` | Define structure |
| DML | `INSERT`, `UPDATE`, `DELETE`, `MERGE` | Modify data |
| DQL | `SELECT`, joins, unions, subqueries, CTEs, window functions | Retrieve/analyze data |
| DCL | `GRANT`, `REVOKE` | Control access |
| TCL | `COMMIT`, `ROLLBACK`, `SAVEPOINT` | Manage transaction lifecycle |
| View | `CREATE VIEW` | Saved reusable query |
| Index | `CREATE INDEX` | Speed up lookups |
| Stored Procedure | `CREATE PROCEDURE` / `CALL` | Reusable multi-step logic |
| Function | `CREATE FUNCTION` | Reusable value-returning logic |
| Trigger | `CREATE TRIGGER` | Automatic side effects on data change |

---

## 🎯 Interview Questions — Core SQL

**Q1. What are the five SQL command categories?**
A: DDL (structure: `CREATE`/`ALTER`/`DROP`), DML (data: `INSERT`/`UPDATE`/`DELETE`), DQL (`SELECT`), DCL (permissions: `GRANT`/`REVOKE`), TCL (transactions: `COMMIT`/`ROLLBACK`/`SAVEPOINT`).

**Q2. Difference between `DELETE`, `TRUNCATE`, `DROP`?**
A: `DELETE` (DML) removes specific rows and is rollback-able; `TRUNCATE` (DDL) removes all rows instantly without row-level logging and generally isn't rollback-able; `DROP` (DDL) removes the entire table including its structure, permanently.

**Q3. `WHERE` vs `HAVING`?**
A: `WHERE` filters rows before grouping and can't use aggregate functions; `HAVING` filters groups after `GROUP BY` and is where aggregate conditions belong.

**Q4. Explain all JOIN types.**
A: `INNER JOIN` — matches only; `LEFT/RIGHT JOIN` — all rows from one side plus matches; `FULL OUTER JOIN` — all rows from both sides; `SELF JOIN` — a table joined to itself (e.g., employee-manager); `CROSS JOIN` — Cartesian product of both tables.

**Q5. `UNION` vs `UNION ALL`?**
A: `UNION` removes duplicate rows across the combined result (extra sort/dedup cost); `UNION ALL` keeps every row including duplicates and is faster since it skips deduplication.

**Q6. What is a subquery vs a CTE — when would you prefer one?**
A: Both let you use a query's result inside another query. A CTE (`WITH ... AS (...)`) is named, more readable, can be referenced multiple times in the same statement, and supports recursion — preferable for multi-step or hierarchical logic. A subquery is often simpler for a single, one-off filter.

**Q7. What is an index, and what's the trade-off of adding one?**
A: An index is typically a B-Tree structure that lets the engine locate rows without scanning the whole table, dramatically speeding up `SELECT`/`JOIN`/`ORDER BY` on the indexed column(s). The trade-off is slower writes (`INSERT`/`UPDATE`/`DELETE`) since every index must also be updated, plus extra storage — so indexes should be added deliberately based on actual query patterns, not "just in case."

**Q8. Stored Procedure vs Function?**
A: A function must return a value, can be called inline within a `SELECT`, and typically cannot modify data. A stored procedure may or may not return a value, is invoked with `CALL`, and is commonly used for multi-step logic including data modification.

**Q9. What is a Trigger, and what's a real risk of overusing them?**
A: A trigger automatically executes logic in response to a table event (`INSERT`/`UPDATE`/`DELETE`), e.g., writing an audit log. The real risk is hidden complexity — a simple-looking `UPDATE` can silently cascade into multiple trigger-driven side effects, making performance issues and bugs much harder to diagnose, since the trigger logic isn't visible at the call site.

**Q10. What are ACID properties?**
A: Atomicity (all-or-nothing execution), Consistency (valid state transitions honoring constraints), Isolation (concurrent transactions don't see each other's uncommitted changes), Durability (committed data survives crashes).

**Q11. What is normalization, and name the first three normal forms.**
A: Normalization organizes data to reduce redundancy and prevent update anomalies. 1NF requires atomic column values with no repeating groups; 2NF requires every non-key column to depend on the whole primary key; 3NF requires no non-key column to depend on another non-key column (no transitive dependency).

**Q12. What are window functions, and how do they differ from `GROUP BY`?**
A: Window functions (`ROW_NUMBER()`, `RANK()`, `LAG()`, `SUM() OVER()`) compute a value across a set of related rows **without collapsing them** into a single summary row — every original row is still returned, just enriched with the computed value (e.g., "this employee's rank within their department"). `GROUP BY` collapses rows into one row per group.

---

## 🧑‍💼 Real-World & Team-Lead-Level Scenario Questions (Performance & Production Issues)

**Q13. "A report query that used to run in 2 seconds now takes 45 seconds after months in production. As team lead, how would you approach diagnosing this?"**
A: I'd approach it systematically rather than guessing:
1. **Check data growth** — the table may have grown 10x since the query was written; a query that was fine at 50K rows can be terrible at 5M rows.
2. **Run `EXPLAIN ANALYZE`** on the current query to see the actual execution plan — is it doing a `Seq Scan` where it used to use an index? Statistics can go stale, causing the optimizer to pick a bad plan.
3. **Check for missing/unused indexes** — verify the index the query relied on still exists and is actually being used (an added column, a changed `WHERE` clause, or a wrapped function on the column can silently disable it).
4. **Check for lock contention** — is a long-running batch job or another transaction holding locks on the same rows during the report window?
5. **Check for parameter sniffing / plan caching issues** (in databases like SQL Server) — a cached plan optimized for one parameter value can be very wrong for another.
6. Only after root-causing would I apply a fix — e.g., adding an index, rewriting the query, or scheduling the report outside peak load — and I'd document the finding so the team learns from it, not just patch it silently.

**Q14. "Multiple team members are getting deadlock errors intermittently in production. How do you lead the investigation and prevent recurrence?"**
A: A deadlock happens when two transactions each hold a lock the other needs, and neither can proceed. As lead, I'd:
1. Pull the **deadlock graph/log** from the database (most RDBMS log the deadlock victim plus the competing transaction's queries).
2. Identify the **access pattern mismatch** — commonly, two transactions update the same tables in a *different order* (e.g., Transaction A updates `orders` then `inventory`; Transaction B updates `inventory` then `orders`).
3. Standardize the **lock acquisition order** across the codebase (always touch tables in the same sequence) — this is usually the actual fix, not just "add retry logic."
4. Reduce transaction scope — the longer a transaction holds locks, the higher the deadlock probability; keep transactions as short as possible and avoid unrelated work (e.g., calling an external API) inside a DB transaction.
5. Add **retry-with-backoff** at the application layer as a safety net for the rare remaining cases, since deadlocks can never be fully eliminated in a highly concurrent system — but retry should be the safety net, not the primary fix.

**Q15. "A junior developer added an index to speed up a slow `SELECT`, but now the nightly batch `UPDATE` job is much slower. How do you explain this and what's your resolution?"**
A: This is the classic **read/write trade-off of indexing**. Every index added to a table must be updated on every `INSERT`/`UPDATE`/`DELETE` that touches the indexed column(s), so a query that speeds up reads can measurably slow down writes — especially on bulk batch jobs updating thousands/millions of rows.
Resolution approach: (1) confirm the index is actually necessary and used by real query traffic (e.g., `pg_stat_user_indexes` or the equivalent) rather than "just helpful in theory"; (2) if it's genuinely needed for reads but hurts batch writes, consider **disabling/dropping the index before the bulk job and rebuilding it after**, which is often dramatically faster than maintaining the index row-by-row during the load; (3) evaluate whether a narrower/composite index would serve the same read query at lower write cost than a broad single-column index.

**Q16. "Your microservice's connection pool keeps exhausting under load, and the database shows many idle-in-transaction connections. What's happening and how do you fix it as a team lead?"**
A: "Idle in transaction" means a transaction was opened (`BEGIN`) but never committed/rolled back promptly — often because application code opened a transaction, then did slow work (an external API call, or waiting on application logic) *before* committing, holding the connection and locks the whole time. Under load, this exhausts the connection pool because connections aren't being returned quickly. As lead, I'd: (1) audit code paths for transactions that span non-DB work — move any external calls outside the transaction boundary; (2) set a **statement/transaction timeout** at the database or connection-pool level as a safety net; (3) review connection pool sizing versus actual concurrent load; (4) add monitoring/alerting on long-running or idle-in-transaction sessions so this is caught in staging, not production.

**Q17. "How would you decide whether to add an index, rewrite the query, or denormalize the schema to fix a slow endpoint, and how do you communicate that trade-off to the team?"**
A: I treat it as a decision tree based on root cause, not a default reflex:
- If `EXPLAIN` shows a full scan on a large table for a **selective filter** → add a targeted index first; it's the cheapest fix with no application code change.
- If the query itself is inefficient (e.g., unnecessary subqueries, `SELECT *`, functions wrapping indexed columns, or N+1 query patterns from the application layer) → rewrite the query/ORM usage before touching the schema; a bad query with a great index is still bad.
- If the bottleneck is fundamentally **too many joins for a read-heavy, high-traffic path** (e.g., a dashboard aggregating across 5+ normalized tables on every request) → consider denormalization or a materialized view/summary table refreshed on a schedule, accepting eventual consistency in exchange for read speed.
  I'd communicate this to the team with the actual execution-plan evidence (not intuition), the expected trade-off (write cost, staleness, maintenance burden), and get agreement before merging — since schema-level fixes affect everyone, unlike a query rewrite.

**Q18. "Two features on your team both need to update the same `accounts` table heavily, and QA is reporting sporadic slowness only under concurrent load. How do you lead root-causing this?"**
A: Sporadic-under-concurrency symptoms usually point to **lock contention or isolation-level side effects**, not a query-plan problem (which would be slow consistently, not sporadically). I'd: (1) capture lock wait statistics during a load test to confirm contention on `accounts` specifically; (2) check whether both features are locking broader ranges than necessary — e.g., an `UPDATE` without a tight `WHERE`/index causing row-level locks to escalate to page/table-level locks; (3) check the transaction isolation level — `SERIALIZABLE` or overly broad `REPEATABLE READ` usage can cause avoidable blocking versus `READ COMMITTED`; (4) evaluate whether the two features can be redesigned to touch narrower row sets or use optimistic concurrency (a version column with conditional updates) instead of long pessimistic locks. I'd also use this as a team learning moment — documenting the contention pattern so future features on shared hot tables are designed with this in mind from day one.

**Q19. "As team lead, how do you review a teammate's PR that adds a new SQL query, specifically for performance risk, before it reaches production?"**
A: I look for a specific checklist rather than a vague "looks fine": (1) does the `WHERE`/`JOIN` use columns that are actually indexed, and does the index match the leading column order for composite indexes; (2) is it selecting only needed columns, not `SELECT *`; (3) is there a risk of N+1 queries if this is called inside a loop from application code (a very common ORM pitfall); (4) for any query touching a table expected to grow significantly, has it been tested against a realistic data volume, not just the tiny dev/test dataset; (5) if it's a write operation, is the transaction scope minimal, and does it avoid non-DB work inside the transaction; (6) for reporting-style queries, would a scheduled aggregation or materialized view be a better fit than computing it live on every request. I'd rather flag this early in review than debug it as a live incident later.

---

## ✅ Conclusion

SQL is far more than `SELECT` statements — a genuinely strong SQL foundation spans schema design and constraints (DDL), safe multi-step data changes (DML, TCL), rich retrieval and analytics (DQL: joins, unions, subqueries, CTEs, window functions), access governance (DCL), and the supporting objects — views, indexes, stored procedures, functions, and triggers — that make a database maintainable at scale. Layered on top of that is the theory (normalization, ACID, isolation levels) and the practical skill that separates a mid-level developer from a team lead: the ability to **diagnose real production slowness systematically** — reading execution plans, understanding lock contention, knowing the true cost of an index, and making deliberate, well-communicated trade-offs rather than guessing. That combination of depth and judgment is exactly what senior/team-lead-level interviews are testing for.