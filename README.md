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

    ``` syntax () -> {}```.

#### Usage
    - main usage is to implement the functional interface.
    -  functional interfaces in other way we call it as SAM(Single Abstract Method) interface.
    - if the interface has only one abstract method those interface is
        consider as a Functional interface.
        example: Runnable, Comparator interface.
        @FunctionalInterface
        public interface Runnable {
            /**
            * Runs this operation.
            */
            void run();
            }
        }