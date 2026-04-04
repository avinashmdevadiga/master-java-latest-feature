package com.avinash.masterJava.java8.lamdas.streamsapi;

import com.avinash.masterJava.java8.lamdas.data.Student;
import com.avinash.masterJava.java8.lamdas.data.StudentDatabase;

import java.util.List;
import java.util.Optional;
import java.util.logging.Logger;

/**
 * Use Case: Demonstrates the Stream reduce() terminal operation.
 * reduce() repeatedly applies a BinaryOperator to accumulate stream elements into one result.
 * - With identity: always returns a value (safe, no Optional).
 * - Without identity: returns Optional<T> because the stream may be empty.
 * Use cases shown:
 *   1. Integer multiplication with an identity value.
 *   2. Integer multiplication without identity → Optional.
 *   3. Finding the student with the highest GPA via reduce.
 *   4. Summing total notebooks across all students.
 */
public class StreamReduceExample {

    private static final Logger LOGGER = Logger.getLogger(StreamReduceExample.class.getName());

    private static int performMultiplication(List<Integer> integerList){
        LOGGER.info("StreamReduceExample :: performMultiplication (with identity=1) on " + integerList);
        return integerList.stream()
                // here a is default value during first iteration and it takes result of the function during second iteration onwords
                // a= 1 and b = 2(from the stream) and the result is =2
                // a= 2(previous result) and b = 4(from the stream) and the result is =8
                // a= 8(previous result) and b = 7(from the stream) and the result is =56
                // a= 56(previous result) and b = 3(from the stream) and the result is =168
                // final result is =168
                .reduce(1,(a,b)->a*b);
    }

    private static Optional<Integer> performMultiplicationWithoutIdentity(List<Integer> integerList){
        LOGGER.info("StreamReduceExample :: performMultiplicationWithoutIdentity (no identity) on " + integerList);
        return integerList.stream()
                .reduce((a,b)-> a*b);
    }

    private static Optional<Student> highetGpaStudent(){
        LOGGER.info("StreamReduceExample :: highetGpaStudent - reducing to student with max GPA");
        return StudentDatabase.getStudents().stream()
                .reduce((a,b)->a.getGpa()>b.getGpa()?a:b);
    }

    private static Optional<Integer> performTotalNumberOfNotebooks(){
        LOGGER.info("StreamReduceExample :: performTotalNumberOfNotebooks - summing notebooks across all students");
        return StudentDatabase.getStudents().stream()
                .map(Student::getNoteBooks)
                .reduce(Integer::sum);
    }

    public static void main(String[] args) {
        LOGGER.info("StreamReduceExample :: main started");
        List<Integer> integerList = List.of(2,4,7,3);
        System.out.println(performMultiplication(integerList));

        Optional<Integer> multiplication = performMultiplicationWithoutIdentity(integerList);
        multiplication.ifPresent(System.out::println);

        Optional<Student> highestGpa = highetGpaStudent();
        highestGpa.ifPresent(System.out::println);

        Optional<Integer> totalNoteBooks = performTotalNumberOfNotebooks();
        totalNoteBooks.ifPresent(System.out::println);

        LOGGER.info("StreamReduceExample :: main finished");
    }
}
