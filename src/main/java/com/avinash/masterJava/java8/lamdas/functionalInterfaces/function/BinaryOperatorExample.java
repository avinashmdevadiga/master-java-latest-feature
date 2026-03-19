package com.avinash.masterJava.java8.lamdas.functionalInterfaces.function;

import java.util.Comparator;
import java.util.function.BinaryOperator;
import java.util.function.UnaryOperator;

public class BinaryOperatorExample {
    /*
    * BinaryOperator is a Functional Interface
    * which takes two input parameter and return value
    * when the input parameter and output value are of same type in that case we need to use binary operator
    *it has static method such as minBy() and maxBy()
    * */
    // here input and output return type same hence only Integer mentioned in the generic
    // it takes two number of integer type and return the result of Integer type
    private static BinaryOperator<Integer> binaryOperator =  (a,b) -> a*b;

    private static Comparator<Integer> comparator = (a,b)->a.compareTo(b);

    private static BinaryOperator<Integer> maxBy = BinaryOperator.maxBy(comparator);
    private static BinaryOperator<Integer> minBy = BinaryOperator.minBy(comparator);

    public static void main(String[] args) {
        System.out.println("multification result : "+binaryOperator.apply(3,5));
        System.out.println("maxBy result : "+maxBy.apply(3,5));
        System.out.println("minBy result : "+minBy.apply(3,5));
    }
}
