package com.avinash.masterJava.java8.lamdas.streamsapi;

import java.util.ArrayList;
import java.util.stream.Stream;

public class CollectionsvsStreamExample {
    public static void main(String[] args) {
        ArrayList<String> names = new ArrayList<>();
        names.add("avi");
        names.add("ani");
        names.add("abhi");
        System.out.println("before"+names);
        names.remove(0);
        System.out.println("before"+names);

        names.forEach(System.out::println);
        names.forEach(System.out::println);

        Stream<String> namesStream =  names.stream();
        namesStream.forEach(System.out::println);
        // stream can be traversed only once.
//        namesStream.forEach(System.out::println);//getting exception :: stream has already been operated upon or closed

    }
}
