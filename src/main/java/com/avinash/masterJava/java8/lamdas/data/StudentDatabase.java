package com.avinash.masterJava.java8.lamdas.data;

import java.util.List;

public class StudentDatabase {

    public static List<Student> getStudents(){

        return List.of(
                new Student("adam",2,3.6,"male",List.of("swimming","basketball","volleyball")),
                new Student("avinash",2,3.6,"male",List.of("swimming","gymnastics","soccer")),
                new Student("james",3,4.0,"female",List.of("swimming","gymnastics","aerobics")),
                new Student("akash",3,3.9,"male",List.of("swimming","gymnastics","soccer")),
                new Student("david",4,3.5,"male",List.of("swimming","dancing","football")),
                new Student("emily",4,3.9,"female",List.of("swimming","basketball","baseball","football"))
        );

    }
}
