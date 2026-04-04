# Master Java Latest Features

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

### findFirst() and findAny()
- These are terminal operations used to retrieve an element from the stream.
- Both return an `Optional<T>` to handle the case where the stream may be empty.
- **`findFirst()`**: Returns the first element in the stream, respecting encounter order.
- **`findAny()`**: Returns any element encountered in the stream. In sequential streams this behaves like `findFirst()`, but in parallel streams it may return any element for better performance.

```java
List<String> names = Arrays.asList("Adam", "Jenny", "Emily");

Optional<String> first = names.stream().findFirst();  // Optional["Adam"]
Optional<String> any   = names.stream().findAny();    // Optional["Adam"] (sequential)

// Parallel stream — findAny() may return any element
Optional<String> anyParallel = names.parallelStream().findAny();
```

---

## Stream API: Stateful vs Stateless Operations

Not all stream operations behave the same way internally. Understanding this distinction helps you reason about performance and correctness, especially in parallel pipelines.

- **Streams do maintain internal state** — the stream pipeline itself tracks processing context.
- **Not all stream operations maintain state** — operations fall into two categories:

| Category | Operations | Notes |
|---|---|---|
| **Stateless** | `filter()`, `map()`, `flatMap()`, `peek()` | Each element is processed independently |
| **Stateful** | `distinct()`, `sorted()`, `limit()`, `skip()` | Must inspect or buffer multiple elements |

- Stateless operations are safe and efficient in parallel streams.
- Stateful operations may require coordination between threads in parallel streams, which can impact performance.

---

## Stream API Factory Methods

Factory methods are used to create streams from various sources.

- **`Stream.of()`**: Creates a stream from a fixed set of values.
- **`Stream.iterate()`**: Creates an infinite sequential stream by iterating a function starting from a seed value.
- **`Stream.generate()`**: Creates an infinite sequential stream using a `Supplier`.

```java
// Finite stream from values
Stream<Integer> integerStream = Stream.of(1, 2, 3);

// Infinite stream: 1, 2, 4, 8, 16, ...
Stream<Integer> powers = Stream.iterate(1, x -> x * 2);

// Infinite stream of random doubles
Stream<Double> randoms = Stream.generate(Math::random);

// Always use limit() with infinite streams to avoid running forever
Stream.iterate(1, x -> x * 2)
      .limit(10)
      .forEach(System.out::println);
```

---

## Numeric Streams (Primitive Streams)

Java 8 provides specialised stream types for primitive values to avoid boxing/unboxing overhead:

- **`IntStream`** — for `int` values
- **`LongStream`** — for `long` values
- **`DoubleStream`** — for `double` values

### IntStream
- `IntStream.range(1, 50)` — returns an `IntStream` of 49 elements from 1 (inclusive) to 50 (exclusive).
- `IntStream.rangeClosed(1, 50)` — returns an `IntStream` of 50 elements from 1 to 50 (both inclusive).

### LongStream
- `LongStream.range(1, 50)` — returns a `LongStream` of 49 elements from 1 to 49.
- `LongStream.rangeClosed(1, 50)` — returns a `LongStream` of 50 elements from 1 to 50.

### Aggregate Functions on Numeric Streams
Numeric streams expose aggregate terminal operations that are not available on `Stream<T>`:
- **`sum()`** — returns the sum of all elements.
- **`min()`** — returns an `OptionalInt/OptionalLong/OptionalDouble` with the minimum value.
- **`max()`** — returns an `OptionalInt/OptionalLong/OptionalDouble` with the maximum value.
- **`average()`** — returns an `OptionalDouble` with the arithmetic mean.
- **`summaryStatistics()`** — returns an object containing count, sum, min, max, and average in a single pass.

```java
int[] numbers = {1, 2, 3, 4, 5};
IntStream.of(numbers).sum();      // 15
IntStream.of(numbers).average();  // OptionalDouble[3.0]
IntStream.of(numbers).max();      // OptionalInt[5]

IntSummaryStatistics stats = IntStream.rangeClosed(1, 10).summaryStatistics();
// IntSummaryStatistics{count=10, sum=55, min=1, average=5.5, max=10}
```

### Conversion Methods

| Method | Description |
|---|---|
| `mapToObj()` | Converts an `IntStream` to a `Stream<T>` (object stream) |
| `mapToLong()` | Converts an `IntStream` to a `LongStream` |
| `mapToDouble()` | Converts an `IntStream` to a `DoubleStream` |
| `boxed()` | Converts `IntStream` → `Stream<Integer>` (convenience shortcut for `mapToObj(Integer::valueOf)`) |

```java
IntStream.range(1, 6)
         .mapToObj(i -> "Item " + i)
         .forEach(System.out::println);
// Item 1, Item 2, Item 3, Item 4, Item 5

// Convert to Stream<Integer> for use with collectors
List<Integer> list = IntStream.range(1, 6)
                              .boxed()
                              .collect(Collectors.toList());
```

---

## Collectors

`Collectors` is a utility class that provides a rich set of `Collector` implementations for use with `collect()`.

### joining()
Concatenates stream elements into a single `String`. It has three overloaded versions:

```java
// No delimiter
String result1 = Stream.of("a", "b", "c").collect(Collectors.joining());
// "abc"

// With delimiter
String result2 = Stream.of("a", "b", "c").collect(Collectors.joining(", "));
// "a, b, c"

// With delimiter, prefix, and suffix
String result3 = Stream.of("a", "b", "c").collect(Collectors.joining(", ", "(", ")"));
// "(a, b, c)"
```

### counting()
Returns the total number of elements as a `Long`. Equivalent to `stream.count()` but useful as a downstream collector.

```java
long count = Stream.of("a", "b", "c").collect(Collectors.counting());
// 3
```

### mapping()
Applies a transformation function to each element before collecting into a container. Useful as a downstream collector inside `groupingBy()`.

```java
Map<String, List<String>> namesByGender = students.stream()
    .collect(Collectors.groupingBy(
        Student::getGender,
        Collectors.mapping(Student::getName, Collectors.toList())
    ));
```

### maxBy() and minBy()
- Both take a `Comparator` as input and return `Optional<T>`.
- **`maxBy()`**: Returns the maximum element based on the comparator.
- **`minBy()`**: Returns the minimum element based on the comparator.

```java
Optional<Student> topStudent = students.stream()
    .collect(Collectors.maxBy(Comparator.comparing(Student::getGpa)));

Optional<Student> lowestGpa = students.stream()
    .collect(Collectors.minBy(Comparator.comparing(Student::getGpa)));
```

### summingInt() / summingLong() / summingDouble()
Returns the sum of values extracted by the provided mapper function.

```java
int totalActivities = students.stream()
    .collect(Collectors.summingInt(student -> student.getActivities().size()));
```

### averagingInt() / averagingLong() / averagingDouble()
Returns the arithmetic mean of values extracted by the mapper. Always returns a `double`.

```java
double avgGpa = students.stream()
    .collect(Collectors.averagingDouble(Student::getGpa));
```

### groupingBy()
Groups stream elements by a classifier function. Equivalent to SQL `GROUP BY`. Returns `Map<K, List<V>>` by default.

Three overloaded versions:

```java
// 1. groupingBy(classifier) — groups into Map<K, List<V>>
Map<String, List<Student>> byGender = students.stream()
    .collect(Collectors.groupingBy(Student::getGender));

// 2. groupingBy(classifier, downstream) — with downstream collector
Map<String, Long> countByGender = students.stream()
    .collect(Collectors.groupingBy(Student::getGender, Collectors.counting()));

// 3. groupingBy(classifier, mapFactory, downstream) — control the Map implementation
Map<String, List<Student>> linkedMap = students.stream()
    .collect(Collectors.groupingBy(Student::getGender, LinkedHashMap::new, Collectors.toList()));
```

### partitioningBy()
Similar to `groupingBy()`, but accepts a `Predicate` as input. The key of the resulting map is always a `boolean` (`true` / `false`).

Two overloaded versions:

```java
// 1. partitioningBy(predicate) — partitions into Map<Boolean, List<T>>
Map<Boolean, List<Student>> partitioned = students.stream()
    .collect(Collectors.partitioningBy(student -> student.getGpa() >= 3.5));
// true  → students with GPA >= 3.5
// false → students with GPA <  3.5

// 2. partitioningBy(predicate, downstream) — with downstream collector
Map<Boolean, Long> countPartitioned = students.stream()
    .collect(Collectors.partitioningBy(
        student -> student.getGpa() >= 3.5,
        Collectors.counting()
    ));
```

### Key Difference: groupingBy() vs partitioningBy()

| Feature | `groupingBy()` | `partitioningBy()` |
|---|---|---|
| Input | Classifier function | Predicate |
| Key type | Any type `K` | Always `Boolean` |
| Number of groups | N (any number) | Always 2 (true / false) |
| Use case | Categorical grouping | Binary condition splitting |

### Paralell Stream
- split the source of data into mutiple parts
- process parelelly and combine the result.
- parallel stream uses the Fork/join framework that got introduced in java 7.
- how may threads are created?  no of threads created  = no of processor available in the machine.
- some time paralell stream takes more time than sequential. we need to understand when to use parallel and when not.

### Optional
- Optional is introduced in java 8 to represents the  a non null value.
- Avoids null pointer exception and Unnnecessary null check.
- 