# master-java-latest-feature

## Java8

### Lamda
 - lamda is a equivalent to function without name
 - lamda is also reffered as ananymous function. which has all the method properties such as:
    - method parameter
    - method body
    - return type
 - lamdas are not tie to the any class like regular method
 - lamda can assigned to any variable and pass as a variable.

``` 
syntax () -> {}

```

#### Usage
- main usage is to implement the functional interface.
-  functional interfaces in other way we call it as SAM(Single Abstract Method) interface.
- if the interface has only one abstract method those interface is
  consider as a Functional interface.
   - example: Runnable, Comparator interface.
        ```@FunctionalInterface
        public interface Runnable {
            /**
            * Runs this operation.
            */
            void run();
            }
        }
     ```
### Functional interface
- An interface has single abstract method is called as Functional Interface
- it exist since java 1.0v itself
- interface annotate as @FunctionalInterface
- this annotation is introduced in jdk 1.8 and its a optional.
- new Functional Interface in java.
  - Functions - it take input value and return output
  - Predicate - it take input value and return boolean value
  - Consumer - it take input value but not returning any value
  - Supplier - it doesnot take any value and return value.

### Method References
- 