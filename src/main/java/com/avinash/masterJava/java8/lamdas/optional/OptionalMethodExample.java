package com.avinash.masterJava.java8.lamdas.optional;

import com.avinash.masterJava.java8.lamdas.data.Bike;
import com.avinash.masterJava.java8.lamdas.data.StudentDatabase;
import io.micrometer.observation.Observation;

import javax.swing.text.html.Option;
import java.util.Optional;
import java.util.PrimitiveIterator;

public class OptionalMethodExample {

    private  static Optional<String> ofNullable(){
//        return Optional.ofNullable("hello"); // returns value optional
        return Optional.ofNullable(null);// returns nullpointer exception
    }

    private static Optional<String> of(){
        return Optional.of("hello");// returns Optional value
//        return Optional.of(null);// returns Optional value
    }

    private static Optional<String> empty(){
        return Optional.empty();
    }

    private static String orElse(){

//        Optional<String> optionalOrElse =  Optional.ofNullable("hello");
        Optional<String> optionalOrElse =  Optional.ofNullable(null);
        return optionalOrElse.orElse("default");

    }

    private static String orElseGet(){
//        Optional<String> optionalOrElseGet =  Optional.ofNullable("hello");
        Optional<String> optionalOrElseGet =  Optional.ofNullable(null);

        return optionalOrElseGet.orElseGet(()->"default");
    }

    private static String orElseThrow(){
//        Optional<String> optionalOrElseThrow =  Optional.ofNullable("hello");
        Optional<String> optionalOrElseThrow =  Optional.ofNullable(null);
        return optionalOrElseThrow.orElseThrow(()->new RuntimeException("exception"));
    }

    //we can appli map() and flatmap() on optional as well
    private static String mapAndFlatMap(){
        return StudentDatabase.sudentSupplier().getBike().map(Bike::getName).get();
    }

    private static String mapAndFlatMapExample(){
        return StudentDatabase.sudentSupplier().getBike().flatMap(bike -> Optional.ofNullable(bike.getName())).orElse("default");
    }


    public static void main(String[] args) {
        System.out.println("Optional ofNullable example: "+ ofNullable());
        System.out.println("Optional of example: "+ of());
        System.out.println("Optional empty example: "+ empty());

        System.out.println("Optional orelse example: "+ orElse());
        System.out.println("Optional orelseget example: "+ orElseGet());
        //System.out.println("Optional orelseThrow example: "+ orElseThrow());
        System.out.println("Optional map and flatmap example: "+ mapAndFlatMap());
        System.out.println("Optional map and flatmap example: "+ mapAndFlatMapExample());
    }
}
