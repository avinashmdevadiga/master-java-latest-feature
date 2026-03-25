package com.avinash.masterJava.java8.lamdas.streamsapi;

import com.avinash.masterJava.java8.lamdas.data.Student;
import com.avinash.masterJava.java8.lamdas.data.StudentDatabase;

import java.util.List;
import java.util.Optional;

public class StreamReduceExample {

    private static int performMultiplication(List<Integer> integerList){
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
        return integerList.stream()
                .reduce((a,b)-> a*b);
    }

    /*
    * reduce the highest gpa in the studentList
    *
    * */
    private static Optional<Student> highetGpaStudent(){
        return StudentDatabase.getStudents().stream()
                .reduce((a,b)->a.getGpa()>b.getGpa()?a:b);
    }

    /*
    * perform the total number of notebook each students has
    * */
    private static Optional<Integer> performTotalNumberOfNotebooks(){
        return StudentDatabase.getStudents().stream()
                .map(Student::getNoteBooks)
//                .reduce((a,b)->a+b);
                .reduce(Integer::sum);
    }

    public static void main(String[] args) {
        List<Integer> integerList = List.of(2,4,7,3);
        System.out.println(performMultiplication(integerList));

        Optional<Integer> multiplication  = performMultiplicationWithoutIdentity(integerList);
        multiplication.ifPresent(System.out::println);


        Optional<Student> highestGpa = highetGpaStudent();
        highestGpa.ifPresent(System.out::println);

        Optional<Integer> totalNoteBooks = performTotalNumberOfNotebooks();
        totalNoteBooks.ifPresent(System.out::println);
    }
}
