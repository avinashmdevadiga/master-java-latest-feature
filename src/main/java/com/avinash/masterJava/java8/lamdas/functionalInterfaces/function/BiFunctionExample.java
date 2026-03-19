package com.avinash.masterJava.java8.lamdas.functionalInterfaces.function;

import com.avinash.masterJava.java8.lamdas.data.Student;
import com.avinash.masterJava.java8.lamdas.data.StudentDatabase;
import com.avinash.masterJava.java8.lamdas.functionalInterfaces.predicate.PredicateStudentExample;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.function.BiFunction;
import java.util.function.Predicate;

public class BiFunctionExample {
    /*
    * BiFunction is functional interface it takes two input parameter and it returns output
    * it has apply() as SAM
    * it has default andThen()
    * */

    private static BiFunction<List<Student>, Predicate<Student>, Map<String,Double>> biFunction = (studentList,predicate)->{
        Map<String,Double> studentMap = new HashMap<>();
        studentList.forEach(student -> {
            if(predicate.test(student)){
                studentMap.put(student.getName(),student.getGpa());
            }
        });
        return  studentMap;
    };

    public static void main(String[] args) {
        System.out.println("result is : "+biFunction.apply(StudentDatabase.getStudents(), PredicateStudentExample.studentGpa));
    }
}
