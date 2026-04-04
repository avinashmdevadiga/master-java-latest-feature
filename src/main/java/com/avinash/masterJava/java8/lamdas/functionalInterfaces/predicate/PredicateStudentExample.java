package com.avinash.masterJava.java8.lamdas.functionalInterfaces.predicate;

import com.avinash.masterJava.java8.lamdas.data.Student;
import com.avinash.masterJava.java8.lamdas.data.StudentDatabase;

import java.util.function.Predicate;
import java.util.logging.Logger;

/**
 * Use Case: Demonstrates Predicate<T> with a real domain model (Student).
 * Defines reusable predicates for grade-level and GPA filters and combines them
 * using and(), or(), and negate() to form compound filtering conditions.
 * The static predicates are also re-used by FunctionStudentExample and BiFunctionExample.
 */
public class PredicateStudentExample {

    private static final Logger LOGGER = Logger.getLogger(PredicateStudentExample.class.getName());

    /*
    * usecase1: gradeleve >3
    * */
     public static Predicate<Student> studentGrade = student -> student.getGradeLevel() > 2;
     public static Predicate<Student> studentGpa   = student -> student.getGpa() >= 3.9;

    private static void printFilterStudentByGrade(){
        LOGGER.info("PredicateStudentExample :: printFilterStudentByGrade - gradeLevel > 2");
        System.out.println("printFilterStudentByGrade :: ");
        StudentDatabase.getStudents().forEach(student -> {
            if(studentGrade.test(student)) System.out.println(student);
        });
    }

    private static void printFilterStudentByGpa(){
        LOGGER.info("PredicateStudentExample :: printFilterStudentByGpa - GPA >= 3.9");
        System.out.println("printFilterStudentByGpa :: ");
        StudentDatabase.getStudents().forEach(student -> {
            if(studentGpa.test(student)) System.out.println(student);
        });
    }

    private static void printFilterStudentByGpaAndGrade(){
        LOGGER.info("PredicateStudentExample :: printFilterStudentByGpaAndGrade - GPA >= 3.9 AND gradeLevel > 2");
        System.out.println("printFilterStudentByGpaAndGrade :: ");
        StudentDatabase.getStudents().forEach(student -> {
            if(studentGpa.and(studentGrade).test(student)) System.out.println(student);
        });
    }

    private static void printFilterStudentByGpaOrGrade(){
        LOGGER.info("PredicateStudentExample :: printFilterStudentByGpaOrGrade - GPA >= 3.9 OR gradeLevel > 2");
        System.out.println("printFilterStudentByGpaOrGrade :: ");
        StudentDatabase.getStudents().forEach(student -> {
            if(studentGpa.or(studentGrade).test(student)) System.out.println(student);
        });
    }

    private static void printFilterStudentByGpaAndGradeAndNegate(){
        LOGGER.info("PredicateStudentExample :: printFilterStudentByGpaAndGradeAndNegate - NOT (GPA >= 3.9 AND gradeLevel > 2)");
        System.out.println("printFilterStudentByGpaAndGradeAndNegate :: ");
        StudentDatabase.getStudents().forEach(student -> {
            if(studentGpa.and(studentGrade).negate().test(student)) System.out.println(student);
        });
    }

    public static void main(String[] args) {
        LOGGER.info("PredicateStudentExample :: main started");
        printFilterStudentByGrade();
        printFilterStudentByGpa();
        printFilterStudentByGpaAndGrade();
        printFilterStudentByGpaOrGrade();
        printFilterStudentByGpaAndGradeAndNegate();
        LOGGER.info("PredicateStudentExample :: main finished");
    }
}
