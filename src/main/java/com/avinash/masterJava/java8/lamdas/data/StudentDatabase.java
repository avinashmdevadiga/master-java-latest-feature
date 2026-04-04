package com.avinash.masterJava.java8.lamdas.data;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;

public class StudentDatabase {
    public static Student studentSupplier(){
      return new Student("adam",2,3.6,"male",List.of("swimming","basketball","volleyball"),15);
    }

    public static Student sudentSupplier(){
        Bike bike = new Bike();
        bike.setName("Pulsar");
        bike.setModel("220");
        Student student = new Student("avinash", 2, 3.6, "male", List.of("swimming", "gymnastics", "soccer"), 13);
        student.setBike(Optional.of(bike));
        return student;
    }

    public static List<Student> getStudents(){

        return List.of(
                new Student("adam",2,3.6,"male",List.of("swimming","basketball","volleyball"),15),
                new Student("avinash",2,3.6,"male",List.of("swimming","gymnastics","soccer"),13),
                new Student("james",3,4.0,"female",List.of("swimming","gymnastics","aerobics"),9),
                new Student("akash",3,3.9,"male",List.of("swimming","gymnastics","soccer"),7),
                new Student("david",4,3.5,"male",List.of("swimming","dancing","football"),16),
                new Student("emily",4,3.9,"female",List.of("swimming","basketball","baseball","football"),10)
        );

    }

    public static List<Student> getStudentsWithNull(){

        return Arrays.asList(
                new Student("adam",2,3.6,"male",List.of("swimming","basketball","volleyball"),15),
                new Student("avinash",2,3.6,"male",List.of("swimming","gymnastics","soccer"),13),
                null,
                new Student("james",3,4.0,"female",List.of("swimming","gymnastics","aerobics"),9),
                new Student("akash",3,3.9,"male",List.of("swimming","gymnastics","soccer"),7),
                null,
                new Student("david",4,3.5,"male",List.of("swimming","dancing","football"),16),
                new Student("emily",4,3.9,"female",List.of("swimming","basketball","baseball","football"),10)
        );

    }
}
