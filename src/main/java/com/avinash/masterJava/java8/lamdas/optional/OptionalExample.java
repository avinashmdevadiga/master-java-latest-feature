package com.avinash.masterJava.java8.lamdas.optional;

import com.avinash.masterJava.java8.lamdas.data.Student;
import com.avinash.masterJava.java8.lamdas.data.StudentDatabase;

import java.util.Optional;

public class OptionalExample {

    private static String getName(){
        //Student student = StudentDatabase.studentSupplier();
        Student student = null;

        if (student != null){
           return student.getName();

        }
        return null;
    }

    private static Optional<String> getNameOptional(){
        Optional<Student> studentOptional = Optional.ofNullable(StudentDatabase.studentSupplier());

        if(studentOptional.isPresent()){
            return studentOptional.map(Student::getName);
        }
        return Optional.empty();
    }
    public static void main(String[] args) {
//        String name = getName();
//        if(name != null)
//            System.out.println("length :: "+name.length());
//        else
//            System.out.println("name is null");


        Optional<String> nameOptional = getNameOptional();
        if (nameOptional.isPresent())
            System.out.println("length :: "+nameOptional.get().length());
        else
            System.out.println("name is null");
    }
}
