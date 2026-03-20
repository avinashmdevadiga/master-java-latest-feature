package com.avinash.masterJava.java8.lamdas.constructorreference;

import com.avinash.masterJava.java8.lamdas.data.Student;

import java.util.function.Function;
import java.util.function.Supplier;

public class ConstructorReferenceExample {

    //no arg constructor
    private static Supplier<Student> supplier = Student::new;

    private static Function<String,Student> function = Student::new;
    public static void main(String[] args) {
        System.out.println(supplier.get());
        System.out.println(function.apply("AAA"));
    }
}
