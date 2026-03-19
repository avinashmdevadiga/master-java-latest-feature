package com.avinash.masterJava.java8.lamdas.functionalInterfaces.predicate;

import com.avinash.masterJava.java8.lamdas.data.Student;
import com.avinash.masterJava.java8.lamdas.data.StudentDatabase;

import java.util.function.Predicate;

public class PredicateStudentExample {
    /*
    * usecase1: gradeleve >3
    * */
     public static Predicate<Student> studentGrade = student -> student.getGradeLevel() > 2;
     public static Predicate<Student> studentGpa = student -> student.getGpa() >= 3.9;

    private static void printFilterStudentByGrade(){
        System.out.println("printFilterStudentByGrade :: ");
        StudentDatabase.getStudents().forEach(student -> {
            if(studentGrade.test(student)){
                System.out.println(student);
            }
        });
    }

    private static void printFilterStudentByGpa(){
        System.out.println("printFilterStudentByGpa :: ");
        StudentDatabase.getStudents().forEach(student -> {
            if(studentGpa.test(student)){
                System.out.println(student);
            }
        });
    }

    private static void printFilterStudentByGpaAndGrade(){
        System.out.println("printFilterStudentByGpaAndGrade :: ");
        StudentDatabase.getStudents().forEach(student -> {
            if(studentGpa.and(studentGrade).test(student)){
                System.out.println(student);
            }
        });
    }

    private static void printFilterStudentByGpaOrGrade(){
        System.out.println("printFilterStudentByGpaOrGrade :: ");
        StudentDatabase.getStudents().forEach(student -> {
            if(studentGpa.or(studentGrade).test(student)){
                System.out.println(student);
            }
        });
    }

    private static void printFilterStudentByGpaAndGradeAndNegate(){
        System.out.println("printFilterStudentByGpaAndGradeAndNegate :: ");
        StudentDatabase.getStudents().forEach(student -> {
            if(studentGpa.and(studentGrade).negate().test(student)){
                System.out.println(student);
            }
        });
    }

    public static void main(String[] args) {
        printFilterStudentByGrade();
        printFilterStudentByGpa();
        printFilterStudentByGpaAndGrade();
        printFilterStudentByGpaOrGrade();
        printFilterStudentByGpaAndGradeAndNegate();

    }
}
