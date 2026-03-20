package com.avinash.masterJava.java8.lamdas.methodreferences;

import java.util.function.Function;

public class FunctionMethodReferenceExample {
    private static Function<String,String> lamdaUpperCase = s-> s.toUpperCase();
    private static Function<String,String> lamdaUppercaseMthodReference =  String::toUpperCase;

    public static void main(String[] args) {
        System.out.println(lamdaUpperCase.apply("java8"));
        System.out.println(lamdaUppercaseMthodReference.apply("java8"));
    }
}
