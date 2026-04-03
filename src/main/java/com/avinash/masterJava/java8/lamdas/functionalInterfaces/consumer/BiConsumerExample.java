package com.avinash.masterJava.java8.lamdas.functionalInterfaces.consumer;

import com.avinash.masterJava.java8.lamdas.data.StudentDatabase;

import java.util.List;
import java.util.function.BiConsumer;
import java.util.logging.Logger;

/**
 * Use Case: Demonstrates the BiConsumer<T,U> functional interface.
 * BiConsumer accepts two input parameters and returns nothing (void).
 * Use cases shown:
 *   1. Print string pairs and arithmetic results (multiply, divide).
 *   2. Consumer chaining with andThen() across two operations.
 *   3. Print student name alongside their activities list.
 */
public class BiConsumerExample {

    private static final Logger LOGGER = Logger.getLogger(BiConsumerExample.class.getName());

    /*
    * usecae1: print both the studentname and aactivities using biconsumer
    *
    * */

    private static void getNamesAndActivities(){
        LOGGER.info("BiConsumerExample :: getNamesAndActivities - printing name + activities via BiConsumer");
        BiConsumer<String, List<String>> studentConsumer = (name,activities)->
                System.out.println(name + " : "+activities);
        StudentDatabase.getStudents().forEach(student -> studentConsumer.accept(student.getName(),student.getActivities()));
    }


    public static void main(String[] args) {
        LOGGER.info("BiConsumerExample :: main started");

        /*
        * BiConsumer is a functional interface which accepts two inputs and does not return any value.
        * SAM: void accept(T t, U u)
        * Default method: andThen(BiConsumer<? super T,? super U> after)
        * */
        LOGGER.info("Applying BiConsumer<String,String> to ('java7','java8')");
        BiConsumer<String,String> biConsumer = (a,b)-> System.out.println("a : "+a+" ,b : "+b);
        biConsumer.accept("java7","java8");


        LOGGER.info("Applying BiConsumer<Integer,Integer> for multiplication: 5 * 7");
        BiConsumer<Integer,Integer> multify = (a,b)-> System.out.println(a +"*"+b +" = "+a*b);
        multify.accept(5,7);

        LOGGER.info("Applying BiConsumer<Integer,Integer> for division: 20 / 5");
        BiConsumer<Integer,Integer> devision = (a,b)-> System.out.println(a +"/"+b +" = "+a/b);
        devision.accept(20,5);

        LOGGER.info("BiConsumer chaining: multiply then divide for (20, 5)");
        multify.andThen(devision).accept(20,5);



        getNamesAndActivities();
        LOGGER.info("BiConsumerExample :: main finished");
    }
}
