package com.avinash.masterJava.java8.lamdas.functionalInterfaces.consumer;

import com.avinash.masterJava.java8.lamdas.data.StudentDatabase;

import java.util.List;
import java.util.function.BiConsumer;

public class BiConsumerExample {

    /*
    * usecae1: print both the studentname and aactivities using biconsumer
    *
    * */

    private static void getNamesAndActivities(){
        BiConsumer<String, List<String>> studentConsumer = (name,activities)->
                System.out.println(name + " : "+activities);
        StudentDatabase.getStudents().forEach(student -> studentConsumer.accept(student.getName(),student.getActivities()));
    }


    public static void main(String[] args) {
        /*
        * BiConsumer is a functional interface which accept two inputs and not return any value.
        *
        *
        * */

        BiConsumer<String,String> biConsumer = (a,b)-> System.out.println("a : "+a+" ,b : "+b);
        biConsumer.accept("java7","java8");


        /*
        * multiply
        * */
        BiConsumer<Integer,Integer> multify = (a,b)-> System.out.println(a +"*"+b +" = "+a*b);

        multify.accept(5,7);

        BiConsumer<Integer,Integer> devision = (a,b)-> System.out.println(a +"/"+b +" = "+a/b);

        devision.accept(20,5);

        //consumer chaining example
        multify.andThen(devision).accept(20,5);



        getNamesAndActivities();

    }
}
