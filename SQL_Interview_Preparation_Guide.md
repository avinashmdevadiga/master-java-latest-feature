# 🎯 SQL Interview Preparation Guide — For Java Full Stack Developers (8+ Years Experience)

> **Audience:** Senior Java Full Stack Developers targeting Product Companies, Service-Based Companies, MNCs, and FAANG-level interviews.
> **Author Persona:** Senior Database Architect · SQL Performance Expert · Technical Interviewer (20+ years)
> **Coverage:** Beginner → Intermediate → Advanced → Expert, with RDBMS-specific notes (MySQL, PostgreSQL, SQL Server, Oracle).

---

## 📚 Table of Contents

| # | Topic | Level |
|---|---|---|
| 1 | [SQL Fundamentals](#1-sql-fundamentals) | Beginner |
| 2 | [SQL Data Types](#2-sql-data-types) | Beginner |
| 3 | [Constraints](#3-constraints) | Beginner |
| 4 | [Joins](#4-joins-very-important) | Intermediate |
| 5 | [SQL Operators](#5-sql-operators) | Beginner |
| 6 | [Aggregate Functions](#6-aggregate-functions) | Intermediate |
| 7 | [GROUP BY and HAVING](#7-group-by-and-having) | Intermediate |
| 8 | [Subqueries](#8-subqueries) | Intermediate |
| 9 | [SQL Functions](#9-sql-functions) | Intermediate |
| 10 | [Set Operators](#10-set-operators) | Intermediate |
| 11 | [Views](#11-views) | Intermediate |
| 12 | [Indexing](#12-indexing-important) | Advanced |
| 13 | [Normalization](#13-normalization) | Advanced |
| 14 | [Transactions (ACID)](#14-transactions) | Advanced |
| 15 | [Isolation Levels](#15-isolation-levels) | Advanced |
| 16 | [Stored Procedures](#16-stored-procedures) | Advanced |
| 17 | [Functions (UDFs)](#17-functions) | Advanced |
| 18 | [Triggers](#18-triggers) | Advanced |
| 19 | [Common Table Expressions (CTE)](#19-common-table-expressions-cte) | Advanced |
| 20 | [Window Functions](#20-window-functions-very-important) | Expert |
| 21 | [Performance Tuning](#21-performance-tuning-most-important-for-8-years) | Expert |
| 22 | [SQL Scenarios (50 Questions)](#22-sql-scenarios-asked-in-real-interviews) | Expert |
| 23 | [SQL in Java Full Stack Projects](#23-sql-in-java-full-stack-projects) | Expert |
| 24 | [SQL System Design Concepts](#24-sql-system-design-concepts) | Expert |
| 25 | [Top 100 SQL Interview Q&A](#25-top-100-sql-interview-questions-and-answers) | All Levels |
| 26 | [Top 25 SQL Coding Problems](#26-top-25-sql-coding-problems) | Expert |
| 27 | [HR + Technical SQL Round Prep](#27-hr--technical-sql-round-preparation) | All Levels |
| 28 | [Final Revision Sheet](#28-final-revision-sheet) | All Levels |

---

## 🧩 Common Schema Used Throughout This Guide

```sql
CREATE TABLE department (
    dept_id     INT PRIMARY KEY,
    dept_name   VARCHAR(50) NOT NULL,
    location    VARCHAR(50)
);

CREATE TABLE employee (
    emp_id      INT PRIMARY KEY,
    emp_name    VARCHAR(50) NOT NULL,
    salary      DECIMAL(10,2) CHECK (salary > 0),
    dept_id     INT,
    manager_id  INT,
    join_date   DATE,
    email       VARCHAR(100) UNIQUE,
    FOREIGN KEY (dept_id) REFERENCES department(dept_id),
    FOREIGN KEY (manager_id) REFERENCES employee(emp_id)
);

CREATE TABLE customer (
    customer_id   INT PRIMARY KEY,
    customer_name VARCHAR(50),
    city          VARCHAR(50),
    email         VARCHAR(100) UNIQUE
);

CREATE TABLE orders (
    order_id     INT PRIMARY KEY,
    customer_id  INT,
    order_date   DATE,
    amount       DECIMAL(10,2),
    status       VARCHAR(20),
    FOREIGN KEY (customer_id) REFERENCES customer(customer_id)
);
```

**Sample Data**

department

| dept_id | dept_name | location |
|---|---|---|
| 1 | Engineering | Bangalore |
| 2 | HR | Mumbai |
| 3 | Finance | Delhi |
| 4 | Marketing | NULL |

employee

| emp_id | emp_name | salary | dept_id | manager_id | join_date |
|---|---|---|---|---|---|
| 101 | Avinash | 104500 | 1 | NULL | 2023-04-10 |
| 102 | Rahul | 68200 | 1 | 101 | 2023-06-15 |
| 103 | Priya | 58000 | 2 | NULL | 2022-11-01 |
| 104 | Kavya | 71000 | 3 | NULL | 2024-01-20 |
| 105 | Sanjay | 71000 | 1 | 101 | 2024-03-05 |
| 106 | Meera | 58000 | 2 | 103 | 2024-07-01 |

customer / orders — used in Joins, Set Operators, Scenarios (defined inline where needed).

---

## 1. SQL Fundamentals

### 1.1 What is a Database?
A **database** is an organized collection of structured data stored electronically, designed for efficient storage, retrieval, update, and management. Example: an e-commerce platform stores customers, products, and orders in a database instead of flat files, enabling fast lookups and consistent updates.

### 1.2 What is RDBMS?
A **Relational Database Management System (RDBMS)** stores data in **tables (relations)** made of rows and columns, and enforces relationships between tables using **keys** (primary/foreign). Examples: MySQL, PostgreSQL, Oracle, SQL Server.

### 1.3 DBMS vs RDBMS

| Aspect | DBMS | RDBMS |
|---|---|---|
| Data Storage | Files/navigational/hierarchical | Tables (rows & columns) |
| Relationships | Not enforced | Enforced via Primary/Foreign Keys |
| Normalization | Not supported | Supported |
| ACID Compliance | Not guaranteed | Guaranteed |
| Examples | XML DB, File System DB | MySQL, Oracle, PostgreSQL, SQL Server |
| Multi-user access | Limited | Full support with concurrency control |
| Data Redundancy | High | Low (via normalization) |

**Interview Q:** *Is a spreadsheet a DBMS or RDBMS?* → Neither — it lacks enforced schema, referential integrity, and ACID guarantees.

### 1.4 SQL Overview
**SQL (Structured Query Language)** is a **declarative** language used to define, manipulate, query, and control access to relational data. "Declarative" means you specify **what** data you want, not **how** to fetch it — the query optimizer decides the execution plan.

### 1.5 SQL Command Types

```
┌─────────────────────────────────────────────────────────┐
│                     SQL COMMANDS                         │
├───────────┬───────────┬───────────┬───────────┬─────────┤
│    DDL    │    DML    │    DQL    │    DCL    │   TCL   │
│ (Structure)│  (Data)  │ (Query)   │(Permission)│(Txn Mgmt)│
├───────────┼───────────┼───────────┼───────────┼─────────┤
│ CREATE    │ INSERT    │ SELECT    │ GRANT     │ COMMIT  │
│ ALTER     │ UPDATE    │           │ REVOKE    │ ROLLBACK│
│ DROP      │ DELETE    │           │           │ SAVEPOINT│
│ TRUNCATE  │ MERGE     │           │           │ SET TXN │
│ RENAME    │           │           │           │         │
└───────────┴───────────┴───────────┴───────────┴─────────┘
```

| Type | Full Form | Commands | Auto-Commit? |
|---|---|---|---|
| DDL | Data Definition Language | CREATE, ALTER, DROP, TRUNCATE, RENAME | Yes (implicit commit) |
| DML | Data Manipulation Language | INSERT, UPDATE, DELETE, MERGE | No (needs COMMIT) |
| DQL | Data Query Language | SELECT | N/A (read-only) |
| DCL | Data Control Language | GRANT, REVOKE | Yes |
| TCL | Transaction Control Language | COMMIT, ROLLBACK, SAVEPOINT | N/A |

### 1.6 Command Examples

```sql
-- DDL
CREATE TABLE employee (emp_id INT PRIMARY KEY, emp_name VARCHAR(50));
ALTER TABLE employee ADD COLUMN phone VARCHAR(15);
ALTER TABLE employee DROP COLUMN phone;
TRUNCATE TABLE employee;      -- removes all rows, keeps structure, resets identity
DROP TABLE employee;          -- removes table + structure permanently

-- DML
INSERT INTO employee (emp_id, emp_name) VALUES (107, 'Neha');
UPDATE employee SET emp_name = 'Neha Sharma' WHERE emp_id = 107;
DELETE FROM employee WHERE emp_id = 107;

-- DQL
SELECT emp_name, salary FROM employee WHERE salary > 60000;
```

### 🎯 Interview Q&A — SQL Fundamentals

**Q1. Why is SQL called a "declarative" language?**
A: You describe the desired result set (`SELECT name FROM employee WHERE salary > 50000`), not the retrieval algorithm. The **query optimizer** chooses the execution strategy (index scan, hash join, etc.) — unlike imperative code where you write the loop yourself.

**Q2. Why does `TRUNCATE` reset auto-increment but `DELETE` does not?**
A: `TRUNCATE` is a DDL operation that deallocates the table's data pages and resets internal metadata (including identity/auto-increment counters) as a minimally-logged operation. `DELETE` is DML — it removes rows one at a time (fully logged, so it can be rolled back), and does **not** touch the identity counter.

**Q3. Can `DDL` statements be rolled back?**
A: In most RDBMS (MySQL/InnoDB with auto-commit, Oracle, SQL Server outside explicit transactions) DDL auto-commits immediately and cannot be rolled back. PostgreSQL and SQL Server (inside an explicit `BEGIN TRANSACTION`) are notable exceptions — they support **transactional DDL**.

**Q4. What's the difference between `RENAME` and `ALTER ... RENAME`?**
A: `RENAME TABLE old_name TO new_name;` (MySQL) is a standalone DDL statement; `ALTER TABLE old_name RENAME TO new_name;` is the ANSI/PostgreSQL/Oracle style. Same effect, different syntax family.

**Q5 (FAANG-style). If SQL is declarative, how does the same query sometimes run fast one day and slow the next with unchanged data?**
A: The optimizer's decision depends on **statistics** (row counts, data distribution/histograms) which change as data grows or skews. A stale statistics snapshot can cause the optimizer to pick a suboptimal plan (e.g., nested loop instead of hash join) — this is a common real-world performance regression, solved by updating statistics (`ANALYZE`, `UPDATE STATISTICS`) or forcing a plan via hints.

---

## 2. SQL Data Types

| Data Type | Storage | Use Case | Notes |
|---|---|---|---|
| `INT` | 4 bytes | IDs, counts, age | Range ≈ ±2.1 billion |
| `BIGINT` | 8 bytes | High-volume PKs (e.g., transaction IDs) | Use when INT may overflow (>2.1B rows) |
| `DECIMAL(p,s)` | Variable (exact) | Money, financial calculations | **Always use for currency** — no rounding error |
| `FLOAT` / `DOUBLE` | 4/8 bytes (approx) | Scientific measurements | **Never use for money** — binary rounding errors |
| `CHAR(n)` | Fixed length | Fixed-size codes (e.g., country code 'IN') | Padded with spaces, faster for fixed-width data |
| `VARCHAR(n)` | Variable length | Names, emails, descriptions | Stores only actual length + overhead |
| `TEXT` / `CLOB` | Large variable | Long content (articles, JSON blobs) | Usually stored off-row; limited indexing |
| `DATE` | 3–4 bytes | Birthdates, join dates | No time component |
| `TIMESTAMP` / `DATETIME` | 8 bytes | Audit columns, event logs | `TIMESTAMP` (MySQL) auto-converts to UTC; `DATETIME` does not |
| `BOOLEAN` | 1 byte | Flags (is_active, is_deleted) | Oracle has no native BOOLEAN (uses NUMBER(1) / CHAR(1)) |

### Best Practices
- Use `DECIMAL(10,2)` for money — never `FLOAT`/`DOUBLE` (binary floating point cannot represent 0.1 exactly, causing cumulative rounding errors in financial systems).
- Prefer `VARCHAR` over `CHAR` unless every value truly has the same fixed length (e.g., ISO country codes).
- Use `BIGINT` for primary keys in tables expected to exceed 2 billion rows (e.g., event logs, audit tables in high-traffic systems).
- Store all timestamps in **UTC** and convert to local time zone at the presentation layer — critical in distributed Java microservices.

### 🎯 Interview Q&A — Data Types
**Q1. Why should money never be stored as FLOAT?**
A: `FLOAT`/`DOUBLE` use IEEE-754 binary representation, which cannot exactly represent many decimal fractions (e.g., 0.1 + 0.2 ≠ 0.3 in binary float). Over many transactions, rounding errors accumulate — a critical bug class in banking systems. `DECIMAL`/`NUMERIC` stores exact base-10 digits.

**Q2. `CHAR` vs `VARCHAR` — performance impact?**
A: `CHAR(n)` is fixed-width — faster for the engine to compute row offsets (good for indexes on short fixed codes like a 2-letter state code), but wastes space via padding. `VARCHAR(n)` stores a length prefix + actual data — more space-efficient for variable-length text but requires a small computation to locate the next column.

**Q3. `TIMESTAMP` vs `DATETIME` (MySQL specific)?**
A: `TIMESTAMP` is stored in UTC internally and converted to session time zone on retrieval, has a range until 2038 (in older MySQL versions), and auto-updates on row modification if configured. `DATETIME` stores the literal value with no time zone conversion and has a wider range (0001–9999).

**Q4. How would you model a multi-currency amount in a global e-commerce schema?**
A: Store `amount DECIMAL(19,4)` alongside a separate `currency_code CHAR(3)` (ISO 4217) column, and keep exchange rates in a separate versioned table — never bake a converted value directly into the amount column without an audit trail.

---

## 3. Constraints

| Constraint | Purpose | Example |
|---|---|---|
| PRIMARY KEY | Uniquely identifies each row; implies `NOT NULL` + `UNIQUE` | `emp_id INT PRIMARY KEY` |
| FOREIGN KEY | Enforces referential integrity between tables | `FOREIGN KEY (dept_id) REFERENCES department(dept_id)` |
| UNIQUE | No duplicate values (nullable, multiple NULLs allowed in most RDBMS) | `email VARCHAR(100) UNIQUE` |
| NOT NULL | Column must always have a value | `emp_name VARCHAR(50) NOT NULL` |
| CHECK | Validates a condition on insert/update | `CHECK (salary > 0)` |
| DEFAULT | Auto-fills value when none provided | `join_date DATE DEFAULT CURRENT_DATE` |
| COMPOSITE KEY | Primary key spanning multiple columns | `PRIMARY KEY (order_id, product_id)` |

### Real-World Schema

```sql
CREATE TABLE department (
    dept_id   INT PRIMARY KEY,
    dept_name VARCHAR(50) NOT NULL UNIQUE
);

CREATE TABLE employee (
    emp_id     INT PRIMARY KEY,
    emp_name   VARCHAR(50) NOT NULL,
    salary     DECIMAL(10,2) CHECK (salary > 0),
    dept_id    INT,
    email      VARCHAR(100) UNIQUE,
    status     VARCHAR(10) DEFAULT 'ACTIVE',
    FOREIGN KEY (dept_id) REFERENCES department(dept_id)
);

CREATE TABLE customer (
    customer_id INT PRIMARY KEY,
    name        VARCHAR(50) NOT NULL,
    email       VARCHAR(100) UNIQUE NOT NULL
);

CREATE TABLE order_item (
    order_id   INT,
    product_id INT,
    quantity   INT CHECK (quantity > 0),
    PRIMARY KEY (order_id, product_id)   -- COMPOSITE KEY
);
```

### 🎯 Interview Q&A — Constraints

**Q1. Can a table have multiple PRIMARY KEYs?**
A: No — only **one** primary key per table, but it can span multiple columns (composite key). A table can have multiple `UNIQUE` constraints though.

**Q2. Difference between `PRIMARY KEY` and `UNIQUE`?**
A: Primary key = unique + not null + exactly one per table + typically used to define clustered index (in SQL Server/MySQL InnoDB). Unique allows one (Oracle/SQL Server) or multiple (MySQL/PostgreSQL) NULL values and a table can have many.

**Q3. What happens on `DELETE` of a parent row referenced by a foreign key?**
A: Depends on the referential action defined:
```sql
FOREIGN KEY (dept_id) REFERENCES department(dept_id)
    ON DELETE CASCADE     -- deletes child rows too
    ON DELETE SET NULL    -- sets FK column to NULL
    ON DELETE RESTRICT    -- (default) blocks the delete if children exist
    ON DELETE NO ACTION   -- similar to RESTRICT
```
Real-world: An e-commerce `orders` table referencing `customer` should typically use `RESTRICT`/`NO ACTION` (don't silently delete order history) rather than `CASCADE`.

**Q4. Why prefer `CHECK` constraints over application-level validation only?**
A: Defense in depth — multiple applications, batch jobs, or manual SQL scripts might write to the same table; a DB-level `CHECK` guarantees the invariant (e.g., `salary > 0`) regardless of which code path writes the data.

**Q5 (8+ yrs). Composite key vs surrogate key — which do you prefer and why?**
A: I generally prefer a **surrogate key** (auto-increment `BIGINT` or UUID) as the primary key even when a natural composite key exists, because: (1) foreign keys referencing a single narrow integer column are cheaper to index and join than multi-column composite FKs; (2) natural keys can change (e.g., email, SSN-like identifiers) which cascades painfully through FK relationships. I still add a `UNIQUE` composite constraint on the natural key columns to preserve business-rule integrity.

---

## 4. Joins (VERY IMPORTANT)

```
INNER JOIN          LEFT JOIN            RIGHT JOIN           FULL OUTER JOIN
  ┌───┬───┐           ┌───┬───┐            ┌───┬───┐            ┌───┬───┐
  │ A ∩ B │           │███│ ∩ │            │ ∩ │███│            │███│███│
  └───┴───┘           └───┴───┘            └───┴───┘            └───┴───┘
  (A,B) match         All A + matched B    All B + matched A    All A + All B
```

**Sample Tables:**

employee

| emp_id | emp_name | dept_id |
|---|---|---|
| 101 | Avinash | 1 |
| 102 | Rahul | 1 |
| 103 | Priya | NULL |

department

| dept_id | dept_name |
|---|---|
| 1 | Engineering |
| 2 | HR |

### 4.1 INNER JOIN
**Definition:** Returns rows only when there's a match in both tables.
```sql
SELECT e.emp_name, d.dept_name
FROM employee e
INNER JOIN department d ON e.dept_id = d.dept_id;
```
**Output:**
```
emp_name | dept_name
Avinash  | Engineering
Rahul    | Engineering
```
**Real-world:** Fetch orders that actually belong to a valid, existing customer (excludes orphaned test data).
**Interview Q:** *Priya (dept_id NULL) is missing — why?* A: `NULL` never equals anything, including in a join predicate, so Priya has no matching row and is excluded.

### 4.2 LEFT JOIN
**Definition:** All rows from the left table, plus matched rows from the right (NULL if no match).
```sql
SELECT e.emp_name, d.dept_name
FROM employee e
LEFT JOIN department d ON e.dept_id = d.dept_id;
```
**Output:**
```
emp_name | dept_name
Avinash  | Engineering
Rahul    | Engineering
Priya    | NULL
```
**Real-world:** List all employees, including those not yet assigned a department (onboarding scenario).
**Interview Q:** *How do you find employees with NO department using this join?*
```sql
SELECT e.emp_name FROM employee e
LEFT JOIN department d ON e.dept_id = d.dept_id
WHERE d.dept_id IS NULL;
```

### 4.3 RIGHT JOIN
**Definition:** All rows from the right table, plus matched rows from the left.
```sql
SELECT e.emp_name, d.dept_name
FROM employee e
RIGHT JOIN department d ON e.dept_id = d.dept_id;
```
**Output:** includes HR department even though no employee belongs to it (`emp_name = NULL`).
**Real-world:** List all departments including empty ones — useful for HR headcount reports.
**Interview Q:** *Can every RIGHT JOIN be rewritten as a LEFT JOIN?* A: Yes — swap table order: `department d LEFT JOIN employee e`. Many teams standardize on `LEFT JOIN` only for readability (MySQL/PostgreSQL style guides commonly disallow RIGHT JOIN).

### 4.4 FULL OUTER JOIN
**Definition:** All rows from both tables; unmatched sides get NULL.
```sql
SELECT e.emp_name, d.dept_name
FROM employee e
FULL OUTER JOIN department d ON e.dept_id = d.dept_id;
```
**Output:** Avinash/Engineering, Rahul/Engineering, Priya/NULL, NULL/HR.
**Note:** MySQL has **no native `FULL OUTER JOIN`** — emulate with `LEFT JOIN UNION RIGHT JOIN`:
```sql
SELECT e.emp_name, d.dept_name FROM employee e LEFT JOIN department d ON e.dept_id = d.dept_id
UNION
SELECT e.emp_name, d.dept_name FROM employee e RIGHT JOIN department d ON e.dept_id = d.dept_id;
```
**Real-world:** Reconcile two systems (e.g., payroll vs HRMS) to find mismatches in both directions.

### 4.5 CROSS JOIN
**Definition:** Cartesian product — every row of A with every row of B.
```sql
SELECT e.emp_name, d.dept_name FROM employee e CROSS JOIN department d;
-- 3 employees x 2 departments = 6 rows
```
**Real-world:** Generating a calendar × store combination matrix for a sales-forecast report, or all size×color combos for a product catalog.

### 4.6 SELF JOIN
**Definition:** A table joined with itself, typically via aliases, to model hierarchical/recursive relationships.
```sql
SELECT e.emp_name AS employee, m.emp_name AS manager
FROM employee e
LEFT JOIN employee m ON e.manager_id = m.emp_id;
```
**Real-world:** Employee–manager hierarchy, category–subcategory trees, referral chains.

### 🎯 Difficult Join Questions (8+ Years)

**Q1. Find employees who earn more than their manager.**
```sql
SELECT e.emp_name AS employee, e.salary, m.emp_name AS manager, m.salary AS mgr_salary
FROM employee e
JOIN employee m ON e.manager_id = m.emp_id
WHERE e.salary > m.salary;
```

**Q2. Find departments with NO employees (anti-join pattern).**
```sql
SELECT d.dept_name
FROM department d
LEFT JOIN employee e ON d.dept_id = e.dept_id
WHERE e.emp_id IS NULL;
```

**Q3. Why does adding a filter in `WHERE` vs the `ON` clause change LEFT JOIN results?**
```sql
-- (A) Filter in ON clause — preserves LEFT JOIN semantics
SELECT e.emp_name, d.dept_name FROM employee e
LEFT JOIN department d ON e.dept_id = d.dept_id AND d.location = 'Bangalore';

-- (B) Filter in WHERE clause — effectively turns it into an INNER JOIN
SELECT e.emp_name, d.dept_name FROM employee e
LEFT JOIN department d ON e.dept_id = d.dept_id
WHERE d.location = 'Bangalore';
```
A: In (A), the condition is evaluated **during the join**, so unmatched left rows are still kept (with NULL department columns) if the department doesn't match the location. In (B), the `WHERE` clause is evaluated **after** the join completes, and since NULL never equals 'Bangalore', all the outer-joined NULL rows get filtered out — silently converting it to an INNER JOIN. This is one of the most common subtle bugs junior-to-mid developers write.

**Q4 (FAANG-style). Three-table join order — does SQL execute joins in the order written?**
A: No. SQL is declarative; the **query optimizer** chooses join order based on cardinality estimates, available indexes, and cost — it may reorder joins (e.g., a 3-table join might execute B⋈C first if that produces a smaller intermediate result) unless a hint forces an order. Understanding `EXPLAIN` output is essential to verify actual join order versus written order.

**Q5. Find the 2nd most recent order per customer using a self-join (without window functions).**
```sql
SELECT o1.customer_id, o1.order_id, o1.order_date
FROM orders o1
JOIN orders o2 ON o1.customer_id = o2.customer_id AND o2.order_date > o1.order_date
GROUP BY o1.customer_id, o1.order_id, o1.order_date
HAVING COUNT(*) = 1;   -- exactly 1 order is newer => this is the 2nd newest
```
(Modern approach: use `ROW_NUMBER()` — see Section 20.)

---

## 5. SQL Operators

| Category | Operators | Example |
|---|---|---|
| Arithmetic | `+ - * / %` | `salary * 1.10` |
| Comparison | `= != <> > < >= <=` | `salary >= 50000` |
| Logical | `AND OR NOT` | `dept_id = 1 AND salary > 60000` |
| Membership | `IN`, `NOT IN` | `dept_id IN (1,2)` |
| Existence | `EXISTS`, `NOT EXISTS` | `WHERE EXISTS (SELECT 1 ...)` |
| Range | `BETWEEN` | `salary BETWEEN 50000 AND 80000` |
| Pattern | `LIKE` | `emp_name LIKE 'A%'` |
| Null Check | `IS NULL`, `IS NOT NULL` | `manager_id IS NULL` |
| Quantified | `ANY`, `ALL` | `salary > ANY (SELECT ...)` |

```sql
SELECT emp_name FROM employee WHERE dept_id IN (1, 2);
SELECT emp_name FROM employee WHERE dept_id NOT IN (1, 2);
SELECT emp_name FROM employee WHERE salary BETWEEN 50000 AND 80000;
SELECT emp_name FROM employee WHERE emp_name LIKE '_a%';   -- 2nd letter 'a'
SELECT emp_name FROM employee WHERE manager_id IS NULL;

-- EXISTS / NOT EXISTS
SELECT d.dept_name FROM department d
WHERE EXISTS (SELECT 1 FROM employee e WHERE e.dept_id = d.dept_id);

-- ANY / ALL
SELECT emp_name, salary FROM employee
WHERE salary > ALL (SELECT salary FROM employee WHERE dept_id = 2);  -- greater than every HR salary

SELECT emp_name, salary FROM employee
WHERE salary > ANY (SELECT salary FROM employee WHERE dept_id = 2);  -- greater than at least one HR salary
```

### 🎯 Interview Q&A — Operators

**Q1. Why does `WHERE dept_id NOT IN (SELECT dept_id FROM department)` sometimes return ZERO rows unexpectedly?**
A: The classic **NULL trap**. If the subquery returns even one `NULL` value, `NOT IN` returns `UNKNOWN` for every comparison (since `x <> NULL` is `UNKNOWN`, and `NOT IN` is effectively a chain of `AND`ed `<>` comparisons), causing the whole `WHERE` clause to evaluate false for all rows. **Fix:** filter nulls in the subquery (`WHERE dept_id IS NOT NULL`) or use `NOT EXISTS` instead, which handles NULLs safely.

**Q2. `IN` vs `EXISTS` — performance difference?**
A: `EXISTS` short-circuits on the first match (correlated, row-by-row check) — efficient when the outer table is large and the inner check is cheap/indexed. `IN` materializes the full subquery result set first — can be more efficient when the subquery result is small and non-correlated. Modern optimizers (PostgreSQL, Oracle) often rewrite one into the other automatically, but the NULL-safety difference above still matters.

**Q3. `ANY` vs `ALL` — quick distinction?**
A: `> ANY` means "greater than **at least one**" (equivalent to `> MIN(...)`); `> ALL` means "greater than **every**" (equivalent to `> MAX(...)`).

**Q4. Why is `emp_name LIKE '%avinash%'` slow on a large table, and how to fix it?**
A: A **leading wildcard** (`%` at the start) prevents the database from using a standard B-Tree index on that column — it forces a full scan since the index is sorted by prefix. **Fix:** use a full-text index (`FULLTEXT` in MySQL, `tsvector`/`GIN` in PostgreSQL, `CONTAINS` in SQL Server) for substring/word search at scale.

---

## 6. Aggregate Functions

| Function | Purpose | Ignores NULL? |
|---|---|---|
| `COUNT(*)` | Row count (including NULLs) | No |
| `COUNT(col)` | Non-null value count | Yes |
| `SUM(col)` | Total | Yes |
| `AVG(col)` | Mean | Yes |
| `MIN(col)` / `MAX(col)` | Extremes | Yes |

```sql
SELECT COUNT(*) AS total_employees FROM employee;                 -- 6
SELECT COUNT(DISTINCT dept_id) AS dept_count FROM employee;        -- 3
SELECT SUM(salary) AS total_payroll FROM employee;
SELECT AVG(salary) AS avg_salary FROM employee;
SELECT MAX(salary) - MIN(salary) AS salary_spread FROM employee;
```

### 🎯 Complex Interview Questions

**Q1. Why does `COUNT(*)` differ from `COUNT(manager_id)`?**
A: `COUNT(*)` counts all rows regardless of NULLs; `COUNT(manager_id)` counts only rows where `manager_id IS NOT NULL` — so `COUNT(*) - COUNT(manager_id)` = number of top-level employees with no manager.

**Q2. Find department(s) with the highest total salary spend.**
```sql
SELECT dept_id, SUM(salary) AS total_salary
FROM employee
GROUP BY dept_id
ORDER BY total_salary DESC
LIMIT 1;
```

**Q3. Can you use an aggregate function inside a `WHERE` clause?**
A: No — aggregates are computed after `WHERE` filtering (and after `GROUP BY`), so they must be filtered using `HAVING`. `WHERE SUM(salary) > 100000` is a syntax error; use `HAVING`.

**Q4 (FAANG-style). Compute average salary excluding the highest and lowest earner per department.**
```sql
SELECT dept_id, AVG(salary) AS trimmed_avg
FROM (
    SELECT dept_id, salary,
           RANK() OVER (PARTITION BY dept_id ORDER BY salary DESC) AS rnk_desc,
           RANK() OVER (PARTITION BY dept_id ORDER BY salary ASC)  AS rnk_asc
    FROM employee
) t
WHERE rnk_desc > 1 AND rnk_asc > 1
GROUP BY dept_id;
```

**Q5. `COUNT(1)` vs `COUNT(*)` — any real difference?**
A: Functionally identical in all modern optimizers (both count all rows) — `COUNT(1)` is a legacy habit from older engines where `*` required column-list expansion; today's optimizers treat them the same. Prefer `COUNT(*)` for readability/convention.

---

## 7. GROUP BY and HAVING

**Scenario Data (employee, dept_id=1 — Engineering):**

| emp_name | salary |
|---|---|
| Avinash | 104500 |
| Sanjay | 71000 |
| Rahul | 68200 |

### Second Highest Salary — 5 Approaches
```sql
-- 1. Subquery with MAX
SELECT MAX(salary) FROM employee WHERE salary < (SELECT MAX(salary) FROM employee);

-- 2. OFFSET/FETCH (PostgreSQL/SQL Server)
SELECT DISTINCT salary FROM employee ORDER BY salary DESC OFFSET 1 ROWS FETCH NEXT 1 ROWS ONLY;

-- 3. LIMIT/OFFSET (MySQL/PostgreSQL)
SELECT DISTINCT salary FROM employee ORDER BY salary DESC LIMIT 1 OFFSET 1;

-- 4. Window function (best — handles ties correctly, works everywhere)
SELECT salary FROM (
    SELECT salary, DENSE_RANK() OVER (ORDER BY salary DESC) AS rnk FROM employee
) t WHERE rnk = 2;

-- 5. Correlated subquery (portable, no window function support needed)
SELECT DISTINCT salary FROM employee e1
WHERE 2 = (SELECT COUNT(DISTINCT salary) FROM employee e2 WHERE e2.salary >= e1.salary);
```
**Why `DENSE_RANK` is preferred:** Handles duplicate salaries correctly (e.g., two people tied for highest still make the 2nd distinct value rank 2), unlike naive `LIMIT`/`OFFSET` on non-distinct values.

### Nth Highest Salary (generalized)
```sql
SELECT salary FROM (
    SELECT salary, DENSE_RANK() OVER (ORDER BY salary DESC) AS rnk FROM employee
) t WHERE rnk = :N;
```

### Department-wise Highest Salary
```sql
SELECT dept_id, MAX(salary) AS highest_salary
FROM employee
GROUP BY dept_id;

-- With employee names (using window function to avoid a self-join)
SELECT dept_id, emp_name, salary FROM (
    SELECT dept_id, emp_name, salary,
           RANK() OVER (PARTITION BY dept_id ORDER BY salary DESC) AS rnk
    FROM employee
) t WHERE rnk = 1;
```

### Average Salary per Department, Only Departments Above Overall Average
```sql
SELECT dept_id, AVG(salary) AS avg_sal
FROM employee
GROUP BY dept_id
HAVING AVG(salary) > (SELECT AVG(salary) FROM employee);
```

### Duplicate Records — Find and Remove
```sql
-- Find duplicates (by email, say)
SELECT email, COUNT(*) FROM employee GROUP BY email HAVING COUNT(*) > 1;

-- Delete duplicates keeping the lowest emp_id (MySQL)
DELETE e1 FROM employee e1
JOIN employee e2 ON e1.email = e2.email AND e1.emp_id > e2.emp_id;

-- Delete duplicates using window function (PostgreSQL/SQL Server/Oracle)
DELETE FROM employee
WHERE emp_id IN (
    SELECT emp_id FROM (
        SELECT emp_id, ROW_NUMBER() OVER (PARTITION BY email ORDER BY emp_id) AS rn
        FROM employee
    ) t WHERE rn > 1
);
```

### 🎯 Interview Q&A
**Q1. Why can't you write `SELECT emp_name, salary FROM employee GROUP BY dept_id`?**
A: `emp_name`/`salary` are not aggregated and not part of the `GROUP BY` list — most RDBMS (PostgreSQL, SQL Server, Oracle) reject this as ambiguous (which row's `emp_name` represents the whole group?). MySQL historically allowed it (`ONLY_FULL_GROUP_BY` off) returning an arbitrary row — considered a footgun, and disabled by default in modern MySQL.

**Q2. Order of clause execution in a full SELECT statement?**
A: `FROM → WHERE → GROUP BY → HAVING → SELECT → DISTINCT → ORDER BY → LIMIT/OFFSET`. This explains why you can use a `SELECT` alias in `ORDER BY` but not in `WHERE`.

---

## 8. Subqueries

| Type | Description | Example |
|---|---|---|
| Single Row | Returns exactly one row/value | `WHERE salary > (SELECT AVG(salary) FROM employee)` |
| Multiple Row | Returns a set of values | `WHERE dept_id IN (SELECT dept_id FROM department WHERE location='Bangalore')` |
| Correlated | References outer query column; re-evaluated per row | `WHERE salary > (SELECT AVG(salary) FROM employee e2 WHERE e2.dept_id = e1.dept_id)` |
| Nested | Subquery inside a subquery | `WHERE dept_id IN (SELECT dept_id FROM department WHERE dept_id IN (SELECT ...))` |

```sql
-- Single-row
SELECT emp_name FROM employee WHERE salary = (SELECT MAX(salary) FROM employee);

-- Multi-row
SELECT emp_name FROM employee WHERE dept_id IN (SELECT dept_id FROM department WHERE location = 'Bangalore');

-- Correlated — employees earning above their department average
SELECT e1.emp_name, e1.salary FROM employee e1
WHERE e1.salary > (SELECT AVG(e2.salary) FROM employee e2 WHERE e2.dept_id = e1.dept_id);

-- Subquery in SELECT clause (scalar)
SELECT emp_name, salary,
       (SELECT dept_name FROM department d WHERE d.dept_id = e.dept_id) AS dept_name
FROM employee e;

-- Subquery in FROM clause (derived table)
SELECT dept_id, avg_sal FROM (
    SELECT dept_id, AVG(salary) AS avg_sal FROM employee GROUP BY dept_id
) dept_avg WHERE avg_sal > 60000;
```

### Advantages / Disadvantages
| Advantages | Disadvantages |
|---|---|
| Breaks complex logic into readable steps | Correlated subqueries can execute once per outer row → O(n²) risk |
| No need for temp tables | Optimizer sometimes can't rewrite them as efficiently as joins |
| Useful for existence/aggregation checks | Harder to read when deeply nested (3+ levels) |

**Performance:** Modern optimizers (PostgreSQL, Oracle CBO, SQL Server) often internally rewrite correlated subqueries into semi-joins — but this isn't guaranteed for every engine/version, so always check `EXPLAIN` on a large, production-representative dataset rather than assuming.

### 🎯 Interview Q&A
**Q1. Correlated subquery vs JOIN — which is faster?**
A: A well-indexed JOIN is usually faster since it's evaluated as a set operation once; a correlated subquery conceptually runs once per outer row (though optimizers often rewrite it). For senior-level answers: "It depends on the optimizer's ability to rewrite it as a semi-join — I'd verify with `EXPLAIN ANALYZE` rather than assume."

**Q2. Can a subquery in `FROM` clause use an alias reference from the outer query?**
A: No — a derived table in the `FROM` clause is fully independent and cannot reference outer query columns (that's what makes it different from a correlated subquery, which only appears in `WHERE`/`SELECT`).

**Q3. Rewrite this correlated `NOT EXISTS` subquery as a `LEFT JOIN`: find customers with no orders.**
```sql
-- Subquery
SELECT c.customer_name FROM customer c
WHERE NOT EXISTS (SELECT 1 FROM orders o WHERE o.customer_id = c.customer_id);

-- Equivalent JOIN
SELECT c.customer_name FROM customer c
LEFT JOIN orders o ON c.customer_id = o.customer_id
WHERE o.order_id IS NULL;
```

---

## 9. SQL Functions

### String Functions
```sql
SELECT UPPER(emp_name), LOWER(emp_name) FROM employee;
SELECT SUBSTRING(emp_name, 1, 3) FROM employee;      -- first 3 chars
SELECT TRIM('  Avinash  ');                          -- 'Avinash'
SELECT LENGTH(emp_name) FROM employee;               -- CHAR_LENGTH in some RDBMS
SELECT REPLACE(email, '@old.com', '@new.com') FROM employee;
SELECT CONCAT(emp_name, ' - ', dept_id) FROM employee;
```

### Date Functions
```sql
SELECT NOW();                          -- current date+time (MySQL/PostgreSQL)
SELECT CURRENT_DATE;                   -- current date only
SELECT DATEDIFF(NOW(), join_date) FROM employee;          -- MySQL: days between
SELECT AGE(NOW(), join_date) FROM employee;               -- PostgreSQL interval
SELECT DATEADD(YEAR, 1, join_date) FROM employee;         -- SQL Server
SELECT EXTRACT(YEAR FROM join_date) FROM employee;        -- ANSI standard
```

### Numeric Functions
```sql
SELECT ROUND(salary, -3) FROM employee;   -- round to nearest thousand
SELECT CEIL(salary / 12.0) FROM employee; -- monthly equivalent, rounded up
SELECT FLOOR(salary / 12.0) FROM employee;
SELECT ABS(-100);
SELECT MOD(10, 3);
```

### 🎯 Interview Q&A
**Q1. How do you calculate tenure in years for each employee?**
```sql
SELECT emp_name, DATEDIFF(CURRENT_DATE, join_date) / 365.25 AS tenure_years FROM employee;  -- MySQL
SELECT emp_name, EXTRACT(YEAR FROM AGE(CURRENT_DATE, join_date)) AS tenure_years FROM employee; -- PostgreSQL
```

**Q2. Difference between `ROUND()`, `CEIL()`, `FLOOR()`?**
A: `ROUND` rounds to nearest (configurable decimal places); `CEIL`/`CEILING` always rounds **up** to the next integer; `FLOOR` always rounds **down**. Critical distinction for billing systems (e.g., always round up minutes of usage for a pay-per-minute service = `CEIL`).

**Q3. Why is `WHERE YEAR(join_date) = 2024` a performance anti-pattern?**
A: Wrapping an indexed column in a function (`YEAR(...)`) makes the predicate **non-sargable** — the optimizer cannot use a standard B-Tree index on `join_date` because it must evaluate the function for every row first. Rewrite as a range: `WHERE join_date >= '2024-01-01' AND join_date < '2025-01-01'` to keep it sargable and index-friendly.

---

## 10. Set Operators

| Operator | Removes Duplicates? | Column/Type Match Required? | Supported By |
|---|---|---|---|
| `UNION` | Yes | Yes | All major RDBMS |
| `UNION ALL` | No (keeps all rows) | Yes | All major RDBMS |
| `INTERSECT` | Yes | Yes | PostgreSQL, SQL Server, Oracle (not MySQL <8.0.31) |
| `EXCEPT` / `MINUS` | Yes | Yes | `EXCEPT`: PostgreSQL/SQL Server; `MINUS`: Oracle; MySQL 8.0.31+ supports `EXCEPT` |

```sql
-- UNION — combined distinct list of cities from customers and warehouse locations
SELECT city FROM customer
UNION
SELECT location AS city FROM department;

-- UNION ALL — faster, keeps duplicates
SELECT city FROM customer
UNION ALL
SELECT location FROM department;

-- INTERSECT — customers who are also employees (by email)
SELECT email FROM customer
INTERSECT
SELECT email FROM employee;

-- EXCEPT/MINUS — departments with no matching city in customer table
SELECT location FROM department
EXCEPT
SELECT city FROM customer;
```

### 🎯 Interview Q&A
**Q1. When would you deliberately choose `UNION ALL` over `UNION`?**
A: When you know the two result sets are mutually exclusive (no overlap possible) or duplicates are acceptable/desired — `UNION ALL` skips the sort/dedup step, which is significantly cheaper on large result sets.

**Q2. How do you emulate `INTERSECT` in MySQL (pre-8.0.31)?**
```sql
SELECT email FROM customer c
WHERE EXISTS (SELECT 1 FROM employee e WHERE e.email = c.email);
```

**Q3. How do you emulate `MINUS`/`EXCEPT` using `LEFT JOIN`?**
```sql
SELECT d.location FROM department d
LEFT JOIN customer c ON d.location = c.city
WHERE c.city IS NULL;
```

---

## 11. Views

| Type | Stores Data? | Refresh | Use Case |
|---|---|---|---|
| Simple View | No | N/A (live query) | Single table, hide columns for security |
| Complex View | No | N/A (live query) | Multi-table joins, aggregates, simplifies reporting |
| Materialized View | Yes | Manual/scheduled `REFRESH` | Expensive aggregate queries, dashboards, OLAP |

```sql
-- Simple view
CREATE VIEW active_employees AS
SELECT emp_id, emp_name, salary FROM employee WHERE status = 'ACTIVE';

-- Complex view
CREATE VIEW employee_dept_summary AS
SELECT e.emp_name, e.salary, d.dept_name
FROM employee e JOIN department d ON e.dept_id = d.dept_id;

-- Materialized view (PostgreSQL/Oracle)
CREATE MATERIALIZED VIEW dept_salary_summary AS
SELECT dept_id, SUM(salary) AS total_salary FROM employee GROUP BY dept_id;

REFRESH MATERIALIZED VIEW dept_salary_summary;   -- PostgreSQL
-- Oracle: DBMS_MVIEW.REFRESH / fast refresh with materialized view logs
-- MySQL: no native materialized views — emulate with a summary table + scheduled event/cron job
```

### 🎯 Interview Q&A
**Q1. Why use a view instead of just writing the JOIN every time?**
A: Encapsulation — hides complexity from consumers (e.g., BI tools, junior devs), provides a stable interface even if underlying tables change (as long as the view is updated to match), and can restrict column/row visibility as a security layer (grant `SELECT` on the view, not the base tables).

**Q2. Can you `INSERT`/`UPDATE` through a view?**
A: Yes, for **simple views** (single table, no aggregates/joins/DISTINCT) — most RDBMS support updatable views in this case. Complex views (joins, `GROUP BY`) are generally **not** directly updatable without `INSTEAD OF` triggers.

**Q3. Materialized view staleness — how do you manage it in a real system?**
A: Choose a refresh strategy based on tolerance for staleness: **on-demand** (`REFRESH MATERIALIZED VIEW`), **scheduled** (cron/DB job every N minutes), or **incremental/fast refresh** (Oracle materialized view logs, PostgreSQL manual triggers) which only recomputes changed rows instead of the full query — critical for large dashboards where a full recompute would be too slow to run frequently.

---

## 12. Indexing (IMPORTANT)

### How Indexing Works Internally — B-Tree

```
                     [50]
                   /      \
              [20,35]      [70,90]
             /   |   \      /  |  \
          [10][25][40] [60][80][95]
```
A **B-Tree index** keeps keys sorted and balanced so lookups, range scans, and ordered scans are all O(log n). The engine walks from root to leaf comparing keys — a `WHERE salary = 71000` lookup touches only a few nodes instead of scanning every row.

| Index Type | Description | Example |
|---|---|---|
| Clustered | Physically orders table data by the index key; **one per table** (usually the PK) | InnoDB: PK is always the clustered index |
| Non-Clustered | Separate structure with pointers back to actual rows; **many per table** | `CREATE INDEX idx_salary ON employee(salary);` |
| Composite | Index on multiple columns; column **order matters** | `CREATE INDEX idx_dept_salary ON employee(dept_id, salary);` |
| Unique | Enforces uniqueness + speeds lookups | `CREATE UNIQUE INDEX idx_email ON employee(email);` |
| Covering | Includes all columns a query needs — avoids a "key lookup" back to the table | `CREATE INDEX idx_covering ON employee(dept_id) INCLUDE (emp_name, salary);` |

### Clustered vs Non-Clustered

| | Clustered | Non-Clustered |
|---|---|---|
| Data storage order | Matches index order | Independent of index order |
| Count per table | 1 | Many |
| Lookup speed | Faster for range scans | Extra lookup step ("bookmark/key lookup") for non-indexed columns |
| Oracle equivalent | Index-Organized Table (IOT) | Regular B-Tree index (Oracle heap tables have no true clustered index by default) |

### When an Index is NOT Used
```sql
WHERE YEAR(join_date) = 2024          -- function wraps column → not sargable
WHERE salary + 1000 > 70000           -- expression on column
WHERE emp_name LIKE '%avi%'           -- leading wildcard
WHERE phone = 12345                   -- implicit type conversion (VARCHAR column vs INT literal)
```
Also: the optimizer may **choose not to use an existing index** if the table is small (full scan is cheaper), or if the filter matches a large percentage of rows (low selectivity) — an index on `is_active BOOLEAN` with 95% `TRUE` values is rarely useful.

### Composite Index — Column Order Matters
```sql
CREATE INDEX idx_dept_salary ON employee(dept_id, salary);
-- Uses index:      WHERE dept_id = 1
-- Uses index:      WHERE dept_id = 1 AND salary > 50000
-- Does NOT use it:  WHERE salary > 50000   (leading column dept_id missing)
```
**Rule of thumb:** put the most selective / most frequently filtered-alone column first (leftmost prefix rule).

### Advantages / Disadvantages
| Advantages | Disadvantages |
|---|---|
| Dramatically faster `SELECT`/`JOIN`/`ORDER BY` | Slower `INSERT`/`UPDATE`/`DELETE` (index must be maintained) |
| Enforces uniqueness (unique index) | Extra disk space |
| Enables efficient sorting (`ORDER BY` on indexed column) | Over-indexing hurts write-heavy tables |

### 🎯 Performance Tuning Interview Questions
**Q1. Why can adding an index make a bulk `UPDATE` job slower?**
A: Every indexed column touched by the update must have its index entry relocated/rebalanced — on a bulk job affecting millions of rows, this multiplies I/O. Common fix: drop non-critical indexes before a bulk load, then rebuild afterward.

**Q2. What's a "covering index" and why does it matter?**
A: An index that contains **every column** referenced by a query (in the key or an `INCLUDE`/`STORING` clause), so the engine can answer the query entirely from the index without a second lookup into the actual table (`Index Only Scan` in PostgreSQL, avoids "Key Lookup" in SQL Server) — a major performance win for read-heavy hot paths.

**Q3. How does a clustered index affect INSERT performance on a monotonically increasing key vs a random key (e.g., UUID)?**
A: With an increasing key (auto-increment `BIGINT`), new rows are always appended at the end of the physical structure — cheap. With a random key (UUID v4), new rows insert into random positions across the B-Tree, causing **page splits** and fragmentation — a well-known real-world issue that leads teams to use sequential UUIDs (UUIDv7) or a `BIGINT` surrogate key instead of raw UUIDv4 for the clustered/PK column in high-write systems.

**Q4 (FAANG-style). You added an index but `EXPLAIN` still shows a full table scan — what would you check?**
A: (1) Stale statistics — run `ANALYZE`/`UPDATE STATISTICS`; (2) function/expression wrapping the column (non-sargable predicate); (3) implicit type conversion; (4) low selectivity — optimizer legitimately prefers a scan; (5) index not actually created on the right column/table (check with `\d tablename` / `SHOW INDEX`); (6) query using `OR` across columns from different indexes, which many optimizers can't combine efficiently without an index merge/bitmap strategy.

---

## 13. Normalization

### 1NF — Atomic Values, No Repeating Groups
❌ **Violates 1NF:**
| emp_id | emp_name | skills |
|---|---|---|
| 101 | Avinash | Java, Spring, SQL |

✅ **1NF-compliant (separate table):**
```sql
CREATE TABLE employee_skill (emp_id INT, skill VARCHAR(30));
```

### 2NF — No Partial Dependency (applies to composite keys)
❌ Table `order_item(order_id, product_id, product_name, quantity)` — `product_name` depends only on `product_id`, not the full composite key `(order_id, product_id)`.
✅ Split into `order_item(order_id, product_id, quantity)` + `product(product_id, product_name)`.

### 3NF — No Transitive Dependency
❌ `employee(emp_id, dept_id, dept_location)` — `dept_location` depends on `dept_id`, not directly on `emp_id`.
✅ Split into `employee(emp_id, dept_id)` + `department(dept_id, dept_location)`.

### BCNF — Stricter 3NF for Overlapping Candidate Keys
Applies when a table has multiple overlapping candidate keys and a non-trivial functional dependency exists where the determinant isn't a candidate key. Example: `(student, course) → instructor` and `instructor → course` — violates BCNF even though it's in 3NF; resolved by decomposing into `(student, instructor)` and `(instructor, course)`.

### Denormalization — When to Break the Rules
Real-world OLAP/reporting systems intentionally **denormalize** (e.g., storing `dept_name` directly on `employee`) to avoid expensive joins on read-heavy dashboards — trading storage/write complexity for read speed. Common in **data warehouses (star schema)**: fact tables denormalized against dimension tables.

### 🎯 Interview Q&A
**Q1. Give a real example of a 3NF violation you've fixed.**
A: A `customer` table storing `city` and `state` directly, where `state` is fully determined by `city` (transitive dependency via a `city_state` lookup) — normalized by extracting a `city` reference table.

**Q2. Why might a senior engineer intentionally violate 3NF in production?**
A: For a read-heavy analytics dashboard aggregating millions of rows per request, joining 5 normalized tables live is too slow — a denormalized summary/reporting table (refreshed on a schedule) trades a small amount of staleness/redundancy for a 10-100x read speed improvement.

---

## 14. Transactions

### ACID Properties

| Property | Meaning | Example |
|---|---|---|
| **A**tomicity | All operations in a transaction succeed, or none do | Bank transfer: debit + credit both happen, or neither |
| **C**onsistency | Transaction moves DB from one valid state to another, respecting constraints | Balance can never go negative (CHECK constraint honored) |
| **I**solation | Concurrent transactions don't interfere with each other's intermediate state | Two withdrawals from the same account don't double-spend |
| **D**urability | Once committed, data survives crashes/power loss | Write-ahead log (WAL) ensures committed data is on disk |

```sql
BEGIN TRANSACTION;
UPDATE account SET balance = balance - 5000 WHERE account_id = 1;
UPDATE account SET balance = balance + 5000 WHERE account_id = 2;
COMMIT;   -- both succeed together

-- If any step fails:
ROLLBACK; -- undoes both, account balances return to original state
```

```sql
-- SAVEPOINT — partial rollback within a larger transaction
BEGIN TRANSACTION;
UPDATE employee SET salary = salary + 1000 WHERE emp_id = 101;
SAVEPOINT sp1;
UPDATE employee SET salary = salary + 1000 WHERE emp_id = 102;
ROLLBACK TO sp1;   -- undoes only the second update
COMMIT;             -- commits only the first update
```

### 🎯 Interview Q&A
**Q1. Explain ACID using a real banking transfer example.**
A: Transferring ₹5000 from Account A to Account B requires two updates. **Atomicity** ensures both happen or neither (no money vanishes if the app crashes mid-transfer). **Consistency** ensures a `CHECK (balance >= 0)` constraint is never violated. **Isolation** ensures a concurrent balance-check query doesn't see the debit without the matching credit. **Durability** ensures that once the customer sees "Transfer Successful," the change survives a server crash a millisecond later.

**Q2. What does "Consistency" actually mean if the application itself has a bug?**
A: DB-level consistency only guarantees that declared constraints (FK, CHECK, UNIQUE) are honored — it can't prevent application-level logical errors outside those constraints. This is a common trick question — consistency is about constraint enforcement, not business-logic correctness.

**Q3. Why is Durability implemented via a Write-Ahead Log (WAL) instead of writing directly to data files?**
A: Writing sequentially to a log file is far faster than random-access writes to data pages, and the log guarantees recovery — on crash, the DB replays the WAL to redo committed transactions and undo uncommitted ones, without needing every commit to force a slow random disk write immediately.

---

## 15. Isolation Levels

### The Three Problems

| Problem | Description |
|---|---|
| **Dirty Read** | Transaction A reads uncommitted data written by Transaction B; B rolls back → A read data that never officially existed |
| **Non-Repeatable Read** | Transaction A reads a row twice; Transaction B updates+commits it in between → A gets different values on each read |
| **Phantom Read** | Transaction A runs the same range query twice; Transaction B inserts a new row matching the range → A sees a "phantom" new row on the 2nd read |

### Isolation Levels vs Problems Prevented

| Isolation Level | Dirty Read | Non-Repeatable Read | Phantom Read | Concurrency |
|---|---|---|---|---|
| Read Uncommitted | ❌ Possible | ❌ Possible | ❌ Possible | Highest |
| Read Committed (default: PostgreSQL, Oracle, SQL Server) | ✅ Prevented | ❌ Possible | ❌ Possible | High |
| Repeatable Read (default: MySQL/InnoDB) | ✅ Prevented | ✅ Prevented | ❌ Possible* | Medium |
| Serializable | ✅ Prevented | ✅ Prevented | ✅ Prevented | Lowest |

*MySQL InnoDB's Repeatable Read actually prevents most phantom reads too via **next-key locking** — a well-known RDBMS-specific nuance worth mentioning in interviews.

```sql
SET TRANSACTION ISOLATION LEVEL READ COMMITTED;
BEGIN TRANSACTION;
SELECT balance FROM account WHERE account_id = 1;
-- ... another transaction updates and commits balance here ...
SELECT balance FROM account WHERE account_id = 1;  -- may return a DIFFERENT value → non-repeatable read
COMMIT;
```

### 🎯 Interview Q&A
**Q1. Give a real scenario for each anomaly.**
A: **Dirty read** — a fraud-detection job reads an uncommitted large withdrawal that gets rolled back, triggering a false alarm. **Non-repeatable read** — a report totals account balances at the start of a transaction, checks again at the end, and gets a different number because another transaction committed a deposit in between. **Phantom read** — a manager counts "employees with salary > 100000" twice in one report transaction; a new hire's row (matching that filter) gets committed in between, appearing only on the second count.

**Q2. Which isolation level would you pick for a financial ledger system, and why?**
A: `SERIALIZABLE` or at least `REPEATABLE READ` with careful locking for the actual balance-transfer transaction — correctness trumps throughput for money movement. For read-heavy reporting queries on the same system, `READ COMMITTED` is usually fine since eventual consistency of a dashboard is acceptable.

**Q3. What's the trade-off of `SERIALIZABLE`?**
A: Strongest correctness guarantee, but achieved via heavy locking or (in PostgreSQL) **serialization failure detection with forced transaction retries** — the lowest concurrency/throughput of all levels, and can cause increased deadlocks/retries under high contention.

**Q4 (FAANG-style). Optimistic vs Pessimistic concurrency — how does this relate to isolation levels?**
A: Isolation levels are about what anomalies the DB *prevents automatically* via locking/MVCC. Optimistic concurrency (a `version`/`updated_at` column checked on `UPDATE ... WHERE version = :old_version`) is an **application-level** strategy often used *instead of* relying on strict isolation levels — it avoids holding locks at all, assuming conflicts are rare, and simply fails/retries when a conflict is detected at commit time. Pessimistic concurrency (`SELECT ... FOR UPDATE`) explicitly locks rows upfront, similar in spirit to higher isolation levels.

---

## 16. Stored Procedures

```sql
-- Syntax (MySQL)
DELIMITER //
CREATE PROCEDURE give_raise(IN p_emp_id INT, IN p_pct DECIMAL(5,2))
BEGIN
    UPDATE employee
    SET salary = salary + (salary * p_pct / 100)
    WHERE emp_id = p_emp_id;
END //
DELIMITER ;

CALL give_raise(102, 10);
```

```sql
-- PostgreSQL (PL/pgSQL)
CREATE OR REPLACE PROCEDURE give_raise(p_emp_id INT, p_pct NUMERIC)
LANGUAGE plpgsql AS $$
BEGIN
    UPDATE employee SET salary = salary + (salary * p_pct / 100) WHERE emp_id = p_emp_id;
END;
$$;

CALL give_raise(102, 10);
```

| Benefits | Limitations |
|---|---|
| Reduces network round-trips (multi-step logic runs server-side) | Business logic split between app code and DB — harder to version control/test with app-level CI |
| Centralizes logic reused across multiple apps/scripts | Vendor-specific syntax (T-SQL vs PL/pgSQL vs PL/SQL) — poor portability |
| Can improve security (grant `EXECUTE`, not direct table access) | Harder to debug/profile than application code with modern IDEs |
| Pre-compiled execution plan (in some RDBMS) | Scaling logic in the DB tier is harder than scaling stateless app servers horizontally |

### 🎯 Interview Q&A
**Q1. Why do modern microservice architectures tend to avoid heavy stored-procedure logic?**
A: Business logic in the DB doesn't scale horizontally the way stateless application instances do, is harder to unit test and code-review alongside application code, and creates tight coupling between the DB engine and business rules — making a future DB migration (e.g., MySQL → PostgreSQL) much harder. Most modern Java shops keep procedures thin (or avoid them) and keep logic in the service layer (Spring Boot service classes), using stored procedures mainly for bulk/batch operations where minimizing round-trips genuinely matters.

**Q2. When *would* you justify using a stored procedure in a Java Spring Boot system?**
A: Bulk data-fixing scripts, complex multi-statement ETL/batch jobs where minimizing app-DB round trips meaningfully reduces load, or when several different applications/languages must share one authoritative implementation of a critical calculation (e.g., interest calculation shared by a batch job and an online portal).

---

## 17. Functions

```sql
-- MySQL
CREATE FUNCTION annual_salary(p_monthly DECIMAL(10,2)) RETURNS DECIMAL(10,2)
DETERMINISTIC
BEGIN
    RETURN p_monthly * 12;
END;

SELECT emp_name, annual_salary(salary) FROM employee;
```

```sql
-- PostgreSQL
CREATE OR REPLACE FUNCTION annual_salary(p_monthly NUMERIC) RETURNS NUMERIC
LANGUAGE sql IMMUTABLE AS $$
    SELECT p_monthly * 12;
$$;
```

### Function vs Stored Procedure

| | Function | Stored Procedure |
|---|---|---|
| Must return a value | Yes | No (can return 0, 1, or multiple result sets) |
| Callable inside `SELECT` | Yes | No |
| Can modify data (INSERT/UPDATE/DELETE) | Generally no (or restricted) | Yes |
| Called via | Directly in query | `CALL`/`EXEC` |
| Transaction control (COMMIT/ROLLBACK) inside | Not allowed | Allowed (in most RDBMS) |

### 🎯 Interview Q&A
**Q1. Why can't you call `COMMIT` inside a function?**
A: Functions are expected to be side-effect-free and usable inside a `SELECT` — allowing transaction control inside would break the atomicity guarantees of the calling query. Most RDBMS explicitly disallow it.

**Q2. What does marking a function `DETERMINISTIC`/`IMMUTABLE` enable?**
A: Tells the optimizer the function always returns the same output for the same input with no side effects, enabling **result caching** and safe use in indexes (e.g., a function-based index) or query rewriting — marking a genuinely non-deterministic function (like one using `NOW()`) as deterministic can cause subtle, hard-to-debug caching bugs.

---

## 18. Triggers

| Trigger Type | Fires | Common Use |
|---|---|---|
| `BEFORE INSERT` | Before row is inserted | Validate/default values, auto-generate a code |
| `AFTER INSERT` | After row is inserted | Audit log, send notification flag |
| `BEFORE UPDATE` | Before row is updated | Prevent invalid state transitions |
| `AFTER UPDATE` | After row is updated | Audit trail (old vs new values) |
| `AFTER/BEFORE DELETE` | On row deletion | Soft-delete enforcement, cascading cleanup, archival |

```sql
CREATE TABLE salary_audit_log (
    emp_id INT, old_salary DECIMAL(10,2), new_salary DECIMAL(10,2), changed_at TIMESTAMP
);

CREATE TRIGGER trg_salary_audit
AFTER UPDATE ON employee
FOR EACH ROW
BEGIN
    IF OLD.salary <> NEW.salary THEN
        INSERT INTO salary_audit_log VALUES (OLD.emp_id, OLD.salary, NEW.salary, NOW());
    END IF;
END;
```

```sql
-- BEFORE INSERT — auto-generate an employee code
CREATE TRIGGER trg_emp_code
BEFORE INSERT ON employee
FOR EACH ROW
BEGIN
    SET NEW.emp_code = CONCAT('EMP-', NEW.emp_id);
END;
```

**Real Scenario:** In a banking system, a `BEFORE UPDATE` trigger on `account` can reject any update that would make `balance < 0` even if the application layer has a bug bypassing validation — a last line of defense.

### 🎯 Interview Q&A
**Q1. What's a serious real-world risk of relying on triggers heavily?**
A: Hidden, invisible logic — a simple-looking `UPDATE employee SET salary = ...` can silently cascade into multiple trigger-driven side effects (audit inserts, notification flags, recalculations), making performance debugging and code review much harder since the logic isn't visible at the call site. It's a very common root cause of "why is this simple UPDATE taking 10x longer than expected" incidents.

**Q2. Can a trigger call another trigger (cascading triggers), and what's the danger?**
A: Yes — an `UPDATE` from within a trigger on the same or different table can fire another trigger. Danger: infinite recursion or deeply nested cascades that are extremely hard to trace; most RDBMS have a nesting-depth limit as a safety net, but the real fix is limiting trigger scope and complexity.

---

## 19. Common Table Expressions (CTE)

### Simple CTE
```sql
WITH dept_avg AS (
    SELECT dept_id, AVG(salary) AS avg_sal FROM employee GROUP BY dept_id
)
SELECT e.emp_name, e.salary, d.avg_sal
FROM employee e JOIN dept_avg d ON e.dept_id = d.dept_id
WHERE e.salary > d.avg_sal;
```

### Recursive CTE — Employee Hierarchy
```sql
WITH RECURSIVE org_chart AS (
    -- anchor member
    SELECT emp_id, emp_name, manager_id, 1 AS level
    FROM employee WHERE manager_id IS NULL
    UNION ALL
    -- recursive member
    SELECT e.emp_id, e.emp_name, e.manager_id, oc.level + 1
    FROM employee e
    JOIN org_chart oc ON e.manager_id = oc.emp_id
)
SELECT * FROM org_chart ORDER BY level;
```
*(SQL Server/Oracle: omit `RECURSIVE` keyword — just `WITH org_chart AS (...)`.)*

### Multiple CTEs Chained
```sql
WITH high_earners AS (
    SELECT * FROM employee WHERE salary > 70000
),
high_earner_depts AS (
    SELECT DISTINCT dept_id FROM high_earners
)
SELECT * FROM department WHERE dept_id IN (SELECT dept_id FROM high_earner_depts);
```

### 🎯 Interview Q&A
**Q1. CTE vs Subquery vs Temp Table — when do you choose each?**
A: **CTE** for readability and when a query is referenced once or needs recursion — most engines treat it as syntactic sugar, inlined into the main query plan (not always materialized, contrary to popular belief — depends on RDBMS/version). **Subquery** for a simple one-off inline filter. **Temp table** when the intermediate result is large, reused across multiple separate statements, or needs its own index for performance — a CTE can't be indexed.

**Q2. Does a CTE always get materialized (computed once) or can it be inlined/re-evaluated?**
A: RDBMS-dependent — PostgreSQL (pre-12) always materialized CTEs; PostgreSQL 12+ can inline them like a subquery unless marked `MATERIALIZED`. SQL Server and Oracle typically treat non-recursive CTEs as inlined views for optimization purposes. This is a genuine "it depends" answer that shows depth in an interview.

**Q3. Write a recursive CTE to generate a series of dates for a report (common FAANG-style question).**
```sql
WITH RECURSIVE date_series AS (
    SELECT DATE '2026-01-01' AS dt
    UNION ALL
    SELECT dt + INTERVAL '1 day' FROM date_series WHERE dt < DATE '2026-01-31'
)
SELECT * FROM date_series;
```

---

## 20. Window Functions (VERY IMPORTANT)

### Syntax Anatomy
```sql
function_name() OVER (
    PARTITION BY col1        -- splits rows into groups (like GROUP BY, but doesn't collapse rows)
    ORDER BY col2             -- defines the order within each partition
    ROWS BETWEEN ... AND ...  -- defines the "frame" — which rows are visible to the function
)
```

### Ranking Functions

| emp_name | dept_id | salary | ROW_NUMBER | RANK | DENSE_RANK |
|---|---|---|---|---|---|
| Avinash | 1 | 104500 | 1 | 1 | 1 |
| Sanjay | 1 | 71000 | 2 | 2 | 2 |
| Rahul | 1 | 68200 | 3 | 3 | 3 |

```sql
SELECT emp_name, dept_id, salary,
       ROW_NUMBER() OVER (PARTITION BY dept_id ORDER BY salary DESC) AS rn,
       RANK()       OVER (PARTITION BY dept_id ORDER BY salary DESC) AS rnk,
       DENSE_RANK() OVER (PARTITION BY dept_id ORDER BY salary DESC) AS dense_rnk
FROM employee;
```
**Tie-breaking difference (dept_id=2, Priya & Meera both salary=58000):**
```
emp_name | salary | ROW_NUMBER | RANK | DENSE_RANK
Priya    | 58000  | 1          | 1    | 1
Meera    | 58000  | 2          | 1    | 1
```
- `ROW_NUMBER()` — always unique, arbitrary tie-break.
- `RANK()` — ties get the same rank, but **skips** the next rank (1, 1, 3).
- `DENSE_RANK()` — ties get the same rank, **no gap** (1, 1, 2).

### LEAD / LAG
```sql
SELECT emp_name, salary,
       LAG(salary)  OVER (ORDER BY emp_id) AS prev_salary,
       LEAD(salary) OVER (ORDER BY emp_id) AS next_salary
FROM employee;
```
**Use case:** Compare each month's revenue to the previous month (`LAG`) or preview the next event in a sequence (`LEAD`) — extremely common in time-series/finance dashboards.

### NTILE — Bucketing
```sql
SELECT emp_name, salary, NTILE(4) OVER (ORDER BY salary DESC) AS quartile FROM employee;
```
**Use case:** Split customers into quartiles for targeted marketing (top 25% = VIP tier).

### FIRST_VALUE / LAST_VALUE
```sql
SELECT emp_name, dept_id, salary,
       FIRST_VALUE(emp_name) OVER (PARTITION BY dept_id ORDER BY salary DESC) AS top_earner,
       LAST_VALUE(emp_name) OVER (
           PARTITION BY dept_id ORDER BY salary DESC
           ROWS BETWEEN UNBOUNDED PRECEDING AND UNBOUNDED FOLLOWING
       ) AS lowest_earner
FROM employee;
```
**Gotcha:** `LAST_VALUE` needs an explicit frame (`ROWS BETWEEN UNBOUNDED PRECEDING AND UNBOUNDED FOLLOWING`) — otherwise the default frame is "up to current row," making `LAST_VALUE` return the *current row's own value* instead of the true last value in the partition. This is one of the most common window-function bugs.

### Common Patterns

**Second Highest Salary**
```sql
SELECT * FROM (
    SELECT emp_name, salary, DENSE_RANK() OVER (ORDER BY salary DESC) AS rnk FROM employee
) t WHERE rnk = 2;
```

**Nth Highest Salary**
```sql
SELECT * FROM (
    SELECT emp_name, salary, DENSE_RANK() OVER (ORDER BY salary DESC) AS rnk FROM employee
) t WHERE rnk = :N;
```

**Latest Record Per User (e.g., latest order per customer)**
```sql
SELECT * FROM (
    SELECT o.*, ROW_NUMBER() OVER (PARTITION BY customer_id ORDER BY order_date DESC) AS rn
    FROM orders o
) t WHERE rn = 1;
```

**Top N Customers by Spend**
```sql
SELECT * FROM (
    SELECT customer_id, SUM(amount) AS total_spent,
           RANK() OVER (ORDER BY SUM(amount) DESC) AS spend_rank
    FROM orders GROUP BY customer_id
) t WHERE spend_rank <= 5;
```

**Running Total**
```sql
SELECT order_date, amount,
       SUM(amount) OVER (ORDER BY order_date ROWS BETWEEN UNBOUNDED PRECEDING AND CURRENT ROW) AS running_total
FROM orders;
```

**Moving Average (7-day)**
```sql
SELECT order_date, amount,
       AVG(amount) OVER (ORDER BY order_date ROWS BETWEEN 6 PRECEDING AND CURRENT ROW) AS moving_avg_7day
FROM orders;
```

### 🎯 Interview Q&A
**Q1. `RANK()` vs `DENSE_RANK()` vs `ROW_NUMBER()` — when would each cause a bug if used incorrectly?**
A: Using `ROW_NUMBER()` to find "top salary per department" when ties exist would arbitrarily pick just one of several tied top earners, silently dropping a legitimately-tied employee from the report — `RANK()` (or `DENSE_RANK()`) would correctly include all tied rows at rank 1.

**Q2. How do window functions differ from `GROUP BY` in terms of what's returned?**
A: `GROUP BY` collapses each group into a single summary row. Window functions compute an aggregate/ranking **per row** while still returning every original row — you get the detail row *and* the group-level insight (e.g., "this employee's salary" *and* "this employee's rank within their department") in the same row.

**Q3 (FAANG-style). Find employees whose salary increased for 3 consecutive "review cycles" (consecutive rows in a history table).**
```sql
WITH salary_changes AS (
    SELECT emp_id, review_date, salary,
           LAG(salary) OVER (PARTITION BY emp_id ORDER BY review_date) AS prev_salary
    FROM salary_history
),
flagged AS (
    SELECT *, CASE WHEN salary > prev_salary THEN 1 ELSE 0 END AS increased FROM salary_changes
),
grouped AS (
    SELECT *, ROW_NUMBER() OVER (PARTITION BY emp_id ORDER BY review_date)
              - ROW_NUMBER() OVER (PARTITION BY emp_id, increased ORDER BY review_date) AS grp
    FROM flagged
)
SELECT emp_id, MIN(review_date), MAX(review_date), COUNT(*) AS consecutive_increases
FROM grouped
WHERE increased = 1
GROUP BY emp_id, grp
HAVING COUNT(*) >= 3;
```
*(This "difference of row numbers" trick is a classic technique for detecting consecutive groups — very frequently asked at senior/FAANG level.)*

**Q4. Can you use a window function result directly in a `WHERE` clause?**
A: No — window functions are evaluated **after** `WHERE`/`GROUP BY`/`HAVING`, in the same logical phase as `SELECT`. To filter on a window function's result, wrap the query in a subquery/CTE and filter in the outer query (as shown in all patterns above).

---

## 21. Performance Tuning (MOST IMPORTANT FOR 8 YEARS)

### Step 1 — Identify the Slow Query
```sql
-- MySQL: enable slow query log
SET GLOBAL slow_query_log = 'ON';
SET GLOBAL long_query_time = 1;   -- log queries > 1 second

-- PostgreSQL: pg_stat_statements extension
SELECT query, calls, total_exec_time, mean_exec_time
FROM pg_stat_statements ORDER BY total_exec_time DESC LIMIT 10;

-- SQL Server: Query Store / DMVs
SELECT TOP 10 * FROM sys.dm_exec_query_stats ORDER BY total_worker_time DESC;
```

### Step 2 — Read the Execution Plan
```sql
EXPLAIN SELECT e.emp_name, d.dept_name FROM employee e
JOIN department d ON e.dept_id = d.dept_id WHERE e.salary > 60000;

EXPLAIN ANALYZE ...   -- actually runs the query, shows real vs estimated rows (PostgreSQL/MySQL 8+)
```
**Key things to check in the plan:**
| Signal | Meaning |
|---|---|
| `Seq Scan` / `Full Table Scan` | No usable index — investigate |
| `Index Scan` vs `Index Only Scan` | The latter avoids a table lookup entirely (covering index) |
| Estimated rows vs actual rows (ANALYZE) | Large mismatch = stale statistics, bad plan likely |
| `Nested Loop` on large tables | Can be very slow — check if a `Hash Join` would be cheaper |
| `Sort` operation | Expensive on large result sets — an index matching `ORDER BY` avoids it |

### Index Optimization
- Index columns used in `WHERE`, `JOIN ON`, and `ORDER BY`.
- Composite indexes: leading column = most selective / most frequently filtered alone.
- Use covering indexes for hot-path read queries.
- Drop unused indexes (check `sys.dm_db_index_usage_stats` / `pg_stat_user_indexes`) — every unused index still costs write performance.

### Query Optimization Checklist
```sql
-- ❌ Bad: SELECT * fetches unneeded columns/bytes
SELECT * FROM orders WHERE customer_id = 5;
-- ✅ Good
SELECT order_id, amount, order_date FROM orders WHERE customer_id = 5;

-- ❌ Bad: function on indexed column disables index
WHERE YEAR(order_date) = 2026
-- ✅ Good: sargable range predicate
WHERE order_date >= '2026-01-01' AND order_date < '2027-01-01'

-- ❌ Bad: implicit type conversion
WHERE phone = 9999999999          -- phone is VARCHAR
-- ✅ Good
WHERE phone = '9999999999'

-- ❌ Bad: OR across different columns often defeats index usage
WHERE customer_id = 5 OR status = 'PENDING'
-- ✅ Good: UNION of two indexed lookups (often faster)
SELECT * FROM orders WHERE customer_id = 5
UNION
SELECT * FROM orders WHERE status = 'PENDING';
```

### Partitioning
Splits a large table into smaller physical segments based on a key (date range, hash, list), while remaining a single logical table.
```sql
-- Range partitioning by year (PostgreSQL)
CREATE TABLE orders (
    order_id INT, order_date DATE, amount DECIMAL(10,2)
) PARTITION BY RANGE (order_date);

CREATE TABLE orders_2025 PARTITION OF orders FOR VALUES FROM ('2025-01-01') TO ('2026-01-01');
CREATE TABLE orders_2026 PARTITION OF orders FOR VALUES FROM ('2026-01-01') TO ('2027-01-01');
```
**Benefit:** Queries filtering on `order_date` only scan the relevant partition (**partition pruning**), and old partitions can be archived/dropped instantly instead of a slow `DELETE`.

### Sharding
Splits data **across multiple database instances/servers** (horizontal scaling), typically by a shard key (e.g., `customer_id % N` or geographic region) — used when a single server's storage/throughput ceiling is reached. Unlike partitioning (still one logical DB), sharding introduces cross-shard query complexity, distributed transactions, and rebalancing challenges — a system design topic covered further in Section 24.

### Batch Processing
```sql
-- ❌ Bad: one massive transaction locks rows for a long time, huge rollback segment/log growth
DELETE FROM orders WHERE status = 'CANCELLED';

-- ✅ Good: batch in chunks
DELETE FROM orders WHERE status = 'CANCELLED' LIMIT 5000;
-- repeat in a loop until 0 rows affected, with a small sleep between batches
```
**Java/Spring Batch angle:** Use `JdbcTemplate.batchUpdate()` or Spring Batch chunk-oriented steps with a commit interval (e.g., 500–1000 records) instead of one `saveAll()` call flushing millions of entities through Hibernate's persistence context (which would also blow up JVM heap).

### Pagination Optimization
```sql
-- ❌ Bad: OFFSET-based pagination gets slower as offset grows (must scan+discard N rows)
SELECT * FROM orders ORDER BY order_id LIMIT 20 OFFSET 1000000;

-- ✅ Good: keyset/seek pagination — uses the index directly, O(log n) regardless of page number
SELECT * FROM orders WHERE order_id > :last_seen_id ORDER BY order_id LIMIT 20;
```

### Large Table Design
- Use `BIGINT` surrogate keys, not UUIDv4 (page-split/fragmentation issue — see Section 12).
- Partition by date for time-series/audit/log tables.
- Archive/cold-storage old data (e.g., move orders >2 years old to an archive table or cheaper storage tier).
- Avoid wide tables with many nullable columns — consider vertical splitting (a `1:1` extension table for rarely-accessed columns).
- Denormalize selectively for known hot read paths (see Section 13).

### 🎯 Interview Q&A
**Q1. Walk through your process for diagnosing a query that suddenly became slow in production.**
A: (1) Confirm it's actually the query, not app-server/network latency — check DB-side execution time via slow query log/APM. (2) Run `EXPLAIN ANALYZE` to compare estimated vs actual rows — a big mismatch signals stale statistics. (3) Check if data volume grew significantly since the query was last reviewed. (4) Check if an index was dropped, or a schema change (added column, changed type) silently disabled sargability. (5) Check for lock contention from a concurrent batch job. (6) Apply the targeted fix (index, rewrite, or statistics update) and verify with `EXPLAIN ANALYZE` again before shipping — never guess-and-ship without confirming via the plan.

**Q2. Sharding vs Partitioning — explain the difference clearly.**
A: Partitioning splits one table into physical segments **within the same database instance** — transparent to the application, still supports cross-partition joins/transactions normally. Sharding splits data **across separate database servers/instances**, usually requiring application-level routing logic (or a sharding proxy) to know which shard holds a given row — cross-shard joins and transactions become significantly harder (often requiring the saga pattern or distributed transaction coordinators).

**Q3. How would you paginate a REST API backed by a 500-million-row table efficiently?**
A: Avoid `OFFSET`-based pagination past a certain depth — instead use **keyset (seek) pagination**: the client passes the last seen `id`/`created_at` value, and the query does `WHERE id > :last_id ORDER BY id LIMIT :pageSize`, which uses the index directly regardless of "page number," giving consistent O(log n) performance instead of degrading linearly with offset.

**Q4 (FAANG-style). A query joins 4 tables and takes 8 seconds; `EXPLAIN` shows a `Nested Loop` where you'd expect a `Hash Join`. What would you investigate?**
A: Nested loop is chosen by the optimizer when it estimates one side of the join is very small — if statistics are stale or a parameter-sensitive plan was cached (parameter sniffing), the optimizer might wrongly believe row counts are small and pick nested loop, which is disastrous when the actual row count is large (effectively O(n×m)). I'd update statistics, check for parameter sniffing (recompile with actual parameter, or use `OPTION (RECOMPILE)` in SQL Server), and consider a query hint or restructuring (e.g., forcing a hash join, or breaking the query into a temp table with proper stats) as a last resort.

**Q5. What's the real-world impact of "over-indexing" a table, concretely?**
A: Beyond slower writes, each additional index means more memory pressure on the buffer pool/cache (fewer hot data pages fit in RAM), longer `VACUUM`/statistics maintenance windows (PostgreSQL), and larger backup/replication payloads — I've seen a "helpful" index added for a rarely-run report silently regress a high-throughput `INSERT` path by 20%+ because nobody measured the write-side cost before merging.

---

## 22. SQL Scenarios Asked in Real Interviews (50 Questions)

> Schema assumed: `employee(emp_id, emp_name, salary, dept_id, manager_id, join_date)`, `orders(order_id, customer_id, order_date, amount, status)`, `customer(customer_id, customer_name, city)`, `login_history(user_id, login_date)`, `product(product_id, product_name, price)`.

**1. Employee hierarchy — all subordinates under a given manager (recursive).**
```sql
WITH RECURSIVE subordinates AS (
    SELECT emp_id, emp_name, manager_id FROM employee WHERE manager_id = 101
    UNION ALL
    SELECT e.emp_id, e.emp_name, e.manager_id FROM employee e
    JOIN subordinates s ON e.manager_id = s.emp_id
)
SELECT * FROM subordinates;
```

**2. Find duplicate rows in a table.**
```sql
SELECT emp_name, email, COUNT(*) FROM employee GROUP BY emp_name, email HAVING COUNT(*) > 1;
```

**3. Delete duplicate rows, keep one copy.**
```sql
DELETE FROM employee WHERE emp_id NOT IN (
    SELECT MIN(emp_id) FROM employee GROUP BY email
);
```

**4. Nth highest salary.**
```sql
SELECT DISTINCT salary FROM employee e1
WHERE (:N - 1) = (SELECT COUNT(DISTINCT salary) FROM employee e2 WHERE e2.salary > e1.salary);
```

**5. Find consecutive login dates per user (streaks).**
```sql
WITH ranked AS (
    SELECT user_id, login_date,
           login_date - (ROW_NUMBER() OVER (PARTITION BY user_id ORDER BY login_date))::INT AS grp
    FROM login_history
)
SELECT user_id, MIN(login_date) AS streak_start, MAX(login_date) AS streak_end, COUNT(*) AS streak_len
FROM ranked GROUP BY user_id, grp HAVING COUNT(*) >= 3;
```

**6. Gap analysis — find missing order_ids in a sequence.**
```sql
SELECT (t.order_id + 1) AS gap_start
FROM orders t
WHERE NOT EXISTS (SELECT 1 FROM orders t2 WHERE t2.order_id = t.order_id + 1)
ORDER BY gap_start;
```

**7. Top 3 salaries per department.**
```sql
SELECT * FROM (
    SELECT emp_name, dept_id, salary, DENSE_RANK() OVER (PARTITION BY dept_id ORDER BY salary DESC) AS rnk
    FROM employee
) t WHERE rnk <= 3;
```

**8. Latest order per customer.**
```sql
SELECT * FROM (
    SELECT o.*, ROW_NUMBER() OVER (PARTITION BY customer_id ORDER BY order_date DESC) rn FROM orders o
) t WHERE rn = 1;
```

**9. Customer retention — customers who ordered in both Jan and Feb.**
```sql
SELECT customer_id FROM orders WHERE EXTRACT(MONTH FROM order_date) = 1
INTERSECT
SELECT customer_id FROM orders WHERE EXTRACT(MONTH FROM order_date) = 2;
```

**10. Running total of daily sales.**
```sql
SELECT order_date, SUM(amount) AS daily_total,
       SUM(SUM(amount)) OVER (ORDER BY order_date) AS running_total
FROM orders GROUP BY order_date;
```

**11. Rank customers by total spend.**
```sql
SELECT customer_id, SUM(amount) total, RANK() OVER (ORDER BY SUM(amount) DESC) rnk
FROM orders GROUP BY customer_id;
```

**12. Find each employee's manager's name and department.**
```sql
SELECT e.emp_name, m.emp_name AS manager_name, d.dept_name
FROM employee e
LEFT JOIN employee m ON e.manager_id = m.emp_id
LEFT JOIN department d ON e.dept_id = d.dept_id;
```

**13. Employees who joined in the last 90 days.**
```sql
SELECT emp_name FROM employee WHERE join_date >= CURRENT_DATE - INTERVAL '90 days';
```

**14. Second highest salary per department without window functions.**
```sql
SELECT dept_id, MAX(salary) FROM employee e1
WHERE salary < (SELECT MAX(salary) FROM employee e2 WHERE e2.dept_id = e1.dept_id)
GROUP BY dept_id;
```

**15. Customers with no orders in the last 6 months (churn risk).**
```sql
SELECT c.customer_id, c.customer_name FROM customer c
WHERE NOT EXISTS (
    SELECT 1 FROM orders o WHERE o.customer_id = c.customer_id
    AND o.order_date >= CURRENT_DATE - INTERVAL '6 months'
);
```

**16. Month-over-month growth percentage.**
```sql
WITH monthly AS (
    SELECT DATE_TRUNC('month', order_date) AS mon, SUM(amount) AS total FROM orders GROUP BY 1
)
SELECT mon, total,
       LAG(total) OVER (ORDER BY mon) AS prev_month,
       ROUND((total - LAG(total) OVER (ORDER BY mon)) * 100.0 / LAG(total) OVER (ORDER BY mon), 2) AS growth_pct
FROM monthly;
```

**17. Products never ordered.**
```sql
SELECT p.product_id, p.product_name FROM product p
LEFT JOIN order_item oi ON p.product_id = oi.product_id
WHERE oi.product_id IS NULL;
```

**18. Employees with the same salary (find pairs/groups).**
```sql
SELECT salary, COUNT(*), STRING_AGG(emp_name, ', ') FROM employee GROUP BY salary HAVING COUNT(*) > 1;
```

**19. First order date per customer.**
```sql
SELECT customer_id, MIN(order_date) AS first_order FROM orders GROUP BY customer_id;
```

**20. Employees who never had a manager change (find longest-tenured reporting relationship).** — covered via `salary_history`-style pattern in Section 20 Q3.

**21. Cumulative distinct customers over time.**
```sql
SELECT order_date, COUNT(DISTINCT customer_id) OVER (ORDER BY order_date) AS cumulative_customers
FROM orders;   -- Note: DISTINCT inside window functions isn't standard everywhere; use a CTE aggregation instead in production.
```

**22. Find the department with the second highest average salary.**
```sql
SELECT dept_id, avg_sal FROM (
    SELECT dept_id, AVG(salary) avg_sal, DENSE_RANK() OVER (ORDER BY AVG(salary) DESC) rnk
    FROM employee GROUP BY dept_id
) t WHERE rnk = 2;
```

**23. Orders placed on weekends.**
```sql
SELECT * FROM orders WHERE EXTRACT(DOW FROM order_date) IN (0, 6);  -- PostgreSQL: 0=Sun, 6=Sat
```

**24. Percentage contribution of each department to total salary.**
```sql
SELECT dept_id, SUM(salary), SUM(salary) * 100.0 / (SELECT SUM(salary) FROM employee) AS pct
FROM employee GROUP BY dept_id;
```

**25. Employees earning more than the company average but less than the department max.**
```sql
SELECT emp_name, salary, dept_id FROM employee e
WHERE salary > (SELECT AVG(salary) FROM employee)
AND salary < (SELECT MAX(salary) FROM employee e2 WHERE e2.dept_id = e.dept_id);
```

**26. Find the longest-serving employee in each department.**
```sql
SELECT * FROM (
    SELECT emp_name, dept_id, join_date, RANK() OVER (PARTITION BY dept_id ORDER BY join_date ASC) rnk
    FROM employee
) t WHERE rnk = 1;
```

**27. Orders with amount above 2 standard deviations from mean (outlier detection).**
```sql
SELECT * FROM orders WHERE amount > (SELECT AVG(amount) + 2 * STDDEV(amount) FROM orders);
```

**28. Pivot: total sales per month as columns (cross-tab).**
```sql
SELECT
  SUM(CASE WHEN EXTRACT(MONTH FROM order_date)=1 THEN amount ELSE 0 END) AS Jan,
  SUM(CASE WHEN EXTRACT(MONTH FROM order_date)=2 THEN amount ELSE 0 END) AS Feb
FROM orders;
```

**29. Find employees whose emp_id is not sequential with the previous hire (detect batch-import gaps).**
```sql
SELECT emp_id, join_date,
       LAG(emp_id) OVER (ORDER BY join_date) AS prev_id
FROM employee
QUALIFY emp_id - LAG(emp_id) OVER (ORDER BY join_date) > 1;  -- Snowflake/BigQuery; use subquery wrapper elsewhere
```

**30. Customers who placed orders every single month this year (loyal customers).**
```sql
SELECT customer_id FROM orders
WHERE EXTRACT(YEAR FROM order_date) = 2026
GROUP BY customer_id
HAVING COUNT(DISTINCT EXTRACT(MONTH FROM order_date)) = 12;
```

**31–50 (rapid-fire, solution pattern noted):**
31. **Median salary** → `PERCENTILE_CONT(0.5) WITHIN GROUP (ORDER BY salary)`.
32. **Mode of a column** → `GROUP BY col ORDER BY COUNT(*) DESC LIMIT 1`.
33. **Employees reporting to a manager in a different department** → self-join + compare `dept_id`.
34. **All-pairs employees with matching skills** → self-join on a normalized `employee_skill` table.
35. **First and last transaction per account** → `FIRST_VALUE`/`LAST_VALUE` or `MIN`/`MAX` with `GROUP BY`.
36. **Find palindromic product codes** → string reversal function comparison (`REVERSE(code) = code`).
37. **Employees never given a raise (salary_history has only 1 row)** → `GROUP BY emp_id HAVING COUNT(*) = 1`.
38. **Top selling product per region** → `RANK() OVER (PARTITION BY region ORDER BY SUM(qty) DESC)`.
39. **Orders that took longer than average to ship** → compare `ship_date - order_date` to `AVG(...) OVER ()`.
40. **Detect circular manager references (data integrity bug)** → recursive CTE with a cycle-detection guard column.
41. **Customers who upgraded then downgraded a subscription tier** → `LAG`/`LEAD` on ordered subscription events.
42. **Employees with overlapping project date ranges** → self-join with `start1 < end2 AND start2 < end1`.
43. **Year-over-year revenue comparison** → `LAG(revenue, 12)` on a monthly aggregation.
44. **Find the 3 consecutive days with the highest combined sales** → windowed `SUM() OVER (ROWS BETWEEN 2 PRECEDING AND CURRENT ROW)`.
45. **Employees promoted more than twice in 2 years** → `COUNT(*)` on a promotions table filtered by date range, `HAVING COUNT(*) > 2`.
46. **Customers whose average order value increased quarter over quarter** → `LAG` on quarterly `AVG(amount)`.
47. **Detect orphaned foreign key values (data quality check)** → `LEFT JOIN ... WHERE parent.id IS NULL`.
48. **Find the busiest hour of day for orders** → `GROUP BY EXTRACT(HOUR FROM order_date) ORDER BY COUNT(*) DESC`.
49. **Employees who are also customers (shared email across systems)** → `INNER JOIN`/`INTERSECT` on email.
50. **Simulate a leaderboard with rank ties shown identically** → `RANK()` (not `ROW_NUMBER()`), display tie-aware rank.

---

## 23. SQL in Java Full Stack Projects

### JPA / Hibernate / Spring Data JPA
```java
@Entity
public class Employee {
    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long empId;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "dept_id")
    private Department department;

    @Version
    private Integer version;   // optimistic locking
}

public interface EmployeeRepository extends JpaRepository<Employee, Long> {
    Page<Employee> findBySalaryGreaterThan(BigDecimal salary, Pageable pageable);

    @Lock(LockModeType.PESSIMISTIC_WRITE)
    @Query("SELECT e FROM Employee e WHERE e.empId = :id")
    Employee findByIdForUpdate(@Param("id") Long id);
}
```

### Lazy vs Eager Loading
| | Lazy | Eager |
|---|---|---|
| When loaded | On first access (proxy) | Immediately with the parent entity |
| Risk | `LazyInitializationException` outside a session/transaction | Over-fetching, unnecessary joins |
| Default for | `@OneToMany`, `@ManyToMany` | `@ManyToOne`, `@OneToOne` (JPA spec default) |
| Best practice | Default to lazy everywhere; fetch explicitly when needed | Use `JOIN FETCH`/entity graphs for known access patterns |

### The N+1 Problem
```java
List<Employee> employees = employeeRepository.findAll();       // 1 query
employees.forEach(e -> System.out.println(e.getDepartment().getDeptName())); // N queries (lazy load per employee)
```
**Fix:**
```java
@Query("SELECT e FROM Employee e JOIN FETCH e.department")
List<Employee> findAllWithDepartment();
// or an @EntityGraph, or a projection/DTO query
```

### Optimistic vs Pessimistic Locking
```java
// Optimistic — @Version column, throws OptimisticLockException on conflict at commit time
UPDATE employee SET salary = ?, version = version + 1 WHERE emp_id = ? AND version = ?;

// Pessimistic — row-level lock held for the transaction duration
SELECT * FROM employee WHERE emp_id = ? FOR UPDATE;
```
**Use optimistic** for low-contention, high-read scenarios (most web apps). **Use pessimistic** for high-contention critical sections (e.g., seat booking, inventory decrement) where retry storms under optimistic locking would hurt UX.

### Pagination
```java
Pageable pageable = PageRequest.of(0, 20, Sort.by("salary").descending());
Page<Employee> page = employeeRepository.findAll(pageable);
// Under the hood: LIMIT 20 OFFSET 0 — remember the OFFSET pitfall from Section 21 for deep pages
```

### Batch Updates
```java
@Modifying
@Query("UPDATE Employee e SET e.salary = e.salary * 1.1 WHERE e.deptId = :deptId")
int giveRaise(@Param("deptId") Long deptId);
```
```properties
# application.properties — enable JDBC batching for saveAll()
spring.jpa.properties.hibernate.jdbc.batch_size=50
spring.jpa.properties.hibernate.order_inserts=true
spring.jpa.properties.hibernate.order_updates=true
```

### Connection Pooling (HikariCP — Spring Boot default)
```properties
spring.datasource.hikari.maximum-pool-size=20
spring.datasource.hikari.minimum-idle=5
spring.datasource.hikari.connection-timeout=30000
spring.datasource.hikari.idle-timeout=600000
```
**Sizing rule of thumb:** pool size ≈ `((core_count * 2) + effective_spindle_count)` per HikariCP's own guidance — bigger isn't always better; too large a pool can overwhelm the DB with context-switching overhead.

### Transaction Management
```java
@Transactional(isolation = Isolation.READ_COMMITTED, propagation = Propagation.REQUIRED, timeout = 5)
public void transferFunds(Long fromId, Long toId, BigDecimal amount) {
    accountRepository.debit(fromId, amount);
    accountRepository.credit(toId, amount);
}
```
**Common pitfall:** calling a `@Transactional` method from within the *same class* — Spring's proxy-based AOP won't intercept the self-invocation, so the transaction silently doesn't apply. Also: never make an external HTTP call inside a `@Transactional` method — it holds DB connections/locks for the duration of a potentially slow network call (see Section 21/Q16-style incident from real production debugging).

### 🎯 Interview Q&A
**Q1. How do you detect and fix an N+1 problem in a Spring Boot app?**
A: Enable `hibernate.show_sql`/`spring.jpa.show-sql` + a query counter in tests (e.g., using `hibernate-micrometer` or a `HibernateQueryInterceptor`) to catch it in CI, or observe it via slow API response with disproportionate DB call count in APM tools (e.g., Datadog, New Relic). Fix with `JOIN FETCH`, `@EntityGraph`, or a DTO projection query that fetches everything in one round trip.

**Q2. Why does `@Transactional` sometimes silently not work?**
A: Self-invocation within the same class bypasses the Spring AOP proxy; also, `@Transactional` on a `private` method is ignored by default (CGLIB/JDK dynamic proxies can't intercept private/final methods); and checked exceptions don't trigger rollback unless explicitly configured via `rollbackFor`.

**Q3. How do you decide between optimistic and pessimistic locking for a "buy now" e-commerce inventory decrement?**
A: For flash-sale/high-contention inventory decrements, pessimistic locking (`SELECT ... FOR UPDATE`) avoids the "everyone fails and retries simultaneously" thundering-herd problem that optimistic locking would cause under heavy concurrent access to the same row — even though it holds a lock, the alternative (mass `OptimisticLockException` retries) is often worse UX and load on a genuinely hot row.

---

## 24. SQL System Design Concepts

### Table Design & Indexing Strategy
- Surrogate `BIGINT`/UUIDv7 PKs, foreign keys narrow and indexed, composite indexes matching real query patterns (leftmost-prefix rule).
- Separate hot (frequently updated) and cold (rarely changed) columns into different tables when a table becomes very wide.

### Scaling a Database
```
                     ┌───────────────┐
                     │   Application  │
                     └───────┬───────┘
             writes ─────────┼───────── reads
                     ┌────────▼────────┐        ┌─────────────┐
                     │  Master (Write)  │──────▶│ Read Replica│
                     └──────────────────┘  async  └─────────────┘
                                            replication (may lag)
```
- **Read Replicas / Master-Slave:** Route reads to replicas, writes to master — scales read throughput, but replicas can **lag** (eventual consistency) — a common FAANG-style gotcha: "user updates profile, then immediately reads it back from a replica and sees stale data" → mitigate with read-after-write consistency (route that specific read to master, or use a session-sticky replica).
- **Sharding:** Horizontal split across independent DB instances by a shard key — needed when write throughput/storage exceeds a single master's capacity. Adds complexity: cross-shard joins, re-sharding/rebalancing, and often a routing layer (e.g., Vitess for MySQL, Citus for PostgreSQL).
- **Database Caching (Redis):** Cache-aside pattern — application checks Redis first, falls back to DB on miss, populates cache with a TTL. Must handle **cache invalidation** on writes (the classic "there are only two hard problems in computer science" joke, but a real design concern) — options: write-through, TTL-based expiry, or explicit invalidation on update.
- **Distributed Transactions:** Across microservices/shards, classic 2PC (two-phase commit) is heavyweight and fragile at scale — most modern systems use the **Saga pattern** (a sequence of local transactions with compensating actions on failure) instead.
- **Eventual Consistency:** Accepting that replicas/caches/downstream services may be briefly stale in exchange for availability and partition tolerance (CAP theorem trade-off) — appropriate for non-critical-path data (e.g., "likes count") but not for account balances.

### 🎯 Interview Q&A
**Q1. How would you design the database layer for a high-traffic e-commerce checkout flow?**
A: Master DB for the transactional write path (inventory decrement + order creation in one transaction, pessimistic lock or `SELECT ... FOR UPDATE` on the inventory row); read replicas for product catalog browsing (tolerant of slight staleness); Redis cache for product detail pages and session data; a message queue (Kafka) to decouple downstream steps (email confirmation, analytics) from the synchronous checkout transaction, so the checkout DB transaction stays as short as possible.

**Q2. Master-Slave replication lag caused a customer to see an old order status right after placing an order — how do you fix this at the architecture level?**
A: Implement **read-your-own-writes consistency** — route reads immediately following a write from the same user session to the master (or a replica guaranteed to be caught up, e.g., via replica lag monitoring), for a short window after the write, then fall back to replicas for subsequent reads.

**Q3. Why is 2PC (two-phase commit) generally avoided in modern distributed systems, and what's used instead?**
A: 2PC requires a coordinator to hold locks across all participants until every participant confirms — if any participant or the coordinator fails mid-protocol, resources can be blocked indefinitely, and it doesn't scale well across many independent services. The **Saga pattern** instead runs a sequence of local transactions, each with a corresponding **compensating transaction** to undo it if a later step fails — trading strict atomicity for availability and scalability, which is generally the right trade-off for microservice architectures.

---

## 25. Top 100 SQL Interview Questions and Answers

> Format: **Question → Expected Answer (brief) → Follow-up.** Grouped by topic; each entry cross-references the detailed section above for full explanation.

### A. Fundamentals & DDL/DML (1–10)
1. **What is the difference between a schema and a database?** → A schema is a logical namespace/collection of objects (tables, views) within a database; some RDBMS (PostgreSQL) support multiple schemas per database, others (MySQL) treat "schema" and "database" as synonyms. *Follow-up: how does this affect multi-tenant design?*
2. **What is a surrogate key?** → An artificial key (auto-increment/UUID) with no business meaning, used as PK instead of a natural key. *Follow-up: trade-offs vs natural keys (Section 3, Q5).*
3. **Difference between `CHAR` and `VARCHAR`?** → See Section 2. *Follow-up: impact on index size.*
4. **What is a NULL, and how does it behave in comparisons?** → Represents unknown/missing data; any comparison with NULL yields UNKNOWN, not TRUE/FALSE. *Follow-up: NULL trap with NOT IN (Section 5, Q1).*
5. **Difference between `DELETE`, `TRUNCATE`, `DROP`?** → See Section 1. *Follow-up: which are DDL vs DML and why it matters for transactions.*
6. **What is a sequence, and when would you use one over auto-increment?** → A sequence is an independent DB object generating unique numbers, not tied to a single table — useful when multiple tables need coordinated/shared numbering (Oracle/PostgreSQL). *Follow-up: gaps in sequence values are normal and expected.*
7. **What is `AUTO_INCREMENT`/`IDENTITY`/`SERIAL`?** → RDBMS-specific auto-numbering for a PK column. *Follow-up: why gaps can appear (rolled-back transactions still consume the value).*
8. **Difference between `INSERT INTO ... VALUES` and `INSERT INTO ... SELECT`?** → The latter inserts rows directly from a query's result set — used for copying/migrating data. *Follow-up: performance with large source sets — batch it.*
9. **What is a composite key vs a compound key?** → Often used interchangeably; a composite key is a PK made of 2+ columns. *Follow-up: impact on FK design elsewhere.*
10. **What does `ALTER TABLE ... ADD CONSTRAINT` do, and is it safe on a live production table?** → Adds a new constraint to an existing table; on large tables it may require a full table scan/lock (validate existing data) — needs to be done carefully (e.g., `NOT VALID` + `VALIDATE CONSTRAINT` in PostgreSQL to avoid a long lock).

### B. Joins & Set Operations (11–20)
11. **Explain all join types with a diagram.** → See Section 4.
12. **Why does a `WHERE` clause filter turn a LEFT JOIN into an INNER JOIN?** → See Section 4, Q3.
13. **What is an anti-join, and how do you write one?** → `LEFT JOIN ... WHERE right.col IS NULL` or `NOT EXISTS` — returns rows in A with no match in B.
14. **What is a semi-join?** → Returns rows from A that **have** a match in B, without duplicating A's rows even if B has multiple matches — typically written as `EXISTS` or `IN`, not a JOIN (which could duplicate rows).
15. **Can a JOIN condition use `<`, `>` instead of `=`?** → Yes — a "non-equi join," e.g., matching salary bands: `JOIN salary_band sb ON e.salary BETWEEN sb.min_sal AND sb.max_sal`.
16. **`UNION` vs `JOIN` — fundamentally different how?** → `JOIN` combines columns from two tables side-by-side (horizontal); `UNION` stacks compatible rows from two queries (vertical).
17. **How would you find data present in Table A but not Table B, and vice versa, in one query?** → `FULL OUTER JOIN` with `WHERE a.id IS NULL OR b.id IS NULL`.
18. **What happens with a JOIN on a nullable column?** → Rows with NULL in the join column never match (NULL ≠ NULL), effectively excluding them — a common source of "missing data" bugs.
19. **How do you self-join a table to find duplicate entries with different IDs?** → `JOIN` the table to itself on the duplicate-defining columns with `t1.id <> t2.id`, then dedupe keeping the min `id`.
20. **Explain lateral joins / `CROSS APPLY` (SQL Server) / `LATERAL` (PostgreSQL).** → Allows a derived table on the right side of a join to reference columns from the left side (like a correlated subquery, but returning multiple columns/rows) — used for "top N per group" queries efficiently.

### C. Aggregation & Grouping (21–30)
21. **Why can't you filter on an aggregate in `WHERE`?** → See Section 6, Q3.
22. **Multiple approaches to find the 2nd highest salary.** → See Section 7.
23. **How do you find duplicate records efficiently on a huge table?** → `GROUP BY` + `HAVING COUNT(*) > 1` with an index on the grouping columns, or a window-function approach to also enable ranked deletion.
24. **Explain `ROLLUP` and `CUBE`.** → `GROUP BY ROLLUP(dept_id, job_title)` generates subtotal rows (per dept, and grand total); `CUBE` generates subtotals for every combination of grouping columns — used for reporting/OLAP cross-tabs.
25. **`GROUPING SETS` — what problem does it solve?** → Lets you compute multiple different `GROUP BY` groupings in a single query/scan instead of multiple `UNION`ed queries.
26. **How do you compute a percentage of total within each group?** → `value * 100.0 / SUM(value) OVER ()` (window function, no self-join needed).
27. **`HAVING COUNT(DISTINCT col) > 1` — what does this check?** → Groups where a column has more than one distinct value — useful for detecting inconsistent data (e.g., a customer with multiple different registered addresses).
28. **Can `GROUP BY` use a column alias defined in `SELECT`?** → MySQL/PostgreSQL: yes. SQL Server/Oracle (strict ANSI): generally no — must repeat the expression.
29. **What's the difference between `COUNT(*)` and `COUNT(col) OVER ()`?** → The former is a regular aggregate collapsing rows; the latter is a window function preserving all rows while adding the total count as a column on each.
30. **How do you find groups where all values meet a condition (e.g., departments where everyone earns > 50k)?** → `GROUP BY dept_id HAVING MIN(salary) > 50000`.

### D. Subqueries, CTEs, Views (31–40)
31. **Correlated vs non-correlated subquery — key difference?** → See Section 8.
32. **When would a CTE be preferable to a temp table?** → See Section 19, Q1.
33. **Can a view include an `ORDER BY`?** → Standard SQL disallows meaningful `ORDER BY` in a view definition (result order isn't guaranteed on later `SELECT`s); some RDBMS allow it syntactically but don't guarantee it persists through further queries against the view.
34. **What is an updatable view, and what disqualifies a view from being updatable?** → See Section 11, Q2. Joins, `GROUP BY`, `DISTINCT`, and aggregates disqualify direct updatability.
35. **What's the danger of nesting views (a view built on top of another view)?** → Performance opacity — the optimizer must unravel multiple layers, and a seemingly simple query against a view can hide an expensive multi-join chain; also makes schema changes riskier (breaking hidden dependents).
36. **Materialized view refresh strategies?** → See Section 11, Q3.
37. **How do you paginate results from a CTE?** → Apply `LIMIT`/`OFFSET` (or keyset filtering) in the outer query selecting from the CTE, same as any derived table.
38. **What is a lateral/correlated derived table used for?** → "Top N per group" queries — e.g., top 3 orders per customer via `CROSS APPLY`/`LATERAL` with a `LIMIT 3` inside.
39. **Can you `INSERT`/`UPDATE`/`DELETE` using a CTE?** → Yes, in most modern RDBMS — the CTE can be referenced in the subsequent DML statement (e.g., `WITH ... DELETE FROM t WHERE id IN (SELECT id FROM cte)`).
40. **What's a "derived table" and how does it differ from a CTE?** → Both are subqueries acting as a temporary result set; a derived table is inline in `FROM (...)`, a CTE is named upfront via `WITH` — CTEs are more readable and reusable within the same statement, and support recursion.

### E. Indexing & Performance (41–55)
41. **How does a B-Tree index work internally?** → See Section 12.
42. **Clustered vs non-clustered index?** → See Section 12.
43. **When is an index NOT used even if it exists?** → See Section 12.
44. **What is index fragmentation, and how do you fix it?** → Over time, `INSERT`/`UPDATE`/`DELETE` cause page splits and unused space within index pages, degrading performance; fixed via periodic `REBUILD`/`REORGANIZE` (SQL Server), `REINDEX` (PostgreSQL), or `OPTIMIZE TABLE` (MySQL).
45. **What is a covering index?** → See Section 12, Q2.
46. **Explain `EXPLAIN` vs `EXPLAIN ANALYZE`.** → `EXPLAIN` shows the estimated plan without executing; `EXPLAIN ANALYZE` actually runs the query and shows real timing/row counts alongside estimates — critical for spotting stale-statistics-driven bad plans.
47. **What is a sargable predicate?** → "Search ARGument ABLE" — a condition the optimizer can satisfy using an index seek (e.g., `col = value`, `col > value`), as opposed to one wrapped in a function/expression that forces a scan.
48. **How do you find unused indexes in production?** → Query engine-specific usage stats (`pg_stat_user_indexes`, `sys.dm_db_index_usage_stats`) over a representative time window before dropping any index.
49. **What causes a deadlock, and how do you prevent it?** → See earlier SQL.md Q14-style scenario — inconsistent lock acquisition order between transactions; prevent via consistent ordering, shorter transactions, and retry-with-backoff as a safety net.
50. **What's the impact of a long-running transaction on other queries?** → Holds locks and (in MVCC systems like PostgreSQL) prevents vacuum/cleanup of old row versions, potentially causing table bloat and blocking concurrent writers.
51. **Explain query plan caching and parameter sniffing.** → The optimizer caches an execution plan keyed by the query text/shape; if the first execution's parameter values are atypical (e.g., a rare filter value), the cached plan may perform badly for typical/common parameter values later — mitigated via `OPTION (RECOMPILE)`, plan guides, or optimizing for typical values.
52. **What is a bitmap index, and when is it useful (Oracle)?** → Efficient for low-cardinality columns (e.g., gender, boolean flags) in read-heavy/OLAP contexts; poor fit for high-write OLTP tables due to locking granularity.
53. **How do you optimize a `LIKE '%text%'` search at scale?** → Full-text index (Section 5, Q4) instead of a leading-wildcard LIKE.
54. **What's the difference between vertical and horizontal scaling for a database?** → Vertical = bigger single server (more CPU/RAM); horizontal = more servers (replicas/shards) — see Section 24.
55. **How do connection pool size and DB `max_connections` interact across multiple app instances?** → Total connections across all app instances/pods must stay under the DB's configured limit; oversizing pools per instance in a horizontally-scaled deployment can exhaust the DB — must be sized holistically, not per-instance in isolation.

### F. Transactions, Concurrency, Normalization (56–70)
56. **Explain ACID with a banking example.** → See Section 14, Q1.
57. **Explain all 4 isolation levels and their trade-offs.** → See Section 15.
58. **Dirty read vs non-repeatable read vs phantom read?** → See Section 15.
59. **Optimistic vs pessimistic locking — give a real use case for each.** → See Section 23, Q3.
60. **What is MVCC (Multi-Version Concurrency Control)?** → A technique (used by PostgreSQL, MySQL/InnoDB, Oracle) where readers see a consistent snapshot of data without blocking writers, by maintaining multiple row versions — avoids read locks entirely for `SELECT`.
61. **1NF, 2NF, 3NF, BCNF — explain with an example each.** → See Section 13.
62. **When would you deliberately denormalize?** → See Section 13.
63. **What's a transitive dependency?** → A non-key column depending on another non-key column rather than directly on the PK (3NF violation).
64. **What is a `SAVEPOINT` used for?** → See Section 14 — partial rollback within a larger transaction.
65. **What does `SET TRANSACTION READ ONLY` achieve?** → Signals to the optimizer/engine that no writes will occur, potentially enabling optimizations and preventing accidental writes in reporting connections.
66. **What is a "lost update" and how is it prevented?** → Two transactions read the same row, both update based on the stale read, and the second commit silently overwrites the first's change — prevented via optimistic locking (version column) or explicit row locking (`SELECT ... FOR UPDATE`).
67. **Explain the difference between a statement-level and row-level trigger.** → Row-level fires once per affected row (`FOR EACH ROW`); statement-level fires once per statement regardless of row count — useful for summary-level side effects.
68. **What is two-phase locking (2PL), conceptually?** → A concurrency control protocol where a transaction acquires all locks it needs before releasing any (growing phase then shrinking phase) — guarantees serializability, underlies how `SERIALIZABLE` isolation is often implemented.
69. **How do you handle schema migrations on a live, high-traffic table without downtime?** → Additive, backward-compatible changes first (add nullable column, deploy code that writes both old+new, backfill in batches, then switch reads, then drop old column) — the "expand-contract" migration pattern.
70. **What is BCNF and how does it differ from 3NF?** → See Section 13.

### G. Window Functions & Advanced Querying (71–85)
71. **`ROW_NUMBER()` vs `RANK()` vs `DENSE_RANK()`.** → See Section 20.
72. **How do you get a running total?** → See Section 20.
73. **How do you get a moving average?** → See Section 20.
74. **What does the `OVER (PARTITION BY ... ORDER BY ... ROWS BETWEEN ...)` frame clause control?** → Which rows within the partition are visible to the window function for the current row — default is "unbounded preceding to current row" when `ORDER BY` is present.
75. **How would you find the top 2 highest-paid employees per department without window functions (legacy engine)?** → Correlated subquery counting how many salaries in the same department are greater (Section 4, Q5 style).
76. **What's the difference between `LEAD`/`LAG` and a self-join for comparing adjacent rows?** → Functionally similar, but window functions avoid the join entirely — cleaner and typically faster since it's a single sorted pass rather than a join operation.
77. **How do you rank within multiple partitions simultaneously (e.g., rank by dept AND by region)?** → Two separate window function calls with different `PARTITION BY` clauses in the same `SELECT`.
78. **Explain `NTILE()` with a real use case.** → See Section 20 — customer segmentation into quartiles/deciles.
79. **Can window functions be used in the `WHERE` clause directly?** → No — see Section 20, Q4.
80. **What is `QUALIFY` and which databases support it?** → A clause (Snowflake, BigQuery, DuckDB) that lets you filter directly on a window function result without a wrapping subquery — not yet standard ANSI SQL or supported in PostgreSQL/MySQL/SQL Server/Oracle as of this writing.
81. **How do you calculate the difference between each row and the previous row's value?** → `value - LAG(value) OVER (ORDER BY ...)`.
82. **How would you detect gaps in a time series (e.g., missing daily sales records)?** → Generate a full date series (recursive CTE, Section 19 Q3) and `LEFT JOIN` against actual data, filtering where actual is NULL.
83. **What is `PERCENT_RANK()` and `CUME_DIST()`?** → Relative ranking functions returning a fraction (0 to 1) representing a row's relative standing within its partition — used for percentile-based reporting.
84. **How do window functions interact with `GROUP BY` in the same query?** → `GROUP BY` executes first (collapsing to groups), then a window function can operate on the grouped/aggregated rows (e.g., ranking department averages, as in Section 22 Q22).
85. **Give a real interview-style query: find the 3-day period with maximum total revenue.** → See Section 22, Q44 pattern (windowed `SUM` with a 3-row frame).

### H. Java Full Stack / System Design (86–100)
86. **What causes the N+1 problem, and how is it fixed?** → See Section 23.
87. **Lazy vs Eager fetching — default behavior per relationship type?** → See Section 23.
88. **Optimistic vs pessimistic locking in JPA — annotations used?** → `@Version` for optimistic; `@Lock(LockModeType.PESSIMISTIC_WRITE)` for pessimistic.
89. **Why does `@Transactional` sometimes not work as expected?** → See Section 23, Q2.
90. **How do you tune a HikariCP connection pool?** → See Section 23.
91. **How do you implement pagination in a Spring Data JPA repository?** → `Pageable`/`Page<T>` — see Section 23.
92. **How do you batch insert 100,000 records efficiently with Hibernate?** → Enable `hibernate.jdbc.batch_size`, disable second-level cache for the batch session, periodically `flush()` + `clear()` the persistence context to avoid heap bloat.
93. **What is a read replica, and how do you route reads to it in a Spring Boot app?** → A secondary DB kept in sync via replication; routed via Spring's `AbstractRoutingDataSource` with a `@Transactional(readOnly=true)`-aware routing key, or a dedicated read-only `DataSource` bean.
94. **How would you design database sharding for a multi-tenant SaaS product?** → Options: shared schema with a `tenant_id` column + row-level filtering (simplest, works for small/medium tenants); schema-per-tenant (better isolation, harder to scale to thousands of tenants); database-per-tenant (strongest isolation, used for large enterprise customers) — often a hybrid based on tenant size/tier.
95. **What is the Saga pattern, and when would you use it over a distributed transaction?** → See Section 24, Q3 — for cross-service order+payment+inventory flows in a microservice architecture.
96. **How do you cache database query results with Redis, and how do you avoid stale cache issues?** → Cache-aside with a sensible TTL, explicit invalidation/update on writes to the underlying row, and consider a versioned cache key (e.g., include an `updated_at` timestamp) to naturally invalidate on change.
97. **What is eventual consistency, and where is it acceptable in a Java microservice system?** → See Section 24 — acceptable for read-replica-served catalog data, denormalized search indexes, and analytics; not acceptable for the authoritative financial ledger write path.
98. **How do you avoid a "thundering herd" of duplicate optimistic-lock retries under high concurrency?** → Add jittered exponential backoff to retries, or switch the specific hot-row operation to pessimistic locking / a queue-based serialized processor (Section 23, Q3).
99. **How do you test SQL query performance changes before deploying to production?** → Test against a production-representative data volume (not a tiny dev dataset), compare `EXPLAIN ANALYZE` before/after, and use a staging environment with realistic concurrency via load testing tools (JMeter/k6) rather than trusting dev-environment timing alone.
100. **As a senior engineer, how do you decide whether a performance problem should be solved at the SQL layer, the ORM layer, or the architecture layer (caching/replicas/sharding)?** → Start at the cheapest, most localized fix (query/index) and escalate only if the root cause is structural: a single bad query → fix the query/index; a systemic N+1/ORM misuse pattern → fix at the ORM/repository layer and add regression tests; a genuine scale ceiling (too much read/write volume for any single well-tuned instance) → escalate to caching, read replicas, or sharding, communicating the added operational complexity and eventual-consistency trade-offs to the team before committing to it.

---

## 26. Top 25 SQL Coding Problems

**1. Second Highest Salary**
Data: `employee(emp_id, salary)`.
```sql
SELECT MAX(salary) FROM employee WHERE salary < (SELECT MAX(salary) FROM employee);
```
Alt: `DENSE_RANK()` (Section 7). **Optimization:** index on `salary` for large tables.

**2. Nth Highest Salary (parameterized)**
```sql
SELECT salary FROM (SELECT salary, DENSE_RANK() OVER (ORDER BY salary DESC) rnk FROM employee) t WHERE rnk = :N;
```
Alt: correlated subquery counting distinct greater salaries. **Optimization:** `DENSE_RANK` avoids O(n²) correlated subquery cost.

**3. Duplicate Emails**
Data: `person(id, email)`.
```sql
SELECT email FROM person GROUP BY email HAVING COUNT(*) > 1;
```
Alt: self-join `p1.email = p2.email AND p1.id <> p2.id`. **Optimization:** index on `email`.

**4. Employees Earning More Than Their Manager**
See Section 4, Q1. **Optimization:** index on `manager_id`.

**5. Department Highest Salary**
See Section 7. **Optimization:** window function avoids N self-joins per department.

**6. Rising Temperature (compare with previous day)**
Data: `weather(id, record_date, temperature)`.
```sql
SELECT w1.id FROM weather w1
JOIN weather w2 ON w1.record_date = w2.record_date + INTERVAL '1 day'
WHERE w1.temperature > w2.temperature;
```
Alt: `LAG()` window function — cleaner, single scan.

**7. Consecutive Numbers (3+ repeats in a log table)**
```sql
SELECT DISTINCT l1.num FROM logs l1, logs l2, logs l3
WHERE l1.id = l2.id - 1 AND l2.id = l3.id - 1
AND l1.num = l2.num AND l2.num = l3.num;
```
Alt: `LAG(num,1)`/`LAG(num,2)` window comparison — avoids self-join, scales far better.

**8. Employees With Missing Manager Chain (Trips and Users style — cancellation rate)**
Data: `trips(id, client_id, driver_id, status)`, `users(users_id, banned)`.
```sql
SELECT t.request_at AS "Day",
       ROUND(SUM(CASE WHEN t.status LIKE 'cancelled%' THEN 1 ELSE 0 END) / COUNT(*)::NUMERIC, 2) AS "Cancellation Rate"
FROM trips t
JOIN users c ON t.client_id = c.users_id AND c.banned = 'No'
JOIN users d ON t.driver_id = d.users_id AND d.banned = 'No'
GROUP BY t.request_at;
```
**Optimization:** filter banned users via JOIN condition (not WHERE with subquery) for index usage.

**9. Department Top 3 Salaries**
See Section 22, Q7. **Optimization:** `DENSE_RANK` handles ties per business rule of "top 3 distinct salary levels."

**10. Exchange Seats (odd/even swap)**
```sql
SELECT
  CASE WHEN id % 2 = 1 AND id = (SELECT MAX(id) FROM seat) THEN id
       WHEN id % 2 = 1 THEN id + 1
       ELSE id - 1 END AS id, student
FROM seat ORDER BY id;
```

**11. Cumulative Salary of Employee (excluding most recent month)**
```sql
SELECT emp_id, month, SUM(salary) OVER (PARTITION BY emp_id ORDER BY month
       ROWS BETWEEN UNBOUNDED PRECEDING AND 1 PRECEDING) AS cumulative
FROM employee_salary_history;
```

**12. Friend Requests / Acceptance Rate**
```sql
SELECT ROUND(
  (SELECT COUNT(DISTINCT requester_id, accepter_id) FROM request_accepted) /
  NULLIF((SELECT COUNT(DISTINCT sender_id, send_to_id) FROM friend_request), 0), 2
) AS accept_rate;
```

**13. Market Analysis (first order year per user)**
```sql
SELECT u.user_id AS buyer_id, u.join_date,
       COUNT(o.order_id) AS orders_in_2019
FROM users u
LEFT JOIN orders o ON u.user_id = o.buyer_id AND EXTRACT(YEAR FROM o.order_date) = 2019
GROUP BY u.user_id, u.join_date;
```

**14. Game Play Analysis (first login, consecutive-day retention)**
```sql
WITH first_login AS (
    SELECT player_id, MIN(event_date) AS first_date FROM activity GROUP BY player_id
)
SELECT ROUND(COUNT(DISTINCT a.player_id) * 1.0 / (SELECT COUNT(DISTINCT player_id) FROM activity), 2) AS fraction
FROM activity a JOIN first_login f
ON a.player_id = f.player_id AND a.event_date = f.first_date + INTERVAL '1 day';
```

**15. Number of Transactions per Visit (gap-based sessionization)**
Alt approach: `LAG(transaction_date)` + a threshold to define a new "session" when gap exceeds N minutes — classic sessionization pattern.

**16. Employee Bonus (LEFT JOIN with NULL/condition filter)**
```sql
SELECT e.emp_name, b.bonus FROM employee e
LEFT JOIN bonus b ON e.emp_id = b.emp_id
WHERE b.bonus < 1000 OR b.bonus IS NULL;
```

**17. Product Sales Analysis (best-selling product per year)**
```sql
SELECT product_id, year FROM (
    SELECT product_id, year, SUM(quantity) AS total_qty,
           RANK() OVER (PARTITION BY year ORDER BY SUM(quantity) DESC) rnk
    FROM sales GROUP BY product_id, year
) t WHERE rnk = 1;
```

**18. Investments in 2016 (unique lat/lon, matching TIV_2015)**
Demonstrates multi-condition aggregate filtering with `HAVING COUNT(*) > 1` combined across two dimensions.

**19. Managers With At Least 5 Direct Reports**
```sql
SELECT m.emp_name FROM employee e
JOIN employee m ON e.manager_id = m.emp_id
GROUP BY m.emp_id, m.emp_name
HAVING COUNT(*) >= 5;
```

**20. Median Employee Salary**
```sql
SELECT PERCENTILE_CONT(0.5) WITHIN GROUP (ORDER BY salary) AS median_salary FROM employee;
```

**21. Human Traffic of Stadium (3+ consecutive rows with people >= 100)**
Classic "consecutive rows" problem solved with the row-number-difference grouping trick (Section 20, Q3) or `LEAD`/`LAG` chained checks.

**22. Employees Whose Manager Left the Company**
```sql
SELECT e.emp_id FROM employee e
WHERE e.manager_id IS NOT NULL
AND e.manager_id NOT IN (SELECT emp_id FROM employee)
ORDER BY e.emp_id;
```
**Note:** must ensure `manager_id` subquery excludes NULLs (NULL trap, Section 5 Q1) — here `emp_id` is a PK so it's safe.

**23. Monthly Active Users / Retention Rate**
Pattern: `DATE_TRUNC('month', activity_date)` grouping + `LAG` comparison across months per user cohort.

**24. Users That Actively Requested Confirmation Messages**
```sql
SELECT DISTINCT user_id FROM confirmations
WHERE user_id IN (
    SELECT user_id FROM confirmations
    GROUP BY user_id
    HAVING COUNT(*) >= 3
);
```

**25. Immediate Food Delivery (percentage of on-time first orders)**
```sql
WITH first_orders AS (
    SELECT customer_id, MIN(order_date) AS first_order_date FROM delivery GROUP BY customer_id
)
SELECT ROUND(100.0 * SUM(CASE WHEN d.order_date = d.customer_pref_delivery_date THEN 1 ELSE 0 END)
       / COUNT(*), 2) AS immediate_percentage
FROM delivery d
JOIN first_orders f ON d.customer_id = f.customer_id AND d.order_date = f.first_order_date;
```

---

## 27. HR + Technical SQL Round Preparation

### Common SQL Round Questions
- "Walk me through how you'd design a schema for [X — e.g., a ride-sharing app]." — expects entity identification, PK/FK design, normalization reasoning out loud.
- "Write a query to find the Nth highest salary" — near-universal screening question.
- "How do you optimize a slow query you've never seen before?" — expects a systematic process (Section 21), not a guess.
- "Tell me about a time you fixed a production database performance issue." — behavioral + technical hybrid; expects STAR format with real metrics (e.g., "reduced query time from 8s to 200ms by adding a composite index").
- "What's the difference between SQL and NoSQL, and when would you choose each?" — expects trade-off reasoning (consistency vs scale, schema flexibility vs integrity), not a one-sided answer.

### How Senior Developers (8+ Years) Should Answer
- **Lead with reasoning, not just syntax.** Instead of just writing the query, briefly state the approach and trade-offs ("I'd use a window function here since it avoids a self-join and handles ties correctly").
- **Mention production experience.** Reference real scenarios (index trade-offs, deadlocks, N+1 problems) rather than purely textbook answers — this is what differentiates 8-year candidates from freshers.
- **Ask clarifying questions before coding** — e.g., "Should ties count as the same rank, or should duplicates be excluded?" for a "Nth highest salary" question — shows senior-level rigor.
- **Discuss scale explicitly** — "This works fine at 10K rows; at 500M rows I'd want to verify the plan and likely add a covering index."

### Mistakes to Avoid
- Jumping straight to code without confirming the exact requirement/edge cases (ties, NULLs, duplicates).
- Forgetting to handle NULLs (the `NOT IN` trap is a very common on-the-spot failure).
- Overusing subqueries where a JOIN or window function would be clearer/faster.
- Ignoring the interviewer's follow-up about scale — a "correct" query that would fall over at production data volume is an incomplete answer for an 8-year-experience candidate.
- Being unable to explain *why* a query works, only that it does — interviewers at this level probe reasoning, not memorization.
- Refusing to admit "I'd need to check `EXPLAIN` to be sure" — senior candidates should show comfort with uncertainty and verification rather than false confidence.

---

## 28. Final Revision Sheet

### 📄 SQL Cheat Sheet
```sql
SELECT DISTINCT col FROM table WHERE cond GROUP BY col HAVING agg_cond ORDER BY col LIMIT n OFFSET m;
-- Logical execution order: FROM → WHERE → GROUP BY → HAVING → SELECT → ORDER BY → LIMIT
```

### 🔗 Joins Cheat Sheet
| Join | Keeps |
|---|---|
| INNER | Matching rows only |
| LEFT | All left + matched right |
| RIGHT | All right + matched left |
| FULL OUTER | All rows both sides |
| CROSS | Cartesian product |
| SELF | Table joined to itself |

### 🪟 Window Functions Cheat Sheet
| Function | Purpose |
|---|---|
| `ROW_NUMBER()` | Unique sequential number, no ties |
| `RANK()` | Ties share rank, gaps after ties |
| `DENSE_RANK()` | Ties share rank, no gaps |
| `LAG`/`LEAD` | Previous/next row value |
| `NTILE(n)` | Bucket into n groups |
| `FIRST_VALUE`/`LAST_VALUE` | First/last in frame (needs explicit frame for LAST_VALUE) |
| `SUM() OVER (ORDER BY ...)` | Running total |

### 📇 Indexing Cheat Sheet
| Rule | Why |
|---|---|
| Index `WHERE`/`JOIN`/`ORDER BY` columns | Enables seeks instead of scans |
| Leftmost column of composite index must be used | Leftmost-prefix rule |
| Avoid functions on indexed columns | Keeps predicate sargable |
| Don't over-index write-heavy tables | Every index slows INSERT/UPDATE/DELETE |
| Use covering indexes for hot reads | Avoids extra row lookup |

### ⚛️ ACID Cheat Sheet
| Letter | Guarantees |
|---|---|
| Atomicity | All-or-nothing |
| Consistency | Constraints always honored |
| Isolation | Concurrent transactions don't interfere |
| Durability | Committed = permanent |

### 🚀 Performance Tuning Cheat Sheet
1. Identify slow query (slow query log / APM / `pg_stat_statements`).
2. `EXPLAIN ANALYZE` — compare estimated vs actual rows.
3. Check index usage & sargability.
4. Check statistics freshness.
5. Check for lock contention.
6. Consider partitioning/sharding only if structurally necessary.
7. Batch large writes; use keyset pagination for large offsets.

---

### 🗓️ 1-Day Revision Plan
| Time | Focus |
|---|---|
| Morning | Joins, Subqueries, GROUP BY/HAVING, Window Functions |
| Afternoon | Indexing, Transactions, Isolation Levels |
| Evening | Top 25 Coding Problems + Top 100 Q&A skim |

### 📅 7-Day Revision Schedule
| Day | Topics |
|---|---|
| 1 | Fundamentals, Data Types, Constraints |
| 2 | Joins (all types + tricky WHERE/ON cases) |
| 3 | Aggregates, GROUP BY/HAVING, Subqueries |
| 4 | Window Functions (deep practice) |
| 5 | Indexing + Performance Tuning |
| 6 | Transactions, Isolation Levels, Normalization |
| 7 | Java/JPA integration + 50 Scenarios + mock interview |

### 🗓️ 30-Day Interview Preparation Roadmap
| Week | Focus |
|---|---|
| Week 1 | Fundamentals → Joins → Operators → Aggregates (Sections 1–6) |
| Week 2 | GROUP BY/HAVING → Subqueries → Functions → Set Ops → Views (Sections 7–11) |
| Week 3 | Indexing → Normalization → Transactions → Isolation → Procs/Functions/Triggers/CTEs (Sections 12–19) |
| Week 4 | Window Functions → Performance Tuning → 50 Scenarios → Java/JPA → System Design → Mock Interviews (Sections 20–28) |

---

## ✅ Conclusion

For an 8-year Java Full Stack Developer, SQL interviews at product companies, service companies, and FAANG-level organizations rarely stop at syntax — they probe **judgment**: knowing *why* an index helps or hurts, *when* to denormalize, *how* to diagnose a live production slowdown, and *how* SQL decisions ripple through a Spring Boot/JPA application (N+1 queries, locking strategy, connection pooling, transaction boundaries). Master the fundamentals in Sections 1–11, internalize the performance and concurrency mechanics in Sections 12–21, and drill the scenario-based and system-design questions in Sections 22–28 until you can explain your reasoning out loud, not just produce a correct query. That combination — correctness, performance awareness, and clear communication of trade-offs — is exactly what separates an 8-year candidate from a fresher in the eyes of a senior interviewer.










