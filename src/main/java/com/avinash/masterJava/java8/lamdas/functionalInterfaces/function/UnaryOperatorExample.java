package com.avinash.masterJava.java8.lamdas.functionalInterfaces.function;

import java.util.function.UnaryOperator;

public class UnaryOperatorExample {
    /*
    * this function is used when the input and the return type are same.
    * this is a Functional interface
    * */

    // here input parater of String type and returnValue also a String type, hence mentioning only one generic type.
    // it take one input value and return the same type as input
    private static UnaryOperator<String> unaryOperator = name -> name.concat("default");

    public static void main(String[] args) {
        System.out.println(unaryOperator.apply("java8"));
    }
}
