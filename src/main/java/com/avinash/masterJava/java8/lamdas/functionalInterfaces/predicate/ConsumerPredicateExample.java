package com.avinash.masterJava.java8.lamdas.functionalInterfaces.predicate;

import com.avinash.masterJava.java8.lamdas.data.Student;
import com.avinash.masterJava.java8.lamdas.data.StudentDatabase;

import java.util.function.Consumer;
import java.util.function.Predicate;

public class ConsumerPredicateExample {
    private static Consumer<Student> consumer = student-> System.out.println("name : "+student.getName()+" --> activities: "+student.getActivities());
    private static Predicate<Student> predicate = student -> student.getGradeLevel() > 2;
    private static Predicate<Student> predicate1 = student -> student.getGpa() >= 3.9;

    private void printFilteredStudent(){
        StudentDatabase.getStudents()
                .forEach(student -> {
                    if(predicate.and(predicate1).test(student)){
                        consumer.accept(student);
                    }
                });
    }
    public static void main(String[] args) {
        //usecase print print all student name and activities whose grad is above 2 and gpa is greater than or e
        //equals to 3.9.
        new ConsumerPredicateExample().printFilteredStudent();

    }
}
