package com.avinash.masterJava.java8.lamdas.functionalInterfaces.predicate;

import com.avinash.masterJava.java8.lamdas.data.StudentDatabase;

import java.util.List;
import java.util.function.BiConsumer;
import java.util.function.BiPredicate;

public class BiPredicateExample {
    /*
    * it is a functional interface which accept two input and return boolean value
    * it has same default method as Predicate like and, or,negate
    * */

    private static BiPredicate<Integer,Double> biPredicate = (gradeLevel,gpa)->gradeLevel >2 && gpa >= 3.9;
    private static BiConsumer<String, List<String>> biConsumer = (name,activities)-> System.out.println("name: "+name+" --> Activities: "+activities);

    private void printFilteredStudent(){
        StudentDatabase.getStudents()
                .forEach(student -> {
                    if(biPredicate.test(student.getGradeLevel(),student.getGpa())){
                        biConsumer.accept(student.getName(),student.getActivities());
                    }
                });
    }
    public static void main(String[] args) {
        new BiPredicateExample().printFilteredStudent();
    }
}
