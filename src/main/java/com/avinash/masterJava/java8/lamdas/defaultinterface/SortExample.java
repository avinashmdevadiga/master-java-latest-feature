package com.avinash.masterJava.java8.lamdas.defaultinterface;

import com.avinash.masterJava.java8.lamdas.data.Student;
import com.avinash.masterJava.java8.lamdas.data.StudentDatabase;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;

public class SortExample {

    private static void sortByName(List<Student> studentList){
        studentList.sort(Comparator.comparing(Student::getName));
        studentList.forEach(System.out::println);
    }

    private static void sortByGPA(List<Student> studentList){
        studentList.sort(Comparator.comparingDouble(Student::getGpa));
        studentList.forEach(System.out::println);
    }

    private static void comparatorChaining(List<Student> studentList){
        studentList.sort(Comparator.comparingInt(Student::getGradeLevel).thenComparing(Student::getName));
        studentList.forEach(System.out::println);
    }

    private static void SortWithNullValues(List<Student> studentList){
        studentList.sort(Comparator.nullsFirst(Comparator.comparing(Student::getName)));
        studentList.forEach(System.out::println);
    }

    private  static  void SortWithNullValues2(List<Student> studentList){
        studentList.sort(Comparator.nullsLast(Comparator.comparing(Student::getName)));
        studentList.forEach(System.out::println);
    }

    public static void main(String[] args) {
        List<Student> studentList = new ArrayList<>(StudentDatabase.getStudents());
        List<Student> studentListwithNull = new ArrayList<>(StudentDatabase.getStudentsWithNull());
//        sortByName(studentList);
//        System.out.printf(" comparingby double :: \n");
//        sortByGPA(studentList);
//        System.out.printf("comparator chaining :: \n");
//        comparatorChaining(studentList);
        System.out.printf("sort with null values :: \n");
        SortWithNullValues(studentListwithNull);
        System.out.printf("sort with null values 2 :: \n");
        SortWithNullValues2(studentListwithNull);

    }
}
