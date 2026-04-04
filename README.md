# Master Java Latest Features

> **Interview Rehearsal Guide** — Java 8 Features  
> Every section follows the pattern: **What → Why → How → Gotchas → Interview Q&A**

---

## Java 8 - Comprehensive Guide

### Lambda Expressions
Lambda expressions are a fundamental feature introduced in Java 8, representing a concise way to express instances of functional interfaces.

#### What are Lambda Expressions?
- A lambda expression is equivalent to an anonymous function (a function without a name)
- Lambda expressions are also referred to as anonymous functions, which have all the method properties such as:
  - Method parameters
  - Method body
  - Return type (optional)
- Lambdas are not tied to any specific class like regular methods
- Lambda expressions can be assigned to variables and passed as parameters

#### Syntax
```java
// Basic syntax
(parameter1, parameter2) -> { body }

// Examples
() -> System.out.println("Hello World")           // No parameters
x -> x * 2                                        // Single parameter
(x, y) -> x + y                                   // Multiple parameters
(x, y) -> { 
    int sum = x + y; 
    return sum; 
}                                                 // Multiple statements
```

#### Usage and Benefits
- **Primary Usage**: Implementing functional interfaces in a more concise way
- **Functional Interfaces**: Also known as SAM (Single Abstract Method) interfaces
- **Definition**: An interface that contains exactly one abstract method is considered a functional interface
- **Examples**: Runnable, Comparator, Callable interfaces
- **Benefits**:
  - Reduces boilerplate code
  - Improves code readability
  - Enables functional programming paradigm
  - Facilitates parallel processing

```java
@FunctionalInterface
public interface Runnable {
    /**
     * Runs this operation.
     */
    void run();
}

// Before Java 8 (Anonymous inner class)
Runnable runnable1 = new Runnable() {
    @Override
    public void run() {
        System.out.println("Running...");
    }
};

// With Java 8 Lambda
Runnable runnable2 = () -> System.out.println("Running...");
```
### Functional Interfaces
A functional interface is an interface that contains exactly one abstract method and serves as the foundation for lambda expressions.

#### Key Characteristics
- **Single Abstract Method**: Contains exactly one abstract method (SAM)
- **Historical Context**: Functional interfaces have existed since Java 1.0 (e.g., Runnable, Callable)
- **@FunctionalInterface Annotation**: Introduced in JDK 1.8, this annotation is optional but recommended
  - Provides compile-time checking
  - Generates documentation
  - Prevents accidental addition of abstract methods

#### Built-in Functional Interfaces in Java 8

##### 1. Function<T, R>
- **Purpose**: Takes an input of type T and returns an output of type R
- **Method**: `R apply(T t)`
- **Use Case**: Transformation operations

```java
Function<String, Integer> stringLength = str -> str.length();
Integer length = stringLength.apply("Hello"); // Returns 5
```

##### 2. Predicate<T>
- **Purpose**: Takes an input of type T and returns a boolean value
- **Method**: `boolean test(T t)`
- **Use Case**: Filtering and conditional logic

```java
Predicate<Integer> isEven = num -> num % 2 == 0;
boolean result = isEven.test(4); // Returns true
```

##### 3. Consumer<T>
- **Purpose**: Takes an input of type T but returns no value (void)
- **Method**: `void accept(T t)`
- **Use Case**: Operations that consume data without returning results

```java
Consumer<String> printer = message -> System.out.println(message);
printer.accept("Hello World"); // Prints: Hello World
```

##### 4. Supplier<T>
- **Purpose**: Takes no input but returns a value of type T
- **Method**: `T get()`
- **Use Case**: Lazy evaluation and factory methods

```java
Supplier<Double> randomValue = () -> Math.random();
Double value = randomValue.get(); // Returns a random double
```

#### Advanced Functional Interfaces
- **BiFunction<T, U, R>**: Takes two inputs and returns one output
- **BiPredicate<T, U>**: Takes two inputs and returns boolean
- **BiConsumer<T, U>**: Takes two inputs and returns void
- **UnaryOperator<T>**: Special case of Function where input and output types are the same
- **BinaryOperator<T>**: Special case of BiFunction where both inputs and output are of the same type

### Method References
Method references provide a way to refer to methods without executing them, offering a more readable alternative to lambda expressions when the lambda simply calls an existing method.

#### Syntax and Types
```java
ClassName::methodName
```

#### Four Types of Method References:

##### 1. Static Method References
```java
// Lambda expression
Function<String, Integer> parseInt1 = str -> Integer.parseInt(str);

// Method reference
Function<String, Integer> parseInt2 = Integer::parseInt;
```

##### 2. Instance Method References of Particular Objects
```java
String message = "Hello World";

// Lambda expression
Supplier<String> supplier1 = () -> message.toUpperCase();

// Method reference
Supplier<String> supplier2 = message::toUpperCase;
```

##### 3. Instance Method References of Arbitrary Objects
```java
// Lambda expression
Function<String, String> toUpper1 = str -> str.toUpperCase();

// Method reference
Function<String, String> toUpper2 = String::toUpperCase;
```

##### 4. Constructor References
```java
// Lambda expression
Supplier<ArrayList<String>> listSupplier1 = () -> new ArrayList<>();

// Constructor reference
Supplier<ArrayList<String>> listSupplier2 = ArrayList::new;
```

#### Benefits
- **Improved Readability**: More concise than equivalent lambda expressions
- **Reusability**: Promotes code reuse by referencing existing methods
- **Performance**: Potential performance benefits due to method handle optimizations

### Constructor References
Constructor references are a special form of method references that refer to constructors.

#### Syntax
```java
ClassName::new
```

#### Examples
```java
// For classes with default constructor
Supplier<Student> studentSupplier = Student::new;
Student student = studentSupplier.get();

// For classes with parameterized constructor
Function<String, Student> studentFunction = Student::new;
Student namedStudent = studentFunction.apply("John");

// For arrays
Function<Integer, String[]> arrayCreator = String[]::new;
String[] stringArray = arrayCreator.apply(10); // Creates array of size 10
```

### Lambda Variables and Restrictions
Lambda expressions have specific rules regarding variable usage that ensure thread safety and predictable behavior.

#### Local Variable Restrictions

##### 1. Variable Name Conflicts
- **Rule**: Cannot use the same local variable name as a lambda parameter or inside the lambda body
- **Reason**: Prevents variable shadowing and maintains clarity

```java
// Invalid - variable shadowing
String message = "Hello";
Consumer<String> consumer = message -> System.out.println(message); // Compilation error

// Valid
String message = "Hello";
Consumer<String> consumer = msg -> System.out.println(msg);
```

##### 2. Effectively Final Variables
- **Rule**: Cannot reassign new values to local variables inside the lambda body
- **Reason**: Local variables used in lambdas must be effectively final

```java
int counter = 0;
// Invalid - modifying local variable
Runnable increment = () -> counter++; // Compilation error

// Valid - using instance variables or atomic references
AtomicInteger atomicCounter = new AtomicInteger(0);
Runnable validIncrement = () -> atomicCounter.incrementAndGet();
```

#### Why These Restrictions Exist
1. **Thread Safety**: Lambdas might be executed in different threads
2. **Variable Capture**: Local variables are captured by value, not reference
3. **Immutability**: Promotes functional programming principles
4. **Predictability**: Ensures consistent behavior across different execution contexts

### Streams API
The Streams API is one of the most significant features introduced in Java 8, providing a functional approach to processing collections of data.

#### What are Streams?
- A Stream is a sequence of elements that can be created from collections, arrays, or other data sources
- Streams enable functional-style operations on collections of data
- Stream operations can be performed sequentially or in parallel
- Streams are not data structures; they don't store elements but rather convey elements from a source through a pipeline of computational operations

#### Creating Streams
```java
// From collections
List<String> names = Arrays.asList("Avinash", "Anil", "Abhishek");
Stream<String> stream1 = names.stream();

// From arrays
String[] array = {"A", "B", "C"};
Stream<String> stream2 = Arrays.stream(array);

// From static methods
Stream<Integer> stream3 = Stream.of(1, 2, 3, 4, 5);
Stream<Integer> stream4 = Stream.iterate(0, n -> n + 2).limit(10); // Even numbers
```

#### Stream Pipeline Structure
A typical stream pipeline consists of:
1. **Source**: Where the stream originates (collection, array, etc.)
2. **Intermediate Operations**: Transform the stream (filter, map, sorted, etc.)
3. **Terminal Operation**: Produces a result or side effect (collect, forEach, reduce, etc.)

```java
Map<String, List<String>> result = StudentDatabase.getStudents()
    .stream()                           // Source
    .filter(student -> student.getGpa() > 3.0)  // Intermediate operation
    .filter(student -> student.getGender().equals("male"))  // Intermediate operation
    .collect(Collectors.toMap(          // Terminal operation
        Student::getName, 
        Student::getActivities
    ));
```

#### Key Characteristics
- **Lazy Evaluation**: Intermediate operations are not executed until a terminal operation is invoked
- **Pipeline Processing**: Operations are chained together to form a pipeline
- **Internal Iteration**: The stream handles iteration internally, unlike traditional loops
- **Immutable**: Original data source remains unchanged

#### Stream vs Collection Processing
**Without Streams (Imperative Style):**
```java
List<Student> students = StudentDatabase.getStudents();
List<String> maleStudentNames = new ArrayList<>();
for (Student student : students) {
    if (student.getGpa() > 3.0 && student.getGender().equals("male")) {
        maleStudentNames.add(student.getName());
    }
}
```

**With Streams (Functional Style):**
```java
List<String> maleStudentNames = StudentDatabase.getStudents()
    .stream()
    .filter(student -> student.getGpa() > 3.0)
    .filter(student -> student.getGender().equals("male"))
    .map(Student::getName)
    .collect(Collectors.toList());
```

### Collections vs Streams: Fundamental Differences

Understanding the differences between Collections and Streams is crucial for effective Java 8+ programming.

#### Mutability
**Collections:**
- **Mutable**: Elements can be added, removed, or modified
- **Direct Manipulation**: Operations directly affect the collection
```java
List<String> names = new ArrayList<>();
names.add("Avinash");     // Modifies the collection
names.remove("Avinash");  // Modifies the collection
```

**Streams:**
- **Immutable**: Cannot add or remove elements from the original source
- **Non-Destructive**: Operations create new streams without modifying the source
```java
List<String> names = Arrays.asList("Avinash", "Anil", "Abhishek");
List<String> filtered = names.stream()
    .filter(name -> name.startsWith("A"))
    .collect(Collectors.toList());
// Original 'names' list remains unchanged
```

#### Data Access Patterns
**Collections:**
- **Random Access**: Elements can be accessed in any order using indices or iterators
- **Multiple Access**: Can iterate multiple times
```java
List<String> names = Arrays.asList("A", "B", "C");
String first = names.get(0);        // Random access
String last = names.get(names.size() - 1);  // Random access
```

**Streams:**
- **Sequential Access**: Elements are processed in a defined sequence
- **Single Use**: Can only be traversed once (consumed after terminal operation)
```java
Stream<String> stream = names.stream();
stream.forEach(System.out::println);  // First traversal
// stream.count();  // IllegalStateException - stream already consumed
```

#### Construction Strategy
**Collections:**
- **Eager Construction**: All elements are computed and stored immediately
- **Memory Intensive**: Requires memory for all elements upfront
```java
List<Integer> numbers = new ArrayList<>();
for (int i = 0; i < 1000000; i++) {
    numbers.add(i);  // All elements stored in memory
}
```

**Streams:**
- **Lazy Construction**: Elements are computed on-demand
- **Memory Efficient**: Only processes elements when needed
```java
Stream<Integer> infiniteStream = Stream.iterate(0, n -> n + 1)
    .limit(1000000);  // No computation until terminal operation
```

#### Iteration Mechanism
**Collections (External Iteration):**
- **Client-Controlled**: Developer controls the iteration logic
- **Imperative Style**: Explicit loops and conditionals
```java
List<String> names = Arrays.asList("Avinash", "Anil", "Abhishek");
for (String name : names) {  // External iteration
    if (name.startsWith("A")) {
        System.out.println(name.toUpperCase());
    }
}
```

**Streams (Internal Iteration):**
- **Library-Controlled**: Stream API controls the iteration
- **Declarative Style**: Focus on what to do, not how to do it
```java
names.stream()  // Internal iteration
    .filter(name -> name.startsWith("A"))
    .map(String::toUpperCase)
    .forEach(System.out::println);
```

#### Performance Characteristics
**Collections:**
- **Immediate Processing**: Operations execute immediately
- **Predictable Memory Usage**: Memory requirements are known upfront
- **Better for Small Datasets**: Lower overhead for simple operations

**Streams:**
- **Optimized Pipeline**: Operations can be fused and optimized
- **Parallel Processing**: Easy parallelization with `.parallelStream()`
- **Better for Large Datasets**: Lazy evaluation and potential optimizations

#### When to Use What?
**Use Collections When:**
- You need to store and retrieve data
- Multiple iterations over the same data
- Random access to elements is required
- Small datasets with simple operations

**Use Streams When:**
- Processing data with multiple transformations
- One-time data processing
- Large datasets that benefit from lazy evaluation
- Functional programming style is preferred
- Parallel processing is needed

### How Streams Process Elements: Understanding the Pipeline

Streams process elements in a specific manner that's different from traditional collection processing. Understanding this mechanism is crucial for writing efficient stream operations.

#### Element-by-Element Processing
Unlike collections that process all elements through one operation before moving to the next, streams process each element through the entire pipeline before moving to the next element.

#### Processing Flow Example
Let's trace through a detailed example to understand how streams work:

```java
List<Student> students = StudentDatabase.getStudents();
students.stream()
    .peek(s -> System.out.println("Before filter: " + s.getName()))
    .filter(s -> s.getGpa() > 3.0)
    .peek(s -> System.out.println("After 1st filter: " + s.getName()))
    .filter(s -> s.getGender().equals("male"))
    .peek(s -> System.out.println("After 2nd filter: " + s.getName()))
    .collect(Collectors.toList());
```

**Processing Flow:**

**Element 1: Adam (GPA: 3.6, Gender: male)**
```
Before filter: Adam
After 1st filter: Adam (passes GPA > 3.0)
After 2nd filter: Adam (passes gender = male)
```

**Element 2: Jenny (GPA: 3.8, Gender: female)**
```
Before filter: Jenny
After 1st filter: Jenny (passes GPA > 3.0)
// Does not pass gender filter, so no "After 2nd filter" log
```

**Element 3: Emily (GPA: 4.0, Gender: female)**
```
Before filter: Emily
After 1st filter: Emily (passes GPA > 3.0)
// Does not pass gender filter, so no "After 2nd filter" log
```

#### Key Insights from Stream Processing

1. **Sequential Element Processing**: Each element goes through the complete pipeline before the next element is processed
2. **Short-Circuit Evaluation**: If an element fails a filter, subsequent operations in the pipeline are skipped for that element
3. **Lazy Evaluation**: Operations are only performed when a terminal operation is called
4. **Pipeline Optimization**: The stream API can optimize the pipeline for better performance

#### Pipeline Efficiency
```java
// Efficient: Filter operations are performed first to reduce data
students.stream()
    .filter(s -> s.getGpa() > 3.0)        // Reduces dataset first
    .filter(s -> s.getGender().equals("male"))  // Further reduces dataset
    .map(Student::getName)                 // Transform only remaining elements
    .collect(Collectors.toList());

// Less efficient: Expensive operations performed on all elements
students.stream()
    .map(s -> expensiveTransformation(s))  // Performed on all elements
    .filter(s -> s.getGpa() > 3.0)        // Filter after expensive operation
    .collect(Collectors.toList());
```

#### Stream Processing vs Traditional Processing

**Traditional Collection Processing:**
```java
List<Student> filtered1 = new ArrayList<>();
for (Student s : students) {
    if (s.getGpa() > 3.0) {
        filtered1.add(s);
    }
}

List<Student> filtered2 = new ArrayList<>();
for (Student s : filtered1) {
    if (s.getGender().equals("male")) {
        filtered2.add(s);
    }
}

List<String> names = new ArrayList<>();
for (Student s : filtered2) {
    names.add(s.getName());
}
```

**Stream Processing:**
```java
List<String> names = students.stream()
    .filter(s -> s.getGpa() > 3.0)
    .filter(s -> s.getGender().equals("male"))
    .map(Student::getName)
    .collect(Collectors.toList());
```

#### Benefits of Stream Processing Model
1. **Memory Efficient**: No intermediate collections are created
2. **Optimizable**: JVM can optimize the entire pipeline
3. **Readable**: Declarative style is easier to understand
4. **Parallelizable**: Easy to convert to parallel processing with `.parallelStream()`

### filter() in Stream Operations

The `filter()` method is one of the most fundamental intermediate operations in the Stream API, used to selectively include elements based on specific criteria.

#### Method Signature and Purpose
- **Method Signature**: `Stream<T> filter(Predicate<? super T> predicate)`
- **Purpose**: Accepts a Predicate (a function that takes input and returns boolean) to test each element
- **Returns**: A new stream containing only elements that match the given condition

#### How filter() Works
```java
// Basic filtering
List<Integer> numbers = Arrays.asList(1, 2, 3, 4, 5, 6, 7, 8, 9, 10);
List<Integer> evenNumbers = numbers.stream()
    .filter(n -> n % 2 == 0)  // Predicate: n % 2 == 0
    .collect(Collectors.toList());
// Result: [2, 4, 6, 8, 10]
```

#### Advanced Filtering Examples

##### Complex Object Filtering
```java
List<Student> students = StudentDatabase.getStudents();

// Single condition
List<Student> highPerformers = students.stream()
    .filter(student -> student.getGpa() > 3.5)
    .collect(Collectors.toList());

// Multiple conditions using method chaining
List<Student> maleHighPerformers = students.stream()
    .filter(student -> student.getGpa() > 3.5)
    .filter(student -> student.getGender().equals("male"))
    .collect(Collectors.toList());

// Complex condition using logical operators
List<Student> eligible = students.stream()
    .filter(student -> student.getGpa() > 3.0 && 
                      student.getGradeLevel() >= 3 &&
                      student.getActivities().size() > 2)
    .collect(Collectors.toList());
```

##### Using Predicate Variables
```java
// Reusable predicates
Predicate<Student> highGpa = student -> student.getGpa() > 3.5;
Predicate<Student> isMale = student -> student.getGender().equals("male");
Predicate<Student> isActive = student -> student.getActivities().size() > 2;

// Combining predicates
List<Student> result = students.stream()
    .filter(highGpa.and(isMale).or(isActive))
    .collect(Collectors.toList());
```

#### Performance Considerations
1. **Early Filtering**: Place filter operations early in the pipeline to reduce downstream processing
2. **Predicate Complexity**: Simple predicates are more efficient than complex ones
3. **Short-Circuit Evaluation**: Combine with limit() for early termination when needed

```java
// Efficient: Filter first, then transform
students.stream()
    .filter(student -> student.getGpa() > 3.0)  // Reduce dataset early
    .map(Student::getName)                      // Transform fewer elements
    .collect(Collectors.toList());

// Less efficient: Transform first, then filter
students.stream()
    .map(Student::getName)                      // Transform all elements
    .filter(name -> name.startsWith("A"))       // Filter after transformation
    .collect(Collectors.toList());
```

### map() in Stream Operations

The `map()` method is a crucial intermediate operation that transforms each element of the stream from one type to another using a provided function.

#### Method Signature and Purpose
- **Method Signature**: `<R> Stream<R> map(Function<? super T, ? extends R> mapper)`
- **Purpose**: Converts/transforms each element in the stream from type T to type R
- **Returns**: A new stream containing the transformed elements
- **One-to-One Mapping**: Each input element produces exactly one output element

#### Basic Transformation Examples

##### Simple Type Transformations
```java
// String to Integer transformation
List<String> numbers = Arrays.asList("1", "2", "3", "4", "5");
List<Integer> integers = numbers.stream()
    .map(Integer::parseInt)  // Method reference
    .collect(Collectors.toList());
// Result: [1, 2, 3, 4, 5]

// String manipulation
List<String> names = Arrays.asList("john", "jane", "bob");
List<String> upperNames = names.stream()
    .map(String::toUpperCase)
    .collect(Collectors.toList());
// Result: ["JOHN", "JANE", "BOB"]
```

##### Object Property Extraction
```java
List<Student> students = StudentDatabase.getStudents();

// Extract single property
List<String> studentNames = students.stream()
    .map(Student::getName)
    .collect(Collectors.toList());

// Extract multiple properties into new objects
List<StudentSummary> summaries = students.stream()
    .map(student -> new StudentSummary(
        student.getName(),
        student.getGpa(),
        student.getGradeLevel()
    ))
    .collect(Collectors.toList());
```

#### Advanced Mapping Techniques

##### Chaining Transformations
```java
List<Student> students = StudentDatabase.getStudents();

// Multiple transformations in sequence
List<String> processedNames = students.stream()
    .filter(student -> student.getGpa() > 3.0)
    .map(Student::getName)           // Extract name
    .map(String::toUpperCase)        // Convert to uppercase
    .map(name -> "Mr./Ms. " + name)  // Add prefix
    .collect(Collectors.toList());
```

##### Complex Object Transformations
```java
// Transform to DTO objects
List<StudentDTO> dtos = students.stream()
    .map(student -> {
        StudentDTO dto = new StudentDTO();
        dto.setFullName(student.getName().toUpperCase());
        dto.setGrade(student.getGradeLevel());
        dto.setPerformanceLevel(student.getGpa() > 3.5 ? "High" : "Average");
        dto.setActivityCount(student.getActivities().size());
        return dto;
    })
    .collect(Collectors.toList());
```

##### Mathematical Transformations
```java
List<Integer> numbers = Arrays.asList(1, 2, 3, 4, 5);

// Mathematical operations
List<Integer> squared = numbers.stream()
    .map(n -> n * n)
    .collect(Collectors.toList());
// Result: [1, 4, 9, 16, 25]

// Complex calculations
List<Double> calculations = numbers.stream()
    .map(n -> Math.pow(n, 2) + Math.sqrt(n))
    .collect(Collectors.toList());
```

#### Performance and Best Practices

##### Function Reusability
```java
// Define reusable functions
Function<String, String> toUpperCase = String::toUpperCase;
Function<Student, String> extractName = Student::getName;
Function<String, String> addPrefix = name -> "Student: " + name;

// Use in pipeline
List<String> formattedNames = students.stream()
    .map(extractName)
    .map(toUpperCase)
    .map(addPrefix)
    .collect(Collectors.toList());
```

##### Null Safety
```java
// Handle potential null values
List<String> safeNames = students.stream()
    .map(student -> Optional.ofNullable(student.getName())
                           .orElse("Unknown"))
    .collect(Collectors.toList());

// Using map with Optional
List<Integer> nameLengths = students.stream()
    .map(Student::getName)
    .filter(Objects::nonNull)
    .map(String::length)
    .collect(Collectors.toList());
```

#### Common Use Cases
1. **Data Type Conversion**: Converting between primitive and object types
2. **Property Extraction**: Getting specific fields from complex objects
3. **Data Transformation**: Modifying or formatting data
4. **DTO Mapping**: Converting domain objects to data transfer objects
5. **Calculation**: Performing mathematical operations on numeric data

### flatMap() in Stream Operations

The `flatMap()` method is a powerful intermediate operation that not only transforms elements but also flattens nested structures into a single stream.

#### Method Signature and Purpose
- **Method Signature**: `<R> Stream<R> flatMap(Function<? super T, ? extends Stream<? extends R>> mapper)`
- **Purpose**: Transforms each element into a stream, then flattens all resulting streams into a single stream
- **Returns**: A flattened stream containing all elements from the individual streams
- **One-to-Many Mapping**: Each input element can produce zero, one, or multiple output elements

#### Understanding the Difference: map() vs flatMap()

##### map() - One-to-One Transformation
```java
List<String> words = Arrays.asList("hello", "world");
List<String[]> result = words.stream()
    .map(word -> word.split(""))  // Each word becomes a String[]
    .collect(Collectors.toList());
// Result: [["h","e","l","l","o"], ["w","o","r","l","d"]] - List of arrays
```

##### flatMap() - One-to-Many with Flattening
```java
List<String> words = Arrays.asList("hello", "world");
List<String> result = words.stream()
    .flatMap(word -> Arrays.stream(word.split("")))  // Flattens arrays into single stream
    .collect(Collectors.toList());
// Result: ["h","e","l","l","o","w","o","r","l","d"] - Flat list
```

#### Practical Examples

##### Flattening Collections
```java
List<Student> students = StudentDatabase.getStudents();

// Extract all activities from all students
List<String> allActivities = students.stream()
    .flatMap(student -> student.getActivities().stream())
    .distinct()  // Remove duplicates
    .collect(Collectors.toList());

// Without flatMap (more verbose)
List<String> activitiesVerbose = new ArrayList<>();
for (Student student : students) {
    activitiesVerbose.addAll(student.getActivities());
}
```

##### Working with Nested Lists
```java
List<List<Integer>> nestedNumbers = Arrays.asList(
    Arrays.asList(1, 2, 3),
    Arrays.asList(4, 5, 6),
    Arrays.asList(7, 8, 9)
);

// Flatten to single list
List<Integer> flatNumbers = nestedNumbers.stream()
    .flatMap(List::stream)
    .collect(Collectors.toList());
// Result: [1, 2, 3, 4, 5, 6, 7, 8, 9]

// Flatten and transform
List<Integer> squaredNumbers = nestedNumbers.stream()
    .flatMap(List::stream)
    .map(n -> n * n)
    .collect(Collectors.toList());
// Result: [1, 4, 9, 16, 25, 36, 49, 64, 81]
```

#### Advanced flatMap() Use Cases

##### File Processing
```java
// Read all lines from multiple files
List<Path> files = Arrays.asList(
    Paths.get("file1.txt"),
    Paths.get("file2.txt"),
    Paths.get("file3.txt")
);

List<String> allLines = files.stream()
    .flatMap(path -> {
        try {
            return Files.lines(path);
        } catch (IOException e) {
            return Stream.empty();  // Handle errors gracefully
        }
    })
    .collect(Collectors.toList());
```

##### Optional Handling
```java
List<Student> students = StudentDatabase.getStudents();

// Extract email addresses (assuming getEmail() returns Optional<String>)
List<String> emails = students.stream()
    .flatMap(student -> student.getEmail()
                              .map(Stream::of)
                              .orElse(Stream.empty()))
    .collect(Collectors.toList());

// More concise with flatMap and Optional
List<String> emailsConcise = students.stream()
    .map(Student::getEmail)
    .flatMap(Optional::stream)  // Java 9+ feature
    .collect(Collectors.toList());
```

##### Complex Data Structures
```java
// Flatten hierarchical structures
public class Department {
    private String name;
    private List<Employee> employees;
    // getters...
}

List<Department> departments = getDepartments();

// Get all employees from all departments
List<Employee> allEmployees = departments.stream()
    .flatMap(dept -> dept.getEmployees().stream())
    .collect(Collectors.toList());

// Get all skills from all employees
List<String> allSkills = departments.stream()
    .flatMap(dept -> dept.getEmployees().stream())
    .flatMap(emp -> emp.getSkills().stream())
    .distinct()
    .collect(Collectors.toList());
```

#### Performance Considerations

##### Memory Efficiency
```java
// Efficient: Process one element at a time
students.stream()
    .flatMap(student -> student.getActivities().stream())
    .filter(activity -> activity.contains("sports"))
    .limit(10)  // Short-circuit evaluation
    .collect(Collectors.toList());

// Less efficient: Create intermediate collections
List<String> allActivities = students.stream()
    .flatMap(student -> student.getActivities().stream())
    .collect(Collectors.toList());  // Large intermediate collection
List<String> filtered = allActivities.stream()
    .filter(activity -> activity.contains("sports"))
    .collect(Collectors.toList());
```

##### Error Handling in flatMap()
```java
// Safe flatMap with error handling
public Stream<String> safeReadLines(Path path) {
    try {
        return Files.lines(path);
    } catch (IOException e) {
        System.err.println("Error reading file: " + path + " - " + e.getMessage());
        return Stream.empty();
    }
}

List<String> allLines = files.stream()
    .flatMap(this::safeReadLines)
    .collect(Collectors.toList());
```

#### Common Patterns and Best Practices

1. **Null Safety**: Always handle potential null collections
```java
students.stream()
    .flatMap(student -> Optional.ofNullable(student.getActivities())
                               .orElse(Collections.emptyList())
                               .stream())
    .collect(Collectors.toList());
```

2. **Combining with Other Operations**: Chain with filter, map, etc.
```java
students.stream()
    .flatMap(student -> student.getActivities().stream())
    .filter(activity -> activity.toLowerCase().contains("music"))
    .map(String::toUpperCase)
    .distinct()
    .sorted()
    .collect(Collectors.toList());
```

3. **Empty Stream Handling**: Return empty streams for error cases
```java
// Good practice: Return empty stream instead of null
public Stream<String> getActivitiesStream(Student student) {
    return student.getActivities() != null ? 
           student.getActivities().stream() : 
           Stream.empty();
}
```

### distinct() in Stream Operations

The `distinct()` method is an intermediate operation that returns a stream containing only unique elements, removing duplicates based on the `equals()` method.

#### Method Signature and Purpose
- **Method Signature**: `Stream<T> distinct()`
- **Purpose**: Returns a stream with duplicate elements removed
- **Mechanism**: Uses `equals()` method to determine equality
- **State**: This is a stateful intermediate operation (maintains internal state)

#### Basic Usage Examples

##### Primitive Types
```java
List<Integer> numbers = Arrays.asList(1, 2, 2, 3, 3, 3, 4, 5, 5);
List<Integer> uniqueNumbers = numbers.stream()
    .distinct()
    .collect(Collectors.toList());
// Result: [1, 2, 3, 4, 5]

List<String> words = Arrays.asList("apple", "banana", "apple", "cherry", "banana");
List<String> uniqueWords = words.stream()
    .distinct()
    .collect(Collectors.toList());
// Result: ["apple", "banana", "cherry"]
```

##### Complex Objects
```java
List<Student> students = StudentDatabase.getStudents();

// Remove duplicate students (based on equals() implementation)
List<Student> uniqueStudents = students.stream()
    .distinct()
    .collect(Collectors.toList());

// Get unique activities from all students
List<String> uniqueActivities = students.stream()
    .flatMap(student -> student.getActivities().stream())
    .distinct()
    .sorted()
    .collect(Collectors.toList());
```

#### Advanced Distinct Operations

##### Distinct by Custom Criteria
Since `distinct()` relies on `equals()`, for custom distinctness criteria, you can use different approaches:

```java
// Method 1: Using map to extract key, then distinct
List<String> uniqueNamesByFirstLetter = students.stream()
    .map(Student::getName)
    .map(name -> name.substring(0, 1).toUpperCase())
    .distinct()
    .collect(Collectors.toList());

// Method 2: Distinct by specific property using Collectors
Map<String, Student> uniqueByGrade = students.stream()
    .collect(Collectors.toMap(
        student -> String.valueOf(student.getGradeLevel()),
        student -> student,
        (existing, replacement) -> existing  // Keep first occurrence
    ));

// Method 3: Using custom collector for distinct by property
public static <T> Predicate<T> distinctByKey(Function<? super T, ?> keyExtractor) {
    Set<Object> seen = ConcurrentHashMap.newKeySet();
    return t -> seen.add(keyExtractor.apply(t));
}

List<Student> uniqueByName = students.stream()
    .filter(distinctByKey(Student::getName))
    .collect(Collectors.toList());
```

#### Performance Characteristics

##### Memory Usage
```java
// distinct() maintains a HashSet internally to track seen elements
// Memory usage is O(n) where n is the number of unique elements

// For large datasets, consider memory implications
Stream<String> largeStream = Files.lines(Paths.get("large-file.txt"));
List<String> uniqueLines = largeStream
    .distinct()  // May consume significant memory
    .collect(Collectors.toList());
```

##### Order Preservation
```java
// distinct() preserves encounter order
List<String> orderedInput = Arrays.asList("c", "a", "b", "a", "c", "d");
List<String> result = orderedInput.stream()
    .distinct()
    .collect(Collectors.toList());
// Result: ["c", "a", "b", "d"] - First occurrence order preserved
```

#### Practical Use Cases

##### Data Cleaning
```java
List<String> emails = Arrays.asList(
    "john@example.com",
    "JOHN@EXAMPLE.COM",
    "jane@example.com",
    "john@example.com"
);

// Case-insensitive distinct
List<String> uniqueEmails = emails.stream()
    .map(String::toLowerCase)
    .distinct()
    .collect(Collectors.toList());
// Result: ["john@example.com", "jane@example.com"]
```

##### Aggregating Unique Values
```java
List<Student> students = StudentDatabase.getStudents();

// Count unique grade levels
long uniqueGradeLevels = students.stream()
    .mapToInt(Student::getGradeLevel)
    .distinct()
    .count();

// Get unique GPAs rounded to one decimal
List<Double> uniqueGPAs = students.stream()
    .map(Student::getGpa)
    .map(gpa -> Math.round(gpa * 10) / 10.0)
    .distinct()
    .sorted()
    .collect(Collectors.toList());
```

#### Best Practices

##### Combining with Other Operations
```java
// Efficient pipeline: filter before distinct to reduce processing
List<String> result = students.stream()
    .filter(student -> student.getGpa() > 3.0)  // Reduce dataset first
    .map(Student::getName)
    .distinct()
    .sorted()
    .collect(Collectors.toList());
```

##### Null Handling
```java
List<String> names = Arrays.asList("John", null, "Jane", "John", null, "Bob");

// Handle nulls explicitly
List<String> uniqueNonNullNames = names.stream()
    .filter(Objects::nonNull)
    .distinct()
    .collect(Collectors.toList());
// Result: ["John", "Jane", "Bob"]

// Or include nulls
List<String> uniqueWithNulls = names.stream()
    .distinct()
    .collect(Collectors.toList());
// Result: ["John", null, "Jane", "Bob"]
```

#### When to Use distinct()
1. **Removing Duplicates**: When you need to eliminate duplicate elements
2. **Data Analysis**: Finding unique values in datasets
3. **Validation**: Ensuring uniqueness constraints
4. **Aggregation**: Before performing operations that should only consider unique values

### sorted()
- Sorts the elements of the stream in natural order or according to a provided comparator.
- You can sort by any property of the object using a comparator, for example:  
  `sorted(Comparator.comparing(Student::getName))` sorts the stream of `Student` objects by their names.
- Sorting is an intermediate operation that returns a new stream with elements ordered based on the specified criteria.

### count()
- Returns a `long` representing the total number of elements present in the stream.
- It is a terminal operation that triggers the processing of the stream pipeline and produces the count as the result.

### collect() in stream
- A terminal operation used to transform the elements of the stream into a different form such as a collection (e.g., List, Set), a Map, or a summary result.
- It takes a `Collector` which defines how to accumulate the elements, combine partial results, and finish the collection process.
- This method is essential for retrieving the processed data from the stream pipeline into a usable container.

### reduce() in stream
- A terminal operation used to combine the elements of the stream into a single result by repeatedly applying a binary operator.
- It can take either one or two parameters:
  - An optional identity (initial) value that serves as the starting point for the reduction.
  - A binary operator function that specifies how two elements are combined.
- Example:
  ```java
  List<Integer> integerList = List.of(2, 4, 7, 3);
  Integer product = integerList.stream()
      .reduce(1, (a, b) -> a * b);
  // Explanation:
  // Iteration 1: a=1 (identity), b=2 → result=2
  // Iteration 2: a=2, b=4 → result=8
  // Iteration 3: a=8, b=7 → result=56
  // Iteration 4: a=56, b=3 → result=168
  // Final product = 168
  ```
### limit() and skip() in Stream Operations

The `limit()` and `skip()` methods are intermediate operations used for controlling the subset of elements processed in a stream, particularly useful for pagination and data sampling.

#### Method Signatures and Purpose
- **limit(n)**: `Stream<T> limit(long maxSize)` - Returns a stream consisting of the first n elements
- **skip(n)**: `Stream<T> skip(long n)` - Returns a stream that discards the first n elements and processes the remaining elements

#### Basic Usage Examples

##### limit() - Restricting Stream Size
```java
List<Integer> numbers = Arrays.asList(1, 2, 3, 4, 5, 6, 7, 8, 9, 10);

// Get first 5 elements
List<Integer> firstFive = numbers.stream()
    .limit(5)
    .collect(Collectors.toList());
// Result: [1, 2, 3, 4, 5]

// Get top 3 students by GPA
List<Student> topStudents = StudentDatabase.getStudents().stream()
    .sorted(Comparator.comparing(Student::getGpa).reversed())
    .limit(3)
    .collect(Collectors.toList());
```

##### skip() - Skipping Elements
```java
List<Integer> numbers = Arrays.asList(1, 2, 3, 4, 5, 6, 7, 8, 9, 10);

// Skip first 5 elements
List<Integer> afterFive = numbers.stream()
    .skip(5)
    .collect(Collectors.toList());
// Result: [6, 7, 8, 9, 10]

// Skip first 2 students
List<Student> remainingStudents = StudentDatabase.getStudents().stream()
    .skip(2)
    .collect(Collectors.toList());
```

#### Pagination Pattern
```java
// Implement pagination using skip() and limit()
public List<Student> getStudentPage(int pageNumber, int pageSize) {
    return StudentDatabase.getStudents().stream()
        .skip((long) pageNumber * pageSize)  // Skip previous pages
        .limit(pageSize)                      // Take current page
        .collect(Collectors.toList());
}

// Get page 2 with 10 students per page
List<Student> page2 = getStudentPage(1, 10);  // pageNumber is 0-indexed
```

#### Combining limit() and skip()
```java
List<Integer> numbers = Arrays.asList(1, 2, 3, 4, 5, 6, 7, 8, 9, 10);

// Get elements from index 3 to 7
List<Integer> middleElements = numbers.stream()
    .skip(3)   // Skip first 3: [4, 5, 6, 7, 8, 9, 10]
    .limit(4)  // Take next 4: [4, 5, 6, 7]
    .collect(Collectors.toList());
// Result: [4, 5, 6, 7]
```

#### Performance Optimization with limit()

##### Short-Circuit Evaluation
```java
// limit() enables short-circuit evaluation
List<Integer> firstThreeEven = IntStream.range(1, 1000000)
    .filter(n -> n % 2 == 0)
    .limit(3)  // Stops processing after finding 3 elements
    .boxed()
    .collect(Collectors.toList());
// Result: [2, 4, 6] - Doesn't process all 1,000,000 elements

// Without limit(), all elements would be processed
List<Integer> allEven = IntStream.range(1, 1000000)
    .filter(n -> n % 2 == 0)
    .boxed()
    .collect(Collectors.toList());  // Processes all elements
```

#### Practical Use Cases

##### Sampling Data
```java
// Get random sample by shuffling and limiting
List<Student> randomSample = StudentDatabase.getStudents().stream()
    .sorted((s1, s2) -> Math.random() > 0.5 ? 1 : -1)  // Random shuffle
    .limit(5)
    .collect(Collectors.toList());

// Get first N unique activities
List<String> topActivities = StudentDatabase.getStudents().stream()
    .flatMap(student -> student.getActivities().stream())
    .distinct()
    .limit(10)
    .collect(Collectors.toList());
```

##### Data Preview
```java
// Preview first few records
System.out.println("First 5 students:");
StudentDatabase.getStudents().stream()
    .limit(5)
    .forEach(System.out::println);

// Skip header row and get data
List<String> dataRows = Files.lines(Paths.get("data.csv"))
    .skip(1)  // Skip header
    .collect(Collectors.toList());
```

#### Best Practices
1. **Ordering Matters**: Apply `skip()` before `limit()` for pagination
2. **Short-Circuit Optimization**: Use `limit()` early for performance with infinite streams
3. **Edge Cases**: Handle cases where skip exceeds stream size (returns empty stream)

```java
// Handle edge cases
List<Integer> numbers = Arrays.asList(1, 2, 3);

// Skip more than available
List<Integer> empty = numbers.stream()
    .skip(10)  // Skips all elements
    .collect(Collectors.toList());
// Result: [] (empty list)

// Limit more than available
List<Integer> all = numbers.stream()
    .limit(10)  // Returns all 3 elements
    .collect(Collectors.toList());
// Result: [1, 2, 3]
```

### anyMatch(), allMatch(), and noneMatch() in Stream Operations

These are terminal operations that take a predicate and return a boolean result based on the elements of the stream. They provide short-circuit evaluation for efficient processing.

#### Method Signatures
- **anyMatch(Predicate<T> predicate)**: Returns `true` if any element in the stream matches the predicate
- **allMatch(Predicate<T> predicate)**: Returns `true` if all elements in the stream satisfy the predicate
- **noneMatch(Predicate<T> predicate)**: Returns `true` if no elements in the stream match the predicate

#### Basic Usage Examples

##### anyMatch() - At Least One Match
```java
List<Integer> numbers = Arrays.asList(1, 2, 3, 4, 5);

// Check if any number is even
boolean hasEven = numbers.stream()
    .anyMatch(n -> n % 2 == 0);
// Result: true (2 and 4 are even)

// Check if any student has GPA > 3.5
boolean hasHighPerformer = StudentDatabase.getStudents().stream()
    .anyMatch(student -> student.getGpa() > 3.5);
```

##### allMatch() - All Elements Match
```java
List<Integer> numbers = Arrays.asList(2, 4, 6, 8, 10);

// Check if all numbers are even
boolean allEven = numbers.stream()
    .allMatch(n -> n % 2 == 0);
// Result: true

// Check if all students have GPA > 2.0
boolean allPassing = StudentDatabase.getStudents().stream()
    .allMatch(student -> student.getGpa() > 2.0);
```

##### noneMatch() - No Elements Match
```java
List<Integer> numbers = Arrays.asList(1, 3, 5, 7, 9);

// Check if no numbers are even
boolean noEven = numbers.stream()
    .noneMatch(n -> n % 2 == 0);
// Result: true

// Check if no students have failing grades
boolean noFailures = StudentDatabase.getStudents().stream()
    .noneMatch(student -> student.getGpa() < 1.0);
```

#### Short-Circuit Evaluation
These operations stop processing as soon as the result is determined:

```java
// anyMatch stops after finding first match
boolean result = IntStream.range(1, 1000000)
    .anyMatch(n -> n > 100);
// Stops at 101, doesn't check remaining elements

// allMatch stops at first non-match
boolean allPositive = IntStream.range(1, 1000000)
    .allMatch(n -> n > 0);
// Would stop immediately if it finds a non-positive number

// noneMatch stops at first match
boolean noneNegative = IntStream.range(1, 1000000)
    .noneMatch(n -> n < 0);
// Stops if it finds any negative number
```

#### Practical Examples

##### Data Validation
```java
List<Student> students = StudentDatabase.getStudents();

// Validation checks
boolean anyUnderAge = students.stream()
    .anyMatch(s -> s.getAge() < 18);

boolean allHaveEmail = students.stream()
    .allMatch(s -> s.getEmail() != null && !s.getEmail().isEmpty());

boolean noInvalidGpa = students.stream()
    .noneMatch(s -> s.getGpa() < 0.0 || s.getGpa() > 4.0);

if (anyUnderAge) {
    System.out.println("Warning: Some students are under 18");
}
```

##### Complex Predicates
```java
List<Student> students = StudentDatabase.getStudents();

// Check if any student is eligible for scholarship
Predicate<Student> scholarshipEligible = student -> 
    student.getGpa() >= 3.5 && 
    student.getActivities().size() >= 3;

boolean hasScholarshipCandidate = students.stream()
    .anyMatch(scholarshipEligible);

// Check if all students meet minimum requirements
Predicate<Student> meetsMinimum = student -> 
    student.getGpa() >= 2.0 && 
    student.getGradeLevel() >= 1;

boolean allQualified = students.stream()
    .allMatch(meetsMinimum);
```

#### Combining with Other Operations
```java
// Filter first, then check
boolean anyHighPerformerMale = StudentDatabase.getStudents().stream()
    .filter(s -> s.getGender().equals("male"))
    .anyMatch(s -> s.getGpa() > 3.8);

// Check activities
boolean allStudentsActive = StudentDatabase.getStudents().stream()
    .allMatch(s -> s.getActivities().size() > 0);

// Verify no empty names
boolean noEmptyNames = StudentDatabase.getStudents().stream()
    .map(Student::getName)
    .noneMatch(name -> name == null || name.trim().isEmpty());
```

#### Best Practices
1. **Use for Validation**: These methods are ideal for checking conditions across collections
2. **Short-Circuit Benefits**: They stop as soon as the result is known, making them efficient
3. **Readable Code**: More expressive than traditional loops for boolean checks

```java
// Traditional approach (verbose)
boolean hasEven = false;
for (Integer num : numbers) {
    if (num % 2 == 0) {
        hasEven = true;
        break;
    }
}

// Stream approach (concise)
boolean hasEven = numbers.stream().anyMatch(n -> n % 2 == 0);
```

### findFirst() and findAny() in Stream Operations

These are terminal operations used to find elements in a stream. Both return an `Optional` containing the element, if found.

#### Method Signatures and Purpose
- **findFirst()**: `Optional<T> findFirst()` - Returns an Optional containing the first element in the stream
- **findAny()**: `Optional<T> findAny()` - Returns an Optional containing any element from the stream (useful in parallel streams)

#### Key Differences
- **findFirst()**: Deterministic - always returns the first element in encounter order
- **findAny()**: Non-deterministic in parallel streams - may return any element for better performance

#### Basic Usage Examples

##### findFirst() - Get First Element
```java
List<Integer> numbers = Arrays.asList(1, 2, 3, 4, 5);

// Find first element
Optional<Integer> first = numbers.stream()
    .findFirst();
// Result: Optional[1]

// Find first even number
Optional<Integer> firstEven = numbers.stream()
    .filter(n -> n % 2 == 0)
    .findFirst();
// Result: Optional[2]

// Unwrap the Optional
Integer value = firstEven.orElse(-1);  // Returns 2
```

##### findAny() - Get Any Element
```java
List<Integer> numbers = Arrays.asList(1, 2, 3, 4, 5);

// Find any element (sequential stream - usually first)
Optional<Integer> any = numbers.stream()
    .findAny();
// Result: Optional[1] (in sequential stream)

// Find any even number
Optional<Integer> anyEven = numbers.stream()
    .filter(n -> n % 2 == 0)
    .findAny();
// Result: Could be Optional[2] or Optional[4]
```

#### Parallel Stream Performance
```java
List<Integer> largeList = IntStream.range(1, 1000000)
    .boxed()
    .collect(Collectors.toList());

// findAny() is faster in parallel streams
Optional<Integer> anyMatch = largeList.parallelStream()
    .filter(n -> n > 100000)
    .findAny();  // Returns quickly, doesn't wait for first match

// findFirst() maintains order even in parallel
Optional<Integer> firstMatch = largeList.parallelStream()
    .filter(n -> n > 100000)
    .findFirst();  // Returns 100001 (first in order)
```

#### Practical Examples with Students

##### Finding Students
```java
List<Student> students = StudentDatabase.getStudents();

// Find first student with GPA > 3.5
Optional<Student> topStudent = students.stream()
    .filter(s -> s.getGpa() > 3.5)
    .findFirst();

topStudent.ifPresent(s -> 
    System.out.println("Top student: " + s.getName()));

// Find any male student
Optional<Student> anyMale = students.stream()
    .filter(s -> s.getGender().equals("male"))
    .findAny();
```

##### Handling Optional Results
```java
// Method 1: Using ifPresent()
students.stream()
    .filter(s -> s.getGpa() > 3.8)
    .findFirst()
    .ifPresent(s -> System.out.println("Found: " + s.getName()));

// Method 2: Using orElse()
Student student = students.stream()
    .filter(s -> s.getName().equals("John"))
    .findFirst()
    .orElse(new Student("Unknown"));  // Default value

// Method 3: Using orElseGet()
Student student = students.stream()
    .filter(s -> s.getName().equals("John"))
    .findFirst()
    .orElseGet(() -> createDefaultStudent());  // Lazy evaluation

// Method 4: Using orElseThrow()
Student student = students.stream()
    .filter(s -> s.getName().equals("John"))
    .findFirst()
    .orElseThrow(() -> new NoSuchElementException("Student not found"));
```

#### Short-Circuit Evaluation
Both operations are short-circuit operations - they stop processing once an element is found:

```java
// Stops after finding first match
Optional<Integer> result = IntStream.range(1, 1000000)
    .filter(n -> n > 500)
    .findFirst();  // Stops at 501

// Useful for existence checks
boolean exists = students.stream()
    .filter(s -> s.getName().equals("John"))
    .findFirst()
    .isPresent();

// Or more concisely with anyMatch()
boolean exists = students.stream()
    .anyMatch(s -> s.getName().equals("John"));
```

#### Complex Search Scenarios

##### Searching with Multiple Criteria
```java
// Find first student matching complex criteria
Optional<Student> eligible = students.stream()
    .filter(s -> s.getGpa() > 3.5)
    .filter(s -> s.getGradeLevel() >= 3)
    .filter(s -> s.getActivities().size() > 2)
    .findFirst();

// Extract property from found student
String studentName = students.stream()
    .filter(s -> s.getGpa() == 4.0)
    .findFirst()
    .map(Student::getName)
    .orElse("No perfect GPA student");
```

##### Chaining Operations
```java
// Find and transform
String upperName = students.stream()
    .filter(s -> s.getGpa() > 3.5)
    .map(Student::getName)
    .map(String::toUpperCase)
    .findFirst()
    .orElse("NOT FOUND");

// Find nested data
Optional<String> firstActivity = students.stream()
    .filter(s -> !s.getActivities().isEmpty())
    .map(Student::getActivities)
    .findFirst()
    .flatMap(activities -> activities.stream().findFirst());
```

#### Best Practices

##### When to Use findFirst() vs findAny()
```java
// Use findFirst() when:
// 1. Order matters
Optional<String> sortedName = students.stream()
    .map(Student::getName)
    .sorted()
    .findFirst();  // Need the alphabetically first name

// 2. Deterministic behavior is required
Optional<Student> youngest = students.stream()
    .sorted(Comparator.comparing(Student::getAge))
    .findFirst();  // Must be the youngest

// Use findAny() when:
// 1. Working with parallel streams
Optional<Student> anyEligible = students.parallelStream()
    .filter(s -> s.getGpa() > 3.0)
    .findAny();  // Faster in parallel

// 2. Order doesn't matter
Optional<String> anyActivity = students.stream()
    .flatMap(s -> s.getActivities().stream())
    .findAny();  // Any activity is fine
```

##### Empty Stream Handling
```java
List<Student> emptyList = new ArrayList<>();

// Safe handling of empty streams
Optional<Student> result = emptyList.stream()
    .findFirst();
// Result: Optional.empty

// With default value
Student student = emptyList.stream()
    .findFirst()
    .orElse(Student.getDefault());

// Check before using
emptyList.stream()
    .findFirst()
    .ifPresent(s -> processStudent(s));
```

#### Interview Tips
1. **Remember**: Both return `Optional<T>`, never null
2. **Performance**: `findAny()` is faster in parallel streams
3. **Order**: `findFirst()` respects encounter order, `findAny()` doesn't guarantee it
4. **Short-Circuit**: Both stop processing once element is found

### Stream Operations: Stateful vs Stateless

Stream operations can be categorized as stateful or stateless based on whether they need to maintain internal state to process elements.

#### Understanding State in Streams
- **Question**: Do Streams have internal state? **Yes**
- **Question**: Do all Stream operations maintain internal state? **No**

#### Stateless Operations
Operations that process each element independently without needing to know about other elements.

##### Characteristics
- **No Memory**: Don't remember previously processed elements
- **Independent Processing**: Each element is processed in isolation
- **Parallel-Friendly**: Can be easily parallelized
- **Low Memory Overhead**: Don't require additional storage

##### Examples of Stateless Operations
```java
// filter() - stateless
List<Integer> numbers = Arrays.asList(1, 2, 3, 4, 5);
List<Integer> even = numbers.stream()
    .filter(n -> n % 2 == 0)  // Each element checked independently
    .collect(Collectors.toList());

// map() - stateless
List<String> names = students.stream()
    .map(Student::getName)  // Each student mapped independently
    .collect(Collectors.toList());

// flatMap() - stateless
List<String> activities = students.stream()
    .flatMap(s -> s.getActivities().stream())  // Independent flattening
    .collect(Collectors.toList());

// peek() - stateless
students.stream()
    .peek(s -> System.out.println(s.getName()))  // Side effect per element
    .collect(Collectors.toList());
```

#### Stateful Operations
Operations that need to maintain state about previously seen elements or need to see all elements before producing results.

##### Characteristics
- **Memory Required**: Must store information about elements
- **Buffering**: May need to process entire stream before producing output
- **Performance Impact**: Can be expensive for large streams
- **Ordering Dependencies**: May depend on element order

##### Examples of Stateful Operations
```java
// distinct() - stateful (maintains set of seen elements)
List<Integer> numbers = Arrays.asList(1, 2, 2, 3, 3, 4);
List<Integer> unique = numbers.stream()
    .distinct()  // Must remember all seen elements
    .collect(Collectors.toList());
// Internal state: HashSet of seen elements

// sorted() - stateful (must see all elements to sort)
List<Student> sorted = students.stream()
    .sorted(Comparator.comparing(Student::getGpa))  // Needs all elements
    .collect(Collectors.toList());
// Internal state: Buffer containing all elements

// limit() - stateful (counts elements processed)
List<Integer> first5 = numbers.stream()
    .limit(5)  // Must track count
    .collect(Collectors.toList());
// Internal state: Counter

// skip() - stateful (counts elements to skip)
List<Integer> afterFirst3 = numbers.stream()
    .skip(3)  // Must track how many skipped
    .collect(Collectors.toList());
// Internal state: Counter
```

#### Comprehensive Comparison Table

| Operation | Type | Reason |
|-----------|------|--------|
| filter() | Stateless | Processes each element independently |
| map() | Stateless | Transforms each element independently |
| flatMap() | Stateless | Flattens each element independently |
| peek() | Stateless | Performs action on each element independently |
| forEach() | Stateless | Processes each element independently |
| distinct() | Stateful | Must remember all previously seen elements |
| sorted() | Stateful | Must buffer all elements to sort |
| limit() | Stateful | Must count elements processed |
| skip() | Stateful | Must count elements to skip |
| reduce() | Stateful | Accumulates result across elements |
| collect() | Stateful | Accumulates elements into collection |

#### Performance Implications

##### Stateless Operations - Efficient Parallelization
```java
// Easily parallelizable - no coordination needed
List<String> upperNames = students.parallelStream()
    .filter(s -> s.getGpa() > 3.0)  // Stateless
    .map(Student::getName)           // Stateless
    .map(String::toUpperCase)        // Stateless
    .collect(Collectors.toList());
```

##### Stateful Operations - Coordination Required
```java
// Requires coordination in parallel streams
List<Student> topStudents = students.parallelStream()
    .sorted(Comparator.comparing(Student::getGpa))  // Stateful - expensive
    .limit(10)                                       // Stateful
    .collect(Collectors.toList());
// Parallel threads must coordinate to merge sorted results
```

#### Memory Considerations

##### Stateless - Constant Memory
```java
// Memory usage independent of stream size
long count = IntStream.range(1, 1000000)
    .filter(n -> n % 2 == 0)  // Stateless - O(1) memory
    .map(n -> n * 2)          // Stateless - O(1) memory
    .count();
```

##### Stateful - Linear Memory
```java
// Memory grows with stream size
List<Integer> sorted = IntStream.range(1, 1000000)
    .boxed()
    .sorted()  // Stateful - O(n) memory, must store all elements
    .collect(Collectors.toList());

List<Integer> unique = IntStream.range(1, 1000000)
    .boxed()
    .distinct()  // Stateful - O(k) memory, where k = unique elements
    .collect(Collectors.toList());
```

#### Best Practices

##### Optimize Pipeline Order
```java
// Good: Stateless operations first, reduce data before stateful ops
List<Student> result = students.stream()
    .filter(s -> s.getGpa() > 3.0)     // Stateless - reduce early
    .filter(s -> s.getGradeLevel() > 2) // Stateless - reduce more
    .sorted(Comparator.comparing(Student::getName))  // Stateful - fewer elements
    .limit(10)                          // Stateful
    .collect(Collectors.toList());

// Bad: Stateful operations on full dataset
List<Student> result = students.stream()
    .sorted(Comparator.comparing(Student::getName))  // Stateful - all elements
    .filter(s -> s.getGpa() > 3.0)     // After expensive sort
    .limit(10)
    .collect(Collectors.toList());
```

##### Use Short-Circuit Operations
```java
// Combine with short-circuit operations to limit stateful work
Optional<Student> first = students.stream()
    .filter(s -> s.getGpa() > 3.5)  // Stateless
    .findFirst();  // Short-circuit - stops early

// Instead of
List<Student> all = students.stream()
    .filter(s -> s.getGpa() > 3.5)
    .collect(Collectors.toList());
Student first = all.get(0);  // Processes entire stream
```

#### Interview Quick Reference
**Stateless Operations**: filter, map, flatMap, peek
**Stateful Operations**: distinct, sorted, limit, skip
**Key Difference**: Stateful operations need to remember or see multiple elements
**Performance**: Stateless operations are more efficient, especially in parallel streams
 ## stream api factory method
- of() -> this factory method is used to create a stream of certain values passed to this method.
- iterate(), generate() -> used to create infinite stream
```java
Stream<Integer> integerStream = Stream.of(1,2,3);
Stream.iterate(1,(x)->x*2);
Stream.generate(<supplier>);
```

### Numeric Stream 
- represents the primitive values in the stream.
- IntStream, LongStream, DoubleStream

### IntStream
- IntStream.range(1,50) --> returns IntStream of 49 elements from 1 to 49
- IntStream.rangeClosed(1,50) --> returns IntStream of 50 elements from 1 to 50.

### LongStream
- LongStream.range(1,50) --> returns LongStream of 49 elements from 1 to 49
- LongStream.rangeClosed(1,50) --> returns LongStream of 50 elements from 1 to 50.

### Aggregate Functions
- sum(), max(), min(), average()
- sum() - returns the sum of elements in the stream (returns int/long/double based on stream type).
- max() - returns the maximum element as OptionalInt/OptionalLong/OptionalDouble.
- min() - returns the minimum element as OptionalInt/OptionalLong/OptionalDouble.
- average() - returns the average of elements as OptionalDouble.

### mapToObj(), mapToLong(), mapToDouble()
- mapToObj() - converts IntStream to a Stream of objects.
- mapToLong() - converts IntStream to LongStream.
- mapToDouble() - converts IntStream to DoubleStream.
- Boxing: boxed() - converts a primitive stream (IntStream/LongStream/DoubleStream) to a wrapper stream (Stream<Integer>/Stream<Long>/Stream<Double>).
- Unboxing: mapToInt(), mapToLong(), mapToDouble() - converts a wrapper stream back to a primitive stream.

### Terminal Operations
- Terminal operations collect the data for you.
  - Terminal operation starts the whole stream pipeline.
  - Terminal Operations are - forEach(), min(), max(), collect(), reduce(), count()

### joining()
- joining() is a Collector (used with collect()) that joins the elements of the stream into a single String.
- joining() - joins elements with no delimiter.
- joining(",") - joins elements with the specified delimiter.
- joining(",", "(", ")") - joins elements with delimiter, prefix, and suffix.
### Terminal Operation
- Terminal Operation collects the data for you.
- Terminal Operation starts the whole stream pipeline.
- ex: forEach(), min(), max(), reduce(), count() etc.
- collect() acts as an accumulator and takes input of type Collector.
- Produces the result as per the input passed to the collect() method.  
- joining() method Collector performs the string concatenation on the elements in the stream.
- joining() has three overloaded versions.
- counting() - Collector returns the total number of elements as a result.
- mapping() - Collector applies a transformation function first and then collects the data in a collection.
- maxBy() and minBy() take a Comparator as an input and return Optional as an output.
- maxBy() returns the max element based on the property passed to the Comparator.
- minBy() returns the min element based on the property passed to the Comparator.
- summingInt() - it returns the sum as a result.
- averagingInt() - it returns the average as a result.
- groupingBy() - it is equivalent to the GROUP BY operation in SQL.
- It groups the elements based on a property.
- The output of groupingBy() is a Map<K, V>.
- 3 versions:
- groupingBy(classifier), groupingBy(classifier, downstream), groupingBy(classifier, supplier, downstream).
- partitioningBy() - it is a special case of groupingBy() that accepts a Predicate and returns a Map<Boolean, List<T>>.
- It partitions the stream into two groups based on the Predicate (true/false).

### Parallel Stream
- Splits the source of data into multiple parts.
- Processes them in parallel using the ForkJoin framework.
- Combines the result.
- Two ways to create a parallel stream:
  - parallelStream() - called on a collection directly.
  - parallel() - called on an existing sequential stream.
- Use sequential() to convert a parallel stream back to a sequential stream.
- Not always faster than sequential streams; depends on the data size, operations, and hardware.
- Avoid using parallel streams with stateful operations or shared mutable state.

### Optional
- It is used to handle null values and avoid NullPointerException.
- Introduced in Java 8 in the java.util package.
- Optional.ofNullable() - returns the actual Optional value if the value is not null, and returns Optional.empty() if the value is null.
- Optional.of() - returns the Optional value if the value is not null. If the value is null, it throws NullPointerException. It is used when we are sure the value should not be null.
- Optional.empty() - returns an empty Optional value.
- orElse() - it returns the value if available, or returns the other (default) value.
- orElseGet() - it takes a Supplier. If the value is available then it returns the value, or else it returns the Supplier's value.
- orElseThrow() - it throws the exception if the Optional value is not available.
- isPresent() - it returns true if the Optional value is available.
- ifPresent() - if the Optional value is present then it executes the Consumer.
- map() - transforms the value inside the Optional if present, returns Optional.
- flatMap() - similar to map(), but the mapping function returns an Optional, avoids nested Optional<Optional<T>>.
- filter() - if the value is present and matches the Predicate, returns the Optional; otherwise returns Optional.empty().

### Default and Static Methods in Interface
- Prior to Java 8, interfaces could only have abstract methods.
  - Define the contract.
  - Only allowed to declare the method signature. Not allowed to provide the implementation.
  - Implementation only allowed in the class which implements the interface.
  - Not easy for interfaces to evolve because if we add a new method in the interface then all the classes which implement the interface need to provide the implementation for that method.
- Java 8 onwards, interfaces can have default and static methods.
- default method - it is used to provide the default implementation for a method in the interface.
  - `default` keyword is used to declare the default method in the interface.
  - Default method can be overridden by the class which implements the interface.
  - It is used to evolve the interface without breaking the existing implementation.
  - If a class implements two interfaces with the same default method, the class must override the method to resolve the conflict (diamond problem).
- static method - it is used to provide a static method in the interface. 
  - `static` keyword is used to declare the static method in the interface.
  - Static method can be called using the interface name directly (e.g., InterfaceName.methodName()), without creating an object.
  - It is used to provide utility methods in the interface.
  - Static method cannot be overridden by the class which implements the interface.

