package com.avinash.masterJava.java8.lamdas.functionalInterfaces.supplier;

import com.avinash.masterJava.java8.lamdas.data.Student;
import com.avinash.masterJava.java8.lamdas.data.StudentDatabase;

import java.util.List;
import java.util.function.Supplier;

public class SupplierExample {
    /*
    * It is a Functional interface
    * which doesnot take any inpur but it will return value.
    *it has a get() as SAM
    * */

    private static Supplier<Student> supplier = ()-> new Student("anil",3,3.9,"male", List.of("swimming","cricket","basketball","football"),11);
    private static Supplier<List<Student>> studentListSupplier = () -> StudentDatabase.getStudents();

    public static void main(String[] args) {
        System.out.println("Student supplier : "+supplier.get());
        System.out.println("StudentList supplier : "+studentListSupplier.get());
    }

}
