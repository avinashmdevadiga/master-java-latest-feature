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
- Using method reference we can write simpler and more readable code
- syntax:
  - classname::methodname
- lamda expression refered to method directly.

### Constructor Reference
- syntax:
    - classname::new
### lamdas and local variable restriction
- not allowed to use same local variable name as a lamda parameter or inside the lamda body
- not allowed to reasaign a new value to the local variable inside the lamda body.

### Strems
- stream is a list of element which created out of collections.
- Streams operation can be perform sequentially or parralelly.
- we can create a strem by calling strem() method.
    ```
  Arrays.asList("avinash","Anil,"Abhishek").stream();
  ```
- Streams have few intermediate operation and termainal operation
    ```
   Map<String, List<String>> collect = StudentDatabase.getStudents()
                .stream()
                .filter(predicate1)
                .filter(predicate2)
                .collect(Collectors.toMap(Student::getName, Student::getActivities));
  ```
  - here stream methos creates a sequence of element and proceeding with the 
  - intermediate operation like filters
  - then finally it calls terminal method like collect.
  - if there in not terminal method stream will not execute any intermediate operation
  - stream is a lazy.

### collections vs stream
- in collection we can add or modify the elements
    ```
   ArrayList<String> names = new ArrayList<>();
        names.add("avi");
  ```
- But in Stream we can not add/ or modify the elements in the list. its a fixed data set.
- Elements in the collections can accessed in any order use appropriate method based on the collections.
- Elements in the stream can be accessed on in sequence.
- Collection is eagerly constructed
- Streams are lazyly constructed.
- Collections can be traversed 'n' numbers of time
- Streams can be traversed only once.
- Collections Perform external iteration to iterate through the element.
- Stream Performs internal iteration to iterate through the elements.

### how Streams will work?
- stream process the element sequencially one by one
    ```
    Before filter startedStudent(name=adam, gradeLevel=2, gpa=3.6, gender=male, activities=[swimming, basketball, volleyball])
    After 1St filterStudent(name=adam, gradeLevel=2, gpa=3.6, gender=male, activities=[swimming, basketball, volleyball])
    ```
    - in This example first it takes first student element then is pass through the filter1 and filter1 passed. hence prints logs
    - then it pass through the filter2 and this filter fails.hence log doesnot print.
    - here one element process completed now it will start for another element
    ```
    Before filter startedStudent(name=avinash, gradeLevel=2, gpa=3.6, gender=male, activities=[swimming, gymnastics, soccer])
    After 1St filterStudent(name=avinash, gradeLevel=2, gpa=3.6, gender=male, activities=[swimming, gymnastics, soccer])
    ```
    ```
    Before filter startedStudent(name=james, gradeLevel=3, gpa=4.0, gender=female, activities=[swimming, gymnastics, aerobics])
    After 1St filterStudent(name=james, gradeLevel=3, gpa=4.0, gender=female, activities=[swimming, gymnastics, aerobics])
    After 2nd filterStudent(name=james, gradeLevel=3, gpa=4.0, gender=female, activities=[swimming, gymnastics, aerobics])
    ```
    - in This example first it takes first student element then is pass through the filter1 and filter1 passed. hence prints logs
    - then it pass through the filter2 and this filter pass.hence log print.
    - here onother element process completed now it will start for one more element.
- By this we can conclude stream will pick one element and pass through the all intermediate process.
- once all intermediate process done. it will process another element sequentially.

### filter() in Stream
- it accept the predicate(meaning accept input and return boolean).
- normally filter is used to filter the element in the stream.

### map() in stream
- map() it convert/transform one type to another type.

### flatMap() in stream
- it convert /transform one type to another like map().
- it used in the context that each element in the stream represent Multiple element.

### distict()
- return unique element in the streams

### sorted()
- sort the element in the stream
- sorted(Comparator.comparing(Student::getName)) we can sort by any properties of the object.

### count()
- Returns long with a total number of element in the stream.

### collect() in stream
- it is a terminal method. after processing the stream we can collect the processed stream by using this kind of terminal method

### reduce() in stream
- it is also a terminal method used to reduce the contents of the stream to a single value.
- it takes two parameter as an input.
- First parameter is default or initial value and its a optional.
- Second parameter is Binary operator.
  ```
  List<Integer> integerList = List.of(2,4,7,3);
  integerList.stream()
  // here a is default value during first iteration and it takes result of the function during second iteration onwords
  // a= 1 and b = 2(from the stream) and the result is =2 
  // a= 2(previous result) and b = 4(from the stream) and the result is =8
  // a= 8(previous result) and b = 7(from the stream) and the result is =56
  // a= 56(previous result) and b = 3(from the stream) and the result is =168
  // final result is =168
  .reduce(1,(a,b)->a*b);
    }
  ```
### limit() and skip()
- these two function helps to create a substream
- limit() - it limits 'n'numbers of elements to be processed in the stream
- skip() - it skips 'n' number of element from the stream

### anyMatch() allMatch() and noneMatch()
- all these function takes predicate as a input and returns a boolean
- anyMatch() -> if any one element in the stream matches the predicates returns true.
- allMatch() -> if all elements in the stream matches the predicate returns true.
- noneMatch() -> it is opposite of anyMatch(). if none of the element in the stream matches the predicate returns true.
- 


