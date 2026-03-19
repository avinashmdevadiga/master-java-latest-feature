package com.avinash.masterJava.java8.lamdas.functionalInterfaces.consumer;

import com.avinash.masterJava.java8.lamdas.data.Student;
import com.avinash.masterJava.java8.lamdas.data.StudentDatabase;

import java.util.function.Consumer;

public class ConsumerExample {
    /*
    * use case 1: from students print all the students name
    * */
    private static Consumer<Student> consumer2 = (student)-> System.out.println("names : "+student.getName());
    private static void printNames(){
        StudentDatabase.getStudents().forEach(consumer2);
    }

    /*
    * usecase2: print name as well as thier activities
    * */

    private static Consumer<Student> consumer3 = (student)-> System.out.println("Activities : "+student.getActivities());
    private static void getNamesAndActivities(){
        // here i am using andthen() to print both the consument .
        // this concept is called consumer chaining.
        // we can use any number od consumer in a single statement using this andThen().
        StudentDatabase.getStudents().forEach(consumer2.andThen(consumer3));
    }

    /*
    * usecase3: print all the student whose grade is 4 and gpa is above and equal 3.9
    * */

    private static void getConditionalStudentName(){
        StudentDatabase.getStudents().forEach((student)->{
            if(student.getGradeLevel() == 4 && student.getGpa() >= 3.9){
                consumer2.andThen(consumer3).accept(student);
            }
        });
    }

    public static void main(String[] args) {
        /*
        * Consumer is a functional interface which accept the input and not return any values.
        * which has void accept(T t) SAM.
        * and also it as default method  default Consumer<T> andThen(Consumer<? super T> after)
        * */
        Consumer<String> consumer = (s)-> System.out.println(s.toUpperCase());

        consumer.accept("java8");

        printNames();
        getNamesAndActivities();
        getConditionalStudentName();
    }
}
